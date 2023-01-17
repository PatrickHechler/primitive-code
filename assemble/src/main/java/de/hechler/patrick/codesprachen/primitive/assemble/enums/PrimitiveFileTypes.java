package de.hechler.patrick.codesprachen.primitive.assemble.enums;

public enum PrimitiveFileTypes {
	
	PRIMITIVE_SOURCE_CODE, PRIMITIVE_MASHINE_CODE, PRIMITIVE_SYMBOL_FILE,
	
	;
	
	public static PrimitiveFileTypes getTypeFromName(String name) { return getTypeFromName(name, null); }
	
	public static PrimitiveFileTypes getTypeFromName(String name, PrimitiveFileTypes fallback) {
		return getTypeFromExtension(name.substring(name.lastIndexOf('.') + 1), fallback);
	}
	
	public static PrimitiveFileTypes getTypeFromExtension(String extension) {
		return getTypeFromExtension(extension, null);
	}
	
	public static PrimitiveFileTypes getTypeFromExtension(String extension, PrimitiveFileTypes fallback) {
		return switch (extension) {
		case "psc" -> PRIMITIVE_SOURCE_CODE;
		case "pmc" -> PRIMITIVE_MASHINE_CODE;
		case "psf" -> PRIMITIVE_SYMBOL_FILE;
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
		default -> throw new InternalError("unknown PrimitiveFileType: " + name());
		};
	}
	
	@Override
	public String toString() {
		return switch (this) {
		case PRIMITIVE_SOURCE_CODE -> "Primitive Source Code";
		case PRIMITIVE_SYMBOL_FILE -> "Primitive Symbol File";
		case PRIMITIVE_MASHINE_CODE -> "Primitive Mashine Code";
		default -> throw new InternalError("unknown PrimitiveFileType: " + name());
		};
	}
	
}
