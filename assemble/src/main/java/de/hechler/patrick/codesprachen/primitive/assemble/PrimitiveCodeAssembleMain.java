package de.hechler.patrick.codesprachen.primitive.assemble;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.logging.Logger;

import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.misc.Interval;

import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.pfs.objects.jfs.PFSFileSystemImpl;
import de.hechler.patrick.pfs.utils.JavaPFSConsants;

public class PrimitiveCodeAssembleMain {
	
	public static final Logger LOG = Logger.getLogger("prim-asm");
	
	private static PrimitiveAssembler asm;
	private static Reader             input;
	private static Closeable          c;
	
	public static void main(String[] args) {
		setup(args);
		try {
			asm.assemble(null, input);
			LOG.info("assembled successful");
			if (c != null) { c.close(); }
		} catch (ThreadDeath t) {
			throw t;
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
	
	private static void setup(String[] args) {
		FileSystem fileSys      = null;
		Charset    charset      = null;
		String     inFile       = null;
		String     outFile      = null;
		boolean    suppressWarn = false;
		boolean    noExport     = false;
		boolean    force        = false;
		try {
			for (int i = 0; i < args.length; i++) {
				switch (args[i].toLowerCase()) {
				case "--help", "-h", "-?" -> argHelp();
				case "--cs", "--charset" -> charset = argCharset(args, charset, ++i);
				case "--in", "--input" -> inFile = argInput(args, inFile, ++i);
				case "--out", "--output" -> outFile = argOutput(args, outFile, ++i);
				case "--rfs", "--real-file-system" -> fileSys = argRfs(args, fileSys, i);
				case "--pfs", "--patr-file-system" -> fileSys = argPfs(args, fileSys, ++i);
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
			doSetup(fileSys, charset, inFile, outFile, suppressWarn, noExport, force);
		} catch (Exception e) {
			e.printStackTrace();
			crash(args, -1, e.getClass() + ": " + e.getMessage());
		}
	}
	
	private static void argHelp() {
		help();
		System.exit(0);
	}
	
	private static Charset argCharset(String[] args, Charset charset, int i) {
		if (args.length <= i) { crash(args, i, "not enugh args for charset option"); }
		if (charset != null) { crash(args, i, "charset already set"); }
		charset = Charset.forName(args[i]);
		return charset;
	}
	
	private static String argInput(String[] args, String inFile, int i) {
		if (args.length <= i) { crash(args, i, "not enugh args for input option"); }
		if (inFile != null) { crash(args, i, "input already set"); }
		inFile = args[i];
		return inFile;
	}
	
	private static String argOutput(String[] args, String outFile, int i) {
		if (args.length <= i) { crash(args, i, "not enugh args for out option"); }
		if (outFile != null) { crash(args, i, "out already set"); }
		outFile = args[i];
		return outFile;
	}
	
	private static FileSystem argRfs(String[] args, FileSystem fileSys, int i) {
		if (fileSys != null) { crash(args, i, "file system already set"); }
		fileSys = FileSystems.getDefault();
		return fileSys;
	}
	
	private static FileSystem argPfs(String[] args, FileSystem fileSys, int i) throws URISyntaxException, IOException {
		if (args.length <= i) { crash(args, i, "not enugh args for pfs option"); }
		if (fileSys != null) { crash(args, i, "file system already set"); }
		URI uri = new URI(JavaPFSConsants.URI_SHEME, null, args[i], null, null);
		fileSys = FileSystems.newFileSystem(uri, Collections.emptyMap());
		c = fileSys;
		return fileSys;
	}
	
	private static void doSetup(FileSystem fileSys, Charset charset, String inFile, String outFile,
			boolean suppressWarn, boolean noExport, boolean force) throws IOException, URISyntaxException {
		Path inPath = Paths.get(inFile);
		if (fileSys == null) {
			Path path = inPath.resolveSibling("out.pfs").toAbsolutePath();
			if (force && Files.exists(path)) { Files.delete(path); }
			URI uri = new URI(JavaPFSConsants.URI_SHEME, null, "/" + path.toUri().toString(), null, null);
			fileSys = FileSystems.newFileSystem(uri, Collections.emptyMap());
		}
		Path outPath;
		if (outFile != null) {
			outPath = fileSys.getPath(outFile);
		} else if (fileSys instanceof PFSFileSystemImpl) {
			outPath = fileSys.getPath("a.out");
		} else {
			outPath = fileSys.getPath(inFile).resolveSibling("a.out");
		}
		input = Files.newBufferedReader(inPath, charset);
		OpenOption[] opts          = force
				? new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.WRITE,
						StandardOpenOption.TRUNCATE_EXISTING }
				: new OpenOption[] { StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE };
		OutputStream outFileStream = Files.newOutputStream(outPath, opts);
		if (noExport) {
			asm = new PrimitiveAssembler(outFileStream, null, new Path[] { Paths.get(".") }, suppressWarn, true);
		} else {
			Path   exportPath;
			String outPathName = outPath.getFileName().toString();
			if (outPathName.endsWith(".pmc")) {
				exportPath = outPath.resolveSibling(outPathName.substring(0, outPathName.length() - 3) + "psf");
			} else {
				exportPath = outPath.resolveSibling(outPathName + ".psf");
			}
			OutputStream exportOutStream = Files.newOutputStream(exportPath, opts);
			asm = new PrimitiveAssembler(outFileStream, new PrintStream(exportOutStream, true, charset),
					new Path[] { Paths.get(".") }, suppressWarn, true);
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
						+ "                                         default to /a.out\n" //
						+ "                                         on rfs to {input-name}/../a.out\n" //
						+ "        --rfs, --real-file-system        to use the default file system\n" //
						+ "                                         instead of the patr-file-system\n" //
						+ "        --pfs, --patr-file-system [FILE] to set the patr-file-system file\n" //
						+ "                                         default to ./out.pfs\n" //
						+ "        --suppress-warn, -s              to suppresss warnings\n" //
						+ "        --no-export, -n                  to suppress the generation of the export file\n" //
						+ "        --force, -f                      to overwrite output files if they exist already\n" //
						+ "");
	}
	
}
