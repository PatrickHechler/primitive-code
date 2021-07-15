package de.hechler.patrick.codesprachen.primitive.compile.objects.num;

public class DeepNum extends Num {
	
	public DeepNum(long num, int numDeep) {
		super(num, numDeep);
	}
	
	public DeepNum(Num inner) {
		super(inner.num, inner.deep + 1);
	}
	
}
