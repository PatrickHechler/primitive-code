package de.patrick.hechler.codesprachen.primitive.assemble.objects;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarLexer;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarParser;
import de.patrick.hechler.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.patrick.hechler.codesprachen.primitive.assemble.exceptions.AssembleError;

public class PrimitiveAssembler {
	
	private static final Map<String, Long> START_CONSTANTS;
	
	static {
		START_CONSTANTS = new LinkedHashMap<>();//faster iteration
		START_CONSTANTS.put("INT-ERRORS-UNKNOWN_COMMAND", (Long) 0L);
		START_CONSTANTS.put("INT-ERRORS-ILLEGAL_INTERRUPT", (Long) 1L);
		START_CONSTANTS.put("INT-ERRORS-ILLEGAL_MEMORY", (Long) 2L);
		START_CONSTANTS.put("INT-ERRORS-ARITHMETIC_ERROR", (Long) 3L);
		START_CONSTANTS.put("INT-EXIT", (Long) 4L);
		START_CONSTANTS.put("INT-MEMORY-ALLOC", (Long) 5L);
		START_CONSTANTS.put("INT-MEMORY-REALLOC", (Long) 6L);
		START_CONSTANTS.put("INT-MEMORY-FREE", (Long) 7L);
		START_CONSTANTS.put("INT-STREAMS-NEW_IN", (Long) 8L);
		START_CONSTANTS.put("INT-STREAMS-NEW_OUT", (Long) 9L);
		START_CONSTANTS.put("INT-STREAMS-NEW_APPEND", (Long) 10L);
		START_CONSTANTS.put("INT_STREAMS-NEW_IN_OUT", (Long) 11L);
		START_CONSTANTS.put("INT-STREAMS-NEW_APPEND_IN_OUT", (Long) 12L);
		START_CONSTANTS.put("INT-STREAMS-WRITE", (Long) 13L);
		START_CONSTANTS.put("INT-STREAMS-READ", (Long) 14L);
		START_CONSTANTS.put("INT-STREAMS-CLOSE_STREAM", (Long) 15L);
		START_CONSTANTS.put("INT-STREAMS-GET_POS", (Long) 16L);
		START_CONSTANTS.put("INT-STREAMS-SET_POS", (Long) 17L);
		START_CONSTANTS.put("INT-STREAMS-SET_POS_TO_END", (Long) 18L);
		START_CONSTANTS.put("INT-STREAMS-REM", (Long) 19L);
		START_CONSTANTS.put("INT-STREAMS-MK_DIR", (Long) 20L);
		START_CONSTANTS.put("INT-STREAMS-REM_DIR", (Long) 21L);
		START_CONSTANTS.put("INT-TIME-GET", (Long) 22L);
		START_CONSTANTS.put("INT-TIME-WAIT", (Long) 23L);
		START_CONSTANTS.put("INT-SOCKET-CLIENT-CREATE", (Long) 24L);
		START_CONSTANTS.put("INT-SOCKET-CLIENT-CONNECT", (Long) 25L);
		START_CONSTANTS.put("INT-SOCKET-SERVER-CREATE", (Long) 26L);
		START_CONSTANTS.put("INT-SOCKET-SERVER-LISTEN", (Long) 27L);
		START_CONSTANTS.put("INT-SOCKET-SERVER-ACCEPT", (Long) 28L);
		START_CONSTANTS.put("INT-RANDOM", (Long) 29L);
		START_CONSTANTS.put("INTERRUPT_COUNT", (Long) 30L);
		START_CONSTANTS.put("INT-FUNC-MEMORY_COPY", (Long) 30L);
		START_CONSTANTS.put("INT-FUNC-MEMORY_MOVE", (Long) 31L);
		START_CONSTANTS.put("INT-FUNC-MEMORY_SET", (Long) 32L);
		START_CONSTANTS.put("INT-FUNC-MEMORY_BSET", (Long) 33L);
		START_CONSTANTS.put("INT-FUNC-STRING_TO_NUMBER", (Long) 34L);
		START_CONSTANTS.put("INT-FUNC-NUMBER_TO_STRING", (Long) 35L);
		START_CONSTANTS.put("INT-FUNC-STRING_FORMAT", (Long) 36L);
		START_CONSTANTS.put("COMPLETE_INTERRUPT_COUNT", (Long) 37L);
		START_CONSTANTS.put("MAX-VALUE", (Long) 0x7FFFFFFFFFFFFFFFL);
		START_CONSTANTS.put("MIN-VALUE", (Long) (-0x8000000000000000L));
		START_CONSTANTS.put("STD-IN", (Long) 0L);
		START_CONSTANTS.put("STD-OUT", (Long) 1L);
		START_CONSTANTS.put("STD-LOG", (Long) 2L);
		START_CONSTANTS.put("FP-NAN", (Long) 0x7FFE000000000000L);
		START_CONSTANTS.put("FP-MAX-VALUE", (Long) 0x7FEFFFFFFFFFFFFFL);
		START_CONSTANTS.put("FP-MIN-VALUE", (Long) 0x0000000000000001L);
		START_CONSTANTS.put("FP-POS-INFINITY", (Long) 0x7FF0000000000000L);
		START_CONSTANTS.put("FP-NEG-INFINITY", (Long) 0xFFF0000000000000L);
	}
	
	private final OutputStream out;
	private final boolean supressWarn;
	private final boolean defaultAlign;
	private final boolean exitOnError;
	
	public PrimitiveAssembler(OutputStream out) {
		this(out, false);
	}
	
	public PrimitiveAssembler(OutputStream out, boolean supressWarnings) {
		this(out, supressWarnings, true);
		
	}
	
	public PrimitiveAssembler(OutputStream out, boolean supressWarnings, boolean defaultAlign) {
		this(out, supressWarnings, defaultAlign, true);
	}
	
	public PrimitiveAssembler(OutputStream out, boolean supressWarnings, boolean defaultAlign, boolean exitOnError) {
		this.out = out;
		this.supressWarn = supressWarnings;
		this.defaultAlign = defaultAlign;
		this.exitOnError = exitOnError;
	}
	
	
	
	public ParseContext preassemble(InputStream in) throws IOException, AssembleError {
		return preassemble(new InputStreamReader(in));
	}
	
	public ParseContext preassemble(InputStream in, Charset cs) throws IOException, AssembleError {
		return preassemble(new InputStreamReader(in, cs));
	}
	
	public ParseContext preassemble(Reader in) throws IOException, AssembleError {
		HashMap <String, Long> constants = new HashMap <>();
		return preassemble(in, constants);
	}
	
	public ParseContext preassemble(InputStream in, Map <String, Long> predefinedConstants) throws IOException, AssembleError {
		return preassemble(new InputStreamReader(in), predefinedConstants);
	}
	
	public ParseContext preassemble(InputStream in, Charset cs, Map <String, Long> predefinedConstants) throws IOException, AssembleError {
		return preassemble(new InputStreamReader(in, cs), predefinedConstants);
	}
	
	public ParseContext preassemble(Reader in, Map <String, Long> predefinedConstants) throws IOException, AssembleError {
		return preassemble(new ANTLRInputStream(in), predefinedConstants);
	}
	
	public ParseContext preassemble(ANTLRInputStream antlrin) throws IOException, AssembleError {
		return preassemble(antlrin, new HashMap <>(START_CONSTANTS));
	}
	
	public ParseContext preassemble(ANTLRInputStream antlrin, Map <String, Long> predefinedConstants) throws IOException, AssembleError {
		PrimitiveFileGrammarLexer lexer = new PrimitiveFileGrammarLexer(antlrin);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		PrimitiveFileGrammarParser parser = new PrimitiveFileGrammarParser(tokens);
		parser.setErrorHandler(new BailErrorStrategy());
		try {
			return parser.parse(0L, defaultAlign, predefinedConstants);
		} catch (ParseCancellationException e) {
			Throwable cause = e.getCause();
			if (cause == null) {
				throw e;
			}
			if (cause instanceof AssembleError) {
				AssembleError ae = (AssembleError) cause;
				handle(ae);
			} else if (cause instanceof NoViableAltException) {
				NoViableAltException nvae = (NoViableAltException) cause;
				handle(nvae);
			} else {
				throw e;
			}
		} catch (AssembleError ae) {
			handle(ae);
		}
		throw new InternalError("handle returned");
	}
	
	/*
	 * the handle methods will never return normally
	 * 
	 * they either throw an error or call System.exit(1)
	 */
	private void handle(NoViableAltException nvae) {
		Token ot = nvae.getOffendingToken();
		IntervalSet ets = nvae.getExpectedTokens();
		if (exitOnError) {
			System.err.println("at line: " + ot.getLine() + ':' + ot.getCharPositionInLine());
			System.err.println("illegal input: " + ot.getText());
			System.err.println("  token: " + tokenToString(ot, PrimitiveFileGrammarLexer.ruleNames));
			System.err.println("expected: ");
			for (int i = 0; i < ets.size(); i ++ ) {
				System.err.println("  " + tokenToString(ot, PrimitiveFileGrammarLexer.ruleNames));
			}
			System.err.flush();
			System.exit(1);
		} else {
			StringBuilder build = new StringBuilder("at line ").append(ot.getLine()).append(':').append(ot.getCharPositionInLine()).append(" token.text='").append(ot.getText());
			build.append("' token.id=").append(tokenToString(ot, PrimitiveFileGrammarLexer.ruleNames)).append('\n').append("expected: ");
			for (int i = 0; i < ets.size(); i ++ ) {
				if (i > 0) {
					build.append(", ");
				}
				build.append(' ').append(tokenToString(ot, PrimitiveFileGrammarLexer.ruleNames));
			}
			throw new AssembleError(ot.getLine(), ot.getCharPositionInLine(), build.toString());
		}
	}
	
	private String tokenToString(Token ot, String[] names) {
		String token;
		if (ot.getType() > 0) {
			token = "<" + names[ot.getType()] + '>';
		} else if (ot.getType() == PrimitiveFileGrammarLexer.EOF) {
			token = "<EOF>";
		} else {
			token = "<UNKNOWN=" + ot.getType() + ">";
		}
		return token;
	}
	
	// private void handle(AssembleError ae) {
	// Throwable cause = ae.getCause();
	// try {
	// if (cause == null) {
	// throw new InternalError("cause is null", ae);
	// }
	// if ( ! (cause instanceof ParseCancellationException)) {
	// throw new InternalError("unknown cause class: " + cause.getClass().getName(), ae);
	// }
	// ParseCancellationException pce = (ParseCancellationException) cause;
	// cause = pce.getCause();
	// if (cause instanceof NoViableAltException) {
	// NoViableAltException nvae = (NoViableAltException) cause;
	// Token ot = nvae.getOffendingToken();
	// int line = ot.getLine();
	// int posInLine = ot.getCharPositionInLine();
	// if (line == 0) {
	// posInLine += ae.posInLine;
	// }
	// line += ae.line;
	// if (exitOnError) {
	// System.err.println("at " + line + ":" + posInLine + " was illegal input: " + ot.getText());
	// System.err.flush();
	// System.exit(1);
	// } else {
	// throw new Error("at line: " + line + " at char: " + posInLine + " was illegal input: " +
	// ot.getText(), nvae);
	// }
	// } else if (cause instanceof AssembleException) {
	// handle(ae.line, ae.posInLine, (AssembleException) cause);
	// } else {
	// throw new InternalError("unknown cause cause class: " + cause.getClass().getName(), cause);
	// }
	// } catch (Throwable t) {
	// if (exitOnError) {
	// t.printStackTrace(System.err);
	// System.err.flush();
	// System.exit(1);
	// }
	// throw t;
	// }
	// }
	//
	private void handle(AssembleError ae) {
		if (exitOnError) {
			System.out.println("an error occured at line: " + ae.line + ':' + ae.posInLine);
			System.out.println(ae.getMessage());
			System.out.flush();
			System.exit(1);
		} else {
			throw new AssembleError(ae.line, ae.posInLine, "at line: " + ae.line + ":" + ae.posInLine + " occured an error: " + ae.getMessage());
		}
	}
	
	public void assemble(InputStream in) throws IOException, AssembleError {
		assemble(preassemble(in));
	}
	
	public void assemble(InputStream in, Charset cs) throws IOException, AssembleError {
		assemble(preassemble(in, cs));
	}
	
	public void assemble(Reader in) throws IOException, AssembleError {
		assemble(preassemble(in));
	}
	
	public void assemble(ANTLRInputStream antlrin) throws IOException, AssembleError {
		assemble(preassemble(antlrin));
	}
	
	public void assemble(ANTLRInputStream antlrin, Map <String, Long> predefinedConstants) throws IOException, AssembleError {
		assemble(preassemble(antlrin, predefinedConstants));
	}
	
	public void assemble(PrimitiveFileGrammarParser.ParseContext parsed) throws IOException {
		assemble(parsed.commands, parsed.labels);
	}
	
	public void assemble(List <Command> cmds, Map <String, Long> labels) throws IOException {
		long pos = 0;
		boolean align = defaultAlign;
		Command last = null, cmd;
		for (int i = 0; i < cmds.size(); i ++ ) {
			cmd = cmds.get(i);
			if (align && last != null && last.alignable()) {
				int mod = (int) (pos % 8);
				if (mod != 0) {
					int add = 8 - mod;
					byte[] bytes = new byte[add];
					out.write(bytes);
					pos += add;
				}
			}
			if (cmd.getClass() == Command.class) {
				last = cmd;// only on command and constant-pool (not on directives)
				byte[] bytes = new byte[8];
				bytes[0] = (byte) cmd.cmd.num;
				switch (cmd.cmd) {
				case CMD_RET:
				case CMD_IRET:
					break;// nothing more to write
				case CMD_RASH:
				case CMD_RLSH:
				case CMD_LSH:
				case CMD_NEG:
				case CMD_NOT:
				case CMD_PUSH:
				case CMD_GET_SP:
				case CMD_GET_IP:
				case CMD_GET_INTS:
				case CMD_GET_INTCNT:
				case CMD_DEC:
				case CMD_INC:
				case CMD_FPTN:
				case CMD_NTFP:
					if (cmd.p1.art == Param.ART_ANUM) {
						throw new IllegalStateException("no constants allowed!");
					}
				case CMD_SET_SP:
				case CMD_SET_IP:
				case CMD_SET_INTS:
				case CMD_SET_INTCNT:
				case CMD_INT:
				case CMD_POP:
					writeOneParam(cmd, bytes);
					break;
				case CMD_DIV:
				case CMD_SWAP:
					if (cmd.p2.art == Param.ART_ANUM) {
						throw new IllegalStateException("no constants allowed on any param!");
					}
				case CMD_MOV:
				case CMD_ADD:
				case CMD_ADDC:
				case CMD_ADDFP:
				case CMD_SUB:
				case CMD_SUBC:
				case CMD_SUBFP:
				case CMD_MUL:
				case CMD_MULFP:
				case CMD_DIVFP:
				case CMD_AND:
				case CMD_OR:
				case CMD_XOR:
					if (cmd.p1.art == Param.ART_ANUM) {
						throw new IllegalStateException("no constants allowed on the first param!");
					}
				case CMD_CMP: {
					assert cmd.p1 != null;
					assert cmd.p2 != null;
					writeTwoParam(cmd, bytes);
					break;
				}
				case CMD_CALL:
				case CMD_JMP:
				case CMD_JMPCS:
				case CMD_JMPCC:
				case CMD_JMPEQ:
				case CMD_JMPGE:
				case CMD_JMPGT:
				case CMD_JMPLE:
				case CMD_JMPLT:
				case CMD_JMPNE:
				case CMD_JMPZS:
				case CMD_JMPZC: {
					assert cmd.p1 != null : "I need a first param!";
					assert cmd.p2 == null : "my command can not have a second param";
					out.write(bytes, 0, bytes.length);
					long relativeDest;
					if (cmd.p1.art == Param.ART_LABEL) {
						assert cmd.p1.num == 0;
						assert cmd.p1.off == 0;
						final long absoluteDest = Objects.requireNonNull(labels.get(cmd.p1.label),
								"can't find the used label '" + cmd.p1.label + "', I know the labels '" + labels + "'");
						assert absoluteDest >= 0;
						relativeDest = absoluteDest - pos;
						assert pos + relativeDest == absoluteDest;
					} else if (cmd.p1.art == Param.ART_ANUM) {
						assert cmd.p1.label == null;
						assert cmd.p1.off == 0;
						if ( !supressWarn) {
							System.err.println("[WARN]: it is not recomended to use jump/call operation with a number instead of a label as param!");
						}
						relativeDest = cmd.p1.num;
					} else {
						throw new IllegalStateException("illegal param art: " + Param.artToString(cmd.p1.art));
					}
					convertLong(bytes, relativeDest);
					break;
				}
				default:
					throw new IllegalStateException("unknown command enum: " + cmd.cmd.name());
				}
				out.write(bytes, 0, bytes.length);
			} else if (cmd instanceof CompilerCommandCommand) {
				CompilerCommandCommand cdc = (CompilerCommandCommand) cmd;
				assert 0 == cdc.length();
				switch (cdc.directive) {
				case align:
					align = true;
					break;
				case notAlign:
					align = false;
					break;
				default:
					throw new InternalError("unknown directive: " + cdc.directive.name());
				}
			} else if (cmd instanceof ConstantPoolCommand) {
				last = cmd;
				ConstantPoolCommand cpc = (ConstantPoolCommand) cmd;
				cpc.write(out);
			} else {
				throw new InternalError("inknown command class: " + cmd.getClass().getName());
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
			break;
		default:
			throw new InternalError("unknown art: " + art);
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
		assert cmd.p1.label == null : "I don't need a label in my params!";
		assert cmd.p2.label == null : "I don't need a label in my params!";
		final long p1num = cmd.p1.num, p1off = cmd.p1.off, p2num = cmd.p2.num, p2off = cmd.p2.off;
		final int p1art = cmd.p1.art, p2art = cmd.p2.art;
		int index = 7;
		bytes[1] = (byte) p1art;
		bytes[2] = (byte) p2art;
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
			default:
				throw new InternalError("unknown art: " + p1art);
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
			default:
				throw new InternalError("unknown art: " + p2art);
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
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1num);
				break;
			case Param.ART_ASR:
				break;
			case Param.ART_ASR_BNUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p1off);
				break;
			case Param.ART_ASR_BREG:
				break;
			case Param.ART_ASR_BSR:
				break;
			default:
				throw new InternalError("unknown art: " + p1art);
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
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2num);
				break;
			case Param.ART_ASR:
				break;
			case Param.ART_ASR_BNUM:
				out.write(bytes, 0, bytes.length);
				convertLong(bytes, p2off);
				break;
			case Param.ART_ASR_BREG:
				break;
			case Param.ART_ASR_BSR:
				break;
			default:
				throw new InternalError("unknown art: " + p2art);
			}
		}
	}
	
}
