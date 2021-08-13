package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.io.IOException;
import java.nio.file.Paths;

public class PrimitiveVirtualMashine {
	
	static {
		System.load(Paths.get("./primitive-runtime.dll").toAbsolutePath().toString());
	}
	
	public static void main(String[] args) {
		PrimitiveVirtualMashine pvm = new PrimitiveVirtualMashine();
		System.out.println("[J-LOG]: values=" + pvm.values);
		System.out.println("[J-LOG]: >> openfile(\"dummy\")");
		try {
			System.out.println("[J-LOG]: " + pvm.openfile("dummy") + " <- openfile(\"dummy\") <<");
		} catch (IOException e) {
			System.err.println("[J-ERR]: catched native error in openfile(\"dummy\"):");
			e.printStackTrace();
			System.out.println("[J-LOG]: " + e.getClass().getName() + " throwed by openfile(\"dummy\") <<");
		}
		
		System.out.println("[J-LOG]: >> malloc(10L)");
		long mem = pvm.malloc(10L);
		System.out.println("[J-LOG]: " + mem + " <- malloc(10L) <<");
		
		System.out.println("[J-LOG]: >> realloc(" + mem + "L, 11L)");
		mem = pvm.realloc(mem, 11L);
		System.out.println("[J-LOG]: " + mem + " <- malloc(11L) <<");
		
		System.out.println("[J-LOG]: >> realloc(" + mem + "L, 2L)");
		pvm.realloc(mem, 2L);
		System.out.println("[J-LOG]: " + mem + " <- realloc(2L) <<");
		
		System.out.println("[J-LOG]: >> malloc(2L)");
		long mem0 = pvm.malloc(2L);
		System.out.println("[J-LOG]: " + mem0 + " <- malloc(2L) <<");
		
		System.out.println("[J-LOG]: >> free(" + mem + ")");
		pvm.free(mem);
		System.out.println("[J-LOG]: free(" + mem + ") <<");
		
		System.out.println("[J-LOG]: >> free(" + mem0 + ")");
		pvm.free(mem0);
		System.out.println("[J-LOG]: free(" + mem0 + ") <<");
	}
	
	/**
	 * allocates the memory needed for the primitive virtual machine
	 * 
	 * @return a Pointer
	 * @throws OutOfMemoryError
	 *             if there is not enough memory
	 */
	public static native long create() throws OutOfMemoryError;
	
	/**
	 * this is for the native code.
	 */
	private final long values;
	
	public PrimitiveVirtualMashine() throws OutOfMemoryError {
		values = create();
		System.out.println("[J-LOG]: values=" + values);
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
	 *             if there is not enough memory for the file to be loaded before execution
	 * @throws IOException
	 *             if an IO error occurs during the load process
	 */
	public long execute(String file) throws OutOfMemoryError, IOException {
		long f = openfile(file);
		long len = filelen(f);
		long pntr = malloc(len);
		setfilepos(pntr, 0);
		long read = readfile(f, len, pntr);
		if (read != len) {
			throw new IOException("did not read the entire file: len=" + len + " read=" + read);
		}
		closefile(f);
		setInstructionPointer(pntr);
		long ret = execute();
		free(pntr);
		return ret;
	}
	
	/**
	 * this method loads the array to a memory block and execute then the progress
	 * 
	 * 
	 * 
	 * @param commands the commands to execute
	 * @return the return value of the progress
	 * @throws OutOfMemoryError if there is not enough memory to copy the array
	 */
	public long execute(long[] commands) throws OutOfMemoryError {
		long pntr = malloc(commands.length);
		for(int i = 0; i < commands.length; i ++) {
			System.out.println("[J-LOG]: set((pntr{"+pntr+"} + i{"+i+"}){"+(pntr + i)+"}, commands[i{"+i+"}]{"+commands[i]+"}");
			set(pntr + i, commands[i]);
		}
		setInstructionPointer(pntr);
		long ret = execute();
		free(pntr);
		return ret;
	}
	
	
	/**
	 * opens a stream to the file {@code file} and returns the FILE-Pointer.
	 * 
	 * @param filePNTR
	 *            the full name of the file
	 * @return the FILE-Pointer
	 * @throws IOException
	 *             if an error occurs during the opening of the file
	 */
	public native long openfile(String file) throws IOException;
	
	/**
	 * sets the {@link #filepos(long) filepos} to the end of the file so {@link #filepos(long)} will return the length of the file and returns the length
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 * @return the pos of the file, which is it's length
	 * @throws IOException
	 *             if an error occurs during the operation
	 */
	public native long filelen(long filePNTR) throws IOException;
	
	/**
	 * returns the position of the file.
	 * 
	 * the file-position will be 64-bit based, so the file has read already filepos 64-bit units.
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 * @return the position of the file
	 * @throws IOException
	 *             if an error occurs during the operation
	 */
	public native long filepos(long filePNTR) throws IOException;
	
	/**
	 * sets the position of the file.
	 * 
	 * the position is 64-bit based, so it is compatible to {@link #filepos(long)}
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 * @param pos
	 *            the new position of the file
	 * @throws IOException
	 *             if an error occurs during the operation
	 */
	public native void setfilepos(long filePNTR, long pos) throws IOException;
	
	/**
	 * reads {@code len} 64-bit units from the current {@link #filepos(long) filepos} to the {@code destenyPNTR}.
	 * 
	 * the {@code len} will be added to the {@link #filepos(long) filepos}.
	 * 
	 * it returns the number of 64-bit units read
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 * @param len
	 *            the number of 64-bit units to read
	 * @param destBufferPNTR
	 *            the Pointer to the destiny buffer
	 * @return the number of 64-bit units read
	 * @throws IOException
	 *             if an error occurs during the operation
	 */
	public native long readfile(long filePNTR, long len, long destBufferPNTR) throws IOException;
	
	/**
	 * closes the file.
	 * 
	 * all further use of the file will lead to an undefined behavior
	 * 
	 * @param filePNTR
	 *            the FILE-Pointer
	 * @throws IOException
	 *             if an error occures during the operation
	 */
	public native void closefile(long filePNTR) throws IOException;
	
	/**
	 * allocates {@code len} 64-bit units of memory and returns a Pointer to the allocated memory block
	 * 
	 * @param len
	 *            the number of 64-bit units which will be in the allocated block
	 * @return a Pointer to the allocated memory
	 * @throws OutOfMemoryError
	 *             if there is not enough memory left to allocate a block of {@code len} 64-bit units
	 */
	public native long malloc(long len) throws OutOfMemoryError;
	
	/**
	 * changes the length of a previously allocated block of memory
	 * 
	 * if there is not enough place the pointer may change its Position, but the values of the block will be copied to the new position if that happens.
	 * 
	 * @param pntr
	 *            the Pointer to the block of previously allocated memory
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
	 *            the new instruction-Pointer
	 */
	public native void setInstructionPointer(long ip);
	
	/**
	 * sets the stack-Pointer of this {@link PrimitiveVirtualMashine}
	 * 
	 * @param sp
	 *            the new stack pointer
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
	
	/**
	 * returns the value of the AX register
	 * 
	 * @return the value of the AX register
	 */
	public native long getAX();
	
	/**
	 * returns the value of the BX register
	 * 
	 * @return the value of the BX register
	 */
	public native long getBX();
	
	/**
	 * returns the value of the CX register
	 * 
	 * @return the value of the CX register
	 */
	public native long getCX();
	
	/**
	 * returns the value of the DX register
	 * 
	 * @return the value of the DX register
	 */
	public native long getDX();
	
	/**
	 * sets the value of the AX register
	 * 
	 * @param val
	 *            the new value of the AX register
	 */
	public native void setAX(long val);
	
	/**
	 * sets the value of the BX register
	 * 
	 * @param val
	 *            the new value of the BX register
	 */
	public native void setBX(long val);
	
	/**
	 * sets the value of the CX register
	 * 
	 * @param val
	 *            the new value of the CX register
	 */
	public native void setCX(long val);
	
	/**
	 * sets the value of the DX register
	 * 
	 * @param val
	 *            the new value of the DX register
	 */
	public native void setDX(long val);
	
	/**
	 * this method frees the place allocated by the {@link #values} (including the SRs ([A-D]X)), but NOT the place for the instructions and the stack!
	 */
	protected native void finalize();
	
}
