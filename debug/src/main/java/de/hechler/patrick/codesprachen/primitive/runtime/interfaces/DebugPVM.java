package de.hechler.patrick.codesprachen.primitive.runtime.interfaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Collection;

import de.hechler.patrick.codesprachen.primitive.runtime.enums.DebugState;

public interface DebugPVM extends PVM {
	
	/**
	 * returns the current {@link DebugState} of the PVM
	 * 
	 * @return the current {@link DebugState} of the PVM
	 */
	DebugState state();
	
	/**
	 * execute the primitive virtual machine until a break point triggers
	 */
	@Override
	void run();
	
	/**
	 * executes a single command
	 */
	void step();
	
	/**
	 * executes a single commands, until a equal number of CALL/CALO/INT and RET/IRET has been executed.<br>
	 * or if the first command is a RET/IRET command.
	 */
	default void stepOver() {
		step(0L);
	}
	
	/**
	 * executes a single commands, until the number of executed CALL/CALO/INT is one lower then the number of executed RET/IRET.<br>
	 */
	default void stepOut() {
		step(1L);
	}
	
	void step(long deep);
	
	/**
	 * stops the execution of the PVM
	 */
	void stop();
	
	/**
	 * terminates the PVM
	 */
	void exit();
	
	/**
	 * returns the {@link BreakHandle} for addresses
	 * 
	 * @return the {@link BreakHandle} for addresses
	 */
	BreakHandle posBreakHandle();
	
	/**
	 * returns the {@link BreakHandle} for default interrupts<br>
	 * this {@link BreakHandle} will ignore breakpoints for the {@link #allIntBreakHandle()}, but default interrupts will also break on those breakpoints
	 * 
	 * @return the {@link BreakHandle} for default interrupts
	 */
	BreakHandle defIntBreakHandle();
	
	/**
	 * returns a {@link BreakHandle} for all default interrupts with read only access
	 * <p>
	 * the returned {@link BreakHandle} will contain all interrupt breakpoints.
	 * <p>
	 * operations which would modify the {@link BreakHandle} will fail
	 */
	default BreakHandle intBreakRead() {
		return new BreakHandle() {
			
			private final BreakHandle def = defIntBreakHandle();
			private final BreakHandle all = allIntBreakHandle();
			
			@Override
			@SuppressWarnings("unchecked")
			public <T> T[] toArray(T[] a) {
				int s = size();
				if (a.length < s) {
					a = (T[]) Array.newInstance(a.getClass().getComponentType(), s);
				} else if (a.length > s) {
					a[s] = null;
				}
				for (Long l : this) {
					a[ -- s] = (T) l;
				}
				return a;
			}
			
			@Override
			public Object[] toArray() {
				return toArray(new Object[0]);
			}
			
			@Override
			public boolean retainAll(Collection <?> c) {
				throw new UnsupportedOperationException("only support for read operations");
			}
			
			@Override
			public boolean removeAll(Collection <?> c) {
				throw new UnsupportedOperationException("only support for read operations");
			}
			
			@Override
			public boolean isEmpty() {
				return all.isEmpty() && def.isEmpty();
			}
			
			@Override
			public boolean containsAll(Collection <?> c) {
				for (Object object : c) {
					if (object == null) return false;
					else if ( ! (object instanceof Long)) return false;
					long v = (long) object;
					if ( !all.contains(v) && def.contains(v)) return false;
				}
				return true;
			}
			
			@Override
			public boolean addAll(Collection <? extends Long> c) {
				throw new UnsupportedOperationException("only support for read operations");
			}
			
			@Override
			public boolean remove(long formerStop) {
				throw new UnsupportedOperationException("only support for read operations");
			}
			
			@Override
			public BreakIter iter() {
				return null;
			}
			
			@Override
			public int size() {
				int s = all.size();
				for (BreakIter iter = def.iter(); iter.hasNext();) {
					if (all.contains(iter.nextBreak())) continue;
					s ++ ;
				}
				return s;
			}
			
			@Override
			public boolean contains(long stop) {
				return all.contains(stop) || def.contains(stop);
			}
			
			@Override
			public void clear() {
				throw new UnsupportedOperationException("only support for read operations");
			}
			
			@Override
			public boolean add(long newStop) {
				throw new UnsupportedOperationException("only support for read operations");
			}
			
		};
	}
	
	/**
	 * returns the {@link BreakHandle} for all interrupts
	 * <p>
	 * the returned {@link BreakHandle} will ignore breakpoints for only defaultInterrupts.
	 * <p>
	 * the PVM will always stop when a interrupt is called (explicitly or because of an error) and the given interrupt-number is saved in the {@link BreakHandle}.<br>
	 * it is ignored if the interrupt is a default interrupt or an overwritten interrupt.
	 * 
	 * @return the {@link BreakHandle} for all interrupts
	 */
	BreakHandle allIntBreakHandle();
	
	/**
	 * overwrites the data of the PVM registers
	 * <p>
	 * the array {@code buf} has to be of length 256
	 * 
	 * @param buf
	 *            the new PVM register data
	 * @throws if
	 *            {@code buf} has a different length (not 256)
	 */
	void putPVM(byte[] buf) throws IllegalArgumentException, IllegalStateException;
	
	/**
	 * reads the data of the PVM registers to the array {@code buf}
	 * <p>
	 * the array {@code buf} has to be of length 256
	 * 
	 * @param buf
	 *            too be filled with the PVM register data
	 * @throws if
	 *            {@code buf} has a different length (not 256)
	 */
	void getPVM(byte[] buf) throws IllegalArgumentException;
	
	/**
	 * reads a block of memory.<br>
	 * the memory to be read has previously to be allocated with {@link #mallocMemory(long)}, {@link #reallocMemory(long, long)} or by the program with the <code>INT_MEMORY_ALLOC</code> or
	 * <code>INT_MEMORY_REALLOC</code>
	 * 
	 * @param addr
	 *             the start address of the memory block
	 * @param buf
	 *             the buffer to be filled with the data of the memory block
	 * @param len
	 *             the number of bytes to be read
	 * @throws IllegalArgumentException
	 *                                  if access to unallocated memory is requested
	 */
	void getMem(long addr, byte[] buf, int len) throws IllegalArgumentException;
	
	/**
	 * reads a block of memory.<br>
	 * the memory to be read has previously to be allocated with {@link #mallocMemory(long)}, {@link #reallocMemory(long, long)} or by the program with the <code>INT_MEMORY_ALLOC</code> or
	 * <code>INT_MEMORY_REALLOC</code>
	 * 
	 * @param addr
	 *             the start address of the memory block
	 * @param buf
	 *             the buffer to be filled with the data of the memory block its length must be greater/equal to <code>boff + len</code>
	 * @param boff
	 *             the offset in the byte array must not be negative (greater/equal zero)
	 * @param len
	 *             the number of bytes to be read must be positive (greater zero)
	 * @throws IllegalArgumentException
	 *                                  if access to unallocated memory is requested or the arguments are invalid
	 */
	void getMem(long addr, byte[] buf, int boff, int len) throws IllegalArgumentException;
	
	/**
	 * sets a block of memory.<br>
	 * the memory to be set has previously to be allocated with {@link #mallocMemory(long)}, {@link #reallocMemory(long, long)} or by the program with the <code>INT_MEMORY_ALLOC</code> or
	 * <code>INT_MEMORY_REALLOC</code>
	 * 
	 * @param addr
	 *             the start address of the memory block
	 * @param buf
	 *             the buffer with the new data of the memory block
	 * @param len
	 *             the number of bytes to be write
	 * @throws IllegalArgumentException
	 *                                  if access to unallocated memory is requested
	 */
	void setMem(long addr, byte[] buf, int len) throws IllegalArgumentException;
	
	/**
	 * allocates a memory block.<br>
	 * this function is compatible with the default memory interrupts usable to the program ({@code INT_MEMORY_ALLOC}, {@code INT_MEMORY_REALLOC} and {@code INT_MEMORY_REALLOC})
	 * 
	 * @param len
	 *            the length of the memory block in bytes
	 * @return the start address of the memory block
	 * @throws OutOfMemoryError
	 *                          if the PVM could not allocate enough memory
	 */
	long mallocMemory(long len) throws OutOfMemoryError;
	
	/**
	 * reallocates a block of memory.<br>
	 * this function is compatible with the default memory interrupts usable to the program ({@code INT_MEMORY_ALLOC}, {@code INT_MEMORY_REALLOC} and {@code INT_MEMORY_REALLOC})
	 * 
	 * @param addr
	 *             the start address of the memory block
	 * @param len
	 *             the new length of the memory block
	 * @return the new start address of the memory block
	 * @throws OutOfMemoryError
	 *                                  if the PVM could not resize the memory block
	 * @throws IllegalArgumentException
	 *                                  if <code>addr</code> does not point to the start of a allocated memory block
	 */
	long reallocMemory(long addr, long len) throws OutOfMemoryError, IllegalArgumentException;
	
	/**
	 * frees a allocated memory block.<br>
	 * this function is compatible with the default memory interrupts usable to the program ({@code INT_MEMORY_ALLOC}, {@code INT_MEMORY_REALLOC} and {@code INT_MEMORY_REALLOC})
	 * 
	 * @param addr
	 *             the start address of the memory block
	 * @throws IllegalArgumentException
	 *                                  if <code>addr</code> does not point to the start of a allocated memory block
	 */
	void freeMemory(long addr) throws IllegalArgumentException;
	
	/**
	 * opens a new output stream which writes to the stdin stream of the program<br>
	 * it is not specified, every call to {@link #stdin()} returns the same or a new {@link OutputStream}.
	 * 
	 * @return a stdin linked stream
	 */
	OutputStream stdin();
	
	/**
	 * opens a new input stream which reads the stdout of the program
	 * 
	 * @return a stdout linked stream
	 */
	InputStream stdout();
	
	/**
	 * opens a new input stream which reads the stdlog of the program
	 * 
	 * @return a stdlog linked stream
	 * @throws IOException
	 */
	InputStream stdlog();
	
	/**
	 * adds a stdout listen stream.<br>
	 * everything which will be written to the stdout of the program will also be written to the given stream
	 * 
	 * @param listen
	 *               the stdout listen stream
	 */
	void stdout(OutputStream listen);
	
	/**
	 * adds a stdlog listen stream.<br>
	 * everything which will be written to the stdlog of the program will also be written to the given stream
	 * 
	 * @param listen
	 *               the stdlog listen stream
	 */
	void stdlog(OutputStream listen);
	
	/**
	 * checks if the given address {@code addr} is in a allocated memory range and {@code addr + len - 1} is also inside the same allocated memory block<br>
	 * if at least one address from {@code addr} to {@code addr + len - 1} is invalid an {@link IllegalArgumentException} will be thrown
	 * <p>
	 * if {@code len} is negative or zero also an {@link IllegalArgumentException} will be thrown
	 * 
	 * @param addr
	 *             the address
	 * @param len
	 *             the length
	 * @throws IllegalArgumentException
	 *                                  if the memory is not completely valid
	 */
	void memcheck(long addr, long len) throws IllegalArgumentException;
	
}
