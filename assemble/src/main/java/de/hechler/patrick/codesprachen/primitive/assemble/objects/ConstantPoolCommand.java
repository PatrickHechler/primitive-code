package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class ConstantPoolCommand extends Command {
	
	private List <byte[]> values;
	private long          len;
	
	public ConstantPoolCommand() {
		super(null, null, null);
		values = new LinkedList <>();
		len = 0;
	}
	
	public void addBytes(byte[] bytes) {
		len += bytes.length;
		values.add(bytes);
	}
	
	@Override
	public long length() {
		return len;
	}
	
	public void write(OutputStream out) throws IOException {
		for (byte[] bytes : values) {
			out.write(bytes);
		}
	}
	
	@Override
	public String toString() {
		return "ConstantPool[len=" + len + "]";
	}
	
}
