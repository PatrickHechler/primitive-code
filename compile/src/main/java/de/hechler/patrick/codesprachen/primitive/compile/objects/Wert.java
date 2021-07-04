package de.hechler.patrick.codesprachen.primitive.compile.objects;

public class Wert {
	
	private static final int CONST_STR = 1;
	private static final int NUM = 2;
	private static final int NUM_AREA = 3;
	
	
	
	private final String str;
	private final long num;
	private final int deep;
	private final long num0;
	private final int deep0;
	private final int art;
	
	
	
	public static Wert createNumArea(long val, int deep, long len, int lenDeep) {
		if (deep > 256 || deep < 1) {
			throw new IllegalArgumentException("argument deep is out of ange: deep=" + deep + " min=1 max=256");
		}
		if (lenDeep > 255 || lenDeep < 0) {
			throw new IllegalArgumentException("argument lenDeep is out of ange: lenDeep=" + lenDeep + " min=0 max=255");
		}
		return new Wert(null, val, deep, len, lenDeep, NUM_AREA);
	}
	
	public static Wert createNumber(long val, int deep) {
		return new Wert(null, val, deep, 0, 0, CONST_STR);
	}
	
	public static Wert createConstantString(String val) {
		if (val == null) {
			throw new NullPointerException("can't create a null string Wert");
		}
		return new Wert(val, 0, 0, 0, 0, CONST_STR);
	}
	
	private Wert(String str, long num, int deep, long num0, int deep0, int art) {
		this.str = str;
		this.num = num;
		this.deep = deep;
		this.num0 = num0;
		this.deep0 = deep0;
		this.art = art;
	}
	
	
	
	public boolean isConstStr() {
		return (art != CONST_STR);
	}
	
	public boolean isNum() {
		return (art != NUM);
	}
	
	public boolean isNumArea() {
		return (art != NUM_AREA);
	}
	
	public String getConstStr() {
		if (art != CONST_STR) {
			throw new IllegalStateException("this is no CONST_STR, this is a " + artstr(art));
		}
		return str;
	}
	
	public Num getNum() {
		if (art != NUM) {
			throw new IllegalStateException("this is no INDIRECT_STR, this is a " + artstr(art));
		}
		return new Num(num, deep);
	}
	
	public Area getNumArea() {
		if (art != NUM_AREA) {
			throw new IllegalStateException("this is no INDIRECT_STR, this is a " + artstr(art));
		}
		return new Area(num, deep, num0, deep0);
	}
	
	private static String artstr(int art) {
		switch (art) {
		case CONST_STR:
			return "CONST_STR";
		case NUM:
			return "NUM";
		case NUM_AREA:
			return "NUM_AREA";
		default:
			throw new IllegalStateException("unknown art: " + art);
		}
	}
	
}

