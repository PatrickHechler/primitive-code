package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import static de.hechler.patrick.zeugs.check.Assert.assertArrayEquals;
import static de.hechler.patrick.zeugs.check.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;

import de.hechler.patrick.codesprachen.primitive.assemble.TestUtils;
import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMSnapshot;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.anotations.End;
import de.hechler.patrick.zeugs.check.anotations.MethodParam;
import de.hechler.patrick.zeugs.check.anotations.Start;
import de.hechler.patrick.zeugs.check.objects.Checker;

@CheckClass
public class PrimitiveAssemblerChecker extends Checker {
	
	public final static String INPUT_ADD = "/sourcecode/add.psc";
	public final static String INPUT_HELLO_WORLD = "/sourcecode/helloworld.psc";
	public final static String INPUT_HELLO_WORLD_TO_FILE = "/sourcecode/helloworldtofile.psc";
	public final static String INPUT_HELLO_WORLD_TO_FILE_OUTPUT = ".\\output\\out.txt";
	public final static String VALUE_HELLO_WORLD_TO_FILE_OUTPUT = "hello file world";
	public final static String INPUT_HELLO_WORLD_FROM_FILE = "/sourcecode/helloworldfromfile.psc";
	
	PVMDebugingComunicator pvm;
	Process pvmexec;
	
	static volatile int port = 5555;
	int myport;
	
	@Start
	private void start(@MethodParam Method met) throws UnknownHostException, IOException {
		myport = port ++ ;
		pvmexec = Runtime.getRuntime().exec(new String[] {"wsl", "-d", "ubuntu", "./pvm", "--wait", "--port=" + myport });
		pvm = new PVMDebugingComunicator(pvmexec, new Socket("localhost", myport));
		System.out.println("start now:        " + met.getName() + "() myport=" + myport);
	}
	
	@End
	private void end() throws UnknownHostException, IOException {
		if (pvmexec != null && pvmexec.isAlive()) {
			Thread t = Thread.currentThread();
			new Thread(() -> {
				try {
					pvm.exit();
				} catch (IOException | RuntimeException e1) {}
				t.interrupt();
			}).start();
			;
			try {
				Thread.sleep(11000);
			} catch (InterruptedException e1) {}
			if (pvmexec.isAlive()) {
				pvmexec.destroyForcibly();
			}
		}
		pvm = null;
		pvmexec = null;
	}
	
	@Check
	public void assembleAddTest() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrimitiveAssembler pa = new PrimitiveAssembler(out, null, new Path[] {Paths.get(".")}, false, true);
			InputStream helloWorldIn = getClass().getResourceAsStream(INPUT_ADD);
			pa.assemble(pa.preassemble(null, new ANTLRInputStream(new InputStreamReader(helloWorldIn, StandardCharsets.UTF_8)), PrimAsmConstants.START_CONSTANTS,
					new BailErrorStrategy(), false));
			byte[] code = out.toByteArray();
			String hexCodeBytes = TestUtils.toHexCode(code);
			System.out.println(hexCodeBytes);
			String hexCodeLongs = TestUtils.toHexCode(TestUtils.toLong(code));
			System.out.println(hexCodeLongs);
			
			new Thread(() -> {
				while (true) {
					try {
						pvm.getSnapshot().print(System.out);
					} catch (IOException e) {
						throw new IOError(e);
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {}
				}
			}).start();
			
			long[] commands = TestUtils.toLong(code);
			long x00 = 5L;
			long x01 = 30L;
			PVMSnapshot pvmsn = new PVMSnapshot(new long[256]);
			pvmsn.x[0] = x00;
			pvmsn.x[1] = x01;
			pvm.setSnapshot(pvmsn);
			System.out.println("execute");
			pvm.executeUntilExit(commands, false);
			pvmsn = pvm.getSnapshot();
			long x02 = pvmsn.x[2];
			assertEquals(x00 + x01, x02);
			System.out.println("X02=" + x02);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Check
	public void assembleHelloWorldTest() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrimitiveAssembler pa = new PrimitiveAssembler(out, null, new Path[] {Paths.get(".")}, false, true);
			InputStream helloWorldIn = getClass().getResourceAsStream(INPUT_HELLO_WORLD);
			pa.assemble(pa.preassemble(null, new ANTLRInputStream(new InputStreamReader(helloWorldIn, StandardCharsets.UTF_8)), PrimAsmConstants.START_CONSTANTS,
					new BailErrorStrategy(), false));
			byte[] code = out.toByteArray();
			String hexCodeBytes = TestUtils.toHexCode(code);
			System.out.println(hexCodeBytes);
			String hexCodeLongs = TestUtils.toHexCode(TestUtils.toLong(code));
			System.out.println(hexCodeLongs);
			
			long[] commands = TestUtils.toLong(code);
			long ax = 5L;
			long bx = 30L;
			long[] ls = new long[256];
			ls[0] = ax;
			ls[1] = bx;
			PVMSnapshot sn = new PVMSnapshot(ls);
			pvm.setSnapshot(sn);
			
			System.out.println("execute");
			pvm.executeUntilExit(commands, false);
			sn = pvm.getSnapshot();
			long cx = sn.x[2];
			assertEquals(ax + bx, cx);
			System.out.println("CX=" + cx);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Check
	public void assembleHelloWorldToFileTest() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrimitiveAssembler pa = new PrimitiveAssembler(out, null, new Path[] {Paths.get(".")}, false, true);
			InputStream helloWorldIn = getClass().getResourceAsStream(INPUT_HELLO_WORLD_TO_FILE);
			pa.assemble(pa.preassemble(null, new ANTLRInputStream(new InputStreamReader(helloWorldIn, StandardCharsets.UTF_8)), PrimAsmConstants.START_CONSTANTS,
					new BailErrorStrategy(), false));
			byte[] code = out.toByteArray();
			String hexCodeBytes = TestUtils.toHexCode(code);
			System.out.println(hexCodeBytes);
			String hexCodeLongs = TestUtils.toHexCode(TestUtils.toLong(code));
			System.out.println(hexCodeLongs);
			
			new File(INPUT_HELLO_WORLD_TO_FILE_OUTPUT).getParentFile().mkdirs();
			
			long[] commands = TestUtils.toLong(code);
			System.out.println("execute");
			pvm.executeUntilExit(commands, false);
			
			helloWorldIn.close();
			helloWorldIn = new FileInputStream(INPUT_HELLO_WORLD_TO_FILE_OUTPUT);
			byte[] checkBytes = VALUE_HELLO_WORLD_TO_FILE_OUTPUT.getBytes(Charset.defaultCharset());
			byte[] bytes = new byte[checkBytes.length];
			assertEquals(bytes.length, helloWorldIn.read(bytes, 0, bytes.length));
			assertArrayEquals(checkBytes, bytes);
			helloWorldIn.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Check
	public void assembleHelloWorldFromFileTest() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrimitiveAssembler pa = new PrimitiveAssembler(out, null, new Path[] {Paths.get(".")}, false, true);
			InputStream helloWorldIn = getClass().getResourceAsStream(INPUT_HELLO_WORLD_FROM_FILE);
			pa.assemble(pa.preassemble(null, new ANTLRInputStream(new InputStreamReader(helloWorldIn, StandardCharsets.UTF_8)), PrimAsmConstants.START_CONSTANTS,
					new BailErrorStrategy(), false));
			byte[] code = out.toByteArray();
			String hexCodeBytes = TestUtils.toHexCode(code);
			System.out.println(hexCodeBytes);
			String hexCodeLongs = TestUtils.toHexCode(TestUtils.toLong(code));
			System.out.println(hexCodeLongs);
			
			long[] commands = TestUtils.toLong(code);
			System.out.println("execute");
			pvm.executeUntilExit(commands, false);
			
			helloWorldIn.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
