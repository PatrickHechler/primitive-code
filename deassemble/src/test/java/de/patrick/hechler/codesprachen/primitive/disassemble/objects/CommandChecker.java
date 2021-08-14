package de.patrick.hechler.codesprachen.primitive.disassemble.objects;

import java.util.HashMap;
import java.util.Map;

import de.hechler.patrick.zeugs.check.Checker;
import de.hechler.patrick.zeugs.check.anotations.Check;
import de.hechler.patrick.zeugs.check.anotations.CheckClass;
import de.hechler.patrick.zeugs.check.anotations.End;
import de.hechler.patrick.zeugs.check.anotations.Start;
import de.patrick.hechler.codesprachen.primitive.disassemble.enums.Commands;
import de.patrick.hechler.codesprachen.primitive.disassemble.exceptions.NoCommandException;
import de.patrick.hechler.codesprachen.primitive.disassemble.interfaces.LabelNameGenerator;
import de.patrick.hechler.codesprachen.primitive.disassemble.objects.Param.ParamBuilder;

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
		pb.v1 = Param.SR_DX;;
		p = pb.build();
		pb.art = Param.ART_ANUM_BREG;
		pb.v1 = 5;
		cmd = new Command(Commands.CMD_ADD, p, pb.build());
		assertEquals("ADD DX, [5]", cmd.toString());
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
		cmd = new Command(Commands.CMD_CALL, 50, lng);
		Map<Long,Integer> map = new HashMap<>();
		map.put((Long) 50L, (Integer) 4);
		assertEquals("CALL L-E", cmd.toString(null, map));
		cmd = new Command(Commands.CMD_JMP, 50, lng);
		assertEquals("JMP L-E", cmd.toString(null, map));
		cmd = new Command(Commands.CMD_JMPEQ, 50, lng);
		assertEquals("JMPEQ L-E", cmd.toString(null, map));
		cmd = new Command(Commands.CMD_JMPNE, 50, lng);
		assertEquals("JMPNE L-E", cmd.toString(null, map));
		cmd = new Command(Commands.CMD_JMPCS, 50, lng);
		assertEquals("JMPCS L-E", cmd.toString(null, map));
		cmd = new Command(Commands.CMD_JMPCC, 50, lng);
		assertEquals("JMPCC L-E", cmd.toString(null, map));
	}
	
}
