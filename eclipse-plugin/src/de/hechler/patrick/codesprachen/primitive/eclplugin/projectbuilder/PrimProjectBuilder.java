package de.hechler.patrick.codesprachen.primitive.eclplugin.projectbuilder;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import de.hechler.patrick.codesprachen.primitive.assemble.exceptions.AssembleError;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.codesprachen.primitive.eclplugin.Activator;
import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;
import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.CountingOutputStream;

public class PrimProjectBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = Activator.PLUGIN_ID + ".primProjectBuilder";

	private static final String MARKER_TYPE = Activator.PLUGIN_ID + ".marker.primBuildProblem";

	public static boolean alwaysAssembleMaxOne = true;
	private static int maxBuildWorkers = 16;
	private static int maxCleanWorkers = 1;

	private int maxWorkerThread = 16;
	private IContainer[] dependencies;
	private java.nio.file.Path[] loockups;

	private volatile boolean isCanceled;
	private volatile int runningThreadCount;
	private final Set<Thread> runningThreads = new HashSet<>();
	private final Object look = new Object();

	private class PrimCodeDeltaVisitorIncrementalBuilder implements IResourceDeltaVisitor {
		private final IProgressMonitor monitor;
		private final IPath build;

		public PrimCodeDeltaVisitorIncrementalBuilder(IContainer build, IProgressMonitor monitor) {
			this.monitor = monitor;
			this.build = build.getFullPath();
		}

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			return PrimProjectBuilder.visit(delta.getFullPath(), build, monitor, () -> visit0(delta));
		}

		public boolean visit0(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				buildPrimCode(resource, monitor);
				break;
			case IResourceDelta.REMOVED:
				cleanPrimCode(resource, monitor);
				break;
			case IResourceDelta.CHANGED:
				buildPrimCode(resource, monitor);
				break;
			}
			return !monitor.isCanceled();
		}
	}

	private class PrimCodeResourceVisitorBuilder implements IResourceVisitor {
		private final IProgressMonitor monitor;
		private final IPath build;

		public PrimCodeResourceVisitorBuilder(IContainer build, IProgressMonitor monitor) {
			this.monitor = monitor;
			this.build = build.getFullPath();
		}

		public boolean visit(IResource resource) {
			return PrimProjectBuilder.visit(resource.getFullPath(), build, monitor, () -> buildPrimCode(resource, monitor));
		}
	}

	private static <T extends Throwable> boolean visit(IPath res, IPath build, IProgressMonitor monitor, ThrowingRunnable<T> run) throws T {
		if (res.segmentCount() < build.segmentCount()) {
			return !monitor.isCanceled();
		}
		if (!build.isPrefixOf(res)) {
			return false;
		}
		run.run();
		// buildPrimCode(resource, monitor);
		return !monitor.isCanceled();
	}

	public static interface ThrowingRunnable<T extends Throwable> {
		void run() throws T;
	}

	private class PrimCodeResourceVisitorCleaner implements IResourceVisitor {
		private final IProgressMonitor monitor;

		public PrimCodeResourceVisitorCleaner(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		public boolean visit(IResource resource) {
			cleanPrimCode(resource, monitor);
			return !monitor.isCanceled();
		}
	}

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
		build(getProject(), kind, monitor);
		IProject[] result = new IProject[this.dependencies.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = this.dependencies[i].getProject();
		}
		return result;
	}

	private void buildDependencies(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		if (this.dependencies == null) {
			return;
		}
		for (IContainer build : this.dependencies) {
			addAndCheck(args, build);
			build(build, kind, monitor);
		}
	}

	private void addAndCheck(Map<String, String> args, IContainer project) throws CoreException {
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
	}

	private void build(IContainer build, int buildMode, IProgressMonitor monitor) throws CoreException, InternalError {
		monitor.subTask("build container: " + build.getFullPath());
		syncInit(true);
		this.maxWorkerThread = maxBuildWorkers;
		switch (buildMode) {
		case INCREMENTAL_BUILD:
		case AUTO_BUILD:
			IResourceDelta delta = getDelta(build.getProject());
			if (delta != null) {
				incrementalBuild(build, delta, monitor);
				break;
			}
		case FULL_BUILD:
			fullBuild(build, monitor);
			break;
		default:
			throw new InternalError("illegal build mode: " + buildMode);
		}
		waitUntilThreadsFinished(monitor);
		build.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	}

	private void update() throws CoreException {
		IProject project = getProject();
		project.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_INFINITE);
		java.nio.file.Path path = ValidatorDocumentSetupParticipant.getResourcePath(project);
		if (!Files.exists(path)) {
			IMarker marker = project.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			marker.setAttribute(IMarker.USER_EDITABLE, true);
			marker.setAttribute(IMarker.MESSAGE, "the project could not be found!");
			throw new CoreException(new Status(IStatus.ERROR, getClass(), "the project could not be found!"));
		}
		IFile file = project.getFile(".primproject");
		if (!file.exists()) {
			return;
		}
		List<IContainer> dependencies = new ArrayList<>();
		List<java.nio.file.Path> lookups = new ArrayList<>();
		lookups.add(project.getRawLocation().toFile().toPath());
		Charset cs;
		try {
			cs = Charset.forName(file.getCharset());
		} catch (CoreException | UnsupportedCharsetException e) {
			cs = StandardCharsets.UTF_8;
		}
		try (Scanner in = new Scanner(file.getContents(true), cs)) {
			int lineNumber = 1;
			while (in.hasNextLine()) {
				String line = in.nextLine().trim();
				if (line.isEmpty()) {
					continue;
				}
				IWorkspace ws = project.getWorkspace();
				IResource add = ws.getRoot().findMember(line);
				if (!add.exists()) {
					IMarker marker = file.createMarker(MARKER_TYPE);
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
					marker.setAttribute(IMarker.USER_EDITABLE, true);
					marker.setAttribute(IMarker.MESSAGE, "the resource does not exist!");
					marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				} else if (add instanceof IContainer) {
					dependencies.add((IProject) add);
					lookups.add(Paths.get(line));
				} else {
					IMarker marker = file.createMarker(MARKER_TYPE);
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
					marker.setAttribute(IMarker.USER_EDITABLE, true);
					marker.setAttribute(IMarker.MESSAGE, "the resource is no container!");
					marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				}
				lineNumber++;
			}
		}
		this.dependencies = dependencies.toArray(new IContainer[dependencies.size()]);
		this.loockups = lookups.toArray(new java.nio.file.Path[lookups.size()]);
	}

	protected synchronized void clean(IProgressMonitor monitor) throws CoreException {
		syncInit(true);
		this.maxWorkerThread = maxCleanWorkers;
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
		getProject().accept(new PrimCodeResourceVisitorCleaner(monitor));
		waitUntilThreadsFinished(monitor);
		assert runningThreads.isEmpty();
	}

	private void syncInit(boolean resetIsCanceled) {
		if (resetIsCanceled) {
			this.isCanceled = false;
		}
		assert runningThreadCount == 0 && runningThreads.isEmpty();
	}

	private void waitUntilThreadsFinished(IProgressMonitor monitor) {
		while (this.runningThreadCount > 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (monitor != null && monitor.isCanceled()) {
				this.isCanceled = true;
				synchronized (this.look) {
					for (Iterator<Thread> iterator = this.runningThreads.iterator(); iterator.hasNext();) {
						Thread thread = iterator.next();
						if (thread.isAlive()) {
							thread.interrupt();
						} else {
							assert false;
							iterator.remove();
						}
					}
				}
			}
		}
	}

	private void cleanPrimCode(IResource resource, IProgressMonitor monitor) {
		if (resource.getType() != IResource.FILE) {
			return;
		}
		String name = resource.getName();
		if (name.endsWith(".psc")) {
			execute("delete mashine files for: " + resource.getFullPath(), () -> deleteMashineFiles(resource, monitor, name));
		}
	}

	private void deleteMashineFiles(IResource resource, IProgressMonitor monitor, String name) {
		IContainer parent = resource.getParent();
		IFile mashineFile = parent.getFile(new Path(name.substring(0, name.length() - 3) + "pmc"));
		IFile exportFile = parent.getFile(new Path(name.substring(0, name.length() - 3) + "psf"));
		deleteIfExist(monitor, mashineFile);
		deleteIfExist(monitor, exportFile);
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
		if (resource.getType() != IResource.FILE) {
			return;
		}
		String name = resource.getName();
		if (!name.endsWith(".psc")) {
			return;
		}
		execute("assemble: " + resource.getFullPath(), () -> assemblePrimCode(monitor, resource));
	}

	private void assemblePrimCode(IProgressMonitor monitor, IResource resource) {
		String name = resource.getName();
		if (monitor != null) {
			monitor.subTask("assemble " + resource.getFullPath());
		}
		try {
			IFile sourceFile = (IFile) resource;
			deleteMarkers(sourceFile);
			IContainer parent = resource.getParent();
			IFile mashineFile = parent.getFile(new Path(name.substring(0, name.length() - 3) + "pmc"));
			IFile symbolFile = parent.getFile(new Path(name.substring(0, name.length() - 3) + "psf"));
			java.nio.file.Path mashinePath = ValidatorDocumentSetupParticipant.getResourcePath(mashineFile);
			java.nio.file.Path symbolPath = ValidatorDocumentSetupParticipant.getResourcePath(symbolFile);
			Charset cs = getCharset(sourceFile);
			CountingOutputStream symbolStream = new CountingOutputStream(Files.newOutputStream(symbolPath));
			try (OutputStream mashineStream = Files.newOutputStream(mashinePath); PrintStream symbolPrint = new PrintStream(symbolStream, false, cs); symbolStream) {
				PrimitiveAssembler asm = new PrimitiveAssembler(mashineStream, symbolPrint, this.loockups, true, true, false, true);
				asm.assemble(ValidatorDocumentSetupParticipant.getResourcePath(resource), sourceFile.getContents(), cs);
				symbolPrint.flush();
			}
			if (symbolStream.getCount() <= 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				Files.delete(symbolPath);
			}
		} catch (IOException | CoreException | AssembleError e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	@SuppressWarnings("unused")
	private void buildPrimCodeOld(IResource resource, IProgressMonitor monitor) {
		String name = resource.getName();
		if (resource instanceof IFile && name.endsWith(".psc")) {
			if (monitor != null) {
				monitor.subTask("assemble " + resource.getFullPath());
			}
			if (alwaysAssembleMaxOne) {
				syncInit(false);
			}
			IFile sourceFile = (IFile) resource;
			deleteMarkers(sourceFile);
			IContainer parent = resource.getParent();
			IFile mashineFile = parent.getFile(new Path(name.substring(0, name.length() - 3) + "pmc"));
			IFile exportFile = parent.getFile(new Path(name.substring(0, name.length() - 3) + "psf"));
			// PrimitiveAssembler asm = new PrimitiveAssembler(baos, true,
			// true, false, true);
			final PipedInputStream in = new PipedInputStream();
			final PipedInputStream expin = new PipedInputStream();
			new Thread(() -> {
				addThread();
				try {
					setContentOrCreate(monitor, mashineFile, in);
				} catch (CoreException e) {
					e.printStackTrace();
				} finally {
					removeThread();
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, "primitive code write mashine file: " + mashineFile.getFullPath()).start();
			final PipedOutputStream expout;
			final PipedOutputStream out;
			try {
				out = new PipedOutputStream(in);
				expout = new PipedOutputStream(expin);
			} catch (IOException e) {
				throw new IOError(e);
			}
			final Thread exportThread = new Thread(() -> {
				addThread();
				try {
					while (expin.available() == 0) {
						try {
							synchronized (Thread.currentThread()) {
								Thread.currentThread().wait(100);
							}
						} catch (InterruptedException e) {
							if (isCanceled) {
								out.close();
								expout.close();
								in.close();
								expin.close();
								return;
							}
							break;
						}
					}
					if (expin.available() == 0) {
						deleteIfExist(monitor, exportFile);
					} else {
						setContentOrCreate(monitor, exportFile, expin);
					}
				} catch (IOException | CoreException e) {
					e.printStackTrace();
				} finally {
					removeThread();
					try {
						expin.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}, "primitive code write symbol file: " + exportFile.getFullPath());
			exportThread.start();
			new Thread(() -> {
				addThread();
				try (PrintStream exportPrint = new PrintStream(expout, false, StandardCharsets.UTF_8)) {
					Charset cs = getCharset(sourceFile);
					PrimitiveAssembler asm = new PrimitiveAssembler(out, exportPrint, this.loockups, true, true, false, true);
					asm.assemble(ValidatorDocumentSetupParticipant.getResourcePath(resource), sourceFile.getContents(), cs);
				} catch (IOException | AssembleError | CoreException e) {
					e.printStackTrace();
				} finally {
					removeThread();
					try {
						out.close();
						expout.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				exportThread.interrupt();
			}, "primitive code assembler: " + sourceFile.getFullPath()).start();
			if (alwaysAssembleMaxOne) {
				waitUntilThreadsFinished(monitor);
			}
		}
	}

	private Charset getCharset(IFile sourceFile) throws CoreException {
		Charset cs;
		try {
			cs = Charset.forName(sourceFile.getCharset());
		} catch (IllegalArgumentException e) {
			cs = StandardCharsets.UTF_8;
		}
		return cs;
	}

	@Deprecated
	private static void setContentOrCreate(IProgressMonitor monitor, IFile file, InputStream in) throws CoreException {
		if (file.exists()) {
			file.setContents(in, IResource.FORCE, monitor);
			file.setDerived(true, monitor);
		} else {
			file.create(in, IResource.FORCE | IResource.DERIVED, monitor);
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(IContainer build, final IProgressMonitor monitor) throws CoreException {
		try {
			getProject().accept(new PrimCodeResourceVisitorBuilder(build, monitor));
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

	protected void incrementalBuild(IContainer build, IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new PrimCodeDeltaVisitorIncrementalBuilder(build, monitor));
	}

	private void removeThread() {
		synchronized (this.look) {
			if (this.runningThreads.remove(Thread.currentThread())) {
				this.runningThreadCount--;
			}
		}
	}

	private void addThread() {
		Thread currentThread = Thread.currentThread();
		while (!this.isCanceled) {
			synchronized (this.look) {
				if (this.runningThreadCount < this.maxWorkerThread) {
					if (this.runningThreads.add(currentThread)) {
						this.runningThreadCount++;
					}
					return;
				} else if (this.runningThreads.contains(currentThread)) {
					return;
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void execute(String name, Runnable run) {
		new Thread(() -> {
			try {
				addThread();
				if (!isCanceled) {
					run.run();
				}
			} finally {
				removeThread();
			}
		}, name).start();
	}

}
