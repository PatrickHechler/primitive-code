package de.hechler.patrick.codesprachen.primitive.compile.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hechler.patrick.antlr.v4.codespr.primitive.PrimGrammarParser.DateiContext;
import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.commands.Command;

public class PrimitiveCompiler {
	
	private OutputStream out;
	
	
	public PrimitiveCompiler(OutputStream out) {
		this.out = out;
	}
	
	
	public void compile(DateiContext datei) throws IOException {
		Map <String, Long> labels = new HashMap <>();
		long len = 0;
		Command[] cmds = datei.cmds.toArray(new Command[0]);
		for (int i = 0; i < cmds.length; i ++ ) {
			if (cmds[i].art == Commands.label) {
				labels.put(cmds[i].params.get(0).getConstStr(), len);
			} else {
				len += cmds[i].art.length;
			}
		}
		for (int i = 0; i < cmds.length; i ++ ) {
			switch (cmds[i].art) {
			case div: {
				out.write(cmds[i].art.nummer);
				Num num = cmds[i].params.get(0).getNum();
				writeNumB1(num);
				num = cmds[i].params.get(1).getNum();
				writeNumB1(num);
				break;
			}
			case mov:
			case add:
			case sub:
			case mul:
			case and:
			case or:
			case xor: {
				out.write(cmds[i].art.nummer);
				Num num = cmds[i].params.get(0).getNum();
				writeNumB1(num);
				num = cmds[i].params.get(1).getNum();
				writeNumB0(num);
				break;
			}
			case call:
			case calleq:
			case callge:
			case callgt:
			case callle:
			case calllo:
			case callne:
			case jmp:
			case jmpeq:
			case jmpge:
			case jmpgt:
			case jmple:
			case jmplo:
			case jmpne:
				out.write(cmds[i].art.nummer);
				String target = cmds[i].params.get(0).getConstStr();
				long dest = (long) labels.get(target);
				writeLong(dest);
				break;
			case cmp: {
				out.write(cmds[i].art.nummer);
				Num num = cmds[i].params.get(0).getNum();
				writeNumB0(num);
				num = cmds[i].params.get(1).getNum();
				writeNumB0(num);
				break;
			}
			case neg:
			case not:
			case pop: {
				out.write(cmds[i].art.nummer);
				Num num = cmds[i].params.get(0).getNum();
				writeNumB1(num);
				break;
			}
			case push:
			case exit: {
				out.write(cmds[i].art.nummer);
				Num num = cmds[i].params.get(0).getNum();
				writeNumB0(num);
				break;
			}
			case ret:
				out.write(cmds[i].art.nummer);
				break;
			case label:// nothing to do
				break;
			}
		}
		out.flush();
	}
	
	
	private void writeNumB0(Num num) throws IOException {
		num.checkMDB0();
		if (num.isNum()) {
			out.write(num.deep);
			writeLong(num.num());
		} else {
			out.write(num.deep | (num.sr() << 6) | 0x20);
		}
	}
	
	
	private void writeNumB1(Num num) throws IOException {
		num.checkMDB1();
		if (num.isNum()) {
			out.write(num.deep - 1);
			writeLong(num.num());
		} else {
			out.write( (num.deep - 1) | (num.sr() << 6) | 0x20);
		}
	}
	
	private void writeLong(long val) throws IOException {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) val;
		bytes[1] = (byte) (val >> 8);
		bytes[2] = (byte) (val >> 16);
		bytes[3] = (byte) (val >> 24);
		bytes[4] = (byte) (val >> 32);
		bytes[5] = (byte) (val >> 40);
		bytes[6] = (byte) (val >> 48);
		bytes[7] = (byte) (val >> 56);
		out.write(bytes);
	}
	
}
