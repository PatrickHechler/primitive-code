package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.ProjectSourceContainer;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

public class PrimitiveCodeSourcePathComputer implements ISourcePathComputerDelegate {

	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {
		String program = configuration.getAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PROGRAM, "./");
		IResource[] res = PrimitiveCodeLaunchShortcut.getStringAsResource(program);
		String dir = configuration.getAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_DIRECTORY, "./");
		IResource[] res2 = PrimitiveCodeLaunchShortcut.getStringAsResource(dir);
		if (program.equals(dir)) {
			dir = null;
			res2 = new IResource[0];
		}
		ISourceContainer[] result = new ISourceContainer[res.length + res2.length + 1];
		createSorceContainer(res, result, 0);
		createSorceContainer(res2, result, res.length);
		result[result.length - 1] = new WorkspaceSourceContainer();
		return result;
	}

	private void createSorceContainer(IResource[] rs, ISourceContainer[] scs, int scsoff) {
		for (int i = 0; i < rs.length; i++) {
			switch (rs[i].getType()) {
			case IResource.FILE:
			case IResource.ROOT:
				break;
			case IResource.FOLDER:
				scs[i + scsoff] = new FolderSourceContainer((IContainer) rs[i], true);
				break;
			case IResource.PROJECT:
				scs[i + scsoff] = new ProjectSourceContainer((IProject) rs[i], true);
				break;
			}
		}
	}

}
