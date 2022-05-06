package de.hechler.patrick.codesprachen.primitive.disassemble;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.DisasmMode;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.PrimitiveDisassembler;

public class PrimitiveDisassemblerMain {
	
	private static PrimitiveDisassembler disasm;
	private static InputStream binary;
	private static long pos;
	
	public static void main(String[] args) {
		setup(args);
		try {
			disasm.deassemble(pos, binary);
		} catch (IOException e) {
			System.err.println("an error occured during the deassinblation:");
			e.printStackTrace();
		}
	}
	
	private static void setup(String[] args) {
		disasm = null;
		binary = null;
		pos = -1L;
		PrintStream out = null;
		DisasmMode dmode = null;
		for (int i = 0; i < args.length; i ++ ) {
			switch (args[i]) {
			case "--help":
				help();
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
				if (binary != null) {
					crash(i, args, "input is already set");
				}
				if (args.length <= ++ i) {
					crash(i, args, "not enugh args");
				}
				try {
					binary = new FileInputStream(args[i]);
				} catch (FileNotFoundException e) {
					crash(i, args, e.toString());
				}
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
			case "--out":
				if (out != null) {
					crash(i, args, "output is already set");
				}
				if (args.length <= ++ i) {
					crash(i, args, "not enugh args");
				}
				try {
					out = new PrintStream(args[i]);
				} catch (FileNotFoundException e) {
					crash(i, args, e.toString());
				}
				break;
			default:
				crash(i, args, "unknown argument: '" + args[i] + "'");
			}
		}
		if (binary == null) {
			crash( -1, args, "no binary/input set");
		}
		pos = pos == -1L ? 0L : pos;
		out = out == null ? System.out : out;
		dmode = dmode == null ? DisasmMode.executable : dmode;
		disasm = new PrimitiveDisassembler(dmode, out);
	}
	
	private static void help() {
		System.out.println("--help");
		System.out.println("  to print this message");
		System.out.println("--pos <POS>");
		System.out.println("  to set the beginning position of the binary data");
		System.out.println("  if the POS starts with '0x' it is read in a hex format, if not decimal");
		System.out.println("--in <BINARY>");
		System.out.println("--bin <BINARY>");
		System.out.println("  to set the binary-file");
		System.out.println("--out <OUTPUT>");
		System.out.println("  to set the output-file");
		System.out.println("-a");
		System.out.println("--analyse");
		System.out.println("  to set the analyse mode");
		System.out.println("    then the output data can not be assembled");
		System.out.println("    without the need further changes");
		System.out.println("    but more information is printed");
		System.out.println("-e");
		System.out.println("--executable");
		System.out.println("  to set the executable mode (default)");
		System.out.println("    then the output data can be assembled");
		System.out.println("    without the need further change");
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
