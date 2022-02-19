package de.patrick.hechler.codesprachen.primitive.assemble.objects;

public class Param {
	
	public static final int ART_LABEL = -1;
	
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
	
	
	public final String label;
	public final long num;
	public final long off;
	public final int art;
	
	
	
	private Param(String label, long num, long off, int art) {
		this.label = label;
		this.num = num;
		this.off = off;
		this.art = art;
	}
	
	public static Param createLabel(String label) {
		return new Param(label, 0L, 0L, ART_LABEL);
	}
	
	/**
	 * can Build all non Label {@link Param}s.
	 */
	public static class ParamBuilder {
		
		//@formatter:off
		public static final int SR_IP     = 0;
		public static final int SR_SP     = 1;
		public static final int SR_STATUS = 2;
		public static final int SR_INTCNT = 3;
		public static final int SR_INTP   = 4;
		public static final int SR_X_ADD  = 5;
		
		public static final int A_NUM = 0x00000001;
		public static final int A_SR  = 0x00000002;
		public static final int B_REG = 0x00000010;
		public static final int B_NUM = 0x00000020;
		public static final int B_SR  = 0x00000040;
		
		private static final int BUILD_ANUM      = A_NUM;
		private static final int BUILD_ASR       = A_SR;
		private static final int BUILD_ANUM_BREG = A_NUM | B_REG;
		private static final int BUILD_ASR_BREG  = A_SR  | B_REG;
		private static final int BUILD_ANUM_BNUM = A_NUM | B_NUM;
		private static final int BUILD_ASR_BNUM  = A_SR  | B_NUM;
		private static final int BUILD_ANUM_BAX  = A_NUM | B_SR;
		private static final int BUILD_AAX_BAX   = A_SR  | B_SR;
		//@formatter:on
		
		
		
		public int art = 0;
		public long v1 = 0;
		public long v2 = 0;
		
		public boolean isValid() {
			try {
				build();
				return true;
			} catch (IllegalStateException e) {
				return false;
			}
		}
		
		public Param build() {
			switch (art) {
			case BUILD_ANUM:
				return new Param(null, v1, 0, Param.ART_ANUM);
			case BUILD_ANUM_BREG:
				return new Param(null, v1, 0, Param.ART_ANUM_BREG);
			case BUILD_ASR:
				Param.checkSR(v1);
				return new Param(null, v1, 0, Param.ART_ASR);
			case BUILD_ASR_BREG:
				Param.checkSR(v1);
				return new Param(null, v1, 0, Param.ART_ASR_BREG);
			case BUILD_ANUM_BNUM:
				return new Param(null, v1, v2, Param.ART_ANUM_BNUM);
			case BUILD_ASR_BNUM:
				Param.checkSR(v1);
				return new Param(null, v1, v2, Param.ART_ASR_BNUM);
			case BUILD_ANUM_BAX:
				Param.checkSR(v2);
				return new Param(null, v1, v2, Param.ART_ANUM_BSR);
			case BUILD_AAX_BAX:
				Param.checkSR(v1);
				Param.checkSR(v2);
				return new Param(null, v1, v2, Param.ART_ASR_BSR);
			default:
				throw new IllegalStateException("art=" + Integer.toHexString(art));
			}
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
	
	public static void checkSR(long num) {
		if (num > 0xFF) {
			throw new IllegalStateException("this num is no SR: num=0x" + Long.toHexString(num));
		}
	}
	
	@Override
	public String toString() {
		if (label != null) {
			return '@' + label;
		}
		switch (art) {
		case ART_ANUM:
			return Long.toString(this.num);
		case ART_ASR:
			return toSRString(this.num);
		case ART_ANUM_BREG:
			return "[" + Long.toString(this.num) + "]";
		case ART_ASR_BREG:
			return "[" + toSRString(this.num) + "]";
		case ART_ANUM_BNUM:
			return "[" + Long.toString(this.num) + "+" + Long.toString(this.off) + "]";
		case ART_ASR_BNUM:
			return "[" + toSRString(this.num) + "+" + Long.toString(this.off) + "]";
		case ART_ANUM_BSR:
			return "[" + Long.toString(this.num) + "+" + toSRString(this.off) + "]";
		case ART_ASR_BSR:
			return "[" + toSRString(this.num) + "+" + toSRString(this.off) + "]";
		default:
			throw new InternalError("unknown param art: " + art);
		}
	}
	
	private String toSRString(long sr) {
		assert sr < 0xFF;
		int reg = (int) sr;
		switch (reg) {
		case ParamBuilder.SR_IP:
			return "IP";
		case ParamBuilder.SR_SP:
			return "SP";
		case ParamBuilder.SR_STATUS:
			return "STATUS";
		case ParamBuilder.SR_INTCNT:
			return "INTCNT";
		case ParamBuilder.SR_INTP:
			return "INTP";
		default:
			return "X" + ( (sr < 0x10) ? ("0" + Integer.toHexString(reg - ParamBuilder.SR_X_ADD)) : Integer.toHexString(reg - ParamBuilder.SR_X_ADD));
		}
	}
	
}
