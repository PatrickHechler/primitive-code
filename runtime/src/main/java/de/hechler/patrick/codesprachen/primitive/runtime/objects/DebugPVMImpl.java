package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import javax.security.auth.Destroyable;

import de.hechler.patrick.codesprachen.primitive.runtime.interfaces.DebugPVM;

public class DebugPVMImpl implements DebugPVM, Destroyable {
	
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
	public DebugPVMImpl(long regCount, long maxStackSize) throws OutOfMemoryError {
		values = construct(regCount, maxStackSize);
	}
	
	
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native long get(long index) throws IndexOutOfBoundsException, IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native void set(long index, long val) throws IndexOutOfBoundsException, IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native void push(long val) throws IndexOutOfBoundsException, IllegalStateException;
	
	@Override
	public native long pop() throws IndexOutOfBoundsException, IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native long getRegCount() throws IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native long getStackSize() throws IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native long getStackMaxSize() throws IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native void run() throws IllegalStateException;
	
	@Override
	public native void setStackMaxSize(long maxSize) throws OutOfMemoryError, IndexOutOfBoundsException, IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native void setRegCount(long count) throws OutOfMemoryError, IndexOutOfBoundsException, IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native void step() throws IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native void stepOut() throws IllegalStateException;
	
	/**
	 * @throws IllegalStateException if this {@link DebugPVM} is destroyed
	 */
	@Override
	public native void jump(long target) throws IndexOutOfBoundsException, IllegalStateException;
	
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
