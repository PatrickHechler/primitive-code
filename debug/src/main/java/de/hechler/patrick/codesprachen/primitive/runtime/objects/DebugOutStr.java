package de.hechler.patrick.codesprachen.primitive.runtime.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class DebugOutStr extends OutputStream {
	
	private final List <OutputStream> outs = new ArrayList <>();
	
	@Override
	public synchronized void write(int b) throws IOException {
		for (OutputStream o : outs) {
			try {
				o.write(b);
			} catch (IOException | RuntimeException tt) {
				tt.printStackTrace();
			}
		}
	}
	
	@Override
	public synchronized void write(byte[] b) throws IOException {
		for (OutputStream o : outs) {
			try {
				o.write(b);
			} catch (IOException | RuntimeException tt) {
				tt.printStackTrace();
			}
		}
	}
	
	@Override
	public synchronized void write(byte[] b, int off, int len) throws IOException {
		for (OutputStream o : outs) {
			try {
				o.write(b, off, len);
			} catch (IOException | RuntimeException tt) {
				tt.printStackTrace();
			}
		}
	}
	
	@Override
	public synchronized void close() throws IOException {
		for (OutputStream o : outs) {
			try {
				o.close();
			} catch (IOException | RuntimeException tt) {
				tt.printStackTrace();
			}
		}
	}
	
	@Override
	public synchronized void flush() throws IOException {
		for (OutputStream o : outs) {
			try {
				o.flush();
			} catch (IOException | RuntimeException tt) {
				tt.printStackTrace();
			}
		}
	}
	
	public synchronized void add(OutputStream out) {
		outs.add(out);
	}
	
}
