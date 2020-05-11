/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.demo.layout;

import com.flexganttfx.demo.FlexGanttFXSample;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.MutableActivityBase;
import com.flexganttfx.model.dateline.ChronoUnitGrid;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.view.GanttChart;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.util.Position;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.BeanPropertyUtils;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

import static java.time.DayOfWeek.MONDAY;
import static java.util.Objects.requireNonNull;

public class HelloAgendaLayout extends FlexGanttFXSample {

	private GanttChart<AgendaRow> gc;
	private AgendaLayout layout;
	private Layer layer;
	private AgendaRow row;

	@Override
	public String getSampleName() {
		return "Agenda";
	}

	class AgendaRow extends Row<AgendaRow, AgendaRow, MutableActivityBase<String>> {
		public AgendaRow(String name) {
			super(name);
		}
	}

	@Override
	protected GanttChart<?> createGanttChart() {
		AgendaRow root = new AgendaRow("Root");

		gc = new GanttChart<>(root);
		gc.getTimeline().showTemporalUnit(ChronoUnit.DAYS, 100);
		ListViewGraphics<AgendaRow> graphics = gc.getGraphics();

		row = new AgendaRow("Test Row");
		row.setMaxHeight(1200);
		row.setHeight(700);

		InvalidationListener redrawListener = it -> graphics.redraw();

		layout = new AgendaLayout();
		layout.setStartTime(LocalTime.of(7, 0));
		layout.setEndTime(LocalTime.of(17, 0));

		layout.layoutStrategyProperty().addListener(redrawListener);
		layout.startTimeProperty().addListener(redrawListener);
		layout.endTimeProperty().addListener(redrawListener);
		layout.overlapOffsetProperty().addListener(redrawListener);
		layout.paddingProperty().addListener(redrawListener);
		layout.minLineSpacingProperty().addListener(redrawListener);

		row.setLayout(layout);

		root.getChildren().add(row);
		root.setExpanded(true);

		layer = new Layer("Default");
		gc.getLayers().add(layer);

		gc.getTreeTable().setShowRoot(false);

		graphics.setVirtualGrid(new ChronoUnitGrid("5 Minutes", ChronoUnit.MINUTES, 5));
		graphics.setShowMarkedTimeInterval(false);
		graphics.setShowVerticalCursor(false);
		graphics.setActivityRenderer(AgendaActivity.class, AgendaLayout.class, new AgendaActivityRenderer(graphics));
		graphics.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> createActivity(evt));
		graphics.setVirtualGrid(null);

		LocalDate date = LocalDate.now().with(TemporalAdjusters.nextOrSame(MONDAY));

		for (int i = 0; i < 50; i++) {
			createSchedule(date);
			date = date.with(TemporalAdjusters.next(MONDAY));
		}

		return gc;
	}

	private void createSchedule(LocalDate date) {

		// Monday
		addActivity(date, Type.ENGLISH, 8, 00, 8, 45);
		addActivity(date, Type.ENGLISH, 8, 50, 9, 35);
		addActivity(date, Type.MATH, 9, 50, 10, 35);
		addActivity(date, Type.MATH, 10, 40, 11, 25);
		addActivity(date, Type.BIOLOGY, 11, 40, 12, 25);
		addActivity(date, Type.BIOLOGY, 12, 30, 13, 15);
		addActivity(date, Type.GERMAN, 14, 30, 15, 15);
		addActivity(date, Type.GERMAN, 15, 20, 16, 5);

		// Tuesday
		addActivity(date.plusDays(1), Type.CHEMISTRY, 8, 00, 8, 45);
		addActivity(date.plusDays(1), Type.ENGLISH, 8, 50, 9, 35);
		addActivity(date.plusDays(1), Type.RELIGION, 9, 50, 10, 35);
		addActivity(date.plusDays(1), Type.RELIGION, 10, 40, 11, 25);
		addActivity(date.plusDays(1), Type.SPORT, 11, 40, 12, 25);
		addActivity(date.plusDays(1), Type.SPORT, 12, 30, 13, 15);
		addActivity(date.plusDays(1), Type.GERMAN, 14, 30, 15, 15);
		addActivity(date.plusDays(1), Type.GERMAN, 15, 20, 16, 5);

		// Wednesday
		addActivity(date.plusDays(2), Type.CHEMISTRY, 8, 00, 8, 45);
		addActivity(date.plusDays(2), Type.ENGLISH, 8, 50, 9, 35);
		addActivity(date.plusDays(2), Type.MATH, 9, 50, 10, 35);
		addActivity(date.plusDays(2), Type.MATH, 10, 40, 11, 25);
		addActivity(date.plusDays(2), Type.SPORT, 11, 40, 12, 25);
		addActivity(date.plusDays(2), Type.SPORT, 12, 30, 13, 15);

		// Thursday
		addActivity(date.plusDays(3), Type.BIOLOGY, 8, 00, 8, 45);
		addActivity(date.plusDays(3), Type.BIOLOGY, 8, 50, 9, 35);
		addActivity(date.plusDays(3), Type.GERMAN, 9, 50, 10, 35);
		addActivity(date.plusDays(3), Type.GERMAN, 10, 40, 11, 25);
		addActivity(date.plusDays(3), Type.PHYSICS, 11, 40, 12, 25);
		addActivity(date.plusDays(3), Type.PHYSICS, 12, 30, 13, 15);
		addActivity(date.plusDays(3), Type.PHYSICS, 14, 30, 16, 5);

		// Friday
		addActivity(date.plusDays(4), Type.CHEMISTRY, 8, 00, 8, 45);
		addActivity(date.plusDays(4), Type.CHEMISTRY, 8, 50, 9, 35);
		addActivity(date.plusDays(4), Type.MATH, 9, 50, 10, 35);
		addActivity(date.plusDays(4), Type.MATH, 10, 40, 11, 25);
		addActivity(date.plusDays(4), Type.BIOLOGY, 11, 40, 12, 25);
		addActivity(date.plusDays(4), Type.BIOLOGY, 12, 30, 13, 15);
	}

	private void addActivity(LocalDate date, Type type, int startHour, int startMinute, int endHour, int endMinute) {
		Instant st = ZonedDateTime.of(date, LocalTime.of(startHour, startMinute), row.getZoneId()).toInstant();
		Instant et = ZonedDateTime.of(date, LocalTime.of(endHour, endMinute), row.getZoneId()).toInstant();
		AgendaActivity activity = new AgendaActivity(type);
		activity.setStartTime(st);
		activity.setEndTime(et);
		row.addActivity(layer, activity);
	}

	private void createActivity(MouseEvent evt) {
		if (evt.getClickCount() == 2) {
			ListViewGraphics<AgendaRow> graphics = gc.getGraphics();
			AgendaRow row = graphics.getRowAt(evt.getY());
			if (row != null) {
				Instant time = graphics.getTimeAt(evt.getX());
				LocalTime localTime = graphics.getLocalTimeAt(evt.getY());
				AgendaActivity activity = new AgendaActivity(Type.SPORT);
				activity.setStartTime(ZonedDateTime.ofInstant(time, row.getZoneId()).with(localTime).toInstant());
				activity.setEndTime(activity.getStartTime().plus(Duration.ofHours(1)));
				row.addActivity(layer, activity);
			}
		}
	}

	@Override
	public String getSampleDescription() {
		return "The agenda layout class is used to layout activities vertically in a way "
				+ "that is similar to regular calendars. Double click to create new activities.";
	}

	@Override
	public Node getControlPanel() {
		PropertySheet sheet = new PropertySheet(
				BeanPropertyUtils.getProperties(layout));
		DefaultPropertyEditorFactory factory = new DefaultPropertyEditorFactory() {
			@Override
			public org.controlsfx.property.editor.PropertyEditor<?> call(Item item) {

				if (item.getType() == LocalTime.class) {
					return new LocalTimePropertyEditor(item);
				}

				return super.call(item);
			}
		};
		sheet.setPropertyEditorFactory(factory);
		return sheet;
	}

	class LocalTimePropertyEditor extends AbstractPropertyEditor<LocalTime, LocalTimeEditor> {

		public LocalTimePropertyEditor(Item item) {
			super(item, new LocalTimeEditor());
		}

		@Override
		public void setValue(LocalTime time) {
			getEditor().setValue(time);
		}

		@Override
		protected ObservableValue<LocalTime> getObservableValue() {
			return getEditor().localTimeProperty();
		}
	}

	class LocalTimeEditor extends Control implements PropertyEditor<LocalTime> {

		public LocalTimeEditor() {
		}

		@Override
		protected Skin<?> createDefaultSkin() {
			return new LocalTimeEditorSkin(this);
		}

		private final ObjectProperty<LocalTime> localTime = new SimpleObjectProperty<>(
				this, "localTime", LocalTime.MIN);

		public final ObjectProperty<LocalTime> localTimeProperty() {
			return localTime;
		}

		public final void setLocalTime(LocalTime time) {
			requireNonNull(time);
			localTime.set(time);
		}

		public final LocalTime getLocalTime() {
			return localTime.get();
		}

		@Override
		public Node getEditor() {
			return this;
		}

		@Override
		public LocalTime getValue() {
			return getLocalTime();
		}

		@Override
		public void setValue(LocalTime time) {
			localTime.set(time);
		}
	}

	class LocalTimeEditorSkin extends SkinBase<LocalTimeEditor> {

		public LocalTimeEditorSkin(LocalTimeEditor editor) {
			super(editor);

			ComboBox<LocalTime> box = new ComboBox<>();
			box.setMaxWidth(Double.MAX_VALUE);

			for (int i = 0; i <= 23; i++) {
				box.getItems().add(LocalTime.of(i, 0));
				box.getItems().add(LocalTime.of(i, 30));
			}
			box.getItems().add(LocalTime.MAX);
			Bindings.bindBidirectional(box.valueProperty(),
					editor.localTimeProperty());
			getChildren().add(box);
		}
	}

	public enum Type {
		ENGLISH("English"), GERMAN("German"), MATH("Math"), SPORT("Sport"), CHEMISTRY(
				"Chemistry"), PHYSICS("Physics"), BIOLOGY("Biology"), RELIGION(
				"Religion");

		private String displayName;

		Type(String name) {
			this.displayName = name;
		}

		public String getDisplayName() {
			return displayName;
		}
	}

	class AgendaActivity extends MutableActivityBase<String> {

		private Type type = Type.GERMAN;

		public AgendaActivity(Type type) {
			this.type = type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public Type getType() {
			return type;
		}
	}

	/**
	 * The renderer used by the showcase application. Not for production use.
	 */
	public class AgendaActivityRenderer extends
			ActivityRenderer<AgendaActivity> {

		private DateTimeFormatter timeFormatter = DateTimeFormatter
				.ofLocalizedTime(FormatStyle.SHORT);

		private Map<Type, Color> fillColorMap = new HashMap<>();
		private Map<Type, Color> strokeColorMap = new HashMap<>();
		private Map<Type, Color> textColorMap = new HashMap<>();
		private Map<Type, Image> imageMap = new HashMap<>();

		private Font font = Font.font("system", FontWeight.BOLD, 10);

		public AgendaActivityRenderer(GraphicsBase<?> graphics) {
			super(graphics, "Agenda Activities");

			fillColorMap.put(Type.GERMAN, Color.GREEN);
			fillColorMap.put(Type.ENGLISH, Color.CRIMSON);
			fillColorMap.put(Type.BIOLOGY, Color.ORANGE);
			fillColorMap.put(Type.PHYSICS, Color.CORNFLOWERBLUE);
			fillColorMap.put(Type.CHEMISTRY, Color.CADETBLUE);
			fillColorMap.put(Type.MATH, Color.INDIANRED);
			fillColorMap.put(Type.RELIGION, Color.WHEAT);
			fillColorMap.put(Type.SPORT, Color.CORAL);

			strokeColorMap.put(Type.GERMAN, Color.GREEN.darker());
			strokeColorMap.put(Type.ENGLISH, Color.CRIMSON.darker());
			strokeColorMap.put(Type.BIOLOGY, Color.ORANGE.darker());
			strokeColorMap.put(Type.PHYSICS, Color.CORNFLOWERBLUE.darker());
			strokeColorMap.put(Type.CHEMISTRY, Color.CADETBLUE.darker());
			strokeColorMap.put(Type.MATH, Color.INDIANRED.darker());
			strokeColorMap.put(Type.RELIGION, Color.WHEAT.darker());
			strokeColorMap.put(Type.SPORT, Color.CORAL.darker());

			textColorMap.put(Type.GERMAN, Color.GREEN.darker().darker()
					.darker());
			textColorMap.put(Type.ENGLISH, Color.CRIMSON.darker().darker()
					.darker());
			textColorMap.put(Type.BIOLOGY, Color.BISQUE.darker().darker()
					.darker());
			textColorMap.put(Type.PHYSICS, Color.CORNFLOWERBLUE.darker()
					.darker().darker());
			textColorMap.put(Type.CHEMISTRY, Color.CADETBLUE.darker().darker()
					.darker());
			textColorMap.put(Type.MATH, Color.INDIANRED.darker().darker()
					.darker());
			textColorMap.put(Type.RELIGION, Color.WHEAT.darker().darker()
					.darker());
			textColorMap
					.put(Type.SPORT, Color.CORAL.darker().darker().darker());

			imageMap.put(
					Type.GERMAN,
					new Image(AgendaActivityRenderer.class
							.getResourceAsStream("german.png")));
			imageMap.put(
					Type.ENGLISH,
					new Image(AgendaActivityRenderer.class
							.getResourceAsStream("english.png")));
			imageMap.put(
					Type.BIOLOGY,
					new Image(AgendaActivityRenderer.class
							.getResourceAsStream("biology.png")));

			imageMap.put(
					Type.PHYSICS,
					new Image(AgendaActivityRenderer.class
							.getResourceAsStream("physics.png")));

			imageMap.put(
					Type.CHEMISTRY,
					new Image(AgendaActivityRenderer.class
							.getResourceAsStream("chemistry.png")));

			imageMap.put(
					Type.MATH,
					new Image(AgendaActivityRenderer.class
							.getResourceAsStream("math.png")));

			imageMap.put(
					Type.RELIGION,
					new Image(AgendaActivityRenderer.class
							.getResourceAsStream("religion.png")));

			imageMap.put(
					Type.SPORT,
					new Image(AgendaActivityRenderer.class
							.getResourceAsStream("sport.png")));

			setCornerRadius(6);
			setPadding(new Insets(0, 3, 0, 2));

			redrawObservable(showReflections);
			redrawObservable(showIcons);
			redrawObservable(showDebugInfo);
		}

		private final BooleanProperty showReflections = new SimpleBooleanProperty(
				this, "showReflections", false);

		public final BooleanProperty showReflectionsProperty() {
			return showReflections;
		}

		public final void setShowReflections(boolean b) {
			showReflectionsProperty().set(b);
		}

		public final boolean isShowReflections() {
			return showReflectionsProperty().get();
		}

		private final BooleanProperty showDebugInfo = new SimpleBooleanProperty(
				this, "showDebugInfo", false);

		public final BooleanProperty showDebugInfoProperty() {
			return showDebugInfo;
		}

		public final void setisShowDebugInfo(boolean b) {
			showDebugInfoProperty().set(b);
		}

		public final boolean isShowDebugInfo() {
			return showDebugInfoProperty().get();
		}

		private final BooleanProperty showIcons = new SimpleBooleanProperty(
				this, "showIcons", true);

		public final BooleanProperty showIconsProperty() {
			return showIcons;
		}

		public final void setShowIcons(boolean b) {
			showIconsProperty().set(b);
		}

		public final boolean isShowIcons() {
			return showIconsProperty().get();
		}

		@Override
		protected ActivityBounds drawActivity(
				ActivityRef<AgendaActivity> activityRef, Position position,
				GraphicsContext gc, double x, double y, double w, double h,
				boolean selected, boolean hover, boolean highlighted,
				boolean pressed) {

			AgendaActivity entry = activityRef.getActivity();

			Type type = entry.getType();
			setFill(fillColorMap.get(type));
			setFillHover(fillColorMap.get(type).brighter());
			setFillSelected(getFill());
			setFillPressed(getFill());

			setStroke(strokeColorMap.get(type));
			setStrokeHover(strokeColorMap.get(type));
			setStrokeSelected(getStroke());
			setStrokePressed(getStroke());

			if (pressed || selected) {
				setAlpha(1);
			} else {
				setAlpha(.66);
			}

			ActivityBounds bounds = super.drawActivity(activityRef, position,
					gc, x, y, w, h, selected, hover, highlighted, pressed);

			if (w > 50) {

				if (isShowIcons()) {
					Image img = imageMap.get(entry.getType());
					double imgWidth = img.getWidth();
					double imgHeight = img.getHeight();

					if (w > imgWidth + 8 && h > imgHeight + 20) {
						gc.drawImage(img, x + (w - imgWidth) / 2, y
								+ (h - imgHeight) / 2);
					}
				}

				gc.setFont(font);

				Row<?, ?, ?> row = activityRef.getRow();
				ZonedDateTime zonedStart = ZonedDateTime.ofInstant(
						entry.getStartTime(), row.getZoneId());
				ZonedDateTime zonedEnd = ZonedDateTime.ofInstant(
						entry.getEndTime(), row.getZoneId());

				String startText = timeFormatter.format(zonedStart);
				String endText = timeFormatter.format(zonedEnd);

				Insets padding = getPadding();
				x += padding.getLeft();
				y += padding.getTop();
				w -= (padding.getLeft() + padding.getRight());
				h -= (padding.getTop() + padding.getBottom());

				if (h >= 20 && w > 50) {
					gc.setFill(textColorMap.get(type));
					gc.setTextBaseline(VPos.TOP);
					gc.setTextAlign(TextAlignment.RIGHT);
					gc.fillText(startText, snapPositionX(x + w - 4), snapPositionY(y + 4));
				}

				if (h >= 40 && w > 50) {
					switch (position) {
					case FIRST:
					case MIDDLE:
						break;
					case LAST:
					case ONLY:
						gc.setFill(textColorMap.get(type));
						gc.setTextBaseline(VPos.BOTTOM);
						gc.setTextAlign(TextAlignment.RIGHT);
						gc.fillText(endText, snapPositionX(x + w - 4), snapPositionY(y + h - 4));
						break;
					default:
						break;
					}
				}
			}

			return bounds;
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
