package de.hechler.patrick.codesprachen.primitive.eclplugin.objects;

import java.io.IOException;
import java.io.OutputStream;

public class CountingOutputStream extends OutputStream {

	private final OutputStream out;
	private volatile long count;

	public CountingOutputStream(OutputStream out) {
		this.out = out;
		this.count = 0L;
	}

	public long getCount() {
		return count;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
		this.count++;
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
		this.count += b.length;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		this.count += len;
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public int hashCode() {
		return out.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return out.equals(obj);
	}

	@Override
	public String toString() {
		return "CountingStream[" + out.toString() + ']';
	}

}
