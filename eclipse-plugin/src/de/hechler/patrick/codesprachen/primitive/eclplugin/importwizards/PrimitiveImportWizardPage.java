package de.hechler.patrick.codesprachen.primitive.eclplugin.importwizards;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import de.hechler.patrick.codesprachen.primitive.assemble.enums.PrimitiveFileTypes;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveConstant;
import de.hechler.patrick.codesprachen.primitive.disassemble.enums.DisasmMode;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.PrimitiveDisassembler;
import de.hechler.patrick.objects.NullOutputStream;

public class PrimitiveImportWizardPage extends WizardNewFileCreationPage {

	protected FileFieldEditor editor;

	public PrimitiveImportWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle(pageName); // NON-NLS-1
		setDescription("Import a file from the local file system into the workspace"); // NON-NLS-1
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.WizardNewFileCreationPage#createAdvancedControls(
	 * org.eclipse.swt.widgets.Composite)
	 */
	protected void createAdvancedControls(Composite parent) {
		Composite fileSelectionArea = new Composite(parent, SWT.NONE);
		GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		fileSelectionArea.setLayoutData(fileSelectionData);

		GridLayout fileSelectionLayout = new GridLayout();
		fileSelectionLayout.numColumns = 3;
		fileSelectionLayout.makeColumnsEqualWidth = false;
		fileSelectionLayout.marginWidth = 0;
		fileSelectionLayout.marginHeight = 0;
		fileSelectionArea.setLayout(fileSelectionLayout);

		editor = new FileFieldEditor("fileSelect", "Select File: ", fileSelectionArea);
		editor.getTextControl(fileSelectionArea).addModifyListener(e -> {
			IPath path = new Path(PrimitiveImportWizardPage.this.editor.getStringValue());
			setFileName(path.lastSegment());
		});
		String[] extensions = new String[]{"*.psc;*.psf;*.pmc", "*.psc", "*.pmc", "*.psf", "*.*"};
		editor.setFileExtensions(extensions);
		fileSelectionArea.moveAbove(null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createLinkTarget()
	 */
	protected void createLinkTarget() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
	protected InputStream getInitialContents() {
		try {
			String fileName = editor.getStringValue();
			java.nio.file.Path path = Paths.get(fileName);
			InputStream in = Files.newInputStream(path);
			PrimitiveFileTypes fromType = PrimitiveFileTypes.getTypeFromName(fileName, PrimitiveFileTypes.primitiveSourceCode);
			PrimitiveFileTypes toType = PrimitiveFileTypes.getTypeFromName(getFileName(), PrimitiveFileTypes.primitiveSourceCode);
			switch (fromType) {
			case primitiveMashineCode:
				switch (toType) {
				case primitiveMashineCode:
					break;
				case primitiveSourceCode: {
					PipedInputStream pin = new PipedInputStream();
					try (PipedOutputStream pout = new PipedOutputStream(pin)) {
						PrimitiveDisassembler disasm = new PrimitiveDisassembler(DisasmMode.executable, new PrintStream(pout, true, StandardCharsets.UTF_8));
						disasm.deassemble(0L, in);
					}
					return pin;
				}
				case primitiveSymbolFile:
					canNotConvert(fromType, toType);
					break;
				default:
					throw new InternalError("unknown PrimitiveFileType: " + fromType.name());
				}
				break;
			case primitiveSourceCode:
				switch (toType) {
				case primitiveMashineCode: {
					PipedInputStream pin = new PipedInputStream();
					try (PipedOutputStream pout = new PipedOutputStream(pin)) {
						PrimitiveAssembler asm = new PrimitiveAssembler(pout, new PrintStream(new NullOutputStream()), new java.nio.file.Path[]{}, true, true, false, true);
						asm.assemble(path, in, StandardCharsets.UTF_8);
					}
					return pin;
				}
				case primitiveSourceCode:
					break;
				case primitiveSymbolFile: {
					PipedInputStream pin = new PipedInputStream();
					try (PipedOutputStream pout = new PipedOutputStream(pin)) {
						PrimitiveAssembler asm = new PrimitiveAssembler(new NullOutputStream(), new PrintStream(pout, true, StandardCharsets.UTF_8), new java.nio.file.Path[]{}, true, true, false, true);
						asm.assemble(path, in, StandardCharsets.UTF_8);
					}
					return pin;
				}
				default:
					throw new InternalError("unknown PrimitiveFileType: " + fromType.name());
				}
				break;
			case primitiveSymbolFile:
				switch (toType) {
				case primitiveMashineCode:
					canNotConvert(fromType, toType);
					break;
				case primitiveSourceCode:
					PipedInputStream pin = new PipedInputStream();
					PipedOutputStream pout = new PipedOutputStream(pin);
					try (PrintStream out = new PrintStream(pout, false, StandardCharsets.UTF_8)) {
						Map<String, PrimitiveConstant> symbols = new LinkedHashMap<>();
						PrimitiveAssembler.readSymbols(null, symbols, new Scanner(in, StandardCharsets.UTF_8), path);
						PrimitiveAssembler.export(symbols, out);
					}
					return pin;
				case primitiveSymbolFile:
					break;
				default:
					throw new InternalError("unknown PrimitiveFileType: " + fromType.name());
				}
				break;
			default:
				throw new InternalError("unknown PrimitiveFileType: " + fromType.name());
			}
			return in;
		} catch (IOException e) {
			return null;
		}
	}

	private void canNotConvert(PrimitiveFileTypes from, PrimitiveFileTypes to) {
		System.err.println("[PrimitiveCode]: can't convert from " + from + " to " + to);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getNewFileLabel()
	 */
	protected String getNewFileLabel() {
		return "New File Name:";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.dialogs.WizardNewFileCreationPage#validateLinkedResource()
	 */
	protected IStatus validateLinkedResource() {
		return new Status(IStatus.OK, "file-create_import-dummy", IStatus.OK, "", null); // NON-NLS-1
	}
}
