package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Num;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;

public class NumCommand extends Command {
	
	public NumCommand(Commands art, Num num) {
		super(art, Wert.createNumber(num));
	}
	
	public static NumCommand create(Commands art, Num num) {
		return new NumCommand(art, num);
	}
	
}
