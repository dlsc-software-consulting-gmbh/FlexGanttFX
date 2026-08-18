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
package com.flexganttfx.extras;

import com.flexganttfx.extras.util.Messages;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.view.util.FlexGanttFXControl;
import impl.com.flexganttfx.extras.skin.VirtualGridControlSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

import static java.util.Objects.requireNonNull;

/**
 * A control used to select a {@link VirtualGrid} from a list of possible
 * virtual grids. The selected grid is normally passed on to the Gantt chart so
 * that activities snap to it while being edited.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * VirtualGridControl control = new VirtualGridControl();
 * control.getGrids().setAll(
 *     new VirtualGrid<>("Hours", ChronoUnit.HOURS, 1),
 *     new VirtualGrid<>("Days", ChronoUnit.DAYS, 1));
 * control.setShowNoGridOption(true);
 *
 * control.valueProperty().addListener(it ->
 *     gantt.getGraphics().setVirtualGrid(control.getValue()));
 * }</pre>
 *
 * @see VirtualGrid
 *
 * @since 1.0
 */
public class VirtualGridControl extends FlexGanttFXControl {

    /**
     * Constructs a new virtual grid control.
     *
     * @since 1.0
     */
    public VirtualGridControl() {
        getStyleClass().add("virtual-grid-control");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new VirtualGridControlSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return super.getUserAgentStylesheet(VirtualGridControl.class, "virtualgrid.css");
    }

    private final StringProperty noGridText = new SimpleStringProperty(this,
            "noGridText", Messages.getString("VirtualGridControl.NO_GRID"));

    /**
     * The noGridText property. Stores the label text shown for the "no grid"
     * option button.
     *
     * @return the noGridText property
     * @since 1.3
     */
    public final StringProperty noGridTextProperty() {
        return noGridText;
    }

    public final void setNoGridText(String text) {
        requireNonNull(text);
        noGridText.set(text);
    }

    public final String getNoGridText() {
        return noGridText.get();
    }

    private final BooleanProperty showNoGridOption = new SimpleBooleanProperty(
            this, "showNoGridOption", true);

    /**
     * The showNoGridOption property. Controls whether the "no grid" option
     * button is shown to the user.
     *
     * @return the showNoGridOption property
     * @since 1.3
     */
    public final BooleanProperty showNoGridOptionProperty() {
        return showNoGridOption;
    }

    public final boolean isShowNoGridOption() {
        return showNoGridOption.get();
    }

    public final void setShowNoGridOption(boolean show) {
        showNoGridOption.set(show);
    }

    private final ObjectProperty<VirtualGrid<?>> value = new SimpleObjectProperty<>(
            this, "value");

    /**
     * The value property. Stores the currently selected virtual grid, or
     * {@code null} if no grid is active.
     *
     * @return the value property
     * @since 1.0
     */
    public final ObjectProperty<VirtualGrid<?>> valueProperty() {
        return value;
    }

    public final VirtualGrid<?> getValue() {
        return valueProperty().get();
    }

    public final void setValue(VirtualGrid<?> grid) {
        valueProperty().set(grid);
    }

    private final ObservableList<VirtualGrid<?>> grids = FXCollections
            .observableArrayList();

    /**
     * Returns the list of possible virtual grid values.
     *
     * @return the possible virtual grids
     * @since 1.0
     */
    public final ObservableList<VirtualGrid<?>> getGrids() {
        return grids;
    }
}
