/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;

import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

public class RowCell<R extends Row<?, ?, ?>> extends ListCell<R> {

    private static final String DEFAULT_STYLE_CLASS = "row-cell";

    private final RowPane<R> rowPane;

    public RowCell(GraphicsBase<R> graphics) {
        requireNonNull(graphics);

        this.rowPane = new RowPane<>(graphics);

        graphics.getRowPanes().add(rowPane);

        getStyleClass().add(DEFAULT_STYLE_CLASS);

        rowPane.prefWidthProperty().bind(widthProperty());
        rowPane.rowProperty().bind(itemProperty());

        /*
         * The pref height of the row pane is bound to the height of the row. So
         * when the row pane grows the cell will also grow.
         */
        Bindings.bindBidirectional(prefHeightProperty(), rowPane.prefHeightProperty());

        /*
         * We might have to redraw activity links.
         */
        heightProperty().addListener((obs, oldHeight, newHeight) -> ((GraphicsBaseSkin<?, ?>) graphics.getSkin()).getLinksCanvas().requestRedraw("height of row " + (getItem() != null ? getItem().getName() : "(empty row)") + " changed from " + oldHeight + " to " + newHeight));

        setPrefWidth(0);
        setGraphic(rowPane);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        visibleProperty().addListener(it -> {
            boolean visible = isVisible();

            Row<?, ?, ?> row = getItem();
            if (row != null) {
                row.getProperties().put("com.flexganttfx.row.showing", visible);
            }

            if (visible) {
                if (LoggingDomain.RENDERING.isLoggable(Level.FINE)) {
                    LoggingDomain.RENDERING.fine("redrawing canvas because of row cell visibility changing to true");
                }
                rowPane.getCanvas().requestRedraw("row cell became visible");
            }
        });
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        rowPane.resizeRelocate(0, 0, getWidth(), getHeight());
    }

    public final RowPane<R> getRowPane() {
        return rowPane;
    }
}
