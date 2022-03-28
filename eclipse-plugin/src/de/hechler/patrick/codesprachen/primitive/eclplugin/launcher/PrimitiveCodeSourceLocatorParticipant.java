package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

import de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.PrimitiveVirtualMashineSourceLocator.LocatedSource;

public class PrimitiveCodeSourceLocatorParticipant extends AbstractSourceLookupParticipant
		implements ISourceLookupParticipant {

	@Override
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof IStackFrame) {
			object = PrimitiveVirtualMashineSourceLocator.INSTANCE.getSourceElement((IStackFrame) object);
		}
		if (object instanceof LocatedSource) {
			LocatedSource ls = (LocatedSource) object;
			return ls.sourcefile.getName();
		}
		return null;
	}

}
