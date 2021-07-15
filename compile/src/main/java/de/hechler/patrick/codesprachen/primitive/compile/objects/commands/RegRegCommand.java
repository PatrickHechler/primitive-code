package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;
import de.hechler.patrick.codesprachen.primitive.compile.objects.num.DeepNum;
import de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num;

public class RegRegCommand extends Command {
	
	private RegRegCommand(Commands art, DeepNum n1, DeepNum n2) {
		super(art, Wert.createNumber(n1.num, n1.deep), Wert.createNumber(n2.num, n2.deep));
	}
	
	public static RegRegCommand create(Commands art, Num n1, Num n2) {
		n1.checkMDB1();
		n2.checkMDB1();
		if ( ! (n1 instanceof DeepNum)) {
			n1 = new DeepNum(n1.num, n1.deep);
		}
		if ( ! (n2 instanceof DeepNum)) {
			n2 = new DeepNum(n2.num, n2.deep);
		}
		return new RegRegCommand(art, (DeepNum) n1, (DeepNum) n2);
	}
	
}
