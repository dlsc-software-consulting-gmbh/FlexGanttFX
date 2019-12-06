/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.emirates.view;

import com.flexganttfx.emirates.map.MapComponent;
import com.flexganttfx.emirates.model.Aircraft;
import com.flexganttfx.emirates.model.Flight;
import com.flexganttfx.emirates.model.Group;
import com.flexganttfx.emirates.model.ModelObject;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.model.util.TimeInterval;
import com.flexganttfx.view.GanttChartLite;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.ActivityEvent;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.timeline.Timeline;
import impl.com.flexganttfx.skin.graphics.DragCanvas;
import impl.com.flexganttfx.skin.graphics.GraphicsBaseSkin;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EmiratesAircraftGanttChart extends
        GanttChartLite<ModelObject<?, ?, ?>> {

	public EmiratesAircraftGanttChart() {
		Timeline timeline = getTimeline();
		timeline.showTemporalUnit(ChronoUnit.HOURS, 50);

		timeline.setMoveAnimated(false);
		timeline.setZoomAnimated(false);

        getGraphics().getBackgroundSystemLayers().add(new GroupSystemLayer(getGraphics()));
		getGraphics().setActivityRenderer(Flight.class, GanttLayout.class,new FlightRenderer(getGraphics()));

		EventHandler<ActivityEvent> updateListener = new EventHandler<>() {
			@Override
			public void handle(ActivityEvent event) {
				updateRow(event.getActivityRef().getRow());
				updateRow(event.getOldRow());
			}

			private void updateRow(Row<?, ?, ?> row) {
				if (row != null && row instanceof Aircraft) {
					Aircraft aircraft = (Aircraft) row;
					aircraft.updateInnerLines();

					Group group = (Group) row.getParent();
					List<Layer> layers = getLayers();

					Instant st = aircraft.getEarliestTimeUsed();
					Instant et = aircraft.getLatestTimeUsed();

					group.updateUsageProfile(new TimeInterval(st, et), layers.get(layers.size() - 1), layers);
				}

			}
		};

		getGraphics().addEventHandler(ActivityEvent.START_TIME_CHANGE_FINISHED, updateListener);
		getGraphics().addEventHandler(ActivityEvent.END_TIME_CHANGE_FINISHED, updateListener);
		getGraphics().addEventHandler(ActivityEvent.DRAG_FINISHED, updateListener);
		getGraphics().addEventHandler(ActivityEvent.HORIZONTAL_DRAG_FINISHED, updateListener);

//		getGraphics().getForegroundSystemLayers().add(new NameLayer<>(getGraphics()));

		getGraphics().setEditModeCallback(Flight.class, GanttLayout.class, param -> GraphicsBase.EditMode.DRAGGING_VERTICAL);
		getGraphics().setActivityEditingCallback(Flight.class, param -> param.getEditMode().equals(GraphicsBase.EditMode.DRAGGING_VERTICAL));

        getGraphics().skinProperty().addListener(it -> {
            GraphicsBaseSkin skin = (GraphicsBaseSkin) getGraphics().getSkin();
            DragCanvas canvas = skin.getDragCanvas();
            canvas.setIncludeSelectedActivitiesInDrag(true);
        });

		detail = new VBox();
		detail.setStyle("-fx-background-color: white;");

		detail.setPrefWidth(400);
		detail.setFillWidth(true);
		ScrollPane scrollPane = new ScrollPane(detail);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		setDetail(scrollPane);

		setupPopOver();
	}

	private VBox detail;

	private void addToDetail() {
		if (mapComponent != null) {
			mapComponent.setPreferredSize(new Dimension(200, 200));

			SwingNode swingNode = new SwingNode();
			swingNode.setStyle("-fx-border-color: black; -fx-padding: 10px;");
			swingNode.setContent(mapComponent);
			VBox.setMargin(swingNode, new Insets(5, 10, 5, 10));

			detail.getChildren().add(swingNode);

			popOver = null;

			setShowDetail(true);

			Platform.runLater(() -> detail.requestLayout());
		}
	}

	private PopOver popOver;

	/*
	 * Trigger the loading of the large CSV file.
	 */
	private MapComponent mapComponent = new MapComponent();

	private void setupPopOver() {
		GraphicsBase<ModelObject<?, ?, ?>> graphics = getGraphics();

		getGraphics()
				.addEventFilter(
						MouseEvent.MOUSE_CLICKED,
						mouseEvent -> {
							if (!(mouseEvent.getSource() == getGraphics())) {
								return;
							}

							if (mouseEvent.getClickCount() == 2) {

								if (popOver == null || popOver.isDetached()) {
									popOver = new PopOver();
									popOver.setArrowLocation(ArrowLocation.LEFT_TOP);

									BorderPane wrapper = new BorderPane();
									wrapper.setStyle("-fx-padding: 20;");

									mapComponent = new MapComponent();
									mapComponent.setBorder(new LineBorder(
											Color.BLACK));
									mapComponent
											.setPreferredSize(new Dimension(
													600, 400));

									SwingNode swingNode = new SwingNode();
									swingNode
											.setStyle("-fx-border-color: black;");
									swingNode.setContent(mapComponent);

									wrapper.setCenter(swingNode);

									Button detailButton = new Button("Add");
									detailButton.setOnAction(actionEvent -> {
										popOver.hide();
										addToDetail();
									});

									HBox.setMargin(detailButton, new Insets(5));
									HBox hbox = new HBox();
									hbox.getChildren().add(detailButton);
									hbox.setAlignment(Pos.CENTER_RIGHT);
									wrapper.setBottom(hbox);
									popOver.setContentNode(wrapper);
								}

								ActivityBounds bounds = graphics
										.getActivityBoundsAt(mouseEvent.getX(),
												mouseEvent.getY());

								if (bounds != null) {
									Flight flight = (Flight) bounds
											.getActivity();

									final List<Flight> shownFlights = new ArrayList<>();
									shownFlights.add(flight);

									for (ActivityRef<?> activityRef : getGraphics()
											.getSelectedActivities()) {
										shownFlights.add((Flight) activityRef
												.getActivity());
									}

									SwingUtilities
											.invokeLater(() -> mapComponent
													.display(shownFlights));
								}

								popOver.show(getGraphics(),
										mouseEvent.getScreenX(),
										mouseEvent.getScreenY());
							} else if (popOver != null && !popOver.isDetached()) {
								popOver.hide();
							}
						});
	}
}
