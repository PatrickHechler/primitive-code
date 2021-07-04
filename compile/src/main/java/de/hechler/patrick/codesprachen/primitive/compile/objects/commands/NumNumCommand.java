package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Num;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;

public class NumNumCommand extends Command {
	
	private NumNumCommand(Commands art, Num n1, Num n2) {
		super(art, Wert.createNumber(n1.num, n1.numDeep), Wert.createNumber(n2.num, n2.numDeep));
	}
	
	public static NumNumCommand create(Commands art, Num n1, Num n2) {
		return new NumNumCommand(art, n1, n2);
	}
	
}
