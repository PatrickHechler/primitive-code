package de.hechler.patrick.codesprachen.primitive.ctj;

import java.nio.file.Path;

public class CTU {
	
	public final Path path;
	
	public CTU(Path path) {
		this.path = path;
	}
	
	
	
}

class CDefine {
	
	final String   name;
	final String[] args;
	
	public CDefine(String name, String[] args) {
		this.name = name;
		this.args = args;
	}
	
}
