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
package com.flexganttfx.view.graphics;

import com.flexganttfx.model.Row;
import impl.com.flexganttfx.skin.graphics.ListViewGraphicsSkin;
import impl.com.flexganttfx.skin.graphics.RowPane;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A specialization of {@link GraphicsBase} that uses a {@link ListView} to
 * display a list of rows. The user can use the arrow up and down keys for
 * scrolling vertically and arrows left and right for scrolling horizontally.
 *
 * @param <R>
 *            the type of the rows displayed inside the list view
 * @since 1.0
 */
public class ListViewGraphics<R extends Row<?, ?, ?>> extends GraphicsBase<R> {

	public ListViewGraphics() {
		getStyleClass().add("list-view-graphics");

		listView = createListView();
		listView.getStyleClass().add("graphics-list-view");

		listView.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			if (e.getCode() == KeyCode.UP) {
				scrollUp();
				e.consume();
			} else if (e.getCode() == KeyCode.DOWN) {
				scrollDown();
				e.consume();
			}
		});

		setPrefSize(700, 350);

		enableRowResizingProperty().bind(Bindings.createBooleanBinding(() -> getFixedCellSize() == -1, fixedCellSizeProperty()));
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new ListViewGraphicsSkin<>(this);
	}

	private final ListView<R> listView;

	/**
	 * Returns the list view control used by this graphics view.
	 *
	 * @return the list view
	 * @since 1.0
	 */
	public final ListView<R> getListView() {
		return listView;
	}

	/**
	 * Creates the list view instance to be used by this graphics view. This
	 * method can be overridden to return a specialization of {@link ListView}.
	 *
	 * @return a list view
	 * @since 1.0
	 */
	protected ListView<R> createListView() {
		ListView<R> listView =new ListView<>();
		listView.getStyleClass().add("striped"); // atlantafx
		return listView;
	}

	@Override
	public List<RowPane<R>> getRowPanes() {
		ListView<R> listView = getListView();
		return listView.lookupAll(".row-pane").stream().map(node -> (RowPane<R>) node).collect(Collectors.toList());
	}

	private final DoubleProperty autoscrollProximity = new SimpleDoubleProperty(
			this, "autoscrollProximity", 20);

	/**
	 * This property defines the distance from the viewport borders (in pixels)
	 * where the viewport will automatically start scrolling, either
	 * horizontally or vertically. This behaviour is needed when the user
	 * performs a drag operation and needs to reach an area inside the graphics
	 * that is currently not visible. Setting this value to 0 disables the
	 * automatic scrolling feature.
	 *
	 * @return the property used to control the autoscroll proximity
	 *
	 * @since 1.3
	 */
	public final DoubleProperty autoscrollProximityProperty() {
		return autoscrollProximity;
	}

	/**
	 * Returns the value of {@link #autoscrollProximityProperty()}.
	 *
	 * @return the distance in pixels to the viewport borders
	 * @since 1.3
	 */
	public final double getAutoscrollProximity() {
		return autoscrollProximityProperty().get();
	}

	/**
	 * Sets the value of {@link #autoscrollProximityProperty()}.
	 *
	 * @param value
	 *            the distance in pixels to the viewport borders
	 * @since 1.3
	 */
	public final void setAutoscrollProximity(double value) {
		autoscrollProximityProperty().set(value);
	}

	private final DoubleProperty scrollValue = new SimpleDoubleProperty(this,
			"scrollValue", Row.DEFAULT_ROW_HEIGHT) {
		@Override
		public void set(double newValue) {
			if (newValue < 1) {
				throw new IllegalArgumentException(
						"scroll value must be larger than or equal to 0 but was "
								+ newValue);
			}
			super.set(newValue);
		}
	};

	/**
	 * Stores the number of pixels that the list view will scroll when the user
	 * uses the arrow up and down keys.
	 *
	 * @see #scrollDown()
	 * @see #scrollUp()
	 *
	 * @return the scroll value
	 * @since 1.3
	 */
	public final DoubleProperty scrollValueProperty() {
		return scrollValue;
	}

	/**
	 * Returns the value of {@link #scrollValueProperty()}.
	 *
	 * @return the scroll value (in pixels)
	 * @since 1.3
	 */
	public final double getScrollValue() {
		return scrollValueProperty().get();
	}

	/**
	 * Sets the value of {@link #scrollValueProperty()}.
	 *
	 * @param pixels
	 *            the scroll value (in pixels)
	 * @since 1.3
	 */
	public final void setScrollValue(double pixels) {
		scrollValueProperty().set(pixels);
	}

	/**
	 * Makes the list view scroll up.
	 *
	 * @see #setScrollValue(double)
	 * @see #scrollValueProperty()
	 * @since 1.3
	 */
	public final void scrollUp() {
		getVirtualFlow().scrollPixels(-getScrollValue());
	}

	/**
	 * Makes the list view scroll down.
	 *
	 * @see #setScrollValue(double)
	 * @see #scrollValueProperty()
	 * @since 1.3
	 */
	public final void scrollDown() {
		getVirtualFlow().scrollPixels(getScrollValue());
	}

	private VirtualFlow<?> getVirtualFlow() {
		return (VirtualFlow<?>) this.lookup("VirtualFlow");
	}
}
