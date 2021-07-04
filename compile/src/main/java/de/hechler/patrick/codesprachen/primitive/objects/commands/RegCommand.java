package de.hechler.patrick.codesprachen.primitive.objects.commands;

import de.hechler.patrick.codesprachen.primitive.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.objects.Num;
import de.hechler.patrick.codesprachen.primitive.objects.Wert;
import de.hechler.patrick.codesprachen.primitive.objects.params.DeepNum;

public class RegCommand extends Command {
	
	private RegCommand(Commands art, DeepNum num) {
		super(art, Wert.createNumber(num.num, num.numDeep));
	}
	
	public static RegCommand create(Commands art, Num num) {
		return new RegCommand(art, DeepNum.create(num.num, num.numDeep));
	}
	
}
