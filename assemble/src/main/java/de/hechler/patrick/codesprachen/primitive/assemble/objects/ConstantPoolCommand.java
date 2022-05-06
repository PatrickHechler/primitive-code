package de.hechler.patrick.codesprachen.primitive.assemble.objects;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ConstantPoolCommand extends Command {
	
	private List <byte[]> values;
	private long len;
	
	public ConstantPoolCommand() {
		super(null, null, null);
		values = new ArrayList <>();
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
		for (int i = 0; i < values.size(); i ++ ) {
			out.write(values.get(i));
		}
	}
	
	@Override
	public boolean alignable() {
		return true;
	}
	
	@Override
	public String toString() {
		return "ConstantPool[len="+len+"]";
	}
	
}
