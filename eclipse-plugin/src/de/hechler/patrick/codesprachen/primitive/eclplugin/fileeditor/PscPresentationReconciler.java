package de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getDocVal;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTokenInfo;

import java.util.NoSuchElementException;

import org.antlr.v4.runtime.RuleContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.CpanythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.NumconstContext;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.StringContext;
import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.String_appendContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.CommandContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungDirektContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungExclusivoderContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungGleichheitContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungInclusivoderContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungPunktContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungRelativeTestsContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungSchubContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungStrichContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ConstBerechnungUndContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.NummerContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.NummerNoConstantContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParamContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.SrContext;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.DocumentValue;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.TokenInfo;

public class PscPresentationReconciler extends PresentationReconciler {

	public PscPresentationReconciler() {
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new PrimTokScanner());
		this.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		this.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
	}

	private static class PrimTokScanner implements ITokenScanner {

		private static final TextAttribute TA_LABEL = new TextAttribute(new Color(new RGB(0, 127, 127)));
		private static final TextAttribute TA_CONSTANT = new TextAttribute(new Color(new RGB(127, 0, 127)));
		private static final TextAttribute TA_EXPORT = new TextAttribute(new Color(new RGB(127, 0, 255)));
		private static final TextAttribute TA_COMMAND = new TextAttribute(new Color(new RGB(127, 127, 0)));
		private static final TextAttribute TA_PARAM_VAL = new TextAttribute(new Color(new RGB(191, 191, 32)));
		private static final TextAttribute TA_COMMENT = new TextAttribute(new Color(new RGB(127, 127, 127)));
		private static final TextAttribute TA_PRE = new TextAttribute(new Color(new RGB(127, 127, 255)));
		private static final TextAttribute TA_WRONG = new TextAttribute(new Color(new RGB(255, 0, 0)));
		private static final TextAttribute TA_CONST_CALC = new TextAttribute(new Color(new RGB(127, 127, 255)));
		private static final TextAttribute TA_KL = new TextAttribute(new Color(new RGB(0, 127, 255)));
		private static final TextAttribute TA_STRING = new TextAttribute(new Color(new RGB(0, 191, 0)));
		private static final TextAttribute TA_CHARS = new TextAttribute(new Color(new RGB(63, 127, 63)));

		@SuppressWarnings("unused") /* just for debugging */
		private IDocument document;
		private DocumentValue doc;
		private int off;
		private int oldoff;
		private int end;

		@Override
		public void setRange(IDocument document, int offset, int length) {
			this.document = document;
			this.doc = getDocVal(document);
			this.off = offset;
			this.end = offset + length;
			this.oldoff = -1;
		}

		@Override
		public IToken nextToken() {
			TokenInfo tinf;
			if (off >= end) {
				return Token.EOF;
			}
			try {
				tinf = getTokenInfo(doc, off);
			} catch (NoSuchElementException e) {
				setOffset(null, 0);
				return Token.WHITESPACE;
			}
			if (tinf.tn.getSymbol().getType() == -1) {
				return Token.EOF;
			}
			if (tinf.cpac != null) {
				return nextCPToken(tinf);
			}
			setOffset(tinf.tn.getSymbol(), 0);
			if (!tinf.ac.enabled && !tinf.ac.enabled_) {
				return new Token(TA_COMMENT);
			}
			RuleContext rc = (RuleContext) tinf.tn.getParent();
			if (rc instanceof CommandContext) {
				if (tinf.tn.getSymbol().getType() == PrimitiveFileGrammarLexer.LABEL_DECLARATION) {
					return new Token(TA_LABEL);
				} else {
					return new Token(TA_COMMAND);
				}
			} else if (rc instanceof SrContext) {
				return new Token(TA_PARAM_VAL);
			} else if (rc instanceof ParamContext) {
				switch (tinf.tn.getSymbol().getType()) {
				case PrimitiveFileGrammarLexer.ECK_KL_AUF:
				case PrimitiveFileGrammarLexer.ECK_KL_ZU:
					return new Token(TA_KL);
				case PrimitiveFileGrammarLexer.NAME:
					if (tinf.ac.constants.containsKey(tinf.tn.getText())) {
						return new Token(TA_CONSTANT);
					} else {
						return new Token(TA_LABEL);
					}
				default:
					return new Token(TA_PARAM_VAL);
				}
			} else if (rc instanceof PrimitiveFileGrammarParser.CommentContext) {
				return new Token(TA_COMMENT);
			} else if (rc instanceof AnythingContext) {
				switch (tinf.tn.getSymbol().getType()) {
				case PrimitiveFileGrammarLexer.CONSTANT:
					return new Token(TA_CONSTANT);
				case PrimitiveFileGrammarLexer.EXPORT_CONSTANT:
				case PrimitiveFileGrammarLexer.ADD_CONSTANT:
				case PrimitiveFileGrammarLexer.READ_SYM:
				case PrimitiveFileGrammarLexer.MY_CONSTS:
				case PrimitiveFileGrammarLexer.SYMBOL:
				case PrimitiveFileGrammarLexer.SOURCE:
				case PrimitiveFileGrammarLexer.GROESSER:
					return new Token(TA_EXPORT);
				case PrimitiveFileGrammarLexer.CD_ALIGN:
				case PrimitiveFileGrammarLexer.CD_NOT_ALIGN:
				case PrimitiveFileGrammarLexer.IF:
				case PrimitiveFileGrammarLexer.ELSE:
				case PrimitiveFileGrammarLexer.ELSE_IF:
				case PrimitiveFileGrammarLexer.ENDIF:
				case PrimitiveFileGrammarLexer.DEL:
					return new Token(TA_PRE);
				case PrimitiveFileGrammarLexer.STR_STR:
					return new Token(TA_STRING);
				case PrimitiveFileGrammarLexer.ERROR:
				case PrimitiveFileGrammarLexer.ERROR_HEX:
				case PrimitiveFileGrammarLexer.ERROR_MESSAGE_START:
				case PrimitiveFileGrammarLexer.ERROR_MESSAGE_END:
				default:
					return new Token(TA_WRONG);
				}
			} else if (rc instanceof NummerNoConstantContext) {
				if (rc.getParent() instanceof ParamContext) {
					return new Token(TA_PARAM_VAL);
				} else if (tinf.tn.getSymbol().getType() == PrimitiveFileGrammarLexer.POS) {
					return new Token(TA_CONSTANT);
				} else {
					return new Token(TA_CONST_CALC);
				}
			} else if (rc instanceof NummerContext || isConstCalc(rc)) {
				if (tinf.ac.constants.containsKey(tinf.tn.getText().replaceFirst("^(#~)?([a-zA-Z_]+)$", "$2"))) {
					return new Token(TA_CONSTANT);
				} else {
					return new Token(TA_CONST_CALC);
				}
			} else {
				return new Token(TA_WRONG);
			}
		}

		private boolean isConstCalc(RuleContext rc) {
			//@formatter:off
			return     rc instanceof ConstBerechnungDirektContext        || rc instanceof ConstBerechnungContext
					|| rc instanceof ConstBerechnungInclusivoderContext  || rc instanceof ConstBerechnungExclusivoderContext
					|| rc instanceof ConstBerechnungUndContext           || rc instanceof ConstBerechnungGleichheitContext
					|| rc instanceof ConstBerechnungRelativeTestsContext || rc instanceof ConstBerechnungSchubContext
					|| rc instanceof ConstBerechnungStrichContext        || rc instanceof ConstBerechnungPunktContext;
			//@formatter:on
		}

		/**
		 * sets the {@link #off}set and the {@link #oldoff}set
		 * 
		 * @param tok the rule or <code>null</code> if there is a whitespace token at
		 *            the current offset
		 */
		private void setOffset(org.antlr.v4.runtime.Token tok, int cpadd) {
			oldoff = off;
			if (tok != null) {
				off = tok.getStopIndex();
			}
			off += cpadd + 1;
			assert cpadd >= 0;
			assert off > oldoff;
			assert off <= end;
		}

		private IToken nextCPToken(TokenInfo tinf) {
			setOffset(tinf.tn.getSymbol(), tinf.ac.start.getStartIndex());
			RuleContext rc = (RuleContext) tinf.tn.getParent();
			if (tinf.cpac == TokenInfo.CONST_POOL_START_END_TOKEN) {
				return new Token(TA_KL);
			} else if (rc instanceof ConstsContext) {
				return new Token(TA_WRONG);
			} else if (rc instanceof CpanythingContext) {
				switch (tinf.tn.getSymbol().getType()) {
				case ConstantPoolGrammarLexer.CD_ALIGN:
				case ConstantPoolGrammarLexer.CD_NOT_ALIGN:
					return new Token(TA_PRE);
				case ConstantPoolGrammarLexer.STR_STR:
					return new Token(TA_STRING);
				case ConstantPoolGrammarLexer.ERROR:
				case ConstantPoolGrammarLexer.ERROR_HEX:
				case ConstantPoolGrammarLexer.ERROR_MESSAGE:
				case ConstantPoolGrammarLexer.ERROR_MESSAGE_START:
				case ConstantPoolGrammarLexer.ERROR_MESSAGE_END:
				default:
					return new Token(TA_WRONG);
				}
			} else if (rc instanceof StringContext) {
				switch (tinf.tn.getSymbol().getType()) {
				case ConstantPoolGrammarLexer.MULTI_STR_START:
				case ConstantPoolGrammarLexer.MULTI_STR_END:
					return new Token(TA_KL);
				case ConstantPoolGrammarLexer.CHAR_STR:
				case ConstantPoolGrammarLexer.CHARS:
					return new Token(TA_CHARS);
				default:
					return new Token(TA_WRONG);
				}
			} else if (rc instanceof String_appendContext) {
				return new Token(TA_STRING);
			} else if (rc instanceof NumconstContext) {
				if (tinf.cpac.constants.containsKey(rc.getText()) || tinf.tn.getSymbol().getType() == PrimitiveFileGrammarLexer.POS) {
					return new Token(TA_CONSTANT);
				} else {
					return new Token(TA_CONST_CALC);
				}
			} else if (rc instanceof ConstantPoolGrammarParser.CommentContext) {
				return new Token(TA_COMMENT);
			} else {
				throw new InternalError("unknown rule (in const-pool): " + rc.getClass().getName());
			}
		}

		@Override
		public int getTokenOffset() {
			return oldoff;
		}

		@Override
		public int getTokenLength() {
			return off - oldoff;
		}

	}

}
