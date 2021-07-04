package de.hechler.patrick.codesprachen.primitive.objects.commands;

import de.hechler.patrick.codesprachen.primitive.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.objects.Wert;
import de.hechler.patrick.codesprachen.primitive.objects.params.DeepNum;

public class RegCommand extends Command {
	
	public RegCommand(Commands art, DeepNum num) {
		super(art, Wert.createNumber(num.num, num.numDeep));
	}
	
}
