// This file is part of the Patr File System and Code Projects
// DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
// Copyright (C) 2023 Patrick Hechler
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.gen;

import java.io.IOError;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import de.hechler.patrick.codesprachen.gen.impl.GenAsmEnumCommands;
import de.hechler.patrick.codesprachen.gen.impl.GenCorePredefined;
import de.hechler.patrick.codesprachen.gen.impl.GenCorePrimAsmCmds;
import de.hechler.patrick.codesprachen.gen.impl.GenDisasmEnumCommands;
import de.hechler.patrick.codesprachen.gen.impl.GenRunCommandArray;
import de.hechler.patrick.codesprachen.gen.impl.GenRunCommandFuncs;
import de.hechler.patrick.codesprachen.gen.impl.GenCErrEnum;
import de.hechler.patrick.codesprachen.gen.impl.GenRunIntHeader;
import de.hechler.patrick.codesprachen.gen.impl.GenSCStdLibFuncs;

@SuppressWarnings("javadoc")
public class GenSourceMain {
	
	private static final String GEN_START = "GENERATED-CODE-START";
	private static final String GEN_END   = "GENERATED-CODE-END";
	
	private static final String ASM_COMMANDS_ENUM    = SrcGen.PRIMITIVE_CODE_DIR
			+ "assemble/src/main/java/de/hechler/patrick/codesprachen/primitive/assemble/enums/Commands.java";
	private static final String CORE_COMMANDS        = SrcGen.PRIMITIVE_CODE_DIR
			+ "prim-core/src/main/java/de/hechler/patrick/codesprachen/primitive/core/utils/PrimAsmCommands.java";
	private static final String CORE_PRE_DEFS_CLS    = SrcGen.PRIMITIVE_CODE_DIR
			+ "prim-core/src/main/java/de/hechler/patrick/codesprachen/primitive/core/utils/PrimAsmPreDefines.java";
	private static final String CORE_PRE_DEFS_RES    = SrcGen.PRIMITIVE_CODE_DIR
			+ "prim-core/src/main/resources/de/hechler/patrick/codesprachen/primitive/core/predefined-constants.psf";
	private static final String DISASM_COMMANDS_ENUM = SrcGen.PRIMITIVE_CODE_DIR
			+ "disassemble/src/main/java/de/hechler/patrick/codesprachen/primitive/disassemble/enums/Commands.java";
	private static final String SC_SDTLIB_FUNCS      = SrcGen.SIMPLE_COMPILE_DIR
			+ "src/main/java/de/hechler/patrick/codesprachen/simple/compile/objects/compiler/StdLib.java";
	private static final String RUN_COMMAND_FUNCS    = SrcGen.PRIMITIVE_CODE_DIR + "native-runtime/src/pvm-cmd.h";
	private static final String RUN_COMMAND_ARRAY    = SrcGen.PRIMITIVE_CODE_DIR + "native-runtime/src/pvm-cmd-cmds-gen.h";
	private static final String RUN_INT_HEADER       = SrcGen.PRIMITIVE_CODE_DIR + "native-runtime/src/pvm-int.h";
	private static final String RUN_ERR_HEADER       = SrcGen.PRIMITIVE_CODE_DIR + "native-runtime/src/pvm-err.h";
	private static final String PFS_ERR_HEADER       = SrcGen.PATR_FILE_SYS_DIR + "pfs-core/src/include/pfs-err.h";
	
	public static void main(String[] args) throws IOException, IOError {
		generate(Path.of(ASM_COMMANDS_ENUM), "\t", new GenAsmEnumCommands());
		generate(Path.of(CORE_COMMANDS), "\t", new GenCorePrimAsmCmds());
		generate(Path.of(CORE_PRE_DEFS_CLS), "\t", new GenCorePredefined(true));
		generate0(Path.of(CORE_PRE_DEFS_RES), new GenCorePredefined(false));
		generate(Path.of(DISASM_COMMANDS_ENUM), "\t", new GenDisasmEnumCommands());
		generate(Path.of(RUN_COMMAND_FUNCS), "", new GenRunCommandFuncs());
		generate(Path.of(RUN_COMMAND_ARRAY), "", new GenRunCommandArray());
		generate0(Path.of(RUN_INT_HEADER), new GenRunIntHeader());
		generate(Path.of(RUN_ERR_HEADER), "\t", new GenCErrEnum("PE_"));
		generate(Path.of(PFS_ERR_HEADER), "\t", new GenCErrEnum("PFS_ERRNO_"));
		generate(Path.of(SC_SDTLIB_FUNCS), "\t", new GenSCStdLibFuncs());
		System.out.println("finish");
	}
	
	private static void generate0(Path file, SrcGen gen) throws IOException, IOError {
		List<String> list = Files.readAllLines(file, StandardCharsets.UTF_8);
		try (Writer out = Files.newBufferedWriter(file)) {
			gen.generate(out);
		} catch (Throwable t) {
			setContent(file, list);
			throw t;
		}
	}
	
	@SuppressWarnings("unused")
	private static void generate(Path file, SrcGen gen) throws IOException, IOError {
		List<String> list = Files.readAllLines(file, StandardCharsets.UTF_8);
		try (OutputStream out = Files.newOutputStream(file)) {
			Files.copy(file.resolveSibling("start-" + file.getFileName()), out);
			try (Writer w = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
				gen.generate(w);
			}
			Files.copy(file.resolveSibling("end-" + file.getFileName()), out);
		} catch (Throwable t) {
			setContent(file, list);
			throw t;
		}
	}
	
	private static void generate(Path file, String indent, SrcGen gen) throws IOException, IOError {
		List<String> list = Files.readAllLines(file, StandardCharsets.UTF_8);
		try (Writer out = Files.newBufferedWriter(file)) {
			for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
				String line = iter.next();
				if (!line.contains(GEN_START)) {
					writeLine(out, line);
				} else {
					out.write(genStartLines(indent));
					while (iter.hasNext()) {
						line = iter.next();
						if (line.contains(GEN_END)) {
							break;
						}
					}
					gen.generate(out);
					out.write(genEndLines(indent));
				}
			}
		} catch (Throwable t) {
			setContent(file, list);
			throw t;
		}
	}
	
	private static void setContent(Path file, List<String> list) throws IOException {
		try (Writer out = Files.newBufferedWriter(file)) {
			for (String line : list) {
				out.append(line).append('\n');
			}
		}
	}
	
	private static String genEndLines(String indent) {
		return indent + '\n' + indent + "// here is the end of the automatic generated code-block\n" + indent + "// " + GEN_END + "\n";
	}
	
	private static String genStartLines(String indent) {
		return indent + "// " + GEN_START + "\n" + indent + "// this code-block is automatic generated, do not modify\n";
	}
	
	public static void writeLine(Writer out, String line) throws IOException {
		out.write(line);
		out.write('\n');
	}
	
}
