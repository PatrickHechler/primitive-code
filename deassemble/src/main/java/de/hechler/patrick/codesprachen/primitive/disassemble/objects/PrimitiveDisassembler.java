package de.hechler.patrick.codesprachen.primitive.disassemble.objects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.disassemble.enums.DisasmMode;
import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;
import de.hechler.patrick.codesprachen.primitive.disassemble.interfaces.LabelNameGenerator;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Command.ConstantPoolCmd;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Param.ParamBuilder;
import de.hechler.patrick.codesprachen.primitive.disassemble.utils.LongArrayInputStream;

import static de.hechler.patrick.codesprachen.primitive.disassemble.utils.Convert.*;

public class PrimitiveDisassembler {
	
	private final PrintStream out;
	private final LabelNameGenerator lng;
	private final DisasmMode mode;
	
	public PrimitiveDisassembler(PrintStream out) {
		this(DisasmMode.analysable, t -> convertLongToHexString("L_", t), out);
	}
	
	public PrimitiveDisassembler(LabelNameGenerator lng, PrintStream out) {
		this(DisasmMode.analysable, lng, out);
	}
	
	public PrimitiveDisassembler(DisasmMode mode, PrintStream out) {
		this(mode, mode == DisasmMode.analysable ? t -> convertLongToHexString("L_", t) : LabelNameGenerator.SIMPLE_GEN, out);
	}
	
	public PrimitiveDisassembler(DisasmMode mode, LabelNameGenerator lng, PrintStream out) {
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
		List <Command> cmds = new ArrayList <>();
		Set <Long> labels = new HashSet <>();
		read(pos, in, cmds, labels);
		write(pos, cmds, labels);
	}
	
	private void write(long pos, List <Command> cmds, Set <Long> labels) {
		switch(mode) {
		case analysable:
			break;
		case executable:
			out.println("$not-align");
			break;
		default:
			throw new InternalError("unknown mode: " + mode.name());
		}
		for (int i = 0; i < cmds.size(); i ++ ) {
			if (labels.contains((Long) pos)) {
				switch (mode) {
				case analysable:
					out.print("@L_");
					break;
				case executable:
					out.println("@" + lng.generateName(pos));
					break;
				default:
					throw new InternalError("unknown mode: " + mode.name());
				}
			} else {
				switch (mode) {
				case analysable:
					out.print("   ");
					break;
				case executable:
					break;
				default:
					throw new InternalError("unknown mode: " + mode.name());
				}
			}
			Command cmd = cmds.get(i);
			if (cmd instanceof ConstantPoolCmd) {
				ConstantPoolCmd cp = (ConstantPoolCmd) cmd;
				switch (mode) {
				case analysable: {
					boolean first = true;
					String prefix = "";
					byte[] bytes = new byte[8];
					int off;
					for (off = 0; off <= cp.length() - 8; off += 8) {
						cp.get(bytes, 0, off, 8);
						out.println(convertLongToHexString(prefix, pos, convertByteArrToHexString(" -> ", bytes, " = unknown")));
						if (first) {
							first = false;
							prefix = "   ";
						}
						pos += 8;
					}
					if (off < cp.length()) {
						int len = cp.length() - off;
						int strlen = (len * 2) + 4;
						out.println(convertLongToHexString(prefix, pos, convertByteArrToHexString(" -> ----------------".substring(0, strlen), bytes, 0, len, " = unknown")));
					}
					break;
				}
				case executable: {
					out.println(cp);
					pos += cp.length();
					break;
				}
				default:
					throw new InternalError("unknown mode: " + mode.name());
				}
				continue;
			}
			switch (mode) {
			case analysable: {
				byte[] bytes = new byte[8];
				bytes[0] = (byte) cmd.cmd.num;
				switch (cmd.cmd.art) {
				case label: {
					out.println(convertLongToHexString(pos, convertByteArrToHexString(" -> ", bytes, " = ")) + cmd.toString(pos));
					out.println(convertLongToHexString("   ", pos + 8, convertLongToHexString(" -> ", cmd.relativeLabel, "   | [relative-label]")));
					break;
				}
				case noParams: {
					out.println(convertLongToHexString(pos, convertByteArrToHexString(" -> ", bytes, " = " + cmd.toString())));
					break;
				}
				case oneParamAllowConst:
				case oneParamNoConst: {
					bytes[1] = (byte) cmd.p1.art;
					int off = 7;
					if ( (cmd.p1.art & Param.PARAM_A_SR) != 0) {
						bytes[off -- ] = (byte) cmd.p1.num;
					}
					if ( (cmd.p1.art & Param.PARAM_B_SR) == Param.PARAM_B_SR) {
						bytes[off -- ] = (byte) cmd.p1.off;
					}
					out.println(convertLongToHexString(pos, convertByteArrToHexString(" -> ", bytes, " = " + cmd.toString())));
					long ipos = pos + 8;
					if ( (cmd.p1.art & Param.PARAM_A_SR) == 0) {
						out.println(convertLongToHexString("   ", ipos, convertLongToHexString(" -> ", cmd.p1.num, "   | [p-num]")));
						ipos += 8;
					}
					if ( (cmd.p1.art & Param.PARAM_B_SR) == Param.PARAM_B_NUM) {
						out.println(convertLongToHexString("   ", ipos, convertLongToHexString(" -> ", cmd.p1.off, "   | [p-offset]")));
					}
					break;
				}
				case twoParamsAllowConsts:
				case twoParamsNoConsts:
				case twoParamsP1NoConstP2AllowConst: {
					bytes[1] = (byte) cmd.p1.art;
					int off = 7;
					if ( (cmd.p1.art & Param.PARAM_A_SR) != 0) {
						bytes[off -- ] = (byte) cmd.p1.num;
					}
					if ( (cmd.p1.art & Param.PARAM_B_SR) == Param.PARAM_B_SR) {
						bytes[off -- ] = (byte) cmd.p1.off;
					}
					if ( (cmd.p2.art & Param.PARAM_A_SR) != 0) {
						bytes[off -- ] = (byte) cmd.p2.num;
					}
					if ( (cmd.p2.art & Param.PARAM_B_SR) == Param.PARAM_B_SR) {
						bytes[off -- ] = (byte) cmd.p2.off;
					}
					out.println(convertLongToHexString(pos, convertByteArrToHexString(" -> ", bytes, " = " + cmd.toString())));
					long ipos = pos;
					if ( (cmd.p1.art & Param.PARAM_A_SR) == 0) {
						ipos += 8;
						out.println(convertLongToHexString("   ", ipos, convertLongToHexString(" -> ", cmd.p1.num, "   | [p1-num]")));
					}
					if ( (cmd.p1.art & Param.PARAM_B_SR) == Param.PARAM_B_NUM) {
						ipos += 8;
						out.println(convertLongToHexString("   ", ipos, convertLongToHexString(" -> ", cmd.p1.off, "   | [p1-offset]")));
					}
					if ( (cmd.p2.art & Param.PARAM_A_SR) == 0) {
						ipos += 8;
						out.println(convertLongToHexString("   ", ipos, convertLongToHexString(" -> ", cmd.p2.num, "   | [p2-num]")));
					}
					if ( (cmd.p2.art & Param.PARAM_B_SR) == Param.PARAM_B_NUM) {
						ipos += 8;
						out.println(convertLongToHexString("   ", ipos, convertLongToHexString(" -> ", cmd.p2.off, "   | [p2-offset]")));
					}
					break;
				}
				default:
					throw new InternalError("unknown cmdart: " + cmd.cmd.art);
				}
				break;
			}
			case executable:
				out.println("    " + cmd.toString(pos));
				break;
			default:
				throw new InternalError("unknown DisasmMode: " + mode.name());
			}
			pos += cmd.length();
		}
	}
	
	private void read(long pos, InputStream in, List <Command> cmds, Set <Long> labels) throws IOException, InternalError {
		byte[] bytes = new byte[8];
		ConstantPoolCmd cp = null;
		while (true) {
			for (int read = 0, add; read < 8; read += add) {
				add = in.read(bytes, read, 8 - read);
				if (add != -1) continue;
				if (read > 0) {
					if (cp == null) {
						cp = new ConstantPoolCmd();
					}
					bytes = Arrays.copyOf(bytes, read);
					cp.add(bytes);
					cmds.add(cp);
					pos += cp.length();// not really needed
				} else if (cp != null) {
					cmds.add(cp);
					pos += cp.length();// not really needed
				}
				return;
			}
			try {
				Commands cmd = Commands.get(bytes[0]);
				Command command;
				switch (cmd.art) {
				case label:
					for (int ii = 1; ii < 8; ii ++ ) {
						Param.zeroCheck(bytes[ii]);
					}
					if (cp != null) {
						cmds.add(cp);
						pos += cp.length();
						cp = null;
					}
					checkedReadBytes(in, bytes);
					long val = convertByteArrToLong(bytes);
					command = new Command(cmd, val, lng);
					labels.add((Long) (pos + command.relativeLabel));
					break;
				case noParams:
					for (int ii = 1; ii < 8; ii ++ ) {
						Param.zeroCheck(bytes[ii]);
					}
					command = new Command(cmd);
					if (cp != null) {
						cmds.add(cp);
						pos += cp.length();
						cp = null;
					}
					break;
				case oneParamAllowConst:
					command = buildOneParam(bytes, in, cmd);
					if (cp != null) {
						cmds.add(cp);
						pos += cp.length();
						cp = null;
					}
					break;
				case oneParamNoConst:
					command = buildOneParam(bytes, in, cmd);
					command.p1.checkNoConst();
					if (cp != null) {
						cmds.add(cp);
						pos += cp.length();
						cp = null;
					}
					break;
				case twoParamsAllowConsts:
					command = buildTwoParam(bytes, in, cmd);
					if (cp != null) {
						cmds.add(cp);
						pos += cp.length();
						cp = null;
					}
					break;
				case twoParamsNoConsts:
					command = buildTwoParam(bytes, in, cmd);
					command.p1.checkNoConst();
					command.p2.checkNoConst();
					if (cp != null) {
						cmds.add(cp);
						pos += cp.length();
						cp = null;
					}
					break;
				case twoParamsP1NoConstP2AllowConst:
					command = buildTwoParam(bytes, in, cmd);
					command.p1.checkNoConst();
					if (cp != null) {
						cmds.add(cp);
						pos += cp.length();
						cp = null;
					}
					break;
				default:
					throw new InternalError("the command '" + cmd.name() + "' does not have a known 'art' value! art=" + cmd.art.name());
				}
				pos += command.length();
				cmds.add(command);
			} catch (NoCommandException nce) {
				if (cp == null) {
					cp = new ConstantPoolCmd();
				}
				cp.add(bytes);
			}
		}
	}
	
	private void checkedReadBytes(InputStream in, byte[] bytes) throws IOException, NoCommandException {
		int r = in.read(bytes);
		if (r != bytes.length) {
			throw new NoCommandException("did not read enugh bytes: read=" + r + " wanted=" + bytes.length);
		}
	}
	
	private Command buildTwoParam(byte[] bytes, InputStream in, Commands cmd) throws NoCommandException, IOException {
		ParamBuilder pb = new ParamBuilder();
		pb.art = 0xFF & bytes[1];
		final byte[] orig = bytes.clone();
		int index;
		switch (pb.art) {
		case Param.ART_ANUM_BREG:
		case Param.ART_ANUM:
			checkedRead(bytes, in);
			pb.v1 = convertByteArrToLong(bytes);
			index = 7;
			break;
		case Param.ART_ANUM_BNUM:
			checkedRead(bytes, in);
			pb.v1 = convertByteArrToLong(bytes);
			checkedRead(bytes, in);
			pb.v2 = convertByteArrToLong(bytes);
			index = 7;
			break;
		case Param.ART_ANUM_BSR:
			pb.v2 = bytes[7];
			checkedRead(bytes, in);
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
			checkedRead(bytes, in);
			pb.v2 = convertByteArrToLong(bytes);
			index = 6;
			break;
		case Param.ART_ASR_BSR:
			pb.v1 = bytes[7];
			pb.v2 = bytes[6];
			index = 5;
			break;
		default:
			throw new NoCommandException("the command has no valid art");
		}
		Param p1 = pb.build();
		pb = new ParamBuilder();
		pb.art = 0xFF & orig[2];
		switch (pb.art) {
		case Param.ART_ANUM_BREG:
		case Param.ART_ANUM:
			for (int i = 3; i < index; i ++ ) {
				Param.zeroCheck(orig[i]);
			}
			checkedRead(bytes, in);
			pb.v1 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ANUM_BNUM:
			for (int i = 3; i < index; i ++ ) {
				Param.zeroCheck(orig[i]);
			}
			checkedRead(bytes, in);
			pb.v1 = convertByteArrToLong(bytes);
			checkedRead(bytes, in);
			pb.v2 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ANUM_BSR:
			checkedRead(bytes, in);
			pb.v1 = convertByteArrToLong(bytes);
			pb.v2 = orig[index -- ];
			for (int i = 3; i < index; i ++ ) {
				Param.zeroCheck(orig[i]);
			}
			break;
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
			pb.v1 = orig[index -- ];
			for (int i = 3; i < index; i ++ ) {
				Param.zeroCheck(orig[i]);
			}
			break;
		case Param.ART_ASR_BNUM:
			pb.v1 = orig[index -- ];
			checkedRead(bytes, in);
			pb.v2 = convertByteArrToLong(bytes);
			for (int i = 3; i < index; i ++ ) {
				Param.zeroCheck(orig[i]);
			}
			break;
		case Param.ART_ASR_BSR:
			pb.v1 = orig[index -- ];
			pb.v2 = orig[index -- ];
			for (int i = 3; i < index; i ++ ) {
				Param.zeroCheck(orig[i]);
			}
			break;
		default:
			throw new NoCommandException("the command has no valid art");
		}
		return new Command(cmd, p1, pb.build());
	}
	
	private void checkedRead(byte[] bytes, InputStream in) throws IOException, NoCommandException {
		int len = in.read(bytes);
		if (len == -1) {
			throw new NoCommandException("reached end of stream, but wanted to read the bytes (len=" + bytes.length + "), I could not read any bytes");
		}
		while (len < bytes.length) {
			int addlen = in.read(bytes, len, bytes.length - len);
			if (addlen == -1) {
				throw new NoCommandException("reached end of stream, but wanted to read the bytes (len=" + bytes.length + "), I could read " + len + " bytes already");
			}
			len += addlen;
		}
	}
	
	private Command buildOneParam(byte[] bytes, InputStream in, Commands cmd) throws NoCommandException, IOException {
		ParamBuilder pb = new ParamBuilder();
		pb.art = 0xFF & bytes[1];
		switch (pb.art) {
		case Param.ART_ANUM_BREG:
		case Param.ART_ANUM:
			for (int i = 2; i < 8; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			checkedReadBytes(in, bytes);
			pb.v1 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ANUM_BNUM:
			for (int i = 2; i < 8; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			checkedReadBytes(in, bytes);
			pb.v1 = convertByteArrToLong(bytes);
			checkedReadBytes(in, bytes);
			pb.v2 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ANUM_BSR:
			for (int i = 2; i < 7; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v2 = bytes[7];
			checkedReadBytes(in, bytes);
			pb.v1 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
			for (int i = 2; i < 7; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v1 = bytes[7];
			break;
		case Param.ART_ASR_BNUM:
			for (int i = 2; i < 7; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v1 = bytes[7];
			checkedReadBytes(in, bytes);
			pb.v2 = convertByteArrToLong(bytes);
			break;
		case Param.ART_ASR_BSR:
			for (int i = 2; i < 6; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v1 = bytes[7];
			pb.v2 = bytes[6];
			break;
		default:
			throw new NoCommandException("the command has no valid art");
		}
		return new Command(cmd, pb.build());
	}
	
}

