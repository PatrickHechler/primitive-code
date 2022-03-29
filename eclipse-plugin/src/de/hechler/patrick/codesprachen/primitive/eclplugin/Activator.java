package de.hechler.patrick.codesprachen.primitive.eclplugin;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.hechler.patrick.codesprachen.primitive.eclplugin"; //$NON-NLS-1$

	// The shared instance
	private volatile static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		if (plugin == null) {
			plugin = this;
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (plugin == this) {
			plugin = null;
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		if (plugin == null) {
			throw new InternalError("plugin not loaded!");
		}
		return plugin;
	}

}
