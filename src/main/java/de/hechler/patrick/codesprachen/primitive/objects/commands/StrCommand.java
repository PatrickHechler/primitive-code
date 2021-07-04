package de.hechler.patrick.codesprachen.primitive.objects.commands;

import de.hechler.patrick.codesprachen.primitive.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.objects.Wert;

public class StrCommand extends Command{

	private StrCommand(Commands art, String val) {
		super(art, Wert.createConstantString(val));
	}
	
	public StrCommand create(Commands art, String val) {
		return new StrCommand(art, val);
	}
	
}
