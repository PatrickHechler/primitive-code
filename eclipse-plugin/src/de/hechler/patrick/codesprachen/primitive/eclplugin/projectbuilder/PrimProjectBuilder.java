package de.hechler.patrick.codesprachen.primitive.eclplugin.projectbuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.codesprachen.primitive.eclplugin.Activator;
import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;

public class PrimProjectBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = Activator.PLUGIN_ID + ".primProjectBuilder";

	private static final String MARKER_TYPE = Activator.PLUGIN_ID + ".primCodeProblem";

	private class PrimCodeDeltaVisitorIncrementalBuilder implements IResourceDeltaVisitor {
		private final IProgressMonitor monitor;

		public PrimCodeDeltaVisitorIncrementalBuilder(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				buildPrimCode(resource, monitor);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				cleanPrimCode(resource, monitor);
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				buildPrimCode(resource, monitor);
				break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	private class PrimCodeResourceVisitorBuilder implements IResourceVisitor {
		private final IProgressMonitor monitor;

		public PrimCodeResourceVisitorBuilder(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		public boolean visit(IResource resource) {
			buildPrimCode(resource, monitor);
			// return true to continue visiting children.
			return true;
		}
	}

	private class PrimCodeResourceVisitorCleaner implements IResourceVisitor {
		private final IProgressMonitor monitor;

		public PrimCodeResourceVisitorCleaner(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		public boolean visit(IResource resource) {
			cleanPrimCode(resource, monitor);
			// return true to continue visiting children.
			return true;
		}
	}

	// class XMLErrorHandler extends DefaultHandler {
	//
	// private IFile file;
	//
	// public XMLErrorHandler(IFile file) {
	// this.file = file;
	// }
	//
	// private void addMarker(SAXParseException e, int severity) {
	// PrimProjectBuilder.this.addMarker(file, e.getMessage(),
	// e.getLineNumber(), severity);
	// }
	//
	// public void error(SAXParseException exception) throws SAXException {
	// addMarker(exception, IMarker.SEVERITY_ERROR);
	// }
	//
	// public void fatalError(SAXParseException exception) throws SAXException {
	// addMarker(exception, IMarker.SEVERITY_ERROR);
	// }
	//
	// public void warning(SAXParseException exception) throws SAXException {
	// addMarker(exception, IMarker.SEVERITY_WARNING);
	// }
	// }

	// private SAXParserFactory parserFactory;

	// private void addMarker(IFile file, String message, int lineNumber, int
	// severity) {
	// try {
	// IMarker marker = file.createMarker(MARKER_TYPE);
	// marker.setAttribute(IMarker.MESSAGE, message);
	// marker.setAttribute(IMarker.SEVERITY, severity);
	// if (lineNumber == -1) {
	// lineNumber = 1;
	// }
	// marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
	// } catch (CoreException e) {
	// }
	// }

	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
		getProject().accept(new PrimCodeResourceVisitorCleaner(monitor));
	}

	private void cleanPrimCode(IResource resource, IProgressMonitor monitor) {
		String name = resource.getName();
		if (resource instanceof IFile && name.endsWith(".psc")) {
			IFile mashineFile = getProject().getFile(new Path(name.substring(0, name.length() - 3) + "pmc"));
			IFile exportFile = getProject().getFile(new Path(name.substring(0, name.length() - 3) + "psf"));
			deleteIfExist(monitor, mashineFile);
			deleteIfExist(monitor, exportFile);
		}
	}

	private static void deleteIfExist(IProgressMonitor monitor, IFile file) {
		if (file.exists()) {
			try {
				file.delete(true, monitor);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	private void buildPrimCode(IResource resource, IProgressMonitor monitor) {
		String name = resource.getName();
		if (resource instanceof IFile && name.endsWith(".psc")) {
			IFile sourceFile = (IFile) resource;
			deleteMarkers(sourceFile);
			try {
				IProject project = getProject();
				IFile mashineFile = project.getFile(new Path(name.substring(0, name.length() - 3) + "pmc"));
				IFile exportFile = project.getFile(new Path(name.substring(0, name.length() - 3) + "psf"));
				File loockup = ValidatorDocumentSetupParticipant.getProjectFile(project);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ByteArrayOutputStream exbaos = new ByteArrayOutputStream();
				// PrimitiveAssembler asm = new PrimitiveAssembler(baos, true,
				// true, false, true);
				PrimitiveAssembler asm = new PrimitiveAssembler(baos, new PrintStream(exbaos, true, StandardCharsets.UTF_8), loockup, true, true, false, true);
				asm.assemble(sourceFile.getContents(), Charset.forName(sourceFile.getCharset()));
				setContentOrCreate(monitor, mashineFile, new ByteArrayInputStream(baos.toByteArray()));
				byte[] exbytes = exbaos.toByteArray();
				if (exbytes.length == 0) {
					deleteIfExist(monitor, exportFile);
				} else {
					setContentOrCreate(monitor, exportFile, new ByteArrayInputStream(exbytes));
				}
			} catch (IOException | CoreException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void setContentOrCreate(IProgressMonitor monitor, IFile file, InputStream in) throws CoreException {
		if (file.exists()) {
			file.setContents(in, true, false, monitor);
		} else {
			file.create(in, true, monitor);
		}
		file.setDerived(true, monitor);
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		try {
			getProject().accept(new PrimCodeResourceVisitorBuilder(monitor));
		} catch (CoreException e) {
		}
	}

	// private SAXParser getParser() throws ParserConfigurationException,
	// SAXException {
	// if (parserFactory == null) {
	// parserFactory = SAXParserFactory.newInstance();
	// }
	// return parserFactory.newSAXParser();
	// }

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new PrimCodeDeltaVisitorIncrementalBuilder(monitor));
	}
}
