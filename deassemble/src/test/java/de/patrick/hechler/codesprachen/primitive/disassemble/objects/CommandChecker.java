package de.patrick.hechler.codesprachen.primitive.disassemble.objects;

import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;
import de.hechler.patrick.codesprachen.primitive.disassemble.interfaces.LabelNameGenerator;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Command;
import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.anotations.End;
import de.hechler.patrick.zeugs.check.anotations.Start;

@CheckClass
public class CommandChecker extends Checker {
	
	private LabelNameGenerator lng;
	private Command cmd;
	
	
	
	@Start
	private void init() {
		lng = LabelNameGenerator.SIMPLE_GEN;
	}
	
	@End
	private void finish() {
		cmd = null;
		lng = null;
	}
	
	
	
	@Check
	private void checkTwoParam() throws NoCommandException {
	}
	
	@Check
	private void checkOneParam() throws NoCommandException {
	}
	
	@Check
	private void checkOtherCmds() {
	}
	
}
