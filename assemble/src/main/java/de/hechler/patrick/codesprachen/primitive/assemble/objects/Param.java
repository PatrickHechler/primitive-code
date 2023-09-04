// This file is part of the Primitive Code Project
// DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
// Copyright (C) 2023 Patrick Hechler
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.*;

@SuppressWarnings("javadoc")
public class Param {
	
	public static final int ART_LABEL = -1;
	
	public static final int ART_ANUM      = PARAM_ART_ANUM;
	public static final int ART_AREG      = PARAM_ART_AREG;
	public static final int ART_ANUM_BADR = PARAM_ART_ANUM_BADR;
	public static final int ART_AREG_BADR = PARAM_ART_AREG_BADR;
	public static final int ART_ANUM_BNUM = PARAM_ART_ANUM_BNUM;
	public static final int ART_AREG_BNUM = PARAM_ART_AREG_BNUM;
	public static final int ART_ANUM_BREG = PARAM_ART_ANUM_BREG;
	public static final int ART_AREG_BREG = PARAM_ART_AREG_BREG;
	
	public final String label;
	public final long   num;
	public final long   off;
	public final int    art;
	
	private Param(String label, long num, long off, int art) {
		this.label = label;
		this.num   = num;
		this.off   = off;
		this.art   = art;
	}
	
	public static Param createLabel(String label) {
		return new Param(label, 0L, 0L, ART_LABEL);
	}
	
	/**
	 * can Build all non Label {@link Param parameters}.
	 */
	public static class ParamBuilder {
		
		public static final int SR_IP     = IP;
		public static final int SR_SP     = SP;
		public static final int SR_STATUS = STATUS;
		public static final int SR_INTCNT = INTCNT;
		public static final int SR_INTP   = INTP;
		public static final int SR_ERRNO  = ERRNO;
		public static final int SR_X_ADD  = X_ADD;
		
		public static final int A_NUM = 0x00000001;
		public static final int A_XX  = 0x00000002;
		public static final int B_REG = 0x00000010;
		public static final int B_NUM = 0x00000020;
		public static final int B_XX  = 0x00000040;
		
		private static final int BUILD_ANUM      = A_NUM;
		private static final int BUILD_AXX       = A_XX;
		private static final int BUILD_ANUM_BREG = A_NUM | B_REG;
		private static final int BUILD_AXX_BREG  = A_XX | B_REG;
		private static final int BUILD_ANUM_BNUM = A_NUM | B_NUM;
		private static final int BUILD_AXX_BNUM  = A_XX | B_NUM;
		private static final int BUILD_ANUM_BXX  = A_NUM | B_XX;
		private static final int BUILD_AXX_BXX   = A_XX | B_XX;
		
		public int  art = 0;
		public long v1  = 0L;
		public long v2  = 0L;
		
		public ParamBuilder() { /* nothing to be done */ }
		
		public boolean isValid() {
			try {
				build();
				return true;
			} catch (@SuppressWarnings("unused") IllegalStateException e) {
				return false;
			}
		}
		
		public Param build() {
			return build(this.art, this.v1, this.v2);
		}
		
		public Param build2() {
			return build2(this.art, this.v1, this.v2);
		}
		
		public static Param build(int art, long v1) {
			if ((art & (B_NUM | B_XX)) != 0) {
				throw new IllegalArgumentException("type specifies the use of v2, but i do not have a v2 value!");
			}
			return build(art, v1, 0L);
		}
		
		public static Param build2(int art, long v1) {
			if ((art & (B_NUM | B_XX)) != 0) {
				throw new IllegalArgumentException("type specifies the use of v2, but i do not have a v2 value!");
			}
			return build2(art, v1, 0L);
		}
		
		public static Param build2(int art, long v1, long v2) {
			switch (art) {
			case BUILD_ANUM, BUILD_ANUM_BREG, BUILD_AXX, BUILD_AXX_BREG, BUILD_AXX_BXX:
				return build(art, v1, v2);
			case BUILD_ANUM_BNUM:
				throw new IllegalStateException("ANUM BNUM on build2");
			case BUILD_AXX_BNUM:
				Param.checkSR(v1);
				if (v2 == 0L) {
					return build(BUILD_AXX_BREG, v1, 0L);
				}
				return new Param(null, v1, v2, Param.ART_AREG_BNUM);
			case BUILD_ANUM_BXX:
				Param.checkSR(v2);
				if (v1 == 0L) {
					return build(BUILD_AXX_BREG, v2, 0L);
				}
				return new Param(null, v1, v2, Param.ART_ANUM_BREG);
			default:
				throw new IllegalStateException("art=" + Integer.toHexString(art));
			}
		}
		
		public static Param build(int art, long v1, long v2) {
			switch (art) {
			case BUILD_ANUM:
				Param.checkZero(v2);
				return new Param(null, v1, 0, Param.ART_ANUM);
			case BUILD_ANUM_BREG:
				Param.checkZero(v2);
				return new Param(null, v1, 0, Param.ART_ANUM_BADR);
			case BUILD_AXX:
				Param.checkSR(v1);
				Param.checkZero(v2);
				return new Param(null, v1, 0, Param.ART_AREG);
			case BUILD_AXX_BREG:
				Param.checkSR(v1);
				Param.checkZero(v2);
				return new Param(null, v1, 0, Param.ART_AREG_BADR);
			case BUILD_ANUM_BNUM:
				return new Param(null, v1, v2, Param.ART_ANUM_BNUM);
			case BUILD_AXX_BNUM:
				Param.checkSR(v1);
				return new Param(null, v1, v2, Param.ART_AREG_BNUM);
			case BUILD_ANUM_BXX:
				Param.checkSR(v2);
				return new Param(null, v1, v2, Param.ART_ANUM_BREG);
			case BUILD_AXX_BXX:
				Param.checkSR(v1);
				Param.checkSR(v2);
				return new Param(null, v1, v2, Param.ART_AREG_BREG);
			default:
				throw new IllegalStateException("art=" + Integer.toHexString(art));
			}
		}
		
	}
	
	public static String artToString(int art) {
		switch (art) {
		case ART_ANUM:
			return "[ANUM]";
		case ART_AREG:
			return "[ASR]";
		case ART_ANUM_BADR:
			return "[ANUM_BREG]";
		case ART_AREG_BADR:
			return "[ASR_BREG]";
		case ART_ANUM_BNUM:
			return "[ANUM_BNUM]";
		case ART_AREG_BNUM:
			return "[ASR_BNUM]";
		case ART_ANUM_BREG:
			return "[ANUM_BSR]";
		case ART_AREG_BREG:
			return "[ASR_BSR]";
		default:
			return "<INVALID[" + art + "]>";
		}
	}
	
	public static void checkZero(long num) {
		if (num != 0) {
			throw new IllegalStateException("this num is not zero: num=0x" + Long.toHexString(num));
		}
	}
	
	public static void checkSR(long num) {
		if (num > 0xFF) {
			throw new IllegalStateException("this num is no SR: num=0x" + Long.toHexString(num));
		}
	}
	
	public static void checkByte(long num) {
		if (num > 0xFF) {
			throw new IllegalStateException("this num is no byte value: num=0x" + Long.toHexString(num));
		}
	}
	
	static String toSRString(long sr) {
		assert sr <= 0xFF;
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
			return "X" + ((sr < 0x10) ? ("0" + Integer.toHexString(reg - ParamBuilder.SR_X_ADD).toUpperCase())
					: Integer.toHexString(reg - ParamBuilder.SR_X_ADD).toUpperCase());
		}
	}
	
	@Override
	public String toString() {
		if (this.label != null) {
			return this.label;
		}
		switch (this.art) {
		case ART_ANUM:
			return Long.toString(this.num);
		case ART_AREG:
			return toSRString(this.num);
		case ART_ANUM_BADR:
			return "[" + Long.toString(this.num) + "]";
		case ART_AREG_BADR:
			return "[" + toSRString(this.num) + "]";
		case ART_ANUM_BNUM:
			return "[" + Long.toString(this.num) + "+" + Long.toString(this.off) + "]";
		case ART_AREG_BNUM:
			return "[" + toSRString(this.num) + "+" + Long.toString(this.off) + "]";
		case ART_ANUM_BREG:
			return "[" + Long.toString(this.num) + "+" + toSRString(this.off) + "]";
		case ART_AREG_BREG:
			return "[" + toSRString(this.num) + "+" + toSRString(this.off) + "]";
		default:
			throw new InternalError("unknown param art: " + this.art);
		}
	}
	
}
