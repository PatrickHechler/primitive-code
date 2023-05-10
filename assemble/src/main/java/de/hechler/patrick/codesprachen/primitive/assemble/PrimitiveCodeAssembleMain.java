package de.hechler.patrick.codesprachen.primitive.assemble;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchProviderException;
import java.util.logging.Logger;

import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.misc.Interval;

import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.zeugs.pfs.FSProvider;
import de.hechler.patrick.zeugs.pfs.interfaces.FS;
import de.hechler.patrick.zeugs.pfs.interfaces.FSElement;
import de.hechler.patrick.zeugs.pfs.interfaces.File;
import de.hechler.patrick.zeugs.pfs.interfaces.Folder;
import de.hechler.patrick.zeugs.pfs.interfaces.ReadStream;
import de.hechler.patrick.zeugs.pfs.interfaces.WriteStream;
import de.hechler.patrick.zeugs.pfs.opts.JavaFSOptions;
import de.hechler.patrick.zeugs.pfs.opts.PatrFSOptions;
import de.hechler.patrick.zeugs.pfs.opts.StreamOpenOptions;

@SuppressWarnings("javadoc")
public class PrimitiveCodeAssembleMain {
	
	private static final String SYMBOL_FILE_POSSIX = ".psf";
	
	public static final Logger LOG = Logger.getLogger("prim-asm");
	
	private static final long CREATE_BLOCK_COUNT = 4096L;
	private static final int  CREATE_BLOCK_SIZE  = 1024;
	
	private static PrimitiveAssembler asm;
	private static Reader             input;
	private static FS                 fileSys;
	
	public static void main(String[] args) throws IOException {
		setup(args);
		try {
			asm.assemble(null, input);
			LOG.info("assembled successful");
			if (fileSys != null) { fileSys.close(); }
			JAVA_FS.close();
		} catch (Throwable t) {
			LOG.severe(() -> {
				StringBuilder b = new StringBuilder();
				appendError(t, b);
				return b.toString();
			});
			System.exit(1);
		}
	}
	
	private static void appendError(Throwable t, StringBuilder b) {
		String start = "";
		while (true) {
			String sp2s = start + "  ";
			b.append(start).append(t.getClass()).append('\n');
			b.append(sp2s).append(t.getLocalizedMessage()).append('\n');
			if (t instanceof AssembleRuntimeException are) {
				appendAsmRunExep(b, sp2s, are);
			} else if (t instanceof AssembleError ae) {
				appendAsmErr(b, sp2s, ae);
			} else if (t instanceof InputMismatchException ime) { appedInputMissmatch(b, sp2s, ime); }
			b.append(sp2s).append("stack trace:\n");
			for (StackTraceElement ste : t.getStackTrace()) {
				b.append(sp2s).append("  at ").append(ste).append('\n');
			}
			for (Throwable s : t.getSuppressed()) {
				b.append(sp2s).append("suppressed: ").append(s.getClass()).append(": ").append(s.getLocalizedMessage());
			}
			t = t.getCause();
			if (t == null) { break; }
			b.append(sp2s).append("cause:\n");
			start = sp2s + "  ";
		}
	}
	
	private static void appendAsmRunExep(StringBuilder b, String sp2s, AssembleRuntimeException are) {
		b.append(sp2s).append("line:         ").append(are.line).append('\n');
		b.append(sp2s).append("char in line: ").append(are.posInLine).append('\n');
		b.append(sp2s).append("length:       ").append(are.length).append('\n');
		b.append(sp2s).append("char in file: ").append(are.charPos).append('\n');
	}
	
	private static void appendAsmErr(StringBuilder b, String sp2s, AssembleError ae) {
		b.append(sp2s).append("line:         ").append(ae.line).append('\n');
		b.append(sp2s).append("char in line: ").append(ae.posInLine).append('\n');
		b.append(sp2s).append("length:       ").append(ae.length).append('\n');
		b.append(sp2s).append("char in file: ").append(ae.charPos).append('\n');
	}
	
	private static void appedInputMissmatch(StringBuilder b, String sp2s, InputMismatchException ime) {
		b.append(sp2s).append("rule: ").append(ime.getCtx()).append('\n');
		b.append(sp2s).append("recognizer: ").append(ime.getRecognizer()).append('\n');
		b.append(sp2s).append("offending token: ").append(ime.getOffendingToken()).append('\n');
		b.append(sp2s).append("expected: ").append(ime.getExpectedTokens()).append('\n');
		String[] names;
		if (ime.getRecognizer() instanceof ConstantPoolGrammarParser) {
			names = ConstantPoolGrammarParser.tokenNames;
		} else if (ime.getRecognizer() instanceof PrimitiveFileGrammarParser) {
			names = PrimitiveFileGrammarParser.tokenNames;
		} else {
			names = null;
		}
		if (names != null) {
			b.append(sp2s + "expected: [");
			boolean first = true;
			for (Interval r : ime.getExpectedTokens().getIntervals()) {
				for (int i = r.a; i < r.b; i++) {
					if (!first) { b.append(", "); }
					first = false;
					b.append('<').append((i == -1 ? "EOF" : names[i])).append(">\n");
				}
			}
			b.append("]\n");
		}
	}
	
	private static final FS JAVA_FS;
	
	static {
		try {
			JAVA_FS = FSProvider.ofName(FSProvider.JAVA_FS_PROVIDER_NAME).loadFS(new JavaFSOptions(Paths.get("/")));
			JAVA_FS.cwd(JAVA_FS.folder(Paths.get(".").toAbsolutePath().toString()));
		} catch (IOException | NoSuchProviderException e) {
			throw new InternalError(e);
		}
	}
	
	private static void setup(String[] args) {
		Charset charset      = null;
		String  inFile       = null;
		String  outFile      = null;
		boolean suppressWarn = false;
		boolean noExport     = false;
		boolean force        = false;
		try {
			for (int i = 0; i < args.length; i++) {
				switch (args[i].toLowerCase()) {
				case "--help", "-h", "-?" -> argHelp();
				case "--cs", "--charset" -> charset = argCharset(args, charset, ++i);
				case "--in", "--input" -> inFile = argInput(args, inFile, ++i);
				case "--out", "--output" -> outFile = argOutput(args, outFile, ++i);
				case "--rfs", "--real-file-system" -> argRfs(args, i);
				case "--pfs", "--patr-file-system" -> argPfs(args, ++i, force);
				case "-s", "--suppress-warn" -> suppressWarn = true;
				case "-n", "--no-export" -> noExport = true;
				case "-f", "--force" -> force = true;
				default -> {
					if (args[i].matches("\\-[hns]+")) {
						if (args[i].indexOf('s') != -1) { suppressWarn = true; }
						if (args[i].indexOf('n') != -1) { noExport = true; }
						if (args[i].indexOf('f') != -1) { force = true; }
						if (args[i].indexOf('h') != -1 || args[i].indexOf('?') != -1) { argHelp(); }
					} else {
						crash(args, i, "unknown arg: " + args[i]);
					}
				}
				}
			}
			if (inFile == null) { crash(args, -1, "no input file set (use --input)"); }
			charset = charset != null ? charset : StandardCharsets.UTF_8;
			doSetup(args, charset, inFile, outFile, suppressWarn, noExport, force);
		} catch (Exception e) {
			e.printStackTrace();
			crash(args, -1, e.getClass() + ": " + e.getMessage());
		}
	}
	
	private static void doSetup(String[] args, Charset charset, String inFile, String outFile,
			boolean suppressWarn, boolean noExport, boolean force) throws IOException {
		@SuppressWarnings("resource")
		FS outFS = PrimitiveCodeAssembleMain.fileSys == null ? JAVA_FS : PrimitiveCodeAssembleMain.fileSys;
		try (File in = JAVA_FS.file(inFile)) {
			WriteStream out;
			WriteStream expOut;
			if (outFile == null) {
				String outName = pmfName(in.name());
				try (Folder parent = in.parent()) {
					try {
						FSElement oldOut = parent.childElement(outName);
						if (force) {
							oldOut.delete();
						} else {
							crash(args, -1, "out file already exists use --force to overwrite");
						}
					} catch (@SuppressWarnings("unused") NoSuchFileException e) { /* ignore */ }
					try (File of = parent.createFile(outFile)) {
						out = of.openWrite();
					}
					if (noExport) {
						expOut = null;
					} else {
						try {
							FSElement oldExpOut = parent.childElement(outName + SYMBOL_FILE_POSSIX);
							if (force) {
								oldExpOut.delete();
							} else {
								crash(args, -1, "out file already exists use --force to overwrite");
							}
						} catch (@SuppressWarnings("unused") NoSuchFileException e) { /* ignore */ }
						try (File eof = parent.createFile(outFile + SYMBOL_FILE_POSSIX)) {
							expOut = eof.openWrite();
						}
					}
				}
			} else {
				StreamOpenOptions opts = new StreamOpenOptions(false, true);
				out = (WriteStream) outFS.stream(outFile, opts);
				if (noExport) {
					expOut = null;
				} else {
					expOut = (WriteStream) outFS.stream(outFile + SYMBOL_FILE_POSSIX, opts);
				}
			}
			initAsm(charset, in.openRead(), out, expOut, suppressWarn);
		}
	}
	
	private static String pmfName(String name) {
		if (name.endsWith(".psc")) {
			return name.substring(0, name.length() - 4);
		} else {
			return name + ".pmf";
		}
	}
	
	private static void argHelp() {
		help();
		System.exit(0);
	}
	
	private static Charset argCharset(String[] args, Charset charset, int i) {
		if (args.length <= i) { crash(args, i, "not enugh args for charset option"); }
		if (charset != null) { crash(args, i, "charset already set"); }
		return Charset.forName(args[i]);
	}
	
	private static String argInput(String[] args, String inFile, int i) {
		if (args.length <= i) { crash(args, i, "not enugh args for input option"); }
		if (inFile != null) { crash(args, i, "input already set"); }
		return args[i];
	}
	
	private static String argOutput(String[] args, String outFile, int i) {
		if (args.length <= i) { crash(args, i, "not enugh args for out option"); }
		if (outFile != null) { crash(args, i, "out already set"); }
		return args[i];
	}
	
	private static void argRfs(String[] args, int i) {
		if (fileSys != null) { crash(args, i, "file system already set"); }
		fileSys = null;
	}
	
	private static void argPfs(String[] args, int i, boolean force) throws IOException {
		if (args.length <= i) { crash(args, i, "not enugh args for pfs option"); }
		if (fileSys != null) { crash(args, i, "file system already set"); }
		FSProvider patrProv = null;
		try {
			patrProv = FSProvider.ofName(FSProvider.PATR_FS_PROVIDER_NAME);
			fileSys = patrProv.loadFS(new PatrFSOptions(args[i]));
		} catch (@SuppressWarnings("unused") IOException e) {
			if (force) {
				fileSys = patrProv.loadFS(new PatrFSOptions(args[i], true, CREATE_BLOCK_COUNT, CREATE_BLOCK_SIZE));
			}
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			LOG.severe(() -> "error: " + e);
			System.exit(1);
			throw new AssertionError();
		}
	}
	
	private static void initAsm(Charset charset, ReadStream in, WriteStream out, WriteStream exportOut,
			boolean suppressWarn) throws IOException {
		input = new InputStreamReader(new BufferedInputStream(in.asInputStream()));
		try (OutputStream outFileStream = new BufferedOutputStream(out.asOutputStream())) {
			if (exportOut == null) {
				asm = new PrimitiveAssembler(outFileStream, null, new Path[] { Paths.get(".") }, suppressWarn, true);
			} else {
				try (OutputStream exportOutStream = new BufferedOutputStream(exportOut.asOutputStream())) {
					asm = new PrimitiveAssembler(outFileStream, new PrintStream(exportOutStream, true, charset),
							new Path[] { Paths.get(".") }, suppressWarn, true);
				}
			}
		}
	}
	
	private static void crash(String[] args, int index, String msg) {
		LOG.severe(() -> {
			StringBuilder b = new StringBuilder();
			b.append(msg).append('\n');
			for (int i = 0; i < args.length; i++) {
				if (i == index) { b.append("error happanded here -> "); }
				b.append('[').append(i).append("]='").append(args[i]).append("'\n");
			}
			return b.toString();
		});
		System.exit(1);
	}
	
	private static void help() {
		LOG.info( //
				/*     */ "primitive-assembler help:\n" //
						+ "    usage: prim-asm [OPTIONS]\n" //
						+ "    \n" //
						+ "    Options:\n" //
						+ "        --help, -h, -?                   to print this message and exit\n" //
						+ "        --charset, --cs [CHARSET]        to set the charset for input and output files\n" //
						+ "                                         the default charset is 'UTF-8'\n" //
						+ "        --in, --input [FILE]             to set the input file\n" //
						+ "                                         this option is non optional\n" //
						+ "        --out, --output [FILE]           to set the output file\n" //
						+ "                                         the default depends on the input\n" //
						+ "        --rfs, --real-file-system        to use the linux file system\n" //
						+ "        --pfs, --patr-file-system [FILE] to set the patr-file-system file\n" //
						+ "        --suppress-warn, -s              to suppresss warnings\n" //
						+ "        --no-export, -n                  to suppress the generation of the export file\n" //
						+ "        --force, -f                      to overwrite output files if they exist already\n" //
						+ "");
	}
	
}
