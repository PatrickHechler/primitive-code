package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hechler.patrick.codesprachen.gen.SrcGen;
import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;


@SuppressWarnings("javadoc")
public class GenSCStdLibFuncs implements SrcGen {
	
	private record Var(int reg, String name, String type, int pointerCnt, String doc) {
		
	}
	
	private static final Pattern ARGS   = Pattern.compile("^\\*\\s*params\\s*:\\s*$");
	private static final Pattern RESS   = Pattern.compile("^\\*\\s*result\\s*values\\s*:\\s*$");
	private static final Pattern VAR    = Pattern.compile("^ {4}\\*\\s*`(STATUS|X[A-F0-9]{2})`\\s*([a-z][a-zA-Z0-9_]+)\\s*:\\s*\\{`([a-zA-Z]+)(#*)`\\}(.*)$");
	private static final Pattern NO_VAR = Pattern.compile("^\\*.*$");
	
	private static Var var(Matcher matcher) {
		String regG = matcher.group(1);
		int    reg;
		if ("STATUS".equals(regG)) {
			reg = PrimAsmConstants.STATUS;
		} else {
			reg = PrimAsmConstants.X_ADD + Integer.parseInt(regG, 16);
		}
		String name = matcher.group(2);
		String type = matcher.group(3);
		int pointerCnt = matcher.group(4).length();
		String doc  = matcher.group(5).trim();
		return new Var(reg, name, type, pointerCnt, doc);
	}
	
	@Override
	public void generate(Writer out) throws IOException {
		start(out);
		for (PrimAsmConstant parc : SrcGen.PrimAsmConstant.ALL_CONSTANTS) {
			if (!parc.name().startsWith("INT_")) continue;
			List<Var> args = new ArrayList<>();
			List<Var> ress = new ArrayList<>();
			fillLists(parc, args, ress);
			
		}
		end(out);
	}
	
	private static void fillLists(PrimAsmConstant parc, List<Var> args, List<Var> ress) throws AssertionError {
		final int stateNone = 0;
		final int stateArgs = 1;
		final int stateRess = 2;
		int       state     = stateNone;
		for (String doc : parc.docu()) {
			switch (state) {
			case stateNone -> {
				Matcher matcher = ARGS.matcher(doc);
				if (matcher.matches()) {
					state = stateArgs;
					break;
				}
				matcher = RESS.matcher(doc);
				if (matcher.matches()) {
					state = stateRess;
				}
			}
			case stateArgs -> {
				Matcher matcher = VAR.matcher(doc);
				if (!matcher.matches()) {
					state = checkNoVal(stateNone, doc);
					break;
				}
				args.add(var(matcher));
			}
			case stateRess -> {
				Matcher matcher = VAR.matcher(doc);
				if (!matcher.matches()) {
					state = checkNoVal(stateNone, doc);
					break;
				}
				ress.add(var(matcher));
			}
			default -> throw new AssertionError(state);
			}
		}
	}
	
	private static int checkNoVal(final int stateNone, String doc) {
		int state;
		if (!NO_VAR.matcher(doc).matches()) {
			throw new IllegalStateException("unexpected end: '" + doc + "'");
		}
		state = stateNone;
		return state;
	}
	
	private static void end(Writer out) throws IOException {
		out.write("		return Collections.unmodifiableMap(res);\n" //
				+ "	}\n");
	}
	
	private static void start(Writer out) throws IOException {
		out.write("	private static Map<String, Func> allInts() {\n"//
				+ "		Map<String, Func> res = new HashMap<>();\n");
	}
	
}
