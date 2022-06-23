package de.hechler.patrick.codesprachen.primitive.disassemble.interfaces;

public interface LabelNameGenerator {
	
	LabelNameGenerator SIMPLE_GEN = t -> ("L_" + Long.toHexString(t));
	
	String CHECK_REGEX = "[a-zA-Z0-9_]+";
	
	/**
	 * the {@link LabelNameGenerator} generates the name for labels.<br>
	 * the returned value can not be <code>null</code>.<br>
	 * the returned value has to be a {@link #checkName(String) valid} label-name (without the '@' at
	 * the begin).<br>
	 * if called twice with the same arguments the generator has to return the same value.<br>
	 * if called with the different arguments the generator has to return a different value.
	 * 
	 * @param targetPos
	 *            the position of the target
	 * @return the name of the label
	 */
	String generateName(long targetPos);
	
	/**
	 * a valid name is not empty ({@code length > 0}) and contains only the chars <code>a-z</code>,
	 * <code>A-Z</code>, <code>0-9</code>, <code>'-'</code> and <code>'_'</code>
	 * 
	 * @param check
	 */
	static void checkName(String check) {
		if ( !check.matches(CHECK_REGEX)) {
			throw new IllegalStateException("this name does not match to the ruleRegex: name='" + check + "' regex='" + CHECK_REGEX + "'");
		}
	}
	
}
