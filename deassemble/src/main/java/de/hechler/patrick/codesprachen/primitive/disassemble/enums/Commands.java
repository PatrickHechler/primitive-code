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
	CMD_JMPGE(ParamArt.label, CmdNums.JMPGE), CMD_JMPLO(ParamArt.label, CmdNums.JMPLO), CMD_JMPLE(ParamArt.label, CmdNums.JMPLE), CMD_JMPCS(ParamArt.label, CmdNums.JMPCS), CMD_JMPCC(ParamArt.label, CmdNums.JMPCC),
	
	CMD_CALL(ParamArt.label, CmdNums.CALL),
	
	CMD_CMP(ParamArt.twoParamsAllowConsts, CmdNums.CMP),
	CMD_RET(ParamArt.noParams, CmdNums.RET), CMD_INT(ParamArt.oneParamAllowConst, CmdNums.INT),
	CMD_PUSH(ParamArt.oneParamAllowConst, CmdNums.PUSH), CMD_POP(ParamArt.oneParamNoConst, CmdNums.POP),
	CMD_SET_IP(ParamArt.oneParamAllowConst, CmdNums.SET_IP), CMD_SET_SP(ParamArt.oneParamAllowConst, CmdNums.SET_SP),
	CMD_GET_IP(ParamArt.oneParamNoConst, CmdNums.GET_IP), CMD_GET_SP(ParamArt.oneParamNoConst, CmdNums.GET_SP),
	CMD_GET_INTS(ParamArt.oneParamNoConst, CmdNums.GET_INTS),
	CMD_SET_INTS(ParamArt.oneParamAllowConst, CmdNums.SET_INTS),
	CMD_IRET(ParamArt.oneParamAllowConst, CmdNums.IRET),
	CMD_GET_INTCNT(ParamArt.oneParamNoConst, CmdNums.GET_INTCNT), CMD_SET_INTCNT(ParamArt.oneParamAllowConst, CmdNums.SET_INTCNT),
	
	
	CMD_ADDC(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.ADDC), CMD_SUBC(ParamArt.twoParamsP1NoConstP2AllowConst, CmdNums.SUBC),
	
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
		switch (b & 0xFF) {
		case CmdNums.MOV:
			return CMD_MOV;
		case CmdNums.ADD:
			return CMD_ADD;
		case CmdNums.SUB:
			return CMD_SUB;
		case CmdNums.MUL:
			return CMD_MUL;
		case CmdNums.DIV:
			return CMD_DIV;
		case CmdNums.AND:
			return CMD_AND;
		case CmdNums.OR:
			return CMD_OR;
		case CmdNums.XOR:
			return CMD_XOR;
		case CmdNums.NOT:
			return CMD_NOT;
		case CmdNums.NEG:
			return CMD_NEG;
		case CmdNums.LSH:
			return CMD_LSH;
		case CmdNums.RLSH:
			return CMD_RLSH;
		case CmdNums.RASH:
			return CMD_RASH;
		case CmdNums.JMP:
			return CMD_JMP;
		case CmdNums.JMPCS:
			return CMD_JMPCS;
		case CmdNums.JMPCC:
			return CMD_JMPCC;
		case CmdNums.JMPEQ:
			return CMD_JMPEQ;
		case CmdNums.JMPNE:
			return CMD_JMPNE;
		case CmdNums.JMPGT:
			return CMD_JMPGT;
		case CmdNums.JMPGE:
			return CMD_JMPGE;
		case CmdNums.JMPLO:
			return CMD_JMPLO;
		case CmdNums.JMPLE:
			return CMD_JMPLE;
		case CmdNums.CALL:
			return CMD_CALL;
		case CmdNums.CMP:
			return CMD_CMP;
		case CmdNums.RET:
			return CMD_RET;
		case CmdNums.INT:
			return CMD_INT;
		case CmdNums.PUSH:
			return CMD_PUSH;
		case CmdNums.POP:
			return CMD_POP;
		case CmdNums.SET_IP:
			return CMD_SET_IP;
		case CmdNums.SET_SP:
			return CMD_SET_SP;
		case CmdNums.ADDC:
			return CMD_ADDC;
		case CmdNums.SUBC:
			return CMD_SUBC;
		case CmdNums.DEC:
			return CMD_DEC;
		case CmdNums.INC:
			return CMD_INC;
		default:
			throw new NoCommandException("this byte does not show a command!");
		}
	}
	
	private static class CmdNums {
		
		private final static int MOV = 0x01;
		private static final int ADD = 0x02;
		private static final int SUB = 0x03;
		private static final int MUL = 0x04;
		private static final int DIV = 0x05;
		private static final int AND = 0x06;
		private static final int OR = 0x07;
		private static final int XOR = 0x08;
		private static final int NOT = 0x09;
		private static final int NEG = 0x0A;
		private static final int LSH = 0x0B;
		private static final int RLSH = 0x0C;
		private static final int RASH = 0x0D;
		private static final int DEC = 0x0E;
		private static final int INC = 0x0F;
		private static final int JMP = 0x10;
		private static final int JMPEQ = 0x11;
		private static final int JMPNE = 0x12;
		private static final int JMPGT = 0x13;
		private static final int JMPGE = 0x14;
		private static final int JMPLO = 0x15;
		private static final int JMPLE = 0x16;
		private static final int JMPCS = 0x17;
		private static final int JMPCC = 0x18;
		private static final int CALL = 0x20;
		private static final int CMP = 0x21;
		private static final int RET = 0x22;
		private static final int INT = 0x23;
		private static final int PUSH = 0x24;
		private static final int POP = 0x25;
		private static final int SET_IP = 0x26;
		private static final int SET_SP = 0x27;
		private static final int GET_IP = 0x28;
		private static final int GET_SP = 0x29;
		private static final int GET_INTS = 0x2A;
		private static final int SET_INTS = 0x2B;
		private static final int IRET = 0x2C;
		private static final int GET_INTCNT = 0x2D;
		private static final int SET_INTCNT = 0x2E;
		private static final int ADDC = 0x30;
		private static final int SUBC = 0x31;
		
	}
	
}
