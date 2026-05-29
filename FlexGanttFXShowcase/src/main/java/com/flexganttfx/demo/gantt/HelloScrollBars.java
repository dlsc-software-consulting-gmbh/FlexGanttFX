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
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.demo.HelloActivity;
import com.flexganttfx.demo.HelloRow;
import com.flexganttfx.model.Layer;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.util.Messages;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.*;

public class HelloScrollBars extends FlexGanttFXSample {

    private GanttChart<HelloRow> gc;

    @Override
    public void dispose() {
        super.dispose();
        gc = null;
    }

    @Override
    protected GanttChart<?> createGanttChart() {
        HelloRow root = new HelloRow("root");

        Layer layer = new Layer("layer");

        gc = new GanttChart<>();
        gc.setAutoHideScrollBar(false);
        gc.setScrollBarType(GanttChartBase.ScrollBarType.FIXED_HORIZON);
        gc.getLayers().add(layer);
        gc.getTimeline().getModel().setHorizonStartTime(ZonedDateTime.now().minusWeeks(1).toInstant());
        gc.getTimeline().getModel().setHorizonEndTime(ZonedDateTime.now().plusYears(2).toInstant());

        HelloActivity activity = new HelloActivity();
        activity.setStartTime(Instant.now());
        activity.setEndTime(Instant.now().plus(Duration.ofDays(7)));
        root.addActivity(layer, activity);

        for (int i = 0; i < 200; i++) {
            HelloRow row = new HelloRow("Row " + (i + 1));
            root.getChildren().add(row);
        }

        gc.setRoot(root);

        return gc;
    }

    @Override
    public String getSampleName() {
        return "Scroll Bar Types";
    }

    @Override
    public String getSampleDescription() {
        return "An example showing the two different scrollbar types: FIXED_HORIZON to scroll between a given start date and end date. INFINITE for scrolling endlessly into the future or the past (except if a horizon start date limits the past). Additionally applications can specify whether the scrollbar should auto-hide or not when not used.";
    }

    @Override
    public Node getControlPanel() {
        ComboBox<GanttChartBase.ScrollBarType> typeComboBox = new ComboBox<>();
        typeComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(GanttChartBase.ScrollBarType type) {
                if (type == null) return "";
                switch (type) {
                    case FIXED_HORIZON:
                        return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_FIXED_HORIZON");
                    case INFINITE:
                        return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_INFINITE");
                    case NONE:
                    default:
                        return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_NONE");
                }
            }

            @Override
            public GanttChartBase.ScrollBarType fromString(String string) {
                return null;
            }
        });
        typeComboBox.getItems().setAll(GanttChartBase.ScrollBarType.values());
        typeComboBox.valueProperty().bindBidirectional(gc.scrollBarTypeProperty());
        typeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if (oldType != null && newType != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_DIALOG_TITLE"));
                alert.setHeaderText(typeComboBox.getConverter().toString(newType));
                alert.setContentText(scrollBarTypeDescription(newType));
                alert.initOwner(typeComboBox.getScene().getWindow());
                alert.show();
            }
        });

        CheckBox autoHideScrollBar = new CheckBox("Auto-Hide Scrollbars");
        autoHideScrollBar.selectedProperty().bindBidirectional(gc.autoHideScrollBarProperty());

        DatePicker startPicker = new DatePicker();
        startPicker.setValue(LocalDate.ofInstant(gc.getTimeline().getModel().getHorizonStartTime(), ZoneId.systemDefault()));
        startPicker.valueProperty().addListener(it -> gc.getTimeline().getModel().setHorizonStartTime(ZonedDateTime.of(startPicker.getValue(), LocalTime.MIN, ZoneId.systemDefault()).toInstant()));
        startPicker.visibleProperty().bind(gc.scrollBarTypeProperty().isEqualTo(GanttChartBase.ScrollBarType.FIXED_HORIZON));
        startPicker.managedProperty().bind(gc.scrollBarTypeProperty().isEqualTo(GanttChartBase.ScrollBarType.FIXED_HORIZON));

        DatePicker endPicker = new DatePicker();
        endPicker.setValue(LocalDate.ofInstant(gc.getTimeline().getModel().getHorizonEndTime(), ZoneId.systemDefault()));
        endPicker.valueProperty().addListener(it -> gc.getTimeline().getModel().setHorizonEndTime(ZonedDateTime.of(endPicker.getValue(), LocalTime.MAX, ZoneId.systemDefault()).toInstant()));
        endPicker.visibleProperty().bind(gc.scrollBarTypeProperty().isEqualTo(GanttChartBase.ScrollBarType.FIXED_HORIZON));
        endPicker.managedProperty().bind(gc.scrollBarTypeProperty().isEqualTo(GanttChartBase.ScrollBarType.FIXED_HORIZON));

        HBox box = new HBox(10, typeComboBox, autoHideScrollBar, startPicker, endPicker);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        return box;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private static String scrollBarTypeDescription(GanttChartBase.ScrollBarType type) {
        switch (type) {
            case FIXED_HORIZON:
                return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_FIXED_HORIZON_DESCRIPTION");
            case INFINITE:
                return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_INFINITE_DESCRIPTION");
            case NONE:
            default:
                return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_NONE_DESCRIPTION");
        }
    }
}
