//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.primitive.assemble.programs;

import static de.hechler.patrick.zeugs.check.Assert.assertEquals;
import static de.hechler.patrick.zeugs.check.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.function.BooleanSupplier;

import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.anotations.End;
import de.hechler.patrick.zeugs.check.anotations.MethodParam;
import de.hechler.patrick.zeugs.check.anotations.Start;
import de.hechler.patrick.zeugs.check.exceptions.CheckerException;
import de.hechler.patrick.zeugs.pfs.FSProvider;
import de.hechler.patrick.zeugs.pfs.interfaces.FS;
import de.hechler.patrick.zeugs.pfs.interfaces.FSElement;
import de.hechler.patrick.zeugs.pfs.interfaces.File;
import de.hechler.patrick.zeugs.pfs.interfaces.WriteStream;
import de.hechler.patrick.zeugs.pfs.misc.ElementType;
import de.hechler.patrick.zeugs.pfs.opts.PatrFSOptions;
import de.hechler.patrick.zeugs.pfs.opts.StreamOpenOptions;

@CheckClass
@SuppressWarnings({"static-method", "javadoc"})
public class ProgramChecker {
	
	protected static final byte[] EMPTY_BARR = new byte[0];
	
	private static final StreamOpenOptions CREATE_FILE_OPTS = new StreamOpenOptions(false, true, false, ElementType.FILE, false, true, false);
	
	private static final String HELLO_WORLD_PMF = "/hello-world";
	private static final String HELLO_WORLD_PFS = "./testout/hello-world.pfs";
	private static final String HELLO_WORLD_RES = "/de/hechler/patrick/codesprachen/primitive/assemble/programs/hello-world.psc";
	
	private static final String ECHO_PMF = "/echo";
	private static final String ECHO_PFS = "./testout/echo.pfs";
	private static final String ECHO_RES = "/de/hechler/patrick/codesprachen/primitive/assemble/programs/echo.psc";
	
	protected FSProvider patrFsProv;
	
	@Start
	protected void init() throws IOException {
		Files.createDirectories(Path.of("./testout/"));
	}
	
	@Start
	protected void start(@MethodParam Method met) throws NoSuchProviderException {
		System.out.println("check now " + met.getName());
		this.patrFsProv = FSProvider.ofName(FSProvider.PATR_FS_PROVIDER_NAME);
	}
	
	@End
	protected void end(@MethodParam Method met) {
		System.out.println("finished " + met.getName() + " check");
		this.patrFsProv.loadedFS().forEach(fs -> {
			try {
				fs.close();
			} catch (IOException e) {
				throw new IOError(e);
			}
		});
	}
	
	@Check
	private void checkHelloWorld() throws IOException, InterruptedException {
		try (FS fs = this.patrFsProv.loadFS(new PatrFSOptions(HELLO_WORLD_PFS, true, 4096L, 1024))) {
			System.out.println("opened fs, asm now");
			asm(fs, HELLO_WORLD_RES, HELLO_WORLD_PMF);
			System.out.println("finished asm, close now fs");
		}
		System.out.println("execute now the program");
		execute(HELLO_WORLD_PFS, HELLO_WORLD_PMF, 0, EMPTY_BARR, "hello primitive world\n".getBytes(StandardCharsets.UTF_8), EMPTY_BARR);
	}
	
	@Check
	private void checkEchoHelloPrimitiveWorld() throws IOException, InterruptedException {
		try (FS fs = this.patrFsProv.loadFS(new PatrFSOptions(ECHO_PFS, true, 4096L, 1024))) {
			System.out.println("opened fs, asm now");
			asm(fs, ECHO_RES, ECHO_PMF);
			System.out.println("finished asm, close now fs");
		}
		System.out.println("execute now the program");
		execute(ECHO_PFS, ECHO_PMF, 0, EMPTY_BARR, "hello primitive world\n".getBytes(StandardCharsets.UTF_8), EMPTY_BARR, "hello",
				"primitive world");
	}
	
	
	@Check
	private void checkEchoManyArgs() throws IOException, InterruptedException {
		try (FS fs = this.patrFsProv.loadFS(new PatrFSOptions(ECHO_PFS, true, 4096L, 1024))) {
			System.out.println("opened fs, asm now");
			asm(fs, ECHO_RES, ECHO_PMF);
			System.out.println("finished asm, close now fs");
		}
		System.out.println("execute now the program");
		execute(ECHO_PFS, ECHO_PMF, 0, EMPTY_BARR,
				("hello primitive world\nthis is a multilie argument and here         comes some spaces and the text ... EOF\n")
						.getBytes(StandardCharsets.UTF_8),
				EMPTY_BARR, "hello", "primitive world\nthis is a multilie argument", "and", "here", "       ", "comes", "some", "spaces", "and",
				"the", "text", "...", "EOF");
	}
	
	@Check
	private void checkEchoNop() throws IOException, InterruptedException {
		try (FS fs = this.patrFsProv.loadFS(new PatrFSOptions(ECHO_PFS, true, 4096L, 1024))) {
			System.out.println("opened fs, asm now");
			asm(fs, ECHO_RES, ECHO_PMF);
			System.out.println("finished asm, close now fs");
		}
		System.out.println("execute now the program");
		execute(ECHO_PFS, ECHO_PMF, 0, EMPTY_BARR, "\n".getBytes(StandardCharsets.UTF_8), EMPTY_BARR);
	}
	
	protected void execute(String pfsFile, String pmfFile, int exitCode, byte[] stdin, byte[] stdout, byte[] stderr, String... programArgs)
			throws IOException, InterruptedException {
		Runtime  r    = Runtime.getRuntime();
		String[] args = new String[] { "pvm", "--pfs=" + pfsFile, "--pmf=" + pmfFile };
		if (programArgs != null && programArgs.length != 0) {
			int olen = args.length;
			args = Arrays.copyOf(args, olen + programArgs.length);
			System.arraycopy(programArgs, 0, args, olen, programArgs.length);
		}
		System.out.println("args: " + Arrays.toString(args));
		Process process = r.exec(args);
		System.out.println("started process pid: " + process.pid() + "   " + process);
		if (stdin.length > 0) { process.getOutputStream().write(stdin); }
		TwoBools b1 = new TwoBools(), b2 = new TwoBools();
		Thread   t  = Thread.ofVirtual().unstarted(() -> check(() -> process.isAlive(), process.getErrorStream(), stderr, b1));
		t.setName("check stderr");
		t.start();
		t = Thread.ofVirtual().unstarted(() -> check(() -> process.isAlive(), process.getInputStream(), stdout, b2));
		t.setName("check stdout");
		t.start();
		assertEquals(exitCode, process.waitFor());
		checkResult(b1);
		checkResult(b2);
	}
	
	protected void checkResult(TwoBools res) throws InterruptedException, CheckerException {
		while (!res.finish) {
			synchronized (res) {
				if (res.finish) { break; }
				res.wait(1000L);
			}
		}
		assertTrue(res.result);
	}
	
	protected void check(BooleanSupplier cond, InputStream stream, byte[] value, TwoBools b) {
		System.err.println(logStart() + "start");
		try {
			b.result = false;
			byte[] other = new byte[value.length];
			for (int i = 0; i < value.length;) {
				try {
					int reat = stream.read(other, i, other.length - i);
					if (reat == -1) {
						if (cond.getAsBoolean()) {
							sleep();
							continue;
						}
						throw new IOException("reached EOF too early");
					}
					System.err.print(logStart() + "little read: ");
					System.err.write(other, i, reat);
					System.err.println("\nreat: " + reat);
					i += reat;
				} catch (IOException e) {
					throw new IOError(e);
				}
			}
			if (other.length != 0) {
				System.err.print(logStart() + "read: ");
				try {
					System.err.write(other);
				} catch (@SuppressWarnings("unused") IOException e) {}
				System.err.println();
			}
			b.result = Arrays.equals(value, other);
			try {
				while (stream.available() > 0 || cond.getAsBoolean()) {
					if (stream.available() == 0) {
						sleep();
						continue;
					}
					b.result = false;
					other    = new byte[stream.available()];
					int r = stream.read(other, 0, other.length);
					System.err.println(logStart() + "additioannly read: " + new String(other, 0, r));
				}
			} catch (IOException e) {
				throw new IOError(e);
			}
		} finally {
			b.finish = true;
			synchronized (b) {
				b.notifyAll();
			}
			System.err.println(logStart() + "finish");
		}
	}
	
	private void sleep() {
		try {
			Thread.sleep(0L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private String logStart() {
		return "[" + Thread.currentThread().getName() + "]: ";
	}
	
	private static final class TwoBools {
		
		volatile boolean finish;
		volatile boolean result;
		
	}
	
	private void asm(FS fs, String res, String pmFile) throws IOException, AssembleError {
		try (InputStream in = getClass().getResourceAsStream(res)) {
			try (WriteStream stream = (WriteStream) fs.stream(pmFile, CREATE_FILE_OPTS)) {
				PrimitiveAssembler asm = newAsm(stream);
				System.out.println("assemble now " + pmFile);
				asm.assemble(Paths.get(res), in, StandardCharsets.UTF_8);
				System.out.println("assembled successful, flag now as executable");
				try (File file = fs.file(pmFile)) {
					file.flag(FSElement.FLAG_EXECUTABLE, 0);
				}
			}
		}
	}
	
	private static PrimitiveAssembler newAsm(WriteStream stream) {
		return new PrimitiveAssembler(new BufferedOutputStream(stream.asOutputStream()), null, new Path[0], false, true);
	}
	
}
