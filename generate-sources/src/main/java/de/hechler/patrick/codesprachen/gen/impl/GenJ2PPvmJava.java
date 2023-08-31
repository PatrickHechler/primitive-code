package de.hechler.patrick.codesprachen.gen.impl;

import java.io.IOException;
import java.io.Writer;

import de.hechler.patrick.codesprachen.gen.SrcGen;
import de.hechler.patrick.codesprachen.primitive.core.objects.PrimitiveConstant;


@SuppressWarnings("javadoc")
public class GenJ2PPvmJava implements SrcGen {
	
	@Override
	public void generate(Writer out) throws IOException {
		for (PrimitiveConstant pc : J2P.CONSTANTS.values()) {
			if (!pc.name().startsWith(J2P.JNI_ENV_START)) continue;
			out.write("\t");
			out.write(pc.name(), 0, pc.name().length() - J2P.JNI_ENV_END.length());
			out.write('\n');
		}
	}
	
}
