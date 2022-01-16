package de.hechler.patrick.codesprachen.primitive.disassemble.objects;

import java.util.List;
import java.util.Map;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.disassemble.enums.Commands.ParamArt;
import de.hechler.patrick.codesprachen.primitive.disassemble.interfaces.LabelNameGenerator;

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
		switch(cmd.art) {
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
	
	public String toString(List<Command> cmds, Map<Long,Integer> indices) {
		StringBuilder build = new StringBuilder(cmd.toString());
		switch(cmd.art) {
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
		
		private static final String ENDL = System.lineSeparator();
		private static final String ZEROS = "000000000000000";
		
		private List <Long> longs;
		
		public ConstantPoolCmd() {
			super(null, null, null, -1, null);
		}
		
		public void add(long val) {
			longs.add((Long) val);
		}
		
		public void add(long[] vals) {
			for (long val : vals) {
				longs.add((Long) val);
			}
		}
		
		@Override
		public String toString() {
			StringBuilder build = new StringBuilder(":").append(ENDL).append("HEX:");
			for (int i = 0; i < longs.size() - 1; i ++ ) {
				String zw = Long.toHexString((long) longs.get(i));
				build.append(ZEROS.substring(16 - zw.length())).append(zw).append(ENDL).append('\t');
			}
			String zw = Long.toHexString((long) longs.get(longs.size() - 1));
			build.append(ZEROS.substring(16 - zw.length())).append(zw).append(ENDL);
			return build.append('>').toString();
		}
		
		public int length() {
			return longs.size();
		}

		public long get(int ii) {
			return longs.get(ii);
		}
		
	}
	
	public int length() {
		if (relativeLabel != -1) {
			return 2;
		}
		if (p2 != null) {
			int len = p1.length();
			len += p2.length();
			return len;
		} else if (p1 != null) {
			return p1.length();
		} else {
			return 1;
		}
	}
	
	public long[] genLongs(long pos) {
		long[] ret = new long[length()];
		byte[] first = new byte[8];
		first[0] = (byte) cmd.num();
		int firstI = -1, retI = -1;
		if (p1 != null) {
			first[1] = (byte) p1.art;
			switch (p1.art) {
			case Param.ART_ANUM:
			case Param.ART_ANUM_BREG:
				ret[1] = p1.num;
				firstI = 7;
				retI = 2;
				break;
			case Param.ART_ANUM_BNUM:
				ret[1] = p1.num;
				ret[2] = p1.off;
				firstI = 7;
				retI = 3;
				break;
			case Param.ART_ANUM_BSR:
				ret[1] = p1.num;
				first[7] = (byte) p1.off;
				firstI = 6;
				retI = 2;
				break;
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
				first[7] = (byte) p1.off;
				firstI = 6;
				retI = 1;
				break;
			case Param.ART_ASR_BNUM:
				first[7] = (byte) p1.num;
				ret[1] = p1.off;
				firstI = 6;
				retI = 2;
				break;
			case Param.ART_ASR_BSR:
				first[7] = (byte) p1.num;
				first[6] = (byte) p1.off;
				firstI = 5;
				retI = 1;
				break;
			default:
				throw new InternalError("unknown art: " + p1.art);
			}
		}
		if (p2 != null) {
			first[1] = (byte) p2.art;
			switch (p2.art) {
			case Param.ART_ANUM:
			case Param.ART_ANUM_BREG:
				ret[retI] = p2.num;
				break;
			case Param.ART_ANUM_BNUM:
				ret[retI] = p2.num;
				ret[retI + 1] = p2.off;
				break;
			case Param.ART_ANUM_BSR:
				ret[retI] = p2.num;
				first[firstI] = (byte) p2.off;
				break;
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
				first[firstI] = (byte) p2.off;
				break;
			case Param.ART_ASR_BNUM:
				first[firstI] = (byte) p2.num;
				ret[retI] = p2.off;
				break;
			case Param.ART_ASR_BSR:
				first[firstI] = (byte) p2.num;
				first[firstI] = (byte) p2.off;
				break;
			default:
				throw new InternalError("unknown art: " + p2.art);
			}
		}
		ret[0] = PrimitiveDisassembler.convertLong(first);
		if (cmd.art == ParamArt.label) {
			ret[1] = relativeLabel - pos;
		}
		return ret;
	}
	
}
