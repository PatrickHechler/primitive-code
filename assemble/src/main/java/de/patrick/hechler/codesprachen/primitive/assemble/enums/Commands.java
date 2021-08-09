package de.patrick.hechler.codesprachen.primitive.assemble.enums;


public enum Commands {
	
	//@formatter:off
	CMD_MOV(0x01), CMD_ADD(0x02), CMD_SUB(0x03), CMD_MUL(0x04), CMD_DIV(0x05),
	CMD_AND(0x06), CMD_OR(0x07), CMD_XOR(0x08),
	CMD_NOT(0x09), CMD_NEG(0x0A),
	
	CMD_JMP(0x10), CMD_JMPEQ(0x11), CMD_JMPNE(0x12), CMD_JMPGT(0x13), CMD_JMPGE(0x14), CMD_JMPLO(0x15), CMD_JMPLE(0x16),
	CMD_CALL(0x17), CMD_CALLEQ(0x18), CMD_CALLNE(0x19), CMD_CALLGT(0x1A), CMD_CALLGE(0x1B), CMD_CALLLO(0x1C), CMD_CALLLE(0x1D),
	CMD_SJMP(0x20), CMD_SJMPEQ(0x21), CMD_SJMPNE(0x22), CMD_SJMPGT(0x23), CMD_SJMPGE(0x24), CMD_SJMPLO(0x25), CMD_SJMPLE(0x26),
	CMD_SCALL(0x27), CMD_SCALLEQ(0x28), CMD_SCALLNE(0x29), CMD_SCALLGT(0x2A), CMD_SCALLGE(0x2B), CMD_SCALLLO(0x2C), CMD_SCALLLE(0x2D),
	
	CMD_CMP(0x30),
	CMD_RET(0x31), CMD_IRET(0x32), CMD_INT(0x33),
	CMD_PUSH(0x34), CMD_POP(0x35),
	CMD_SET_IP(0x36);
	//@formatter:on
	
	public final int num;
	
	private Commands(int num) {
		this.num = num;
	}
	
}
