package de.hechler.patrick.codesprachen.primitive.assemble.enums;

public enum PrimitiveFileTypes {

	primitiveSourceCode, primitiveMashineCode, primitiveSymbolFile,

	;

	public static PrimitiveFileTypes getTypeFromName(String name) {
		return getTypeFromName(name, null);
	}

	public static PrimitiveFileTypes getTypeFromName(String name, PrimitiveFileTypes fallback) {
		return getTypeFromExtension(name.substring(name.lastIndexOf('.') + 1), fallback);
	}

	public static PrimitiveFileTypes getTypeFromExtension(String extension) {
		return getTypeFromExtension(extension, null);
	}

	public static PrimitiveFileTypes getTypeFromExtension(String extension, PrimitiveFileTypes fallback) {
		switch (extension) {
			case "psc":
				return primitiveSourceCode;
			case "pmc":
				return primitiveMashineCode;
			case "psf":
				return primitiveSymbolFile;
			default:
				if (fallback != null) {
					return fallback;
				} else {
					throw new IllegalArgumentException("unknown extension type: '" + extension + "'");
				}
		}
	}
	
	public String getExtension() {
		switch (this) {
			case primitiveSourceCode:
				return "psc";
			case primitiveSymbolFile:
				return "psf";
			case primitiveMashineCode:
				return "pmc";
			default:
				throw new InternalError("unknown PrimitiveFileType: " + name());
		}
	}
	
	@Override
	public String toString() {
		switch (this) {
			case primitiveSourceCode:
				return "Primitive Source Code";
			case primitiveSymbolFile:
				return "Primitive Symbol File";
			case primitiveMashineCode:
				return "Primitive Mashine Code";
			default:
				throw new InternalError("unknown PrimitiveFileType: " + name());
		}
	}
	
}
