package de.hechler.patrick.codesprachen.primitive.compile.c.objects.cmds;


public abstract class CCmdCommand extends CCommand {
	
	public final CCommand sub;

	protected CCmdCommand(CCommand sub) {
		super();
		this.sub = sub;
	}
	
}
