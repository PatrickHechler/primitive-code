package de.hechler.patrick.codesprachen.primitive.compile.objects.commands;

import de.hechler.patrick.codesprachen.primitive.compile.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Num;
import de.hechler.patrick.codesprachen.primitive.compile.objects.Wert;
import de.hechler.patrick.codesprachen.primitive.compile.objects.params.DeepNum;

public class RegCommand extends Command {
	
	private RegCommand(Commands art, DeepNum num) {
		super(art, Wert.createNumber(num.num, num.numDeep));
	}
	
	public static RegCommand create(Commands art, Num num) {
		return new RegCommand(art, DeepNum.create(num.num, num.numDeep));
	}
	
}
