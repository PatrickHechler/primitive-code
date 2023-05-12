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
