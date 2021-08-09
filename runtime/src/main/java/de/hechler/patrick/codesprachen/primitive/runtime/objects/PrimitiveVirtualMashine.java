package de.hechler.patrick.codesprachen.primitive.runtime.objects;

public class PrimitiveVirtualMashine {
	
	public native long create() throws OutOfMemoryError;
	
	private final long values;
	
	public PrimitiveVirtualMashine() throws OutOfMemoryError {
		values = create();
	}
	
	/**
	 * This method will load and {@link #execute() execute} the given file.
	 * 
	 * After the {@link #execute() execution} the {@link #malloc(long) allocated memory} for the file will be set {@link #free(long) free}.
	 * 
	 * @param file
	 *            the full name of the file to be executed
	 * @return the exit code of the runed progress
	 * @throws OutOfMemoryError
	 *             if there is not enugh memory for the file to be loaded before execution
	 */
	public long execute(String file) throws OutOfMemoryError {
		long f = openfile(file);
		long len = filelen(f);
		long pntr = malloc(len);
		readfile(f, len, pntr);
		closefile(f);
		setInstructionPointer(pntr);
		long ret = execute();
		free(pntr);
		return ret;
	}
	
	/**
	 * opens a stream to the file {@code file} and returns the FILE-Pointer.
	 * 
	 * @param filePNTR
	 *            thr full name of the file
	 * @return the FILE-Pointer
	 */
	public native long openfile(String file);
	
	/**
	 * returns the length of the file.
	 * 
	 * the filelen will be a 64-bit based len, so the file contains filen 64-bit Bits.
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 * @return the length of the file
	 */
	public native long filelen(long filePNTR);
	
	/**
	 * returns the position of the file.
	 * 
	 * the file-position will be 64-bit based, so the file has read already filepos 64-bit units.
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 * @return the position of the file
	 */
	public native long filepos(long filePNTR);
	
	/**
	 * sets the position of the file.
	 * 
	 * the position is 64-bit based, so it is compatible to {@link #filepos(long)}
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 * @param pos
	 *            the new position of the file
	 */
	public native void setfilepos(long filePNTR, long pos);
	
	/**
	 * reads {@code len} 64-bit units from the current {@link #filepos(long) filepos} to the {@code destenyPNTR}.
	 * 
	 * the {@code len} will be added to the {@link #filepos(long) filepos}.
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 * @param len
	 *            the number of 64-bit units to read
	 * @param destBufferPNTR
	 *            the Pointer to the desteny buffer
	 */
	public native void readfile(long filePNTR, long len, long destBufferPNTR);
	
	/**
	 * closes the file.
	 * 
	 * all further use of the file will lead to an undefined behavior
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 */
	public native void closefile(long filePNTR);
	
	/**
	 * allocates {@code len} 64-bit units of memory and returns a Pointer to the allocated memory block
	 * 
	 * @param len
	 *            the number of 64-bit units which will be in the allocated block
	 * @return a Pointer to the allocated memory
	 * @throws OutOfMemoryError
	 *             if there is not enugh memory left to allocate a block of {@code len} 64-bit units
	 */
	public native long malloc(long len) throws OutOfMemoryError;
	
	/**
	 * changes the length of a previusly allocated block of memory
	 * 
	 * if there is not enugh place the pointer may change its Position, but the values of the block will be copied to the new position if that happanes.
	 * 
	 * @param pntr
	 *            the Pointer to te block of previusly allocated memory
	 * @param len
	 *            the new length of the block
	 * @return a Pointer to the new block of allocated memory
	 * @throws OutOfMemoryError
	 */
	public native long realloc(long pntr, long len) throws OutOfMemoryError;
	
	/**
	 * frees a block of allocated memory
	 * 
	 * @param pntr
	 *            the Pointer to the allocated memory to be freed
	 */
	public native void free(long pntr);
	
	/**
	 * runs the {@link PrimitiveVirtualMashine} and returns the exit code
	 * 
	 * @return the exit code
	 */
	public native long execute();
	
	/**
	 * sets the instruction-Pointer of this {@link PrimitiveVirtualMashine}
	 * 
	 * @param ip
	 *            the new instruction-Poitner
	 */
	public native void setInstructionPointer(long ip);
	
	/**
	 * sets the stack-Pointer of this {@link PrimitiveVirtualMashine}
	 * 
	 * @param sp
	 *            the new stacl pointer
	 */
	public native void setStackPointer(long sp);
	
	/**
	 * pushes the {@code value} to the stack
	 * 
	 * @param value
	 *            the value to be pushed
	 */
	public native void push(long value);
	
	/**
	 * pops a value from the stack and returns it
	 * 
	 * @return the poped value
	 */
	public native long pop();
	
	/**
	 * returns the value of the Pointer {@code PNTR}.
	 * 
	 * if the Pointer {@code PNTR} points to a not allocated value the behavior is undefined.
	 * 
	 * @param PNTR
	 *            the Pointer of the value to be returned
	 * @return the value of the Pointer {@code PNTR}
	 */
	public native long get(long PNTR);
	
	/**
	 * sets the value of the Pointer {@code PNTR}.
	 * 
	 * if the Pointer {@code PNTR} points to a not allocated value the behavior is undefined.
	 * 
	 * @param PNTR
	 *            the Pointer of the value to be set
	 * @param value
	 *            the value to be set to the Pointer {@code PNTR}
	 */
	public native void set(long PNTR, long value);
	
}
