package de.patrick.hechler.codesprachen.primitive.assemble.objects;

import de.patrick.hechler.codesprachen.primitive.assemble.enums.CompilerDirective;

public class CompilerDirectiveCommand extends Command {
	
	public final CompilerDirective directive;
	
	public CompilerDirectiveCommand(CompilerDirective directive) {
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
