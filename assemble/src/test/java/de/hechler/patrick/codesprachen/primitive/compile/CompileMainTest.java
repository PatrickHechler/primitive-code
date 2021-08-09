package de.hechler.patrick.codesprachen.primitive.compile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarParser;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.Command;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.Command.ConstantPoolCommand;

public class CompileMainTest {
	
	public void testname() throws Exception {
		main(new String[0]);
	}
	
	public static void main(String[] args) throws IOException {
		InputStream ras = CompileMainTest.class.getResourceAsStream("/testin.prmc");
		if (ras == null) {
			ras = CompileMainTest.class.getResourceAsStream("/resources/testin.prmc");
		}
		ANTLRInputStream in = new ANTLRInputStream(ras);
		PrimitiveFileGrammarLexer lexer = new PrimitiveFileGrammarLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PrimitiveFileGrammarParser parser = new PrimitiveFileGrammarParser(tokens);
		ParseContext parsed = parser.parse();
		System.out.println(parsed.toStringTree());
		System.out.println(parsed.children.size());
		parsed.children.forEach(new Consumer <ParseTree>() {
		
			public void accept(ParseTree pt) 
			{
				System.out.println("\tchild: " + pt);
				System.out.println("\tchild-childs: " + pt.getChildCount());
				System.out.println("\tchild-token: " + pt.getClass());
				if (pt instanceof PrimitiveFileGrammarParser.CommandContext) {
					System.out.println("\tenter child ------------------");
					((PrimitiveFileGrammarParser.CommandContext)pt).children.forEach(this);
					System.out.println("\texit child  ------------------");
				} else if (pt instanceof TerminalNodeImpl) {
					TerminalNodeImpl tni = (TerminalNodeImpl) pt;
					int type = ((CommonToken)tni.symbol).getType();
					System.out.println("\tsymbol-type: " + type);
					System.out.println("\tsymbol-type_name: " + (type == -1 ? "EOF" : PrimitiveFileGrammarParser.tokenNames[type]));
				}
				for (int i = 0; i < pt.getChildCount(); i ++ ) {
					System.out.println("\t\tchild["+i+"]: " + pt.getChild(i));
					System.out.println("\t\tchild["+i+"]-childs: " + pt.getChild(i).getChildCount());
					if (pt.getChild(i) instanceof PrimitiveFileGrammarParser.CommandContext) {
						System.out.println("\t\tenter child --------------------");
						((PrimitiveFileGrammarParser.CommandContext)pt.getChild(i)).children.forEach(this);
						System.out.println("\t\texit child  --------------------");
					} else if (pt.getChild(i) instanceof TerminalNodeImpl) {
						TerminalNodeImpl tni = (TerminalNodeImpl) pt.getChild(i);
						int type = ((CommonToken)tni.symbol).getType();
						System.out.println("\t\tsymbol-type: " + type);
						System.out.println("\t\tsymbol-type_name: " + (type == -1 ? "EOF" : PrimitiveFileGrammarParser.tokenNames[type]));
					}
				}
			}
		});
		System.out.println("FINISH");
		Map <String, Long> l = new HashMap<>();
		Map<String, Long> c = new HashMap<>();
		ConstantPoolCommand cp = Command.parseCP(":\r\n"
				+ "#HELLO-WORLD --POS--\r\n"
				+ "CHARS 'ASCII' \"hello world\"\r\n"
				+ "@HELLO-WORLD_END\r\n"
				+ "HEX: AA BB CC DD  00 11 22 33\r\n"
				+ "0123456789abcdef\r\n"
				+ ">", c, l, 0);
		System.out.println("consts: " + c);
		System.out.println("labels: " + l);
		System.out.println("cpool-len: " + cp.length());
	}
}
