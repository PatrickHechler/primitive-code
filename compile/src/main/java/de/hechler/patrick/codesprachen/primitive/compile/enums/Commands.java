package de.hechler.patrick.codesprachen.primitive.compile.enums;


public enum Commands {
	
	mov(0x01, 1 + 2 + 2 * 8),
	
	add(0x02, 1 + 2 + 2 * 8), sub(0x03, 1 + 2 + 2 * 8), mul(0x04, 1 + 2 + 2 * 8), div(0x05, 1 + 2 + 2 * 8),
	
	neg(0x06, 1 + 1 + 1 * 8),
	
	
	and(0x07, 1 + 2 + 2 * 8), or(0x08, 1 + 2 + 2 * 8), xor(0x09, 1 + 2 + 2 * 8),
	
	not(0x0A, 1 + 1 + 1 * 8),
	
	
	cmp(0x10, 1 + 2 + 2 * 8),
	
	
	jmp(0x11, 1 + 1 * 8), jmpeq(0x12, 1 + 1 * 8), jmpne(0x13, 1 + 1 * 8), jmpgt(0x14, 1 + 1 * 8), jmpge(0x15, 1 + 1 * 8), jmplo(0x16, 1 + 1 * 8), jmple(0x17, 1 + 1 * 8),
	
	
	push(0x20, 1 + 1 + 1 * 8), pop(0x21, 1 + 1 + 1 * 8),
	
	call(0x22, 1 + 1 * 8), calleq(0x23, 1 + 1 * 8), callne(0x24, 1 + 1 * 8), callgt(0x25, 1 + 1 * 8), callge(0x26, 1 + 1 * 8), calllo(0x27, 1 + 1 * 8), callle(0x28, 1 + 1 * 8),
	
	ret(0x29, 1),
	
	
	label( -1, 0),;
	
	
	
	public final int nummer;
	public final int length;
	
	
	
	private Commands(int nummer, int length) {
		this.nummer = nummer;
		this.length = length;
	}
	
}
