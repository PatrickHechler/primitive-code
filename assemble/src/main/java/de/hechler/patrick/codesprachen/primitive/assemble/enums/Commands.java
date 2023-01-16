package de.hechler.patrick.codesprachen.primitive.assemble.enums;

import static de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands.*;

import java.lang.reflect.Field;

import de.hechler.patrick.codesprachen.primitive.core.utils.PrimAsmCommands;

public enum Commands {
	
	//@formatter:off
	CMD_MVAD(MVAD, 3, 1),
	
	;//@formatter:on
	
	public final int num;
	public final int params;
	public final int noconstParams;
	
	private Commands(int num, int params, int nokonstParams) {
		this.num = num;
		this.params = params;
		this.noconstParams = nokonstParams;
	}
	
	@Override
	public String toString() {
		return name().substring(4);
	}
	
	static {
		Field[] fields = PrimAsmCommands.class.getFields();
		if (values().length != fields.length) {
			throw new AssertionError("I do not have the same amount of commands (my-count=" + values().length + " should-be=" + fields.length);
		}
		for (Field field : fields) {
			Commands val = valueOf("CMD_" + field.getName());
			try {
				if (val.num != field.getInt(null)) {
					throw new AssertionError("I have an illegal value: command: " + field.getName() + " my-num=" + Integer.toHexString(val.num) + " other-num="
							+ Integer.toHexString(field.getInt(null)));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new InternalError(e);
			}
		}
	}
	
}
