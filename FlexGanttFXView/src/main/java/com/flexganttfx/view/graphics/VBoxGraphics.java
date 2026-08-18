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
import impl.com.flexganttfx.skin.graphics.VBoxGraphicsSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * A specialization of {@link GraphicsBase} that displays several rows inside a
 * {@link VBox}. Rows will be laid out based on their {@link VBox} constraints
 * (see for example {@link VBox#setVgrow(javafx.scene.Node, Priority)}).
 *
 * @param <R>
 *            the type of the rows displayed by this view
 * @since 1.0
 */
public class VBoxGraphics<R extends Row<?, ?, ?>> extends GraphicsBase<R> {

	/**
	 * Creates a new VBox graphics view and configures its default layout behavior.
	 */
	public VBoxGraphics() {
		vbox = createVBox();

		getStyleClass().add("vbox-graphics");

		setPriorityCallback(row -> Priority.ALWAYS);
	}

	/**
	 * Creates the default skin used by this graphics view.
	 *
	 * @return the default skin
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new VBoxGraphicsSkin<>(this);
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

	private final VBox vbox;

	/**
	 * Returns the {@link VBox} instance that is being used by this view.
	 *
	 * @return the {@link VBox}
	 * @since 1.0
	 */
	public final VBox getVBox() {
		return vbox;
	}

	/**
	 * Creates the {@link VBox} used by this view. This method can be overriden
	 * to plug in an application-specific specialization of VBox.
	 *
	 * @return the VBox instance that will be used by this graphics view
	 * @since 1.0
	 */
	protected VBox createVBox() {
		return new VBox();
	}

	private final ObjectProperty<Callback<R, Priority>> priorityCallback = new SimpleObjectProperty<>(
			this, "priorityCallback");

	/**
	 * Returns the property used to store a callback for looking up a resize
	 * {@link Priority} for each row inside the {@link VBox}.
	 *
	 * @return a callback for looking up resize {@link Priority} values for each
	 *         row
	 * @since 1.0
	 */
	public final ObjectProperty<Callback<R, Priority>> priorityCallbackProperty() {
		return priorityCallback;
	}

	/**
	 * Sets the value of {@link #priorityCallbackProperty()}.
	 *
	 * @param callback
	 *            the callback used for looking up resize {@link Priority}
	 *            values
	 * @since 1.0
	 */
	public final void setPriorityCallback(Callback<R, Priority> callback) {
		priorityCallbackProperty().set(callback);
	}

	/**
	 * Returns the value of {@link #priorityCallbackProperty()}.
	 *
	 * @return the callback used for looking up resize {@link Priority} values
	 * @since 1.0
	 */
	public final Callback<R, Priority> getPriorityCallback() {
		return priorityCallbackProperty().get();
	}
}
