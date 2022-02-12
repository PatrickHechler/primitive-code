package de.patrick.hechler.codesprachen.primitive.assemble.objects;

import de.patrick.hechler.codesprachen.primitive.assemble.enums.CompilerCommand;

public class CompilerCommandCommand extends Command {
	
	public final CompilerCommand directive;
	
	public CompilerCommandCommand(CompilerCommand directive) {
		super(null, null, null);
		this.directive = directive;
	}
	
	@Override
	public long length() {
		return 0L;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CompilerDirectiveCommand [");
		builder.append(directive);
		builder.append("]");
		return builder.toString();
	}
	
}
