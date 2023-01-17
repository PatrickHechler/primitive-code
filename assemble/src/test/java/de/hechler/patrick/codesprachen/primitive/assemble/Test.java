package de.hechler.patrick.codesprachen.primitive.assemble;

import de.hechler.patrick.zeugs.check.objects.BigCheckResult;
import de.hechler.patrick.zeugs.check.objects.BigChecker;

public class Test {
	
	public void testname() throws Exception { main(new String[0]); }
	
	public static void main(String[] args) {
		BigCheckResult res = BigChecker.tryCheckAll(true, Test.class.getPackage(), Test.class.getClassLoader());
		res.print();
		if (res.wentUnexpected()) {
			res.detailedPrint();
			throw new AssertionError(res);
		}
	}
	
}
