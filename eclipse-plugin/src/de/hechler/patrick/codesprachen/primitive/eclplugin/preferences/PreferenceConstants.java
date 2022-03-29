package de.hechler.patrick.codesprachen.primitive.eclplugin.preferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	//@formatter:off
	public static final String P_LABEL         = "labels";
	public static final String P_CONSTANT      = "constants";
	public static final String P_EXPORT        = "exports";
	public static final String P_COMMAND       = "commands";
	public static final String P_PARAM_VAL     = "params";
	public static final String P_COMMENT       = "comments";
	public static final String P_PRE           = "pre";
	public static final String P_WRONG         = "wrong";
	public static final String P_CONST_CALC    = "constant calculations";
	public static final String P_KL            = "parens";
	public static final String P_STRING        = "strings";
	public static final String P_CHARS         = "charsets";

	public static final String TEXT_LABEL      = "labels:";
	public static final String TEXT_CONSTANT   = "constants:";
	public static final String TEXT_EXPORT     = "exports:";
	public static final String TEXT_COMMAND    = "commands:";
	public static final String TEXT_PARAM_VAL  = "params:";
	public static final String TEXT_COMMENT    = "comments:";
	public static final String TEXT_PRE        = "pre:";
	public static final String TEXT_WRONG      = "wrong:";
	public static final String TEXT_CONST_CALC = "const calcs:";
	public static final String TEXT_KL         = "parens:";
	public static final String TEXT_STRING     = "strings:";
	public static final String TEXT_CHARS      = "charsets:";

	public static final int INDEX_LABEL = 0;
	public static final int INDEX_CONSTANT = 1;
	public static final int INDEX_EXPORT = 2;
	public static final int INDEX_COMMAND = 3;
	public static final int INDEX_PARAM_VAL = 4;
	public static final int INDEX_COMMENT = 5;
	public static final int INDEX_PRE = 6;
	public static final int INDEX_WRONG = 7;
	public static final int INDEX_CONST_CALC = 8;
	public static final int INDEX_KL = 9;
	public static final int INDEX_STRING = 10;
	public static final int INDEX_CHARS = 11;
	public static final int PREFS_COUNT = 12;

	public static final String DEFAULT_LABEL      = StringConverter.asString(new RGB(0, 127, 127));
	public static final String DEFAULT_CONSTANT   = StringConverter.asString(new RGB(127, 0, 127));
	public static final String DEFAULT_EXPORT     = StringConverter.asString(new RGB(127, 0, 255));
	public static final String DEFAULT_COMMAND    = StringConverter.asString(new RGB(127, 127, 0));
	public static final String DEFAULT_PARAM_VAL  = StringConverter.asString(new RGB(191, 191, 32));
	public static final String DEFAULT_COMMENT    = StringConverter.asString(new RGB(127, 127, 127));
	public static final String DEFAULT_PRE        = StringConverter.asString(new RGB(127, 127, 255));
	public static final String DEFAULT_WRONG      = StringConverter.asString(new RGB(255, 0, 0));
	public static final String DEFAULT_CONST_CALC = StringConverter.asString(new RGB(127, 127, 255));
	public static final String DEFAULT_KL         = StringConverter.asString(new RGB(0, 127, 255));
	public static final String DEFAULT_STRING     = StringConverter.asString(new RGB(0, 191, 0));
	public static final String DEFAULT_CHARS      = StringConverter.asString(new RGB(63, 127, 63));
	
	private static final String[] PREFS;
	private static final String[] TEXTS;
	private static final String[] DEFAULTS;

	public static final List<String> PREFS_LIST;
	public static final List<String> TEXTS_LIST;
	
	static {
		PREFS                   = new String[PREFS_COUNT];  TEXTS                   = new String[PREFS_COUNT];  DEFAULTS                   = new String[PREFS_COUNT];
		PREFS[INDEX_LABEL]      = P_LABEL;                  TEXTS[INDEX_LABEL]      = TEXT_LABEL;               DEFAULTS[INDEX_LABEL]      = DEFAULT_LABEL;
		PREFS[INDEX_CONSTANT]   = P_CONSTANT;               TEXTS[INDEX_CONSTANT]   = TEXT_CONSTANT;            DEFAULTS[INDEX_CONSTANT]   = DEFAULT_CONSTANT;
		PREFS[INDEX_EXPORT]     = P_EXPORT;                 TEXTS[INDEX_EXPORT]     = TEXT_EXPORT;              DEFAULTS[INDEX_EXPORT]     = DEFAULT_EXPORT;
		PREFS[INDEX_COMMAND]    = P_COMMAND;                TEXTS[INDEX_COMMAND]    = TEXT_COMMAND;             DEFAULTS[INDEX_COMMAND]    = DEFAULT_COMMAND;
		PREFS[INDEX_PARAM_VAL]  = P_PARAM_VAL;              TEXTS[INDEX_PARAM_VAL]  = TEXT_PARAM_VAL;           DEFAULTS[INDEX_PARAM_VAL]  = DEFAULT_PARAM_VAL;
		PREFS[INDEX_COMMENT]    = P_COMMENT;                TEXTS[INDEX_COMMENT]    = TEXT_COMMENT;             DEFAULTS[INDEX_COMMENT]    = DEFAULT_COMMENT;
		PREFS[INDEX_PRE]        = P_PRE;                    TEXTS[INDEX_PRE]        = TEXT_PRE;                 DEFAULTS[INDEX_PRE]        = DEFAULT_PRE;
		PREFS[INDEX_WRONG]      = P_WRONG;                  TEXTS[INDEX_WRONG]      = TEXT_WRONG;               DEFAULTS[INDEX_WRONG]      = DEFAULT_WRONG;
		PREFS[INDEX_CONST_CALC] = P_CONST_CALC;             TEXTS[INDEX_CONST_CALC] = TEXT_CONST_CALC;          DEFAULTS[INDEX_CONST_CALC] = DEFAULT_CONST_CALC;
		PREFS[INDEX_KL]         = P_KL;                     TEXTS[INDEX_KL]         = TEXT_KL;                  DEFAULTS[INDEX_KL]         = DEFAULT_KL;
		PREFS[INDEX_STRING]     = P_STRING;                 TEXTS[INDEX_STRING]     = TEXT_STRING;              DEFAULTS[INDEX_STRING]     = DEFAULT_STRING;
		PREFS[INDEX_CHARS]      = P_CHARS;                  TEXTS[INDEX_CHARS]      = TEXT_CHARS;               DEFAULTS[INDEX_CHARS]      = DEFAULT_CHARS;
		PREFS_LIST = Collections.unmodifiableList(Arrays.asList(PREFS));
		TEXTS_LIST = Collections.unmodifiableList(Arrays.asList(TEXTS));
		for (int i = 0; i < PREFS_COUNT; i++) {
			assert PREFS[i] != null;
			assert TEXTS[i] != null;
			//can't check for default integer value, since it is a valid RGB value (black)
		}
	}
	//@formatter:on

	public static String[] getPrefs() {
		return PREFS.clone();
	}

	public static String[] getTexts() {
		return TEXTS.clone();
	}

	public static String[] getDefaults() {
		return DEFAULTS.clone();
	}
	
}
