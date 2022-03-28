package de.hechler.patrick.codesprachen.primitive.eclplugin.objects;

import org.antlr.v4.runtime.tree.TerminalNode;

import de.hechler.patrick.codesprachen.primitive.assemble.ConstantPoolGrammarParser.CpanythingContext;
import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.AnythingContext;

public class TokenInfo {

	public static final CpanythingContext CONST_POOL_START_END_TOKEN = new CpanythingContext(null, 0);
	
	public final TerminalNode tn;
	public final AnythingContext ac;
	public final CpanythingContext cpac;

	public TokenInfo(TerminalNode tn, AnythingContext ac) {
		this(tn, ac, null);
	}

	public TokenInfo(TerminalNode tn, AnythingContext ac, CpanythingContext cpac) {
		this.tn = tn;
		this.ac = ac;
		this.cpac = cpac;
	}

}
