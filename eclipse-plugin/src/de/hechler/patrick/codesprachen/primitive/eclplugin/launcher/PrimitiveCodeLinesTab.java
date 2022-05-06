package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.launcher.PrimitiveCodeLaunchShortcut.getRootAsString;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PrimitiveCodeLinesTab extends AbstractLaunchConfigurationTab {

	private static final int BROWSE_PROGRAM = 1;
	private static final int BROWSE_PVM = 2;
	private static final int BROWSE_DIR = 3;

	private Text program;
	private Text pvm;
	private Text dir;
	private Text port;

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		Label label = new Label(container, SWT.NULL);
		label.setText("&Mashine File:");
		program = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(SWT.FILL, 0, true, false);
		program.setLayoutData(gd);
		program.addModifyListener(e -> dialogChanged(true));
		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse(BROWSE_PROGRAM);
			}

		});

		label = new Label(container, SWT.NULL);
		label.setText("&PVM File:");
		pvm = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL, 0, true, false);
		pvm.setLayoutData(gd);
		pvm.addModifyListener(e -> dialogChanged(true));
		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse(BROWSE_PVM);
			}

		});

		label = new Label(container, SWT.NULL);
		label.setText("&Directory:");
		dir = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL, 0, true, false);
		dir.setLayoutData(gd);
		dir.addModifyListener(e -> dialogChanged(true));
		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse(BROWSE_DIR);
			}

		});

		label = new Label(container, SWT.NULL);
		label.setText("&Port:");
		port = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL, 0, true, false);
		port.setLayoutData(gd);
		port.addModifyListener(e -> dialogChanged(true));

		pvm.setText("pvm");

		dialogChanged(false);
		setControl(container);
	}

	private void handleBrowse(int browse) {
		String chosen;
		if (browse == BROWSE_DIR) {
			DirectoryDialog dd = new DirectoryDialog(getShell());
			dd.setFilterPath(getRootAsString());
			chosen = dd.open();
		} else {
			FileDialog fd = new FileDialog(getShell());
			fd.setFilterPath(getRootAsString());
			chosen = fd.open();
		}
		if (chosen == null) {
			return;
		}
		switch (browse) {
		case BROWSE_PROGRAM:
			this.program.setText(chosen);
			break;
		case BROWSE_PVM:
			this.pvm.setText(chosen);
			break;
		case BROWSE_DIR:
			this.dir.setText(chosen);
			break;
		default:
			throw new InternalError("unknown browse type: browse=" + browse);
		}
		dialogChanged(false);
	}

	private void dialogChanged(boolean isDirty) {
		setDirty(isDirty);
		String text = program.getText();
		if (!text.endsWith(".pmc") && !text.endsWith(".psc")) {
			setErrorMessage("File extension must be \"pmc\" or \"psc\"");
			return;
		}
		if (text.trim().isEmpty()) {
			setErrorMessage("program not set!");
			return;
		}
		File res = new File(text);
		if (!res.exists()) {
			setErrorMessage("program does not exists!");
			return;
		}
		if (!res.isFile()) {
			setErrorMessage("program is no file!");
			return;
		}
		text = pvm.getText();
		if (text.trim().isEmpty()) {
			setErrorMessage("pvm not set!");
			return;
		}
		res = new File(text);
		if (!res.exists()) {
			setErrorMessage("pvm does not exists!");
			return;
		}
		if (!res.isFile()) {
			setErrorMessage("pvm is no file!");
			return;
		}
		text = dir.getText();
		if (text.trim().isEmpty()) {
			text = getRootAsString();
		}
		res = new File(text);
		if (!res.exists()) {
			setErrorMessage("dir does not exists!");
			return;
		}
		if (!res.isDirectory()) {
			setErrorMessage("dir is no directory!");
			return;
		}

		String port = this.port.getText().trim();
		if (!port.isEmpty()) {
			try {
				int pnum = Integer.parseInt(port);
				new ServerSocket(pnum).close();
			} catch (NumberFormatException e) {
				setErrorMessage("number format exception on the port filed: " + e);
			} catch (IOException e) {
				setErrorMessage("could not open the port: " + e);
			}
		}
		setErrorMessage(null);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PROGRAM, "");
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM,
				PrimitiveCodeLaunchShortcut.getPVM());
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_DIRECTORY, getRootAsString());
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_DEBUG_PORT, -1);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String program = configuration.getAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PROGRAM, "");
			String pvm = configuration.getAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM, "pvm");
			String dir = configuration.getAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_DIRECTORY,
					getRootAsString());
			int port = configuration.getAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_DEBUG_PORT, -1);
			this.program.setText(program);
			this.pvm.setText(pvm);
			this.dir.setText(dir);
			if (port != -1) {
				this.port.setText(port + "");
			}
			dialogChanged(false);
		} catch (CoreException e) {
			throw new InternalError(e);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String program = this.program.getText();
		String pvm = this.pvm.getText();
		String dir = this.dir.getText();
		String text = this.port.getText().trim();
		int port = text.isEmpty() ? -1 : Integer.parseInt(text);
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PROGRAM, program);
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM, pvm);
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_DIRECTORY, dir);
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_DEBUG_PORT, port);
		setDirty(false);
	}

	@Override
	public String getName() {
		return "Primitive-Code Files Tab";
	}

}
