package de.hechler.patrick.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.ClosedChannelException;

public class NullOutputStream extends OutputStream {

	private volatile boolean closed = false;
	
	@Override
	public void write(int b) throws IOException {
		if (this.closed) {
			throw new ClosedChannelException();
		}
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (this.closed) {
			throw new ClosedChannelException();
		}
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		if (this.closed) {
			throw new ClosedChannelException();
		}
	}
	
	@Override
	public void close() throws IOException {
		this.closed = true;
	}
	
}
