package de.hechler.patrick.codesprachen.primitive.assemble;

import java.nio.file.Files;
import java.nio.file.Paths;

import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssemblerChecker;
import de.hechler.patrick.zeugs.check.BigCheckResult;
import de.hechler.patrick.zeugs.check.Checker;

public class PrimitiveCodeAssembleMainTest {
	
	public void testname() throws Exception {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		System.out.println(
				"[J-LOG]: '" + ".\\src\\test\\resources\\readfiles\\helloworld.txt" + "' exists: " + Files.exists(Paths.get(".\\src\\test\\resources\\readfiles\\helloworld.txt")));
		System.out.println("----- start checks -----");
		BigCheckResult checked = Checker.checkAll(true, PrimitiveAssemblerChecker.class);
		System.out.println("----- finished checks -----");
		checked.print();
		checked.forAllUnexpectedCheckResults((cls, cr) -> {
			System.err.println("some errors on class: " + cls.getName());
			cr.forAllUnexpected((m, t) -> {
				System.err.println("method: " + m);
				t.printStackTrace(System.err);
			});
		});
		if (checked.wentUnexpected()) {
			throw new RuntimeException("not all checks went well!");
		}
		System.out.println("___super finish___");
	}
	
}
