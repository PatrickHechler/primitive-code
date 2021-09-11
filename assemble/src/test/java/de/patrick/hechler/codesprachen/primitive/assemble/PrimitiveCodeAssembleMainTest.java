package de.patrick.hechler.codesprachen.primitive.assemble;

import de.hechler.patrick.codesprachen.primitive.compile.objects.PrimitiveAssemblerChecker;
import de.hechler.patrick.zeugs.check.BigCheckResult;
import de.hechler.patrick.zeugs.check.Checker;

public class PrimitiveCodeAssembleMainTest {
	
	public void testname() throws Exception {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		System.out.println("start checks");
		BigCheckResult checked = Checker.checkAll(true, PrimitiveAssemblerChecker.class);
		System.out.println("finished checks");
		checked.print();
		checked.forAllUnexpectedCheckResults((cls, cr) -> {
			System.err.println("some errors on class: " + cls.getName());
			cr.forAllUnexpected((m, t) -> {
				System.err.println("method: " + m);
				t.printStackTrace(System.err);
			});
		});
		if ( ! checked.allUnexpected().isEmpty()) {
			throw new RuntimeException("not all checks went well!");
		}
	}
	
}
