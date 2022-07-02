package de.hechler.patrick.codesprachen.primitive.disassemble.enums;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.*;

import java.lang.reflect.Field;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands;
import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;

public enum Commands {
	
	CMD_MOV(ParamArt.twoParamsP1NoConstP2AllowConst, MOV), CMD_ADD(ParamArt.twoParamsP1NoConstP2AllowConst, ADD),
	CMD_SUB(ParamArt.twoParamsP1NoConstP2AllowConst, SUB), CMD_MUL(ParamArt.twoParamsP1NoConstP2AllowConst, MUL),
	CMD_DIV(ParamArt.twoParamsNoConsts, DIV), CMD_AND(ParamArt.twoParamsP1NoConstP2AllowConst, AND),
	CMD_OR(ParamArt.twoParamsP1NoConstP2AllowConst, OR), CMD_XOR(ParamArt.twoParamsP1NoConstP2AllowConst, XOR),
	CMD_NOT(ParamArt.oneParamNoConst, NOT), CMD_NEG(ParamArt.oneParamNoConst, NEG),
	CMD_LSH(ParamArt.twoParamsP1NoConstP2AllowConst, LSH), CMD_RLSH(ParamArt.twoParamsP1NoConstP2AllowConst, RLSH),
	CMD_RASH(ParamArt.twoParamsP1NoConstP2AllowConst, RASH), CMD_DEC(ParamArt.oneParamNoConst, DEC),
	CMD_INC(ParamArt.oneParamNoConst, INC),
	
	CMD_JMP(ParamArt.label, JMP), CMD_JMPEQ(ParamArt.label, JMPEQ), CMD_JMPNE(ParamArt.label, JMPNE),
	CMD_JMPGT(ParamArt.label, JMPGT), CMD_JMPGE(ParamArt.label, JMPGE), CMD_JMPLT(ParamArt.label, JMPLT),
	CMD_JMPLE(ParamArt.label, JMPLE), CMD_JMPCS(ParamArt.label, JMPCS), CMD_JMPCC(ParamArt.label, JMPCC),
	CMD_JMPZS(ParamArt.label, JMPZS), CMD_JMPZC(ParamArt.label, JMPZC), CMD_JMPNAN(ParamArt.label, JMPNAN),
	CMD_JMPAN(ParamArt.label, JMPAN), CMD_JMPAB(ParamArt.label, JMPAB), CMD_JMPSB(ParamArt.label, JMPSB),
	CMD_JMPNB(ParamArt.label, JMPNB),
	
	CMD_CALL(ParamArt.label, CALL), CMD_CMP(ParamArt.twoParamsAllowConsts, CMP), CMD_RET(ParamArt.noParams, RET),
	CMD_INT(ParamArt.oneParamAllowConst, INT), CMD_PUSH(ParamArt.oneParamAllowConst, PUSH),
	CMD_POP(ParamArt.oneParamNoConst, POP), CMD_IRET(ParamArt.noParams, IRET), CMD_SWAP(ParamArt.twoParamsNoConsts, SWAP),
	CMD_LEA(ParamArt.twoParamsP1NoConstP2AllowConst, LEA), CMD_MVAD(ParamArt.threeParamsP1NoConstP2AllowConstP3CompileConst, MVAD),
	CMD_CALO(ParamArt.twoParamsP1NoConstP2CompileConst, CALO), CMD_BCP(ParamArt.twoParamsAllowConsts, BCP),
	CMD_CMPFP(ParamArt.twoParamsAllowConsts, CMPFP), CMD_CHKFP(ParamArt.oneParamAllowConst, CHKFP),
	
	CMD_ADDC(ParamArt.twoParamsP1NoConstP2AllowConst, ADDC), CMD_SUBC(ParamArt.twoParamsP1NoConstP2AllowConst, SUBC),
	CMD_ADDFP(ParamArt.twoParamsP1NoConstP2AllowConst, ADDFP), CMD_SUBFP(ParamArt.twoParamsP1NoConstP2AllowConst, SUBFP),
	CMD_MULFP(ParamArt.twoParamsP1NoConstP2AllowConst, MULFP), CMD_DIVFP(ParamArt.twoParamsP1NoConstP2AllowConst, DIVFP),
	CMD_NTFP(ParamArt.oneParamNoConst, NTFP), CMD_FPTN(ParamArt.oneParamAllowConst, FPTN), CMD_UDIV(ParamArt.twoParamsNoConsts, UDIV),
	CMD_MVB(ParamArt.twoParamsP1NoConstP2AllowConst, MVB), CMD_MVW(ParamArt.twoParamsP1NoConstP2AllowConst, MVW),
	CMD_MVDW(ParamArt.twoParamsP1NoConstP2AllowConst, MVDW),
	
	;
	
	public final ParamArt art;
	public final int      num;
	
	private Commands(ParamArt art, int num) {
		this.art = art;
		this.num = num;
	}
	
	public enum ParamArt {
		
		noParams,
		
		label,
		
		oneParamNoConst, oneParamAllowConst,
		
		twoParamsNoConsts, twoParamsP1NoConstP2AllowConst, twoParamsAllowConsts, twoParamsP1NoConstP2CompileConst,
		
		threeParamsP1NoConstP2AllowConstP3CompileConst,
	
	}
	
	@Override
	public String toString() {
		return name().substring(4);
	}
	
	public int num() {
		return this.num;
	}
	
	// @formatter:off
	private static final Commands[] COMMANDS = new Commands[] {
	  null,    CMD_MOV,  CMD_ADD,  CMD_SUB,  CMD_MUL,  CMD_DIV,  CMD_AND,  CMD_OR,   CMD_XOR,  CMD_NOT,  CMD_NEG,  CMD_LSH,   CMD_RLSH, CMD_RASH, CMD_DEC,  CMD_INC,
	  CMD_JMP, CMD_JMPEQ,CMD_JMPNE,CMD_JMPGT,CMD_JMPGE,CMD_JMPLT,CMD_JMPLE,CMD_JMPCS,CMD_JMPCC,CMD_JMPZS,CMD_JMPZC,CMD_JMPNAN,CMD_JMPAN,CMD_JMPAB,CMD_JMPSB,CMD_JMPNB,
	  CMD_CALL,CMD_CMP,  CMD_RET,  CMD_INT,  CMD_PUSH, CMD_POP,  CMD_IRET, CMD_SWAP, CMD_LEA,  CMD_MVAD, CMD_CALO, CMD_BCP,   CMD_CMPFP,CMD_CHKFP,null,     null,
	  CMD_ADDC,CMD_SUBC, CMD_ADDFP,CMD_SUBFP,CMD_MULFP,CMD_DIVFP,CMD_NTFP, CMD_FPTN, CMD_UDIV, CMD_MVB,  CMD_MVW,  CMD_MVDW,  null,     null,     null,     null,
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	  null,    null,     null,     null,     null,     null,     null,     null,     null,      null,    null,     null,      null,     null,     null,     null, 
	}; // @formatter:on
	
	static {
		if (COMMANDS.length != 256) {
			throw new AssertionError("commands length != 256: " + COMMANDS.length);
		}
		Field[] fields = PrimAsmCommands.class.getFields();
		if (fields.length != values().length) {
			throw new AssertionError("illegal command count! should be: " + fields.length + ", but my command count is " + values().length + '!');
		}
		for (Field field : fields) {
			try {
				int val = field.getInt(null);
				Commands cmd = COMMANDS[val];
				if (cmd == null) {
					throw new AssertionError("cmd[" + val + "]: " + field.getName() + " cmd=null");
				}
				if (cmd.num != val) {
					throw new AssertionError("cmd[" + val + "]: " + field.getName() + " cmd.num=" + cmd.num + " cmd.name=" + cmd.name());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InternalError(e);
			}
		}
	}
	
	public static Commands get(byte b) throws NoCommandException {
		Commands cmd = COMMANDS[0xFF & b];
		if (cmd == null) {
			throw new NoCommandException("this byte does not show a command! byte=0x" + Integer.toHexString(0xFF & b));
		}
		return cmd;
	}
	
}
