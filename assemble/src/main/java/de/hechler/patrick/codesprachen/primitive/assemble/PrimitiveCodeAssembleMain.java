package de.hechler.patrick.codesprachen.primitive.assemble;

import java.io.Closeable;
import java.io.IOException;
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

import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
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
		} catch (IOException e) {
			e.printStackTrace();
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
		boolean noMkdir = false;
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
				case "--no-mkdir":
					noMkdir = true;
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
			if (outFile == null) {
				int li = inFile.lastIndexOf('.');
				li = li == -1 ? inFile.length() : li;
				outFile = inFile.substring(0, li) + ".pmc";
			}
			if (cs == null) {
				cs = "UTF-8";
			}
			input = Files.newBufferedReader(Paths.get(inFile), Charset.forName(cs));
			OpenOption[] opts = force
				? new OpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.TRUNCATE_EXISTING }
				: new OpenOption[] {StandardOpenOption.CREATE_NEW, StandardOpenOption.APPEND };
			if (fs == null) {
				fs = FileSystems.newFileSystem(new URI(JavaPFSConsants.URI_SHEME, null, Paths.get("out.pfs").toAbsolutePath().toString(), null, null), Collections.emptyMap());
			}
			if ( !noMkdir) {
				Path bin = fs.getPath("./bin/");
				if ( !Files.exists(bin)) {
					Files.createDirectory(bin);
				}
			}
			OutputStream outFileStream = Files.newOutputStream(fs.getPath("bin", outFile), opts);
			if (ne) {
				asm = new PrimitiveAssembler(outFileStream, null, new Path[] {Paths.get(".") }, sw, true);
			} else {
				String exportFile;
				if (outFile.endsWith(".pmc")) {
					exportFile = outFile.substring(0, outFile.length() - 3) + "psf";
				} else {
					exportFile = outFile + ".psf";
				}
				OutputStream exportOutStream = Files.newOutputStream(fs.getPath("bin", exportFile), opts);
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
		System.out.print(
			"primitive-assembler help:\n"
				+ "    usage: prim-asm [OPTIONS]\n"
				+ "    \n"
				+ "    Options:\n"
				+ "        --help, -h, -?                      to print this message and exit\n"
				+ "        --charset, --cs [CHARSET]           to set the charset for input and output files\n"
				+ "                                            the default charset is 'UTF-8'\n"
				+ "        --in, --input [FILE]                to set the input file\n"
				+ "                                            this option is non optional\n"
				+ "        --out, --output [FILE]              to set the output file\n"
				+ "                                            default to /bin/{input-name}\n"
				+ "                                            or rfs: ./bin/{input-name}\n"
				+ "        --rfs                               to use the default file system\n"
				+ "                                            instead of the patr-file-system\n"
				+ "        --pfs, --patr-file-system [FILE]    to set the patr-file-system file\n"
				+ "                                            default to ./out.pfs\n"
				+ "        --suppress-warn, -s                 to suppresss warnings\n"
				+ "        --no-export, -n                     to suppress the generation of the export file\n"
				+ "        --force, -f                         to overwrite output files if they exist already\n"
				+ "        --no-mkdir                          fail if /bin/ (or ./bin/ on rfs) does not already exist\n"
				+ ""
				+ "");
		// case "-f":
		// case "--force":
		// ne = true;
		// break;
		// case "--no-mkdirs":
		// noMkdirs = true;
		// break;
		// default:
		// if (args[i].matches("\\-[hns]+")) {
		// if (args[i].indexOf('s') != -1) {
		// sw = true;
		// }
		// if (args[i].indexOf('n') != -1) {
		// ne = true;
		// }
		// if (args[i].indexOf('f') != -1) {
		// force = true;
		// }
		// if (args[i].indexOf('h') != -1 || args[i].indexOf('?') != -1) {
		// help();
		// System.exit(0);
		// }
		// } else {
		// crash(args, i, "unknown arg: " + args[i]);
		// }
		// }
	}
	
}
