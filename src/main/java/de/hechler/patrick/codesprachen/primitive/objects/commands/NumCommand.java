package de.hechler.patrick.codesprachen.primitive.objects.commands;

import de.hechler.patrick.codesprachen.primitive.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.objects.Num;
import de.hechler.patrick.codesprachen.primitive.objects.Wert;

public class NumCommand extends Command {
	
	public NumCommand(Commands art, Num num) {
		super(art, Wert.createNumber(num.num, num.numDeep));
	}
	
	public NumCommand create(Commands art, Num num) {
		return new NumCommand(art, num);
	}
	
}
