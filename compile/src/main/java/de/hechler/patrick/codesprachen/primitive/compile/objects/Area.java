package de.hechler.patrick.codesprachen.primitive.compile.objects;

public class Area {

	public final long start;
	public final int startDeep;
	public final long len;
	public final int lenDeep;

	public Area(long start, int startDeep, long len, int lenDeep) {
		this.start = start;
		this.startDeep = startDeep;
		this.len = len;
		this.lenDeep = lenDeep;
	}

}
