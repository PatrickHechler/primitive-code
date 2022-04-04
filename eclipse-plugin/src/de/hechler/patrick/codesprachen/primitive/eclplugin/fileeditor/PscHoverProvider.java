package de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getDocVal;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTextInterval;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTokenInfo;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.RuleNode;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungDirektContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungGleichheitContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungPunktContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungRelativeTestsContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungSchubContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungStrichContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.NummerNoConstantContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParamContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.SrContext;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.Param;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.DocumentValue;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.TokenInfo;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;

public class PscHoverProvider implements ITextHover {

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		int off = hoverRegion.getOffset();
		IDocument document = textViewer.getDocument();
		DocumentValue docval = getDocVal(document);
		TokenInfo inf;
		try {
			inf = getTokenInfo(docval, off);
		} catch (NoSuchElementException nsee) {
			return "";
		}
		if (inf.cpac != null) {
			if (inf.tn.getSymbol().getType() != ConstantPoolGrammarLexer.NAME) {
				return "";
			}
			String text = inf.tn.getText();
			Long val_ = inf.ac.constants.get(text);
			if (val_ == null) {
				return text + " : <unknown constant>";
			}
			long val = val_;
			if (val >= 0) {
				return text + " : " + val + " [HEX-" + Long.toHexString(val) + "]";
			} else {
				return text + " : " + val + " [NHEX" + Long.toString(val, 16) + " = UHEX-" + Long.toUnsignedString(val, 16) + "]";
			}
		}
		switch (inf.tn.getSymbol().getType()) {
		case PrimitiveFileGrammarLexer.CONSTANT:
		case PrimitiveFileGrammarLexer.LABEL_DECLARATION:
		case PrimitiveFileGrammarLexer.NAME: {
			String name = inf.tn.getText().replaceFirst("[#@]?([a-zA-Z_]+)", "$1");
			if (inf.ac.constants.containsKey(name)) {
				long val = inf.ac.constants.get(name);
				return constantToString(name, val);
			} else {
				Long val = inf.ac.labels.get(name);
				if (val == null) {
					return name + " : unknown";
				}
				return "@" + name + " : " + (val == null ? "unknown" : "p-" + Long.toHexString(val));
			}
		}
		case PrimitiveFileGrammarLexer.BIN_NUM:
		case PrimitiveFileGrammarLexer.DEC_FP_NUM:
		case PrimitiveFileGrammarLexer.DEC_NUM:
		case PrimitiveFileGrammarLexer.DEC_NUM0:
		case PrimitiveFileGrammarLexer.HEX_NUM:
		case PrimitiveFileGrammarLexer.OCT_NUM:
		case PrimitiveFileGrammarLexer.NEG_DEC_NUM0:
		case PrimitiveFileGrammarLexer.NEG_BIN_NUM:
		case PrimitiveFileGrammarLexer.NEG_HEX_NUM:
		case PrimitiveFileGrammarLexer.NEG_OCT_NUM: {
			NummerNoConstantContext nncc = (NummerNoConstantContext) inf.tn.getParent();
			return constantToString(null, nncc.num);
		}
		case PrimitiveFileGrammarLexer.ERROR:
		case PrimitiveFileGrammarLexer.ERROR_HEX:
		case PrimitiveFileGrammarLexer.ERROR_MESSAGE_START:
		case PrimitiveFileGrammarLexer.ERROR_MESSAGE_END:
			return "error message: " + (String) inf.ac.zusatz;
		case PrimitiveFileGrammarLexer.PLUS:
			if (!(inf.tn.getParent() instanceof ConstBerechnungStrichContext)) {
				if (docval.currentDebugSession == null) {
					return "";
				}
				ParamContext pc = (ParamContext) inf.tn.getParent();
				String value;
				try {
					switch (pc.p.art) {
					case Param.ART_ANUM_BNUM: {
						PVMSnapshot sn = docval.currentDebugSession.getSnapshot();
						byte[] bytes = new byte[24];
						docval.currentDebugSession.getMem(sn.ip, bytes, 0, 16);
						docval.currentDebugSession.getMem(Convert.convertByteArrToLong(bytes, 8) + Convert.convertByteArrToLong(bytes, 16), bytes, 0, 8);
						long val = Convert.convertByteArrToLong(bytes);
						if (val >= 0) {
							value = val + " [HEX-" + Long.toHexString(val) + "]";
						} else {
							value = val + " [NHEX" + Long.toString(val, 16) + " = UHEX-" + Long.toUnsignedString(val, 16) + "]";
						}
						break;
					}
					case Param.ART_ASR_BNUM:
					case Param.ART_ANUM_BSR: {
						PVMSnapshot sn = docval.currentDebugSession.getSnapshot();
						byte[] bytes = new byte[16];
						docval.currentDebugSession.getMem(sn.ip, bytes, 0, 16);
						docval.currentDebugSession.getMem(Convert.convertByteArrToLong(bytes, 8) + sn.getRegister(bytes[7]), bytes, 0, 8);
						long val = Convert.convertByteArrToLong(bytes);
						if (val >= 0) {
							value = val + " [HEX-" + Long.toHexString(val) + "]";
						} else {
							value = val + " [NHEX" + Long.toString(val, 16) + " = UHEX-" + Long.toUnsignedString(val, 16) + "]";
						}
						break;
					}
					case Param.ART_ASR_BSR: {
						PVMSnapshot sn = docval.currentDebugSession.getSnapshot();
						byte[] bytes = new byte[8];
						docval.currentDebugSession.getMem(sn.ip, bytes, 0, 8);
						docval.currentDebugSession.getMem(sn.getRegister(bytes[7]) + sn.getRegister(bytes[6]), bytes, 0, 8);
						long val = Convert.convertByteArrToLong(bytes);
						if (val >= 0) {
							value = val + " [HEX-" + Long.toHexString(val) + "]";
						} else {
							value = val + " [NHEX" + Long.toString(val, 16) + " = UHEX-" + Long.toUnsignedString(val, 16) + "]";
						}
						break;
					}
					case Param.ART_ANUM_BREG:// no PLUS Token
					case Param.ART_ASR_BREG:
					case Param.ART_ANUM:
					case Param.ART_ASR:
					default:
						throw new InternalError("why?! art=" + Param.artToString(pc.p.art));
					}
					getTextInterval((RuleNode) inf.tn.getParent());
					return inf.tn.getParent().getText() + " : " + value;
				} catch (IOException e) {
					return "IOException while comunicating with the debugger: " + e.getMessage();
				}
			}
		case PrimitiveFileGrammarLexer.MINUS:
		case PrimitiveFileGrammarLexer.KOMMA_MINUS:
		case PrimitiveFileGrammarLexer.KOMMA_PLUS: {
			ConstBerechnungStrichContext cbsc = (ConstBerechnungStrichContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.GLEICH_GLEICH:
		case PrimitiveFileGrammarLexer.UNGLEICH: {
			ConstBerechnungGleichheitContext cbsc = (ConstBerechnungGleichheitContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.GROESSER:
		case PrimitiveFileGrammarLexer.GROESSER_GLEICH:
		case PrimitiveFileGrammarLexer.KLEINER_GLEICH:
		case PrimitiveFileGrammarLexer.KLEINER: {
			ConstBerechnungRelativeTestsContext cbsc = (ConstBerechnungRelativeTestsContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.MAL:
		case PrimitiveFileGrammarLexer.GETEILT:
		case PrimitiveFileGrammarLexer.KOMMA_MAL:
		case PrimitiveFileGrammarLexer.KOMMA_GETEILT:
		case PrimitiveFileGrammarLexer.MODULO: {
			ConstBerechnungPunktContext cbsc = (ConstBerechnungPunktContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.FRAGEZEICHEN:
		case PrimitiveFileGrammarLexer.DOPPELPUNKT: {
			ConstBerechnungDirektContext cbsc = (ConstBerechnungDirektContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.LINKS_SCHUB:
		case PrimitiveFileGrammarLexer.ARITMETISCHER_RECHTS_SCHUB:
		case PrimitiveFileGrammarLexer.LOGISCHER_RECHTS_SCHUB: {
			ConstBerechnungSchubContext cbsc = (ConstBerechnungSchubContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.EXIST_CONSTANT:
		case PrimitiveFileGrammarLexer.RND_KL_AUF:
		case PrimitiveFileGrammarLexer.RND_KL_ZU: {
			ConstBerechnungDirektContext cbsc = (ConstBerechnungDirektContext) inf.tn.getParent();
			Interval i = getTextInterval(cbsc);
			try {
				return constantToString(null, cbsc.num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(null, cbsc.num) + " = " + cbsc.getText();
			}
		}
		case PrimitiveFileGrammarLexer.XNN:
		case PrimitiveFileGrammarLexer.INTCNT:
		case PrimitiveFileGrammarLexer.INTP:
		case PrimitiveFileGrammarLexer.STATUS:
		case PrimitiveFileGrammarLexer.SP:
		case PrimitiveFileGrammarLexer.IP:
			if (docval.currentDebugSession == null) {
				return "";
			}
			try {
				PVMSnapshot snapshot = docval.currentDebugSession.getSnapshot();
				SrContext sr = (SrContext) inf.tn.getParent();
				return Param.toSRString(sr.srnum) + " : " + snapshot.getRegister(sr.srnum);
			} catch (IOException e) {
				return "";
			}
		case PrimitiveFileGrammarLexer.CONSTANT_POOL:
			assert false;
		default:
			return "";
		}
	}

	private String constantToString(String name, long val) {
		StringBuilder build = new StringBuilder();
		if (name != null) {
			build.append('#').append(name).append(" : ");
		}
		build.append(val);
		if (val >= 0) {
			build.append(" [HEX-").append(Long.toHexString(val));
		} else {
			build.append(" [NHEX" + Long.toString(val, 16)).append(" = UHEX-").append(Long.toUnsignedString(val, 16));
		}
		build.append(" FP: ").append(Double.longBitsToDouble(val)).append(']');
		return build.toString();
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 0);
	}
}