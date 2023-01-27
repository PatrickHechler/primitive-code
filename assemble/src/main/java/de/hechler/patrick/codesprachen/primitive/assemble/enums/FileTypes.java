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
				throw new IllegalArgumentException("unknown extension type: '" + extension + "'");
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
