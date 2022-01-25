package de.hechler.patrick.codesprachen.primitive.compile.c.objects.cmds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CBlock extends CCommand {
	
	private List <CCommand> cmds = new ArrayList <>();
	
	public CBlock() {
		super();
	}
	
	
	@Override
	public void seal() {
		cmds = Collections.unmodifiableList(cmds);
		for (CCommand cmd : cmds) {
			cmd.seal();
		}
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
	
}
