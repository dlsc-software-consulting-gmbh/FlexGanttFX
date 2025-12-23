/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.licensing.util;

import com.smardec.license4j.LicenseUtil;

/**
 * Creates key pair in the specified file.
 */
public class CreateKeyPair {
	public static void main(String[] args) {
		try {
			LicenseUtil.createKeyPair("generated_keys.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}