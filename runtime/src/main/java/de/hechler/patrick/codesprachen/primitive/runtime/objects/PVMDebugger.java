package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.DisasmMode;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.PrimitiveDisassembler;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert;

public class PVMDebugger implements Runnable {
	
	private final Scanner in;
	private final PrintStream out;
	private final PVMDebugingComunicator comunicate;
	private final Process pvm;
	private final PrimitiveDisassembler disasm;
	
	public PVMDebugger(Scanner in, PrintStream out, PVMDebugingComunicator comunicate, Process pvm) {
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
				out.print("(pvmdb): ");
				String str = in.next();
				switch (str.toLowerCase()) {
				case "help":
					help();
					break;
				case "terminate":
					kill(pvm, true);
					out.println("exit-code: " + pvm.exitValue());
					break;
				case "exit":
					comunicate.exit();
					out.println("pvm exited (with 0), will now end");
					return;
				case "exec-until":
				case "exec_until":
				case "execuntil":
				case "execute-until":
				case "execute_until":
				case "executeuntil":
					str = in.next();
					switch (str.toLowerCase()) {
					case "exit":
						comunicate.executeUntilExit();
						break;
					case "error":
						comunicate.executeUntilErrorOrExitCall();
						break;
					default:
						throw new RuntimeException("unknown execute: '" + str + "'");
					}
					break;
				case "run":
					comunicate.run();
					break;
				case "next": {
					comunicate.executeNext();
					PVMSnapshot sn = comunicate.getSnapshot();
					int len = 24;
					byte[] bytes = new byte[len];
					while (true) {
						try {
							comunicate.getMem(sn.ip, bytes, 0, len);
							disasm.deassemble(sn.ip, bytes);
							break;
						} catch (RuntimeException e) {
							len -= 8;
							if (len <= 0) {
								break;
							}
						}
					}
					break;
				}
				case "tell":
					str = in.nextLine().substring(1) + '\n';
					pvm.getOutputStream().write(str.getBytes(StandardCharsets.UTF_8));
					break;
				case "break": {
					long bp = in.nextLong(16);
					comunicate.addBreakpoints(new long[] {bp });
					break;
				}
				// System.out.println("rembr [ADDRESS]");
				// System.out.println(" to remove a breakpoint");
				case "rembr": {
					long bp = in.nextLong(16);
					comunicate.removeBreakpoints(new long[] {bp });
					break;
				}
				// System.out.println("remallbr");
				// System.out.println(" to remove all breakpoint");
				case "remallbr": {
					long[] br = comunicate.getBreakpoints();
					comunicate.removeBreakpoints(br);
					break;
				}
				// System.out.println("br [(enable | true | 1) | (disable | false | 0)]");
				// System.out.println(" to enable/disable breakpoints");
				case "br": {
					str = in.next();
					boolean enableBreaks;
					switch (str.toLowerCase()) {
					case "enable":
					case "true":
					case "1":
						enableBreaks = true;
						break;
					case "disable":
					case "false":
					case "0":
						enableBreaks = false;
						break;
					default:
						throw new RuntimeException("unknown br[eakpoint] propertie: '" + str + "'");
					}
					comunicate.setBreakpointsEnabled(enableBreaks);
				}
				case "get":
					str = in.next();
					switch (str.toLowerCase()) {
					case "snapshot":
						comunicate.getSnapshot().print(out);
						break;
					case "memory": {
						long PNTR = in.nextLong(16);
						int len = in.nextInt();
						byte[] bytes = new byte[len];
						comunicate.getMem(PNTR, bytes, 0, len);
						disasm.deassemble(PNTR, bytes);
						break;
					}
					case "string": {
						str = in.next();
						Charset cs = Charset.forName(str);
						long PNTR = in.nextLong(16);
						int len = in.nextInt();
						byte[] bytes = new byte[len];
						comunicate.getMem(PNTR, bytes, 0, len);
						out.print(new String(bytes, cs));
						break;
					}
					case "chars": {
						long PNTR = in.nextLong(16);
						int len = in.nextInt();
						byte[] bytes = new byte[len];
						comunicate.getMem(PNTR, bytes, 0, len);
						out.print(new String(bytes, StandardCharsets.US_ASCII));
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
							System.out.println("  " + toFullHexStr(breakpoints[i]));
						}
						break;
					default:
						throw new RuntimeException("unknown: get '" + str + "'");
					}
					break;
				case "set":
					str = in.next();
					switch (str) {
					case "ax": {
						long nval = in.nextLong(16);
						PVMSnapshot sn = comunicate.getSnapshot();
						sn.ax = nval;
						comunicate.setSnapshot(sn);
						break;
					}
					case "bx": {
						long nval = in.nextLong(16);
						PVMSnapshot sn = comunicate.getSnapshot();
						sn.bx = nval;
						comunicate.setSnapshot(sn);
						break;
					}
					case "cx": {
						long nval = in.nextLong(16);
						PVMSnapshot sn = comunicate.getSnapshot();
						sn.cx = nval;
						comunicate.setSnapshot(sn);
						break;
					}
					case "dx": {
						long nval = in.nextLong(16);
						PVMSnapshot sn = comunicate.getSnapshot();
						sn.dx = nval;
						comunicate.setSnapshot(sn);
						break;
					}
					case "sp": {
						long nval = in.nextLong(16);
						PVMSnapshot sn = comunicate.getSnapshot();
						sn.sp = nval;
						comunicate.setSnapshot(sn);
						break;
					}
					case "ip": {
						long nval = in.nextLong(16);
						PVMSnapshot sn = comunicate.getSnapshot();
						sn.ip = nval;
						comunicate.setSnapshot(sn);
						break;
					}
					case "intp": {
						long nval = in.nextLong(16);
						PVMSnapshot sn = comunicate.getSnapshot();
						sn.intp = nval;
						comunicate.setSnapshot(sn);
						break;
					}
					case "intcnt": {
						long nval = in.nextLong(16);
						PVMSnapshot sn = comunicate.getSnapshot();
						sn.intcnt = nval;
						comunicate.setSnapshot(sn);
						break;
					}
					case "status": {
						long nval = in.nextLong(16);
						PVMSnapshot sn = comunicate.getSnapshot();
						sn.status = nval;
						comunicate.setSnapshot(sn);
						break;
					}
					case "memory": {
						long addr = in.nextLong(16);
						long nval = in.nextLong(16);
						byte[] bytes = new byte[8];
						Convert.convertLongToByteArr(bytes, 0, nval);
						comunicate.setMem(addr, bytes, 0, 8);
						break;
					}
					case "chars": {
						long addr = in.nextLong(16);
						str = in.nextLine().trim();
						byte[] bytes = formattString(str, StandardCharsets.US_ASCII);
						comunicate.setMem(addr, bytes, 0, bytes.length);
					}
					case "string": {
						str = in.next();
						Charset cs = Charset.forName(str);
						long addr = in.nextLong(16);
						str = in.nextLine().trim();
						byte[] bytes = formattString(str, cs);
						comunicate.setMem(addr, bytes, 0, bytes.length);
						break;
					}
					default:
						throw new RuntimeException("unknown set: '" + str + "'");
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

	private byte[] formattString(String str, Charset cs) {
		char[] carr = str.toCharArray();
		int start = -1, len = -1;
		for (int i = 0; i < carr.length && carr[i] != '\0'; i ++ ) {
			switch (carr[i]) {
			case '"':
				if (start == -1) {
					start = i + 1;
					break;
				}
				if (i + 1 >= carr.length) {
					break;
				}
				if (carr[i + 1] == '\0') {
					break;
				}
				throw new RuntimeException("illegal formatt of string: '" + str + "'");
			case '\\':
				if (start == -1) {
					throw new RuntimeException("illegal formatt of string: '" + str + "'");
				}
				if (i >= carr.length) {
					throw new RuntimeException("illegal formatt of string: '" + str + "'");
				}
				switch (carr[i + 1]) {
				case '"':
					carr[i] = '"';
					break;
				case '\\':
					carr[i] = '\\';
					break;
				case 't':
					carr[i] = '\t';
					break;
				case 'r':
					carr[i] = '\r';
					break;
				case 'n':
					carr[i] = '\n';
					break;
				case '0':
					carr[i] = '\0';
					break;
				default:
					throw new RuntimeException("illegal formatt of string: '" + str + "'");
				}
				System.arraycopy(carr, i + 2, carr, i + 1, carr.length - i - 2);
				carr[carr.length - 1] = '\0';
				len ++ ;
				break;
			default:
				if (start == -1 && carr[i] > ' ') {
					throw new RuntimeException("illegal formatt of string: '" + str + "'");
				}
				len ++ ;
			}
		}
		byte[] bytes = new String(carr, start, len).getBytes(cs);
		return bytes;
	}
	
	private void help() {
		System.out.println("help");
		System.out.println("  to print this message");
		System.out.println("terminate");
		System.out.println("  to terminate the pvm forcefully");
		System.out.println("exit");
		System.out.println("  to send the pvm the exit signal");
		System.out.println("execute-until");
		System.out.println("  error");
		System.out.println("    to let the pvm execute until an error happanes, the exit interrupt gets called or a breakpoint triggers (if enabled)");
		System.out.println("  exit");
		System.out.println("    to let the pvm execute until the pvm would exit or a breakpoint triggers (if enabled)");
		System.out.println("run");
		System.out.println("  to let the pvm execute (until a breakpoint triggers (if enabled))");
		System.out.println("next");
		System.out.println("  to execute the next command");
		System.out.println("tell [MESSAGE]");
		System.out.println("  to delegate the message to the pvms stdin");
		System.out.println("break [ADDRESS]");
		System.out.println("  to add a breakpoint");
		System.out.println("rembr [ADDRESS]");
		System.out.println("  to remove a breakpoint");
		System.out.println("remallbr");
		System.out.println("  to remove all breakpoint");
		System.out.println("br [(enable | true | 1) | (disable | false | 0)]");
		System.out.println("  to enable/disable breakpoints");
		System.out.println("get [FIELD]");
		System.out.println("  to the value of the field:");
		System.out.println("    snapshot");
		System.out.println("      makes an snapshot of the pvm and prints it");
		System.out.println("    memory [ADDR] [LEN]");
		System.out.println("      reads the memory");
		System.out.println("    chars [ADDR] [LEN]");
		System.out.println("      to read the memory as an ASCII string");
		System.out.println("    string [CHARSET] [ADDR] [LEN]");
		System.out.println("      to read the memory as an string, with the given charset");
		System.out.println("    breakpoints");
		System.out.println("      to list all breakpoints");
		System.out.println("set");
		System.out.println("  to set the value of the field:");
		System.out.println("    ax");
		System.out.println("      the AX register");
		System.out.println("    bx");
		System.out.println("      the BX register");
		System.out.println("    cx");
		System.out.println("      the CX register");
		System.out.println("    dx");
		System.out.println("      the DX register");
		System.out.println("    sp");
		System.out.println("      the stack pointer");
		System.out.println("    ip");
		System.out.println("      the instruction pointer");
		System.out.println("    intp");
		System.out.println("      the interrupt pointer");
		System.out.println("    intcnt");
		System.out.println("      the interrupt count register");
		System.out.println("    status");
		System.out.println("      the status register");
		System.out.println("    memory [ADDR] [VALUE]");
		System.out.println("      set the value of the memory at the given address");
		System.out.println("    chars [ADDR] [STRING]");
		System.out.println("      to write the ASCII string to the address");
		System.out.println("      the string starts and end with '\"'");
		System.out.println("      to write '\"', write '\\\"'");
		System.out.println("      to write '\\', write '\\\\'");
		System.out.println("      to write [TAB], write '\\t'");
		System.out.println("      to write [CR], write '\\r'");
		System.out.println("      to write [LF], write '\\n'");
		System.out.println("      to write [NULL], write '\\0'");
		System.out.println("    string [CHARSET] [ADDR] [STRING]");
		System.out.println("      to write the string, with the given charset, to the address");
		System.out.println("      the string starts and end with '\"'");
	}
	
	
	public InputStream getPVMError() {
		return pvm.getErrorStream();
	}
	
	public InputStream getPVMIn() {
		return pvm.getInputStream();
	}
	
	public OutputStream getPVMOut() {
		return pvm.getOutputStream();
	}
	
	public static String toFullHexStr(long PNTR) {
		String str = Long.toHexString(PNTR);
		return ("0000000000000000".substring(str.length())) + str;
	}
	
	public static void kill(Process p, boolean force) {
		if (p.isAlive()) {
			if ( !force) {
				p.destroy();
				try {
					p.waitFor(1000L, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			if (p.isAlive()) {
				p.destroyForcibly();
			}
		}
		// p.children().forEach(c -> kill(c, force));
	}
	
	// public static void kill(ProcessHandle p, boolean force) {
	// if (p instanceof Process) {
	// kill((Process) p, force);
	// return;
	// }
	// if (p.isAlive()) {
	// if ( !force) {
	// try {
	// Thread.sleep(1000L);
	// } catch (InterruptedException e1) {
	// e1.printStackTrace();
	// }
	// }
	// p.destroy();
	// if (p.isAlive()) {
	// p.destroyForcibly();
	// }
	// }
	// p.children().forEach(c -> kill(c, force));
	// }
	//
}
