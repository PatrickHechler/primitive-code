package de.hechler.patrick.codesprachen.primitive.eclplugin.hyperlink;

import java.nio.file.Path;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.codesprachen.primitive.eclplugin.fileeditor.ValidatorDocumentSetupParticipant;

public class PrimitiveHyperlink implements IHyperlink {

	private final Path path;
	private final int line;
	private final IRegion region;
	private final String text;

	public PrimitiveHyperlink(Path path, int line, IRegion region, String text) {
		this.path = path;
		this.line = line;
		this.region = region;
		this.text = text;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return null;
	}

	@Override
	public String getHyperlinkText() {
		return text;
	}

	@Override
	public void open() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorReference[] refs = page.getEditorReferences();
		IEditorPart editorPart = null;
		for (IEditorReference editorRef : refs) {
			if (!isEditor(editorRef)) {
				continue;
			}
			editorPart = editorRef.getEditor(true);
			break;
		}
		if (editorPart == null) {
			try {
				editorPart = page.openEditor(getEditorInput(), getEditorID());
			} catch (PartInitException e) {
				e.printStackTrace();
				return;
			}
		}
		// https://stackoverflow.com/questions/35591397/eclipseget-and-set-caret-position-of-the-editor
		Control control = editorPart.getAdapter(Control.class);
		if (control instanceof StyledText) {
			StyledText text = (StyledText) control;
			int lineOff = text.getLineAtOffset(line);
			text.setCaretOffset(lineOff);
			// Point relPos = text.getLocationAtOffset(text.getCaretOffset());
			// Point absPos = text.toDisplay(relPos);
		}
		editorPart.setFocus();
	}

	private boolean isEditor(IEditorReference editorRef) {
		try {
			IEditorInput ein = editorRef.getEditorInput();
			if (ein instanceof FileEditorInput) {
				if (path == PrimitiveAssembler.START_CONSTANTS_PATH) {
					return false;
				}
				IFile file = ((FileEditorInput) ein).getFile();
				IFile pathFile = ValidatorDocumentSetupParticipant.getPathFile(path);
				if (file.equals(pathFile)) {
					return true;
				}
				return false;
			} else if (ein instanceof IStorageEditorInput) {
				if (path != PrimitiveAssembler.START_CONSTANTS_PATH) {
					return false;
				}
				IStorage storage = ((IStorageEditorInput) ein).getStorage();
				return storage.equals(getStorageFromResource());
			} else {
				return false;
			}
		} catch (CoreException e) {
			return false;
		}
	}

	private IEditorInput getEditorInput() {
		if (path == PrimitiveAssembler.START_CONSTANTS_PATH) {
			return new PrimitiveEditorInput(getStorageFromResource(), "readme.html", null, "/res/readme.html");
		} else {
			return new FileEditorInput(ValidatorDocumentSetupParticipant.getPathFile(path));
		}
	}

	private PrimitiveStorage getStorageFromResource() {
		return new PrimitiveStorage("readme.html", getClass().getResource("/res/readme.html"));
	}

	private String getEditorID() {
		return ValidatorDocumentSetupParticipant.MY_EDITOR_ID;
	}

}
