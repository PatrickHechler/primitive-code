package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PrimitiveCodeArgumentsTab extends AbstractLaunchConfigurationTab {

	private Text programargs;
	private Text pvmargs;

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 9;

		Label label = new Label(container, SWT.NONE);
		label.setText("Program Arguments:");
		programargs = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		programargs.setLayoutData(data);
		programargs.setEditable(true);
		programargs.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				dialogChanged(true);
			}
		});

		label = new Label(container, SWT.NONE);
		label.setText("PVM Arguments:");
		pvmargs = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		pvmargs.setLayoutData(data);
		pvmargs.setEditable(true);
		pvmargs.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				dialogChanged(true);
			}
		});

		dialogChanged(false);
		setControl(container);
	}

	private void dialogChanged(boolean isDirty) {
		setDirty(isDirty);
		List<String> pvmargs = toList(this.pvmargs);
		for (String pvmarg : pvmargs) {
			switch (pvmarg) {
			case "--wait":
			case "--port":
			case "--pmc":
				setErrorMessage("illegal pvm argument: '" + pvmarg + "'");
				return;
			default:
				if (pvmarg.startsWith("--port=") || pvmarg.startsWith("--pmc=")) {
					setErrorMessage("illegal pvm argument: '" + pvmarg + "'");
					return;
				}
			}
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PROGRAM_ARGS, Collections.emptyList());
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM_ARGS, Collections.emptyList());
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			List<String> programargs = configuration
					.getAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PROGRAM_ARGS, Collections.emptyList());
			List<String> pvmargs = configuration.getAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM_ARGS, PrimitiveCodeLaunchShortcut.getPVMArgs());
			initText(programargs, this.programargs);
			initText(pvmargs, this.pvmargs);
			dialogChanged(false);
		} catch (CoreException e) {
			throw new InternalError(e);
		}
	}

	private void initText(List<String> list, Text text) {
		if (list.isEmpty()) {
			text.setText("");
		} else {
			String cmdLine = getCommandLine(list);
			text.setText(cmdLine);
		}
	}

	public static String getCommandLine(List<String> list) {
		StringBuilder build = new StringBuilder(list.get(0));
		for (int i = 1, size = list.size(); i < size; i++) {
			String add = list.get(i);
			if (add.matches("[^\\s]*\\s.*")) {
				build.append(" \"").append(add).append('"');
			} else {
				build.append(' ').append(add);
			}
		}
		String cmdLine = build.toString();
		return cmdLine;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		List<String> programaregs = toList(this.programargs);
		List<String> pvmargs = toList(this.pvmargs);
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PROGRAM_ARGS, programaregs);
		configuration.setAttribute(PrimitiveCodeLauncerDelegate.ATTRIBUTE_NAME_PVM_ARGS, pvmargs);
		setDirty(false);
	}

	private List<String> toList(Text text) throws RuntimeException {
		return PrimitiveCodeLaunchShortcut.argumentLineToList(text.getText());
	}

	@Override
	public String getName() {
		return "Primitive-Code Arguments Tab";
	}
}
