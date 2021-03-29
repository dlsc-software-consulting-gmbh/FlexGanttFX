/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.gantt;

import com.flexganttfx.demo.FlexGanttFXSampleBase;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.dateline.ChronoUnitGrid;
import com.flexganttfx.model.dateline.VirtualGrid;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.container.DualGanttChartContainer;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.DragAndDropFeedback;
import com.flexganttfx.view.graphics.GraphicsBase.DragAndDropInfo;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.util.Position;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class HelloDragAndDrop extends FlexGanttFXSampleBase {

	private TableView<Order> orderTable;
	private TableView<Person> personTable;
	private GanttChart<Resource> gantt1;
	private GanttChart<Resource> gantt2;
	private DualGanttChartContainer dualContainer;
	private TextArea textDropArea;
	private TextArea textEventsArea;

	private DragInfoPane dragInfoPane;

	private Map<String, Order> orders;
	private Map<String, Person> people;
	private Layer layer;

	@Override
	public void dispose() {
		super.dispose();
		gantt1 = null;
		gantt2 = null;
		dualContainer = null;
		orderTable = null;
		personTable = null;
		textDropArea = null;
		textEventsArea = null;
		dragInfoPane = null;
		orders = null;
		people = null;
		layer = null;
	}

	@Override
	public Node getControlPanel() {
		VBox box = new VBox();
		box.setSpacing(10);

		box.getChildren().add(dragInfoPane);
		box.getChildren().add(new TitledPane("Upper Gantt", new DragSettings(gantt1.getGraphics())));
		box.getChildren().add(new TitledPane("Lower Gantt", new DragSettings(gantt2.getGraphics())));

		return box;
	}

	class DragSettings extends VBox {

		public DragSettings(GraphicsBase<?> graphics) {
			setSpacing(10);

			ComboBox<DragAndDropFeedback> feedbackBox = new ComboBox<>();
			feedbackBox.getItems().addAll(DragAndDropFeedback.values());
			Bindings.bindBidirectional(feedbackBox.valueProperty(),
					graphics.dragAndDropFeedbackProperty());
			getChildren().add(feedbackBox);

			CheckBox autogrid = new CheckBox("Gantt 1: Autogrid");

			Bindings.bindBidirectional(autogrid.selectedProperty(),
					graphics.autoGridEnabledProperty());

			getChildren().add(autogrid);

			ChronoUnitGrid days = new ChronoUnitGrid("Days", ChronoUnit.DAYS, 1);
			ChronoUnitGrid hours = new ChronoUnitGrid("Hours",
					ChronoUnit.HOURS, 1);
			ChronoUnitGrid minutes = new ChronoUnitGrid("Minutes",
					ChronoUnit.MINUTES, 1);

			ObservableList<VirtualGrid<?>> gridList = FXCollections
					.observableArrayList(days, hours, minutes);

			ComboBox<VirtualGrid<?>> gridBox = new ComboBox<>(gridList);
			getChildren().add(gridBox);
			Bindings.bindBidirectional(gridBox.valueProperty(),
					graphics.virtualGridProperty());
		}
	}

	@Override
	public String getSampleDescription() {
		return "This sample highlights various aspects of the drag and drop support "
				+ "that is built into FlexGanttFX. The user can drag elements from "
				+ "the tables to the Gantt chart and also (by pressing SHIFT) from the Gantt chart to "
				+ "the text area on the right-hand side. At the same time the text "
				+ "area at the bottom shows the events that are being generated while "
				+ "dragging.";
	}

	private void setupOrderTable() {
		TableColumn<Order, String> nameColumn = new TableColumn<>("Order");
		TableColumn<Order, LocalDate> startColumn = new TableColumn<>("Start");
		TableColumn<Order, LocalDate> endColumn = new TableColumn<>("Finish");
		TableColumn<Order, Priority> priorityColumn = new TableColumn<>("Priority");

		nameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		startColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
		endColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
		priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

		nameColumn.setPrefWidth(200);
		startColumn.setPrefWidth(100);
		endColumn.setPrefWidth(100);
		priorityColumn.setPrefWidth(100);

		orderTable.getColumns().add(nameColumn);
		orderTable.getColumns().add(startColumn);
		orderTable.getColumns().add(endColumn);
		orderTable.getColumns().add(priorityColumn);

		List<Order> list = new ArrayList<>();
		for (int i = 0; i < 40; i++) {
			Order order = new Order("Order #" + (i + 1));
			LocalDate start = LocalDate.now().plusDays(2 + (int) (Math.random() * 10));
			LocalDate end = start.plusDays(5 + (int) (Math.random() * 10));

			order.setStartDate(start);
			order.setEndDate(end);
			order.setPriority(Priority.values()[(int) (Math.random() * 3)]);
			list.add(order);

			orders.put(order.getTitle(), order);
		}

		orderTable.getItems().setAll(list);

		orderTable.setOnDragDetected(evt -> dragDetectedOrderTable(evt));
		orderTable.setOnDragDone(evt -> dragDoneOrderTable(evt));
	}

	private void setupPersonTable() {
		TableColumn<Person, String> nameColumn = new TableColumn<>("Person");

		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		nameColumn.setPrefWidth(200);

		personTable.getColumns().add(nameColumn);

		List<Person> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Person person = new Person("Person #" + (i + 1));
			list.add(person);
			people.put(person.getName(), person);
		}

		personTable.getItems().setAll(list);

		personTable.setOnDragDetected(evt -> dragDetectedPersonTable(evt));
	}

	private void dragDetectedOrderTable(MouseEvent evt) {
		Dragboard db = orderTable.startDragAndDrop(TransferMode.MOVE);
		ClipboardContent content = new ClipboardContent();
		content.putString("order:" + orderTable.getSelectionModel().getSelectedItem().getTitle());
		db.setContent(content);
	}

	private void dragDoneOrderTable(DragEvent evt) {
		String title = evt.getDragboard().getString();
		StringTokenizer st = new StringTokenizer(title, ":");
		st.nextToken();
		Order order = orders.get(st.nextToken());
		orderTable.getItems().remove(order);
	}

	private void dragDetectedPersonTable(MouseEvent evt) {
		Dragboard db = personTable.startDragAndDrop(TransferMode.MOVE);
		ClipboardContent content = new ClipboardContent();
		content.putString("person:"
				+ personTable.getSelectionModel().getSelectedItem().getName());
		db.setContent(content);
	}

	private void setupGantt() {

		List<Resource> resources1 = new ArrayList<>();
		List<Resource> resources2 = new ArrayList<>();

		for (int i = 0; i < 40; i++) {
			Resource resource1 = new Resource("Resource A" + (i + 1));
			resources1.add(resource1);
		}

		for (int i = 0; i < 3; i++) {
			Resource resource2 = new Resource("Resource B" + (i + 1));
			resource2.setHeight(24);
			resources2.add(resource2);
		}

		Resource root1 = new Resource("Root 1");
		root1.getChildren().setAll(resources1);
		root1.setExpanded(true);

		Resource root2 = new Resource("Root 2");
		root2.getChildren().setAll(resources2);
		root2.setExpanded(true);

		gantt1.getTimeline().getModel().setHorizonStartTime(Instant.now());

		gantt1.getLayers().add(layer);
		gantt2.getLayers().add(layer);

		gantt1.getTreeTable().setShowRoot(false);
		gantt2.getTreeTable().setShowRoot(false);

		gantt1.setRoot(root1);
		gantt2.setRoot(root2);

		GraphicsBase<Resource> graphics1 = gantt1.getGraphics();
		graphics1.setDragAndDropFeedback(DragAndDropFeedback.RENDERED_GRID_SNAPPED);
		graphics1.setAutoGridEnabled(true);
		graphics1.setOnDragOver(evt -> dragOver(evt));
		graphics1.setOnDragDropped(evt -> dragDropped(evt));
		graphics1.setActivityRenderer(OrderAssignment.class, GanttLayout.class, new OrderAssignmentRenderer(graphics1));
		graphics1.dragAndDropInfoProperty().addListener(it -> updateDragAndDropInfo(graphics1.getDragAndDropInfo()));
		graphics1.dragAndDropInfoProperty().addListener(evt -> dragInfoPane.setInfo(graphics1.getDragAndDropInfo()));

		GraphicsBase<Resource> graphics2 = gantt2.getGraphics();
		graphics2.setOnDragOver(evt -> dragOver(evt));
		graphics2.setOnDragDropped(evt -> dragDropped(evt));
		graphics2.setActivityRenderer(OrderAssignment.class, GanttLayout.class, new OrderAssignmentRenderer(graphics1));
		graphics2.dragAndDropInfoProperty().addListener(it -> updateDragAndDropInfo(graphics2.getDragAndDropInfo()));
		graphics2.dragAndDropInfoProperty().addListener(evt -> dragInfoPane.setInfo(graphics2.getDragAndDropInfo()));

		dualContainer = new DualGanttChartContainer(gantt1, gantt2);
	}

	private void dragOver(DragEvent evt) {
		evt.acceptTransferModes(TransferMode.MOVE);
	}

	private void dragDropped(DragEvent evt) {
		evt.acceptTransferModes(TransferMode.ANY);

		if (evt.getTarget() instanceof RowCanvas) {
			@SuppressWarnings("unchecked")
			RowCanvas<Resource> canvas = (RowCanvas<Resource>) evt.getTarget();
			GraphicsBase<Resource> graphics = canvas.getGraphics();
			Resource resource = graphics.getRowAt(evt.getY());

			String dragString = evt.getDragboard().getString();
			if (dragString != null) {

				if (dragString.startsWith("order")) {

					StringTokenizer st = new StringTokenizer(dragString, ":");
					st.nextToken();

					Order order = orders.get(st.nextToken());

					OrderAssignment assignment = new OrderAssignment(order);
					resource.addActivity(layer, assignment);
				} else if (dragString.startsWith("person")) {
					StringTokenizer st = new StringTokenizer(dragString, ":");
					st.nextToken();

					Person person = people.get(st.nextToken());

					ActivityRef<?> ref = graphics.getActivityRefAt(evt.getX(),
							evt.getY());
					if (ref != null) {
						Activity activity = ref.getActivity();
						if (activity instanceof OrderAssignment) {
							OrderAssignment assignment = (OrderAssignment) activity;
							assignment.setPerson(person);
							graphics.redraw();
						}
					}
				}
			}

		}

		evt.consume();
	}

	private void setupTextDropArea() {
		SplitPane.setResizableWithParent(textDropArea, false);
		textDropArea.setPromptText("Drop items here, too!");
		textDropArea.setOnDragOver(evt -> dragOverOnTextArea(evt));
		textDropArea.setOnDragDropped(evt -> dragDroppedOnTextArea(evt));
	}

	private void dragOverOnTextArea(DragEvent evt) {
		evt.acceptTransferModes(TransferMode.ANY);
	}

	private void dragDroppedOnTextArea(DragEvent evt) {
		evt.acceptTransferModes(TransferMode.ANY);

		textDropArea.appendText(evt.getDragboard().getString());
		textDropArea.appendText(System.getProperty("line.separator"));
		textDropArea.appendText(System.getProperty("line.separator"));

		evt.consume();
	}

	private void setupTextEventsArea() {
		textEventsArea.setPromptText("Events...");
		gantt1.getGraphics().setOnActivityChange(evt -> {
			textEventsArea.appendText("Gantt 1: " + evt.toString());
			textEventsArea.appendText(System.getProperty("line.separator"));
		});
		gantt2.getGraphics().setOnActivityChange(evt -> {
			textEventsArea.appendText("Gantt 2: " + evt.toString());
			textEventsArea.appendText(System.getProperty("line.separator"));
		});
	}

	private void updateDragAndDropInfo(DragAndDropInfo info) {
		if (info != null) {
			// textEventsArea.appendText(info.toString());
			// textEventsArea.appendText(System.getProperty("line.separator"));
		}
	}

	@Override
	public String getSampleName() {
		return "Drag & Drop";
	}

	@Override
	public Node getPanel(Stage stage) {
		orderTable = new TableView<>();
		personTable = new TableView<>();
		gantt1 = new GanttChart<>();
		gantt2 = new GanttChart<>();
		textDropArea = new TextArea();
		textEventsArea = new TextArea();

		dragInfoPane = new DragInfoPane();

		orders = new HashMap<>();
		people = new HashMap<>();
		layer = new Layer("Orders");

		setupOrderTable();
		setupPersonTable();
		setupGantt();
		setupTextDropArea();
		setupTextEventsArea();

		SplitPane horizontalSplit = new SplitPane();
		horizontalSplit.setOrientation(Orientation.HORIZONTAL);
		horizontalSplit.getItems().addAll(orderTable, personTable);

		SplitPane lowerSplit = new SplitPane();
		lowerSplit.setOrientation(Orientation.HORIZONTAL);
		lowerSplit.getItems().addAll(dualContainer, textDropArea);
		lowerSplit.setDividerPositions(.8);

		textDropArea.setPrefWidth(200);

		SplitPane verticalSplit = new SplitPane();
		verticalSplit.setOrientation(Orientation.VERTICAL);
		verticalSplit.setDividerPositions(.2, .9);
		verticalSplit.getItems().addAll(horizontalSplit, lowerSplit,
				textEventsArea);

		return verticalSplit;
	}

	enum Priority {
		LOW, MEDIUM, HIGH
	}

	public class Order {
		private String title;
		private LocalDate startDate = LocalDate.now();
		private LocalDate endDate = LocalDate.now().plusWeeks(1);
		private Priority priority;

		public Order(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setStartDate(LocalDate startDate) {
			this.startDate = startDate;
		}

		public LocalDate getStartDate() {
			return startDate;
		}

		public void setEndDate(LocalDate endDate) {
			this.endDate = endDate;
		}

		public LocalDate getEndDate() {
			return endDate;
		}

		public void setPriority(Priority priority) {
			this.priority = priority;
		}

		public Priority getPriority() {
			return priority;
		}
	}

	public class Person {
		private String name;

		public Person(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public class OrderAssignment extends MutableActivityBase<Order> {
		private Person person;

		public OrderAssignment(Order order) {
			super(order.getTitle());

			setUserObject(order);

			setStartTime(ZonedDateTime.of(order.getStartDate(), LocalTime.MIN,
					ZoneId.systemDefault()).toInstant());
			setEndTime(ZonedDateTime.of(order.getEndDate(), LocalTime.MAX,
					ZoneId.systemDefault()).toInstant());

		}

		public void setPerson(Person person) {
			this.person = person;
		}

		public Person getPerson() {
			return person;
		}
	}

	class OrderAssignmentRenderer extends ActivityRenderer<OrderAssignment> {

		public OrderAssignmentRenderer(GraphicsBase<?> graphics) {
			super(graphics, "Order Assignments");
		}

		@Override
		protected ActivityBounds drawActivity(
				ActivityRef<OrderAssignment> activityRef, Position position,
				GraphicsContext gc, double x, double y, double w, double h,
				boolean selected, boolean hover, boolean highlighted,
				boolean pressed) {

			OrderAssignment assignment = activityRef.getActivity();

			switch (assignment.getUserObject().getPriority()) {
			case LOW:
				setFill(Color.LIGHTGREEN);
				break;
			case MEDIUM:
				setFill(Color.YELLOW);
				break;
			case HIGH:
				setFill(Color.RED);
				break;
			}

			ActivityBounds bounds = super.drawActivity(activityRef, position,
					gc, x, y, w, h, selected, hover, highlighted, pressed);

			Person person = assignment.getPerson();
			if (person != null) {
				gc.setTextAlign(TextAlignment.LEFT);
				gc.setTextBaseline(VPos.CENTER);
				gc.setFill(getStroke());
				gc.fillText(person.getName(), x + 4, y + h / 2);
			}

			return bounds;
		}
	}

	public class Resource extends Row<Resource, Resource, OrderAssignment> {

		public Resource(String name) {
			super(name);

			setHeight(50);
		}
	}

	class DragInfoPane extends GridPane {

		private TextField activityField = new TextField();
		private TextField rowField = new TextField();
		private TextField transferModeField = new TextField();
		private DatePicker startTimeField = new DatePicker();
		private DatePicker endTimeField = new DatePicker();

		public DragInfoPane() {
			setHgap(5);
			setVgap(5);

			Label activityLabel = new Label("Activity:");
			Label rowLabel = new Label("Row:");
			Label transferModeLabel = new Label("Mode:");
			Label startTimeLabel = new Label("Start:");
			Label endTimeLabel = new Label("End:");

			add(activityLabel, 0, 0);
			add(rowLabel, 0, 1);
			add(transferModeLabel, 0, 2);
			add(startTimeLabel, 0, 3);
			add(endTimeLabel, 0, 4);

			add(activityField, 1, 0);
			add(rowField, 1, 1);
			add(transferModeField, 1, 2);
			add(startTimeField, 1, 3);
			add(endTimeField, 1, 4);

			startTimeField.setEditable(false);
			endTimeField.setEditable(false);
		}

		public void setInfo(DragAndDropInfo info) {
			if (info != null) {
				activityField.setText(info.getActivityBounds().getActivity()
						.getName());
				if (info.getRow() != null) {
					rowField.setText(info.getRow().getName());
				} else {
					rowField.setText("<No Row>");
				}

				TransferMode transferMode = info.getDragEvent().getTransferMode();
				if (transferMode != null) {
					transferModeField.setText(transferMode
							.toString());
				} else {
					transferModeField.setText("NONE");
				}
				startTimeField.setValue(ZonedDateTime.ofInstant(
						info.getDropInterval().getStartTime(),
						ZoneId.systemDefault()).toLocalDate());
				endTimeField.setValue(ZonedDateTime.ofInstant(
						info.getDropInterval().getEndTime(),
						ZoneId.systemDefault()).toLocalDate());
			} else {
				activityField.setText("");
				rowField.setText("");
				transferModeField.setText("");
				startTimeField.setValue(null);
				endTimeField.setValue(null);
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
