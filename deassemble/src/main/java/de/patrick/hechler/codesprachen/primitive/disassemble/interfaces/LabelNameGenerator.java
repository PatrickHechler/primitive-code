package de.patrick.hechler.codesprachen.primitive.disassemble.interfaces;

import java.util.List;

import de.patrick.hechler.codesprachen.primitive.disassemble.objects.Command;

public interface LabelNameGenerator {
	
	/**
	 * the {@link LabelNameGenerator} generates the name for labels.<br>
	 * 
	 * the returned value can not be <code>null</code>.<br>
	 * 
	 * the returned value has to be a {@link #checkName(String) valid} label-name (without the '@' at the begin.<br>
	 * 
	 * if called twice with the same arguments the generator has to return the same value.<br>
	 * 
	 * if called with the different arguments the generator has to return a different value.
	 * 
	 * @param targetPos
	 *            the position of the target
	 * @param commands
	 *            all commands in the order of the program-code
	 * @param destenyCommandIndex
	 *            the index of the target-command in the {@code commands} {@link List}
	 * @return the name of the label
	 */
	String generateName(long targetPos, List <Command> commands, int destenyCommandIndex);
	
	/**
	 * a valid name is not empty ({@code length > 0}) 
	 * and contains only 
	 * @param check
	 */
	static void checkName(String check) {
		if (check.length() < 1) {
			throw new IllegalStateException("empty names are invalid and this name is empty!");
		}
		for (char c : check.toCharArray()) {
			if (c >= 'A' && c <= 'Z') {
				continue;
			}
			if (c >= 'a' && c <= 'z') {
				continue;
			}
			if (c >= '0' && c <= '9') {
				continue;
			}
			if (c == '-') {
				continue;
			}
			if (c == '_') {
				continue;
			}
			throw new IllegalStateException("this name contsins a invalid character: '" + c + "' fullName='" + check + "'");
		}
	}
	
}
