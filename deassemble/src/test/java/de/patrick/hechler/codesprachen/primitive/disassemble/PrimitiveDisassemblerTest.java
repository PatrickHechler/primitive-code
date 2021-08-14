package de.patrick.hechler.codesprachen.primitive.disassemble;

import de.hechler.patrick.zeugs.check.BigCheckResult;
import de.hechler.patrick.zeugs.check.Checker;
import de.patrick.hechler.codesprachen.primitive.disassemble.objects.CommandChecker;

public class PrimitiveDisassemblerTest {
	
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		BigCheckResult res = Checker.checkAll(true, CommandChecker.class);
		res.print();
		res.forAllUnexpected((cls,m,t) -> t.printStackTrace());
	}
	
}
