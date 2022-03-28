package de.hechler.patrick.codesprachen.primitive.eclplugin.objects;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.core.resources.IMarker;

import de.hechler.patrick.codesprachen.primitive.assemble.PrimitiveFileGrammarParser.ParseContext;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;

public class DocumentValue {

	public PVMDebugingComunicator currentDebugSession;
	public ParseContext context;
	public ByteArrayOutputStream baos = new ByteArrayOutputStream();
	public ByteArrayOutputStream exportBaos = new ByteArrayOutputStream();
	public final PrimitiveAssembler asm;
	public final List<IMarker> markers;

	public DocumentValue(List<IMarker> markers, File lookup) {
		this.asm = new PrimitiveAssembler(baos, new PrintStream(exportBaos, true, StandardCharsets.UTF_8), lookup == null ? new File("./") : lookup, true, true, false, true);
		this.markers = markers;
	}

}
