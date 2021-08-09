package de.patrick.hechler.codesprachen.primitive.assemble.objects;

public class Param {
	
	public static final int ART_LABEL = -1;
	
	public static final int ART_ANUM = 0x00;
	public static final int ART_ANUM_BREG = 0x02;
	public static final int ART_ANUM_BNUM = 0x06;
	public static final int ART_ANUM_BSR = 0x0A;
	public static final int ART_ANUM_BREG_CREG = 0x0E;
	public static final int ART_ANUM_BNUM_CREG = 0x04;
	public static final int ART_ANUM_BSR_CREG = 0x08;
	public static final int ART_ASR = 0x01;
	public static final int ART_ASR_BREG = 0x03;
	public static final int ART_ASR_BNUM = 0x07;
	public static final int ART_ASR_BSR = 0x0B;
	public static final int ART_ASR_BREG_CREG = 0x0F;
	public static final int ART_ASR_BNUM_CREG = 0x05;
	public static final int ART_ASR_BSR_CREG = 0x09;
	
	
	public static final long SR_AX = 0x00L;
	public static final long SR_BX = 0x01L;
	public static final long SR_CX = 0x02L;
	public static final long SR_DX = 0x03L;
	
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
	 * 
	 */
	public static class ParamBuilder {
		
		public static final int A_NUM = 0x00000001;
		public static final int A_AX = 0x00000002;
		public static final int A_BX = 0x00000004;
		public static final int A_CX = 0x00000008;
		public static final int A_DX = 0x00000010;
		public static final int B_REG = 0x00000020;
		public static final int B_NUM = 0x00000040;
		public static final int B_AX = 0x00000080;
		public static final int B_BX = 0x00000100;
		public static final int B_CX = 0x00000200;
		public static final int B_DX = 0x00000400;
		public static final int C_REG = 0x00000800;
		
		private static final int BUILD_ANUM = A_NUM;
		private static final int BUILD_ANUM_BREG = A_NUM | B_REG;
		private static final int BUILD_ANUM_BREG_CREG = A_NUM | B_REG | C_REG;
		private static final int BUILD_AAX = A_AX;
		private static final int BUILD_ABX = A_BX;
		private static final int BUILD_ACX = A_CX;
		private static final int BUILD_ADX = A_DX;
		private static final int BUILD_AAX_BREG = A_AX | B_REG;
		private static final int BUILD_ABX_BREG = A_BX | B_REG;
		private static final int BUILD_ACX_BREG = A_CX | B_REG;
		private static final int BUILD_ADX_BREG = A_DX | B_REG;
		private static final int BUILD_AAX_BREG_CREG = A_AX | B_REG | C_REG;
		private static final int BUILD_ABX_BREG_CREG = A_BX | B_REG | C_REG;
		private static final int BUILD_ACX_BREG_CREG = A_CX | B_REG | C_REG;
		private static final int BUILD_ADX_BREG_CREG = A_DX | B_REG | C_REG;
		private static final int BUILD_ANUM_BNUM = A_NUM | B_NUM;
		private static final int BUILD_ANUM_BNUM_CREG = A_NUM | B_NUM | C_REG;
		private static final int BUILD_AAX_BNUM = A_AX | B_NUM;
		private static final int BUILD_ABX_BNUM = A_BX | B_NUM;
		private static final int BUILD_ACX_BNUM = A_CX | B_NUM;
		private static final int BUILD_ADX_BNUM = A_DX | B_NUM;
		private static final int BUILD_AAX_BNUM_CREG = A_AX | B_NUM | C_REG;
		private static final int BUILD_ABX_BNUM_CREG = A_BX | B_NUM | C_REG;
		private static final int BUILD_ACX_BNUM_CREG = A_CX | B_NUM | C_REG;
		private static final int BUILD_ADX_BNUM_CREG = A_DX | B_NUM | C_REG;
		private static final int BUILD_ANUM_BAX = A_NUM | B_AX;
		private static final int BUILD_ANUM_BBX = A_NUM | B_BX;
		private static final int BUILD_ANUM_BCX = A_NUM | B_CX;
		private static final int BUILD_ANUM_BDX = A_NUM | B_DX;
		private static final int BUILD_ANUM_BAX_CREG = A_NUM | B_AX | C_REG;
		private static final int BUILD_ANUM_BBX_CREG = A_NUM | B_BX | C_REG;
		private static final int BUILD_ANUM_BCX_CREG = A_NUM | B_CX | C_REG;
		private static final int BUILD_ANUM_BDX_CREG = A_NUM | B_DX | C_REG;
		private static final int BUILD_AAX_BAX = A_AX | B_AX;
		private static final int BUILD_AAX_BBX = A_AX | B_BX;
		private static final int BUILD_AAX_BCX = A_AX | B_CX;
		private static final int BUILD_AAX_BDX = A_AX | B_DX;
		private static final int BUILD_ABX_BAX = A_BX | B_AX;
		private static final int BUILD_ABX_BBX = A_BX | B_BX;
		private static final int BUILD_ABX_BCX = A_BX | B_CX;
		private static final int BUILD_ABX_BDX = A_BX | B_DX;
		private static final int BUILD_ACX_BAX = A_CX | B_AX;
		private static final int BUILD_ACX_BBX = A_CX | B_BX;
		private static final int BUILD_ACX_BCX = A_CX | B_CX;
		private static final int BUILD_ACX_BDX = A_CX | B_DX;
		private static final int BUILD_ADX_BAX = A_DX | B_AX;
		private static final int BUILD_ADX_BBX = A_DX | B_BX;
		private static final int BUILD_ADX_BCX = A_DX | B_CX;
		private static final int BUILD_ADX_BDX = A_DX | B_DX;
		private static final int BUILD_AAX_BAX_CREG = A_AX | B_AX | C_REG;
		private static final int BUILD_AAX_BBX_CREG = A_AX | B_BX | C_REG;
		private static final int BUILD_AAX_BCX_CREG = A_AX | B_CX | C_REG;
		private static final int BUILD_AAX_BDX_CREG = A_AX | B_DX | C_REG;
		private static final int BUILD_ABX_BAX_CREG = A_BX | B_AX | C_REG;
		private static final int BUILD_ABX_BBX_CREG = A_BX | B_BX | C_REG;
		private static final int BUILD_ABX_BCX_CREG = A_BX | B_CX | C_REG;
		private static final int BUILD_ABX_BDX_CREG = A_BX | B_DX | C_REG;
		private static final int BUILD_ACX_BAX_CREG = A_CX | B_AX | C_REG;
		private static final int BUILD_ACX_BBX_CREG = A_CX | B_BX | C_REG;
		private static final int BUILD_ACX_BCX_CREG = A_CX | B_CX | C_REG;
		private static final int BUILD_ACX_BDX_CREG = A_CX | B_DX | C_REG;
		private static final int BUILD_ADX_BAX_CREG = A_DX | B_AX | C_REG;
		private static final int BUILD_ADX_BBX_CREG = A_DX | B_BX | C_REG;
		private static final int BUILD_ADX_BCX_CREG = A_DX | B_CX | C_REG;
		private static final int BUILD_ADX_BDX_CREG = A_DX | B_DX | C_REG;
		
		
		
		public int art = 0;
		public long v1 = 0;
		public long v2 = 0;
		public long v3 = 0;
		
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
			case BUILD_ANUM_BREG_CREG:
				return new Param(null, v1, 0, Param.ART_ANUM_BREG_CREG);
			case BUILD_AAX:
				return new Param(null, Param.SR_AX, 0, Param.ART_ASR);
			case BUILD_ABX:
				return new Param(null, Param.SR_BX, 0, Param.ART_ASR);
			case BUILD_ACX:
				return new Param(null, Param.SR_CX, 0, Param.ART_ASR);
			case BUILD_ADX:
				return new Param(null, Param.SR_DX, 0, Param.ART_ASR);
			case BUILD_AAX_BREG:
				return new Param(null, Param.SR_AX, 0, Param.ART_ASR_BREG);
			case BUILD_ABX_BREG:
				return new Param(null, Param.SR_BX, 0, Param.ART_ASR_BREG);
			case BUILD_ACX_BREG:
				return new Param(null, Param.SR_CX, 0, Param.ART_ASR_BREG);
			case BUILD_ADX_BREG:
				return new Param(null, Param.SR_DX, 0, Param.ART_ASR_BREG);
			case BUILD_AAX_BREG_CREG:
				return new Param(null, Param.SR_AX, 0, Param.ART_ASR_BREG_CREG);
			case BUILD_ABX_BREG_CREG:
				return new Param(null, Param.SR_BX, 0, Param.ART_ASR_BREG_CREG);
			case BUILD_ACX_BREG_CREG:
				return new Param(null, Param.SR_CX, 0, Param.ART_ASR_BREG_CREG);
			case BUILD_ADX_BREG_CREG:
				return new Param(null, Param.SR_DX, 0, Param.ART_ASR_BREG_CREG);
			case BUILD_ANUM_BNUM:
				return new Param(null, v1, v2, Param.ART_ANUM_BNUM);
			case BUILD_ANUM_BNUM_CREG:
				return new Param(null, v1, v2, Param.ART_ANUM_BNUM_CREG);
			case BUILD_AAX_BNUM:
				return new Param(null, Param.SR_AX, v2, Param.ART_ASR_BNUM);
			case BUILD_ABX_BNUM:
				return new Param(null, Param.SR_BX, v2, Param.ART_ASR_BNUM);
			case BUILD_ACX_BNUM:
				return new Param(null, Param.SR_CX, v2, Param.ART_ASR_BNUM);
			case BUILD_ADX_BNUM:
				return new Param(null, Param.SR_DX, v2, Param.ART_ASR_BNUM);
			case BUILD_AAX_BNUM_CREG:
				return new Param(null, Param.SR_AX, v2, Param.ART_ASR_BNUM_CREG);
			case BUILD_ABX_BNUM_CREG:
				return new Param(null, Param.SR_BX, v2, Param.ART_ASR_BNUM_CREG);
			case BUILD_ACX_BNUM_CREG:
				return new Param(null, Param.SR_CX, v2, Param.ART_ASR_BNUM_CREG);
			case BUILD_ADX_BNUM_CREG:
				return new Param(null, Param.SR_DX, v2, Param.ART_ASR_BNUM_CREG);
			case BUILD_ANUM_BAX:
				return new Param(null, v1, Param.SR_AX, Param.ART_ANUM_BSR);
			case BUILD_ANUM_BBX:
				return new Param(null, v1, Param.SR_BX, Param.ART_ANUM_BSR);
			case BUILD_ANUM_BCX:
				return new Param(null, v1, Param.SR_CX, Param.ART_ANUM_BSR);
			case BUILD_ANUM_BDX:
				return new Param(null, v1, Param.SR_DX, Param.ART_ANUM_BSR);
			case BUILD_ANUM_BAX_CREG:
				return new Param(null, v1, Param.SR_AX, Param.ART_ANUM_BSR_CREG);
			case BUILD_ANUM_BBX_CREG:
				return new Param(null, v1, Param.SR_BX, Param.ART_ANUM_BSR_CREG);
			case BUILD_ANUM_BCX_CREG:
				return new Param(null, v1, Param.SR_CX, Param.ART_ANUM_BSR_CREG);
			case BUILD_ANUM_BDX_CREG:
				return new Param(null, v1, Param.SR_DX, Param.ART_ANUM_BSR_CREG);
			case BUILD_AAX_BAX:
				return new Param(null, Param.SR_AX, Param.SR_AX, Param.ART_ASR_BSR);
			case BUILD_AAX_BBX:
				return new Param(null, Param.SR_AX, Param.SR_BX, Param.ART_ASR_BSR);
			case BUILD_AAX_BCX:
				return new Param(null, Param.SR_AX, Param.SR_CX, Param.ART_ASR_BSR);
			case BUILD_AAX_BDX:
				return new Param(null, Param.SR_AX, Param.SR_DX, Param.ART_ASR_BSR);
			case BUILD_ABX_BAX:
				return new Param(null, Param.SR_BX, Param.SR_AX, Param.ART_ASR_BSR);
			case BUILD_ABX_BBX:
				return new Param(null, Param.SR_BX, Param.SR_BX, Param.ART_ASR_BSR);
			case BUILD_ABX_BCX:
				return new Param(null, Param.SR_BX, Param.SR_CX, Param.ART_ASR_BSR);
			case BUILD_ABX_BDX:
				return new Param(null, Param.SR_BX, Param.SR_DX, Param.ART_ASR_BSR);
			case BUILD_ACX_BAX:
				return new Param(null, Param.SR_CX, Param.SR_AX, Param.ART_ASR_BSR);
			case BUILD_ACX_BBX:
				return new Param(null, Param.SR_CX, Param.SR_BX, Param.ART_ASR_BSR);
			case BUILD_ACX_BCX:
				return new Param(null, Param.SR_CX, Param.SR_CX, Param.ART_ASR_BSR);
			case BUILD_ACX_BDX:
				return new Param(null, Param.SR_CX, Param.SR_DX, Param.ART_ASR_BSR);
			case BUILD_ADX_BAX:
				return new Param(null, Param.SR_DX, Param.SR_AX, Param.ART_ASR_BSR);
			case BUILD_ADX_BBX:
				return new Param(null, Param.SR_DX, Param.SR_BX, Param.ART_ASR_BSR);
			case BUILD_ADX_BCX:
				return new Param(null, Param.SR_DX, Param.SR_CX, Param.ART_ASR_BSR);
			case BUILD_ADX_BDX:
				return new Param(null, Param.SR_DX, Param.SR_DX, Param.ART_ASR_BSR);
			case BUILD_AAX_BAX_CREG:
				return new Param(null, Param.SR_AX, Param.SR_AX, Param.ART_ASR_BSR_CREG);
			case BUILD_AAX_BBX_CREG:
				return new Param(null, Param.SR_AX, Param.SR_BX, Param.ART_ASR_BSR_CREG);
			case BUILD_AAX_BCX_CREG:
				return new Param(null, Param.SR_AX, Param.SR_CX, Param.ART_ASR_BSR_CREG);
			case BUILD_AAX_BDX_CREG:
				return new Param(null, Param.SR_AX, Param.SR_DX, Param.ART_ASR_BSR_CREG);
			case BUILD_ABX_BAX_CREG:
				return new Param(null, Param.SR_BX, Param.SR_AX, Param.ART_ASR_BSR_CREG);
			case BUILD_ABX_BBX_CREG:
				return new Param(null, Param.SR_BX, Param.SR_BX, Param.ART_ASR_BSR_CREG);
			case BUILD_ABX_BCX_CREG:
				return new Param(null, Param.SR_BX, Param.SR_CX, Param.ART_ASR_BSR_CREG);
			case BUILD_ABX_BDX_CREG:
				return new Param(null, Param.SR_BX, Param.SR_DX, Param.ART_ASR_BSR_CREG);
			case BUILD_ACX_BAX_CREG:
				return new Param(null, Param.SR_CX, Param.SR_AX, Param.ART_ASR_BSR_CREG);
			case BUILD_ACX_BBX_CREG:
				return new Param(null, Param.SR_CX, Param.SR_BX, Param.ART_ASR_BSR_CREG);
			case BUILD_ACX_BCX_CREG:
				return new Param(null, Param.SR_CX, Param.SR_CX, Param.ART_ASR_BSR_CREG);
			case BUILD_ACX_BDX_CREG:
				return new Param(null, Param.SR_CX, Param.SR_DX, Param.ART_ASR_BSR_CREG);
			case BUILD_ADX_BAX_CREG:
				return new Param(null, Param.SR_DX, Param.SR_AX, Param.ART_ASR_BSR_CREG);
			case BUILD_ADX_BBX_CREG:
				return new Param(null, Param.SR_DX, Param.SR_BX, Param.ART_ASR_BSR_CREG);
			case BUILD_ADX_BCX_CREG:
				return new Param(null, Param.SR_DX, Param.SR_CX, Param.ART_ASR_BSR_CREG);
			case BUILD_ADX_BDX_CREG:
				return new Param(null, Param.SR_DX, Param.SR_DX, Param.ART_ASR_BSR_CREG);
			default:
				throw new IllegalStateException();
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
		case ART_ANUM_BREG_CREG:
			return "[ANUM_BREG_CREG]";
		case ART_ASR_BREG_CREG:
			return "[ASR_BREG_CREG]";
		case ART_ANUM_BNUM_CREG:
			return "[ANUM_BNUM_CREG]";
		case ART_ASR_BNUM_CREG:
			return "[ASR_BNUM_CREG]";
		case ART_ANUM_BSR_CREG:
			return "[ANUM_BSR_CREG]";
		case ART_ASR_BSR_CREG:
			return "[ASR_BSR_CREG]";
		default:
			return "<INVALID[" + art + "]>";
		}
	}
	
}
