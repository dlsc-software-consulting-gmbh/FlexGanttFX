/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.view.util;

import javafx.scene.control.Control;

import java.net.URL;
import java.util.Objects;

/**
 * The super class of most controls found inside FlexGanttFX. Used to support
 * licensing and to improve stylesheet lookup performance.
 *
 * @since 1.3
 */
public abstract class FlexGanttFXControl extends Control {

	/**
	 * Constructs a new control.
	 */
	protected FlexGanttFXControl() {
	}

	private String stylesheet;
	private String stylesheetAtlantaFX;

	/**
	 * A helper method that ensures that the resource based lookup of the user
	 * agent stylesheet only happens once per theme type. When an AtlantaFX theme
	 * is active it tries to load {@code <name>-atlantafx.css} from the same
	 * package as {@code clazz} and falls back to the regular {@code fileName}
	 * when no AtlantaFX variant exists.
	 *
	 * @param clazz
	 *            the clazz used for the resource lookup
	 * @param fileName
	 *            the name of the default user agent stylesheet (e.g. {@code "gantt.css"})
	 * @return the external form of the user agent stylesheet (the path)
	 * @since 1.3
	 */
	protected String getUserAgentStylesheet(Class<?> clazz, String fileName) {
		if (ThemingUtil.isAtlantaFXActive(getScene())) {
			if (stylesheetAtlantaFX == null) {
				String afxFileName = toAtlantaFXFileName(fileName);
				URL afxResource = clazz.getResource(afxFileName);
				if (afxResource != null) {
					stylesheetAtlantaFX = afxResource.toExternalForm();
				} else {
					stylesheetAtlantaFX = resolveDefaultStylesheet(clazz, fileName);
				}
			}
			return stylesheetAtlantaFX;
		}
		return resolveDefaultStylesheet(clazz, fileName);
	}

	private String resolveDefaultStylesheet(Class<?> clazz, String fileName) {
		if (stylesheet == null) {
			stylesheet = Objects.requireNonNull(clazz.getResource(fileName)).toExternalForm();
		}
		return stylesheet;
	}

	private static String toAtlantaFXFileName(String fileName) {
		int dot = fileName.lastIndexOf('.');
		if (dot >= 0) {
			return fileName.substring(0, dot) + "-atlantafx" + fileName.substring(dot);
		}
		return fileName + "-atlantafx";
	}
}
