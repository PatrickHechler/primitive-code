package de.hechler.patrick.codesprachen.primitive.eclplugin.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTabGroup;

public class PrimitiveCodeTabGroup extends AbstractLaunchConfigurationTabGroup implements ILaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		setTabs(new PrimitiveCodeLinesTab(),  new PrimitiveCodeArgumentsTab(), new CommonTab());
	}

}
