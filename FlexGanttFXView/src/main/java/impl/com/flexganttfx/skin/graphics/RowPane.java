/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.model.ActivityRepository;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.repository.RepositoryEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.RowControlsParameter;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import static javafx.scene.input.MouseEvent.MOUSE_ENTERED;
import static javafx.scene.input.MouseEvent.MOUSE_EXITED;

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

		canvas = new RowCanvas<>(graphics);
		canvas.widthProperty().bind(widthProperty().add(graphics.canvasBufferProperty().multiply(2)));
		canvas.heightProperty().bind(heightProperty());
		canvas.rowProperty().bind(rowProperty());
		StackPane.setAlignment(canvas, Pos.CENTER); // VERY IMPORTANT, we want buffer to the left AND the right

		zoneIdLabel = new Label("Zone ID");
		zoneIdLabel.getStyleClass().add("zone-id-label");
		zoneIdLabel.visibleProperty().bind(Bindings.and(Bindings.isNotNull(row), graphics.showZoneIdProperty()));

		flipPane = new FlipPane<>(this);
		flipPane.getFront().getChildren().add(canvas);
		flipPane.getFront().getChildren().add(zoneIdLabel);
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

		final EventHandler<MouseEvent> mouseEntered = evt -> {
			if (getRow() != null && !flipPane.isBackVisible()) {
				Callback<RowControlsParameter<R>, Node> controlsFactory = graphics.getRowControlsFactory();
				if (controlsFactory != null) {
					RowControlsParameter<R> param = new RowControlsParameter<>(
							graphics, getRow());
					Node controlsNode = controlsFactory.call(param);
					setControlsNode(controlsNode);
				}
			}
		};

		final EventHandler<MouseEvent> mouseExited = evt -> {
			if (getRow() != null) {
				setControlsNode(null);
			}
		};

		rowProperty().addListener(
				(observable, oldRow, newRow) -> {

					if (oldRow != null) {
						oldRow.linesManagerProperty()
								.removeListener(weakRedrawListener);
						oldRow.lineCountProperty()
								.removeListener(weakRedrawListener);
						oldRow.layoutProperty()
								.removeListener(weakRedrawListener);
						oldRow.getRepository()
								.removeEventHandler(weakRepositoryListener);
						oldRow.repositoryProperty()
								.removeListener(weakRepositoryReplacedListener);
						oldRow.zoneIdProperty()
								.removeListener(weakUpdateZoneIdListener);
						oldRow.getProperties().put(
								"com.flexganttfx.row.showing",
								false);

						Bindings.unbindBidirectional(prefHeightProperty(),
								oldRow.heightProperty());
					}

					if (newRow != null) {
						newRow.linesManagerProperty()
								.addListener(weakRedrawListener);
						newRow.lineCountProperty()
								.addListener(weakRedrawListener);
						newRow.layoutProperty().addListener(weakRedrawListener);
						newRow.getRepository()
								.addEventHandler(weakRepositoryListener);
						newRow.repositoryProperty()
								.addListener(weakRepositoryReplacedListener);
						newRow.zoneIdProperty()
								.addListener(weakUpdateZoneIdListener);

						newRow.getProperties().put(
								"com.flexganttfx.row.showing",
								isVisible());

						/*
						 * Important call: first initialize the pref height with
						 * the current height of the new row.
						 */
						setPrefHeight(newRow.getHeight());
						Bindings.bindBidirectional(prefHeightProperty(),
								newRow.heightProperty());
					} else {
						setPrefHeight(Row.DEFAULT_ROW_HEIGHT);
					}

					updateZoneIdLabel();
				});

		addEventHandler(MOUSE_ENTERED, mouseEntered);
		addEventHandler(MOUSE_EXITED, mouseExited);

		getChildren().add(flipPane);
	}

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

	public final Node getControlsNode() {
		return controlsNode;
	}

	private final ObjectProperty<R> row = new SimpleObjectProperty<>(this,
			"row");

	public final ObjectProperty<R> rowProperty() {
		return row;
	}

	public final void setRow(R row) {
		rowProperty().set(row);
	}

	public final R getRow() {
		return rowProperty().get();
	}

	public final RowCanvas<R> getCanvas() {
		return canvas;
	}

	public final void startEditing() {
		flipPane.setRow(getRow());
		getScene().setCamera(new PerspectiveCamera());
		if (graphics.isAnimateRowEditor()) {
			flipPane.flipToBack();
		} else {
			flipPane.flipToBackImmediately();
		}
	}

	public final void stopEditing() {
		if (graphics.isAnimateRowEditor()) {
			flipPane.flipToFront();
		} else {
			flipPane.flipToFrontImmediately();
		}
	}

	public void draw() {
		canvas.draw();
	}

	private void updateZoneIdLabel() {
		R row = getRow();
		if (row != null) {
			ZoneId zoneId = row.getZoneId();
			zoneIdLabel.setText(zoneId.getDisplayName(TextStyle.FULL_STANDALONE,
					Locale.getDefault()));
		} else {
			zoneIdLabel.setText("");
		}
	}

	private final InvalidationListener redrawListener = it -> draw();

	private final InvalidationListener weakRedrawListener = new WeakInvalidationListener(
			redrawListener);

	private final InvalidationListener updateZoneIdListener = evt -> updateZoneIdLabel();

	private final InvalidationListener weakUpdateZoneIdListener = new WeakInvalidationListener(
			updateZoneIdListener);

	private final EventHandler<RepositoryEvent> repositoryListener = evt -> {
		/*
		 * Do not redraw immediately after each repository event if automatic redraw
		 * is set to false. Can be used to fine-tune application when adding a lot of data
		 * in a batch.
		 */
		if (graphics.isAutomaticRedraw()) {
			draw();
		}
	};

	private final WeakEventHandler<RepositoryEvent> weakRepositoryListener = new WeakEventHandler<>(
			repositoryListener);

	private final ChangeListener<ActivityRepository<?>> repositoryReplacedListener = (
			observable, oldRepository, newRepository) -> {
		if (oldRepository != null) {
			oldRepository.removeEventHandler(weakRepositoryListener);
		}
		if (newRepository != null) {
			newRepository.addEventHandler(weakRepositoryListener);
		}
	};

	private final WeakChangeListener<ActivityRepository<?>> weakRepositoryReplacedListener = new WeakChangeListener<>(
			repositoryReplacedListener);

}
