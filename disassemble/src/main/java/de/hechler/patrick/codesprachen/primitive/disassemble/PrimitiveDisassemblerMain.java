package de.hechler.patrick.codesprachen.primitive.disassemble;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchProviderException;
import java.util.logging.Logger;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.DisasmMode;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.PrimitiveDisassembler;
import de.hechler.patrick.zeugs.pfs.FSProvider;
import de.hechler.patrick.zeugs.pfs.interfaces.FS;
import de.hechler.patrick.zeugs.pfs.interfaces.ReadStream;
import de.hechler.patrick.zeugs.pfs.opts.JavaFSOptions;
import de.hechler.patrick.zeugs.pfs.opts.PatrFSOptions;
import de.hechler.patrick.zeugs.pfs.opts.StreamOpenOptions;

public class PrimitiveDisassemblerMain {
	
	private static final String NOT_ENUGH_ARGS = "not enugh args";
	
	public static final Logger LOG = Logger.getLogger("prim-disasm");
	
	private static PrimitiveDisassembler disasm;
	private static InputStream           binary;
	private static long                  pos;
	private static FS                    fs;
	
	public static void main(String[] args) {
		setup(args);
		int exitCode = 0;
		try {
			disasm.deassemble(pos, binary);
		} catch (IOException e) {
			LOG.severe("an error occured during the diassinble of the binary:");
			e.printStackTrace();
			exitCode = 2;
		}
		try {
			binary.close();
		} catch (IOException e) {
			LOG.warning("an error occured during the cloye operation of the binary:");
			e.printStackTrace();
			if (exitCode == 2) {
				exitCode = 3;
			} else {
				exitCode = 1;
			}
		}
		try {
			fs.close();
		} catch (IOException e) {
			LOG.warning("an error occured during the cloye operation of the file system:");
			e.printStackTrace();
			if (exitCode == 2) {
				exitCode = 3;
			} else {
				exitCode = 1;
			}
		}
		System.exit(exitCode);
	}
	
	private static void setup(String[] args) {
		fs = null;
		disasm = null;
		binary = null;
		pos = -1L;
		String     in    = null;
		String     out   = null;
		DisasmMode dmode = null;
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "--help" -> argHelp();
			case "--pos" -> i = argPos(args, ++i);
			case "--in", "--bin", "--pmf" -> in = argBin(args, in, ++i);
			case "-a", "--analyse" -> dmode = argAnalyze(args, dmode, i);
			case "-e", "--executable" -> dmode = argExecutable(args, dmode, i);
			case "--rfs" -> argRFS(args, i);
			case "--pfs" -> argPFS(args, ++i);
			case "--out" -> out = argOut(args, out, ++i);
			default -> crash(i, args, "unknown argument: '" + args[i] + "'");
			}
		}
		if (fs == null) { crash(-1, args, "no file system spezified!"); }
		if (binary == null) { crash(-1, args, "no binary/input set"); }
		pos = pos == -1L ? 0L : pos;
		dmode = dmode == null ? DisasmMode.executable : dmode;
		try {
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(out));
			disasm = new PrimitiveDisassembler(dmode, writer);
		} catch (IOException e) {
			crash(-1, args, "error on opening outputstream for '" + out + "': " + e.getClass().getSimpleName() + ": "
					+ e.getMessage());
		}
		try {
			binary = ((ReadStream) fs.stream(in, new StreamOpenOptions(true, false))).asInputStream();
		} catch (IOException e) {
			crash(-1, args, "error on opening inputstream for '" + in + "': " + e.getClass().getSimpleName() + ": "
					+ e.getMessage());
		}
	}
	
	private static String argOut(String[] args, String out, int i) {
		if (out != null) { crash(i - 1, args, "output is already set"); }
		if (args.length <= i) { crash(i, args, NOT_ENUGH_ARGS); }
		out = args[i];
		return out;
	}
	
	private static void argPFS(String[] args, int i) {
		if (fs != null) { crash(i, args, "file-system is already set"); }
		if (args.length <= i) { crash(i - 1, args, NOT_ENUGH_ARGS); }
		try {
			FSProvider patrProv = FSProvider.ofName(FSProvider.PATR_FS_PROVIDER_NAME);
			fs = patrProv.loadFS(new PatrFSOptions(args[i]));
		} catch (NoSuchProviderException | IOException e) {
			crash(i, args, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
	
	private static void argRFS(String[] args, int i) {
		if (fs != null) { crash(i, args, "file-system is already set"); }
		try {
			fs = FSProvider.ofName(FSProvider.JAVA_FS_PROVIDER_NAME).loadFS(new JavaFSOptions(Paths.get("/")));
		} catch (NoSuchProviderException | IOException e) {
			crash(i, args, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
	
	private static DisasmMode argExecutable(String[] args, DisasmMode dmode, int i) {
		if (dmode != null) { crash(i, args, "disasm-mode is already set"); }
		dmode = DisasmMode.executable;
		return dmode;
	}
	
	private static DisasmMode argAnalyze(String[] args, DisasmMode dmode, int i) {
		if (dmode != null) { crash(i, args, "disasm-mode is already set"); }
		dmode = DisasmMode.analysable;
		return dmode;
	}
	
	private static String argBin(String[] args, String in, int i) {
		if (in != null) { crash(i - 1, args, "input is already set"); }
		if (args.length <= i) { crash(i, args, NOT_ENUGH_ARGS); }
		in = args[i];
		return in;
	}
	
	private static int argPos(String[] args, int i) {
		if (pos != -1L) { crash(i - 1, args, "pos is already set"); }
		if (args.length <= i) { crash(i, args, NOT_ENUGH_ARGS); }
		if (args[i].startsWith("0x")) {
			pos = Long.parseUnsignedLong(args[i].substring(2), 16);
		} else if (args[i].startsWith("HEX-")) {
			pos = Long.parseUnsignedLong(args[i].substring(4), 16);
		} else {
			pos = Long.parseUnsignedLong(args[i]);
		}
		return i;
	}
	
	private static void argHelp() {
		help();
		System.exit(0);
	}
	
	private static void help() {
		System.out.print( //
				/*	    */"Usage: prim-asm [OPTIONS]\n" //
						+ "    --help\n" //
						+ "        to print this message and exit\n" //
						+ "    --pos <POS>\n" //
						+ "        to set the beginning position of the binary data\n" //
						+ "        if the POS starts with '0x' or 'HEX-' it is read in a\n" //
						+ "        hexadecimal format, if not the decimal format is used\n" //
						+ "    --in <BINARY>\n" //
						+ "    --bin <BINARY>\n" //
						+ "    --pmf <BINARY>\n" //
						+ "        to set the input-machine-file\n" //
						+ "    --out <OUTPUT>\n" //
						+ "        to set the output-file\n" //
						+ "    -a\n" //
						+ "    --analyse\n" //
						+ "        to set the analyse mode\n" //
						+ "        then the output data can not be assembled\n" //
						+ "        without the need further changes\n" //
						+ "        but more information is printed\n" //
						+ "    -e\n" //
						+ "    --executable\n" //
						+ "        to set the executable mode (default)\n" //
						+ "        then the output data can be assembled\n" //
						+ "        without the need further change\n" //
						+ "    --rfs\n" //
						+ "        to use the 'real' file-system\n" //
						+ "    --pfs <PFS_PATH>\n" //
						+ "        to specify where the patr-file-system file is\n" //
		);
	}
	
	private static void crash(int index, String[] args, String msg) {
		LOG.severe(msg);
		for (int nfi = 0; nfi < args.length; nfi++) {
			final int i = nfi;
			if (i == index) {
				LOG.severe(() -> "error happaned here -> args[" + i + "]: " + args[i]);
			} else {
				LOG.severe(() -> "args[" + i + "]: " + args[i]);
			}
		}
		System.exit(1);
	}
	
}
