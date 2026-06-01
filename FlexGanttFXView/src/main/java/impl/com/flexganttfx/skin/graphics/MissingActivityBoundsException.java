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
package impl.com.flexganttfx.skin.graphics;

import java.text.MessageFormat;

import com.flexganttfx.model.Activity;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;

public class MissingActivityBoundsException extends Exception {

	private static final long serialVersionUID = -6806986448373406748L;
	private final Activity activity;
	private final Row<?, ?, ?> row;
	private final int lineIndex;
	private final ActivityRenderer<?> renderer;

	public MissingActivityBoundsException(ActivityRenderer<?> renderer,
			Activity activity, Row<?, ?, ?> row, int lineIndex) {
		super(
				MessageFormat
						.format("The renderer of type {0} returned no bounds after drawing activity \"{1}\" in row \"{2}\" (line index = {3}).",
								renderer.getClass(), activity.getName(),
								row.getName(), lineIndex));

		this.renderer = renderer;
		this.activity = activity;
		this.row = row;
		this.lineIndex = lineIndex;
	}

	/**
	 * Returns the unresolved activity.
	 *
	 * @return the unresolved activity
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * Returns the row containing the unresolved activity.
	 *
	 * @return the row containing the unresolved activity
	 */
	public Row<?, ?, ?> getRow() {
		return row;
	}

	/**
	 * Returns the renderer that failed to resolve the activity bounds.
	 *
	 * @return the renderer that failed to resolve the activity bounds
	 */
	public ActivityRenderer<?> getRenderer() {
		return renderer;
	}

	/**
	 * Returns the line index of the unresolved activity.
	 *
	 * @return the line index
	 */
	public int getLineIndex() {
		return lineIndex;
	}
}
