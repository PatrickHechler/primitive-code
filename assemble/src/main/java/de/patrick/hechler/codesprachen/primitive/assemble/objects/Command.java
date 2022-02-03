package de.patrick.hechler.codesprachen.primitive.assemble.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarLexer;
import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarParser;
import de.patrick.hechler.codesprachen.primitive.assemble.ConstantPoolGrammarParser.ConstsContext;
import de.patrick.hechler.codesprachen.primitive.assemble.enums.Commands;

public class Command {
	
	public final Commands cmd;
	public final Param p1;
	public final Param p2;
	
	public Command(Commands cmd, Param p1, Param p2) {
		this.cmd = cmd;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public static class ConstantPoolCommand extends Command {
		
		private List <byte[]> values;
		private long len;
		
		public ConstantPoolCommand() {
			super(null, null, null);
			values = new ArrayList <>();
			len = 0;
		}
		
		public void addBytes(byte[] bytes) {
			len += bytes.length;
			values.add(bytes);
		}
		
		@Override
		public long length() {
			return len;
		}
		
		public void write(OutputStream out) throws IOException {
			for (int i = 0; i < values.size(); i ++ ) {
				out.write(values.get(i));
			}
		}
		
	}
	
	public static ConstantPoolCommand parseCP(String cp, Map <String, Long> constants, Map <String, Long> labels, long pos) {
		ANTLRInputStream in = new ANTLRInputStream(cp);
		ConstantPoolGrammarLexer lexer = new ConstantPoolGrammarLexer(in);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ConstantPoolGrammarParser parser = new ConstantPoolGrammarParser(tokens);
		ConstsContext parsed = parser.consts(constants, labels, pos);
		return parsed.pool;
	}
	
	public long length() {
		switch (cmd) {
		case CMD_ADD:
		case CMD_ADDC:
		case CMD_ADDFP:
		case CMD_AND:
		case CMD_MOV:
		case CMD_MUL:
		case CMD_MULFP:
		case CMD_OR:
		case CMD_SUB:
		case CMD_SUBC:
		case CMD_SUBFP:
		case CMD_XOR:
		case CMD_CMP:
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
				throw new IllegalStateException("unknown art: " + Param.artToString(p1.art));
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
				throw new IllegalStateException("unknown art: " + Param.artToString(p2.art));
			}
			return len;
		case CMD_POP:
		case CMD_PUSH:
		case CMD_DEC:
		case CMD_INC:
		case CMD_SET_IP:
		case CMD_SET_SP:
		case CMD_SET_INTS:
		case CMD_GET_IP:
		case CMD_GET_SP:
		case CMD_GET_INTS:
		case CMD_INT:
		case CMD_NEG:
		case CMD_NOT:
		case CMD_LSH:
		case CMD_RASH:
		case CMD_RLSH:
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
			return 24;
		default:
			throw new AssertionError("unknown enum constant of my Command: " + cmd.name());
		}
	}
	
}

