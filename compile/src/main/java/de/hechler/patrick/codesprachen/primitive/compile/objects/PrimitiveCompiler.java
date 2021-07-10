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
			case add:
			case sub:
			case mul:
			case div:
			case and:
			case or:
			case xor: {
				out.write(cmds[i].art.nummer);
				Num num = cmds[i].params.get(0).getNum();
				num.checkMDB1();
				out.write(num.numDeep - 1);
				writeLong(num.num);
				num = cmds[i].params.get(1).getNum();
				num.checkMDB0();
				out.write(num.numDeep);
				writeLong(num.num);
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
				num.checkMDB0();
				out.write(num.numDeep);
				writeLong(num.num);
				num = cmds[i].params.get(1).getNum();
				num.checkMDB0();
				out.write(num.numDeep);
				writeLong(num.num);
				break;
			}
			case neg:
			case not:
			case pop: {
				out.write(cmds[i].art.nummer);
				Num num = cmds[i].params.get(0).getNum();
				num.checkMDB1();
				out.write(num.numDeep - 1);
				writeLong(num.num);
				break;
			}
			case push: {
				out.write(cmds[i].art.nummer);
				Num num = cmds[i].params.get(0).getNum();
				num.checkMDB0();
				out.write(num.numDeep);
				writeLong(num.num);
				break;
			}
			case label:// nothing to do
			}
		}
		out.flush();
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
