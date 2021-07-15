package de.hechler.patrick.codesprachen.primitive.compile.objects.num;


public class Num {
	
	public final long num;
	public final int deep;
	
	public Num(long num, int deep) {
		this.num = num;
		this.deep = deep;
	}
	
	
	
	public void checkMDB0() {
		if (deep > 0xFF || deep < 0x00) {
			throw new IllegalStateException("numDeep out of supported range: numDeep=" + deep + " min=0 max=255");
		}
	}
	
	public void checkMDB1() {
		if (deep > 0x100 || deep < 0x01) {
			throw new IllegalStateException("numDeep out of supported range: numDeep=" + deep + " min=1 max=256");
		}
	}
	
}
