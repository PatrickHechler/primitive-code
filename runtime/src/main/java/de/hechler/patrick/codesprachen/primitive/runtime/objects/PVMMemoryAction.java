package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.util.Arrays;

public class PVMMemoryAction {
	
	public final MemAction action;
	private long PNTR;
	private final long[] values;
	
	
	
	/**
	 * 
	 * @param action the action to perform
	 * @param PNTR the pointer, when {@code action} is {@link MemAction#allocate} it will be ignored and overwritten
	 * @param values the values may be changed without beefing copied before (done when {@code action} is {@link MemAction#read})
	 */
	public PVMMemoryAction(MemAction action, long PNTR, long[] values) {
		this.action = action;
		this.PNTR = PNTR;
		this.values = values;
	}
	
	
	
	public long getPNTR() {
		return PNTR;
	}
	
	void setPNTR(long pNTR) {
		PNTR = pNTR;
	}
	
	public long[] getValues() {
		return values.clone();
	}
	
	long[] getValues0() {
		return values;
	}
	
	@Override
	public String toString() {
		return "PVMMemoryAction [action=" + action + ", PNTR=" + PNTR + ", values=" + Arrays.toString(values) + "]";
	}
	
	public static enum MemAction {
		
		read(8), write(9),
		
		allocate(10), reallocate(11), free(12);
		
		public final int signal;
		
		private MemAction(int signal) {
			this.signal = signal;
		}
		
	}
	
}
