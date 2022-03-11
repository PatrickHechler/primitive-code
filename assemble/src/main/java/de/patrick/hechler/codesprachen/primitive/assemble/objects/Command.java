package de.patrick.hechler.codesprachen.primitive.assemble.objects;

import java.util.Map;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRErrorStrategy;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarLexer;
import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarParser;
import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
import de.patrick.hechler.codesprachen.primitive.assemble.enums.Commands;
import de.patrick.hechler.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.patrick.hechler.codesprachen.primitive.assemble.exceptions.AssembleRuntimeException;

public class Command {
	
	public final Commands cmd;
	public final Param p1;
	public final Param p2;
	
	public Command(Commands cmd, Param p1, Param p2) {
		this.cmd = cmd;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public static ConstsContext parseCP(String cp, Map <String, Long> constants, Map <String, Long> labels, long pos, boolean align, int line, int posInLine, int charPos,
			boolean bailError, ANTLRErrorStrategy errorHandler, ANTLRErrorListener errorListener) {
		ANTLRInputStream in = new ANTLRInputStream(cp);
		ConstantPoolGrammarLexer lexer = new ConstantPoolGrammarLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ConstantPoolGrammarParser parser = new ConstantPoolGrammarParser(tokens);
		if (errorHandler != null) {
			parser.setErrorHandler(errorHandler);
		}
		if (errorListener != null) {
			parser.addErrorListener(errorListener);
		}
		try {
			ConstsContext constantPool = parser.consts(constants, labels, pos, align, bailError);
			return constantPool;
		} catch (AssembleRuntimeException ae) {
			assert false;
			AssembleRuntimeException err = new AssembleRuntimeException(line + ae.line, ae.line == 0 ? ae.posInLine + posInLine : ae.posInLine, ae.length, charPos + ae.charPos,
					ae.getMessage());
			err.setStackTrace(ae.getStackTrace());
			throw err;
		} catch (AssembleError ae) {
			assert bailError;
			AssembleError err = new AssembleError(line, posInLine, ae.length, ae.charPos, ae.getMessage());
			err.setStackTrace(ae.getStackTrace());
			throw err;
		} catch (ParseCancellationException e) {
			Throwable cause = e.getCause();
			if (cause instanceof AssembleError) {
				AssembleError ae = (AssembleError) cause;
				AssembleError err = new AssembleError(line + ae.line, ae.posInLine + (ae.length == 0 ? posInLine : 0), ae.length, ae.charPos, ae.getMessage());
				err.setStackTrace(ae.getStackTrace());
				throw err;
			} else if (cause instanceof NoViableAltException) {
				NoViableAltException nvae = (NoViableAltException) cause;
				Token ot = nvae.getOffendingToken();
				String[] names = parser.getTokenNames();
				StringBuilder msg = new StringBuilder("illegal token: ").append(ot).append("\nexpected: [");
				IntervalSet ets = nvae.getExpectedTokens();
				for (int i = 0; i < ets.size(); i ++ ) {
					if (i > 0) {
						msg.append(", ");
					}
					int t = ets.get(i);
					msg.append('<').append(names[t]).append('>');
				}
				int lineAdd = ot.getLine();
				throw new AssembleError(line + lineAdd, lineAdd == 0 ? posInLine + ot.getCharPositionInLine() : ot.getCharPositionInLine(),
						ot.getStopIndex() - ot.getStartIndex() + 1, ot.getStartIndex(), msg.append(']').toString());
			} else {
				throw new AssembleError(line, posInLine, 1, charPos, "ParseCancelationException: " + e.getMessage());
			}
		}
	}
	
	public long length() {
		switch (cmd) {
		case CMD_ADD:
		case CMD_ADDC:
		case CMD_ADDFP:
		case CMD_AND:
		case CMD_MOV:
		case CMD_LEA:
		case CMD_MUL:
		case CMD_MULFP:
		case CMD_OR:
		case CMD_SUB:
		case CMD_SUBC:
		case CMD_SUBFP:
		case CMD_XOR:
		case CMD_CMP:
		case CMD_LSH:
		case CMD_RASH:
		case CMD_RLSH:
		case CMD_DIV:
		case CMD_DIVFP:
			int len;
			switch (p1.art) {
			case Param.ART_ANUM_BNUM:
				len = 24;
				break;
			case Param.ART_ANUM:
			case Param.ART_ASR_BNUM:
			case Param.ART_ANUM_BREG:
			case Param.ART_ANUM_BSR:
				len = 16;
				break;
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
			case Param.ART_ASR_BSR:
				len = 8;
				break;
			default:
				throw new IllegalStateException("unknown art: " + Param.artToString(p1.art) + " " + this);
			}
			switch (p2.art) {
			case Param.ART_ANUM_BNUM:
				len += 16;
			case Param.ART_ANUM:
			case Param.ART_ASR_BNUM:
			case Param.ART_ANUM_BREG:
			case Param.ART_ANUM_BSR:
				len += 8;
				break;
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
			case Param.ART_ASR_BSR:
				break;
			default:
				throw new IllegalStateException("unknown art: " + Param.artToString(p2.art) + " " + this);
			}
			return len;
		case CMD_POP:
		case CMD_PUSH:
		case CMD_DEC:
		case CMD_INC:
		case CMD_INT:
		case CMD_NEG:
		case CMD_NOT:
		case CMD_NTFP:
		case CMD_FPTN:
			switch (p1.art) {
			case Param.ART_ANUM_BNUM:
				return 24;
			case Param.ART_ANUM:
			case Param.ART_ASR_BNUM:
			case Param.ART_ANUM_BREG:
			case Param.ART_ANUM_BSR:
				return 16;
			case Param.ART_ASR:
			case Param.ART_ASR_BREG:
			case Param.ART_ASR_BSR:
				return 8;
			default:
				throw new IllegalStateException("unknown art: " + Param.artToString(p1.art));
			}
		case CMD_IRET:
		case CMD_RET:
			return 8;
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
		case CMD_JMPZC:
			return 16;
		default:
			throw new AssertionError("unknown enum constant of my Command: " + cmd.name());
		}
	}
	
	public boolean alignable() {
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cmd[");
		builder.append(cmd);
		if (p1 != null) {
			builder.append(", p1=");
			builder.append(p1);
			if (p2 != null) {
				builder.append(", p2=");
				builder.append(p2);
			}
		}
		builder.append("]");
		return builder.toString();
	}
	
}
