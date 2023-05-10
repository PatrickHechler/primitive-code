package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;

import de.hechler.patrick.codesprachen.gen.SrcGen;


public class GenDisasmEnumCommands implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		write(out, ParamType.NO_CONST_PARAM, ParamType.PARAM, ParamType.CONST_PARAM, "THREE_PARAMS_P1_NO_CONST_P2_ALLOW_CONST_P3_COMPILE_CONST");
		write(out, ParamType.NO_CONST_PARAM, ParamType.NO_CONST_PARAM, null, "TWO_PARAMS_NO_CONSTS");
		write(out, ParamType.NO_CONST_PARAM, ParamType.PARAM, null, "TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST");
		write(out, ParamType.PARAM, ParamType.PARAM, null, "TWO_PARAMS_ALLOW_CONSTS");
		write(out, ParamType.PARAM, ParamType.CONST_PARAM, null, "TWO_PARAMS_P1_NO_CONST_P2_COMPILE_CONST");
		write(out, ParamType.NO_CONST_PARAM, null, null, "ONE_PARAM_NO_CONST");
		write(out, ParamType.PARAM, null, null, "ONE_PARAM_ALLOW_CONST");
		write(out, ParamType.LABEL, null, null, "LABEL_OR_CONST");
	}
	
	private static void write(Writer out, ParamType p1, ParamType p2, ParamType p3, String paramArt) throws IOException {
		out.write("\t\n");
		if (p3 != null) {
			out.write("\t// Params: <" + p1 + "> , <" + p2 + "> , <" + p3 + ">\n");
		} else if (p2 != null) {
			out.write("\t// Params: <" + p1 + "> , <" + p2 + ">\n");
		} else if (p1 != null) {
			out.write("\t// Params: <" + p1 + ">\n");
		} else {
			out.write("\t// Params: none \n");
		}
		for (PrimAsmReadmeCommand cmd : SrcGen.PrimAsmReadmeCommand.ALL_CMDS) {
			if (cmd.p1() != p1 || cmd.p2() != p2 || cmd.p3() != p3) {
				continue;
			}
			out.write("\tCMD_" + cmd.name() + "(ParamArt." + paramArt + ", " + cmd.name() + "),\n");
		}
	}
	
}
