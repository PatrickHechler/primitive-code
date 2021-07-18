package de.hechler.patrick.codesprachen.primitive.compile.objects;

public class Wert {
	
	private final String str;
	private final Num num;
	
	
	
	public static Wert createNumber(Num val) {
		return new Wert(null, val);
	}
	
	public static Wert createConstantString(String val) {
		if (val == null) {
			throw new NullPointerException("can't create a null string Wert");
		}
		return new Wert(val, null);
	}
	
	private Wert(String str, Num num) {
		this.str = str;
		this.num = num;
	}
	
	
	
	public boolean isConstStr() {
		return str != null;
	}
	
	public boolean isNum() {
		return num != null;
	}
	
	public String getConstStr() {
		if (str == null) {
			throw new IllegalStateException("this is no CONST_STR");
		}
		return str;
	}
	
	public Num getNum() {
		if (num == null) {
			throw new IllegalStateException("this is no NUM");
		}
		return num;
	}
	
	@Override
	public String toString() {
		if (str != null) {
			return "CONST_STR[" + str + "]";
		} else if (num != null) {
			return "NUM: " + num;
		} else {
			throw new IllegalStateException("unbekannter Wert!");
		}
	}
	
}

