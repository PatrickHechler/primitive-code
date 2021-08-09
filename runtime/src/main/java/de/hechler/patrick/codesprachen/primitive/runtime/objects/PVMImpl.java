package de.hechler.patrick.codesprachen.primitive.runtime.objects;

public class PVMImpl {
	
	public native long create() throws OutOfMemoryError;
	
	private final long values;
	
	public PVMImpl() throws OutOfMemoryError {
		values = create();
	}
	
}
