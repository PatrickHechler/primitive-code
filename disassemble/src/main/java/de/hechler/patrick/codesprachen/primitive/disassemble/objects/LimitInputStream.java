package de.hechler.patrick.codesprachen.primitive.disassemble.objects;

import java.io.IOException;
import java.io.InputStream;

import de.hechler.patrick.codesprachen.primitive.disassemble.PrimitiveDisassemblerMain;

public class LimitInputStream extends InputStream {
	
	private final InputStream in;
	private long              remain;
	
	public LimitInputStream(InputStream in, long length) {
		super();
		this.in = in;
		this.remain = length;
		if (in == null) {
			throw new NullPointerException("the InputStream in is null");
		}
		if (length < 0L) {
			throw new IllegalArgumentException("negative length");
		}
	}
	
	@Override
	public int read() throws IOException {
		synchronized (this) {
			PrimitiveDisassemblerMain.LOG.finer(() -> "read now 1 bytes (currently " + remain + " remaining)");
			if (remain <= 0) {
				return -1;
			}
			int r = in.read();
			if (r != -1) {
				remain--;
			}
			return r;
		}
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (len == 0) { return 0; }
		if (len < 0) { throw new IllegalArgumentException("negaive len: " + len); }
		synchronized (this) {
			PrimitiveDisassemblerMain.LOG.finer(() -> "read now " + len + " bytes (currently " + remain + " remaining)");
			if (remain <= 0) { return -1; }
			int rlen = len > remain ? (int) remain : len;
			int r    = in.read(b, off, rlen);
			if (r > 0) {
				remain -= r;
			}
			return r;
		}
	}
	
}
