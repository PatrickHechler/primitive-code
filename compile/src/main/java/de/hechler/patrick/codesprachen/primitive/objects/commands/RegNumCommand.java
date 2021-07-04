package de.hechler.patrick.codesprachen.primitive.objects.commands;

import de.hechler.patrick.codesprachen.primitive.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.objects.Num;
import de.hechler.patrick.codesprachen.primitive.objects.Wert;
import de.hechler.patrick.codesprachen.primitive.objects.params.DeepNum;

public class RegNumCommand extends Command {

	private RegNumCommand(Commands art, DeepNum n1, Num n2) {
		super(art, Wert.createNumber(n1.num, n1.numDeep), Wert.createNumber(n2.num, n2.numDeep));
	}

	public static RegNumCommand create(Commands art, Num n1, Num n2) {
		return new RegNumCommand(art, DeepNum.create(n1.num, n1.numDeep), DeepNum.create(n2.num, n2.numDeep));
	}
	
}
