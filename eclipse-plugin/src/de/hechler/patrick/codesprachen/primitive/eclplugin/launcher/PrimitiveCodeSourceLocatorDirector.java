package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

public class PrimitiveCodeSourceLocatorDirector extends AbstractSourceLookupDirector implements ISourceLookupDirector {

	@Override
	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[] { new PrimitiveCodeSourceLocatorParticipant() });
	}

}
