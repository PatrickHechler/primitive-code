package de.patrick.hechler.codesprachen.primitive.assemble.objects;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarParser;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.patrick.hechler.codesprachen.primitive.assemble.objects.Command.ConstantPoolCommand;

public class PrimitiveAssembler {
	
	private final OutputStream out;
	private final boolean supressWarn;
	
	public PrimitiveAssembler(OutputStream out) {
		this(out, false);
	}
	
	
	public PrimitiveAssembler(OutputStream out, boolean supressWarnings) {
		this.out = out;
		this.supressWarn = supressWarnings;
	}
	
	
	public void assemble(InputStream in) throws IOException {
		ANTLRInputStream antlrin = new ANTLRInputStream(in);
		PrimitiveFileGrammarLexer lexer = new PrimitiveFileGrammarLexer(antlrin);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PrimitiveFileGrammarParser parser = new PrimitiveFileGrammarParser(tokens);
		ParseContext parsed = parser.parse();
		assemble(parsed);
	}
	
	public void assemble(PrimitiveFileGrammarParser.ParseContext parsed) throws IOException {
		List <Command> cmds = parsed.commands;
		Map <String, Long> labels = parsed.labels;
		assemble(cmds, labels);
	}
	
	
	public void assemble(List <Command> cmds, Map <String, Long> labels) throws IOException {
		long pos = 0;
		for (int i = 0; i < cmds.size(); i ++ ) {
			Command cmd = cmds.get(i);
			if (cmd instanceof ConstantPoolCommand) {
				ConstantPoolCommand cpc = (ConstantPoolCommand) cmd;
				cpc.write(out);
			} else {
				byte[] bytes = new byte[8];
				bytes[0] = (byte) cmd.cmd.num;
				switch (cmd.cmd) {
				case CMD_IRET:
				case CMD_RET:
					break;// nothing more to write
				case CMD_NEG:
				case CMD_NOT:
				case CMD_PUSH:
					if (cmd.p1.art == Param.ART_ANUM) {
						throw new IllegalStateException("no constants allowed!");
					}
				case CMD_INT:
				case CMD_POP:
				case CMD_SET_IP:
					writeOneParam(cmd, bytes);
					break;
				case CMD_DIV:
					if (cmd.p2.art == Param.ART_ANUM) {
						throw new IllegalStateException("no constants allowed on any param!");
					}
				case CMD_MOV:
				case CMD_ADD:
				case CMD_SUB:
				case CMD_MUL:
				case CMD_AND:
				case CMD_OR:
				case CMD_XOR:
					if (cmd.p1.art == Param.ART_ANUM) {
						throw new IllegalStateException("no constants allowed on the first param!");
					}
				case CMD_CMP: {
					writeTwoParam(cmd, bytes);
					break;
				}
				case CMD_CALL:
				case CMD_CALLEQ:
				case CMD_CALLGE:
				case CMD_CALLGT:
				case CMD_CALLLE:
				case CMD_CALLLO:
				case CMD_CALLNE:
				case CMD_JMP:
				case CMD_JMPEQ:
				case CMD_JMPGE:
				case CMD_JMPGT:
				case CMD_JMPLE:
				case CMD_JMPLO:
				case CMD_JMPNE: {
					assert cmd.p1 != null : "I need a first param!";
					assert cmd.p2 == null : "my command can not have a second param";
					out.write(bytes, 0, bytes.length);
					long dest;
					if (cmd.p1.art == Param.ART_LABEL) {
						assert cmd.p1.num == 0;
						assert cmd.p1.off == 0;
						Long zw = labels.get(cmd.p1.label);
						if (zw == null) {
							throw new NullPointerException("can't find the used label '" + cmd.p1.label + "', I know the labels '" + labels + "'");
						}
						dest = (long) zw;
						assert dest >= 0;
						dest = pos - dest;
					} else if (cmd.p1.art == Param.ART_ANUM) {
						assert cmd.p1.label == null;
						assert cmd.p1.off == 0;
						if ( !supressWarn) {
							System.err.println("[WARN]: jump/call operation with a number instead of a label as param!");
						}
						dest = cmd.p1.num;
					} else {
						throw new IllegalStateException("illegal param art: " + Param.artToString(cmd.p1.art));
					}
					bytes[0] = (byte) (dest >> 56);
					bytes[1] = (byte) (dest >> 48);
					bytes[2] = (byte) (dest >> 40);
					bytes[3] = (byte) (dest >> 32);
					bytes[4] = (byte) (dest >> 24);
					bytes[5] = (byte) (dest >> 16);
					bytes[6] = (byte) (dest >> 8);
					bytes[7] = (byte) dest;
					break;
				}
				case CMD_SCALL:
				case CMD_SCALLEQ:
				case CMD_SCALLGE:
				case CMD_SCALLGT:
				case CMD_SCALLLE:
				case CMD_SCALLLO:
				case CMD_SCALLNE:
				case CMD_SJMP:
				case CMD_SJMPEQ:
				case CMD_SJMPGE:
				case CMD_SJMPGT:
				case CMD_SJMPLE:
				case CMD_SJMPLO:
				case CMD_SJMPNE: {
					assert cmd.p1.label != null : "I need a desteny label!";
					assert cmd.p2 == null : "my command can not have a second param";
					long dest;
					if (cmd.p1.art == Param.ART_LABEL) {
						assert cmd.p1.num == 0;
						assert cmd.p1.off == 0;
						Long zw = labels.get(cmd.p1.label);
						if (zw == null) {
							throw new NullPointerException("can't find the used label '" + cmd.p1.label + "', I know the labels '" + labels + "'");
						}
						dest = (long) zw;
						assert dest >= 0;
						dest = pos - dest;
					} else if (cmd.p1.art == Param.ART_ANUM) {
						assert cmd.p1.label == null;
						assert cmd.p1.off == 0;
						if ( !supressWarn) {
							System.err.println("[WARN]: jump/call operation with a number instead of a label as param!");
						}
						dest = cmd.p1.num;
					} else {
						throw new IllegalStateException("illegal param art: " + Param.artToString(cmd.p1.art));
					}
					assert dest >= 0;
					dest = pos - dest;
					if (dest > 0x00FFFFFFFFFFFFFFL) {
						throw new IndexOutOfBoundsException("this goes to faar for a small jump/call (max=0x00FFFFFFFFFFFFFF off=0x" + Long.toHexString(dest) + ")");
					}
					bytes[1] = (byte) (dest >> 48);
					bytes[2] = (byte) (dest >> 40);
					bytes[3] = (byte) (dest >> 32);
					bytes[4] = (byte) (dest >> 24);
					bytes[5] = (byte) (dest >> 16);
					bytes[6] = (byte) (dest >> 8);
					bytes[7] = (byte) dest;
					break;
				}
				default:
					throw new IllegalStateException("unknown command enum: " + cmd.cmd.name());
				}
				out.write(bytes);
			}
			pos += cmd.length();
		}
		out.flush();
	}
	
	
	private void writeTwoParam(Command cmd, byte[] bytes) {
		assert cmd.p1 != null : "I need a first Param!";
		assert cmd.p2 != null : "I only a second Param!";
		assert cmd.p1.label == null : "I dom't need a label in my params!";
		assert cmd.p2.label == null : "I dom't need a label in my params!";
		bytes[1] = (byte) (cmd.p1.art << 4);
		bytes[1] |= (byte) cmd.p2.art;
		// BLOCKED: 0xFFFF000000000000 (CMD + P1-ART + P2-ART)
		switch (cmd.p1.art) {
		case Param.ART_ANUM_BREG:
		case Param.ART_ANUM_BREG_CREG:
			if ( !supressWarn) {
				System.err.println("[WARN]: it is not recommended to use constant numbers to access registers!");
			}
		case Param.ART_ANUM:
			assert cmd.p1.off == 0 : "I don't have a offset, but the first offset value is not 0!";
			switch (cmd.p2.art) {
			case Param.ART_ANUM_BREG:
			case Param.ART_ANUM_BREG_CREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use constant numbers to access registers!");
				}
			case Param.ART_ANUM: {
				assert cmd.p2.off == 0 : "I don't have a offset, but the second offset value is not 0!";
				// BLOCKED: 0xFFFF000000000000
				// P1: ---- 0x0000FFFFFF000000
				// P2: ---- 0x0000000000FFFFFF
				if ( (cmd.p1.num & 0x0000000000FFFFFFL) != cmd.p1.num || (cmd.p2.num & 0x0000000000FFFFFFL) != cmd.p2.num) {
					throw new IllegalStateException(
							"the number is too high: max=0x000FFFFFFFFFFFFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				bytes[2] = (byte) (cmd.p1.num >> 16);
				bytes[3] = (byte) (cmd.p1.num >> 8);
				bytes[4] = (byte) cmd.p1.num;
				bytes[5] = (byte) (cmd.p2.num >> 16);
				bytes[6] = (byte) (cmd.p2.num >> 8);
				bytes[7] = (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
			case Param.ART_ASR_BREG_CREG: {
				assert cmd.p2.off == 0 : "I don't have a offset, but the second offset value is not 0!";
				// BLOCKED: 0xFFFF000000000000
				// P1: ---- 0x0000FFFFFFFFFFFC
				// P2: ---- 0x0000000000000003
				if ( (cmd.p1.num & 0x00003FFFFFFFFFFFL) != cmd.p1.num) {
					throw new IllegalStateException("the number is too high: p1-num=" + Long.toHexString(cmd.p1.num));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number is no SR: p2-num=" + cmd.p2.num);
				}
				bytes[2] = (byte) (cmd.p1.num >> 30);
				bytes[3] = (byte) (cmd.p1.num >> 22);
				bytes[4] = (byte) (cmd.p1.num >> 14);
				bytes[5] = (byte) (cmd.p1.num >> 6);
				bytes[6] = (byte) (cmd.p1.num << 2);
				bytes[7] |= (byte) cmd.p2.num;
			}
			case Param.ART_ANUM_BNUM:
			case Param.ART_ANUM_BNUM_CREG: {
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use two constant numbers to access registers!");
				}
				// BLOCKED: - 0xFFFF000000000000
				// P1: ------ 0x0000FFFF00000000
				// P2-NUM: -- 0x00000000FFFF0000
				// P2-OFF: -- 0x000000000000FFFF
				if ( (cmd.p1.num & 0x000000000000FFFFL) != cmd.p1.num || (cmd.p2.num & 0x000000000000FFFFL) != cmd.p2.num || (cmd.p2.off & 0x000000000000FFFFL) != cmd.p2.off) {
					throw new IllegalStateException("the number/offset is too high: max=0x000000000000FFFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p2-num=0x"
							+ Long.toHexString(cmd.p2.num) + " p2-off=0x" + Long.toHexString(cmd.p2.off));
				}
				bytes[2] = (byte) (cmd.p1.num >> 8);
				bytes[3] = (byte) cmd.p1.num;
				bytes[4] = (byte) (cmd.p2.num >> 8);
				bytes[5] = (byte) cmd.p2.num;
				bytes[6] = (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BNUM:
			case Param.ART_ASR_BNUM_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1: ------ 0x0000FFFFFE000000
				// P2-NUM: -- 0x0000000001800000
				// P2-OFF: -- 0x00000000007FFFFF
				if ( (cmd.p2.off & 0x00000000007FFFFFL) != cmd.p2.off) {
					throw new IllegalStateException("the number is too high: max=0x00000000007FFFFF p2-off=0x" + Long.toHexString(cmd.p2.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number is no SR: p2-num=" + cmd.p2.num);
				}
				bytes[2] = (byte) (cmd.p1.num >> 15);
				bytes[3] = (byte) (cmd.p1.num >> 7);
				bytes[4] = (byte) (cmd.p1.num << 1);
				bytes[4] |= (byte) (cmd.p2.num >> 1);
				bytes[5] = (byte) (cmd.p2.num << 7);
				bytes[5] |= (byte) (cmd.p2.off >> 16);
				bytes[6] = (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ANUM_BSR:
			case Param.ART_ANUM_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1: ------ 0x0000FFFFFE000000
				// P2-NUM: -- 0x0000000001FFFFFC
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x00000000007FFFFFL) != cmd.p1.num || (cmd.p2.num & 0x00000000007FFFFFL) != cmd.p2.num) {
					throw new IllegalStateException(
							"the number is too high: max=0x00000000007FFFFF p1-num=0x" + Long.toHexString(cmd.p1.num) + "p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				if ( (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the offset is no SR: p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num >> 15);
				bytes[3] = (byte) (cmd.p1.num >> 7);
				bytes[4] = (byte) (cmd.p1.num << 1);
				bytes[4] |= (byte) (cmd.p1.num >> 22);
				bytes[5] = (byte) (cmd.p2.num >> 14);
				bytes[6] = (byte) (cmd.p2.num >> 6);
				bytes[7] = (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BSR:
			case Param.ART_ASR_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1: ------ 0x0000FFFFFFFFFFF0
				// P2-NUM: -- 0x000000000000000C
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x00000FFFFFFFFFFFL) != cmd.p1.num) {
					throw new IllegalStateException("the number is too high: max=0x00000000007FFFFF p1-num=0x" + Long.toHexString(cmd.p1.num));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num || (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number/offset is no SR: p2-num=" + cmd.p2.num + " p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num >> 32);
				bytes[3] = (byte) (cmd.p1.num >> 24);
				bytes[4] = (byte) (cmd.p1.num >> 16);
				bytes[5] = (byte) (cmd.p1.num >> 8);
				bytes[6] = (byte) cmd.p1.num;
				bytes[7] = (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			default:
				throw new IllegalStateException("unknown param art: " + Param.artToString(cmd.p1.art));
			}
			break;
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
		case Param.ART_ASR_BREG_CREG:
			assert cmd.p1.off == 0 : "I don't have a offset, but the first offset value is not 0!";
			if ( (cmd.p1.num & 0x0000000000000003L) != cmd.p1.num) {
				throw new IllegalStateException("the number is no SR: p1-num=" + cmd.p1.num);
			}
			switch (cmd.p2.art) {
			case Param.ART_ANUM_BREG:
			case Param.ART_ANUM_BREG_CREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use constant numbers to access registers!");
				}
			case Param.ART_ANUM: {
				assert cmd.p2.off == 0 : "I don't have a offset, but the second offset value is not 0!";
				// BLOCKED: - 0xFFFF000000000000
				// P1: ------ 0x0000C00000000000
				// P2-NUM: -- 0x00003FFFFFFFFFFF
				if ( (cmd.p2.num & 0x00003FFFFFFFFFFFL) != cmd.p2.num) {
					throw new IllegalStateException("the number is too high: max=0x00003FFFFFFFFFFF p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p2.num >> 48);
				bytes[3] = (byte) (cmd.p2.num >> 40);
				bytes[4] = (byte) (cmd.p2.num >> 32);
				bytes[5] = (byte) (cmd.p2.num >> 24);
				bytes[6] = (byte) (cmd.p2.num >> 16);
				bytes[7] = (byte) (cmd.p2.num >> 8);
				bytes[7] |= (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
			case Param.ART_ASR_BREG_CREG: {
				assert cmd.p2.off == 0 : "I don't have a offset, but the second offset value is not 0!";
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x000000000000000C
				// P2-NUM: -- 0x0000000000000003
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number/offset is no SR: p2-num=" + cmd.p2.num);
				}
				bytes[7] = (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ANUM_BNUM:
			case Param.ART_ANUM_BNUM_CREG: {
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use two constant numbers to access registers!");
				}
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000C00000000000
				// P2-NUM: -- 0x00003FFFFF800000
				// P2-OFF: -- 0x00000000007FFFFF
				if ( (cmd.p2.num & 0x00000000007FFFFFL) != cmd.p2.num || (cmd.p2.off & 0x00000000007FFFFFL) != cmd.p2.off) {
					throw new IllegalStateException("the number is too high: max=0x00000000007FFFFF p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p2.num >> 15);
				bytes[3] = (byte) (cmd.p2.num >> 7);
				bytes[4] = (byte) (cmd.p2.num >> 1);
				bytes[5] = (byte) (cmd.p2.num << 7);
				bytes[5] |= (byte) (cmd.p2.off >> 16);
				bytes[6] = (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BNUM:
			case Param.ART_ASR_BNUM_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000C00000000000
				// P2-NUM: -- 0x0000300000800000
				// P2-OFF: -- 0x00000FFFFFFFFFFF
				if ( (cmd.p2.num & 0x00000FFFFFFFFFFFL) != cmd.p2.num || (cmd.p2.off & 0x00000FFFFFFFFFFFL) != cmd.p2.off) {
					throw new IllegalStateException("the number is too high: max=0x00000FFFFFFFFFFF p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p2.num << 4);
				bytes[2] |= (byte) (cmd.p2.off >> 40);
				bytes[3] = (byte) (cmd.p2.off >> 32);
				bytes[4] = (byte) (cmd.p2.off >> 24);
				bytes[5] = (byte) (cmd.p2.off >> 16);
				bytes[6] = (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ANUM_BSR:
			case Param.ART_ANUM_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000C00000000000
				// P2-NUM: -- 0x00003FFFFFFFFFFC
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p2.num & 0x00000FFFFFFFFFFFL) != cmd.p2.num) {
					throw new IllegalStateException("the number is too high: max=0x00000FFFFFFFFFFF p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				if ( (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number/offset is no SR: p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p2.num >> 38);
				bytes[3] = (byte) (cmd.p2.num >> 30);
				bytes[4] = (byte) (cmd.p2.num >> 22);
				bytes[5] = (byte) (cmd.p2.num >> 14);
				bytes[6] = (byte) (cmd.p2.num >> 6);
				bytes[7] = (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BSR:
			case Param.ART_ASR_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000000000000030
				// P2-NUM: -- 0x000000000000000C
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num || (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number/offset is no SR: p2-num=" + cmd.p2.num + " p2-off=" + cmd.p2.off);
				}
				bytes[7] = (byte) (cmd.p1.num << 4);
				bytes[7] |= (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			default:
				throw new IllegalStateException("unknown param art: " + Param.artToString(cmd.p1.art));
			}
			break;
		case Param.ART_ANUM_BNUM:
		case Param.ART_ANUM_BNUM_CREG:
			if ( !supressWarn) {
				System.err.println("[WARN]: it is not recommended to use two constant numbers to access registers!");
			}
			switch (cmd.p2.art) {
			case Param.ART_ANUM_BREG:
			case Param.ART_ANUM_BREG_CREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use constant numbers to access registers!");
				}
			case Param.ART_ANUM: {
				assert cmd.p2.off == 0 : "I don't have a offset, but the second offset value is not 0!";
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFF00000000
				// P1-OFF: -- 0x00000000FFFF0000
				// P2-NUM: -- 0x000000000000FFFF
				if ( (cmd.p1.num & 0x000000000000FFFFL) != cmd.p1.num || (cmd.p1.off & 0x000000000000FFFFL) != cmd.p1.off || (cmd.p2.num & 0x000000000000FFFFL) != cmd.p2.num) {
					throw new IllegalStateException("the number is too high: max=0x00000FFFFFFFFFFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p1-off=0x"
							+ Long.toHexString(cmd.p1.off) + " p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				bytes[2] = (byte) (cmd.p1.num << 8);
				bytes[3] = (byte) cmd.p1.num;
				bytes[4] = (byte) (cmd.p1.off << 8);
				bytes[5] = (byte) cmd.p1.off;
				bytes[6] = (byte) (cmd.p2.num << 8);
				bytes[7] = (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
			case Param.ART_ASR_BREG_CREG: {
				assert cmd.p2.off == 0 : "I don't have a offset, but the second offset value is not 0!";
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFE000000
				// P1-OFF: -- 0x0000000001FFFFFC
				// P2-NUM: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x00000000007FFFFFL) != cmd.p1.num || (cmd.p1.off & 0x00000000007FFFFFL) != cmd.p1.off) {
					throw new IllegalStateException(
							"the number is too high: max=0x00000FFFFFFFFFFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number/offset is no SR: p2-num=" + cmd.p2.num);
				}
				bytes[2] = (byte) (cmd.p1.off >> 15);
				bytes[3] = (byte) (cmd.p1.off >> 7);
				bytes[4] = (byte) (cmd.p1.off << 1);
				bytes[4] |= (byte) (cmd.p1.off >> 22);
				bytes[5] = (byte) (cmd.p1.off >> 14);
				bytes[6] = (byte) (cmd.p1.off >> 6);
				bytes[7] = (byte) (cmd.p1.off << 2);
				bytes[7] |= (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ANUM_BNUM:
			case Param.ART_ANUM_BNUM_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFF000000000
				// P1-OFF: -- 0x0000000FFF000000
				// P2-NUM: -- 0x0000000000FFF000
				// P2-OFF: -- 0x0000000000000FFF
				if ( (cmd.p1.num & 0x0000000000000FFFL) != cmd.p1.num || (cmd.p1.off & 0x0000000000000FFFL) != cmd.p1.off || (cmd.p2.num & 0x0000000000000FFFL) != cmd.p2.num
						|| (cmd.p2.off & 0x0000000000000FFFL) != cmd.p2.off) {
					throw new IllegalStateException("the number is too high: max=0x0000000000000FFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p1-off=0x"
							+ Long.toHexString(cmd.p1.off) + " p2-num=0x" + Long.toHexString(cmd.p2.num) + " p2-off=0x" + Long.toHexString(cmd.p2.off));
				}
				bytes[2] = (byte) (cmd.p1.off >> 4);
				bytes[3] = (byte) (cmd.p1.off << 4);
				bytes[3] |= (byte) (cmd.p1.off >> 8);
				bytes[4] = (byte) cmd.p1.off;
				bytes[5] = (byte) (cmd.p2.num >> 4);
				bytes[6] = (byte) (cmd.p2.num << 4);
				bytes[6] |= (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BNUM:
			case Param.ART_ASR_BNUM_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFE00000000
				// P1-OFF: -- 0x00000001FFFC0000
				// P2-NUM: -- 0x0000000000030000
				// P2-OFF: -- 0x000000000000FFFF
				if ( (cmd.p1.num & 0x00000000000007FFL) != cmd.p1.num || (cmd.p1.off & 0x00000000000007FFL) != cmd.p1.off) {
					throw new IllegalStateException(
							"the number/offset is too high: max=0x00000000000007FF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.off & 0x0000000000000FFFL) != cmd.p2.off) {
					throw new IllegalStateException("the offset is too high: max=0x0000000000000FFF p2-off=0x" + Long.toHexString(cmd.p2.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number is no SR: max=0x0000000000000FFF p2-num=" + cmd.p2.num);
				}
				bytes[2] = (byte) (cmd.p1.num >> 7);
				bytes[3] = (byte) (cmd.p1.num << 1);
				bytes[3] |= (byte) (cmd.p1.off >> 14);
				bytes[4] = (byte) (cmd.p1.off >> 6);
				bytes[5] = (byte) (cmd.p1.off << 2);
				bytes[5] |= (byte) cmd.p2.num;
				bytes[6] = (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ANUM_BSR:
			case Param.ART_ANUM_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFE00000000
				// P1-OFF: -- 0x00000001FFFC0000
				// P2-NUM: -- 0x000000000002FFFC
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x00000000000007FFL) != cmd.p1.num || (cmd.p1.off & 0x00000000000007FFL) != cmd.p1.off) {
					throw new IllegalStateException(
							"the number/offset is too high: max=0x00000000000007FF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000000FFFL) != cmd.p2.num) {
					throw new IllegalStateException("the offset is too high: max=0x0000000000000FFF p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				if ( (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR: max=0x0000000000000FFF p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num >> 7);
				bytes[3] = (byte) (cmd.p1.num << 1);
				bytes[3] |= (byte) (cmd.p1.off >> 14);
				bytes[4] = (byte) (cmd.p1.off >> 6);
				bytes[5] = (byte) (cmd.p1.off << 2);
				bytes[5] |= (byte) (cmd.p2.num >> 14);
				bytes[6] = (byte) (cmd.p2.num >> 6);
				bytes[7] = (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BSR:
			case Param.ART_ASR_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFC000000
				// P1-OFF: -- 0x0000000003FFFFF0
				// P2-NUM: -- 0x000000000000000C
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x00000000000007FFL) != cmd.p1.num || (cmd.p1.off & 0x00000000000007FFL) != cmd.p1.off) {
					throw new IllegalStateException(
							"the number/offset is too high: max=0x00000000000007FF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000000FFFL) != cmd.p2.num) {
					throw new IllegalStateException("the offset is too high: max=0x0000000000000FFF p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				if ( (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR: p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num >> 20);
				bytes[3] = (byte) (cmd.p1.num >> 12);
				bytes[4] = (byte) (cmd.p1.num << 4);
				bytes[4] |= (byte) (cmd.p1.off >> 20);
				bytes[5] = (byte) (cmd.p1.off >> 12);
				bytes[6] = (byte) (cmd.p1.off >> 4);
				bytes[7] = (byte) (cmd.p1.off << 4);
				bytes[7] |= (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			default:
				throw new IllegalStateException("unknown param art: " + Param.artToString(cmd.p1.art));
			}
			break;
		case Param.ART_ASR_BNUM:
		case Param.ART_ASR_BNUM_CREG:
			if ( (cmd.p1.num & 0x0000000000000003L) != cmd.p1.num) {
				throw new IllegalStateException("the number is no SR: p1-num=" + cmd.p1.num);
			}
			switch (cmd.p2.art) {
			case Param.ART_ANUM_BREG:
			case Param.ART_ANUM_BREG_CREG:
				if ( (cmd.p1.num & 0x0000000000000003L) != cmd.p1.num) {
					throw new IllegalStateException("the number is no SR: p1-num=" + cmd.p1.num);
				}
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use constant numbers to access registers!");
				}
			case Param.ART_ANUM: {
				assert cmd.p2.off == 0 : "I have no offset, but my offset value is not 0!";
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000C00000000000
				// P1-OFF: -- 0x00003FFFFF800000
				// P2-NUM: -- 0x00000000007FFFFF
				if ( (cmd.p1.off & 0x00000000000007FFL) != cmd.p1.off) {
					throw new IllegalStateException("the number/offset is too high: max=0x00000000000007FF p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000000FFFL) != cmd.p2.num) {
					throw new IllegalStateException("the offset is too high: max=0x0000000000000FFF p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p1.off >> 15);
				bytes[3] = (byte) (cmd.p1.off >> 9);
				bytes[4] = (byte) (cmd.p1.off >> 1);
				bytes[5] = (byte) (cmd.p1.off << 7);
				bytes[5] |= (byte) (cmd.p2.num >> 16);
				bytes[6] = (byte) (cmd.p2.num >> 8);
				bytes[7] = (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
			case Param.ART_ASR_BREG_CREG: {
				assert cmd.p2.off == 0 : "I have no offset, but my offset value is not 0!";
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000C00000000000
				// P1-OFF: -- 0x00003FFFFFFFFFFC
				// P2-NUM: -- 0x0000000000000003
				if ( (cmd.p1.off & 0x00000FFFFFFFFFFFL) != cmd.p1.off) {
					throw new IllegalStateException("the number/offset is too high: max=0x00000FFFFFFFFFFF p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number is no SR p2-num=" + cmd.p2.num);
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p1.off >> 38);
				bytes[3] = (byte) (cmd.p1.off >> 30);
				bytes[4] = (byte) (cmd.p1.off >> 22);
				bytes[5] = (byte) (cmd.p1.off >> 14);
				bytes[6] = (byte) (cmd.p1.off >> 6);
				bytes[7] = (byte) (cmd.p1.off << 2);
				bytes[7] |= (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ANUM_BNUM:
			case Param.ART_ANUM_BNUM_CREG: {
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use two constant numbers to access registers!");
				}
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000C00000000000
				// P1-OFF: -- 0x00003FFFC0000000
				// P2-NUM: -- 0x000000003FFF8000
				// P2-OFF: -- 0x0000000000007FFF
				if ( (cmd.p1.off & 0x000000000000FFFFL) != cmd.p1.off) {
					throw new IllegalStateException("the number/offset is too high: max=0x000000000000FFFF p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000007FFFL) != cmd.p2.num || (cmd.p2.off & 0x0000000000007FFFL) != cmd.p2.off) {
					throw new IllegalStateException(
							"the offset is too high: max=0x000000000000FFFF p2-num=0x" + Long.toHexString(cmd.p2.num) + " p2-off=0x" + Long.toHexString(cmd.p2.off));
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p1.off >> 10);
				bytes[3] = (byte) (cmd.p1.off >> 2);
				bytes[4] = (byte) (cmd.p1.off << 6);
				bytes[4] |= (byte) (cmd.p1.off >> 9);
				bytes[5] = (byte) (cmd.p2.num >> 1);
				bytes[6] = (byte) (cmd.p2.num << 7);
				bytes[6] |= (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BNUM:
			case Param.ART_ASR_BNUM_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000C00000000000
				// P1-OFF: -- 0x00003FFFFF000000
				// P2-NUM: -- 0x0000000000C00000
				// P2-OFF: -- 0x00000000003FFFFF
				if ( (cmd.p1.off & 0x0000000000CFFFFFL) != cmd.p1.off || (cmd.p2.off & 0x0000000000CFFFFFL) != cmd.p2.off) {
					throw new IllegalStateException(
							"the offset is too high: max=0x0000000000CFFFFF p2-off=0x" + Long.toHexString(cmd.p2.off) + " p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number is no SR p2-num=" + cmd.p2.num);
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p1.off >> 16);
				bytes[3] = (byte) (cmd.p1.off >> 8);
				bytes[4] = (byte) cmd.p1.off;
				bytes[5] = (byte) (cmd.p2.off << 6);
				bytes[5] |= (byte) (cmd.p2.off >> 16);
				bytes[6] = (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ANUM_BSR:
			case Param.ART_ANUM_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000C00000000000
				// P1-OFF: -- 0x00003FFFFF000000
				// P2-NUM: -- 0x0000000000FFFFFC
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.off & 0x0000000000CFFFFFL) != cmd.p1.off || (cmd.p2.num & 0x0000000000CFFFFFL) != cmd.p2.num) {
					throw new IllegalStateException(
							"the offset is too high: max=0x0000000000CFFFFF p2-num=0x" + Long.toHexString(cmd.p2.num) + " p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p1.off >> 16);
				bytes[3] = (byte) (cmd.p1.off >> 8);
				bytes[4] = (byte) cmd.p1.off;
				bytes[5] = (byte) (cmd.p2.num >> 14);
				bytes[6] = (byte) (cmd.p2.num >> 6);
				bytes[7] = (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BSR:
			case Param.ART_ASR_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000C00000000000
				// P1-OFF: -- 0x00003FFFFFFFFFF0
				// P2-NUM: -- 0x000000000000000C
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.off & 0x0000000000CFFFFFL) != cmd.p1.off) {
					throw new IllegalStateException("the offset is too high: max=0x0000000000CFFFFF p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num || (cmd.p2.num & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR p2-num=" + cmd.p2.num + " p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num << 6);
				bytes[2] |= (byte) (cmd.p1.off >> 36);
				bytes[3] = (byte) (cmd.p1.off >> 28);
				bytes[4] = (byte) (cmd.p1.off >> 20);
				bytes[5] = (byte) (cmd.p1.off >> 12);
				bytes[6] = (byte) (cmd.p1.off >> 4);
				bytes[7] = (byte) (cmd.p1.off << 4);
				bytes[7] |= (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			default:
				throw new IllegalStateException("unknown param art: " + Param.artToString(cmd.p1.art));
			}
			break;
		case Param.ART_ANUM_BSR:
		case Param.ART_ANUM_BSR_CREG:
			if ( (cmd.p1.off & 0x0000000000000003L) != cmd.p1.off) {
				throw new IllegalStateException("the number is no SR p2-num=" + cmd.p2.off);
			}
			switch (cmd.p2.art) {
			case Param.ART_ANUM_BREG:
			case Param.ART_ANUM_BREG_CREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use constant numbers to access registers!");
				}
			case Param.ART_ANUM: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFFFFFFC0
				// P1-OFF: -- 0x0000000000000030
				// P2-NUM: -- 0x000000000000000C
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.off & 0x000003FFFFFFFFFFL) != cmd.p1.off) {
					throw new IllegalStateException("the offset is too high: max=0x000003FFFFFFFFFF p1-off=0x" + Long.toHexString(cmd.p1.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num || (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR p2-num=" + cmd.p2.num + " p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num >> 34);
				bytes[3] = (byte) (cmd.p1.num >> 26);
				bytes[4] = (byte) (cmd.p1.num >> 18);
				bytes[5] = (byte) (cmd.p1.num >> 10);
				bytes[6] = (byte) (cmd.p1.num >> 2);
				bytes[7] = (byte) (cmd.p1.num << 6);
				bytes[7] |= (byte) (cmd.p1.off << 4);
				bytes[7] |= (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
			case Param.ART_ASR_BREG_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFFFFFFF0
				// P1-OFF: -- 0x000000000000000C
				// P2-NUM: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x00000FFFFFFFFFFFL) != cmd.p1.num) {
					throw new IllegalStateException("the offset is too high: max=0x00000FFFFFFFFFFF p1-num=0x" + Long.toHexString(cmd.p1.num));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num || (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR p2-num=" + cmd.p2.num + " p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num >> 36);
				bytes[3] = (byte) (cmd.p1.num >> 28);
				bytes[4] = (byte) (cmd.p1.num >> 20);
				bytes[5] = (byte) (cmd.p1.num >> 12);
				bytes[6] = (byte) (cmd.p1.num >> 4);
				bytes[7] = (byte) (cmd.p1.num << 4);
				bytes[7] |= (byte) (cmd.p1.off << 2);
				bytes[7] |= (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ANUM_BNUM:
			case Param.ART_ANUM_BNUM_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFF00000000
				// P1-OFF: -- 0x00000000C0000000
				// P2-NUM: -- 0x000000003FFF8000
				// P2-NUM: -- 0x0000000000007FFF
				if ( (cmd.p1.num & 0x000000000000FFFFL) != cmd.p1.num) {
					throw new IllegalStateException("the number is too high: max=0x000000000000FFFF p1-num=0x" + Long.toHexString(cmd.p1.num));
				}
				if ( (cmd.p2.num & 0x0000000000007FFFL) != cmd.p2.num || (cmd.p2.off & 0x0000000000007FFFL) != cmd.p2.off) {
					throw new IllegalStateException(
							"the offset/number is too high: max=0x0000000000007FFF p2-num=0x" + Long.toHexString(cmd.p2.num) + " p2-off=0x" + Long.toHexString(cmd.p2.off));
				}
				bytes[2] = (byte) (cmd.p1.num >> 8);
				bytes[3] = (byte) cmd.p1.num;
				bytes[4] = (byte) (cmd.p1.off << 6);
				bytes[4] |= (byte) (cmd.p2.num >> 9);
				bytes[5] = (byte) (cmd.p2.num >> 1);
				bytes[6] = (byte) (cmd.p2.num << 7);
				bytes[6] |= (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BNUM:
			case Param.ART_ASR_BNUM_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFC000000
				// P1-OFF: -- 0x0000000003000000
				// P2-NUM: -- 0x0000000000C00000
				// P2-NUM: -- 0x00000000003FFFFF
				if ( (cmd.p1.num & 0x00000000003FFFFFL) != cmd.p1.num || (cmd.p2.off & 0x00000000003FFFFFL) != cmd.p2.off) {
					throw new IllegalStateException(
							"the number is too high: max=0x0000000000007FFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p2-off=0x" + Long.toHexString(cmd.p2.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number is no SR p2-num=" + cmd.p2.num);
				}
				bytes[2] = (byte) (cmd.p1.num << 18);
				bytes[3] = (byte) (cmd.p1.num << 10);
				bytes[4] = (byte) (cmd.p1.num << 2);
				bytes[4] |= (byte) cmd.p1.off;
				bytes[5] = (byte) (cmd.p2.num << 6);
				bytes[5] |= (byte) (cmd.p2.off >> 16);
				bytes[6] = (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ANUM_BSR:
			case Param.ART_ANUM_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFC000000
				// P1-OFF: -- 0x0000000003000000
				// P2-NUM: -- 0x0000000000FFFFFC
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x00000000003FFFFFL) != cmd.p1.num || (cmd.p2.num & 0x00000000003FFFFFL) != cmd.p2.num) {
					throw new IllegalStateException(
							"the number is too high: max=0x00000000003FFFFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				if ( (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num >> 18);
				bytes[3] = (byte) (cmd.p1.num >> 10);
				bytes[4] = (byte) (cmd.p1.num << 2);
				bytes[4] |= (byte) cmd.p1.off;
				bytes[5] = (byte) (cmd.p2.num >> 18);
				bytes[6] = (byte) (cmd.p2.num >> 10);
				bytes[7] = (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BSR:
			case Param.ART_ASR_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFFFFFFC0
				// P1-OFF: -- 0x0000000000000030
				// P2-NUM: -- 0x000000000000000C
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x000003FFFFFFFFFFL) != cmd.p1.num) {
					throw new IllegalStateException("the number is too high: max=0x000003FFFFFFFFFF p1-num=0x" + Long.toHexString(cmd.p1.num));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num || (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR p2-num=" + cmd.p2.num + " p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p2.num >> 34);
				bytes[3] = (byte) (cmd.p2.num >> 26);
				bytes[4] = (byte) (cmd.p2.num >> 18);
				bytes[5] = (byte) (cmd.p2.num >> 10);
				bytes[6] = (byte) (cmd.p2.num >> 2);
				bytes[7] = (byte) (cmd.p2.num << 6);
				bytes[7] |= (byte) (cmd.p2.off << 4);
				bytes[7] |= (byte) (cmd.p2.off << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			default:
				throw new IllegalStateException("unknown param art: " + Param.artToString(cmd.p1.art));
			}
			break;
		case Param.ART_ASR_BSR:
		case Param.ART_ASR_BSR_CREG:
			switch (cmd.p2.art) {
			case Param.ART_ANUM_BREG:
			case Param.ART_ANUM_BREG_CREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use constant numbers to access registers!");
				}
			case Param.ART_ANUM: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFE000000
				// P1-OFF: -- 0x0000000001800000
				// P2-NUM: -- 0x00000000007FFFFF
				if ( (cmd.p1.num & 0x00000000007FFFFFL) != cmd.p1.num || (cmd.p2.num & 0x00000000007FFFFFL) != cmd.p2.num) {
					throw new IllegalStateException(
							"the number is too high: max=0x00000000007FFFFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				if ( (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR: p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num >> 15);
				bytes[3] = (byte) (cmd.p1.num >> 7);
				bytes[4] = (byte) (cmd.p1.num << 1);
				bytes[4] |= (byte) (cmd.p1.off >> 1);
				bytes[5] = (byte) (cmd.p1.off << 7);
				bytes[5] |= (byte) (cmd.p2.num >> 16);
				bytes[6] = (byte) (cmd.p2.num >> 8);
				bytes[7] = (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
			case Param.ART_ASR_BREG_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFFFFFFF0
				// P1-OFF: -- 0x000000000000000C
				// P2-NUM: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x00000FFFFFFFFFFFL) != cmd.p1.num) {
					throw new IllegalStateException("the number is too high: max=0x00000FFFFFFFFFFF p1-num=0x" + Long.toHexString(cmd.p1.num));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number is no SR: p2-num=" + cmd.p2.num);
				}
				bytes[2] = (byte) (cmd.p1.num >> 36);
				bytes[3] = (byte) (cmd.p1.num >> 28);
				bytes[4] = (byte) (cmd.p1.num >> 20);
				bytes[5] = (byte) (cmd.p1.num >> 12);
				bytes[6] = (byte) (cmd.p1.num >> 4);
				bytes[7] = (byte) (cmd.p1.num << 4);
				bytes[7] |= (byte) (cmd.p1.off << 2);
				bytes[7] |= (byte) cmd.p2.num;
				break;
			}
			case Param.ART_ANUM_BNUM:
			case Param.ART_ANUM_BNUM_CREG: {
				if ( !supressWarn) {
					System.err.println("[WARN]: it is not recommended to use two constant numbers to access registers!");
				}
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFF00000000
				// P1-OFF: -- 0x00000000C0000000
				// P2-NUM: -- 0x000000003FFF1000
				// P2-OFF: -- 0x0000000000007FFF
				if ( (cmd.p1.num & 0x000000000000FFFFL) != cmd.p1.num) {
					throw new IllegalStateException("the number is too high: max=0x000000000000FFFF p1-num=0x" + Long.toHexString(cmd.p1.num));
				}
				if ( (cmd.p2.num & 0x0000000000007FFFL) != cmd.p2.num || (cmd.p2.off & 0x0000000000007FFFL) != cmd.p2.off) {
					throw new IllegalStateException(
							"the number is too high: max=0x0000000000007FFF p2-num=0x" + Long.toHexString(cmd.p2.num) + " p2-off=0x" + Long.toHexString(cmd.p2.off));
				}
				bytes[2] = (byte) (cmd.p1.off >> 8);
				bytes[3] = (byte) cmd.p1.off;
				bytes[4] = (byte) (cmd.p1.off >> 9);
				bytes[4] |= (byte) (cmd.p2.num >> 9);
				bytes[5] = (byte) (cmd.p2.num >> 1);
				bytes[6] = (byte) (cmd.p2.num << 7);
				bytes[6] |= (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BNUM:
			case Param.ART_ASR_BNUM_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFC000000
				// P1-OFF: -- 0x0000000003000000
				// P2-NUM: -- 0x0000000000C00000
				// P2-OFF: -- 0x00000000003FFFFF
				if ( (cmd.p1.num & 0x000000000000FFFFL) != cmd.p1.num || (cmd.p2.off & 0x0000000000007FFFL) != cmd.p2.off) {
					throw new IllegalStateException(
							"the number is too high: max=0x0000000000007FFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p2-off=0x" + Long.toHexString(cmd.p2.off));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num) {
					throw new IllegalStateException("the number is no SR: p2-num=" + cmd.p2.num);
				}
				bytes[2] = (byte) (cmd.p1.num >> 18);
				bytes[3] = (byte) (cmd.p1.num >> 10);
				bytes[4] = (byte) (cmd.p1.num << 2);
				bytes[4] |= (byte) cmd.p1.off;
				bytes[5] = (byte) (cmd.p2.num << 6);
				bytes[5] |= (byte) (cmd.p2.off >> 16);
				bytes[6] = (byte) (cmd.p2.off >> 8);
				bytes[7] = (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ANUM_BSR:
			case Param.ART_ANUM_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFC000000
				// P1-OFF: -- 0x0000000003000000
				// P2-NUM: -- 0x0000000000FFFFFC
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x000000000000FFFFL) != cmd.p1.num || (cmd.p2.num & 0x0000000000007FFFL) != cmd.p2.num) {
					throw new IllegalStateException(
							"the number is too high: max=0x0000000000007FFF p1-num=0x" + Long.toHexString(cmd.p1.num) + " p2-num=0x" + Long.toHexString(cmd.p2.num));
				}
				if ( (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR: p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p1.num >> 18);
				bytes[3] = (byte) (cmd.p1.num >> 10);
				bytes[4] = (byte) (cmd.p1.num << 2);
				bytes[4] |= (byte) cmd.p1.off;
				bytes[5] = (byte) (cmd.p2.num >> 18);
				bytes[6] = (byte) (cmd.p2.num >> 10);
				bytes[7] = (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			case Param.ART_ASR_BSR:
			case Param.ART_ASR_BSR_CREG: {
				// BLOCKED: - 0xFFFF000000000000
				// P1-NUM: -- 0x0000FFFFFFFFFFC0
				// P1-OFF: -- 0x0000000000000030
				// P2-NUM: -- 0x000000000000000C
				// P2-OFF: -- 0x0000000000000003
				if ( (cmd.p1.num & 0x000003FFFFFFFFFFL) != cmd.p1.num) {
					throw new IllegalStateException("the number is too high: max=0x000003FFFFFFFFFF p1-num=0x" + Long.toHexString(cmd.p1.num));
				}
				if ( (cmd.p2.num & 0x0000000000000003L) != cmd.p2.num || (cmd.p2.off & 0x0000000000000003L) != cmd.p2.off) {
					throw new IllegalStateException("the number is no SR: p2-num=" + cmd.p1.num + " p2-off=" + cmd.p2.off);
				}
				bytes[2] = (byte) (cmd.p2.num >> 34);
				bytes[3] = (byte) (cmd.p2.num >> 26);
				bytes[4] = (byte) (cmd.p2.num >> 18);
				bytes[5] = (byte) (cmd.p2.num >> 10);
				bytes[6] = (byte) (cmd.p2.num >> 2);
				bytes[7] = (byte) (cmd.p2.num << 6);
				bytes[7] |= (byte) (cmd.p2.num << 4);
				bytes[7] |= (byte) (cmd.p2.num << 2);
				bytes[7] |= (byte) cmd.p2.off;
				break;
			}
			default:
				throw new IllegalStateException("unknown param art: " + Param.artToString(cmd.p1.art));
			}
			break;
		default:
			throw new IllegalStateException("unknown param art: " + Param.artToString(cmd.p1.art));
		}
	}
	
	
	private void writeOneParam(Command cmd, byte[] bytes) {
		assert cmd.p2 == null : "I only need one Param!";
		assert cmd.p1 != null : "I need a first Param!";
		assert cmd.p1.label == null : "I dom't need a label in my param!";
		bytes[1] = (byte) (cmd.p1.art << 4);
		switch (cmd.p1.art) {
		case Param.ART_ANUM_BREG:
		case Param.ART_ANUM_BREG_CREG:
			if ( !supressWarn) {
				System.err.println("[WARN]: it is not recommended to use constant numbers to access registers!");
			}
		case Param.ART_ANUM: {
			assert cmd.p1.off == 0;
			if ( (cmd.p1.num & 0x000FFFFFFFFFFFFFL) != cmd.p1.num) {
				throw new IllegalStateException("the number is too high: max=0x000FFFFFFFFFFFFF num=0x" + Long.toHexString(cmd.p1.num));
			}
			bytes[1] = (byte) (cmd.p1.num >> 48);
			bytes[2] = (byte) (cmd.p1.num >> 40);
			bytes[3] = (byte) (cmd.p1.num >> 32);
			bytes[4] = (byte) (cmd.p1.num >> 24);
			bytes[5] = (byte) (cmd.p1.num >> 16);
			bytes[6] = (byte) (cmd.p1.num >> 8);
			bytes[7] = (byte) cmd.p1.num;
			break;
		}
		case Param.ART_ASR:
		case Param.ART_ASR_BREG:
		case Param.ART_ASR_BREG_CREG: {
			assert cmd.p1.off == 0;
			bytes[7] = (byte) cmd.p1.num;
			if (cmd.p1.num > Param.SR_DX || cmd.p1.num < Param.SR_AX) {
				throw new IllegalStateException("param is not AX, BX, CX or DX: " + cmd.p1.num + "!");
			}
			break;
		}
		case Param.ART_ANUM_BNUM:
		case Param.ART_ANUM_BNUM_CREG: {
			// BLOCKED: 0xFFF0000000000000 (CMD + P1_ART)
			// NUM: --- 0x000FFFFFFC000000
			// OFF: --- 0x0000000003FFFFFF
			if ( (cmd.p1.num & 0x0000000000FFFFFFL) != cmd.p1.num || (cmd.p1.off & 0x0000000000FFFFFFL) != cmd.p1.off) {
				throw new IllegalStateException("the number is too high: max=0x0000000003FFFFFF num=0x" + Long.toHexString(cmd.p1.num) + " off=" + Long.toHexString(cmd.p1.off));
			}
			if ( !supressWarn) {
				System.err.println("[WARN]: it is not recommended to use two constant numbers to access registers!");
			}
			bytes[7] = (byte) (cmd.p1.num >> 22);
			bytes[6] = (byte) (cmd.p1.num >> 14);
			bytes[5] = (byte) (cmd.p1.num >> 6);
			bytes[4] = (byte) (cmd.p1.num << 2);
			bytes[4] |= (byte) (cmd.p1.off >> 24);
			bytes[3] = (byte) (cmd.p1.off >> 16);
			bytes[2] = (byte) (cmd.p1.off >> 8);
			bytes[1] = (byte) cmd.p1.off;
			break;
		}
		case Param.ART_ASR_BNUM:
		case Param.ART_ASR_BNUM_CREG: {
			// BLOCKED: 0xFFF0000000000000
			// V1: ---- 0x000C000000000000
			// V2: ---- 0x0003FFFFFFFFFFFF
			if ( (cmd.p1.num & 0x0000000000000003L) != cmd.p1.num) {
				throw new IllegalStateException("the number no SR: num=0x" + Long.toHexString(cmd.p1.num));
			}
			if ( (cmd.p1.off & 0x0003FFFFFFFFFFFFL) != cmd.p1.off) {
				throw new IllegalStateException("the offset too high: max=0x0003FFFFFFFFFFFF off=" + Long.toHexString(cmd.p1.off));
			}
			bytes[6] = (byte) (cmd.p1.num >> 6);
			bytes[6] |= (byte) (cmd.p1.off >> 48);
			bytes[5] = (byte) (cmd.p1.off >> 40);
			bytes[4] = (byte) (cmd.p1.off >> 32);
			bytes[3] = (byte) (cmd.p1.off >> 24);
			bytes[2] = (byte) (cmd.p1.off >> 16);
			bytes[1] = (byte) (cmd.p1.off >> 8);
			bytes[0] = (byte) cmd.p1.off;
			break;
		}
		case Param.ART_ANUM_BSR:
		case Param.ART_ANUM_BSR_CREG: {
			// BLOCKED: 0xFFF0000000000000
			// V1: ---- 0x000FFFFFFFFFFFFC
			// V2: ---- 0x0000000000000003
			if ( (cmd.p1.num & 0x0003FFFFFFFFFFFFL) != cmd.p1.num) {
				throw new IllegalStateException("the nomber too high: max=0x0003FFFFFFFFFFFF off=" + Long.toHexString(cmd.p1.num));
			}
			if ( (cmd.p1.off & 0x0000000000000003L) != cmd.p1.off) {
				throw new IllegalStateException("the offset no SR: off=" + cmd.p1.off);
			}
			bytes[6] = (byte) (cmd.p1.num >> 50);
			bytes[5] = (byte) (cmd.p1.num >> 42);
			bytes[4] = (byte) (cmd.p1.num >> 34);
			bytes[3] = (byte) (cmd.p1.num >> 26);
			bytes[2] = (byte) (cmd.p1.num >> 18);
			bytes[1] = (byte) (cmd.p1.num >> 10);
			bytes[0] = (byte) (cmd.p1.num >> 2);
			bytes[0] |= (byte) cmd.p1.off;
			break;
		}
		case Param.ART_ASR_BSR:
		case Param.ART_ASR_BSR_CREG: {
			if ( (cmd.p1.num & 0x0000000000000003L) != cmd.p1.num || (cmd.p1.off & 0x0000000000000003L) != cmd.p1.off) {
				throw new IllegalStateException("the offset or the number is no SR: num=" + cmd.p1.num + " off=" + cmd.p1.off);
			}
			bytes[0] = (byte) (cmd.p1.num << 2);
			bytes[0] |= (byte) cmd.p1.off;
			break;
		}
		default:
			throw new IllegalStateException("unknown param art: " + Param.artToString(cmd.p1.art));
		}
	}
	
}
