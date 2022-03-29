package de.hechler.patrick.codesprachen.primitive.eclplugin.preferences;

import static de.hechler.patrick.codesprachen.primitive.eclplugin.preferences.PreferenceConstants.PREFS_COUNT;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.preferences.PreferenceConstants.getPrefs;
import static de.hechler.patrick.codesprachen.primitive.eclplugin.preferences.PreferenceConstants.getTexts;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.hechler.patrick.codesprachen.primitive.eclplugin.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class PrimitiveCodePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PrimitiveCodePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preference page for Primitive Source Code Coloring");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		String[] prefs = getPrefs(), texts = getTexts();
		for (int i = 0; i < PREFS_COUNT; i++) {
			addField(new ColorFieldEditor(prefs[i], texts[i], getFieldEditorParent()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	public static RGB[] getRGBs() {
		RGB[] result = new RGB[PREFS_COUNT];
		String[] prefs = getPrefs();
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		for (int i = 0; i < PREFS_COUNT; i++) {
			String strval = store.getString(prefs[i]);
			result[i] = StringConverter.asRGB(strval);
		}
		return result;
	}

	public static TextAttribute[] getTextAttributes() {
		TextAttribute[] result = new TextAttribute[PREFS_COUNT];
		RGB[] rgbs = getRGBs();
		for (int i = 0; i < PREFS_COUNT; i++) {
			result[i] = new TextAttribute(new Color(rgbs[i]));
		}
		return result;
	}

}
