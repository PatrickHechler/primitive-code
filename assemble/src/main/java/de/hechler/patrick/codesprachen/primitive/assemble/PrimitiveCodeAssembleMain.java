package de.hechler.patrick.codesprachen.primitive.assemble;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.misc.Interval;

import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.pfs.objects.jfs.PFSFileSystemImpl;
import de.hechler.patrick.pfs.utils.JavaPFSConsants;

public class PrimitiveCodeAssembleMain {
	
	private static PrimitiveAssembler asm;
	private static Reader             input;
	private static Closeable          c;
	
	public static void main(String[] args) {
		setup(args);
		try {
			asm.assemble(null, input);
			System.out.println("assembled successful");
			if (c != null) {
				c.close();
			}
		} catch (ThreadDeath t) {
			throw t;
		} catch (Throwable t) {
			String start = "";
			while (true) {
				String sp2s = start + "  ";
				System.err.println(start + t.getClass());
				System.err.println(sp2s + t.getLocalizedMessage());
				if (t instanceof AssembleRuntimeException) {
					AssembleRuntimeException are = (AssembleRuntimeException) t;
					System.err.println(sp2s + "line:         " + are.line);
					System.err.println(sp2s + "char in line: " + are.posInLine);
					System.err.println(sp2s + "length:       " + are.length);
					System.err.println(sp2s + "char in file: " + are.charPos);
				} else if (t instanceof AssembleError) {
					AssembleError ae = (AssembleError) t;
					System.err.println(sp2s + "line:         " + ae.line);
					System.err.println(sp2s + "char in line: " + ae.posInLine);
					System.err.println(sp2s + "length:       " + ae.length);
					System.err.println(sp2s + "char in file: " + ae.charPos);
				} else if (t instanceof InputMismatchException) {
					InputMismatchException ime = (InputMismatchException) t;
					System.err.println(sp2s + "rule: " + ime.getCtx());
					System.err.println(sp2s + "recognizer: " + ime.getRecognizer());
					System.err.println(sp2s + "offending token: " + ime.getOffendingToken());
					System.err.println(sp2s + "expected: " + ime.getExpectedTokens());
					String[] names;
					if (ime.getRecognizer() instanceof ConstantPoolGrammarParser) {
						names = ConstantPoolGrammarParser.tokenNames;
					} else if (ime.getRecognizer() instanceof PrimitiveFileGrammarParser) {
						names = PrimitiveFileGrammarParser.tokenNames;
					} else {
						names = null;
					}
					if (names != null) {
						System.err.print(sp2s + "expected: [");
						boolean first = true;
						for (Interval r : ime.getExpectedTokens().getIntervals()) {
							for (int i = r.a; i < r.b; i ++ ) {
								if ( !first) {
									System.err.print(", ");
								}
								first = false;
								System.err.println("<" + (i == -1 ? "EOF" : names[i]) + ">");
							}
						}
						System.err.println("]");
					}
				}
				System.err.println(sp2s + "stack trace:");
				for (StackTraceElement ste : t.getStackTrace()) {
					System.err.println(sp2s + "  at " + ste);
				}
				for (Throwable s : t.getSuppressed()) {
					System.err.print(sp2s + "suppressed: " + s.getClass() + ": " + s.getLocalizedMessage());
				}
				t = t.getCause();
				if (t == null) {
					break;
				}
				System.err.println(sp2s + "cause:");
				start = sp2s + "  ";
			}
			System.exit(1);
		}
	}
	
	@SuppressWarnings("resource")
	private static void setup(String[] args) {
		FileSystem fs = null;
		String cs = null;
		String inFile = null;
		String outFile = null;
		boolean sw = false;
		boolean ne = false;
		boolean force = false;
		try {
			for (int i = 0; i < args.length; i ++ ) {
				switch (args[i].toLowerCase()) {
				case "--help":
				case "-h":
				case "-?":
					help();
					System.exit(0);
					break;
				case "--cs":
				case "--charset":
					if (args.length <= ++ i) {
						crash(args, i, "not enugh args for charset option");
					}
					if (cs != null) {
						crash(args, i, "charset already set");
					}
					cs = args[i];
					break;
				case "--in":
				case "--input":
					if (args.length <= ++ i) {
						crash(args, i, "not enugh args for input option");
					}
					if (inFile != null) {
						crash(args, i, "input already set");
					}
					inFile = args[i];
					break;
				case "--out":
				case "--output":
					if (args.length <= ++ i) {
						crash(args, i, "not enugh args for out option");
					}
					if (outFile != null) {
						crash(args, i, "out already set");
					}
					outFile = args[i];
					break;
				case "--rfs":
				case "--real-file-system":
					if (fs != null) {
						crash(args, i, "file system already set");
					}
					fs = FileSystems.getDefault();
					break;
				case "--pfs":
				case "--patr-file-system": {
					if (args.length <= ++ i) {
						crash(args, i, "not enugh args for pfs option");
					}
					if (fs != null) {
						crash(args, i, "file system already set");
					}
					URI uri = new URI(JavaPFSConsants.URI_SHEME, null, args[i], null, null);
					c = FileSystems.newFileSystem(uri, Collections.emptyMap());
					fs = (FileSystem) c;
					break;
				}
				case "-s":
				case "--suppress-warn":
					sw = true;
					break;
				case "-n":
				case "--no-export":
					ne = true;
					break;
				case "-f":
				case "--force":
					force = true;
					break;
				default:
					if (args[i].matches("\\-[hns]+")) {
						if (args[i].indexOf('s') != -1) {
							sw = true;
						}
						if (args[i].indexOf('n') != -1) {
							ne = true;
						}
						if (args[i].indexOf('f') != -1) {
							force = true;
						}
						if (args[i].indexOf('h') != -1 || args[i].indexOf('?') != -1) {
							help();
							System.exit(0);
						}
					} else {
						crash(args, i, "unknown arg: " + args[i]);
					}
				}
			}
			if (inFile == null) {
				crash(args, -1, "no input file set (use --input)");
			}
			Path inPath = Paths.get(inFile);
			if (fs == null) {
				Path path = inPath.resolveSibling("out.pfs").toAbsolutePath();
				if (force && Files.exists(path)) {
					Files.delete(path);
				}
				URI uri = new URI(JavaPFSConsants.URI_SHEME, null, "/" + path.toUri().toString(), null, null);
				fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
			}
			Path outPath;
			if (outFile != null) {
				outPath = fs.getPath(outFile);
			} else {
				int li = inFile.lastIndexOf('.');
				li = li == -1 ? inFile.length() : li;
				if (fs instanceof PFSFileSystemImpl) {
					outPath = fs.getPath("a.out");
				} else {
					outPath = fs.getPath(inFile).resolveSibling("a.out");
				}
			}
			if (cs == null) {
				cs = "UTF-8";
			}
			input = Files.newBufferedReader(inPath, Charset.forName(cs));
			OpenOption[] opts = force ? new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING }
				: new OpenOption[] {StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE };
			OutputStream outFileStream = Files.newOutputStream(outPath, opts);
			if (ne) {
				asm = new PrimitiveAssembler(outFileStream, null, new Path[] {Paths.get(".") }, sw, true);
			} else {
				Path exportPath;
				String outPathName = outPath.getFileName().toString();
				if (outPathName.endsWith(".pmc")) {
					exportPath = outPath.resolveSibling(outPathName.substring(0, outPathName.length() - 3) + "psf");
				} else {
					exportPath = outPath.resolveSibling(outPathName + ".psf");
				}
				OutputStream exportOutStream = Files.newOutputStream(exportPath, opts);
				asm = new PrimitiveAssembler(outFileStream, new PrintStream(exportOutStream, true, cs), new Path[] {Paths.get(".") }, sw, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			crash(args, -1, e.getClass() + ": " + e.getMessage());
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
		System.out.print(
			/* */ "primitive-assembler help:\n"
				+ "    usage: prim-asm [OPTIONS]\n"
				+ "    \n"
				+ "    Options:\n"
				+ "        --help, -h, -?                      to print this message and exit\n"
				+ "        --charset, --cs [CHARSET]           to set the charset for input and output files\n"
				+ "                                            the default charset is 'UTF-8'\n"
				+ "        --in, --input [FILE]                to set the input file\n"
				+ "                                            this option is non optional\n"
				+ "        --out, --output [FILE]              to set the output file\n"
				+ "                                            default to /a.out\n"
				+ "                                            on rfs to {input-name}/../a.out\n"
				+ "        --rfs, --real-file-system           to use the default file system\n"
				+ "                                            instead of the patr-file-system\n"
				+ "        --pfs, --patr-file-system [FILE]    to set the patr-file-system file\n"
				+ "                                            default to ./out.pfs\n"
				+ "        --suppress-warn, -s                 to suppresss warnings\n"
				+ "        --no-export, -n                     to suppress the generation of the export file\n"
				+ "        --force, -f                         to overwrite output files if they exist already\n"
				+ "");
	}
	
}
