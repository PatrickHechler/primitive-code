package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;
import de.hechler.patrick.codesprachen.primitive.compile.objects.num.DeepNum;
import de.hechler.patrick.codesprachen.primitive.compile.objects.num.Num;

public class RegCommand extends Command {
	
	private RegCommand(Commands art, DeepNum num) {
		super(art, Wert.createNumber(num.num, num.deep));
	}
	
	public static RegCommand create(Commands art, Num num) {
		num.checkMDB1();
		if (num instanceof DeepNum) {
			return new RegCommand(art, (DeepNum) num);
		}else {
			return new RegCommand(art, new DeepNum(num.num,num.deep));
		}
	}
	
}
