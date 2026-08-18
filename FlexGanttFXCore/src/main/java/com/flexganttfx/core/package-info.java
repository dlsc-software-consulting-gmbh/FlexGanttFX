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
/**
 * Core classes used for logging, general framework information, and string utilities.
 * This package does not depend on JavaFX and is used by all other FlexGanttFX modules.
 *
 * <h2>Key Types</h2>
 * <ul>
 * <li>{@link com.flexganttfx.core.LoggingDomain} - the predefined loggers used by the
 * framework. Applications should use these loggers instead of creating their own.</li>
 * <li>{@link com.flexganttfx.core.LoggingFormatter} - a compact formatter for the
 * output of the framework loggers.</li>
 * <li>{@link com.flexganttfx.core.FlexGanttFX} - general information about the
 * framework, for example its version number.</li>
 * <li>{@link com.flexganttfx.core.StringUtils} - a small set of null-safe string
 * utility methods.</li>
 * </ul>
 *
 * @since 1.0
 */
package com.flexganttfx.core;

