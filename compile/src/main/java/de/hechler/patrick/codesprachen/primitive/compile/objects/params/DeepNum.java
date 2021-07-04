package de.hechler.patrick.codesprachen.primitive.compile.objects.params;

import de.hechler.patrick.codesprachen.primitive.compile.objects.Num;

public class DeepNum extends Num {
	
	private DeepNum(long num, int numDeep) {
		super(num, numDeep);
	}
	
	public static DeepNum create(long num, int numDeep) {
		if (numDeep < 1 || numDeep > 256) {
			throw new IllegalArgumentException("numDeep is out of supported range: numDeep=" + numDeep + " min=1 max=256");
		}
		return new DeepNum(num, numDeep);
	}
	
}
