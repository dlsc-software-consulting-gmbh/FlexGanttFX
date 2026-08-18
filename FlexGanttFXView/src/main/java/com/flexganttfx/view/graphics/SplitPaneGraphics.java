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
package com.flexganttfx.view.graphics;

import com.flexganttfx.model.Row;
import impl.com.flexganttfx.skin.graphics.RowPane;
import impl.com.flexganttfx.skin.graphics.SplitPaneGraphicsSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * A specialization of {@link GraphicsBase} that displays several rows inside a
 * {@link SplitPane}. This way each row can be easily resized via the dividers
 * provided by the {@link SplitPane}.
 *
 * @param <R>
 *            the type of the rows
 * @since 1.0
 */
public class SplitPaneGraphics<R extends Row<?, ?, ?>> extends GraphicsBase<R> {

	/**
	 * Creates a new split pane graphics view.
	 */
	public SplitPaneGraphics() {
		splitPane = createSplitPane();
		getStyleClass().add("split-pane-graphics");
	}

	/**
	 * Creates the default skin used by this graphics view.
	 *
	 * @return the default skin
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new SplitPaneGraphicsSkin<>(this);
	}

	private final List<RowPane<R>> rowPanes = new ArrayList<>();

	/**
	 * Returns the row panes currently managed by this graphics view.
	 *
	 * @return the row panes
	 */
	@Override
	public List<RowPane<R>> getRowPanes() {
		return rowPanes;
	}

	private final SplitPane splitPane;

	/**
	 * Returns the {@link SplitPane} instance used by this graphics view.
	 *
	 * @return the splitpane
	 * @since 1.0
	 */
	public final SplitPane getSplitPane() {
		return splitPane;
	}

	/**
	 * Creates the {@link SplitPane} used by this view. This method can be
	 * overridden to provide an application-specific specialization of
	 * {@link SplitPane}.
	 *
	 * @return the split pane control used by the view
	 * @since 1.0
	 */
	protected SplitPane createSplitPane() {
		return new SplitPane();
	}

	private final ObjectProperty<Callback<R, Boolean>> resizableCallback = new SimpleObjectProperty<>(this, "resizableCallback");

	/**
	 * Returns the property used to store a callback that provides the resizing
	 * behaviour of each row (see
	 * {@link SplitPane#setResizableWithParent(javafx.scene.Node, Boolean)}).
	 *
	 * @return the property used to store the resizable callback
	 * @since 1.0
	 */
	public final ObjectProperty<Callback<R, Boolean>> resizableCallbackProperty() {
		return resizableCallback;
	}

	/**
	 * Sets the value of the {@link #resizableCallbackProperty()}.
	 *
	 * @param callback
	 *            the callback to be used for looking up resize behaviour for
	 *            each row
	 * @since 1.0
	 */
	public final void setResizableCallback(Callback<R, Boolean> callback) {
		resizableCallbackProperty().set(callback);
	}

	/**
	 * Returns the value of the {@link #resizableCallbackProperty()}.
	 *
	 * @return the callback
	 * @since 1.0
	 */
	public final Callback<R, Boolean> getResizableCallback() {
		return resizableCallbackProperty().get();
	}
}
