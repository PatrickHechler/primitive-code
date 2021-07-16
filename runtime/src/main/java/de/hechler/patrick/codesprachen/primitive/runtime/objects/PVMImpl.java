package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import javax.security.auth.Destroyable;

import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.PVM;

public class PVMImpl implements PVM, Destroyable {
	
	private native long construct(long regCount, long maxStackSize) throws OutOfMemoryError;
	
	/**
	 * only for the native code
	 */
	public final long values;
	
	/**
	 * creates a new PVM with the given number of registers and the given maximum stack size
	 * 
	 * @param regCount
	 *            the number of registers
	 * @param maxStackSize
	 *            the maximum stack size
	 * @throws OutOfMemoryError
	 *             if there is not enough memory for a PVM with the given regCount and maxStackSize. the space will be cleared, so
	 */
	public PVMImpl(long regCount, long maxStackSize) throws OutOfMemoryError {
		values = construct(regCount, maxStackSize);
	}
	
	
	
	@Override
	public native long get(long index) throws IndexOutOfBoundsException;
	
	@Override
	public native void set(long index, long val) throws IndexOutOfBoundsException;
	
	@Override
	public native void push(long val) throws IndexOutOfBoundsException;
	
	@Override
	public native long pop() throws IndexOutOfBoundsException;
	
	@Override
	public native long getRegCount();
	
	@Override
	public native long getStackSize();
	
	@Override
	public native long getStackMaxSize();
	
	@Override
	public native void run();
	
	@Override
	public native void destroy();
	
	@Override
	public native boolean isDestroyed();
	
	/**
	 * if overwritten it this method should be called and not {@link #destroy()}, because the {@link #finalize()} method frees all memory and not only the stack and the registers
	 */
	@Override
	protected native void finalize();
	
}

