package de.hechler.patrick.codesprachen.primitive.compile.c.objects.cmds;

import de.hechler.patrick.codesprachen.primitive.compile.c.objects.CVariable;

public class CVarDeclareCommand extends CCommand {
	
	private final CVariable[] declare;
	
	
	
	public CVarDeclareCommand(CVariable[] declare) {
		super();
		this.declare = declare;
	}
	
	
	public CVariable[] getDeclare() {
		return declare.clone();
	}


}
