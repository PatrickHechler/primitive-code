package de.patrick.hechler.codesprachen.primitive.disassemble.objects;

import de.hechler.patrick.codesprachen.primitive.disassemble.enums.Commands;
import de.hechler.patrick.codesprachen.primitive.disassemble.exceptions.NoCommandException;
import de.hechler.patrick.codesprachen.primitive.disassemble.interfaces.LabelNameGenerator;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Command;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Param;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.Param.ParamBuilder;
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
		Param p;
		ParamBuilder pb = new ParamBuilder();
		pb.art = Param.ART_ANUM;
		pb.v1 = 0;
		p = pb.build();
		pb.art = Param.ART_ANUM_BNUM;
		pb.v1 = 7;
		pb.v2 = 1;
		cmd = new Command(Commands.CMD_CMP, p, pb.build());
		assertEquals("CMP 0, [7 + 1]", cmd.toString());
		pb = new ParamBuilder();
		pb.art = Param.ART_ASR;
		pb.v1 = Param.SR_DX;
		;
		p = pb.build();
		pb.art = Param.ART_ANUM_BREG;
		pb.v1 = 5;
		cmd = new Command(Commands.CMD_ADD, p, pb.build());
		assertEquals("ADD DX, [5]", cmd.toString());
		pb = new ParamBuilder();
		pb.art = Param.ART_ASR;
		pb.v1 = Param.SR_DX;
		;
		p = pb.build();
		pb.art = Param.ART_ANUM;
		pb.v1 = 0;
		cmd = new Command(Commands.CMD_ADDC, p, pb.build());
		assertEquals("ADDC DX, 0", cmd.toString());
	}
	
	@Check
	private void checkOneParam() throws NoCommandException {
		ParamBuilder pb = new ParamBuilder();
		pb.art = Param.ART_ANUM;
		pb.v1 = 0;
		cmd = new Command(Commands.CMD_INT, pb.build());
		assertEquals("INT 0", cmd.toString());
		pb = new ParamBuilder();
		pb.art = Param.ART_ASR;
		pb.v1 = 0;
		cmd = new Command(Commands.CMD_GET_IP, pb.build());
		assertEquals("GET_IP AX", cmd.toString());
	}
	
	@Check
	private void checkOtherCmds() {
		cmd = new Command(Commands.CMD_RET);
		assertEquals("RET", cmd.toString());
		cmd = new Command(Commands.CMD_CALL, 0x32, lng);
		assertEquals("CALL L-32", cmd.toString(0));
		cmd = new Command(Commands.CMD_JMP, 0x32, lng);
		assertEquals("JMP L-32", cmd.toString(0));
		cmd = new Command(Commands.CMD_JMPEQ, 0x32, lng);
		assertEquals("JMPEQ L-32", cmd.toString(0));
		cmd = new Command(Commands.CMD_JMPNE, 0x32, lng);
		assertEquals("JMPNE L-32", cmd.toString(0));
		cmd = new Command(Commands.CMD_JMPCS, 0x32, lng);
		assertEquals("JMPCS L-32", cmd.toString(0));
		cmd = new Command(Commands.CMD_JMPCC, 0x32, lng);
		assertEquals("JMPCC L-32", cmd.toString(0));
	}
	
}
