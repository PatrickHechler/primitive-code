package de.hechler.patrick.codesprachen.primitive.compile;

import java.io.PrintStream;
import java.util.Arrays;

public class CompileMain {
	
	public static void main(String[] args) {
		init(args);
		
		
	}
	
	private static void init(String[] args) {
		exit(args, -1, "not yet implemented");
	}

	private static void help(PrintStream out) {
		out.println("primitive compiler help:");
		out.println("\t<-src> [SOURCE_FILE]");
		out.println("\t\tto set the source-code file");
		out.println("\t<-target> [TARGET_FILE]");
		out.println("\t\tto set the target file");
		out.println("\t<-force>");
		out.println("\t\tto set overwrite the target file if it exist already");
	}
	
	private static void exit(String[] args, int index, String msg) {
		System.err.println("invalid args: index=" + index + " args: " + Arrays.deepToString(args));
		System.err.println(msg);
		help(System.err);
		Runtime.getRuntime().exit(1);
	}
	
}
