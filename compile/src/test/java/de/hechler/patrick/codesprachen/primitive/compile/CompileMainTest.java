package de.hechler.patrick.codesprachen.primitive.compile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import de.hechler.patrick.antlr.v4.codespr.primitive.PrimGrammarLexer;
import de.hechler.patrick.antlr.v4.codespr.primitive.PrimGrammarParser;
import de.hechler.patrick.codesprachen.primitive.compile.objects.PrimitiveCompiler;

public class CompileMainTest {
	
	public void testname() throws Exception {
		main(new String[0]);
	}
	
	public static void main(String[] args) throws IOException {
		PrimitiveCompiler pc = new PrimitiveCompiler(new FileOutputStream("./target/test.pbc"));
		InputStream ras = CompileMainTest.class.getResourceAsStream("/testin.prmc");
		if (ras == null) {
			ras = CompileMainTest.class.getResourceAsStream("/resources/testin.prmc");
		}
		ANTLRInputStream in = new ANTLRInputStream(ras);
		PrimGrammarLexer lexer = new PrimGrammarLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PrimGrammarParser parser = new PrimGrammarParser(tokens);
		pc.compile(parser.datei());
		System.out.println("FINISH-testin");
		pc = new PrimitiveCompiler(new FileOutputStream("./target/test-1.pbc"));
		ras = CompileMainTest.class.getResourceAsStream("/testin1.prmc");
		if (ras == null) {
			ras = CompileMainTest.class.getResourceAsStream("/resources/testin1.prmc");
		}
		in = new ANTLRInputStream(ras);
		lexer = new PrimGrammarLexer(in);
		tokens = new CommonTokenStream(lexer);
		parser = new PrimGrammarParser(tokens);
		pc.compile(parser.datei());
		System.out.println("FINISH-testin1");
	}
}
