package de.hechler.patrick.codesprachen.primitive.disassemble.objects;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.disassemble.enums.Commands.ParamArt;
import de.hechler.patrick.codesprachen.primitive.disassemble.interfaces.LabelNameGenerator;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert;

public class Command {
	
	public final Commands cmd;
	public final Param p1;
	public final Param p2;
	public final long relativeLabel;
	private LabelNameGenerator lng;
	
	
	
	private Command(Commands cmd, Param p1, Param p2, long relativeLabel, LabelNameGenerator lng) {
		this.cmd = cmd;
		this.p1 = p1;
		this.p2 = p2;
		this.relativeLabel = relativeLabel;
		this.lng = lng;
	}
	
	public Command(Commands cmd, Param p1, Param p2) {
		this(cmd, p1, p2, -1, null);
	}
	
	public Command(Commands cmd) {
		this(cmd, null, null, -1, null);
	}
	
	public Command(Commands cmd, Param p1) {
		this(cmd, p1, null, -1, null);
	}
	
	public Command(Commands cmd, long relativeLabel, LabelNameGenerator lng) {
		this(cmd, null, null, relativeLabel, lng);
	}
	
	@Override
	public String toString() {
		StringBuilder build = new StringBuilder(cmd.toString());
		switch (cmd.art) {
		case label:
			build.append(' ').append("DEST-POS=" + relativeLabel);
			break;
		case noParams:
			break;
		case oneParamAllowConst:
		case oneParamNoConst:
			build.append(' ').append(p1);
			break;
		case twoParamsAllowConsts:
		case twoParamsNoConsts:
		case twoParamsP1NoConstP2AllowConst:
			build.append(' ').append(p1).append(", ").append(p2);
			break;
		default:
			break;
		}
		return build.toString();
	}
	
	public String toString(List <Command> cmds, Map <Long, Integer> indices) {
		StringBuilder build = new StringBuilder(cmd.toString());
		switch (cmd.art) {
		case label:
			build.append(' ').append(lng.generateName(relativeLabel, cmds, (int) indices.get((Long) relativeLabel)));
			break;
		case noParams:
			break;
		case oneParamAllowConst:
		case oneParamNoConst:
			build.append(' ').append(p1);
			break;
		case twoParamsAllowConsts:
		case twoParamsNoConsts:
		case twoParamsP1NoConstP2AllowConst:
			build.append(' ').append(p1).append(", ").append(p2);
			break;
		default:
			break;
		}
		return build.toString();
	}
	
	public static class ConstantPoolCmd extends Command {
		
		private byte[] bytes = new byte[0];
		
		public ConstantPoolCmd() {
			super(null, null, null, -1, null);
		}
		
		public void add(long val) {
			final int oldlen = bytes.length;
			bytes = Arrays.copyOf(bytes, oldlen + 8);
			Convert.convertLongToByteArr(bytes, oldlen, val);
		}
		
		public void add(long[] vals) {
			int off = bytes.length;
			bytes = Arrays.copyOf(bytes, off + (vals.length * 8));
			for (int i = 0; i < vals.length; i ++ , off += 8) {
				Convert.convertLongToByteArr(bytes, off, vals[i]);
			}
		}
		
		public void add(byte[] vals) {
			final int off = bytes.length;
			bytes = Arrays.copyOf(bytes, off + vals.length);
			System.arraycopy(vals, 0, bytes, off, vals.length);
		}
		
		@Override
		public String toString() {
			StringBuilder build = new StringBuilder(":").append('\n').append("HEX:");
			String hexStr;
			int off;
			for (off = 0; off < bytes.length - 8; off += 8) {
				hexStr = PrimitiveDisassembler.bytesToHexString(bytes, off, 8);
				build.append(hexStr).append('\n').append("    ");
			}
			hexStr = PrimitiveDisassembler.bytesToHexString(bytes, off, bytes.length - off);
			return build.append(hexStr).append('\n').append('>').toString();
		}
		
		@Override
		public String toString(List <Command> cmds, Map <Long, Integer> indices) {
			return toString();
		}
		
		public int length() {
			return bytes.length;
		}
		
		public void get(byte[] bytes, int boff, int off, int len) {
			System.arraycopy(this.bytes, off, bytes, boff, len);
		}
		
	}
	
	public int length() {
		if (relativeLabel != -1) {
			return 16;
		}
		if (p2 != null) {
			int len = p1.length();
			len += p2.length();
			return 8 + len;
		} else if (p1 != null) {
			return 8 + p1.length();
		} else {
			return 8;
		}
	}
	
	public byte[] genBytes(long pos) {
		byte[] bytes = new byte[length()];
		bytes[0] = (byte) cmd.num();
		if (p1 != null) {
			int firstI, retI;
			bytes[1] = (byte) p1.art;
			switch (p1.art) {
			case Param.ART_ANUM:
			case Param.ART_ANUM_BREG:
				Convert.convertLongToByteArr(bytes, 8, p1.num);
				firstI = 7;
				retI = 16;
				break;
			case Param.ART_ANUM_BNUM:
				Convert.convertLongToByteArr(bytes, 8, p1.num);
				Convert.convertLongToByteArr(bytes, 16, p1.off);
				firstI = 7;
				retI = 24;
				break;
			case Param.ART_ANUM_BSR:
				Convert.convertLongToByteArr(bytes, 8, p1.num);
				bytes[7] = (byte) p1.off;
				firstI = 6;
				retI = 16;
				break;
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
				bytes[7] = (byte) p1.off;
				firstI = 6;
				retI = 8;
				break;
			case Param.ART_ASR_BNUM:
				bytes[7] = (byte) p1.num;
				Convert.convertLongToByteArr(bytes, 8, p1.off);
				firstI = 6;
				retI = 2;
				break;
			case Param.ART_ASR_BSR:
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
				case Param.ART_ANUM:
				case Param.ART_ANUM_BREG:
					Convert.convertLongToByteArr(bytes, retI, p2.num);
					break;
				case Param.ART_ANUM_BNUM:
					Convert.convertLongToByteArr(bytes, retI, p2.num);
					Convert.convertLongToByteArr(bytes, retI + 8, p2.off);
					break;
				case Param.ART_ANUM_BSR:
					Convert.convertLongToByteArr(bytes, retI, p2.num);
					bytes[firstI] = (byte) p2.off;
					break;
				case Param.ART_ASR:
				case Param.ART_ASR_BREG:
					bytes[firstI] = (byte) p2.off;
					break;
				case Param.ART_ASR_BNUM:
					bytes[firstI] = (byte) p2.num;
					Convert.convertLongToByteArr(bytes, retI, p2.off);
					break;
				case Param.ART_ASR_BSR:
					bytes[firstI] = (byte) p2.num;
					bytes[firstI] = (byte) p2.off;
					break;
				default:
					throw new InternalError("unknown art: " + p2.art);
				}
			}
		} else {
			assert p2 == null;
			if (cmd.art == ParamArt.label) {
				Convert.convertLongToByteArr(bytes, 8, relativeLabel);
			}
		}
		return bytes;
	}
	
}
