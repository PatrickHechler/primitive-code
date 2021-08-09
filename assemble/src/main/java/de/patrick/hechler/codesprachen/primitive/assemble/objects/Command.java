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
import edu.emory.mathcs.backport.java.util.Arrays;

public class Command {
	
	public final Commands cmd;
	public final Param p1;
	public final Param p2;
	
	public Command(Commands cmd, Param p1, Param p2) {
		this.cmd = cmd;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public void check(Map <String, Long> labels) {
		switch (cmd) {
		case CMD_CMP:
			// p2.checkTwoParam(constants, true);
			// p2.checkTwoParam(constants, true);
			break;
		case CMD_ADD:
		case CMD_AND:
		case CMD_MOV:
		case CMD_MUL:
		case CMD_OR:
		case CMD_SUB:
		case CMD_XOR:
			// p2.checkTwoParam(constants, false);
			// p2.checkTwoParam(constants, true);
			break;
		case CMD_DIV:
			// p2.checkTwoParam(constants, false);
			// p2.checkTwoParam(constants, false);
			break;
		case CMD_NEG:
		case CMD_NOT:
		case CMD_POP:
			// p1.checkOneParam(constants, false);
			if (p2 != null) {
				throw new IllegalStateException("I can't have a second param on this command cmd: '" + cmd.name() + "' p1: '" + p1 + "' + p2: '" + p2 + "'");
			}
			break;
		case CMD_INT:
		case CMD_PUSH:
		case CMD_SET_IP:
			// p1.checkOneParam(constants, true);
			if (p2 != null) {
				throw new IllegalStateException("I can't have a second param on this command cmd: '" + cmd.name() + "' p1: '" + p1 + "' + p2: '" + p2 + "'");
			}
			break;
		case CMD_IRET:
		case CMD_RET:
			if (p1 != null || p2 != null) {
				throw new IllegalStateException("I can't have params on command <IRET> or <RET> cmd: '" + cmd.name() + "' p1: '" + p1 + "' + p2: '" + p2 + "'");
			}
			break;
		case CMD_CALL:
		case CMD_CALLEQ:
		case CMD_CALLNE:
		case CMD_CALLGE:
		case CMD_CALLGT:
		case CMD_CALLLE:
		case CMD_CALLLO:
		case CMD_JMP:
		case CMD_JMPEQ:
		case CMD_JMPGE:
		case CMD_JMPGT:
		case CMD_JMPLE:
		case CMD_JMPLO:
		case CMD_JMPNE:
			if (p2 != null) {
				throw new IllegalStateException("I can't have a second param on this command cmd: '" + cmd.name() + "' p1: '" + p1 + "' + p2: '" + p2 + "'");
			}
			break;
		case CMD_SCALL:
		case CMD_SCALLEQ:
		case CMD_SCALLNE:
		case CMD_SCALLGE:
		case CMD_SCALLGT:
		case CMD_SCALLLE:
		case CMD_SCALLLO:
		case CMD_SJMP:
		case CMD_SJMPEQ:
		case CMD_SJMPGE:
		case CMD_SJMPGT:
		case CMD_SJMPLE:
		case CMD_SJMPLO:
		case CMD_SJMPNE:
			if (p2 != null) {
				throw new IllegalStateException("I can't have a second param on this command cmd: '" + cmd.name() + "' p1: '" + p1 + "' + p2: '" + p2 + "'");
			}
			break;
		}
	}
	
	public static class ConstantPoolCommand extends Command {
		
		private List <byte[]> values;
		private long len;
		
		public ConstantPoolCommand() {
			super(null, null, null);
			values = new ArrayList <>();
			len = 0;
		}
		
		@Override
		public void check(Map <String, Long> labels) {
		}
		
		public void addBytes(byte[] bytes) {
			len += bytes.length / 8;
			int mod = bytes.length % 8;
			if (mod != 0) {
				int l = bytes.length;
				int nl = l + (8 - mod);
				bytes = Arrays.copyOf(bytes, nl);
				for (; l < bytes.length; l ++ ) {
					bytes[l] = 0x00;
				}
				len ++ ;
				System.err.println("[WARN]: the added bytes does not fit in the right len (len % 8 != 0) added empty (0x00) bytes to the end (len=" + l + " newLen=" + nl + ")");
			}
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
		switch(cmd) {
		case CMD_ADD:
		case CMD_AND:
		case CMD_MOV:
		case CMD_MUL:
		case CMD_NEG:
		case CMD_NOT:
		case CMD_OR:
		case CMD_POP:
		case CMD_PUSH:
		case CMD_RET:
		case CMD_SCALL:
		case CMD_SCALLEQ:
		case CMD_SCALLGE:
		case CMD_SCALLGT:
		case CMD_SCALLLE:
		case CMD_SCALLLO:
		case CMD_SCALLNE:
		case CMD_SET_IP:
		case CMD_SJMP:
		case CMD_SJMPEQ:
		case CMD_SJMPGE:
		case CMD_SJMPGT:
		case CMD_SJMPLE:
		case CMD_SJMPLO:
		case CMD_SJMPNE:
		case CMD_SUB:
		case CMD_XOR:
		case CMD_CMP:
		case CMD_DIV:
		case CMD_INT:
		case CMD_IRET:
			return 1;
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
		case CMD_JMPNE:
			return 2;
		default:
			throw new AssertionError("unknown enum constant of my Command: " + cmd.name());
		}
	}
	
}

