package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;
import de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num;

public class NumNumCommand extends Command {
	
	private NumNumCommand(Commands art, Num n1, Num n2) {
		super(art, Wert.createNumber(n1.num, n1.deep), Wert.createNumber(n2.num, n2.deep));
	}
	
	public static NumNumCommand create(Commands art, Num n1, Num n2) {
		return new NumNumCommand(art, n1, n2);
	}
	
}
