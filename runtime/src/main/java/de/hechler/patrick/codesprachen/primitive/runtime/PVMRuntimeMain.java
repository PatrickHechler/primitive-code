package de.hechler.patrick.codesprachen.primitive.runtime;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugger;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;

public class PVMRuntimeMain {
	
	private static PVMDebugger debug;
	
	public static void main(String[] args) {
		setup(args);
		Thread sett = new Thread(() -> transfer(debug.getPVMError(), System.err), "std-err-transfer");
		sett.setDaemon(true);
		sett.start();
		Thread sitt = new Thread(() -> transfer(debug.getPVMIn(), System.out), "std-out-transfer");
		sitt.setDaemon(true);
		sitt.start();
		debug.run();
	}
	
	private static void transfer(InputStream from, PrintStream to) {
		while (true) {
			try {
				int avl = from.available();
				if (avl == 0) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				byte[] bytes = new byte[avl];
				int read = from.read(bytes, 0, avl);
				to.write(bytes, 0, read);
			} catch (IOException e) {
				throw new IOError(e);
			}
		}
	}
	
	private static void setup(String[] args) {
		String pvm = null;
		String pmc = null;
		int port = -1;
		for (int i = 0; i < args.length; i ++ ) {
			switch (args[i]) {
			case "--help":
				help();
				break;
			case "--p":
			case "--port":
				if ( ++ i >= args.length) {
					crash(i, args, "not enugh args");
				}
				if (port != -1) {
					crash(i, args, "port double set");
				}
				port = Integer.parseInt(args[i]);
				break;
			case "--pvm":
				if ( ++ i >= args.length) {
					crash(i, args, "not enugh args");
				}
				if (pvm != null) {
					crash(i, args, "pvm double set");
				}
				pvm = args[i];
				break;
			case "-pmc":
				if ( ++ i >= args.length) {
					crash(i, args, "not enugh args");
				}
				if (pmc != null) {
					crash(i, args, "pmc double set");
				}
				pmc = args[i];
				break;
			default:
				crash(i, args, "unknown argument");
			}
		}
		if (pmc == null) {
			crash( -1, args, "pmc not set");
		}
		if (pvm == null) {
			pvm = "pvm";
		}
		if (port == -1) {
			port = 5555;
		}
		Process pvmp = null;
		try {
			try {
				pvmp = Runtime.getRuntime().exec(new String[] {pvm, "--pmc", pmc, "--wait", "--port=" + port });
			} catch (IOException e1) {
				try {
					pvmp = Runtime.getRuntime().exec(new String[] {"wsl", "-d", "ubuntu", pvm, "--pmc", pmc, "--wait", "--port=" + port });
				} catch (IOException e2) {
					throw e1;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			final Process _pvmp = pvmp;
			Runtime.getRuntime().addShutdownHook(new Thread(() -> PVMDebugger.kill(_pvmp, false)));
			debug = new PVMDebugger(new Scanner(System.in), System.out, new PVMDebugingComunicator(pvmp, new Socket("localhost", port)), pvmp);
		} catch (IOException e) {
			if (pvmp != null) {
				PVMDebugger.kill(pvmp, false);
			}
			throw new IOError(e);
		}
	}
	
	
	private static void crash(int index, String[] args, String msg) {
		System.err.println(msg);
		for (int i = 0; i < args.length; i ++ ) {
			if (i == index) {
				System.err.print("error hapaned here -> ");
			}
			System.err.println("[" + i + "]: '" + args[i] + "'");
		}
		System.exit(1);
	}
	
	private static void help() {
		System.out.println("--help");
		System.out.println("  to print this message");
		System.out.println("--pvm [PVM]");
		System.out.println("  to set the debugging port");
		System.out.println("-port [PORT]");
		System.out.println("or");
		System.out.println("-p [PORT]");
		System.out.println("  to set the debugging port");
		System.out.println("-pmc [PMC]");
		System.out.println("  to set mashine code file");
	}
	
}
