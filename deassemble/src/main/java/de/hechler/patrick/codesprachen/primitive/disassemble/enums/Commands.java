package de.hechler.patrick.codesprachen.primitive.disassemble.enums;

import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;

public enum Commands {
	
	//@formatter:off
	CMD_MOV(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.MOV),
	CMD_ADD(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.ADD), CMD_SUB(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.SUB),
	CMD_MUL(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.MUL), CMD_DIV(ParamArt.twoParamsNoConsts, CmdNums.DIV),
	CMD_AND(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.AND), CMD_OR(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.OR), CMD_XOR(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.XOR),
	CMD_NOT(ParamArt.oneParamNoConst, CmdNums.NOT), CMD_NEG(ParamArt.oneParamNoConst, CmdNums.NEG),
	CMD_LSH(ParamArt.oneParamNoConst, CmdNums.LSH), CMD_RLSH(ParamArt.oneParamNoConst, CmdNums.RLSH), CMD_RASH(ParamArt.oneParamNoConst, CmdNums.RASH),
	CMD_DEC(ParamArt.oneParamNoConst, CmdNums.DEC), CMD_INC(ParamArt.oneParamNoConst, CmdNums.INC),
	
	
	CMD_JMP(ParamArt.label, CmdNums.JMP), CMD_JMPEQ(ParamArt.label, CmdNums.JMPEQ), CMD_JMPNE(ParamArt.label, CmdNums.JMPNE), CMD_JMPGT(ParamArt.label, CmdNums.JMPGT),
	CMD_JMPGE(ParamArt.label, CmdNums.JMPGE), CMD_JMPLT(ParamArt.label, CmdNums.JMPLT), CMD_JMPLE(ParamArt.label, CmdNums.JMPLE),
	CMD_JMPCS(ParamArt.label, CmdNums.JMPCS), CMD_JMPCC(ParamArt.label, CmdNums.JMPCC), CMD_JMPZS(ParamArt.label, CmdNums.JMPZS), CMD_JMPZC(ParamArt.label, CmdNums.JMPZC),
	
	CMD_CALL(ParamArt.label, CmdNums.CALL),
	
	CMD_CMP(ParamArt.twoParamsAllowConsts, CmdNums.CMP),
	CMD_RET(ParamArt.noParams, CmdNums.RET), CMD_INT(ParamArt.oneParamAllowConst, CmdNums.INT),
	CMD_PUSH(ParamArt.oneParamAllowConst, CmdNums.PUSH), CMD_POP(ParamArt.oneParamNoConst, CmdNums.POP),
	CMD_SET_IP(ParamArt.oneParamAllowConst, CmdNums.SET_IP), CMD_SET_SP(ParamArt.oneParamAllowConst, CmdNums.SET_SP),
	CMD_GET_IP(ParamArt.oneParamNoConst, CmdNums.GET_IP), CMD_GET_SP(ParamArt.oneParamNoConst, CmdNums.GET_SP),
	CMD_GET_INTS(ParamArt.oneParamNoConst, CmdNums.GET_INTS),
	CMD_SET_INTS(ParamArt.oneParamAllowConst, CmdNums.SET_INTS),
	CMD_IRET(ParamArt.noParams, CmdNums.IRET),
	CMD_GET_INTCNT(ParamArt.oneParamNoConst, CmdNums.GET_INTCNT), CMD_SET_INTCNT(ParamArt.oneParamAllowConst, CmdNums.SET_INTCNT),
	CMD_SWAP(ParamArt.twoParamsNoConsts, CmdNums.SWAP),
	
	
	CMD_ADDC(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.ADDC), CMD_SUBC(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.SUBC),
	CMD_ADDFP(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.ADDFP), CMD_SUBFP(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.SUBFP),
	CMD_MULFP(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.MULFP), CMD_DIVFP(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.DIVFP),
	CMD_NTFP(ParamArt.oneParamNoConst, CmdNums.NTFP), CMD_FPTN(ParamArt.oneParamNoConst, CmdNums.FPTN),
	
	;
	//@formatter:on
	
	public final ParamArt art;
	public final int num;
	
	private Commands(ParamArt art, int num) {
		this.art = art;
		this.num = num;
	}
	
	public enum ParamArt {
		noParams,
		
		label,
		
		oneParamNoConst, oneParamAllowConst,
		
		twoParamsNoConsts, twoParamsP1NoConstP2AllowConst, twoParamsAllowConsts
	}
	
	@Override
	public String toString() {
		return name().substring(4);
	}
	
	public int num() {
		return this.num;
	}
	
	public static Commands get(byte b) throws NoCommandException {
		switch (0xFF & b) {
		case CmdNums.MOV:
			return Commands.CMD_MOV;
		case CmdNums.ADD:
			return Commands.CMD_ADD;
		case CmdNums.SUB:
			return Commands.CMD_SUB;
		case CmdNums.MUL:
			return Commands.CMD_MUL;
		case CmdNums.DIV:
			return Commands.CMD_DIV;
		case CmdNums.AND:
			return Commands.CMD_AND;
		case CmdNums.OR:
			return Commands.CMD_OR;
		case CmdNums.XOR:
			return Commands.CMD_XOR;
		case CmdNums.NOT:
			return Commands.CMD_NOT;
		case CmdNums.NEG:
			return Commands.CMD_NEG;
		case CmdNums.LSH:
			return Commands.CMD_LSH;
		case CmdNums.RLSH:
			return Commands.CMD_RLSH;
		case CmdNums.RASH:
			return Commands.CMD_RASH;
		case CmdNums.DEC:
			return Commands.CMD_DEC;
		case CmdNums.INC:
			return Commands.CMD_INC;
		case CmdNums.JMP:
			return Commands.CMD_JMP;
		case CmdNums.JMPEQ:
			return Commands.CMD_JMPEQ;
		case CmdNums.JMPNE:
			return Commands.CMD_JMPNE;
		case CmdNums.JMPGT:
			return Commands.CMD_JMPGT;
		case CmdNums.JMPGE:
			return Commands.CMD_JMPGE;
		case CmdNums.JMPLT:
			return Commands.CMD_JMPLT;
		case CmdNums.JMPLE:
			return Commands.CMD_JMPLE;
		case CmdNums.JMPCS:
			return Commands.CMD_JMPCS;
		case CmdNums.JMPCC:
			return Commands.CMD_JMPCC;
		case CmdNums.JMPZS:
			return Commands.CMD_JMPZS;
		case CmdNums.JMPZC:
			return Commands.CMD_JMPZC;
		case CmdNums.CALL:
			return Commands.CMD_CALL;
		case CmdNums.CMP:
			return Commands.CMD_CMP;
		case CmdNums.RET:
			return Commands.CMD_RET;
		case CmdNums.INT:
			return Commands.CMD_INT;
		case CmdNums.PUSH:
			return Commands.CMD_PUSH;
		case CmdNums.POP:
			return Commands.CMD_POP;
		case CmdNums.SET_IP:
			return Commands.CMD_SET_IP;
		case CmdNums.SET_SP:
			return Commands.CMD_SET_SP;
		case CmdNums.GET_IP:
			return Commands.CMD_GET_IP;
		case CmdNums.GET_SP:
			return Commands.CMD_GET_SP;
		case CmdNums.GET_INTS:
			return Commands.CMD_GET_INTS;
		case CmdNums.SET_INTS:
			return Commands.CMD_SET_INTS;
		case CmdNums.IRET:
			return Commands.CMD_IRET;
		case CmdNums.GET_INTCNT:
			return Commands.CMD_GET_INTCNT;
		case CmdNums.SET_INTCNT:
			return Commands.CMD_SET_INTCNT;
		case CmdNums.SWAP:
			return Commands.CMD_SWAP;
		case CmdNums.ADDC:
			return Commands.CMD_ADDC;
		case CmdNums.SUBC:
			return Commands.CMD_SUBC;
		case CmdNums.ADDFP:
			return Commands.CMD_ADDFP;
		case CmdNums.SUBFP:
			return Commands.CMD_SUBFP;
		case CmdNums.MULFP:
			return Commands.CMD_MULFP;
		case CmdNums.DIVFP:
			return Commands.CMD_DIVFP;
		case CmdNums.NTFP:
			return Commands.CMD_NTFP;
		case CmdNums.FPTN:
			return Commands.CMD_FPTN;
		}
		throw new NoCommandException("this byte does not show a command! byte=0x" + Integer.toHexString(0xFF & b));
	}
	
}
