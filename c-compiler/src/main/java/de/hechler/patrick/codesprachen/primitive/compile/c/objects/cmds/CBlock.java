package de.hechler.patrick.codesprachen.primitive.compile.c.objects.cmds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hechler.patrick.codesprachen.primitive.compile.c.interfaces.Sealable;

public class CBlock extends CCommand implements Sealable {
	
	private List <CCommand> cmds = new ArrayList <>();
	
	public CBlock() {
		super();
	}
	
	
	@Override
	public void seal() {
		cmds = Collections.unmodifiableList(cmds);
	}
	
	public void add(CCommand cmd) {
		cmds.add(cmd);
	}
	
	public CCommand get(int index) {
		return cmds.get(index);
	}
	
	public int size() {
		return cmds.size();
	}
	
	@Override
	public String toString() {
		return toString("");
	}
	
	public String toString(String start) {
		StringBuilder build = new StringBuilder().append("{\n");
		final String subStart = start + "    ";
		for (CCommand cmd : cmds) {
			build.append(start);
			if (cmd instanceof CBlock) {
				build.append( ((CBlock) cmd).toString(subStart));
			} else {
				build.append(cmd);
			}
			build.append('\n');
		}
		return build.append('}').toString();
	}
	
}
