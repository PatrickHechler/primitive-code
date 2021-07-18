package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Num;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;

public class RegNumCommand extends Command {
	
	private RegNumCommand(Commands art, Num n1, Num n2) {
		super(art, Wert.createNumber(n1), Wert.createNumber(n2));
	}
	
	public static RegNumCommand create(Commands art, Num n1, Num n2) {
		n1.checkMDB1();
		n2.checkMDB0();
		return new RegNumCommand(art, n1, n2);
	}
	
}
