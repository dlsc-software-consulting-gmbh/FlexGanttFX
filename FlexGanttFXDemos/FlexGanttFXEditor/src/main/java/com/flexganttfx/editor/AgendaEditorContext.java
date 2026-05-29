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
package com.flexganttfx.editor;

import java.time.Duration;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.graphics.SingleRowGraphics;

/**
 * The interface required for any control that wants to work together with the
 * {@link AgendaController} and the {@link AgendaConflictResolver}. This
 * interface allows the agenda support to work inside the standalone agenda
 * editor and also in the regular Gantt chart.
 * 
 * @param <R>
 *            the rows used by the editor
 */
public interface AgendaEditorContext<R extends Row<?, ?, ?>> {

	/**
	 * Returns the graphics view used by the context. The agenda editor uses the
	 * {@link SingleRowGraphics} while regular Gantt charts use the
	 * {@link ListViewGraphics}.
	 * 
	 * @return the graphics view used by the context
	 */
	GraphicsBase<R> getGraphics();

	/**
	 * Used for debugging purposes only. Allows the context to disable the
	 * restore step after a conflict resolution.
	 */
	BooleanProperty restoreProperty();

	/**
	 * Controls the delay before the conflict resolution starts. A delay allows
	 * the user to drag entries across several days without triggering any
	 * changes.
	 */
	LongProperty changeDelayProperty();

	/**
	 * Controls whether agenda entries are allowed to overlap each other or not.
	 * The conflict resolution will run only if overlapping is forbidden.
	 */
	BooleanProperty allowOverlappingProperty();

	/**
	 * Determines if the possible paste locations will be shown by the
	 * {@link AgendaEditorBackgroundLayer}.
	 * 
	 * @return
	 */
	BooleanProperty showPasteLocationsProperty();

	/**
	 * The inital duration given to new agenda entries when the user performs a
	 * double click.
	 */
	ObjectProperty<Duration> initialEntryDurationProperty();

	/**
	 * Returns the possible new time intervals for the given copied activities.
	 * 
	 * @param x the x-coordinate of the mouse event
	 * @param y the y-coordinate of the mouse event
	 */
	List<TimeInterval> getPasteLocations(double x, double y,
			List<ActivityRef<?>> copiedActivities);
}
