package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class PrimitiveCodeLaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			searchAndLaunch(((IStructuredSelection) selection).toArray(), mode);
		} else if (selection instanceof IMarkSelection) {
			IDocument document = ((IMarkSelection) selection).getDocument();
			String text = document.get();
			searchAndLaunch(new Object[] { text }, mode);
//		} else if (selection instanceof ITextSelection) {
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		if (editor instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) editor;
			IDocumentProvider provider = textEditor.getDocumentProvider();
			IDocument document = provider.getDocument(input);
			String text = document.get();
			searchAndLaunch(new Object[] { text }, mode);
		} else if (input instanceof FileEditorInput) {
			FileEditorInput fei = (FileEditorInput) input;
			IFile file = fei.getFile();
			searchAndLaunch(new Object[] { file }, mode);
		}
	}

	protected void searchAndLaunch(Object[] search, String mode) {
		if (search != null) {
			Object found = null;
			for (Object obj : search) {
				if (obj == null) {
					continue;
				}
				if (obj instanceof String || obj instanceof SourceFile) {
					if (found != null) {
						return;
					}
					found = obj;
				} else if (obj instanceof IFile) {
					Object val = findFile((IFile) obj);
					if (found != null) {
						if (val != null) {
							return;
						}
					}
					found = val;
				} else if (obj instanceof IAdaptable) {
					IFile file = ((IAdaptable) obj).getAdapter(IFile.class);
					Object val = findFile(file);
					if (found != null) {
						if (val != null) {
							return;
						}
					}
					found = val;
				}
			}
			if (found != null) {
				try {
					ILaunchConfiguration config;
					ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
					ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(
							de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.debugelements.PrimitiveCodeDebugTarget.PVM_MODEL_IDENTIFIER);
					if (found instanceof String) {
						String text = (String) found;
						String name = launchManager.generateLaunchConfigurationName("pvm source text: " + mode);
						ILaunchConfigurationWorkingCopy workingcopy = type.newInstance(null, name);
						workingcopy.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_SOURCE_TEXT, text);
						config = workingcopy.doSave();
					} else if (found instanceof SourceFile) {
						SourceFile sf = (SourceFile) found;
						config = launchManager.getLaunchConfiguration(sf.sourceFile);
						if (config == null) {
							String name = launchManager.generateLaunchConfigurationName("pvm source text: " + mode);
							ILaunchConfigurationWorkingCopy workingcopy = type.newInstance(null, name);
							String file = getResourceAsString(sf.sourceFile);
							workingcopy.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_SOURCE_TEXT, sf.text);
							workingcopy.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PROGRAM, file);
							workingcopy.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM, getPVM());
							workingcopy.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM_ARGS,
									getPVMArgs());
							config = workingcopy.doSave();
						}
					} else if (found instanceof IFile) {
						IFile binaryFile = (IFile) found;
						config = launchManager.getLaunchConfiguration(binaryFile);
						if (config == null) {
							String name = launchManager.generateLaunchConfigurationName("pvm source text: " + mode);
							ILaunchConfigurationWorkingCopy workingcopy = type.newInstance(null, name);
							String file = getResourceAsString(binaryFile);
							workingcopy.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PROGRAM, file);
							workingcopy.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM, getPVM());
							workingcopy.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM_ARGS,
									getPVMArgs());
							config = workingcopy.doSave();
						}
					} else {
						throw new InternalError("found object of an unknown class: " + found.getClass());
					}
					ILaunch launch = config.launch(mode, null);
					launchManager.addLaunch(launch);
				} catch (CoreException e) {
				}
			}
		}
	}

	private static final String PVM_ENV = "pvm_executable";

	private static List<String> pvmArguments = null;

	public static String getPVM() {
		if (pvmArguments == null) {
			initilize();
		}
		return pvmArguments.get(0);
	}

	public static List<String> getPVMArgs() {
		if (pvmArguments == null) {
			initilize();
		}
		return pvmArguments.subList(1, pvmArguments.size());
	}

	public static List<String> getPVMAndArgs() {
		if (pvmArguments == null) {
			initilize();
		}
		return pvmArguments;
	}

	private static void initilize() {
		String pvm = System.getenv(PVM_ENV);
		IOException first = null;
		if (pvm != null) {
			try {
				List<String> all = argumentLineToList(pvm);
				Runtime.getRuntime().exec(all.toArray(new String[all.size()]));
				pvmArguments = Collections.unmodifiableList(all);
				return;
			} catch (IOException e) {
				first = e;
			}
		}
		try {
			Runtime.getRuntime().exec(new String[] { "pvm", "--help" });
			pvmArguments = Collections.unmodifiableList(Arrays.asList("pvm"));
			return;
		} catch (IOException e) {
			try {
				Runtime.getRuntime().exec(new String[] { "wsl", "-e", "pvm", "--help" });
				pvmArguments = Collections.unmodifiableList(Arrays.asList("wsl", "-e", "pvm"));
				return;
			} catch (IOException e2) {
				if (first == null) {
					first = e;
				} else {
					first.addSuppressed(e);
				}
				first.addSuppressed(e2);
				System.err.println(
						"can not determine how to eecute the PrimitiveVirtualMashine: set the default by using the env-var: '"
								+ PVM_ENV + "' fallback to default with: 'pvm' and no args for the default execution");
				pvmArguments = Collections.unmodifiableList(Arrays.asList("pvm"));
				return;
			}
		}
	}

	private Object findFile(IFile file) {
		if (!file.exists()) {
			return null;
		}
		String name = file.getName();
		if (name.endsWith(".psc")) {
			IContainer parent = file.getParent();
			IResource mashineFile = parent.findMember("./" + name.substring(0, name.length() - 3) + "pmc");
			if (mashineFile.exists() && mashineFile.getType() == IResource.FILE) {
				file = (IFile) mashineFile;
				name = file.getName();
			} else {
				try {
					InputStream in = file.getContents();
					byte[] bytes = in.readAllBytes();
					String cs = file.getCharset();
					String text = new String(bytes, cs);
					return new SourceFile(text, file);
				} catch (CoreException | IOException e) {
				}
			}
		}
		if (name.endsWith(".pmc")) {
			return file;
		}
		return null;
	}

	public static class SourceFile {

		public final String text;
		public final IFile sourceFile;

		public SourceFile(String text, IFile sourceFile) {
			this.text = text;
			this.sourceFile = sourceFile;
		}
	}

	public static String getRootAsString() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath rowLoc = root.getRawLocation();
		return rowLoc.toOSString();
	}

	public static String getResourceAsString(IResource res) {
		IPath rowLoc = res.getRawLocation();
		return rowLoc.toOSString();
	}

	public static List<String> argumentLineToList(String text) throws RuntimeException {
		List<String> result = new ArrayList<>();
		while (!text.isEmpty()) {
			if (text.charAt(0) == '"') {
				String add = text.replaceFirst("^[\"](([^\"]|\\\\\")+)\".*$", "$1").replaceAll("\\\\\"", "\"")
						.replaceAll("\\\\\\\\", "\\");
				result.add(add);
				if (add == text) {
					throw new RuntimeException("illegal format of argument string: (illeglaPart='" + text + "')");
				}
				text = text.substring(add.length() + 2).trim();
			} else {
				String start = text.replaceFirst("^([^\\s]+)\\s.*$", "$1");
				result.add(start);
				text = text.substring(start.length()).trim();
			}
		}
		return result;
	}

	public static IResource[] getStringAsResource(String absolute) {
		URI uri = new File(absolute).toURI();
		IContainer[] containers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(uri);
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(uri);
		IResource[] result = new IResource[containers.length + files.length];
		System.arraycopy(containers, 0, result, 0, containers.length);
		System.arraycopy(files, 0, result, containers.length, files.length);
		return result;
	}

}
