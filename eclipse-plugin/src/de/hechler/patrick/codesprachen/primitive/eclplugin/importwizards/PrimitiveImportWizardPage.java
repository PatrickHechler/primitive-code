package de.hechler.patrick.codesprachen.primitive.eclplugin.importwizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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

import de.hechler.patrick.codesprachen.primitive.assemble.objects.PrimitiveAssembler;
import de.hechler.patrick.codesprachen.primitive.disassemble.enums.DisasmMode;
import de.hechler.patrick.codesprachen.primitive.disassemble.objects.PrimitiveDisassembler;
import de.hechler.patrick.codesprachen.primitive.eclplugin.enums.PrimitiveFileTypes;
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
		String[] extensions = new String[]{"*.psc;*.psf;*.pmc", "*.psc", "*.pmc", "*.psf", "*.*"}; // NON-NLS-1
																									// //NON-NLS-2
																									// //NON-NLS-3
																									// //NON-NLS-4
																									// //NON-NLS-5
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
			FileInputStream in = new FileInputStream(new File(fileName));
			PrimitiveFileTypes fromType = PrimitiveFileTypes.getTypeFromName(fileName, PrimitiveFileTypes.primitiveSourceCode);
			PrimitiveFileTypes toType = PrimitiveFileTypes.getTypeFromName(getFileName(), PrimitiveFileTypes.primitiveSourceCode);
			switch (fromType) {
				case primitiveMashineCode:
					switch (toType) {
						case primitiveMashineCode:
							break;
						case primitiveSourceCode: {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							PrimitiveDisassembler disasm = new PrimitiveDisassembler(DisasmMode.executable, new PrintStream(baos, true, StandardCharsets.UTF_8));
							disasm.deassemble(0L, in);
							return new ByteArrayInputStream(baos.toByteArray());
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
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							PrimitiveAssembler asm = new PrimitiveAssembler(baos, new PrintStream(new NullOutputStream()), null, true, true, false, true);
							asm.assemble(in, StandardCharsets.UTF_8);
							return new ByteArrayInputStream(baos.toByteArray());
						}
						case primitiveSourceCode:
							break;
						case primitiveSymbolFile: {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							PrimitiveAssembler asm = new PrimitiveAssembler(new NullOutputStream(), new PrintStream(baos), null, true, true, false, true);
							asm.assemble(in, StandardCharsets.UTF_8);
							return new ByteArrayInputStream(baos.toByteArray());
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
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							try (PrintStream out = new PrintStream(baos, false, StandardCharsets.UTF_8)) {
								Map<String, Long> symbols = new LinkedHashMap<>();
								PrimitiveAssembler.readSymbols("", symbols, new Scanner(in, StandardCharsets.UTF_8));
								symbols.forEach((symbol, value) -> {
									out.println("#" + symbol + " UHEX-" + Long.toUnsignedString(value, 16));
								});
								out.flush();
							}
							return new ByteArrayInputStream(baos.toByteArray());
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
