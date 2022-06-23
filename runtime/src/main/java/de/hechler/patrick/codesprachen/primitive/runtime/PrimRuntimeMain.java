package de.hechler.patrick.codesprachen.primitive.runtime;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.PVM;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMImpl;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMUnsafeImpl;
import de.hechler.patrick.pfs.objects.ba.SeekablePathBlockAccessor;
import de.hechler.patrick.pfs.objects.fs.PatrFileSysImpl;

public class PrimRuntimeMain {
	
	private static PVM pvm;
	
	public static void main(String[] args) {
		setup(args);
		pvm.run();
	}
	
	private static void setup(String[] args) {
		pvm = null;
		long stack = -1L;
		PatrFileSysImpl pfs = null;
		String pmc = null;
		int i;
		boolean unsafe = false,
			safe = false;
		for (i = 0; i < args.length && pmc == null; i ++ ) {
			switch (args[i]) {
			case "--help":
				help();
				System.exit(0);
			case "--pfs":
				if ( ++ i >= args.length) {
					crash(i - 1, args, "not enugh arguments");
				}
				pfs = pfs(args, pfs, i, args[i]);
				break;
			case "--pmc":
				if ( ++ i >= args.length) {
					crash(i - 1, args, "not enugh arguments");
				}
				pmc = args[i];
				break;
			case "--unsafe":
				if (safe || unsafe) {
					crash(i, args, "safe|unsafe already set!");
				}
				unsafe = true;
				break;
			case "--safe":
				if (safe || unsafe) {
					crash(i, args, "safe|unsafe already set!");
				}
				safe = true;
				break;
			default:
				if (args[i].startsWith("--pfs=")) {
					String p = args[i].substring("--pfs=".length());
					pfs = pfs(args, pfs, i, p);
				} else if (args[i].startsWith("--pmc=")) {
					pmc = args[i].substring("--pmc=".length());
				} else if (args[i].startsWith("--stack=")) {
					if (stack != -1L) {
						crash(i, args, "stack-size already specified");
					}
					String s = args[i].substring("--stack=".length());
					if (s.startsWith("0x")) {
						stack = Long.parseUnsignedLong(s.substring("0x".length()), 16);
					} else if (s.startsWith("HEX-")) {
						stack = Long.parseUnsignedLong(s.substring("HEX-".length()), 16);
					} else {
						stack = Long.parseUnsignedLong(s, 10);
					}
				} else {
					crash(i, args, "unknown option");
				}
			}
		}
		i -- ;
		if (pmc == null) {
			crash( -1, args, "no mashine file spezified!");
		}
		args[i] = pmc;
		if (safe) {
			pvm = new PVMImpl(pfs).init(i, args);
		} else {
			try {
				pvm = new PVMUnsafeImpl(pfs).init(i, args);
			} catch (Throwable t) {
				if (unsafe || t instanceof ThreadDeath) {
					throw t;
				}
				pvm = new PVMImpl(pfs).init(i, args);
			}
		}
	}
	
	private static PatrFileSysImpl pfs(String[] args, PatrFileSysImpl pfs, int i, String p) {
		try {
			if (pfs != null) {
				crash(i, args, "patr-file-system already set");
			}
			Path path = (p.indexOf(':') == -1) ? Paths.get(p) : Paths.get(URI.create(p));
			pfs = new PatrFileSysImpl(SeekablePathBlockAccessor.create(path, -1, false));
		} catch (IOException e) {
			crash(i, args, e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		return pfs;
	}
	
	private static void help() {
		System.out.print(
			/* */ "Usage: pvm [PVM_OPTIONS] --pmc=<MASHINE_FILE> [PROGRAM_ARGUMENTS]\n"
				+ "or:    pvm [PVM_OPTIONS] --pmc <MASHINE_FILE> [PROGRAM_ARGUMENTS]\n"
				+ "    --help\n"
				+ "        to print this message and exit\n"
				+ "    --pfs=<PFS_FILE>\n"
				+ "    or --pfs <PFS_FILE>\n"
				+ "        to set the patr-file-system file\n"
				+ "        this option is always needed!\n"
				+ "    --pmc=<PFS_FILE>\n"
				+ "    or --pmc <PFS_FILE>\n"
				+ "        to set the primitive-mashine-code file\n"
				+ "        this option is always needed!\n"
				+ "        options after this option will be delegated\n"
				+ "        to the mashine file as program arguments\n"
				+ "        if the mashine file starts with '#!' it will\n"
				+ "        be interpreted: the file followed by the '#!'\n"
				+ "        is loaded instead and given the mashine file\n"
				+ "        followed by its program arguments as program args\n"
				+ "    --stack=<INIT_STACK>\n"
				+ "        to init the stack with a memory block of the given size\n"
				+ "        if the INIT_STACK starts with '0x' or 'HEX-' it is parsed\n"
				+ "        as a hexadecimal number, otherwise it is parsed as a\n"
				+ "        decimal number\n"
				+ "    --unsafe\n"
				+ "        to always use the 'unsafe' pvm impl\n"
				+ "        and to fail if impl is unusable\n"
				+ "        this option is impl specific\n"
				+ "    --safe\n"
				+ "        to never use the 'unsafe' pvm impl\n"
				+ "        this option is impl specific\n"
				+ "");
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
