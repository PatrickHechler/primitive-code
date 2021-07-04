package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;

public class StrCommand extends Command{

	private StrCommand(Commands art, String val) {
		super(art, Wert.createConstantString(val));
	}
	
	public static StrCommand create(Commands art, String val) {
		return new StrCommand(art, val);
	}
	
}
