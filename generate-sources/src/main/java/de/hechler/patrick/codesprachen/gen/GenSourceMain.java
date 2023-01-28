package de.hechler.patrick.codesprachen.gen;

import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;

public class GenSourceMain {
	
	private static final String GEN_START = "GENERATED-CODE-START";
	private static final String GEN_END   = "GENERATED-CODE-END";
	
	private static final String ASM_COMMANDS_ENUMS = "/users/feri/git/primitive-code/assemble/src/main/java/de/hechler/patrick/codesprachen/primitive/assemble/enums/Commands.java";
	private static final String CORE_COMMANDS      = "/users/feri/git/primitive-code/prim-core/src/main/java/de/hechler/patrick/codesprachen/primitive/core/utils/PrimAsmCommands.java";
	
	public static void main(String[] args) throws IOException, IOError {
		generate(Path.of(ASM_COMMANDS_ENUMS), "\t", new GenAsmEnumCommands());
		generate(Path.of(CORE_COMMANDS), "\t", new GenCorePrimAsmCmds());
	}
	
	private static void generate(Path file, String indent, SrcGen gen) throws IOException, IOError {
		List<String> list = Files.readAllLines(file, StandardCharsets.UTF_8);
		try (Writer out = Files.newBufferedWriter(file, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
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
		}
	}
	
	private static String genEndLines(String indent) {
		return indent + '\n' + indent + "// here is the end of the automatic generated code-block\n" + indent + "// " + GEN_END + "\n";
	}
	
	private static String genStartLines(String indent) {
		return indent + "//" + GEN_START + "\n" + indent + "// this code-block is automatic generated, do not modify\n";
	}
	
	public static void writeLine(Writer out, String line) throws IOException {
		out.write(line);
		out.write('\n');
	}
	
}
