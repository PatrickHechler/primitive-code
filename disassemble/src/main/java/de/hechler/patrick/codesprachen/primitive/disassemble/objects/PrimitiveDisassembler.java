package de.hechler.patrick.codesprachen.primitive.disassemble.objects;

import static de.hechler.patrick.codesprachen.primitive.core.utils.Convert.convertByteArrToHexString;
import static de.hechler.patrick.codesprachen.primitive.core.utils.Convert.convertByteArrToLong;
import static de.hechler.patrick.codesprachen.primitive.core.utils.Convert.convertLongToHexString;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_A_NUM;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_A_SR;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_B_NUM;
import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmConstants.PARAM_B_SR;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hechler.patrick.codesprachen.primitive.disassemble.PrimitiveDisassemblerMain;
import de.hechler.patrick.codesprachen.primitive.disassemble.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.disassemble.enums.DisasmMode;
import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;
import de.hechler.patrick.codesprachen.primitive.disassemble.interfaces.LabelNameGenerator;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Command.ConstantPoolCmd;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Param.ParamBuilder;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.LongArrayInputStream;

public class PrimitiveDisassembler implements Closeable {
	
	private static final String      NO_VALID_CMD_ART = "the command has no valid art";
	private static final String      UNKNOWN_MODE     = "unknown mode: ";
	private final Writer             out;
	private final LabelNameGenerator lng;
	private final DisasmMode         mode;
	
	public PrimitiveDisassembler(Writer out) {
		this(DisasmMode.analysable, t -> convertLongToHexString("L_", t), out);
	}
	
	public PrimitiveDisassembler(LabelNameGenerator lng, Writer out) {
		this(DisasmMode.analysable, lng, out);
	}
	
	public PrimitiveDisassembler(DisasmMode mode, Writer out) {
		this(mode, mode == DisasmMode.analysable ? t -> convertLongToHexString("L_", t) : LabelNameGenerator.SIMPLE_GEN,
				out);
	}
	
	public PrimitiveDisassembler(DisasmMode mode, LabelNameGenerator lng, Writer out) {
		this.out = out;
		this.lng = lng;
		this.mode = mode;
	}
	
	
	
	public void deassemble(long pos, long[] cmds) throws IOException {
		deassemble(pos, new LongArrayInputStream(cmds));
	}
	
	public void deassemble(long pos, byte[] cmds) throws IOException {
		deassemble(pos, new ByteArrayInputStream(cmds));
	}
	
	public void deassemble(long pos, InputStream in) throws IOException {
		List<Command> cmds   = new ArrayList<>();
		Set<Long>     labels = new HashSet<>();
		PrimitiveDisassemblerMain.LOG.fine("read now");
		read(pos, in, cmds, labels);
		PrimitiveDisassemblerMain.LOG.fine("write now");
		write(pos, cmds, labels);
		out.flush();
	}
	
	private void write(long pos, List<Command> cmds, Set<Long> labels) throws IOException {
		switch (mode) {
		case analysable:
			break;
		case executable:
			out.write("$not-align\n");
			break;
		default:
			throw new InternalError(UNKNOWN_MODE + mode.name());
		}
		for (int i = 0; i < cmds.size(); i++) {
			if (labels.contains(pos)) {
				switch (mode) {
				case analysable -> out.write("@L_");
				case executable -> {
					out.write('@');
					out.write(lng.generateName(pos));
					out.write('\n');
				}
				default -> throw new InternalError(UNKNOWN_MODE + mode.name());
				}
			} else {
				switch (mode) {
				case analysable -> out.write("   ");
				case executable -> {}
				default -> throw new InternalError(UNKNOWN_MODE + mode.name());
				}
			}
			Command cmd = cmds.get(i);
			if (cmd instanceof ConstantPoolCmd cp) {
				switch (mode) {
				case analysable -> {
					boolean first  = true;
					String  prefix = "";
					byte[]  bytes  = new byte[8];
					int     off;
					for (off = 0; off <= cp.length() - 8; off += 8) {
						cp.get(bytes, 0, off, 8);
						out.write(convertLongToHexString(prefix, pos,
								convertByteArrToHexString(" -> ", bytes, string(" ", bytes, 0, 8))));
						out.write('\n');
						if (first) {
							first = false;
							prefix = "   ";
						}
						pos += 8;
					}
					if (off < cp.length()) {
						int len = cp.length() - off;
						cp.get(bytes, 0, off, len);
						int strlen = 16 + 4 - (len * 2);
						out.write(convertLongToHexString(prefix, pos,
								convertByteArrToHexString(" ->                 ".substring(0, strlen), bytes, 0, len,
										string(" ", bytes, 0, len))));
						out.write('\n');
					}
				}
				case executable -> {
					out.write(cp.toString());
					out.write('\n');
					pos += cp.length();
				}
				default -> throw new InternalError(UNKNOWN_MODE + mode.name());
				}
				continue;
			}
			switch (mode) {
			case analysable -> {
				byte[] bytes = new byte[8];
				bytes[0] = (byte) cmd.cmd.num;
				switch (cmd.cmd.art) {
				case LABEL_OR_CONST: {
					out.write(convertLongToHexString(pos, convertByteArrToHexString(" -> ", bytes, " = ")));
					out.write(cmd.toString(pos));
					out.write(convertLongToHexString("\n   ", pos + 8,
							convertLongToHexString(" -> ", cmd.relativeLabel, "   | [relative-label]\n")));
					break;
				}
				case NO_PARAMS: {
					out.write(convertLongToHexString(pos, convertByteArrToHexString(" -> ", bytes, " = ")));
					out.write(cmd.toString());
					out.write('\n');
					break;
				}
				case ONE_PARAM_ALLOW_CONST, ONE_PARAM_NO_CONST: {
					bytes[2] = (byte) cmd.p1.art;
					int off = 7;
					if ((cmd.p1.art & PARAM_A_SR) == PARAM_A_SR) {
						bytes[off--] = (byte) cmd.p1.num;
					}
					if ((cmd.p1.art & PARAM_B_SR) == PARAM_B_SR) {
						bytes[off--] = (byte) cmd.p1.off;
					}
					out.write(convertLongToHexString(pos, convertByteArrToHexString(" -> ", bytes, " = ")));
					out.write(cmd.toString());
					out.write('\n');
					long ipos = pos + 8;
					if ((cmd.p1.art & PARAM_A_SR) != PARAM_A_SR) {
						out.write(convertLongToHexString("   ", ipos,
								convertLongToHexString(" -> ", cmd.p1.num, "   | [p-num]\n")));
						ipos += 8;
					}
					if ((cmd.p1.art & PARAM_B_SR) == PARAM_B_NUM) {
						out.write(convertLongToHexString("   ", ipos,
								convertLongToHexString(" -> ", cmd.p1.off, "   | [p-offset]\n")));
					}
					break;
				}
				case TWO_PARAMS_ALLOW_CONSTS, TWO_PARAMS_NO_CONSTS, TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST: {
					bytes[2] = (byte) cmd.p1.art;
					bytes[3] = (byte) cmd.p2.art;
					int off = 7;
					if ((cmd.p1.art & PARAM_A_SR) == PARAM_A_SR) {
						bytes[off--] = (byte) cmd.p1.num;
					}
					if ((cmd.p1.art & PARAM_B_SR) == PARAM_B_SR) {
						bytes[off--] = (byte) cmd.p1.off;
					}
					if ((cmd.p2.art & PARAM_A_SR) == PARAM_A_SR) {
						bytes[off--] = (byte) cmd.p2.num;
					}
					if ((cmd.p2.art & PARAM_B_SR) == PARAM_B_SR) {
						bytes[off--] = (byte) cmd.p2.off;
					}
					out.write(convertLongToHexString(pos, convertByteArrToHexString(" -> ", bytes, " = ")));
					out.write(cmd.toString());
					out.write('\n');
					long ipos = pos;
					if ((cmd.p1.art & PARAM_A_SR) != PARAM_A_SR) {
						ipos += 8;
						out.write(convertLongToHexString("   ", ipos,
								convertLongToHexString(" -> ", cmd.p1.num, "   | [p1-num]\n")));
					}
					if ((cmd.p1.art & PARAM_B_SR) == PARAM_B_NUM) {
						ipos += 8;
						out.write(convertLongToHexString("   ", ipos,
								convertLongToHexString(" -> ", cmd.p1.off, "   | [p1-offset]\n")));
					}
					if ((cmd.p2.art & PARAM_A_SR) != PARAM_A_SR) {
						ipos += 8;
						out.write(convertLongToHexString("   ", ipos,
								convertLongToHexString(" -> ", cmd.p2.num, "   | [p2-num]\n")));
					}
					if ((cmd.p2.art & PARAM_B_SR) == PARAM_B_NUM) {
						ipos += 8;
						out.write(convertLongToHexString("   ", ipos,
								convertLongToHexString(" -> ", cmd.p2.off, "   | [p2-offset]\n")));
					}
					break;
				}
				default:
					throw new InternalError("unknown cmdart: " + cmd.cmd.art);
				}
			}
			case executable -> {
				out.write("    ");
				out.write(cmd.toString(pos));
				out.write('\n');
			}
			default -> throw new InternalError("unknown DisasmMode: " + mode.name());
			}
			pos += cmd.length();
		}
	}
	
	private static String string(String prefix, byte[] bytes, int off, int len) {
		StringBuilder b = new StringBuilder(10 + prefix.length()).append(prefix).append('"');
		for (int i = 0; i < len; i++) {
			int val = bytes[off + i];
			switch (val) {
			case '\0' -> b.append("\\0");
			case '\t' -> b.append("\\t");
			case '\r' -> b.append("\\r");
			case '\n' -> b.append("\\n");
			default -> {
				if (val < 0x20 || (val & 0x7F) != val) {
					b.append('?');
				} else {
					b.append((char) val);
				}
			}
			}
		}
		return b.append('"').toString();
	}
	
	private void read(long pos, InputStream in, List<Command> cmds, Set<Long> labels)
			throws IOException, InternalError {
		byte[]          bytes = new byte[8];
		ConstantPoolCmd cp    = new ConstantPoolCmd();
		while (true) {
			try {
				checkedReadBytes(in, bytes, cp);
			} catch (NoCommandException nce) {
				if (cp.length() != 0) {
					cmds.add(cp);
				}
				return;
			}
			try {
				Commands cmd = Commands.get(0xFF & bytes[0] | (0xFF & (bytes[1]) << 8));
				Command  command;
				switch (cmd.art) {
				case LABEL_OR_CONST:
					if (cp.length() != 0) {
						cmds.add(cp);
						pos += cp.length();
						cp = new ConstantPoolCmd();
					}
					long relativeLabel = 0x0000FFFFFFFFFFFFL & convertByteArrToLong(bytes);
					command = new Command(cmd, relativeLabel, lng);
					labels.add((pos + relativeLabel));
					break;
				case NO_PARAMS:
					for (int ii = 2; ii < 8; ii++) {
						Param.zeroCheck(bytes[ii]);
					}
					command = new Command(cmd);
					if (cp.length() != 0) {
						cmds.add(cp);
						pos += cp.length();
						cp = new ConstantPoolCmd();
					}
					break;
				case ONE_PARAM_ALLOW_CONST:
					command = buildOneParam(bytes, in, cmd);
					if (cp.length() != 0) {
						cmds.add(cp);
						pos += cp.length();
						cp = new ConstantPoolCmd();
					}
					break;
				case ONE_PARAM_NO_CONST:
					command = buildOneParam(bytes, in, cmd);
					command.p1.checkNoConst();
					if (cp.length() != 0) {
						cmds.add(cp);
						pos += cp.length();
						cp = new ConstantPoolCmd();
					}
					break;
				case TWO_PARAMS_ALLOW_CONSTS:
					command = buildTwoParam(bytes, in, cmd);
					if (cp.length() != 0) {
						cmds.add(cp);
						pos += cp.length();
						cp = new ConstantPoolCmd();
					}
					break;
				case TWO_PARAMS_NO_CONSTS:
					command = buildTwoParam(bytes, in, cmd);
					command.p1.checkNoConst();
					command.p2.checkNoConst();
					if (cp.length() != 0) {
						cmds.add(cp);
						pos += cp.length();
						cp = new ConstantPoolCmd();
					}
					break;
				case TWO_PARAMS_P1_NO_CONST_P2_ALLOW_CONST:
					command = buildTwoParam(bytes, in, cmd);
					command.p1.checkNoConst();
					if (cp.length() != 0) {
						cmds.add(cp);
						pos += cp.length();
						cp = new ConstantPoolCmd();
					}
					break;
				case TWO_PARAMS_P1_NO_CONST_P2_COMPILE_CONST: {
					Command command0 = buildOneParam(bytes, in, cmd);
					checkedReadBytes(in, bytes, cp);
					ParamBuilder pb = new ParamBuilder();
					pb.art = PARAM_A_NUM;
					pb.v1 = convertByteArrToLong(bytes);
					command = new Command(cmd, command0.p1, pb.build());
					if (cp.length() != 0) {
						cmds.add(cp);
						pos += cp.length();
						cp = new ConstantPoolCmd();
					}
					break;
				}
				case THREE_PARAMS_P1_NO_CONST_P2_ALLOW_CONST_P3_COMPILE_CONST: {
					Command command0 = buildTwoParam(bytes, in, cmd);
					command0.p1.checkNoConst();
					ParamBuilder pb = new ParamBuilder();
					pb.art = PARAM_A_NUM;
					pb.v1 = convertByteArrToLong(bytes);
					command = new Command(cmd, command0.p1, command0.p2, pb.build());
					if (cp.length() != 0) {
						cmds.add(cp);
						pos += cp.length();
						cp = new ConstantPoolCmd();
					}
					break;
				}
				default:
					throw new InternalError("the command '" + cmd.name() + "' does not have a known 'art' value! art="
							+ cmd.art.name());
				}
				pos += command.length();
				cmds.add(command);
			} catch (NoCommandException nce) { /* the consumed bytes are already added to my constant pool */ }
		}
	}
	
	private static void checkedReadBytes(InputStream in, byte[] bytes, ConstantPoolCmd cp)
			throws IOException, NoCommandException {
		PrimitiveDisassemblerMain.LOG.finer(() -> "read now " + bytes.length + " bytes ");
		for (int read = 0, add = in.read(bytes, read, bytes.length - read); read < bytes.length; read += add, add = in
				.read(bytes, read, bytes.length - read)) {
			if (add != -1) continue;
			if (read > 0) {
				cp.add(bytes, read);
			}
			throw new NoCommandException("reached EOF");
		}
		cp.add(bytes);
	}
	
	private static Command buildTwoParam(byte[] bytes, InputStream in, Commands cmd)
			throws NoCommandException, IOException {
		ConstantPoolCmd cp = new ConstantPoolCmd();
		cp.add(bytes);
		ParamBuilder pb = new ParamBuilder();
		pb.art = 0xFF & bytes[3];
		final byte[] orig = bytes.clone();
		int          index;
		switch (pb.art) {
		case Param.ART_ANUM_BREG:
		case Param.ART_ANUM:
			checkedReadBytes(in, bytes, cp);
			pb.v1 = convertByteArrToLong(bytes);
			index = 7;
			break;
		case Param.ART_ANUM_BNUM:
			checkedReadBytes(in, bytes, cp);
			pb.v1 = convertByteArrToLong(bytes);
			checkedReadBytes(in, bytes, cp);
			pb.v2 = convertByteArrToLong(bytes);
			index = 7;
			break;
		case Param.ART_ANUM_BSR:
			pb.v2 = bytes[7];
			checkedReadBytes(in, bytes, cp);
			pb.v1 = convertByteArrToLong(bytes);
			index = 6;
			break;
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
			pb.v1 = bytes[7];
			index = 6;
			break;
		case Param.ART_ASR_BNUM:
			pb.v1 = bytes[7];
			checkedReadBytes(in, bytes, cp);
			pb.v2 = convertByteArrToLong(bytes);
			index = 6;
			break;
		case Param.ART_ASR_BSR:
			pb.v1 = bytes[7];
			pb.v2 = bytes[6];
			index = 5;
			break;
		default:
			throw new NoCommandException(NO_VALID_CMD_ART);
		}
		Param p1 = pb.build();
		pb = new ParamBuilder();
		pb.art = 0xFF & orig[4];
		switch (pb.art) {
		case Param.ART_ANUM_BREG:
		case Param.ART_ANUM:
			for (int i = 4; i < index; i++) {
				Param.zeroCheck(orig[i]);
			}
			checkedReadBytes(in, bytes, cp);
			pb.v1 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ANUM_BNUM:
			for (int i = 4; i < index; i++) {
				Param.zeroCheck(orig[i]);
			}
			checkedReadBytes(in, bytes, cp);
			pb.v1 = convertByteArrToLong(bytes);
			checkedReadBytes(in, bytes, cp);
			pb.v2 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ANUM_BSR:
			checkedReadBytes(in, bytes, cp);
			pb.v1 = convertByteArrToLong(bytes);
			pb.v2 = orig[index--];
			for (int i = 4; i < index; i++) {
				Param.zeroCheck(orig[i]);
			}
			break;
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
			pb.v1 = orig[index--];
			for (int i = 4; i < index; i++) {
				Param.zeroCheck(orig[i]);
			}
			break;
		case Param.ART_ASR_BNUM:
			pb.v1 = orig[index--];
			checkedReadBytes(in, bytes, cp);
			pb.v2 = convertByteArrToLong(bytes);
			for (int i = 4; i < index; i++) {
				Param.zeroCheck(orig[i]);
			}
			break;
		case Param.ART_ASR_BSR:
			pb.v1 = orig[index--];
			pb.v2 = orig[index--];
			for (int i = 4; i < index; i++) {
				Param.zeroCheck(orig[i]);
			}
			break;
		default:
			throw new NoCommandException(NO_VALID_CMD_ART);
		}
		return new Command(cmd, p1, pb.build());
	}
	
	private static Command buildOneParam(byte[] bytes, InputStream in, Commands cmd)
			throws NoCommandException, IOException {
		ConstantPoolCmd cp = new ConstantPoolCmd();
		cp.add(bytes);
		ParamBuilder pb = new ParamBuilder();
		pb.art = 0xFF & bytes[1];
		switch (pb.art) {
		case Param.ART_ANUM_BREG:
		case Param.ART_ANUM:
			for (int i = 3; i < 8; i++) {
				Param.zeroCheck(bytes[i]);
			}
			checkedReadBytes(in, bytes, cp);
			pb.v1 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ANUM_BNUM:
			for (int i = 3; i < 8; i++) {
				Param.zeroCheck(bytes[i]);
			}
			checkedReadBytes(in, bytes, cp);
			pb.v1 = convertByteArrToLong(bytes);
			checkedReadBytes(in, bytes, cp);
			pb.v2 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ANUM_BSR:
			for (int i = 3; i < 7; i++) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v2 = bytes[7];
			checkedReadBytes(in, bytes, cp);
			pb.v1 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
			for (int i = 3; i < 7; i++) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v1 = bytes[7];
			break;
		case Param.ART_ASR_BNUM:
			for (int i = 3; i < 7; i++) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v1 = bytes[7];
			checkedReadBytes(in, bytes, cp);
			pb.v2 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ASR_BSR:
			for (int i = 3; i < 6; i++) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v1 = bytes[7];
			pb.v2 = bytes[6];
			break;
		default:
			throw new NoCommandException(NO_VALID_CMD_ART);
		}
		Param p1 = pb.build();
		return new Command(cmd, p1);
	}
	
	@Override
	public void close() throws IOException {
		out.close();
	}
	
}

