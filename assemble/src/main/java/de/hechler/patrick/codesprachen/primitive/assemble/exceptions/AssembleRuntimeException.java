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
package de.hechler.patrick.codesprachen.primitive.assemble.exceptions;

public class AssembleRuntimeException extends RuntimeException {
	
	/** UID */
	private static final long serialVersionUID = -8548990438376299254L;

	
	public final int line;
	public final int posInLine;
	public final int length;
	public final int charPos;
	
	public AssembleRuntimeException(int line, int posInLine, int length, int charPos, String msg, Throwable cause) {
		super(msg, cause);
		this.line = line;
		this.posInLine = posInLine;
		this.length = length;
		this.charPos = charPos;
	}
	
	public AssembleRuntimeException(int line, int posInLine, int length, int charPos, String msg) {
		super(msg);
		this.line = line;
		this.posInLine = posInLine;
		this.length = length;
		this.charPos = charPos;
	}
	
}
