package de.patrick.hechler.codesprachen.primitive.assemble.objects;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
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
		assemble(new ANTLRInputStream(in));
	}
	
	public void assemble(Reader in) throws IOException {
		assemble(new ANTLRInputStream(in));
	}
	
	public void assemble(ANTLRInputStream antlrin) throws IOException {
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
				case CMD_RET:
					break;// nothing more to write
				case CMD_RASH:
				case CMD_RLSH:
				case CMD_LSH:
				case CMD_NEG:
				case CMD_NOT:
				case CMD_PUSH:
				case CMD_GET_SP:
				case CMD_GET_IP:
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
						dest -= pos;
					} else if (cmd.p1.art == Param.ART_ANUM) {
						assert cmd.p1.label == null;
						assert cmd.p1.off == 0;
						if ( !supressWarn) {
							System.err.println("[WARN]: it is not recomended to use jump/call operation with a number instead of a label as param!");
						}
						dest = cmd.p1.num;
					} else {
						throw new IllegalStateException("illegal param art: " + Param.artToString(cmd.p1.art));
					}
					convertLong(bytes, dest);
					break;
				}
				default:
					throw new IllegalStateException("unknown command enum: " + cmd.cmd.name());
				}
				out.write(bytes, 0, bytes.length);
			}
			pos += cmd.length();
		}
		out.flush();
	}
	
	
	
	private void writeOneParam(Command cmd, byte[] bytes) throws IOException {
		assert cmd.p1 != null : "I need a first Param!";
		assert cmd.p2 == null : "I can't have a second Param!";
		assert cmd.p1.label == null : "I dom't need a label in my params!";
		bytes[1] = (byte) cmd.p1.art;
		long num = cmd.p1.num, off = cmd.p1.off;
		int art = cmd.p1.art;
		switch (art) {
		case Param.ART_ANUM:
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			break;
		case Param.ART_ANUM_BNUM:
			if ( !supressWarn) {
				System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
			}
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, off);
			break;
		case Param.ART_ANUM_BREG:
			if ( !supressWarn) {
				System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
			}
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			break;
		case Param.ART_ANUM_BSR:
			if ( !supressWarn) {
				System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
			}
			Param.checkSR(off);
			bytes[7] = (byte) off;
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			break;
		case Param.ART_ASR:
			Param.checkSR(num);
			bytes[7] = (byte) num;
			break;
		case Param.ART_ASR_BNUM:
			Param.checkSR(num);
			bytes[7] = (byte) num;
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			break;
		case Param.ART_ASR_BREG:
			Param.checkSR(num);
			bytes[7] = (byte) num;
			break;
		case Param.ART_ASR_BSR:
			Param.checkSR(num);
			Param.checkSR(off);
			bytes[6] = (byte) off;
			bytes[7] = (byte) num;
			out.write(bytes, 0, bytes.length);
			convertLong(bytes, num);
			break;
		}
	}
	
	private static void convertLong(byte[] bytes, long num) {
		bytes[0] = (byte) num;
		bytes[1] = (byte) (num >> 8);
		bytes[2] = (byte) (num >> 16);
		bytes[3] = (byte) (num >> 24);
		bytes[4] = (byte) (num >> 32);
		bytes[5] = (byte) (num >> 40);
		bytes[6] = (byte) (num >> 48);
		bytes[7] = (byte) (num >> 56);
	}
	
	private void writeTwoParam(Command cmd, byte[] bytes) throws IOException {
		assert cmd.p1 != null : "I need a first Param!";
		assert cmd.p2 != null : "I need a second Param!";
		assert cmd.p1.label == null : "I dom't need a label in my params!";
		assert cmd.p2.label == null : "I dom't need a label in my params!";
		bytes[1] = (byte) cmd.p1.art;
		bytes[2] = (byte) cmd.p2.art;
		long p1num = cmd.p1.num, p1off = cmd.p1.off, p2num = cmd.p2.num, p2off = cmd.p2.off;
		int p1art = cmd.p1.art, p2art = cmd.p2.art, index = 7;
		{
			switch (p1art) {
			case Param.ART_ANUM:
				break;
			case Param.ART_ANUM_BNUM:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				break;
			case Param.ART_ANUM_BREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				break;
			case Param.ART_ANUM_BSR:
				Param.checkSR(p1off);
				bytes[index -- ] = (byte) p1off;
				break;
			case Param.ART_ASR:
				Param.checkSR(p1num);
				bytes[index -- ] = (byte) p1num;
				break;
			case Param.ART_ASR_BNUM:
				Param.checkSR(p1num);
				bytes[index -- ] = (byte) p1num;
				break;
			case Param.ART_ASR_BREG:
				Param.checkSR(p1num);
				bytes[index -- ] = (byte) p1num;
				break;
			case Param.ART_ASR_BSR:
				Param.checkSR(p1num);
				Param.checkSR(p1off);
				bytes[index -- ] = (byte) p1num;
				bytes[index -- ] = (byte) p1off;
				break;
			}
			switch (p2art) {
			case Param.ART_ANUM:
				break;
			case Param.ART_ANUM_BNUM:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				break;
			case Param.ART_ANUM_BREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				break;
			case Param.ART_ANUM_BSR:
				Param.checkSR(p2off);
				bytes[index -- ] = (byte) p2off;
				break;
			case Param.ART_ASR:
				Param.checkSR(p2num);
				bytes[index -- ] = (byte) p2num;
				break;
			case Param.ART_ASR_BNUM:
				Param.checkSR(p2num);
				bytes[index -- ] = (byte) p2num;
				break;
			case Param.ART_ASR_BREG:
				Param.checkSR(p2num);
				bytes[index -- ] = (byte) p2num;
				break;
			case Param.ART_ASR_BSR:
				Param.checkSR(p2num);
				Param.checkSR(p2off);
				bytes[index -- ] = (byte) p2num;
				bytes[index -- ] = (byte) p2off;
				break;
			}
		}
		{
			switch (p1art) {
			case Param.ART_ANUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				break;
			case Param.ART_ANUM_BNUM:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1off);
				break;
			case Param.ART_ANUM_BREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				break;
			case Param.ART_ANUM_BSR:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				break;
			case Param.ART_ASR:
				break;
			case Param.ART_ASR_BNUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				break;
			case Param.ART_ASR_BREG:
				break;
			case Param.ART_ASR_BSR:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				break;
			}
			switch (p2art) {
			case Param.ART_ANUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				break;
			case Param.ART_ANUM_BNUM:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to add two constant numbers at runtime to access memory.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2off);
				break;
			case Param.ART_ANUM_BREG:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				break;
			case Param.ART_ANUM_BSR:
				if ( !supressWarn) {
					System.err.println("[WARN]: It is not recommended to access memory with a constant adress.");
				}
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				break;
			case Param.ART_ASR:
				break;
			case Param.ART_ASR_BNUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				break;
			case Param.ART_ASR_BREG:
				break;
			case Param.ART_ASR_BSR:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				break;
			}
		}
	}
	
}
