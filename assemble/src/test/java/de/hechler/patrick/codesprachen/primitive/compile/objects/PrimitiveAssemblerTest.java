package de.hechler.patrick.codesprachen.primitive.compile.objects;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import de.hechler.patrick.codesprachen.primitive.runtime.objects.PrimitiveVirtualMashine;
import de.hechler.patrick.zeugs.check.CheckResult;
import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.PrimitiveAssembler;

@CheckClass
public class PrimitiveAssemblerTest extends Checker {
	
	public final static String INPUT_ADD = "/sourcecode/add.pcs";
	public final static String INPUT_HELLO_WORLD = "/sourcecode/helloworld.pcs";
	public final static String INPUT_HELLO_WORLD_TO_FILE = "/sourcecode/helloworldtofile.pcs";
	public final static String INPUT_HELLO_WORLD_TO_FILE_OUTPUT = "C:\\Users\\Patrick\\git\\primitive-code\\assemble\\target\\testout\\out.txt";
	public final static String VALUE_HELLO_WORLD_TO_FILE_OUTPUT = "hello file world";
	public final static String INPUT_HELLO_WORLD_FROM_FILE = "/sourcecode/helloworldfromfile.pcs";
	
	@Check
	public void assembleAddTest() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrimitiveAssembler pa = new PrimitiveAssembler(out);
			InputStream helloWorldIn = getClass().getResourceAsStream(INPUT_ADD);
			pa.assemble(helloWorldIn);
			byte[] code = out.toByteArray();
			String hexCodeBytes = TestUtils.toHexCode(code);
			System.out.println(hexCodeBytes);
			String hexCodeLongs = TestUtils.toHexCode(TestUtils.toLong(code));
			System.out.println(hexCodeLongs);
			
			PrimitiveVirtualMashine pvm = new PrimitiveVirtualMashine();
			long[] commands = TestUtils.toLong(code);
			long ax = 5L;
			long bx = 30L;
			pvm.setAX(ax);
			pvm.setBX(bx);
			System.out.println("execute");
			pvm.execute(commands);
			long cx = pvm.getCX();
			assertEquals(ax + bx, cx);
			System.out.println("CX=" + cx);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Check
	public void assembleHelloWorldTest() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrimitiveAssembler pa = new PrimitiveAssembler(out);
			InputStream helloWorldIn = getClass().getResourceAsStream(INPUT_HELLO_WORLD);
			pa.assemble(helloWorldIn);
			byte[] code = out.toByteArray();
			String hexCodeBytes = TestUtils.toHexCode(code);
			System.out.println(hexCodeBytes);
			String hexCodeLongs = TestUtils.toHexCode(TestUtils.toLong(code));
			System.out.println(hexCodeLongs);
			
			PrimitiveVirtualMashine pvm = new PrimitiveVirtualMashine();
			long[] commands = TestUtils.toLong(code);
			long ax = 5L;
			long bx = 30L;
			pvm.setAX(ax);
			pvm.setBX(bx);
			System.out.println("execute");
			pvm.execute(commands);
			long cx = pvm.getCX();
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
			PrimitiveAssembler pa = new PrimitiveAssembler(out);
			InputStream helloWorldIn = getClass().getResourceAsStream(INPUT_HELLO_WORLD_TO_FILE);
			pa.assemble(helloWorldIn);
			byte[] code = out.toByteArray();
			String hexCodeBytes = TestUtils.toHexCode(code);
			System.out.println(hexCodeBytes);
			String hexCodeLongs = TestUtils.toHexCode(TestUtils.toLong(code));
			System.out.println(hexCodeLongs);
			
			PrimitiveVirtualMashine pvm = new PrimitiveVirtualMashine();
			long[] commands = TestUtils.toLong(code);
			System.out.println("execute");
			pvm.execute(commands);
			
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
			PrimitiveAssembler pa = new PrimitiveAssembler(out);
			InputStream helloWorldIn = getClass().getResourceAsStream(INPUT_HELLO_WORLD_FROM_FILE);
			pa.assemble(helloWorldIn);
			byte[] code = out.toByteArray();
			String hexCodeBytes = TestUtils.toHexCode(code);
			System.out.println(hexCodeBytes);
			String hexCodeLongs = TestUtils.toHexCode(TestUtils.toLong(code));
			System.out.println(hexCodeLongs);
			
			PrimitiveVirtualMashine pvm = new PrimitiveVirtualMashine();
			long[] commands = TestUtils.toLong(code);
			System.out.println("execute");
			pvm.execute(commands);
			
			helloWorldIn.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		CheckResult check = Checker.check(PrimitiveAssemblerTest.class);
		check.print();
		if (check.wentUnexpected()) {
			check.forAllUnexpected((m, t) -> {
				System.err.println(m.getName() + ":");
				t.printStackTrace();
			});
		}
	}
	
}
