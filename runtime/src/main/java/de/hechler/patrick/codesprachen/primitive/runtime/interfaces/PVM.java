package de.hechler.patrick.codesprachen.primitive.runtime.interfaces;


public interface PVM {
	
	/**
	 * returns the value of the register at the given index
	 * 
	 * @param index
	 *            the index of the register
	 * @return the value of the register at the given index
	 * @throws IndexOutOfBoundsException if the index is out of the provided range of registers
	 */
	long get(long index) throws IndexOutOfBoundsException;
	
	/**
	 * sets the value of the register with the given index to the given value
	 * 
	 * @param index
	 *            the index of the register
	 * @param val
	 *            the new value of the register
	 * @throws IndexOutOfBoundsException if the index is out of the provided range of registers
	 */
	void set(long index, long val) throws IndexOutOfBoundsException;
	
	/**
	 * pushes the given value to the stack
	 * 
	 * @param val
	 *            pushes it to the stack
	 * @throws IndexOutOfBoundsException if the stack is already at the maximum size
	 */
	void push(long val) throws IndexOutOfBoundsException;
	
	/**
	 * pops the highest value from the stack and returns it
	 * 
	 * @return the former highest value of the stack
	 * @throws IndexOutOfBoundsException if the stack is already empty
	 */
	long pop() throws IndexOutOfBoundsException;
	
	void jump(long dest) throws IndexOutOfBoundsException;
	
	/**
	 * returns the number of registers
	 * 
	 * @return the number of registers
	 */
	long getRegCount();
	
	/**
	 * returns the actual size of the stack
	 * 
	 * @return the actual size of the stack
	 */
	long getStackSize();
	
	long getInstructionPointer();
	
	long getStackPointer();
	
	void load(String file);
	
	long execute();

	long getInstructionCount();
	
	void setInstructionCount(long instcount);
	
}
