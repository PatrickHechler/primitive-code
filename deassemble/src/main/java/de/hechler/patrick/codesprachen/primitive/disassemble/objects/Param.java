package de.hechler.patrick.codesprachen.primitive.disassemble.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.*;

import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;

public class Param {
	
//	public static final int PARAM_BASE  = PARAM_PART_BASE;
//	public static final int PARAM_A_NUM = PARAM_PART_A_NUM;
//	public static final int PARAM_A_SR  = PARAM_PART_A_SR;
//	public static final int PARAM_NO_B  = PARAM_PART_NO_B;
//	public static final int PARAM_B_REG = PARAM_PART_B_REG;
//	public static final int PARAM_B_NUM = PARAM_PART_B_NUM;
//	public static final int PARAM_B_SR  = PARAM_PART_B_SR;
	
	public static final int ART_ANUM      = PARAM_ART_ANUM;
	public static final int ART_ASR       = PARAM_ART_ASR;
	public static final int ART_ANUM_BREG = PARAM_ART_ANUM_BREG;
	public static final int ART_ASR_BREG  = PARAM_ART_ASR_BREG;
	public static final int ART_ANUM_BNUM = PARAM_ART_ANUM_BNUM;
	public static final int ART_ASR_BNUM  = PARAM_ART_ASR_BNUM;
	public static final int ART_ANUM_BSR  = PARAM_ART_ANUM_BSR;
	public static final int ART_ASR_BSR   = PARAM_ART_ASR_BSR;
	
	
	public static final int SR_IP     = 0;
	public static final int SR_SP     = 1;
	public static final int SR_STATUS = 2;
	public static final int SR_INTCNT = 3;
	public static final int SR_INTP   = 4;
	public static final int SR_X_SUB  = 5;
	
	
	public final long num;
	public final long off;
	public final int  art;
	
	
	
	private Param(long num, long off, int art) {
		this.num = num;
		this.off = off;
		this.art = art;
	}
	
	/**
	 * can Build all {@link Param}s.
	 */
	public static class ParamBuilder {
		
		public int  art = 0;
		public long v1  = 0;
		public long v2  = 0;
		
		public boolean isValid() {
			try {
				build();
				return true;
			} catch (NoCommandException e) {
				return false;
			}
		}
		
		public Param build() throws NoCommandException {
			switch (art) {
			case ART_ANUM:
				zeroCheck(v2);
				return new Param(v1, 0, art);
			case ART_ANUM_BNUM:
				return new Param(v1, v2, art);
			case ART_ANUM_BREG:
				zeroCheck(v2);
				return new Param(v1, 0, art);
			case ART_ANUM_BSR:
				checkSR(v2);
				return new Param(v1, v2, art);
			case ART_ASR:
				checkSR(v1);
				zeroCheck(v2);
				return new Param(v1, 0, art);
			case ART_ASR_BNUM:
				checkSR(v1);
				return new Param(v1, v2, art);
			case ART_ASR_BREG:
				checkSR(v1);
				zeroCheck(v2);
				return new Param(v1, 0, art);
			case ART_ASR_BSR:
				checkSR(v1);
				checkSR(v2);
				return new Param(v1, v2, art);
			default:
				throw new NoCommandException("unknown art");
			}
		}
		
	}
	
	public static void zeroCheck(long val) throws NoCommandException {
		if (val != 0) {
			throw new NoCommandException("value is not 0: " + val);
		}
	}
	
	public static void zeroCheck(byte val) throws NoCommandException {
		if (val != 0) {
			throw new NoCommandException("value is not 0: " + val);
		}
	}
	
	public static String artToString(int art) {
		switch (art) {
		case ART_ANUM:
			return "[ANUM]";
		case ART_ASR:
			return "[ASR]";
		case ART_ANUM_BREG:
			return "[ANUM_BREG]";
		case ART_ASR_BREG:
			return "[ASR_BREG]";
		case ART_ANUM_BNUM:
			return "[ANUM_BNUM]";
		case ART_ASR_BNUM:
			return "[ASR_BNUM]";
		case ART_ANUM_BSR:
			return "[ANUM_BSR]";
		case ART_ASR_BSR:
			return "[ASR_BSR]";
		default:
			return "<INVALID[" + art + "]>";
		}
	}
	
	public static void checkSR(long num) throws NoCommandException {
		if ( (num & 0xFFFFFFFFFFFFFF00L) != 0) {
			throw new NoCommandException("this num is no SR: num=" + num + " AX=0 BX=1 CX=2 DX=3");
		}
	}
	
	public void checkNoConst() throws NoCommandException {
		if (art == ART_ANUM) {
			throw new NoCommandException("this is not allowed to be a constant!");
		}
	}
	
	public int length() {
		switch (art) {
		case ART_ASR:
		case ART_ASR_BREG:
		case ART_ASR_BSR:
			return 0;
		case ART_ANUM:
		case ART_ANUM_BREG:
		case ART_ANUM_BSR:
		case ART_ASR_BNUM:
			return 8;
		case ART_ANUM_BNUM:
			return 16;
		default:
			throw new InternalError("unknown art: " + art);
		}
	}
	
	@Override
	public String toString() {
		switch (art) {
		case ART_ASR:
			return getSR(num);
		case ART_ASR_BREG:
			return "[" + getSR(num) + "]";
		case ART_ASR_BSR:
			return "[" + getSR(num) + " + " + getSR(off) + "]";
		case ART_ANUM:
			return "" + num;
		case ART_ANUM_BREG:
			return "[" + num + "]";
		case ART_ANUM_BSR:
			return "[" + num + " + " + getSR(off) + "]";
		case ART_ASR_BNUM:
			return "[" + getSR(num) + " + " + off + "]";
		case ART_ANUM_BNUM:
			return "[" + num + " + " + off + "]";
		default:
			throw new InternalError("unknown art: " + art);
		}
	}
	
	private String getSR(long sr) {
		if ( (sr & 0x00000000000000FFL) != sr) {
			throw new InternalError("this is no register");
		}
		switch ((int) sr) {
		case SR_IP:
			return "IP";
		case SR_SP:
			return "SP";
		case SR_STATUS:
			return "STATUS";
		case SR_INTCNT:
			return "INTCNT";
		case SR_INTP:
			return "INTP";
		default:
			String num = Integer.toHexString((int) sr - SR_X_SUB).toUpperCase();
			if (num.length() == 1) {
				return "X0" + num;
			}
			return "X" + num;
		}
	}
	
}
