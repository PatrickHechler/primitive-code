package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements;

import java.io.IOError;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IMemoryBlock;

public class PrimitiveCodeMemoryBlock extends PrimitiveCodeNTDE implements IMemoryBlock {

	private final long startAddress;
	private final long length;

	public PrimitiveCodeMemoryBlock(PrimitiveCodeDebugTarget debug, long startAddress, long length) {
		super(debug);
		this.startAddress = startAddress;
		this.length = length;
	}

	@Override
	public long getStartAddress() {
		return this.startAddress;
	}

	@Override
	public long getLength() {
		return this.length;
	}

	@Override
	public byte[] getBytes() {
		try {
			if (this.length > Integer.MAX_VALUE) {
				throw new IllegalStateException("I am larger than every java byte array can be! (length=" + this.length + ")");
			}
			int len = (int) this.length;
			byte[] bytes = new byte[len];
			this.debug.com.getMem(this.startAddress, bytes, 0, len);
			return bytes;
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public boolean supportsValueModification() {
		return true;
	}

	@Override
	public void setValue(long offset, byte[] bytes) throws DebugException {
		if (offset + bytes.length > this.length) {
			throw new DebugException(new Status(IStatus.ERROR, getClass(), "off+bytes.len>this.len off=" + offset + " bytes.len=" + bytes.length + " this.len=" + this.length));
		}
		try {
			this.debug.com.setMem(this.startAddress + offset, bytes, 0, bytes.length);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return findAdapter(adapter, this);
	}

	@Override
	public PrimitiveCodeDebugTarget getDebugTarget() {
		return this.debug;
	}
	
	@Override
	public String getName() {
		return "PVM Memory Block";
	}
	
}
