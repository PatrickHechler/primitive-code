//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.primitive.disassemble.enums;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.*;

import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;

public enum Commands {
	
	//@formatter:off
	// GENERATED-CODE-START
	// this code-block is automatic generated, do not modify
	
	// Params: <NO_CONST_PARAM> , <PARAM> , <CONST_PARAM>
	CMD_MVAD(ParamArt.THREE_PARAMS_P1_NO_CONST_P2_ALLOW_CONST_P3_COMPILE_CONST, MVAD),
	
	// Params: <NO_CONST_PARAM> , <NO_CONST_PARAM>
	CMD_SWAP(ParamArt.TWO_PARAMS_NO_CONSTS, SWAP),
	CMD_DIV(ParamArt.TWO_PARAMS_NO_CONSTS, DIV),
	CMD_UDIV(ParamArt.TWO_PARAMS_NO_CONSTS, UDIV),
	CMD_BADD(ParamArt.TWO_PARAMS_NO_CONSTS, BADD),
	CMD_BSUB(ParamArt.TWO_PARAMS_NO_CONSTS, BSUB),
	CMD_BMUL(ParamArt.TWO_PARAMS_NO_CONSTS, BMUL),
	CMD_BDIV(ParamArt.TWO_PARAMS_NO_CONSTS, BDIV),
	CMD_CMPB(ParamArt.TWO_PARAMS_NO_CONSTS, CMPB),
	
	// Params: <NO_CONST_PARAM> , <PARAM>
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
	
	// Params: <PARAM> , <PARAM>
	CMD_CMP(ParamArt.TWO_PARAMS_ALLOW_CONSTS, CMP),
	CMD_CMPL(ParamArt.TWO_PARAMS_ALLOW_CONSTS, CMPL),
	CMD_CMPFP(ParamArt.TWO_PARAMS_ALLOW_CONSTS, CMPFP),
	CMD_CMPU(ParamArt.TWO_PARAMS_ALLOW_CONSTS, CMPU),
	CMD_PUSHBLK(ParamArt.TWO_PARAMS_ALLOW_CONSTS, PUSHBLK),
	CMD_POPBLK(ParamArt.TWO_PARAMS_ALLOW_CONSTS, POPBLK),
	
	// Params: <PARAM> , <CONST_PARAM>
	CMD_CALO(ParamArt.TWO_PARAMS_P1_NO_CONST_P2_COMPILE_CONST, CALO),
	
	// Params: <NO_CONST_PARAM>
	CMD_NOT(ParamArt.ONE_PARAM_NO_CONST, NOT),
	CMD_NEG(ParamArt.ONE_PARAM_NO_CONST, NEG),
	CMD_INC(ParamArt.ONE_PARAM_NO_CONST, INC),
	CMD_DEC(ParamArt.ONE_PARAM_NO_CONST, DEC),
	CMD_NEGFP(ParamArt.ONE_PARAM_NO_CONST, NEGFP),
	CMD_BNEG(ParamArt.ONE_PARAM_NO_CONST, BNEG),
	CMD_FPTN(ParamArt.ONE_PARAM_NO_CONST, FPTN),
	CMD_NTFP(ParamArt.ONE_PARAM_NO_CONST, NTFP),
	CMD_POP(ParamArt.ONE_PARAM_NO_CONST, POP),
	
	// Params: <PARAM>
	CMD_CHKFP(ParamArt.ONE_PARAM_ALLOW_CONST, CHKFP),
	CMD_SGN(ParamArt.ONE_PARAM_ALLOW_CONST, SGN),
	CMD_SGNFP(ParamArt.ONE_PARAM_ALLOW_CONST, SGNFP),
	CMD_INT(ParamArt.ONE_PARAM_ALLOW_CONST, INT),
	CMD_PUSH(ParamArt.ONE_PARAM_ALLOW_CONST, PUSH),
	
	// Params: <LABEL>
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
	
	// Params: none 
	CMD_EXTERN(ParamArt.NO_PARAMS, EXTERN),
	CMD_IRET(ParamArt.NO_PARAMS, IRET),
	CMD_RET(ParamArt.NO_PARAMS, RET),
	
	// here is the end of the automatic generated code-block
	// GENERATED-CODE-END
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
		
		TWO_PARAMS_NO_CONSTS, TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, TWO_PARAMS_ALLOW_CONSTS, TWO_PARAMS_P1_NO_CONST_P2_COMPILE_CONST,
		
		THREE_PARAMS_P1_NO_CONST_P2_ALLOW_CONST_P3_COMPILE_CONST,
	
	}
	
	@Override
	public String toString() { return name().substring(4); }
	
	private static final Commands[] COMMANDS = new Commands[1 << 16];
	
	static {
		for (Commands c : values()) {
			if (COMMANDS[c.num] != null) {
				throw new InternalError("multiple commands with the num " + c.num + " : '" + COMMANDS[c.num].name() + "' '" + c.name() + '\'');
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
