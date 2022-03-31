package de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getDocVal;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTokenInfo;

import java.util.NoSuchElementException;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.DocumentValue;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.TokenInfo;

public class PscAutoEditStrategy implements IAutoEditStrategy {

	private static boolean enableAutoEdit = true;

	@Override
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		if (!enableAutoEdit) {
			return;
		}
		try {
			DocumentValue docVal = getDocVal(document);
			TokenInfo tokInf;
			try {
				tokInf = getTokenInfo(docVal, command.offset);
			} catch (NoSuchElementException e) {
				tokInf = null;
			}
			//@formatter:off
//			System.err.println("[Command]: text: " + command.text);
//			System.err.println("[TerminalNode]: class: " + tokInf.tn.getClass());
//			System.err.println("[TerminalNode]: text: " + tokInf.tn.getText());
//			System.err.println("[TerminalNode.Symbol]: text: " + tokInf.tn.getSymbol().getClass());
//			System.err.println("[TerminalNode.Symbol]: type: " + tokInf.tn.getSymbol().getType());
			//@formatter:on
			if (tokInf != null && matchesOff(command, tokInf) && (isString(tokInf) || isComment(tokInf))) {
				return;
			}
			switch (command.text) {
			case "'":
			case "\"": {
				boolean charString = command.text.equals("'");
				IRegion info = document.getLineInformationOfOffset(command.offset);
				String line = document.get(info.getOffset(), info.getLength());
				int inLineOff = command.offset - info.getOffset();
				char[] chars = line.toCharArray();
				for (int i = 0; i <= inLineOff; i++) {
					if (i == inLineOff) {
						if (charString) {
							command.text = "''";
						} else {
							command.text = "\"\"";
						}
						break;
					}
					switch (chars[i]) {
					case '"':
						for (i++; i < inLineOff; i++) {
							if (chars[i] == '"') {
								break;
							}
						}
						break;
					case '\'':
						for (i++; i < inLineOff; i++) {
							if (chars[i] == '\'') {
								break;
							}
						}
						break;
					}
				}
				break;
			}
			case ":": {
				if (document.getChar(command.offset - 1) != '|') {
					return;
				}
				command.text = "::>";
				break;
			}
			default:
				break;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private boolean isComment(TokenInfo tokInf) {
		if (tokInf.cpac != null) {
			return !(tokInf.tn instanceof ErrorNode)
					&& (tokInf.tn.getSymbol().getType() == ConstantPoolGrammarLexer.BLOCK_COMMENT || tokInf.tn.getSymbol().getType() == ConstantPoolGrammarLexer.LINE_COMMENT);
		} else {
			return !(tokInf.tn instanceof ErrorNode)
					&& (tokInf.tn.getSymbol().getType() == PrimitiveFileGrammarLexer.BLOCK_COMMENT || tokInf.tn.getSymbol().getType() == PrimitiveFileGrammarLexer.LINE_COMMENT);
		}
	}

	private boolean isString(TokenInfo tokInf) {
		if (tokInf.cpac != null) {
			return tokInf.tn.getSymbol().getType() == ConstantPoolGrammarLexer.STR_STR || tokInf.tn.getSymbol().getType() == ConstantPoolGrammarLexer.CHAR_STR;
		} else {
			return tokInf.tn.getSymbol().getType() == PrimitiveFileGrammarLexer.STR_STR || tokInf.tn.getSymbol().getType() == ConstantPoolGrammarLexer.STR_STR;
		}
	}

	private boolean matchesOff(DocumentCommand command, TokenInfo tokInf) {
		return tokInf.tn.getSymbol().getStartIndex() + (tokInf.ac.CONSTANT_POOL == null ? 0 : tokInf.ac.CONSTANT_POOL.getStartIndex()) - command.offset <= 0;
	}

}
