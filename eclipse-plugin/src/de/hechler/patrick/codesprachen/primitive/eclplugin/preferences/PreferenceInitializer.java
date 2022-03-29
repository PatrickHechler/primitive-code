package de.hechler.patrick.codesprachen.primitive.eclplugin.preferences;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.preferences.PreferenceConstants.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.hechler.patrick.codesprachen.primitive.eclplugin.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String[] prefs = getPrefs();
		String[] defs = getDefaults();
		for (int i = 0; i < PREFS_COUNT; i++) {
			store.setDefault(prefs[i], defs[i]);
		}
	}

}
