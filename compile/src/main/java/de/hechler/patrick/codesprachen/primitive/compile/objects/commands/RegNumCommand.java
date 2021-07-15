package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;
import de.hechler.patrick.codesprachen.primitive.compile.objects.num.DeepNum;
import de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num;

public class RegNumCommand extends Command {
	
	private RegNumCommand(Commands art, DeepNum n1, Num n2) {
		super(art, Wert.createNumber(n1.num, n1.deep), Wert.createNumber(n2.num, n2.deep));
	}
	
	public static RegNumCommand create(Commands art, Num n1, Num n2) {
		n1.checkMDB1();
		n2.checkMDB0();
		if (n1 instanceof DeepNum) {
			return new RegNumCommand(art, (DeepNum) n1, n2);
		} else {
			return new RegNumCommand(art, new DeepNum(n1.num, n1.deep), n2);
		}
	}
	
}
