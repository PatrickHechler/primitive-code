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
package de.hechler.patrick.codesprachen.simple.symbol.objects.types;

import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.PRIM_BYTE;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.PRIM_DWORD;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.PRIM_FPNUM;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.PRIM_NUM;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.PRIM_UBYTE;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.PRIM_UDWORD;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.PRIM_UNUM;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.PRIM_UWORD;
import static de.hechler.patrick.codesprachen.simple.symbol.interfaces.SimpleExportable.PRIM_WORD;

public enum SimpleTypePrimitive implements SimpleType {
	
	pt_num(64, true), pt_unum(64, false), pt_dword(32, true), pt_udword(32, false), pt_word(16, true),
	pt_uword(16, false), pt_byte(8, true), pt_ubyte(8, false), pt_fpnum(64, true) {
		
		@Override
		public boolean signed() {
			throw new AssertionError("fpnum");
		}
		
	},
	pt_inval(-1, false) {
		
		@Override
		public int bits() {
			throw new AssertionError("inval");
		}
		
		@Override
		public boolean signed() {
			throw new AssertionError("inval");
		}
		
		@Override
		public long byteCount() {
			throw new AssertionError("inval");
		}
		
	};
	
	public static SimpleType get(int bits, boolean signed) {
		switch (bits) {
		case 64:
			if (signed) {
				return pt_num;
			} else {
				return pt_unum;
			}
		case 32:
			if (signed) {
				return pt_dword;
			} else {
				return pt_udword;
			}
		case 16:
			if (signed) {
				return pt_word;
			} else {
				return pt_uword;
			}
		case 8:
			if (signed) {
				return pt_byte;
			} else {
				return pt_ubyte;
			}
		default:
			throw new InternalError();
		}
	}
	
	private final int     bits;
	private final boolean signed;
	
	private SimpleTypePrimitive(int bits, boolean signed) {
		this.bits = bits;
		this.signed = signed;
	}
	
	public int bits() {
		return bits;
	}
	
	public boolean signed() {
		return signed;
	}
	
	@Override
	public boolean isPrimitive() {
		return true;
	}
	
	@Override
	public boolean isPointerOrArray() {
		return false;
	}
	
	@Override
	public boolean isPointer() {
		return false;
	}
	
	@Override
	public boolean isArray() {
		return false;
	}
	
	@Override
	public boolean isStruct() {
		return false;
	}
	
	@Override
	public boolean isFunc() {
		return false;
	}
	
	@Override
	public long byteCount() {
		return bits >> 3;
	}
	
	@Override
	public String toString() {
		return name().substring(3);
	}
	
	@Override
	public void appendToExportStr(StringBuilder build) {
		switch (this) {
		case pt_fpnum -> build.append(PRIM_FPNUM);
		case pt_unum -> build.append(PRIM_UNUM);
		case pt_num -> build.append(PRIM_NUM);
		case pt_udword -> build.append(PRIM_UDWORD);
		case pt_dword -> build.append(PRIM_DWORD);
		case pt_uword -> build.append(PRIM_UWORD);
		case pt_word -> build.append(PRIM_WORD);
		case pt_ubyte -> build.append(PRIM_UBYTE);
		case pt_byte -> build.append(PRIM_BYTE);
		case pt_inval -> throw new AssertionError("the primitive type inval and bool is only for intern use, thus it can not be exported!");
		default -> throw new InternalError("unknown primitive type: " + name());
		}
	}
	
}
