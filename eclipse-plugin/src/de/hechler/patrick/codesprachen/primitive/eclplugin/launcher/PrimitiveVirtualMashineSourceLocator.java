package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.ui.ISourcePresentation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;
import de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements.PrimitiveCodeDebugTarget;
import de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements.PrimitiveCodeStackFrame;

public class PrimitiveVirtualMashineSourceLocator implements ISourceLocator, ISourcePresentation {

	public static final PrimitiveVirtualMashineSourceLocator INSTANCE = new PrimitiveVirtualMashineSourceLocator(null);

	private final ISourceLocator fallback;

	public PrimitiveVirtualMashineSourceLocator(ISourceLocator fallback) {
		this.fallback = fallback;
	}

	@Override
	public Object getSourceElement(IStackFrame stackFrame) {
		IDebugTarget dt = stackFrame.getDebugTarget();
		if (dt instanceof PrimitiveCodeDebugTarget) {
			PrimitiveCodeStackFrame sf = (PrimitiveCodeStackFrame) stackFrame;
			IFile file = sf.getFile();
			return new LocatedSource(file, sf.getLineNumber(), sf.getCharStart(), sf.getCharEnd());
		}
		if (this.fallback != null) {
			return fallback.getSourceElement(stackFrame);
		}
		return null;
	}

	public static class LocatedSource {

		public final IFile sourcefile;
		public final int line;
		public final int charStart;
		public final int charStop;

		public LocatedSource(IFile souorcefile, int line, int charStart, int charStop) {
			this.sourcefile = souorcefile;
			this.line = line;
			this.charStart = charStart;
			this.charStop = charStop;
		}

	}

	@Override
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IStackFrame) {
			element = PrimitiveVirtualMashineSourceLocator.INSTANCE.getSourceElement((IStackFrame) element);
		}
		if (element instanceof LocatedSource) {
			return new FileEditorInput(((LocatedSource) element).sourcefile);
		} else if (element instanceof IFile) {
			return new FileEditorInput((IFile) element);
		} else if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile) ((ILineBreakpoint) element).getMarker().getResource());
		} else {
			return null;
		}
	}

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		if (element instanceof IFile || element instanceof ILineBreakpoint || element instanceof LocatedSource
				|| element instanceof IStackFrame)
			return ValidatorDocumentSetupParticipant.MY_EDITOR_ID;
		return null;
	}

}
