package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Num;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;

public class RegCommand extends Command {
	
	private RegCommand(Commands art, Num num) {
		super(art, Wert.createNumber(num));
	}
	
	public static RegCommand create(Commands art, Num num) {
		num.checkMDB1();
		return new RegCommand(art, num);
	}
	
}
