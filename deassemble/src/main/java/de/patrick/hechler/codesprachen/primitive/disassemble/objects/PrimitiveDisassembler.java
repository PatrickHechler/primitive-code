package de.patrick.hechler.codesprachen.primitive.disassemble.objects;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.patrick.hechler.codesprachen.primitive.disassemble.enums.Commands;
import de.patrick.hechler.codesprachen.primitive.disassemble.enums.Commands.ParamArt;
import de.patrick.hechler.codesprachen.primitive.disassemble.exceptions.NoCommandException;
import de.patrick.hechler.codesprachen.primitive.disassemble.interfaces.LabelNameGenerator;
import de.patrick.hechler.codesprachen.primitive.disassemble.objects.Command.ConstantPoolCmd;
import de.patrick.hechler.codesprachen.primitive.disassemble.objects.Param.ParamBuilder;

public class PrimitiveDisassembler {
	
	private final PrintStream out;
	private final LabelNameGenerator lng;
	
	public PrimitiveDisassembler(PrintStream out) {
		this((t, c, d) -> "L-" + d, out);
	}
	
	public PrimitiveDisassembler(LabelNameGenerator lng, PrintStream out) {
		this.out = out;
		this.lng = lng;
	}
	
	public void deassemble(InputStream in) throws IOException {
		List <Command> cmds = new ArrayList <>();
		Set <Long> labels = new HashSet <>();
		Map <Long, Integer> indices = new HashMap <>();
		read(in, cmds, indices, labels);
		write(cmds, indices, labels);
	}
	
	private void write(List <Command> cmds, Map <Long, Integer> indices, Set <Long> labels) {
		long pos = 0;
		for (int i = 0; i < cmds.size(); i ++ ) {
			if (labels.contains((Long) pos)) {
				out.print("@" + lng.generateName(pos, cmds, i) + " ");
			}
			Command cmd = cmds.get(i);
			out.println(cmd.toString());
			pos += cmd.length();
		}
	}
	
	private void read(InputStream in, List <Command> cmds, Map <Long, Integer> indices, Set <Long> labels) throws IOException, InternalError {
		readCommands(in, cmds, indices, labels);
		corretRead(cmds, indices);
	}
	
	private void readCommands(InputStream in, List <Command> cmds, Map <Long, Integer> indices, Set <Long> labels) throws IOException, InternalError {
		indices.put((Long) 0L, (Integer) 0);
		byte[] bytes = new byte[8];
		ConstantPoolCmd cp = null;
		long pos = 0;
		int i = 0;
		while (true) {
			int read = in.read(bytes);
			if (read < 8) {
				if (read == 0) {
					break;
				} else {
					throw new IOException("the stream read not enugh bytes: wanted=8, read=" + read);
				}
			}
			try {
				Commands cmd = Commands.get(bytes[0]);
				Command command;
				switch (cmd.art) {
				case label:
					for (int ii = 1; ii < 8; ii ++ ) {
						Param.zeroCheck(bytes[ii]);
					}
					in.read(bytes);
					long val = convertLong(bytes);
					command = new Command(cmd, val, lng);
					labels.add((Long) command.labelDest);
					break;
				case noParams:
					for (int ii = 1; ii < 8; ii ++ ) {
						Param.zeroCheck(bytes[ii]);
					}
					command = new Command(cmd, lng);
					break;
				case oneParamAllowConst:
					command = buildOneParam(bytes, in, cmd);
					break;
				case oneParamNoConst:
					command = buildOneParam(bytes, in, cmd);
					command.p1.checkNoConst();
					break;
				case twoParamsAllowConsts:
					command = buildTwoParam(bytes, in, cmd);
					break;
				case twoParamsNoConsts:
					command = buildTwoParam(bytes, in, cmd);
					command.p1.checkNoConst();
					command.p2.checkNoConst();
					break;
				case twoParamsP1NoConstP2AllowConst:
					command = buildTwoParam(bytes, in, cmd);
					command.p1.checkNoConst();
					break;
				default:
					throw new InternalError("the command '" + cmd.name() + "' does not have a 'art' value!");
				}
				if (cp != null) {
					cmds.add(cp);
					pos += cp.length();
					indices.put((Long) pos, (Integer) i ++ );
					cp = null;
				}
				pos += command.length();
				indices.put((Long) pos, (Integer) i ++ );
			} catch (NoCommandException nce) {
				if (cp != null) {
					cp = new ConstantPoolCmd();
				}
				long val = convertLong(bytes);
				cp.add(val);
			}
		}
	}
	
	private void corretRead(List <Command> cmds, Map <Long, Integer> indices) {
		ConstantPoolCmd cp;
		long pos;
		int i;
		boolean change = false;
		do {
			Command last = null;
			for (i = 0, pos = 0; i < cmds.size(); i ++ , pos += last.length()) {
				Command cmd = cmds.get(i);
				if (cmd instanceof ConstantPoolCmd) {
					last = cmd;
					continue;
				}
				if (cmd.cmd.art != ParamArt.label) {
					last = cmd;
					continue;
				}
				change = true;
				if ( !indices.containsKey((Long) cmd.labelDest)) {
					if (last != null && last instanceof ConstantPoolCmd) {
						cp = (ConstantPoolCmd) last;
						indices.remove((Long) pos);
					} else {
						cp = new ConstantPoolCmd();
					}
					cp.add(cmd.genLongs(pos));
					cmd = last;
				}
				last = cmd;
			}
		} while (change);
	}
	
	private Command buildTwoParam(byte[] bytes, InputStream in, Commands cmd) throws NoCommandException, IOException {
		ParamBuilder pb = new ParamBuilder();
		pb.art = 0xFF & bytes[1];
		byte[] orig = bytes;
		int index;
		switch (pb.art) {
		case Param.ART_ANUM_BREG:
		case Param.ART_ANUM:
			in.read(bytes);
			pb.v1 = convertLong(bytes);
			index = 7;
			break;
		case Param.ART_ANUM_BNUM:
			in.read(bytes);
			pb.v1 = convertLong(bytes);
			in.read(bytes);
			pb.v2 = convertLong(bytes);
			index = 7;
			break;
		case Param.ART_ANUM_BSR:
			pb.v2 = bytes[7];
			in.read(bytes);
			pb.v1 = convertLong(bytes);
			index = 6;
			break;
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
			pb.v2 = bytes[7];
			index = 6;
			break;
		case Param.ART_ASR_BNUM:
			pb.v1 = bytes[7];
			in.read(bytes);
			pb.v2 = convertLong(bytes);
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
			in.read(bytes);
			pb.v1 = convertLong(bytes);
			break;
		case Param.ART_ANUM_BNUM:
			for (int i = 3; i < index; i ++ ) {
				Param.zeroCheck(orig[i]);
			}
			in.read(bytes);
			pb.v1 = convertLong(bytes);
			in.read(bytes);
			pb.v2 = convertLong(bytes);
			break;
		case Param.ART_ANUM_BSR:
			in.read(bytes);
			pb.v1 = convertLong(bytes);
			pb.v2 = orig[index -- ];
			for (int i = 3; i < index; i ++ ) {
				Param.zeroCheck(orig[i]);
			}
			break;
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
			pb.v2 = orig[index -- ];
			for (int i = 3; i < index; i ++ ) {
				Param.zeroCheck(orig[i]);
			}
			break;
		case Param.ART_ASR_BNUM:
			pb.v1 = orig[index -- ];
			in.read(bytes);
			pb.v2 = convertLong(bytes);
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
		return new Command(cmd, p1, pb.build(), lng);
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
			in.read(bytes);
			pb.v1 = convertLong(bytes);
			break;
		case Param.ART_ANUM_BNUM:
			for (int i = 2; i < 8; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			in.read(bytes);
			pb.v1 = convertLong(bytes);
			in.read(bytes);
			pb.v2 = convertLong(bytes);
			break;
		case Param.ART_ANUM_BSR:
			for (int i = 2; i < 7; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v2 = bytes[7];
			in.read(bytes);
			pb.v1 = convertLong(bytes);
			break;
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
			for (int i = 2; i < 7; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v2 = bytes[7];
			break;
		case Param.ART_ASR_BNUM:
			for (int i = 2; i < 7; i ++ ) {
				Param.zeroCheck(bytes[i]);
			}
			pb.v1 = bytes[7];
			in.read(bytes);
			pb.v2 = convertLong(bytes);
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
		return new Command(cmd, pb.build(), lng);
	}
	
	public static long convertLong(byte[] bytes) {
		long val;
		val = 0xFF & bytes[0];
		val |= (0xFF & bytes[1]) << 8;
		val |= (0xFF & bytes[2]) << 16;
		val |= (0xFF & bytes[3]) << 24;
		val |= (0xFF & bytes[4]) << 32;
		val |= (0xFF & bytes[5]) << 40;
		val |= (0xFF & bytes[6]) << 48;
		val |= (0xFF & bytes[7]) << 56;
		return val;
	}
	
}
