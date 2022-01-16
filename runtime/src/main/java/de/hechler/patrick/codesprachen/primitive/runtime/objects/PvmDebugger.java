package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.io.PrintStream;
import java.util.Scanner;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.DisasmMode;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.PrimitiveDisassembler;

public class PvmDebugger implements Runnable {
	
	private final Scanner in;
	private final PrintStream out;
	private final PVMDebugingComunicator comunicate;
	private final Process pvm;
	private final PrimitiveDisassembler disasm;
	
	public PvmDebugger(Scanner in, PrintStream out, PVMDebugingComunicator comunicate, Process pvm) {
		this.in = in;
		this.out = out;
		this.comunicate = comunicate;
		this.pvm = pvm;
		this.disasm = new PrimitiveDisassembler(DisasmMode.analysable, out);
	}
	
	@Override
	public void run() {
		help();
		while (pvm.isAlive()) {
			try {
				String str = in.next();
				switch (str.toLowerCase()) {
				case "help":
					help();
					break;
				case "terminate":
					pvm.destroyForcibly();
					out.println("exit-code: " + pvm.waitFor());
				case "exit":
					comunicate.exit();
					out.println("pvm exited (with 0), will now exit");
					return;
				case "run":
					comunicate.run();
					out.println("startet pvm");
					break;
				case "next": {
					comunicate.executeNext();
					PVMSnapshot sn = comunicate.getSnapshot();
					byte[] bytes = new byte[8];
					comunicate.getMem(sn.ip, bytes, 0, 8);
					disasm.deassemble(sn.ip, bytes);
					break;
				}
				case "break": {
					long bp = in.nextLong(16);
					comunicate.addBreakpoints(new long[] {bp});
				}
				case "get":
					str = in.next();
					switch (str.toLowerCase()) {
					case "snapshot":
						comunicate.getSnapshot().print(out);
						break;
					case "memory": {
						long PNTR = in.nextLong();
						int len = in.nextInt();
						byte[] bytes = new byte[len];
						comunicate.getMem(PNTR, bytes, 0, len);
						disasm.deassemble(PNTR, bytes);
						break;
					}
					case "breakpoints":
						if (comunicate.isBreakpointsEnabled()) {
							out.println("breakpoints: enabled");
						} else {
							out.println("breakpoints: disabled");
						}
						long[] breakpoints = comunicate.getBreakpoints();
						System.out.println(breakpoints.length + " breakpoints:");
						for (int i = 0; i < breakpoints.length; i ++ ) {
							System.out.println("  " + pntrStr(breakpoints[i]));
						}
						break;
					default:
						throw new RuntimeException("unknown: get '" + str + "'");
					}
					break;
				default:
					throw new RuntimeException("unknown command: '" + str + "'");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		out.println("the pvm terminated with exie-code " + pvm.exitValue());
		out.println("thanks for using me.");
		out.println("goodbye, have a nice day.");
	}
	
	private String pntrStr(long PNTR) {
		String str = Long.toHexString(PNTR);
		return "0x" + ("0000000000000000".substring(str.length())) + str;
	}
	
	private void help() {
		// TODO Auto-generated method stub
		
	}
	
}
