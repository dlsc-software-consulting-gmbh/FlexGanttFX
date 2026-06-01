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

import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.repository.RepositoryEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.RowControlsParameter;
import com.flexganttfx.view.graphics.GraphicsBase.RowHeader;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import static javafx.scene.input.MouseEvent.MOUSE_ENTERED;
import static javafx.scene.input.MouseEvent.MOUSE_EXITED;

/**
 * Container representing one row inside a graphics skin. It combines the row canvas, optional
 * header content, controls, and flip editor pane for the row.
 */
public class RowPane<R extends Row<?, ?, ?>> extends StackPane {

    private final RowCanvas<R> canvas;

    private final FlipPane<R> flipPane;

    private final Label zoneIdLabel;

    private GraphicsBase<R> graphics;

    public RowPane(GraphicsBase<R> graphics) {
        this.graphics = graphics;

        getStyleClass().add("row-pane");

        setPrefWidth(0);
        setMinWidth(0);
        setPrefHeight(Row.DEFAULT_ROW_HEIGHT);

        flipPane = new FlipPane<>(this);

        canvas = new RowCanvas<>(graphics);
        canvas.widthProperty().bind(flipPane.widthProperty().add(graphics.canvasBufferProperty().multiply(2)));
        canvas.heightProperty().bind(flipPane.heightProperty());
        canvas.rowProperty().bind(rowProperty());
        StackPane.setAlignment(canvas, Pos.CENTER); // VERY IMPORTANT, we want buffer to the left AND the right

        zoneIdLabel = new Label("Zone ID");
        zoneIdLabel.getStyleClass().add("zone-id-label");
        zoneIdLabel.visibleProperty().bind(Bindings.and(Bindings.isNotNull(row), graphics.showZoneIdProperty()));

        flipPane.getFront().getChildren().add(canvas);
        flipPane.getFront().getChildren().add(zoneIdLabel);

        if (!Boolean.getBoolean("rowpane.no.clip")) {
            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(flipPane.widthProperty());
            clip.heightProperty().bind(flipPane.heightProperty());
            flipPane.setClip(clip);
        }

        StackPane.setAlignment(zoneIdLabel, Pos.TOP_RIGHT);

        InvalidationListener editorListener = evt -> {
            if (flipPane.isBackVisible()) {
                if (graphics.getRowsEditing().contains(getRow())) {
                    flipPane.setRow(getRow());
                } else {
                    flipPane.flipToFrontImmediately();
                }
            } else {
                if (graphics.getRowsEditing().contains(getRow())) {
                    flipPane.setRow(getRow());
                    flipPane.flipToBackImmediately();
                }
            }
        };

        rowProperty().addListener(editorListener);

        final EventHandler<MouseEvent> mouseEntered = evt -> maybeShowRowControls();

        final EventHandler<MouseEvent> mouseExited = evt -> {
            if (getRow() != null) {
                setControlsNode(null);
            }
        };

        rowProperty().addListener((observable, oldRow, newRow) -> {

            if (oldRow != null) {
                oldRow.linesManagerProperty().removeListener(weakRedrawListener);
                oldRow.lineCountProperty().removeListener(weakRedrawListener);
                oldRow.layoutProperty().removeListener(weakRedrawListener);
                oldRow.getRepository().removeEventHandler(weakRepositoryListener);
                oldRow.repositoryProperty().removeListener(weakRepositoryReplacedListener);
                oldRow.zoneIdProperty().removeListener(weakUpdateZoneIdListener);
                oldRow.getProperties().put("com.flexganttfx.row.showing", false);

                Bindings.unbindBidirectional(prefHeightProperty(), oldRow.heightProperty());
            }

            if (newRow != null) {
                newRow.linesManagerProperty().addListener(weakRedrawListener);
                newRow.lineCountProperty().addListener(weakRedrawListener);
                newRow.layoutProperty().addListener(weakRedrawListener);
                newRow.getRepository().addEventHandler(weakRepositoryListener);
                newRow.repositoryProperty().addListener(weakRepositoryReplacedListener);
                newRow.zoneIdProperty().addListener(weakUpdateZoneIdListener);

                newRow.getProperties().put("com.flexganttfx.row.showing", isVisible());

                /*
                 * Important call: first initialize the pref height with
                 * the current height of the new row.
                 */
                setPrefHeight(newRow.getHeight());
                Bindings.bindBidirectional(prefHeightProperty(), newRow.heightProperty());

                if (getControlsNode() != null) {
                    // we are currently showing row controls, let's recreate new ones so that they are valid for the new row
                    maybeShowRowControls();
                }
            } else {
                setPrefHeight(Row.DEFAULT_ROW_HEIGHT);
                setControlsNode(null);
            }

            updateZoneIdLabel();
        });

        addEventHandler(MOUSE_ENTERED, mouseEntered);
        addEventHandler(MOUSE_EXITED, mouseExited);

        RowHeader<R> rowHeader = null;
        final Callback<GraphicsBase<R>, RowHeader<R>> rowHeaderFactory = graphics.getRowHeaderFactory();
        if (rowHeaderFactory != null) {
            rowHeader = rowHeaderFactory.call(graphics);
            if (rowHeader != null) {

                // the width of the row header is determined by an outside property
                rowHeader.prefWidthProperty().bind(graphics.rowHeadersWidthProperty());
                rowHeader.setMinWidth(Region.USE_PREF_SIZE);
                rowHeader.setMaxWidth(Region.USE_PREF_SIZE);

                // the row header is always as high as the row
                rowHeader.prefHeightProperty().bind(heightProperty());
                rowHeader.setMinHeight(Region.USE_PREF_SIZE);
                rowHeader.setMaxHeight(Region.USE_PREF_SIZE);

                rowHeader.visibleProperty().bind(graphics.showRowHeadersProperty());
                rowHeader.managedProperty().bind(graphics.showRowHeadersProperty());

                // row header displays the same row as the row pane / canvas
                rowHeader.itemProperty().bind(rowProperty());
            }
        }

        if (rowHeader != null) {
            HBox box = new HBox(rowHeader, flipPane);
            box.setFillHeight(true);
            box.setMinSize(0,0); // super important, otherwise we get flickering UI (see CovidApp)
            HBox.setHgrow(flipPane, Priority.ALWAYS);
            getChildren().add(box);
        } else {
            getChildren().add(flipPane);
        }
    }

    private void maybeShowRowControls() {
        if (getRow() != null && !flipPane.isBackVisible()) {
            Callback<RowControlsParameter<R>, Node> controlsFactory = graphics.getRowControlsFactory();
            if (controlsFactory != null) {
                RowControlsParameter<R> param = new RowControlsParameter<>(graphics, getRow());
                Node controlsNode = controlsFactory.call(param);
                setControlsNode(controlsNode);
            }
        }
    }

    /**
     * Returns the graphics control.
     *
     * @return the graphics control
     */
    public final GraphicsBase<R> getGraphics() {
        return graphics;
    }

    private Node controlsNode;

    private void setControlsNode(Node node) {
        if (controlsNode != null) {
            getChildren().remove(controlsNode);
        }

        this.controlsNode = node;

        if (controlsNode != null) {
            StackPane.setAlignment(controlsNode, Pos.TOP_RIGHT);
            if (controlsNode instanceof Region) {
                ((Region) controlsNode).setMinSize(0, 0);
            }
            getChildren().add(controlsNode);
        }
    }

    /**
     * Returns the controls node.
     *
     * @return the controls node
     */
    public final Node getControlsNode() {
        return controlsNode;
    }

    private final ObjectProperty<R> row = new SimpleObjectProperty<>(this, "row");

    /**
     * The row property.
     *
     * @return the row property
     */
    public final ObjectProperty<R> rowProperty() {
        return row;
    }

    public final void setRow(R row) {
        rowProperty().set(row);
    }

    public final R getRow() {
        return rowProperty().get();
    }

    /**
     * Returns the row canvas.
     *
     * @return the row canvas
     */
    public final RowCanvas<R> getCanvas() {
        return canvas;
    }

    /**
     * Starts editing the row.
     */
    public final void startEditing() {
        flipPane.setRow(getRow());
        if (graphics.isAnimateRowEditor()) {
            getScene().setCamera(new PerspectiveCamera());
            flipPane.flipToBack();
        } else {
            flipPane.flipToBackImmediately();
        }
    }

    /**
     * Stops editing the row.
     */
    public final void stopEditing() {
        if (graphics.isAnimateRowEditor()) {
            flipPane.flipToFront();
        } else {
            flipPane.flipToFrontImmediately();
        }
    }

    private void updateZoneIdLabel() {
        R row = getRow();
        if (row != null) {
            ZoneId zoneId = row.getZoneId();
            zoneIdLabel.setText(zoneId.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()));
        } else {
            zoneIdLabel.setText("");
        }
    }

    private final InvalidationListener redrawListener = it -> getCanvas().requestRedraw("row pane's redraw listener was called");

    private final InvalidationListener weakRedrawListener = new WeakInvalidationListener(redrawListener);

    private final InvalidationListener updateZoneIdListener = evt -> updateZoneIdLabel();

    private final InvalidationListener weakUpdateZoneIdListener = new WeakInvalidationListener(updateZoneIdListener);

    private final EventHandler<RepositoryEvent> repositoryListener = evt -> {
        /*
         * Do not redraw immediately after each repository event if automatic redraw
         * is set to false. Can be used to fine-tune application when adding a lot of data
         * in a batch.
         */
        if (graphics.isAutomaticRedraw()) {
            getCanvas().requestRedraw("row pane's repository listener fired");
        }
    };

    private final WeakEventHandler<RepositoryEvent> weakRepositoryListener = new WeakEventHandler<>(repositoryListener);

    private final ChangeListener<ActivityRepository<?>> repositoryReplacedListener = (observable, oldRepository, newRepository) -> {
        if (oldRepository != null) {
            oldRepository.removeEventHandler(weakRepositoryListener);
        }
        if (newRepository != null) {
            newRepository.addEventHandler(weakRepositoryListener);
        }
    };

    private final WeakChangeListener<ActivityRepository<?>> weakRepositoryReplacedListener = new WeakChangeListener<>(repositoryReplacedListener);
}
