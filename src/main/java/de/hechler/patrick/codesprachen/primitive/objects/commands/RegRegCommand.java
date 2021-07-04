package de.hechler.patrick.codesprachen.primitive.objects.commands;

import de.hechler.patrick.codesprachen.primitive.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.objects.Wert;
import de.hechler.patrick.codesprachen.primitive.objects.params.DeepNum;

public class RegRegCommand extends Command {
	
	public RegRegCommand(Commands art, DeepNum n1, DeepNum n2) {
		super(art, Wert.createNumber(n1.num, n1.numDeep), Wert.createNumber(n2.num, n2.numDeep));
	}
	
}
