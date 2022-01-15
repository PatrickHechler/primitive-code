package de.patrick.hechler.codesprachen.primitive.assemble.enums;


public enum Commands {
	
	//@formatter:off
	CMD_MOV(0x01), CMD_ADD(0x02), CMD_SUB(0x03), CMD_MUL(0x04), CMD_DIV(0x05),
	CMD_AND(0x06), CMD_OR(0x07), CMD_XOR(0x08),
	CMD_NOT(0x09), CMD_NEG(0x0A),
	CMD_LSH(0x0B), CMD_RLSH(0x0C), CMD_RASH(0x0D),
	CMD_DEC(0x0E), CMD_INC(0x0F),
	
	
	CMD_JMP(0x10), CMD_JMPEQ(0x11), CMD_JMPNE(0x12), CMD_JMPGT(0x13), CMD_JMPGE(0x14), CMD_JMPLO(0x15), CMD_JMPLE(0x16), CMD_JMPCS(0x17), CMD_JMPCC(0x18),
	
	CMD_CALL(0x20), 
	CMD_CMP(0x21),
	CMD_RET(0x22), CMD_INT(0x23),
	CMD_PUSH(0x24), CMD_POP(0x25),
	CMD_SET_IP(0x26), CMD_SET_SP(0x27),
	CMD_GET_IP(0x28), CMD_GET_SP(0x29),
	CMD_GET_INTS(0x2A), CMD_SET_INTS(0x2B),
	CMD_IRET(0x2C),
	CMD_GET_INTCNT(0x2D), CMD_SET_INTCNT(0x2E),
	
	CMD_ADDC(0x30), CMD_SUBC(0x31),
	
	;
	//@formatter:on
	
	public final int num;
	
	private Commands(int num) {
		this.num = num;
	}
	
}
