package de.hechler.patrick.codesprachen.primitive.objects.params;

import de.hechler.patrick.codesprachen.primitive.objects.Num;

public class DeepNum extends Num{

	private DeepNum(long num, int numDeep) {
		super(num, numDeep);
	}
	
	public DeepNum create(long num, int numDeep) {
		if (numDeep < 1 || numDeep > 255) {
			throw new IllegalArgumentException("numDeep is out of supported range: numDeep=" + numDeep + " min=1 max=255");
		}
		return new DeepNum(num, numDeep);
	}
	
}
