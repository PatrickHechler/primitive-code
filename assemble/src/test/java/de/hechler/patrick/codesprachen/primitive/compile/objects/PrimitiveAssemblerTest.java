package de.hechler.patrick.codesprachen.primitive.compile.objects;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hechler.patrick.codesprachen.primitive.runtime.objects.PrimitiveVirtualMashine;
import de.hechler.patrick.zeugs.check.CheckResult;
import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.PrimitiveAssembler;

@CheckClass
public class PrimitiveAssemblerTest extends Checker {
	
	public final static String INPUT_HELLO_WORLD = "/sourcecode/helloworld.pcs"; 
	
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
			pvm.execute(commands);
			long cx = pvm.getCX();
			assertEquals(ax+bx, cx);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		CheckResult check = Checker.check(PrimitiveAssemblerTest.class);
		if (check.wentUnexpected()) {
			check.print();
			check.forAllUnexpected((m, t) -> {
				System.err.println(m.getName()+":");
				t.printStackTrace();			
			}); 
		}
	}
}
