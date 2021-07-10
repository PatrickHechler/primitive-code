package de.hechler.patrick.codesprachen.primitive.compile.enums;


public enum Commands {
	
	add(1, 1 + 2 + 2 * 8), sub(2, 1 + 2 + 2 * 8), mul(3, 1 + 2 + 2 * 8), div(4, 1 + 2 + 2 * 8),
	
	neg(5, 1 + 1 + 1 * 8),
	
	
	and(6, 1 + 2 + 2 * 8), or(7, 1 + 2 + 2 * 8), xor(8, 1 + 2 + 2 * 8),
	
	not(9, 1 + 1 + 1 * 8),
	
	
	
	push(10, 1 + 1 + 1 * 8), pop(11, 1 + 1 + 1 * 8),
	
	
	
	cmp(12, 1 + 2 + 2 * 8),
	
	
	jmp(13, 1 + 1 * 8), jmpeq(14, 1 + 1 * 8), jmpne(15, 1 + 1 * 8), jmpgt(16, 1 + 1 * 8), jmpge(17, 1 + 1 * 8), jmplo(18, 1 + 1 * 8), jmple(19, 1 + 1 * 8),
	
	
	call(20, 1 + 1 * 8), calleq(21, 1 + 1 * 8), callne(22, 1 + 1 * 8), callgt(23, 1 + 1 * 8), callge(24, 1 + 1 * 8), calllo(25, 1 + 1 * 8), callle(26, 1 + 1 * 8),
	
	
	label( -1, 0),;
	
	public final int nummer;
	public final int length;
	
	
	private Commands(int nummer, int length) {
		this.nummer = nummer;
		this.length = length;
	}
	
}
