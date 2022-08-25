package de.hechler.patrick.codesprachen.primitive.disassemble;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.DisasmMode;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.PrimitiveDisassembler;
import de.hechler.patrick.pfs.interfaces.BlockAccessor;
import de.hechler.patrick.pfs.interfaces.PatrFileSystem;
import de.hechler.patrick.pfs.objects.ba.SeekablePathBlockAccessor;
import de.hechler.patrick.pfs.objects.fs.PatrFileSysImpl;
import de.hechler.patrick.pfs.utils.JavaPFSConsants;

public class PrimitiveDisassemblerMain {
	
	private static PrimitiveDisassembler disasm;
	private static InputStream           binary;
	private static long                  pos;
	private static FileSystem            fs;
	
	public static void main(String[] args) {
		setup(args);
		int exitCode = 0;
		try {
			disasm.deassemble(pos, binary);
		} catch (IOException e) {
			System.err.println("an error occured during the diassinble of the binary:");
			e.printStackTrace();
			exitCode = 2;
		}
		if (fs != FileSystems.getDefault()) {
			try {
				fs.close();
			} catch (IOException e) {
				System.err.println("an error occured during the cloye operation of the file system:");
				e.printStackTrace();
				if (exitCode == 2) {
					exitCode = 3;
				} else {
					exitCode = 1;
				}
			}
		}
		System.exit(exitCode);
	}
	
	private static void setup(String[] args) {
		fs = null;
		disasm = null;
		binary = null;
		pos = -1L;
		String in = null;
		String out = null;
		DisasmMode dmode = null;
		for (int i = 0; i < args.length; i ++ ) {
			switch (args[i]) {
			case "--help":
				help();
				System.exit(0);
				break;
			case "--pos":
				if (pos != -1L) {
					crash(i, args, "pos is already set");
				}
				if (args.length <= ++ i) {
					crash(i, args, "not enugh args");
				}
				if (args[i].startsWith("0x")) {
					pos = Long.parseUnsignedLong(args[i].substring(2), 16);
				} else if (args[i].startsWith("HEX-")) {
					pos = Long.parseUnsignedLong(args[i].substring(4), 16);
				} else {
					pos = Long.parseUnsignedLong(args[i]);
				}
				break;
			case "--in":
			case "--bin":
				if (in != null) {
					crash(i, args, "input is already set");
				}
				if (args.length <= ++ i) {
					crash(i, args, "not enugh args");
				}
				in = args[i];
				break;
			case "-a":
			case "--analyse":
				if (dmode != null) {
					crash(i, args, "disasm-mode is already set");
				}
				dmode = DisasmMode.analysable;
				break;
			case "-e":
			case "--executable":
				if (dmode != null) {
					crash(i, args, "disasm-mode is already set");
				}
				dmode = DisasmMode.executable;
				break;
			case "--rfs":
				if (fs != null) {
					crash(i, args, "file-system is already set");
				}
				fs = FileSystems.getDefault();
				break;
			case "--pfs": {
				if (fs != null) {
					crash(i, args, "file-system is already set");
				}
				if (args.length <= ++ i) {
					crash(i, args, "not enugh args");
				}
				try {
					URI uri = new URI(JavaPFSConsants.URI_SHEME, null, args[i], null);
					Map <String, Object> map = new HashMap <>();
					Path path = args[i].indexOf(':') == -1 ? Paths.get(args[i]) : Paths.get(URI.create(args[i]));
					BlockAccessor ba = SeekablePathBlockAccessor.create(path, -1, true, null);
					PatrFileSystem pfs = new PatrFileSysImpl(ba, true);
					map.put(JavaPFSConsants.NEW_FILE_SYS_ENV_ATTR_FILE_SYS, pfs);
					fs = FileSystems.newFileSystem(uri, map);
				} catch (IOException | URISyntaxException e) {
					crash(i, args, e.getClass().getSimpleName() + ": " + e.getMessage());
				}
				break;
			}
			case "--out":
				if (out != null) {
					crash(i, args, "output is already set");
				}
				if (args.length <= ++ i) {
					crash(i, args, "not enugh args");
				}
				out = args[i];
				break;
			default:
				crash(i, args, "unknown argument: '" + args[i] + "'");
			}
		}
		if (fs == null) {
			crash( -1, args, "no file system spezified!");
		}
		if (binary == null) {
			crash( -1, args, "no binary/input set");
		}
		pos = pos == -1L ? 0L : pos;
		dmode = dmode == null ? DisasmMode.executable : dmode;
		try {
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(out));
			disasm = new PrimitiveDisassembler(dmode, writer);
		} catch (IOException e) {
			crash( -1, args, "error on opening outputstream for '" + out + "': " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		try {
			binary = Files.newInputStream(fs.getPath(in));
		} catch (IOException e) {
			crash( -1, args, "error on opening inputstream for '" + in + "': " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}
	
	private static void help() {
		System.out.print(
			"Usage: prim-asm [OPTIONS]\n"
				+ "    --help\n"
				+ "        to print this message and exit\n"
				+ "    --pos <POS>\n"
				+ "        to set the beginning position of the binary data\n"
				+ "        if the POS starts with '0x' or 'HEX-' it is read in a\n"
				+ "        hexadecimal format, if not the decimal format is used\n"
				+ "    --in <BINARY>\n"
				+ "    --bin <BINARY>\n"
				+ "        to set the input-binary-file\n"
				+ "    --out <OUTPUT>\n"
				+ "        to set the output-file\n"
				+ "    -a\n"
				+ "    --analyse\n"
				+ "        to set the analyse mode\n"
				+ "        then the output data can not be assembled\n"
				+ "        without the need further changes\n"
				+ "        but more information is printed\n"
				+ "    -e\n"
				+ "    --executable\n"
				+ "        to set the executable mode (default)\n"
				+ "        then the output data can be assembled\n"
				+ "        without the need further change\n"
				+ "    --rfs\n"
				+ "        to use the 'real' file-system\n"
				+ "    --pfs <PFS_PATH>\n"
				+ "        to specify where the patr-file-system file is\n");
	}
	
	private static void crash(int index, String[] args, String msg) {
		System.err.println(msg);
		for (int i = 0; i < args.length; i ++ ) {
			if (i == index) {
				System.out.print("error happaned here -> ");
			}
			System.out.println("args[" + i + "]: " + args[i]);
		}
		System.exit(1);
	}
	
}
