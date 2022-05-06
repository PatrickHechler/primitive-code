package de.patrick.hechler.codesprachen.primitive.asmeditor.objects;

import java.awt.Color;

public class HL {
	
	public final Color val;
	public final boolean ishp;
	public final int start;
	public final int stop;
	
	public HL(Color val, boolean ishp, int startIndex, int stopIndex) {
		this.val = val;
		this.ishp = ishp;
		this.start = startIndex;
		this.stop = stopIndex;
	}
	
}
