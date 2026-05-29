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
package com.flexganttfx.demo;

import javafx.scene.Node;
import javafx.stage.Stage;

/**
 */
public interface Sample {

    /**
     * A short, most likely single-word, name to show to the user - e.g. "CheckBox"
     */
    String getSampleName();

    /**
     * A short, multiple sentence description of the sample.
     */
    String getSampleDescription();

    /**
     * Returns the name of the project that this sample belongs to (e.g. 'JFXtras'
     * or 'ControlsFX').
     */
    String getProjectName();

    /**
     * Returns the version of the project that this sample belongs to (e.g. '1.0.0')
     */
    String getProjectVersion();

    /**
     * Returns the main sample panel.
     */
    Node getPanel(final Stage stage);

    /**
     * Returns the panel to display to the user that allows for manipulating
     * the sample.
     */
    Node getControlPanel();

    /**
     * Provides a place to dispose of any resources when sample is deselected
     */
    void dispose();


    /**
     * Returns divider position to use for split between main panel and control panel
     */
    double getControlPanelDividerPosition();

    /**
     * A full URL to the javadoc for the API being demonstrated in this sample.
     */
    String getJavaDocURL();

    /**
     * Returns URL for control's stylesheet.
     */
    String getControlStylesheetURL();

    /**
     * A full URL to a sample source code, which is assumed to be in java.
     */
    String getSampleSourceURL();

    /**
     * if true, this sample is shown to users, if false it is not.
     */
    boolean isVisible();

    /**
     * Returns true if this sample requires an AtlantaFX theme to function correctly.
     * When true, the sample will be hidden from the sidebar when the Modena theme is active.
     */
    default boolean requiresAtlantaFX() {
        return false;
    }

}
