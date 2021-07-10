package de.hechler.patrick.codesprachen.primitive.runtime;

import java.io.IOException;

public class PrimitiveRuntime {
	
	static  {
		System.loadLibrary("primitive-runtime-lib");
	}
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("start0");
		new PrimitiveRuntime(2, 4).finalize();
		System.out.println("start");
		PrimitiveRuntime pr = new PrimitiveRuntime(1, 0);
		System.out.println("PrimitiveRuntime: " + pr);
		System.out.println("vals: " + pr.values);
		pr.push(4l);
		System.out.println("pushed 42");
		System.out.println("pop: " + pr.pop());
		System.out.println("vals[0]: " + pr.getRegister(0l));
		pr.setRegister(0l,1l);
		System.out.println("vals[0]: " + pr.getRegister(0l));
		System.out.println("end");
	 }
	
	private static native long create(long valuesLen, long stackMaxSize);
	
	private final long values;
	
	public PrimitiveRuntime(long valuesLen, long stackMaxSize) {
		this.values = create(valuesLen, stackMaxSize);
	}
	
	public native void read(String file) throws IOException;
	
	public native void setRegistersLen(long len) throws OutOfMemoryError;
	
	public native long getRegistersLen();
	
	public native void setRegister(long index, long val) throws IndexOutOfBoundsException;
	
	public native long getRegister(long index) throws IndexOutOfBoundsException;
	
	public native void setStackmaxSize(long len) throws OutOfMemoryError;
	
	public native long getStackMaxSize();
	
	public native long getStackSize();
	
	public native void push(long val) throws IndexOutOfBoundsException;
	
	public native long pop();
	
	@Override
	protected native void finalize();
	
}
