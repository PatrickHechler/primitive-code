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

import static de.hechler.patrick.codesprachen.primitive.core.utils.Convert.*;

import java.util.Arrays;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.disassemble.enums.Commands.ParamArt;
import de.hechler.patrick.codesprachen.primitive.disassemble.interfaces.LabelNameGenerator;

public class Command {
	
	public final Commands            cmd;
	public final Param               p1;
	public final Param               p2;
	public final Param               p3;
	public final long                relativeLabel;
	private final LabelNameGenerator lng;
	
	
	
	private Command(Commands cmd, Param p1, Param p2, Param p3, long relativeLabel, LabelNameGenerator lng) {
		this.cmd = cmd;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.relativeLabel = relativeLabel;
		this.lng = lng;
	}
	
	private Command(Commands cmd, Param p1, Param p2, long relativeLabel, LabelNameGenerator lng) {
		this(cmd, p1, p2, null, relativeLabel, lng);
	}
	
	public Command(Commands cmd) { this(cmd, null, null, -1L, null); }
	
	public Command(Commands cmd, Param p1) { this(cmd, p1, null, -1L, null); }
	
	public Command(Commands cmd, Param p1, Param p2) { this(cmd, p1, p2, -1L, null); }
	
	public Command(Commands cmd, Param p1, Param p2, Param p3) { this(cmd, p1, p2, p3, -1L, null); }
	
	public Command(Commands cmd, long relativeLabel, LabelNameGenerator lng) {
		this(cmd, null, null, relativeLabel, lng == null ? LabelNameGenerator.SIMPLE_GEN : lng);
	}
	
	@Override
	public String toString() {
		return toString(0L);
	}
	
	public String toString(long pos) {
		StringBuilder build = new StringBuilder(this.cmd.toString());
		switch (this.cmd.art) {
		case LABEL_OR_CONST:
			build.append(' ').append(this.lng.generateName(pos + this.relativeLabel));
			break;
		case NO_PARAMS:
			break;
		case ONE_PARAM_ALLOW_CONST, ONE_PARAM_NO_CONST:
			build.append(' ').append(this.p1);
			break;
		case TWO_PARAMS_ALLOW_CONSTS, TWO_PARAMS_NO_CONSTS, TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST, TWO_PARAMS_P1_NO_CONST_P2_COMPILE_CONST:
			build.append(' ').append(this.p1).append(", ").append(this.p2);
			break;
		case THREE_PARAMS_P1_NO_CONST_P2_ALLOW_CONST_P3_COMPILE_CONST:
			build.append(' ').append(this.p1).append(", ").append(this.p2).append(", ").append(this.p3);
			break;
		default:
			throw new AssertionError("unknown art: "+ this.cmd.art);
		}
		return build.toString();
	}
	
	public static class ConstantPoolCmd extends Command {
		
		private byte[] bytes = new byte[0];
		
		public ConstantPoolCmd() { super(null, null, null, -1, null); }
		
		public void add(long val) {
			final int oldlen = bytes.length;
			bytes = Arrays.copyOf(bytes, oldlen + 8);
			convertLongToByteArr(bytes, oldlen, val);
		}
		
		public void add(long[] vals) {
			int off = bytes.length;
			bytes = Arrays.copyOf(bytes, off + (vals.length * 8));
			for (int i = 0; i < vals.length; i++, off += 8) {
				convertLongToByteArr(bytes, off, vals[i]);
			}
		}
		
		public void add(byte[] vals) {
			final int oldlen = bytes.length;
			bytes = Arrays.copyOf(bytes, oldlen + vals.length);
			System.arraycopy(vals, 0, bytes, oldlen, vals.length);
		}
		
		public void add(byte[] vals, int len) {
			final int oldlen = bytes.length;
			bytes = Arrays.copyOf(bytes, oldlen + len);
			System.arraycopy(vals, 0, bytes, oldlen, len);
		}
		
		public void add(ConstantPoolCmd cp) {
			final int oldlen = bytes.length;
			bytes = Arrays.copyOf(bytes, oldlen + cp.bytes.length);
			System.arraycopy(cp.bytes, 0, bytes, oldlen, cp.bytes.length);
		}
		
		@Override
		public String toString() {
			StringBuilder build = new StringBuilder(":");
			int           off;
			for (off = 0; off < bytes.length - 8; off += 8) {
				String prefix = "\n    UHEX-";
				build.append(convertByteArrToHexString(prefix, bytes, off, 8, ""));
			}
			if (bytes.length - off == 8) {
				String prefix = "\n    UHEX-";
				build.append(convertByteArrToHexString(prefix, bytes, off, 8, "\n>"));
			} else {
				for (; off < bytes.length; off++) {
					build.append(convertByteArrToHexString("\n    B-HEX-", bytes, off, 1, ""));
				}
				build.append("\n>");
			}
			return build.toString();
		}
		
		@Override
		public String toString(long pos) { return toString(); }
		
		@Override
		public int length() { return bytes.length; }
		
		public void get(byte[] bytes, int boff, int off, int len) {
			System.arraycopy(this.bytes, off, bytes, boff, len);
		}
		
		public void truncate(int newLen) {
			if (newLen >= bytes.length) {
				throw new IllegalArgumentException();
			}
			this.bytes = Arrays.copyOf(bytes, newLen);
		}
		
	}
	
	public int length() {
		if (cmd.art == ParamArt.LABEL_OR_CONST) { return 8; }
		if (p3 != null) {
			int len = p1.length();
			len += p2.length();
			len += p3.length();
			return 8 + len;
		} else if (p2 != null) {
			int len = p1.length();
			len += p2.length();
			return 8 + len;
		} else if (p1 != null) {
			return 8 + p1.length();
		} else {
			return 8;
		}
	}
	
	public byte[] genBytes() {
		byte[] bytes = new byte[length()];
		bytes[0] = (byte) cmd.num;
		bytes[1] = (byte) (cmd.num >>> 8);
		if (p1 != null) {
			int firstI;
			int retI;
			bytes[2] = (byte) p1.art;
			switch (p1.art) {
			case Param.ART_ANUM, Param.ART_ANUM_BADR:
				convertLongToByteArr(bytes, 8, p1.num);
				firstI = 7;
				retI = 16;
				break;
			case Param.ART_ANUM_BNUM:
				convertLongToByteArr(bytes, 8, p1.num);
				convertLongToByteArr(bytes, 16, p1.off);
				firstI = 7;
				retI = 24;
				break;
			case Param.ART_ANUM_BREG:
				convertLongToByteArr(bytes, 8, p1.num);
				bytes[7] = (byte) p1.off;
				firstI = 6;
				retI = 16;
				break;
			case Param.ART_AREG, Param.ART_AREG_BADR:
				bytes[7] = (byte) p1.off;
				firstI = 6;
				retI = 8;
				break;
			case Param.ART_AREG_BNUM:
				bytes[7] = (byte) p1.num;
				convertLongToByteArr(bytes, 8, p1.off);
				firstI = 6;
				retI = 2;
				break;
			case Param.ART_AREG_BREG:
				bytes[7] = (byte) p1.num;
				bytes[6] = (byte) p1.off;
				firstI = 5;
				retI = 8;
				break;
			default:
				throw new InternalError("unknown art: " + p1.art);
			}
			if (p2 != null) {
				bytes[1] = (byte) p2.art;
				switch (p2.art) {
				case Param.ART_ANUM, Param.ART_ANUM_BADR:
					convertLongToByteArr(bytes, retI, p2.num);
					break;
				case Param.ART_ANUM_BNUM:
					convertLongToByteArr(bytes, retI, p2.num);
					convertLongToByteArr(bytes, retI + 8, p2.off);
					break;
				case Param.ART_ANUM_BREG:
					convertLongToByteArr(bytes, retI, p2.num);
					bytes[firstI] = (byte) p2.off;
					break;
				case Param.ART_AREG, Param.ART_AREG_BADR:
					bytes[firstI] = (byte) p2.off;
					break;
				case Param.ART_AREG_BNUM:
					bytes[firstI] = (byte) p2.num;
					convertLongToByteArr(bytes, retI, p2.off);
					break;
				case Param.ART_AREG_BREG:
					bytes[firstI] = (byte) p2.num;
					bytes[firstI] = (byte) p2.off;
					break;
				default:
					throw new InternalError("unknown art: " + p2.art);
				}
			}
		} else {
			assert p2 == null;
			if (cmd.art == ParamArt.LABEL_OR_CONST) { convertLongToByteArr(bytes, 8, relativeLabel); }
		}
		return bytes;
	}
	
}
