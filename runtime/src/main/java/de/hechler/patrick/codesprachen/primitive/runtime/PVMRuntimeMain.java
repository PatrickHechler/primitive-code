package de.hechler.patrick.codesprachen.primitive.runtime;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugger;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;

public class PVMRuntimeMain {
	
	private static PVMDebugger debug;
	private static boolean delegateStreams;
	
	public static void main(String[] args) {
		setup(args);
		if (delegateStreams) {
			Thread sett = new Thread(() -> transfer(debug.getPVMError(), System.err), "std-err-transfer");
			sett.setDaemon(true);
			sett.start();
			Thread sitt = new Thread(() -> transfer(debug.getPVMIn(), System.out), "std-out-transfer");
			sitt.setDaemon(true);
			sitt.start();
		}
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
		boolean attach = false;
		String pvm = null;
		String pmc = null;
		int port = -1;
		int i;
		arg_loop: for (i = 0; i < args.length; i ++ ) {
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
				if (attach) {
					crash(i, args, "pvm option not compatible with atach option");
				}
				if ( ++ i >= args.length) {
					crash(i, args, "not enugh args");
				}
				if (pvm != null) {
					crash(i, args, "pvm double set");
				}
				pvm = args[i];
				break;
			case "-pmc":
				if (attach) {
					crash(i, args, "pmc option not compatible with atach option");
				}
				if ( ++ i >= args.length) {
					crash(i, args, "not enugh args");
				}
				if (pmc != null) {
					crash(i, args, "pmc double set");
				}
				pmc = args[i ++ ];
				break arg_loop;
			case "--attach":
				if (pvm != null) {
					crash(i, args, "pvm option not compatible with atach option");
				}
				if (pmc != null) {
					crash(i, args, "pmc option not compatible with atach option");
				}
				attach = true;
				break;
			default:
				crash(i, args, "unknown argument");
			}
		}
		if (attach) {
			delegateStreams = false;
			if (port == -1) {
				crash( -1, args, "port not set, but needed on atach");
			}
			try {
				PVMDebugingComunicator com = new PVMDebugingComunicator(null, new Socket("localhost", port));
				debug = new PVMDebugger(new Scanner(System.in), System.out, com, null);
			} catch (IOException e) {
				throw new IOError(e);
			}
		} else {
			delegateStreams = true;
			if (pmc == null) {
				crash( -1, args, "pmc not set");
			}
			if (pvm == null) {
				pvm = "pvm";
			}
			if (port == -1) {
				port = new Random().nextInt(65535 - 2024) + 2024;
			}
			System.out.println("port=" + port);
			Process pvmp = null;
			try {
				String[] arguments;
				arguments = new String[4 + args.length - i];
				arguments[0] = pvm;
				arguments[1] = "--port=" + port;
				arguments[2] = "--wait";
				arguments[3] = "--pmc=" + pmc;
				System.arraycopy(args, i, arguments, 4, args.length - i);
				try {
					pvmp = Runtime.getRuntime().exec(arguments);
				} catch (IOException e1) {
					String[] args2 = new String[arguments.length + 3];
					System.arraycopy(arguments, 0, args2, 3, arguments.length);
					args2[0] = "wsl";
					args2[1] = "-d";
					args2[2] = "ubuntu";
					try {
						pvmp = Runtime.getRuntime().exec(args2);
					} catch (IOException e2) {
						e1.addSuppressed(e2); throw e1;
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				debug = new PVMDebugger(new Scanner(System.in), System.out, new PVMDebugingComunicator(pvmp, new Socket("localhost", port)), pvmp);
			} catch (IOException e) {
				if (pvmp != null) {
					PVMDebugger.kill(pvmp, false);
				}
				System.err.println("pvm exited with code: " + pvmp.exitValue());
				throw new IOError(e);
			}
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
		System.out.println("  not compatible with attach");
		System.out.println("-port [PORT]");
		System.out.println("or");
		System.out.println("-p [PORT]");
		System.out.println("  to set the debugging port");
		System.out.println("  needed when attach is set");
		System.out.println("  if both not set a random port between 2024 and 65535 is tried set");
		System.out.println("--attach");
		System.out.println("  to attach the debugger to a already running debug session");
		System.out.println("  an attached debugger is not able to access the streams of the pvm");
		System.out.println("  an attached debugger is also not able to terminate a pvm (the debugger can still send the exitt command)");
		System.out.println("-pmc [PMC]");
		System.out.println("  to set mashine code file");
		System.out.println("  the folowing arguments will be delegated to the debug program");
		System.out.println("  not compatible with attach");
	}
	
}
