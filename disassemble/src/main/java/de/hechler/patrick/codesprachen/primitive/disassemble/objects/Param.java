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
package de.hechler.patrick.codesprachen.primitive.disassemble.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.*;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ANUM_BNUM;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ANUM_BADR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_ANUM_BREG;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_AREG;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_AREG_BNUM;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_AREG_BADR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_ART_AREG_BREG;

import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;

public class Param {
	
	public static final int ART_ANUM      = PARAM_ART_ANUM;
	public static final int ART_AREG      = PARAM_ART_AREG;
	public static final int ART_ANUM_BADR = PARAM_ART_ANUM_BADR;
	public static final int ART_AREG_BADR = PARAM_ART_AREG_BADR;
	public static final int ART_ANUM_BNUM = PARAM_ART_ANUM_BNUM;
	public static final int ART_AREG_BNUM = PARAM_ART_AREG_BNUM;
	public static final int ART_ANUM_BREG = PARAM_ART_ANUM_BREG;
	public static final int ART_AREG_BREG = PARAM_ART_AREG_BREG;
	
	
	public static final int REG_IP     = IP;
	public static final int REG_SP     = SP;
	public static final int REG_STATUS = STATUS;
	public static final int REG_INTCNT = INTCNT;
	public static final int REG_INTP   = INTP;
	public static final int REG_ERRNO  = ERRNO;
	public static final int REG_X_SUB  = X_ADD;
	
	public final long num;
	public final long off;
	public final int  art;
	
	private Param(long num, long off, int art) {
		this.num = num;
		this.off = off;
		this.art = art;
	}
	
	/**
	 * can be used to build {@link Param}s.
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
			case ART_ANUM, ART_ANUM_BADR:
				zeroCheck(v2);
				return new Param(v1, 0, art);
			case ART_ANUM_BNUM:
				return new Param(v1, v2, art);
			case ART_ANUM_BREG:
				checkSR(v2);
				return new Param(v1, v2, art);
			case ART_AREG, ART_AREG_BADR:
				checkSR(v1);
				zeroCheck(v2);
				return new Param(v1, 0, art);
			case ART_AREG_BNUM:
				checkSR(v1);
				return new Param(v1, v2, art);
			case ART_AREG_BREG:
				checkSR(v1);
				checkSR(v2);
				return new Param(v1, v2, art);
			default:
				throw new NoCommandException("unknown art");
			}
		}
		
	}
	
	public static void zeroCheck(long val) throws NoCommandException {
		if (val != 0) { throw new NoCommandException("value is not 0: " + val); }
	}
	
	public static void zeroCheck(byte val) throws NoCommandException {
		if (val != 0) { throw new NoCommandException("value is not 0: " + val); }
	}
	
	public static String artToString(int art) {
		return switch (art) {
		case ART_ANUM -> "[ANUM]";
		case ART_AREG -> "[ASR]";
		case ART_ANUM_BADR -> "[ANUM_BREG]";
		case ART_AREG_BADR -> "[ASR_BREG]";
		case ART_ANUM_BNUM -> "[ANUM_BNUM]";
		case ART_AREG_BNUM -> "[ASR_BNUM]";
		case ART_ANUM_BREG -> "[ANUM_BSR]";
		case ART_AREG_BREG -> "[ASR_BSR]";
		default -> "<INVALID[" + art + "]>";
		};
	}
	
	public static void checkSR(long num) throws NoCommandException {
		if ((num & 0xFFFFFFFFFFFFFF00L) != 0) { throw new NoCommandException("this num is no SR: num=" + num + " AX=0 BX=1 CX=2 DX=3"); }
	}
	
	public void checkNoConst() throws NoCommandException {
		if (art == ART_ANUM) { throw new NoCommandException("this is not allowed to be a constant!"); }
	}
	
	public int length() {
		return switch (art) {
		case ART_AREG, ART_AREG_BADR, ART_AREG_BREG -> 0;
		case ART_ANUM, ART_ANUM_BADR, ART_ANUM_BREG, ART_AREG_BNUM -> 8;
		case ART_ANUM_BNUM -> 16;
		default -> throw new InternalError("unknown art: " + art);
		};
	}
	
	@Override
	public String toString() {
		return switch (art) {
		case ART_AREG -> getReg(num);
		case ART_AREG_BADR -> "[" + getReg(num) + "]";
		case ART_AREG_BREG -> "[" + getReg(num) + " + " + getReg(off) + "]";
		case ART_ANUM -> Long.toString(num);
		case ART_ANUM_BADR -> "[" + num + "]";
		case ART_ANUM_BREG -> "[" + num + " + " + getReg(off) + "]";
		case ART_AREG_BNUM -> "[" + getReg(num) + " + " + off + "]";
		case ART_ANUM_BNUM -> "[" + num + " + " + off + "]";
		default -> throw new InternalError("unknown art: " + art);
		};
	}
	
	private static String getReg(long reg) {
		if ((reg & 0x00000000000000FFL) != reg) { throw new InternalError("this is no register"); }
		return switch ((int) reg) {
		case REG_IP -> "IP";
		case REG_SP -> "SP";
		case REG_STATUS -> "STATUS";
		case REG_INTCNT -> "INTCNT";
		case REG_INTP -> "INTP";
		case REG_ERRNO -> "ERRNO";
		default -> {
			String num = Integer.toHexString((int) reg - REG_X_SUB).toUpperCase();
			if (num.length() == 1) yield "X0" + num;
			else yield "X" + num;
		}
		};
	}
	
}
