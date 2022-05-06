package de.hechler.patrick.codesprachen.primitive.eclplugin.hyperlink;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getDocVal;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant.getTokenInfo;

import java.nio.file.Path;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveConstant;
import de.hechler.patrick.codesprachen.primitive.eclplugin.Activator;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.DocumentValue;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.TokenInfo;

public class PrimitiveHyperlinkDetector implements IHyperlinkDetector {

	public static final String MY_ID = Activator.PLUGIN_ID + ".primitiveHyperlinkDetector";

	@Override
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		DocumentValue docVal = getDocVal(textViewer.getDocument());
		TokenInfo tokinf = getTokenInfo(docVal, region.getOffset());
		Token tok = tokinf.tn.getSymbol();
		if (tokinf.cpac == null && tok.getType() != PrimitiveFileGrammarLexer.NAME || tokinf.cpac != null && tok.getType() != ConstantPoolGrammarLexer.NAME) {
			return null;
		}
		Map<String, PrimitiveConstant> consts = tokinf.cpac == null ? tokinf.ac.constants_ : tokinf.cpac.constants_;
		String name = tokinf.tn.getText();
		PrimitiveConstant primConst = consts.get(name);
		Path path;
		int line;
		if (primConst != null) {
			path = primConst.path;
			line = primConst.line;
		} else {
			path = null;
			line = -1;
			ParserRuleContext rule = tokinf.ac.getParent();
			for (ParseTree pt : rule.children) {
				if (pt instanceof AnythingContext) {
					AnythingContext ac = (AnythingContext) pt;
					if (ac.command != null && ac.command.LABEL_DECLARATION != null) {
						if (ac.command.LABEL_DECLARATION.getText().substring(1).equals(name)) {
							path = ((ParseContext) rule).path;
							line = ac.command.LABEL_DECLARATION.getLine();
						}
					}
				}
			}
		}
		return new IHyperlink[]{new PrimitiveHyperlink(path, line, new Region(tok.getStartIndex(), tok.getStopIndex() - tok.getStartIndex() + 1), name + ":" + line)};
	}

}
