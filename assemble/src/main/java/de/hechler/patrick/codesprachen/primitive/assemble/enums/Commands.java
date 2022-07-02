package de.hechler.patrick.codesprachen.primitive.assemble.enums;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.*;

import java.lang.reflect.Field;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands;

public enum Commands {
	
	//@formatter:off
	CMD_MOV(MOV, 2, 1), CMD_ADD(ADD, 2, 1), CMD_SUB(SUB, 2, 1), CMD_MUL(MUL, 2, 1), CMD_DIV(DIV, 2, 2),
	CMD_AND(AND, 2, 1), CMD_OR(OR, 2, 1), CMD_XOR(XOR, 2, 1),
	CMD_NOT(NOT, 1, 1), CMD_NEG(NEG, 1, 1),
	CMD_LSH(LSH, 2, 1), CMD_RLSH(RLSH, 2, 1), CMD_RASH(RASH, 2, 1),
	CMD_DEC(DEC, 1, 1), CMD_INC(INC, 1, 1),
	
	CMD_JMP(JMP, 1, -1), CMD_JMPEQ(JMPEQ, 1, -1), CMD_JMPNE(JMPNE, 1, -1), CMD_JMPGT(JMPGT, 1, -1), CMD_JMPGE(JMPGE, 1, -1), CMD_JMPLT(JMPLT, 1, -1), CMD_JMPLE(JMPLE, 1, -1),
	CMD_JMPCS(JMPCS, 1, -1), CMD_JMPCC(JMPCC, 1, -1), CMD_JMPZS(JMPZS, 1, -1), CMD_JMPZC(JMPZC, 1, -1), CMD_JMPNAN(JMPNAN, 1, -1), CMD_JMPAN(JMPAN, 1, -1),
	CMD_JMPAB(JMPAB, 1, -1), CMD_JMPSB(JMPSB, 1, -1), CMD_JMPBB(JMPNB, 1, -1),
	
	CMD_CALL(CALL, 1, -1),
	CMD_CMP(CMP, 2, 0),
	CMD_RET(RET, 0, 0), CMD_INT(INT, 1, 0),
	CMD_PUSH(PUSH, 1, 0), CMD_POP(POP, 1, 1),
	CMD_IRET(IRET, 0, 0),
	CMD_SWAP(SWAP, 2, 2), CMD_LEA(LEA, 2, 1), CMD_MVAD(MVAD, 3, 1),
	CMD_CALO(CALO, 2, -1),
	CMD_BCP(BCP, 2, 0), CMD_CMPFP(CMPFP, 2, 0), CMD_CHKFP(CHKFP, 1, 0),
	
	CMD_ADDC(ADDC, 2, 1), CMD_SUBC(SUBC, 2, 1),
	CMD_ADDFP(ADDFP, 2, 1), CMD_SUBFP(SUBFP, 2, 1), CMD_MULFP(MULFP, 2, 1), CMD_DIVFP(DIVFP, 2, 1),
	CMD_NTFP(NTFP, 1, 1), CMD_FPTN(FPTN, 1, 1),
	CMD_UDIV(UDIV, 2, 1),
	CMD_MVB(MVB, 2, 1), CMD_MVW(MVW, 2, 1), CMD_MVDW(MVDW, 2, 1),
	;//@formatter:on
	
	public final int num;
	public final int params;
	public final int nokonstParams;
	
	private Commands(int num, int params, int nokonstParams) {
		this.num = num;
		this.params = params;
		this.nokonstParams = nokonstParams;
	}
	
	@Override
	public String toString() {
		return name().substring(4);
	}
	
	static {
		Field[] fields = PrimAsmCommands.class.getFields();
		if (values().length != fields.length) {
			throw new AssertionError("I do not have the same amount of commands (my-count=" + values().length + " should-be=" + fields.length);
		}
		for (Field field : fields) {
			Commands val = valueOf("CMD_" + field.getName());
			try {
				if (val.num != field.getInt(null)) {
					throw new AssertionError("I have an illegal value: command: " + field.getName() + " my-num=" + Integer.toHexString(val.num) + " other-num="
							+ Integer.toHexString(field.getInt(null)));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InternalError(e);
			}
		}
	}
	
}
