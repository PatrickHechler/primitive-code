package de.hechler.patrick.codesprachen.primitive.core.utils;

public class PrimAsmCommands {
	
	/**
	 * MOV [0x01] <br>
	 * params: 2<br>
	 */
	public static final int MOV  = 0x01;
	/**
	 * ADD [0x02] <br>
	 * params: 2<br>
	 */
	public static final int ADD  = 0x02;
	/**
	 * SUB [0x03] <br>
	 * params: 2<br>
	 */
	public static final int SUB  = 0x03;
	/**
	 * MUL [0x04] <br>
	 * params: 2<br>
	 */
	public static final int MUL  = 0x04;
	/**
	 * DIV [0x05] <br>
	 * params: 2<br>
	 */
	public static final int DIV  = 0x05;
	/**
	 * AND [0x06] <br>
	 * params: 2<br>
	 */
	public static final int AND  = 0x06;
	/**
	 * OR [0x07] <br>
	 * params: 2<br>
	 */
	public static final int OR   = 0x07;
	/**
	 * XOR [0x08] <br>
	 * params: 2<br>
	 */
	public static final int XOR  = 0x08;
	/**
	 * NOT [0x09] <br>
	 * params: 2<br>
	 */
	public static final int NOT  = 0x09;
	/**
	 * NEG [0x0A] <br>
	 * params: 2<br>
	 */
	public static final int NEG  = 0x0A;
	/**
	 * LSH [0x0B] <br>
	 * params: 2<br>
	 */
	public static final int LSH  = 0x0B;
	/**
	 * RLSH [0x0C] <br>
	 * params: 2<br>
	 */
	public static final int RLSH = 0x0C;
	/**
	 * RASH [0x0D] <br>
	 * params: 2<br>
	 */
	public static final int RASH = 0x0D;
	/**
	 * DEC [0x0E] <br>
	 * params: 1<br>
	 */
	public static final int DEC  = 0x0E;
	/**
	 * INC [0x0F] <br>
	 * params: 1<br>
	 */
	public static final int INC  = 0x0F;
	
	/**
	 * JMP [0x10] <br>
	 * params: 1<br>
	 */
	public static final int JMP    = 0x10;
	/**
	 * JMPEQ [0x11] <br>
	 * params: 1<br>
	 */
	public static final int JMPEQ  = 0x11;
	/**
	 * JMPNE [0x12] <br>
	 * params: 1<br>
	 */
	public static final int JMPNE  = 0x12;
	/**
	 * JMPGT [0x13] <br>
	 * params: 1<br>
	 */
	public static final int JMPGT  = 0x13;
	/**
	 * JMPGE [0x14] <br>
	 * params: 1<br>
	 */
	public static final int JMPGE  = 0x14;
	/**
	 * JMPLT [0x15] <br>
	 * params: 1<br>
	 */
	public static final int JMPLT  = 0x15;
	/**
	 * JMPLE [0x16] <br>
	 * params: 1<br>
	 */
	public static final int JMPLE  = 0x16;
	/**
	 * JMPCS [0x17] <br>
	 * params: 1<br>
	 */
	public static final int JMPCS  = 0x17;
	/**
	 * JMPCC [0x18] <br>
	 * params: 1<br>
	 */
	public static final int JMPCC  = 0x18;
	/**
	 * JMPZS [0x19] <br>
	 * params: 1<br>
	 */
	public static final int JMPZS  = 0x19;
	/**
	 * JMPZC [0x1A] <br>
	 * params: 1<br>
	 */
	public static final int JMPZC  = 0x1A;
	/**
	 * JMPNAN [0x1B] <br>
	 * params: 1<br>
	 */
	public static final int JMPNAN = 0x1B;
	/**
	 * JMPAN [0x1C] <br>
	 * params: 1<br>
	 */
	public static final int JMPAN  = 0x1C;
	
	/**
	 * CALL [0x20] <br>
	 * params: 1<br>
	 */
	public static final int CALL  = 0x20;
	/**
	 * CMP [0x21] <br>
	 * params: 2<br>
	 */
	public static final int CMP   = 0x21;
	/**
	 * RET [0x22] <br>
	 * params: 0<br>
	 */
	public static final int RET   = 0x22;
	/**
	 * INT [0x23] <br>
	 * params: 1<br>
	 */
	public static final int INT   = 0x23;
	/**
	 * PUSH [0x24] <br>
	 * params: 1<br>
	 */
	public static final int PUSH  = 0x24;
	/**
	 * POP [0x25] <br>
	 * params: 1<br>
	 */
	public static final int POP   = 0x25;
	/**
	 * IRET [0x26] <br>
	 * params: 0<br>
	 */
	public static final int IRET  = 0x26;
	/**
	 * SWAP [0x27] <br>
	 * params: 2<br>
	 */
	public static final int SWAP  = 0x27;
	/**
	 * LEA [0x28] <br>
	 * params: 2<br>
	 */
	public static final int LEA   = 0x28;
	/**
	 * MVAD [0x29] <br>
	 * params: 3<br>
	 */
	public static final int MVAD  = 0x29;
	/**
	 * CALO [0x2A] <br>
	 * params: 2<br>
	 */
	public static final int CALO  = 0x2A;
	/**
	 * BCP [0x2B] <br>
	 * params: 2<br>
	 */
	public static final int BCP   = 0x2B;
	/**
	 * CMPFP [0x2C] <br>
	 * params: 2<br>
	 */
	public static final int CMPFP = 0x2C;
	/**
	 * CHKFP [0x2D] <br>
	 * params: 1<br>
	 */
	public static final int CHKFP = 0x2D;
	
	/**
	 * ADDC [0x30] <br>
	 * params: 2<br>
	 */
	public static final int ADDC  = 0x30;
	/**
	 * SUBC [0x31] <br>
	 * params: 2<br>
	 */
	public static final int SUBC  = 0x31;
	/**
	 * ADDFP [0x32] <br>
	 * params: 2<br>
	 */
	public static final int ADDFP = 0x32;
	/**
	 * SUBFP [0x33] <br>
	 * params: 2<br>
	 */
	public static final int SUBFP = 0x33;
	/**
	 * MULFP [0x34] <br>
	 * params: 2<br>
	 */
	public static final int MULFP = 0x34;
	/**
	 * DIVFP [0x35] <br>
	 * params: 2<br>
	 */
	public static final int DIVFP = 0x35;
	/**
	 * NTFP [0x36] <br>
	 * params: 1<br>
	 */
	public static final int NTFP  = 0x36;
	/**
	 * FPTN [0x37] <br>
	 * params: 1<br>
	 */
	public static final int FPTN  = 0x37;
	/**
	 * UMUL [0x38] <br>
	 * params: 2<br>
	 */
	public static final int UMUL  = 0x38;
	/**
	 * UDIV [0x39] <br>
	 * params: 2<br>
	 */
	public static final int UDIV  = 0x39;
	
}
