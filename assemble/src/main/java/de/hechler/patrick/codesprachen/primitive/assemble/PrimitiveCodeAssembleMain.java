package de.hechler.patrick.codesprachen.primitive.assemble;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;

public class PrimitiveCodeAssembleMain {
	
	private static PrimitiveAssembler asm;
	private static Reader             input;
	
	public static void main(String[] args) {
		setup(args);
		try {
			asm.assemble(null, input);
			System.out.println("assembled successful");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static void setup(String[] args) {
		Charset cs = null;
		String inFile = null;
		String outFile = null;
		boolean sw = false;
		boolean ne = false;
		try {
			for (int i = 0; i < args.length; i ++ ) {
				switch (args[i].toLowerCase()) {
				case "--help":
					help();
					break;
				case "--cs":
				case "--charset":
					if (args.length <= ++ i) {
						crash(args, i, "not enugh args for charset option");
					}
					cs = Charset.forName(args[i]);
					break;
				case "-pa":
				case "-primasm":
					if (args.length <= ++ i) {
						crash(args, i, "not enugh args for primasm option");
					}
					inFile = args[i];
					break;
				case "-out":
					if (args.length <= ++ i) {
						crash(args, i, "not enugh args for out option");
					}
					outFile = args[i];
					break;
				case "-sw":
				case "-suppress-warns":
					sw = true;
					break;
				case "-ne":
				case "-no-export":
					ne = true;
					break;
				default:
					crash(args, i, "unknown arg: " + args[i]);
				}
			}
			if (inFile == null) {
				crash(args, -1, "no input file set (use primasm)");
			}
			if (outFile == null) {
				int li = inFile.lastIndexOf('.');
				li = li == -1 ? inFile.length() : li;
				outFile = inFile.substring(0, li) + ".pmc";
			}
			if (cs == null) {
				cs = StandardCharsets.UTF_8;
			}
			input = Files.newBufferedReader(Paths.get(inFile), cs);
			OutputStream outFileStream = new FileOutputStream(outFile);
			if (ne) {
				asm = new PrimitiveAssembler(outFileStream, null, new Path[] {Paths.get(".") }, sw, true);
			} else {
				String exportFile;
				if (outFile.endsWith(".pmc")) {
					exportFile = outFile.substring(0, outFile.length() - 3) + "psf";
				} else {
					exportFile = outFile + ".psf";
				}
				OutputStream exportOutStream = Files.newOutputStream(Paths.get(exportFile));
				asm = new PrimitiveAssembler(outFileStream, new PrintStream(exportOutStream, true, cs), new Path[] {Paths.get(".") }, sw, true);
			}
		} catch (Exception e) {
			crash(args, -1, e.getMessage());
		}
	}
	
	private static void crash(String[] args, int index, String msg) {
		System.err.println(msg);
		for (int i = 0; i < args.length; i ++ ) {
			if (i == index) {
				System.err.print("error happanded here -> ");
			}
			System.err.println("[" + i + "]='" + args[i] + "'");
		}
		System.exit(1);
	}
	
	private static void help() {
		System.out.println("--help");
		System.out.println("  to print this message");
		// case "--help":
		// help();
		// break;
		System.out.println("--cs <CHARSET>");
		System.out.println("or");
		System.out.println("--charset <<CHARSET>");
		System.out.println("  to set the charset of the input file");
		// case "--cs":
		// case "--charset":
		// if (args.length <= ++ i) {
		// crash(args, i, "not enugh args for charset option");
		// }
		// cs = Charset.forName(args[i]);
		// break;
		System.out.println("-pa <IN_FILE>");
		System.out.println("or");
		System.out.println("--primasm <IN_FILE>");
		System.out.println("  to set the input file");
		// case "-pa":
		// case "-primasm":
		// if (args.length <= ++ i) {
		// crash(args, i, "not enugh args for primasm option");
		// }
		// inFile = args[i];
		// break;
		System.out.println("-out <IN_FILE>");
		System.out.println("  to set the output file");
		// case "-out":
		// if (args.length <= ++ i) {
		// crash(args, i, "not enugh args for out option");
		// }
		// outFile = args[i];
		// break;
		System.out.println("-sw");
		System.out.println("or");
		System.out.println("-suppress-warns");
		System.out.println("  to suppress warnings");
		// case "-sw":
		// case "-suppress-warns":
		// if (sw) {
		// crash(args, i, "suppress-wars is already set");
		// }
		// sw = true;
		// break;
		System.out.println("-ne");
		System.out.println("or");
		System.out.println("-no-export");
		System.out.println("  to suppress exporting export symbols");
		// case "-ne":
		// case "-no-export":
	}
	
}
