package de.hechler.patrick.codesprachen.primitive.compile.objects;


public class Num {
	
	private final long num;
	private final int art;
	public final int deep;
	
	private Num(long num, int art, int deep) {
		this.num = num;
		this.art = art;
		this.deep = deep;
	}
	
	
	
	public static Num create(Num num) {
		return new Num(num.num, num.art, num.deep + 1);
	}
	
	public static Num create(long num) {
		return new Num(num, 0, 0);
	}
	
	public static Num create(long num, int deep) {
		return new Num(num, 0, deep);
	}
	
	public static Num createAX(int deep) {
		return new Num(0, 1, deep);
	}
	
	public static Num createBX(int deep) {
		return new Num(0, 2, deep);
	}
	
	public static Num createCX(int deep) {
		return new Num(0, 3, deep);
	}
	
	public static Num createDX(int deep) {
		return new Num(0, 4, deep);
	}
	
	
	
	public void checkMDB0() {
		if (deep > 0x1F || deep < 0x00) {
			throw new IllegalStateException("numDeep out of supported range: numDeep=" + deep + " min=0x00 max=0x1F");
		}
	}
	
	public void checkMDB1() {
		if (deep > 0x20 || deep < 0x01) {
			throw new IllegalStateException("numDeep out of supported range: numDeep=" + deep + " min=0x01 max=0x20");
		}
	}
	
	
	public boolean isNum() {
		return art == 0;
	}
	
	public boolean isAX() {
		return art == 1;
	}
	
	public boolean isBX() {
		return art == 2;
	}
	
	public boolean isCX() {
		return art == 3;
	}
	
	public boolean isDX() {
		return art == 4;
	}
	
	public long num() throws IllegalStateException {
		if (art != 0) {
			throw new IllegalStateException("this is no num! this is special reg: " + ('A' - 1 + art) + "X");
		}
		return num;
	}

	public int sr() {
		if (art != 0) {
			throw new IllegalStateException("this is no num! this is no special reg!");
		}
		return art - 1;
	}
	
}
