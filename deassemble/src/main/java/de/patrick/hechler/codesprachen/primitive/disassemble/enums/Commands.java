package de.patrick.hechler.codesprachen.primitive.disassemble.enums;

import de.patrick.hechler.codesprachen.primitive.disassemble.exceptions.NoCommandException;

public enum Commands {
	
	//@formatter:off
	CMD_MOV(ParamArt.twoParamsP1NoConstP2AllowConst), CMD_ADD(ParamArt.twoParamsP1NoConstP2AllowConst), CMD_SUB(ParamArt.twoParamsP1NoConstP2AllowConst), CMD_MUL(ParamArt.twoParamsP1NoConstP2AllowConst), CMD_DIV(ParamArt.twoParamsNoConsts),
	CMD_AND(ParamArt.twoParamsP1NoConstP2AllowConst), CMD_OR(ParamArt.twoParamsP1NoConstP2AllowConst), CMD_XOR(ParamArt.twoParamsP1NoConstP2AllowConst),
	CMD_NOT(ParamArt.oneParamNoConst), CMD_NEG(ParamArt.oneParamNoConst),
	CMD_LSH(ParamArt.oneParamNoConst), CMD_RLSH(ParamArt.oneParamNoConst), CMD_RASH(ParamArt.oneParamNoConst), 
	
	CMD_JMP(ParamArt.label), CMD_JMPEQ(ParamArt.label), CMD_JMPNE(ParamArt.label), CMD_JMPGT(ParamArt.label), CMD_JMPGE(ParamArt.label), CMD_JMPLO(ParamArt.label), CMD_JMPLE(ParamArt.label), CMD_JMPCS(ParamArt.label), CMD_JMPCC(ParamArt.label),
	CMD_CALL(ParamArt.label),
	
	CMD_CMP(ParamArt.twoParamsAllowConsts),
	CMD_RET(ParamArt.noParams), CMD_INT(ParamArt.oneParamAllowConst),
	CMD_PUSH(ParamArt.oneParamAllowConst), CMD_POP(ParamArt.oneParamNoConst),
	CMD_SET_IP(ParamArt.oneParamAllowConst), CMD_SET_SP(ParamArt.oneParamAllowConst),
	CMD_GET_IP(ParamArt.oneParamNoConst), CMD_GET_SP(ParamArt.oneParamNoConst),
	
	;
	//@formatter:on
	
	public final ParamArt art;
	
	private Commands(ParamArt art) {
		this.art = art;
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
	
	public int num() {
		switch (this) {
		case CMD_ADD:
			return ADD;
		case CMD_AND:
			return AND;
		case CMD_CALL:
			return CALL;
		case CMD_CMP:
			return CMP;
		case CMD_DIV:
			return DIV;
		case CMD_INT:
			return INT;
		case CMD_JMP:
			return JMP;
		case CMD_JMPCS:
			return JMPCS;
		case CMD_JMPCC:
			return JMPCC;
		case CMD_JMPEQ:
			return JMPEQ;
		case CMD_JMPGE:
			return JMPGE;
		case CMD_JMPGT:
			return JMPGT;
		case CMD_JMPLE:
			return JMPLE;
		case CMD_JMPLO:
			return JMPLO;
		case CMD_JMPNE:
			return JMPNE;
		case CMD_LSH:
			return LSH;
		case CMD_MOV:
			return MOV;
		case CMD_MUL:
			return MUL;
		case CMD_NEG:
			return NEG;
		case CMD_NOT:
			return NOT;
		case CMD_OR:
			return OR;
		case CMD_POP:
			return POP;
		case CMD_PUSH:
			return PUSH;
		case CMD_RASH:
			return RASH;
		case CMD_RET:
			return RET;
		case CMD_RLSH:
			return RLSH;
		case CMD_SET_IP:
			return SET_IP;
		case CMD_SET_SP:
			return SET_SP;
		case CMD_GET_IP:
			return GET_IP;
		case CMD_GET_SP:
			return GET_SP;
		case CMD_SUB:
			return SUB;
		case CMD_XOR:
			return XOR;
		default:
			throw new InternalError("unknown command: " + this.name());
		
		}
	}
	
	public static Commands get(byte b) throws NoCommandException {
		switch (b & 0xFF) {
		case MOV:
			return CMD_MOV;
		case ADD:
			return CMD_ADD;
		case SUB:
			return CMD_SUB;
		case MUL:
			return CMD_MUL;
		case DIV:
			return CMD_DIV;
		case AND:
			return CMD_AND;
		case OR:
			return CMD_OR;
		case XOR:
			return CMD_XOR;
		case NOT:
			return CMD_NOT;
		case NEG:
			return CMD_NEG;
		case LSH:
			return CMD_LSH;
		case RLSH:
			return CMD_RLSH;
		case RASH:
			return CMD_RASH;
		case JMP:
			return CMD_JMP;
		case JMPCS:
			return CMD_JMPCS;
		case JMPCC:
			return CMD_JMPCC;
		case JMPEQ:
			return CMD_JMPEQ;
		case JMPNE:
			return CMD_JMPNE;
		case JMPGT:
			return CMD_JMPGT;
		case JMPGE:
			return CMD_JMPGE;
		case JMPLO:
			return CMD_JMPLO;
		case JMPLE:
			return CMD_JMPLE;
		case CALL:
			return CMD_CALL;
		case CMP:
			return CMD_CMP;
		case RET:
			return CMD_RET;
		case INT:
			return CMD_INT;
		case PUSH:
			return CMD_PUSH;
		case POP:
			return CMD_POP;
		case SET_IP:
			return CMD_SET_IP;
		case SET_SP:
			return CMD_SET_SP;
		default:
			throw new NoCommandException("this byte does not show a command!");
		}
	}
	
}
