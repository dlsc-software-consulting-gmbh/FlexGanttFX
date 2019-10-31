/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.extras.skin;

import com.flexganttfx.extras.LayersView;
import com.flexganttfx.extras.util.Messages;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class LayersViewSkin<R extends Row<?, ?, ?>> extends
		SkinBase<LayersView<R>> {

	private GridPane gridPane;

	private final InvalidationListener rebuildListener = observable -> buildControls();

	private final WeakInvalidationListener weakRebuildListener = new WeakInvalidationListener(rebuildListener);

	public LayersViewSkin(LayersView<R> view) {
		super(view);

		gridPane = new GridPane();
		gridPane.setAlignment(Pos.TOP_CENTER);
		gridPane.getStyleClass().add("layer-controls");
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		buildControls();

		view.graphicsProperty().addListener(
				new ChangeListener<GraphicsBase<R>>() {
					@Override
					public void changed(
							ObservableValue<? extends GraphicsBase<R>> observable,
							GraphicsBase<R> oldGraphics,
							GraphicsBase<R> newGraphics) {
						if (oldGraphics != null) {
							oldGraphics.getLayers().removeListener(
									weakRebuildListener);
						}

						if (newGraphics != null) {
							newGraphics.getLayers()
									.addListener(weakRebuildListener);
						}

						buildControls();
					}
				});

		GraphicsBase<R> graphics = view.getGraphics();
		if (graphics != null) {
			graphics.getLayers().addListener(weakRebuildListener);
		}

		getChildren().add(gridPane);
	}

	private void buildControls() {
		gridPane.getChildren().clear();

		GraphicsBase<R> graphics = getSkinnable().getGraphics();
		if (graphics != null) {
			final ObservableList<Layer> modelLayers = graphics.getLayers();
			int row = modelLayers.size();

			for (int i = 0; i < modelLayers.size(); i++) {
				final Layer layer = modelLayers.get(i);
				CheckBox checkBox = new CheckBox();
				Slider slider = new Slider();
				checkBox = new CheckBox();
				slider = new Slider(0, 1, 1);

				GridPane.setHgrow(slider, Priority.ALWAYS);

				checkBox.setText(layer.getName());
				checkBox.setSelected(layer.isVisible());
				slider.setValue(layer.getOpacity());

				Button moveToFront = new Button();
				Button moveToBack = new Button();
				Button moveForward = new Button();
				Button moveBackward = new Button();
				Button delete = new Button();

				gridPane.add(checkBox, 0, row);
				gridPane.add(slider, 1, row);

				if (i < modelLayers.size() - 1) {
					gridPane.add(moveToFront, 2, row);
					gridPane.add(moveForward, 3, row);
				}

				if (i > 0) {
					gridPane.add(moveBackward, 4, row);
					gridPane.add(moveToBack, 5, row);
				}

				gridPane.add(delete, 6, row);

				moveToFront.getStyleClass().addAll("navigate-button",
						"navigate-up2-16");
				moveToBack.getStyleClass().addAll("navigate-button",
						"navigate-down2-16");
				moveForward.getStyleClass().addAll("navigate-button",
						"navigate-up-16");
				moveBackward.getStyleClass().addAll("navigate-button",
						"navigate-down-16");
				delete.getStyleClass().addAll("layers-navigate-button", "delete-16");

				moveToFront.setTooltip(new Tooltip(Messages.getString("LayersViewSkin.TOOLTIP_MOVE_LAYER_TO_FRONT")));
				moveToBack.setTooltip(new Tooltip(Messages.getString("LayersViewSkin.TOOLTIP_MOVE_LAYER_TO_BACK")));
				moveForward.setTooltip(new Tooltip(
						Messages.getString("LayersViewSkin.TOOLTIP_MOVE_LAYER_FORWARD")));
				moveBackward.setTooltip(new Tooltip(
						Messages.getString("LayersViewSkin.TOOLTIP_MOVE_LAYER_BACK")));
				delete.setTooltip(new Tooltip(Messages.getString("LayersViewSkin.TOOLTIP_DELETE_LAYER")));

				moveToFront.setOnAction(moveToFront(layer));
				moveToBack.setOnAction(moveToBack(layer));
				moveForward.setOnAction(moveForward(layer));
				moveBackward.setOnAction(moveBackward(layer));
				delete.setOnAction(delete(layer));

				Bindings.bindBidirectional(checkBox.selectedProperty(),
						layer.visibleProperty());
				Bindings.bindBidirectional(checkBox.textProperty(),
						layer.nameProperty());
				Bindings.bindBidirectional(slider.valueProperty(),
						layer.opacityProperty());

				delete.visibleProperty().bind(layer.deletableProperty());

				row--;
			}

			HBox hBox = new HBox();
			hBox.setSpacing(10);
			hBox.setAlignment(Pos.CENTER_RIGHT);

			Button showAll = new Button(Messages.getString("LayersViewSkin.BUTTON_SHOW_ALL"));
			Button hideAll = new Button(Messages.getString("LayersViewSkin.BUTTON_HIDE_ALL"));

			hBox.getChildren().add(hideAll);
			hBox.getChildren().add(showAll);

			gridPane.add(hBox, 0, modelLayers.size() + 1, 7, 1);

			GridPane.setMargin(hBox, new Insets(20, 0, 0, 0));

			showAll.setOnAction(showAll(modelLayers));
			hideAll.setOnAction(hideAll(modelLayers));
		}

		Label headerName = new Label(Messages.getString("LayersViewSkin.HEADER_LAYER_NAME"));
		Label headerOpacity = new Label(Messages.getString("LayersViewSkin.HEADER_OPACITY"));
		Label headerOrder = new Label(Messages.getString("LayersViewSkin.HEADER_ORDER"));

		headerName.setMaxWidth(Double.MAX_VALUE);
		headerOpacity.setMaxWidth(Double.MAX_VALUE);
		headerOrder.setMaxWidth(Double.MAX_VALUE);

		headerName.getStyleClass().add("layers-table-header");
		headerOpacity.getStyleClass().add("layers-table-header");
		headerOrder.getStyleClass().add("layers-table-header");

		GridPane.setHgrow(headerOpacity, Priority.ALWAYS);

		GridPane.setFillWidth(headerName, true);
		GridPane.setFillWidth(headerOpacity, true);
		GridPane.setFillWidth(headerOrder, true);

		gridPane.add(headerName, 0, 0);
		gridPane.add(headerOpacity, 1, 0);
		gridPane.add(headerOrder, 2, 0, 4, 1);
	}

	private EventHandler<ActionEvent> hideAll(
			final ObservableList<Layer> modelLayers) {
		return event -> {
			for (Layer layer : modelLayers) {
				layer.setVisible(false);
			}
		};
	}

	private EventHandler<ActionEvent> showAll(
			final ObservableList<Layer> modelLayers) {
		return event -> {
			for (Layer layer : modelLayers) {
				layer.setVisible(true);
			}
		};
	}

	private EventHandler<ActionEvent> delete(final Layer layer) {
		return evt -> getSkinnable().getGraphics().getLayers().remove(layer);
	}

	private EventHandler<ActionEvent> moveBackward(final Layer layer) {
		return evt -> getSkinnable().getGraphics().moveLayerBackward(layer);
	}

	private EventHandler<ActionEvent> moveForward(final Layer layer) {
		return evt -> getSkinnable().getGraphics().moveLayerForward(layer);
	}

	private EventHandler<ActionEvent> moveToBack(final Layer layer) {
		return evt -> getSkinnable().getGraphics().moveLayerToBack(layer);
	}

	private EventHandler<ActionEvent> moveToFront(final Layer layer) {
		return evt -> getSkinnable().getGraphics().moveLayerToFront(layer);
	}
}
