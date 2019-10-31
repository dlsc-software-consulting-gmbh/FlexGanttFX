/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A utility class used for translating strings of the framework. The resource
 * bundle has to be located in the com.flexganttfx.view package and must be
 * called "messages.properties". Applications can customize the translations but
 * adding the same package to their codebase.
 */
public class Messages {
	private static final String BUNDLE_NAME = "com.flexganttfx.view.util.messages";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	/**
	 * Returns a translation for the given key.
	 *
	 * @param key
	 *            the i18n key
	 * @return the translation
	 */
	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
