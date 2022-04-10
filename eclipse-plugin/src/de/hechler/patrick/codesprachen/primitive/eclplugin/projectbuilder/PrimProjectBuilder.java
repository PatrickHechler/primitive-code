package de.hechler.patrick.codesprachen.primitive.eclplugin.projectbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.codesprachen.primitive.eclplugin.Activator;

public class PrimProjectBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = Activator.PLUGIN_ID + ".primProjectBuilder";

	private static final String MARKER_TYPE = Activator.PLUGIN_ID + ".primCodeProblem";

	private IProject[] dependencies;
	private java.nio.file.Path[] loockups;

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
	protected synchronized IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		update();
		args = args == null ? new HashMap<>() : new HashMap<>(args);
		args.merge(getProject().getFullPath().toPortableString(), "[PROJECT]", (old, add) -> {
			assert "[PROJECT]".equals(old);
			if (old.contains(add)) {
				return old;
			} else {
				return old + "," + add;
			}
		});
		buildDependencies(kind, args, monitor);
		build(kind, monitor);
		return this.dependencies;
	}

	private void buildDependencies(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		if (this.dependencies != null) {
			for (IProject project : this.dependencies) {
				try {
					args.compute(project.getFullPath().toPortableString(), (key, val) -> {
						if (val == null) {
							return "[PROJECT]";
						} else if (val.contains("[PROJECT]")) {
							throw new RuntimeException("dependency loop detected: me: '" + getProject().getFullPath() + "' other: '" + project.getFullPath() + "'");
						} else {
							return "[PROJECT], " + val;
						}
					});
				} catch (RuntimeException re) {
					throw new CoreException(new Status(IStatus.ERROR, getClass(), re.getMessage(), re));
				}
				project.build(kind, BUILDER_ID, args, monitor);
			}
		}
	}

	private void build(int buildMode, IProgressMonitor monitor) throws CoreException, InternalError {
		switch (buildMode) {
		case FULL_BUILD:
			fullBuild(monitor);
			break;
		case INCREMENTAL_BUILD:
		case AUTO_BUILD:
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
			break;
		default:
			throw new InternalError("illegal build mode: " + buildMode);
		}
	}

	private void update() throws CoreException {
		IFile file = getProject().getFile(".primproject");
		if (!file.exists()) {
			return;
		}
		List<IProject> dependencies = new ArrayList<>();
		List<java.nio.file.Path> lookups = new ArrayList<>();
		lookups.add(getProject().getRawLocation().toFile().toPath());
		Charset cs;
		try {
			cs = Charset.forName(file.getCharset());
		} catch (CoreException | UnsupportedCharsetException e) {
			cs = StandardCharsets.UTF_8;
		}
		file.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
		try (Scanner in = new Scanner(file.getContents(true), cs)) {
			int lineNumber = 1;
			while (in.hasNextLine()) {
				String line = in.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}
				IWorkspace ws = getProject().getWorkspace();
				IResource add = ws.getRoot().findMember(line);
				if (add.getType() == IResource.PROJECT) {
					dependencies.add((IProject) add);
					lookups.add(Paths.get(line));
				} else {
					IMarker marker = file.createMarker(IMarker.PROBLEM);
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					marker.setAttribute(IMarker.USER_EDITABLE, true);
					marker.setAttribute(IMarker.MESSAGE, "the resource is no project!");
					marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				}
				lineNumber++;
			}
		}
		this.dependencies = dependencies.toArray(new IProject[dependencies.size()]);
		this.loockups = lookups.toArray(new java.nio.file.Path[lookups.size()]);
	}

	protected synchronized void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
		getProject().accept(new PrimCodeResourceVisitorCleaner(monitor));
	}

	private void cleanPrimCode(IResource resource, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE) {
			return;
		}
		String name = resource.getName();
		switch (name.substring(name.length() - 4, name.length())) {
		case ".pmc":
		case ".psf": {
			IFile sourceFile = getProject().getFile(new Path(name.substring(0, name.length() - 3) + "psc"));
			if (sourceFile.exists()) {
				deleteIfExist(monitor, (IFile) resource);
			}
			break;
		}
		case ".psc": {
			IFile mashineFile = getProject().getFile(new Path(name.substring(0, name.length() - 3) + "pmc"));
			IFile exportFile = getProject().getFile(new Path(name.substring(0, name.length() - 3) + "psf"));
			deleteIfExist(monitor, mashineFile);
			deleteIfExist(monitor, exportFile);
		}
			break;
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
				IContainer project = resource.getParent();
				IFile mashineFile = project.getFile(new Path(name.substring(0, name.length() - 3) + "pmc"));
				IFile exportFile = project.getFile(new Path(name.substring(0, name.length() - 3) + "psf"));
				// PrimitiveAssembler asm = new PrimitiveAssembler(baos, true,
				// true, false, true);
				PipedInputStream in = new PipedInputStream();
				PipedOutputStream out = new PipedOutputStream(in);
				PipedInputStream expin = new PipedInputStream();
				PipedOutputStream expout = new PipedOutputStream(expin);
				PrimitiveAssembler asm = new PrimitiveAssembler(out, new PrintStream(expout, true, StandardCharsets.UTF_8), this.loockups, true, true, false, true);
				asm.assemble(sourceFile.getContents(), Charset.forName(sourceFile.getCharset()));
				setContentOrCreate(monitor, mashineFile, in);
				expout.close();
				if (expin.available() == 0) {
					deleteIfExist(monitor, exportFile);
				} else {
					setContentOrCreate(monitor, exportFile, expin);
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
