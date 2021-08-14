package de.patrick.hechler.codesprachen.primitive.disassemble.objects;

import de.patrick.hechler.codesprachen.primitive.disassemble.exceptions.NoCommandException;

public class Param {
	
	private static final int BASE = 0x01;
	private static final int A_NUM = 0x00;
	private static final int A_SR = 0x02;
	private static final int NO_B = 0x00;
	private static final int B_REG = 0x04;
	private static final int B_NUM = 0x08;
	private static final int B_SR = 0x0C;
	
	public static final int ART_ANUM = BASE | A_NUM | NO_B;
	public static final int ART_ASR = BASE | A_SR | NO_B;
	public static final int ART_ANUM_BREG = BASE | A_NUM | B_REG;
	public static final int ART_ASR_BREG = BASE | A_SR | B_REG;
	public static final int ART_ANUM_BNUM = BASE | A_NUM | B_NUM;
	public static final int ART_ASR_BNUM = BASE | A_SR | B_NUM;
	public static final int ART_ANUM_BSR = BASE | A_NUM | B_SR;
	public static final int ART_ASR_BSR = BASE | A_SR | B_SR;
	
	
	public static final int SR_AX = 0x00;
	public static final int SR_BX = 0x01;
	public static final int SR_CX = 0x02;
	public static final int SR_DX = 0x03;
	
	public final long num;
	public final long off;
	public final int art;
	
	
	
	private Param(long num, long off, int art) {
		this.num = num;
		this.off = off;
		this.art = art;
	}
	
	/**
	 * can Build all {@link Param}s.
	 * 
	 */
	public static class ParamBuilder {
		
		public int art = 0;
		public long v1 = 0;
		public long v2 = 0;
		
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
				return new Param(v1, 0, ART_ANUM);
			case ART_ANUM_BNUM:
				return new Param(v1, v2, ART_ANUM_BNUM);
			case ART_ANUM_BREG:
				zeroCheck(v2);
				return new Param(v1, 0, ART_ANUM_BREG);
			case ART_ANUM_BSR:
				checkSR(v2);
				return new Param(v1, v2, ART_ANUM_BSR);
			case ART_ASR:
				checkSR(v1);
				zeroCheck(v2);
				return new Param(v1, 0, ART_ASR);
			case ART_ASR_BNUM:
				checkSR(v1);
				return new Param(v1, v2, ART_ASR_BNUM);
			case ART_ASR_BREG:
				checkSR(v1);
				zeroCheck(v2);
				return new Param(v1, 0, ART_ASR_BREG);
			case ART_ASR_BSR:
				checkSR(v1);
				checkSR(v2);
				return new Param(v1, v2, ART_ASR_BSR);
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
		if ( (num & 0xFFFFFFFFFFFFFFFCL) != 0) {
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
			return 1;
		case ART_ANUM:
		case ART_ANUM_BREG:
		case ART_ANUM_BSR:
		case ART_ASR_BNUM:
			return 2;
		case ART_ANUM_BNUM:
			return 3;
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
			return "[" + num + " + " + getSR(off)+ "]";
		case ART_ASR_BNUM:
			return "[" + getSR(num) + " + " + off + "]";
		case ART_ANUM_BNUM:
			return "[" + num + " + " + off + "]";
		default:
			throw new InternalError("unknown art: " + art);
		}
	}
	
	private String getSR(long sr) {
		if ( (sr & 0x0000000000000003L) != sr) {
			throw new InternalError("this is no register");
		}
		switch ((int) sr) {
		case SR_AX:
			return "AX";
		case SR_BX:
			return "AX";
		case SR_CX:
			return "AX";
		case SR_DX:
			return "AX";
		default:
			throw new InternalError("this is no register");
		}
	}
	
}
