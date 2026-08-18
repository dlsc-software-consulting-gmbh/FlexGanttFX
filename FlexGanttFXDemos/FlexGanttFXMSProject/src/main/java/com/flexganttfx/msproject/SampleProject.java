/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing.html>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://www.gnu.org/licenses/agpl-3.0.html>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
 */
package com.flexganttfx.msproject;

import net.sf.mpxj.ProjectFile;

import java.util.function.Supplier;

/**
 * A named sample project entry shown in the project-selector ComboBox.
 * The {@link #getFactory()} supplier creates a fresh {@link ProjectFile}
 * on each call so the chart always gets a clean instance.
 */
public final class SampleProject {

    private final String name;
    private final Supplier<ProjectFile> factory;

    public SampleProject(String name, Supplier<ProjectFile> factory) {
        this.name    = name;
        this.factory = factory;
    }

    public String getName() {
        return name;
    }

    public Supplier<ProjectFile> getFactory() {
        return factory;
    }

    @Override
    public String toString() {
        return name;
    }
}
