package de.hechler.patrick.codesprachen.primitive.disassemble.enums;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.ADD;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.ADDC;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.ADDFP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.AND;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.BADD;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.BDIV;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.BMUL;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.BNEG;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.BSUB;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.CALL;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.CALO;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.CHKFP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.CMP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.CMPB;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.CMPFP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.CMPL;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.CMPU;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.DEC;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.DIV;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.DIVFP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.FPTN;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.INC;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.INT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPAB;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPAN;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPCC;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPCS;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPEQ;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPERR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPGE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPGT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPLE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPLT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPNAN;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPNB;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPNE;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPSB;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPZC;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.JMPZS;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.LEA;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.LSH;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.MOV;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.MUL;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.MULFP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.MVAD;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.MVB;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.MVDW;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.MVW;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.NEG;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.NEGFP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.NOT;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.NTFP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.OR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.POP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.POPBLK;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.PUSH;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.PUSHBLK;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.RASH;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.RLSH;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.SUB;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.SUBC;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.SUBFP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.SWAP;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.UADD;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.UDIV;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.UMUL;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.USUB;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.XOR;

import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;

public enum Commands {
	
	//@formatter:off
	// NO_CONST_PARAM , PARAM , CONST_PARAM
	CMD_MVAD(ParamArt.THREE_PARAMS_P1_NO_CONST_P2_ALLOW_CONST_P3_COMPILE_CONST, MVAD),
	
	// NO_CONST_PARAM , NO_CONST_PARAM
	CMD_SWAP(ParamArt.TWO_PARAMS_NO_CONSTS, SWAP),
	CMD_DIV(ParamArt.TWO_PARAMS_NO_CONSTS, DIV),
	CMD_UDIV(ParamArt.TWO_PARAMS_NO_CONSTS, UDIV),
	CMD_BADD(ParamArt.TWO_PARAMS_NO_CONSTS, BADD),
	CMD_BSUB(ParamArt.TWO_PARAMS_NO_CONSTS, BSUB),
	CMD_BMUL(ParamArt.TWO_PARAMS_NO_CONSTS, BMUL),
	CMD_BDIV(ParamArt.TWO_PARAMS_NO_CONSTS, BDIV),
	CMD_CMPB(ParamArt.TWO_PARAMS_NO_CONSTS, CMPB),
	
	// NO_CONST_PARAM , PARAM
	CMD_MVB(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, MVB),
	CMD_MVW(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, MVW),
	CMD_MVDW(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, MVDW),
	CMD_MOV(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, MOV),
	CMD_LEA(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, LEA),
	CMD_OR(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, OR),
	CMD_AND(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, AND),
	CMD_XOR(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, XOR),
	CMD_LSH(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, LSH),
	CMD_RASH(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, RASH),
	CMD_RLSH(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, RLSH),
	CMD_ADD(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, ADD),
	CMD_SUB(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, SUB),
	CMD_MUL(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, MUL),
	CMD_ADDC(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, ADDC),
	CMD_SUBC(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, SUBC),
	CMD_ADDFP(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, ADDFP),
	CMD_SUBFP(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, SUBFP),
	CMD_MULFP(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, MULFP),
	CMD_DIVFP(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, DIVFP),
	CMD_UADD(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, UADD),
	CMD_USUB(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, USUB),
	CMD_UMUL(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, UMUL),
	CMD_FPTN(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, FPTN),
	CMD_NTFP(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, NTFP),
	
	// PARAM , PARAM
	CMD_CMP(ParamArt.TWO_PARAMS_ALLOW_CONSTS, CMP),
	CMD_CMPL(ParamArt.TWO_PARAMS_ALLOW_CONSTS, CMPL),
	CMD_CMPFP(ParamArt.TWO_PARAMS_ALLOW_CONSTS, CMPFP),
	CMD_CHKFP(ParamArt.TWO_PARAMS_ALLOW_CONSTS, CHKFP),
	CMD_CMPU(ParamArt.TWO_PARAMS_ALLOW_CONSTS, CMPU),
	
	// PARAM , CONST_PARAM
	CMD_CALO(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_COMPILE_CONST, CALO),
	
	// NO_CONST_PARAM
	CMD_NEG(ParamArt.ONE_PARAM_NO_CONST, NEG),
	CMD_BNEG(ParamArt.ONE_PARAM_NO_CONST, BNEG),
	CMD_NEGFP(ParamArt.ONE_PARAM_NO_CONST, NEGFP),
	CMD_NOT(ParamArt.ONE_PARAM_NO_CONST, NOT),
	CMD_INC(ParamArt.ONE_PARAM_NO_CONST, INC),
	CMD_DEC(ParamArt.ONE_PARAM_NO_CONST, DEC),
	CMD_POP(ParamArt.ONE_PARAM_NO_CONST, POP),
	
	// PARAM
	CMD_INT(ParamArt.ONE_PARAM_ALLOW_CONST, INT),
	CMD_PUSH(ParamArt.ONE_PARAM_ALLOW_CONST, PUSH),
	CMD_PUSHBLK(ParamArt.ONE_PARAM_ALLOW_CONST, PUSHBLK),
	CMD_POPBLK(ParamArt.ONE_PARAM_ALLOW_CONST, POPBLK),
	
	// COPNST_PARAM/LABEL
	CMD_JMPERR(ParamArt.LABEL_OR_CONST, JMPERR),
	CMD_JMPEQ(ParamArt.LABEL_OR_CONST, JMPEQ),
	CMD_JMPNE(ParamArt.LABEL_OR_CONST, JMPNE),
	CMD_JMPGT(ParamArt.LABEL_OR_CONST, JMPGT),
	CMD_JMPGE(ParamArt.LABEL_OR_CONST, JMPGE),
	CMD_JMPLT(ParamArt.LABEL_OR_CONST, JMPLT),
	CMD_JMPLE(ParamArt.LABEL_OR_CONST, JMPLE),
	CMD_JMPCS(ParamArt.LABEL_OR_CONST, JMPCS),
	CMD_JMPCC(ParamArt.LABEL_OR_CONST, JMPCC),
	CMD_JMPZS(ParamArt.LABEL_OR_CONST, JMPZS),
	CMD_JMPZC(ParamArt.LABEL_OR_CONST, JMPZC),
	CMD_JMPNAN(ParamArt.LABEL_OR_CONST, JMPNAN),
	CMD_JMPAN(ParamArt.LABEL_OR_CONST, JMPAN),
	CMD_JMPAB(ParamArt.LABEL_OR_CONST, JMPAB),
	CMD_JMPSB(ParamArt.LABEL_OR_CONST, JMPSB),
	CMD_JMPNB(ParamArt.LABEL_OR_CONST, JMPNB),
	CMD_JMP(ParamArt.LABEL_OR_CONST, JMP),
	CMD_CALL(ParamArt.LABEL_OR_CONST, CALL),
	//@formatter:on
	
	;
	
	public final ParamArt art;
	public final int      num;
	
	private Commands(ParamArt art, int num) {
		this.art = art;
		this.num = num;
	}
	
	public enum ParamArt {
		
		NO_PARAMS,
		
		LABEL_OR_CONST,
		
		ONE_PARAM_NO_CONST, ONE_PARAM_ALLOW_CONST,
		
		TWO_PARAMS_NO_CONSTS, TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, TWO_PARAMS_ALLOW_CONSTS,
		TWO_PARAMS_P1_NO_CONST_P2_COMPILE_CONST,
		
		THREE_PARAMS_P1_NO_CONST_P2_ALLOW_CONST_P3_COMPILE_CONST,
	
	}
	
	@Override
	public String toString() { return name().substring(4); }
	
	private static final Commands[] COMMANDS = new Commands[1 << 16];
	
	static {
		for (Commands c : values()) {
			if (COMMANDS[c.num] != null) {
				throw new InternalError("multiple commands with the num " + c.num + " : '" + COMMANDS[c.num].name()
						+ "' '" + c.name() + '\'');
			}
			COMMANDS[c.num] = c;
		}
	}
	
	public static Commands get(int val) throws NoCommandException {
		Commands cmd = COMMANDS[val];
		if (cmd == null) {
			throw new NoCommandException("this word does not show a command! byte=0x" + Integer.toHexString(val));
		}
		return cmd;
	}
	
}
