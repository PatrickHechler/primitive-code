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
package de.hechler.patrick.codesprachen.primitive.assemble.enums;

public enum FileTypes {
	
	PRIMITIVE_SOURCE_CODE, PRIMITIVE_MASHINE_CODE, PRIMITIVE_SYMBOL_FILE,
	
	SIMPLE_SOURCE_CODE, SIMPLE_SYMBOL_FILE,
	
	;
	
	public static FileTypes getTypeFromName(String name) { return getTypeFromName(name, null); }
	
	public static FileTypes getTypeFromName(String name, FileTypes fallback) {
		return getTypeFromExtension(name.substring(name.lastIndexOf('.') + 1), fallback);
	}
	
	public static FileTypes getTypeFromExtension(String extension) {
		return getTypeFromExtension(extension, null);
	}
	
	public static FileTypes getTypeFromExtension(String extension, FileTypes fallback) {
		return switch (extension) {
		case "psc" -> PRIMITIVE_SOURCE_CODE;
		case "pmc" -> PRIMITIVE_MASHINE_CODE;
		case "psf" -> PRIMITIVE_SYMBOL_FILE;
		case "ssc" -> SIMPLE_SOURCE_CODE;
		case "ssf" -> SIMPLE_SYMBOL_FILE;
		default -> {
			if (fallback != null) {
				yield fallback;
			} else {
				throw new IllegalArgumentException("unknown extension type: '" + extension + '\'');
			}
		}
		};
	}

	public String getExtension() {
		return switch (this) {
		case PRIMITIVE_SOURCE_CODE -> "psc";
		case PRIMITIVE_SYMBOL_FILE -> "psf";
		case PRIMITIVE_MASHINE_CODE -> "pmc";
		case SIMPLE_SOURCE_CODE -> "ssc";
		case SIMPLE_SYMBOL_FILE -> "ssf";
		default -> throw new InternalError("unknown FileType: " + name());
		};
	}
	
	public String getExtensionWithDot() {
		return switch (this) {
		case PRIMITIVE_SOURCE_CODE -> ".psc";
		case PRIMITIVE_SYMBOL_FILE -> ".psf";
		case PRIMITIVE_MASHINE_CODE -> ".pmc";
		case SIMPLE_SOURCE_CODE -> ".ssc";
		case SIMPLE_SYMBOL_FILE -> ".ssf";
		default -> throw new InternalError("unknown FileType: " + name());
		};
	}
	
	@Override
	public String toString() {
		return switch (this) {
		case PRIMITIVE_SOURCE_CODE -> "Primitive Source Code";
		case PRIMITIVE_SYMBOL_FILE -> "Primitive Symbol File";
		case PRIMITIVE_MASHINE_CODE -> "Primitive Mashine Code";
		case SIMPLE_SOURCE_CODE -> "Simple Source Code";
		case SIMPLE_SYMBOL_FILE -> "Simple Symbol File";
		default -> throw new InternalError("unknown PrimitiveFileType: " + name());
		};
	}
	
}
