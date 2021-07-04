package de.hechler.patrick.codesprachen.primitive.objects;


public class Num {
	
	public final long num;
	public final int numDeep;
	
	public Num(long num, int numDeep) {
		this.num = num;
		this.numDeep = numDeep;
	}
	
	
	
	public void checkMDB0() {
		if (numDeep > 255 || numDeep < 0) {
			throw new IllegalStateException("numDeep out of supported range: numDeep=" + numDeep + " min=0 max=255");
		}
	}
	
	public void checkMDB1() {
		if (numDeep > 256 || numDeep < 1) {
			throw new IllegalStateException("numDeep out of supported range: numDeep=" + numDeep + " min=1 max=256");
		}
	}
	
}
