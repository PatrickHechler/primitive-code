package de.hechler.patrick.codesprachen.primitive.runtime.enums;


public enum DebugState {
	
	/**
	 * the PVM is currently executing code until a breakpoint triggers or it is told to stop executing code
	 */
	running,
	
	/**
	 * the PVM is currently executing code until a predefined condition triggers (like the first command has been executed), a breakpoint triggers or it is told to stop executing code
	 */
	stepping,
	
	/**
	 * the PVM is not executing code, the PVM waits for further commands
	 */
	waiting,
	
}
