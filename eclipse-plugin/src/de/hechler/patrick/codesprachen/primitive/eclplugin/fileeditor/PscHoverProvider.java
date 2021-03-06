package de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getDocVal;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTextInterval;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTokenInfo;

import java.io.IOException;
import java.util.Map;
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
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungStrichContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.NummerNoConstantContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParamContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.SrContext;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.Param;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveConstant;
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
			return null;
		}
		if (inf.cpac != null) {
			if (inf.tn.getSymbol().getType() != ConstantPoolGrammarLexer.NAME) {
				return null;
			}
			String text = inf.tn.getText();
			PrimitiveConstant primConst = inf.ac.constants.get(text);
			if (primConst == null) {
				return text + " : <unknown constant>";
			}
			return constantToString(inf.ac.constants, text, primConst.value);
		}
		switch (inf.tn.getSymbol().getType()) {
		case PrimitiveFileGrammarLexer.EXPORT_CONSTANT:
		case PrimitiveFileGrammarLexer.CONSTANT:
		case PrimitiveFileGrammarLexer.LABEL_DECLARATION:
		case PrimitiveFileGrammarLexer.NAME: {
			String text = inf.tn.getText();
			String start = text.replaceFirst("((#|@|\\#EXP\\~)?)([a-zA-Z_]+)", "$1");
			String name = text.replaceFirst("(#|@|\\#EXP\\~)?([a-zA-Z_]+)", "$2");
			Boolean isConst;
			switch (start) {
			case "":
				isConst = null;
				break;
			case "@":
				isConst = false;
				break;
			case "#":
			case "#EXP~":
				isConst = true;
				break;
			default:
				throw new InternalError("unknown start: '" + start + "'");
			}
			boolean canBeConst = inf.ac.constants.containsKey(name);
			if (isConst == null && canBeConst || isConst != null && isConst) {
				long val = inf.ac.constants.get(name).value;
				return constantToString(inf.ac.constants, name, val);
			}
			if (isConst == null && !canBeConst || isConst != null && !isConst) {
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
			return constantToString(inf.ac.constants, null, nncc.num);
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
		case PrimitiveFileGrammarLexer.KOMMA_PLUS:
		case PrimitiveFileGrammarLexer.GLEICH_GLEICH:
		case PrimitiveFileGrammarLexer.UNGLEICH:
		case PrimitiveFileGrammarLexer.GROESSER:
		case PrimitiveFileGrammarLexer.GROESSER_GLEICH:
		case PrimitiveFileGrammarLexer.KLEINER_GLEICH:
		case PrimitiveFileGrammarLexer.KLEINER:
		case PrimitiveFileGrammarLexer.MAL:
		case PrimitiveFileGrammarLexer.GETEILT:
		case PrimitiveFileGrammarLexer.KOMMA_MAL:
		case PrimitiveFileGrammarLexer.KOMMA_GETEILT:
		case PrimitiveFileGrammarLexer.MODULO:
		case PrimitiveFileGrammarLexer.FRAGEZEICHEN:
		case PrimitiveFileGrammarLexer.DOPPELPUNKT:
		case PrimitiveFileGrammarLexer.LINKS_SCHUB:
		case PrimitiveFileGrammarLexer.ARITMETISCHER_RECHTS_SCHUB:
		case PrimitiveFileGrammarLexer.LOGISCHER_RECHTS_SCHUB:
		case PrimitiveFileGrammarLexer.EXIST_CONSTANT:
		case PrimitiveFileGrammarLexer.RND_KL_AUF:
		case PrimitiveFileGrammarLexer.RND_KL_ZU: {
			RuleNode rn = (RuleNode) inf.tn.getParent();
			Interval i = getTextInterval(rn);
			long num;
			try {
				num = rn.getClass().getField("num").getLong(rn);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				return "error: " + e.getClass().getName() + " msg: " + e.getMessage();
			}
			try {
				return constantToString(inf.ac.constants, null, num) + " = " + document.get(i.a, i.b + 1 - i.a);
			} catch (BadLocationException e) {
				return constantToString(inf.ac.constants, null, num) + " = " + rn.getText();
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

	private String constantToString(Map<String, PrimitiveConstant> constants, String name, long val) {
		StringBuilder build = new StringBuilder();
		if (name != null) {
			build.append('#').append(name).append(" : ");
		}
		build.append(Long.toString(val));
		if (val >= 0) {
			build.append(" [HEX-").append(Long.toHexString(val).toUpperCase());
		} else {
			build.append(" [NHEX" + Long.toString(val, 16).toUpperCase()).append(" = UHEX-").append(Long.toUnsignedString(val, 16).toUpperCase());
		}
		build.append(" FP: ").append(Double.longBitsToDouble(val)).append(']');
		if (name != null) {
			PrimitiveConstant primConst = constants.get(name);
			if (primConst.comment != null) {
				build.append('\n').append(primConst.comment);
			}
		}
		return build.toString();
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 0);
	}
}