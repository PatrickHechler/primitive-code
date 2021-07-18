package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Num;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;

public class RegRegCommand extends Command {
	
	private RegRegCommand(Commands art, Num n1, Num n2) {
		super(art, Wert.createNumber(n1), Wert.createNumber(n2));
	}
	
	public static RegRegCommand create(Commands art, Num n1, Num n2) {
		n1.checkMDB1();
		n2.checkMDB1();
		return new RegRegCommand(art, n1, n2);
	}
	
}
