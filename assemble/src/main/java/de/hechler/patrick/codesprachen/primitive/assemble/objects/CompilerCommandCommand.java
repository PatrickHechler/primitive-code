package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import de.hechler.patrick.codesprachen.primitive.assemble.enums.CompilerCommand;

public class CompilerCommandCommand extends Command {

	public final CompilerCommand directive;
	public final long value;

	public CompilerCommandCommand(CompilerCommand directive) {
		this(directive, -1L);
	}

	public CompilerCommandCommand(CompilerCommand directive, long value) {
		super(null, null, null);
		this.directive = directive;
		this.value = value;
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
		if (value != -1L) {
			builder.append(", value=");
			builder.append(value);
			builder.append(" : 0x");
			builder.append(Long.toHexString(value));
		}
		builder.append(']');
		return builder.toString();
	}

}
