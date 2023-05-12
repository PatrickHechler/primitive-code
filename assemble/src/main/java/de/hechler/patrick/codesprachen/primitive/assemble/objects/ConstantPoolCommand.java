//This file is part of the Primitive Code Project
//DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//Copyright (C) 2023  Patrick Hechler
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
