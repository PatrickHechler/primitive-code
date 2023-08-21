package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hechler.patrick.codesprachen.gen.SrcGen;
import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;
import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmPreDefines;


@SuppressWarnings("javadoc")
public class GenSCStdLibIntFuncs implements SrcGen {
	
	private record Var(int reg, String name, String type, int pointerCnt, long flags, String doc) {
		
		public Var {
			if (reg < 0 || reg >= 256 || name == null || type == null || pointerCnt < 0 || doc == null || flags != 0L && pointerCnt != 0) throw new AssertionError();
		}
		
	}
	
	private static final Pattern ARGS        = Pattern.compile("^\\*\\s*params\\s*:\\s*$");
	private static final Pattern RESS        = Pattern.compile("^\\*\\s*result\\s*values\\s*:\\s*$");
	private static final Pattern VAR         = Pattern
			.compile("^ {4}\\*\\s*`(STATUS|X[A-F0-9]{2})`\\s*`([a-z][a-zA-Z0-9_]+)`\\s*:\\s*\\(`([a-zA-Z, `]+)(#*)`\\)(.*)$");
	private static final Pattern NO_VAR_SKIP      = Pattern.compile("^ {4} {4}+\\*.*$");
	private static final Pattern NO_VAR      = Pattern.compile("^\\*.*$");
	private static final Pattern STATUS_FLAG = Pattern.compile("`([a-zA-Z]+)`");
	
	private static Var v(Matcher matcher) {
		String regG = matcher.group(1);
		int    reg;
		if ("STATUS".equals(regG)) {
			reg = PrimAsmConstants.STATUS;
		} else {
			reg = PrimAsmConstants.X_ADD + Integer.parseInt(regG.substring(1), 16);
		}
		String name       = matcher.group(2);
		String type       = matcher.group(3);
		int    pointerCnt = 0;
		long   flags      = 0L;
		if (reg == PrimAsmConstants.STATUS) {
			if (pointerCnt != 0) throw new AssertionError(matcher.group(0));
			Matcher flagMatcher = STATUS_FLAG.matcher('`' + type + '`');
			while (flagMatcher.find()) {
				String flagName = flagMatcher.group(1);
				flags |= PrimAsmConstants.START_CONSTANTS.get("STATUS_" + flagName).value();
			}
			if (flags != (PrimAsmPreDefines.STATUS_LOWER | PrimAsmPreDefines.STATUS_EQUAL | PrimAsmPreDefines.STATUS_GREATER)) {
				throw new AssertionError("unknown status flags: `" + type + "` : 0x" + Long.toHexString(flags));
			}
		} else {
			String pntrGrp = matcher.group(4);
			if (pntrGrp != null) {
				pointerCnt = pntrGrp.length();
			}
		}
		String doc = matcher.group(5).trim();
		return new Var(reg, name, type, pointerCnt, flags, doc);
	}
	
	@Override
	public void generate(Writer out) throws IOException {
		start(out);
		for (PrimAsmConstant pac : SrcGen.PrimAsmConstant.ALL_CONSTANTS) {
			if (!pac.name().startsWith("INT_")) continue;
			List<Var> args = new ArrayList<>();
			List<Var> ress = new ArrayList<>();
			fillLists(pac, args, ress);
			String name = name(pac.name());
			out.write("\t\tres.put(\"" + name + "\", slf(" + pac.value() + ", \"" + name + "\", of(");
			write(out, args);
			out.write("), of(");
			write(out, ress);
			out.write(")");
			writeStatus(out, args, ress);
			out.write("));\n");
		}
		end(out);
	}
	
	private static void writeStatus(Writer out, List<Var> args, List<Var> ress) throws IOException {
		for (Var v : args) {
			if (v.reg == PrimAsmConstants.STATUS) {
				throw new AssertionError("STATUS in params");
			}
		}
		int rs = ress.size();
		if (rs > 0) {
			for (int i = rs - 2; i >= 0; i--) {
				if (ress.get(i).reg == PrimAsmConstants.STATUS) {
					throw new AssertionError("STATUS in results, but not at the end");
				}
			}
			Var last = ress.get(rs - 1);
			if (last.reg == PrimAsmConstants.STATUS) {
				out.write(", 0x" + Long.toHexString(last.flags).toUpperCase() + 'L');
			}
		}
	}
	
	private static void write(Writer out, List<Var> vars) throws IOException {
		if (vars.isEmpty()) return;
		int     ignored = 0;
		int     reg     = PrimAsmConstants.X_ADD;
		boolean first   = true;
		for (Var v : vars) {
			for (; reg < v.reg; reg++, ignored++) {
				first = writeSep(out, first);
				out.write("sv(NUM, 0, \"ignored" + ignored + "\")");
			}
			first = writeSep(out, first);
			if (v.reg == PrimAsmConstants.STATUS) {
				out.write("sv(0x" + Long.toHexString(v.flags).toUpperCase() + "L, \"" + v.name + "\")");
			} else {
				out.write("sv(" + v.type.toUpperCase() + ", " + v.pointerCnt + ", \"" + v.name + "\")");
			}
			reg++;
		}
	}
	
	private static boolean writeSep(Writer out, boolean first) throws IOException {
		if (!first) {
			out.write(", ");
		}
		return false;
	}
	
	private static String name(String name) {
		StringBuilder b = new StringBuilder(name.length() - 4);
		for (int i = 4; i < name.length();) {
			int ni = name.indexOf('_', i);
			if (i == 4) i--;
			else b.append(name.charAt(i));
			if (ni == -1) ni = name.length();
			String sub = name.substring(i + 1, ni);
			b.append(sub.toLowerCase());
			i = ni + 1;
		}
		return b.toString();
	}
	
	private static final int STATE_NONE = 0;
	private static final int STATE_ARGS = 1;
	private static final int STATE_RESS = 2;
	
	private static void fillLists(PrimAsmConstant parc, List<Var> args, List<Var> ress) throws AssertionError {
		int state = STATE_NONE;
		for (String doc : parc.docu()) {
			state = fillDocLine(args, ress, state, doc);
		}
	}
	
	private static int fillDocLine(List<Var> args, List<Var> ress, int state, String doc) throws AssertionError {
		return switch (state) {
		case STATE_NONE -> {
			Matcher matcher = ARGS.matcher(doc);
			if (matcher.matches()) {
				yield STATE_ARGS;
			}
			matcher = RESS.matcher(doc);
			if (matcher.matches()) {
				yield STATE_RESS;
			}
			yield state;
		}
		case STATE_ARGS -> processVar(args, ress, doc, args, state);
		case STATE_RESS -> processVar(args, ress, doc, ress, state);
		default -> throw new AssertionError(state);
		};
	}
	
	private static int processVar(List<Var> args, List<Var> ress, String doc, List<Var> useList, int state) throws AssertionError {
		Matcher matcher = VAR.matcher(doc);
		if (!matcher.matches()) {
			if (NO_VAR_SKIP.matcher(doc).matches()) {
				return state;
			}
			checkNoVal(doc);
			return fillDocLine(args, ress, STATE_NONE, doc);
		}
		Var v = v(matcher);
		if (!useList.isEmpty() && useList.get(useList.size() - 1).reg >= v.reg) throw new AssertionError(useList + " " + v);
		useList.add(v);
		return state;
	}
	
	private static int checkNoVal(String doc) {
		if (!NO_VAR.matcher(doc).matches()) {
			throw new IllegalStateException("unexpected end: '" + doc + "'");
		}
		return STATE_NONE;
	}
	
	private static void end(Writer out) throws IOException {
		out.write("\t\treturn res;\n" //
				+ "}\n");
	}
	
	private static void start(Writer out) throws IOException {
		out.write("\tprivate static Map<String, StdLibFunc> allInts() {\n"//
				+ "\t\tMap<String, StdLibFunc> res = new HashMap<>();\n");
	}
	
}
