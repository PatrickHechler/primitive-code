package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.DebugPVM;

public class PVMDebugShell implements Runnable {
	
	private final DebugPVM     pvm;
	private final Scanner      in;
	private final Charset      cs;
	private final OutputStream stdin;
	
	public PVMDebugShell(DebugPVM pvm, Scanner in) {
		this(pvm, in, StandardCharsets.UTF_16LE);
	}
	
	public PVMDebugShell(DebugPVM pvm, Scanner in, Charset cs) {
		this.pvm = pvm;
		this.in = in;
		this.cs = cs;
		this.stdin = pvm.stdin();
	}
	
	public PVMDebugShell init() {
		pvm.stdout(System.out);
		pvm.stdlog(System.err);
		return this;
	}
	
	// TODO more commands
	@Override
	public void run() {
		for (prompt(); in.hasNextLine(); prompt()) {
			try {
				String line = in.nextLine().trim();
				if (line.isEmpty()) continue;
				String[] cmds = line.split("\\s+");
				switch (cmds[0].toLowerCase()) {
				case "help":
				case "h":
				case "?":
					help();
					break;
				case "tell":
					String tell = line.substring(cmds[0].length()).trim() + '\n';
					byte[] bytes = tell.getBytes(cs);
					stdin.write(bytes);
					break;
				case "exit":
					System.out.println("exit pvm");
					pvm.exit();
					// will be only printed when remote debugging
					System.out.println("pvm exited");
					return;
				case "run":
				case "r":
					pvm.run();
					break;
				case "si":
					pvm.step();
					break;
				case "so":
					pvm.stepOut();
					break;
				case "next":
				case "n":
					pvm.stepOver();
					break;
				case "step":
					if (cmds.length < 2) {
						System.out.println("'step' is no complete command ('over', 'in', 'out' needed)");
						continue;
					}
					switch (cmds[1].toLowerCase()) {
					case "in":
						pvm.step();
						break;
					case "over":
						pvm.stepOver();
						break;
					case "out":
						if (cmds.length > 2) {
							try {
								long deep = Long.parseLong(cmds[2]);
								pvm.step(deep);
							} catch (NumberFormatException e) {
								System.out.println("illegal command: after 'step out' only a number is allowed (" + e.getMessage() + ")");
								continue;
							}
						} else {
							pvm.stepOut();
						}
						break;
					case "stop":
						pvm.stop();
						break;
					default:
						System.out.println("unknown command: '" + line + "'");
						continue;
					}
					break;
				}
			} catch (Exception e) {
				System.out.println("error: " + e.getClass().getName() + ": " + e.getMessage());
			}
		}
	}
	
	private void help() {
		System.out.println(
			/* */ "pvm debug shell: help\n"
				+ "    help\n"
				+ "        to print this message\n"
				+ "    exit\n"
				+ "        to exit the pvm\n"
				+ "    step in\n"
				+ "        to execute a single command\n"
				+ "    step over\n"
				+ "        to execute single commands until a equal\n"
				+ "        number of CALLs and RETs are made\n"
				+ "    step out\n"
				+ "        to execute single commands until the number\n"
				+ "        of CALLs is one lower than the RETs which are made\n"
				+ "    step out <NUMBER>\n"
				+ "        to execute single commands until the number\n"
				+ "        of CALLs is NUMBER lower than the RETs which are made\n"
				+ "    stop\n"
				+ "        to let the pvm stop executing code and wait\n"
				+ "");
	}
	
	private void prompt() {
		System.out.print("[pvm]: ");
	}
	
}
