package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.RuntimeProcess;

import de.hechler.patrick.codesprachen.primitive.eclplugin.objects.DocumentValue;
import de.hechler.patrick.codesprachen.primitive.runtime.objects.PVMDebugingComunicator;

//	org.eclipse.debug.core.model.ILaunchConfigurationDelegate
public class PrimitiveCodeLauncerDelegate implements ILaunchConfigurationDelegate {

	public static final String ATTRIBUTE_NAME_DEBUG_PORT = "pvm_debug_port";
	public static final String ATTRIBUTE_NAME_PVM_ARGS = "pvm_args";
	public static final String ATTRIBUTE_NAME_PROGRAM_ARGS = "program_args";
	public static final String ATTRIBUTE_NAME_PROGRAM = "program";
	public static final String ATTRIBUTE_NAME_PVM = "pvm";
	public static final String ATTRIBUTE_NAME_DIRECTORY = "dir";
	public static final String ATTRIBUTE_NAME_SOURCE_TEXT = "pvm_sources";

	private static final Random rnd = new Random();

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		int dport = configuration.getAttribute(ATTRIBUTE_NAME_DEBUG_PORT, -1);
		List<String> pvmargs = configuration.getAttribute(ATTRIBUTE_NAME_PVM_ARGS, Collections.emptyList());
		List<String> programargs = configuration.getAttribute(ATTRIBUTE_NAME_PROGRAM_ARGS, Collections.emptyList());
		String pvm = configuration.getAttribute(ATTRIBUTE_NAME_PVM, "pvm");
		String program = configuration.getAttribute(ATTRIBUTE_NAME_PROGRAM, "");
		String dir = configuration.getAttribute(ATTRIBUTE_NAME_DIRECTORY, "/");
		if (program.isEmpty()) {
			IStatus status = new Status(IStatus.ERROR, getClass(), "can not run without a program (attribute name='" + ATTRIBUTE_NAME_PROGRAM + "')");
			throw new CoreException(status);
		}
		if (program.endsWith(".psc")) {
			program = program.substring(0, program.length() - 3) + "pmc";
		} else if (!program.endsWith(".pmc")) {
			IStatus status = new Status(IStatus.ERROR, getClass(), "program does not have the type *.pmc (program='" + program + "')");
			throw new CoreException(status);
		}
		for (String arg : pvmargs) {
			switch (arg) {
				case "--port" :
				case "--wait" :
				case "--pmc" :
					error("illegal pvmarg: (pvmarg='" + arg + "', all pvmarg=" + pvmargs + ")");
			}
			if (arg.startsWith("--port=")) {
				error("illegal pvmarg: (pvmarg='" + arg + "', all pvmarg=" + pvmargs + ")");
			}
			if (arg.startsWith("--pmc=")) {
				error("illegal pvmarg: (pvmarg='" + arg + "', all pvmarg=" + pvmargs + ")");
			}
		}
		boolean run = true;
		List<String> arguments = new ArrayList<>(2 + pvmargs.size() + programargs.size());
		arguments.add(pvm);
		arguments.addAll(pvmargs);
		switch (mode) {
			case ILaunchManager.DEBUG_MODE :
				run = false;
				arguments.add("--wait");
				dport = dport == -1 ? rnd.nextInt((1 << 16) - 2500) + 2500 : dport;
				arguments.add("--port=" + dport);
				arguments.add("--pmc=" + program);
				break;
			case ILaunchManager.RUN_MODE :
				if (dport != -1) {
					error("port set on run mode! (port='" + mode + "')");
				}
				arguments.add("--pmc=" + program);
				break;
			default :
				error("unknown launch mode: (mode='" + mode + "')");
		}
		arguments.addAll(programargs);
		if (!program.endsWith(".pmc")) {
			error("illegal mashin file (program='" + program + "' (expected file of type *.pmc))");
		}
		// DocumentValue docval = null;
		IProcess process;
		Process p;
		String[] argArr = arguments.toArray(new String[arguments.size()]);
		long timestamp = System.currentTimeMillis();
		try {
			p = Runtime.getRuntime().exec(argArr, null, new File(dir));
		} catch (IOException e) {
			e.printStackTrace();
			throw new CoreException(new Status(IStatus.ERROR, getClass(), e.getMessage(), e));
		}
		if (!run) {
			IFile sourcefile = null;
			// long proglen = 0L;
			// String source = program.substring(0, program.length() - 3) +
			// "psc";
			// DocumentValue[] ds = new DocumentValue[1];
			sourcefile = findSourceFile(program);
			// docval = ds[0];
			// proglen = docval.context.pos;
			ISourceLocator old = launch.getSourceLocator();
			launch.setSourceLocator(old == null ? PrimitiveVirtualMashineSourceLocator.INSTANCE : new PrimitiveVirtualMashineSourceLocator(old));
			// IDebugModelPresentation dmp =
			// DebugUITools.newDebugModelPresentation(
			// de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements.PrimitiveCodeDebugTarget.PVM_MODEL_IDENTIFIER);
			// if (dmp == null) {
			// // TODO
			// }
			try {
				PVMDebugingComunicator com = new PVMDebugingComunicator(p, new Socket("localhost", dport));
				de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements.PrimitiveCodeDebugTarget debug = new de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements.PrimitiveCodeDebugTarget(
						p, com, launch, PrimitiveCodeArgumentsTab.getCommandLine(arguments), timestamp, dir, program);
				debug.addFile(sourcefile, com.getSnapshot().ip);
				process = debug.process;
				launch.addDebugTarget(debug);
			} catch (IOException e) {
				if (p.isAlive()) {
					p.destroy();
					e.printStackTrace();
					throw new CoreException(new Status(IStatus.ERROR, getClass(), e.getMessage(), e));
				} else {
					throw new CoreException(new Status(IStatus.ERROR, getClass(), "failed to start the pvm!"));
				}
			}
		} else {
			process = new RuntimeProcess(launch, p, pvm + " : " + pvmargs + " : " + program + " : " + programargs, null);
		}
		launch.addProcess(process);
		// oldLaunch(launch, dport, dir, run, sourcefile, proglen, docval,
		// argArr);
	}

	@SuppressWarnings({"deprecation", "unused"})
	private void oldLaunch(ILaunch launch, int dport, String dir, boolean run, IFile sourcefile, long proglen, DocumentValue docval, String[] argArr) {
		PrimitiveCodeDebugTarget process = new PrimitiveCodeDebugTarget(launch, argArr, sourcefile);
		try {
			process.start(dir, dport, proglen, launch);
			if (!run) {
				launch.addDebugTarget(process);
				docval.currentDebugSession = process.getCom();
			}
			launch.addProcess(process.getProcess());
		} catch (IOException e) {
		}
	}

	public static IFile findSourceFile(String absolute) {
		if (absolute.endsWith(".pmc")) {
			absolute = absolute.substring(0, absolute.length() - 3) + "psc";
		}
		// return findSourceFile(absolute, null);
		// }
		//
		// private static IFile findSourceFile(String source, DocumentValue[]
		// ds) {
		IFile sourcefile;
		sourcefile = findSourceFile0(absolute);
		if (sourcefile == null && absolute.startsWith("/mnt/")) { // maybe
																	// running
																	// on WSL
			sourcefile = findSourceFile0(absolute.substring("/mnt/".length(), "/mnt/".length() + 1) + ":" + absolute.substring("/mnt/a".length()).replaceAll("\\/", "\\\\"));
		}
		return sourcefile;
	}

	private static IFile findSourceFile0(String source) {
		IFile[] files;
		files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(new File(source).toURI());
		for (IFile check : files) {
			if (check.exists()) {
				return check;
			}
		}
		return null;
	}

	private void error(String msg) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, getClass(), msg);
		throw new CoreException(status);
	}

}
