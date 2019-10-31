/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.layer;

import com.flexganttfx.model.Layout;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.AgendaLayout;
import com.flexganttfx.model.layout.ChartLayout;
import com.flexganttfx.model.layout.GanttLayout;
import com.flexganttfx.view.graphics.GraphicsBase;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import impl.com.flexganttfx.skin.util.AgendaHelper;
import impl.com.flexganttfx.skin.util.AgendaHelper.AgendaLineLocation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static javafx.geometry.VPos.CENTER;
import static javafx.scene.text.TextAlignment.RIGHT;

/**
 * Draws a scale for an entire row or for each line within the row. Scales vary
 * depending on the layout used for the row / line. The scale for the chart
 * layout displays the minimum and maximum values while the scale for the agenda
 * layout displays a time scale (8am, 9am, 10am, .....). The labels and dashes
 * in the scale layer have to align perfectly with the lines drawn by the agenda
 * lines layer and the chart lines layer.
 *
 * @param <R>
 *            the type of the rows
 *
 * @see GraphicsBase#getForegroundSystemLayers()
 * @see GraphicsBase#getBackgroundSystemLayers()
 * @see GraphicsBase#getForegroundSystemLayer(Class)
 * @see GraphicsBase#getBackgroundSystemLayer(Class)
 *
 * @since 1.0
 */
public class ScaleLayer<R extends Row<?, ?, ?>> extends SystemLayer<R> {

	public ScaleLayer(GraphicsBase<R> graphics) {
		super("Scale", graphics);

		setNumberFormat(NumberFormat.getIntegerInstance());
		setBackgroundFill(Color.rgb(77, 112, 128, .9));
		setDividerLineStroke(Color.rgb(255, 255, 255, .5));

		// major chart lines and labels
		setMajorChartLinesLineWidth(.5);
		setMajorChartLinesStroke(Color.WHITE);
		setMajorChartLinesVisible(true);
		setMajorChartLinesSize(15);
		setMajorChartLabelsFill(Color.WHITE);
		setMajorChartLabelsVisible(true);

		// minor chart lines and labels
		setMinorChartLinesLineWidth(0.5);
		setMinorChartLinesStroke(Color.LIGHTGRAY);
		setMinorChartLinesVisible(true);
		setMinorChartLinesSize(8);
		setMinorChartLabelsFill(Color.LIGHTGRAY);
		setMinorChartLabelsVisible(false);

		// major agenda lines and labels
		setAgendaLinesLineWidth(.5);
		setAgendaLinesStroke(Color.WHITE);
		setAgendaLinesVisible(true);
		setAgendaLinesSize(15);
		setAgendaLabelsFill(Color.WHITE);
		setAgendaLabelsVisible(true);

		redrawObservable(backgroundFill);
		redrawObservable(bluredBackground);
		redrawObservable(prefWidth);
		redrawObservable(scaleWidth);
		redrawObservable(font);
		redrawObservable(dividerLineStroke);
		redrawObservable(dateTimeFormatter);
		redrawObservable(numberFormat);

		// major chart properties
		redrawObservable(majorChartLinesLineWidth);
		redrawObservable(majorChartLinesSize);
		redrawObservable(majorChartLinesStroke);
		redrawObservable(majorChartLinesVisible);
		redrawObservable(majorChartLabelsVisible);
		redrawObservable(majorChartLabelsFill);

		// minor chart properties
		redrawObservable(minorChartLinesVisible);
		redrawObservable(minorChartLinesSize);
		redrawObservable(minorChartLinesLineWidth);
		redrawObservable(minorChartLinesStroke);
		redrawObservable(minorChartLabelsVisible);
		redrawObservable(minorChartLabelsFill);

		// major agenda properties
		redrawObservable(agendaLinesLineWidth);
		redrawObservable(agendaLinesSize);
		redrawObservable(agendaLinesStroke);
		redrawObservable(agendaLinesVisible);
		redrawObservable(agendaLabelsFill);
		redrawObservable(agendaLabelsVisible);

		fadeInOutObservable(graphics.showScaleLayerProperty());

		graphics.showScaleLayerProperty().addListener(observable -> {
			if (getGraphics().isShowScaleLayer()) {
				slide(getPrefWidth());
			} else {
				slide(0);
			}
		});

		if (graphics.isShowScaleLayer()) {
			scaleWidth.set(getPrefWidth());
		}
	}

	private void slide(double targetWidth) {
		if (getGraphics().isFadeInOutVisibilityChanges()) {
			KeyValue keyValue = new KeyValue(scaleWidth, targetWidth);
			KeyFrame keyFrame = new KeyFrame(Duration.millis(333), keyValue);
			Timeline timeline = new Timeline(keyFrame);
			timeline.play();
		} else {
			scaleWidth.set(targetWidth);
		}
	}

	@Override
	public void drawLayer(RowCanvas<R> canvas, Instant startTime,
			Instant endTime) {
		double canvasHeight = canvas.getHeight();

		double width = getScaleWidth();
		if (width > 0) {
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.setFont(getFont());

			if (isBluredBackground()) {
				updateBackground(canvas);

				if (image != null) {
					gc.setEffect(blur);
					gc.drawImage(image, 0, 0);
					gc.setEffect(null);
				}
			}

			gc.setFill(getBackgroundFill());
			gc.fillRect(0, 0, width, canvasHeight);

			Row<?, ?, ?> row = canvas.getRow();
			if (row != null) {
				double xOffset = 0;
				xOffset = drawLayoutSpecificHeader(xOffset, 0, canvasHeight,
						canvas, row.getLayout());

				for (int lineIndex = 0; lineIndex < row
						.getLineCount(); lineIndex++) {
					Layout lineLayout = row.getLineLayout(lineIndex);
					double lineOffset = row.getLineLocation(lineIndex);
					double lineHeight = row.getLineHeight(lineIndex);
					drawLayoutSpecificHeader(xOffset, lineOffset, lineHeight,
							canvas, lineLayout);

					gc.setLineWidth(.5);
					gc.setStroke(getDividerLineStroke());
					gc.strokeLine(0, ((int) lineOffset + lineHeight) - .5,
							getScaleWidth(),
							((int) lineOffset + lineHeight) - .5);
				}
			}

			gc.setLineWidth(.5);
			gc.setStroke(getDividerLineStroke());
			gc.strokeLine(0, ((int) canvasHeight) - .5, getScaleWidth(),
					((int) canvasHeight) - .5);
		}
	}

	private double drawLayoutSpecificHeader(double xOffset, double yOffset,
			double height, RowCanvas<R> canvas, Layout layout) {

		yOffset += layout.getPadding();
		height -= (layout.getPadding() * 2);

		GraphicsContext gc = canvas.getGraphicsContext2D();

		final GraphicsBase<R> graphics = getGraphics();

		try {
			if (graphics.isSafeRendering()) {
				gc.save();
			}

			if (layout instanceof GanttLayout) {
				return drawLayoutHeaderGantt();
			} else if (layout instanceof AgendaLayout) {
				return drawLayoutHeaderAgenda(yOffset, height, canvas,
						(AgendaLayout) layout);
			} else if (layout instanceof ChartLayout) {
				return drawLayoutHeaderChart(yOffset, height, canvas,
						(ChartLayout) layout);
			}
		} finally {
			if (graphics.isSafeRendering()) {
				gc.restore();
			}
		}

		return xOffset;
	}

	private double drawLayoutHeaderAgenda(double yOffset, double height, RowCanvas<R> canvas, AgendaLayout layout) {

		GraphicsContext gc = canvas.getGraphicsContext2D();

		DateTimeFormatter formatter = getDateTimeFormatter();

		double width = getScaleWidth();

		List<AgendaLineLocation> lines = AgendaHelper.getLineLocations(layout, yOffset, height);

		lines.stream().filter(loc -> !loc.isMinor()).forEach(loc -> {
			if (isAgendaLinesVisible()) {
				gc.setLineWidth(getAgendaLinesLineWidth());
				gc.setStroke(getAgendaLinesStroke());
				gc.strokeLine(width - getAgendaLinesSize(), loc.getLocation(), width, loc.getLocation());
			}

			if (isAgendaLabelsVisible()) {

				LocalTime time = loc.getTime();
				if (time.equals(LocalTime.MAX)) {
					time = LocalTime.MIDNIGHT; // will look prettier than 23:59
				}

				gc.setFill(getAgendaLabelsFill());
				gc.setTextAlign(RIGHT);
				gc.setTextBaseline(CENTER);
				gc.fillText(formatter.format(time), width - getAgendaLinesSize() - 3, loc.getLocation());
			}
		});

		return getScaleWidth();
	}

	private double drawLayoutHeaderGantt() {
		return getScaleWidth();
	}

	private double drawLayoutHeaderChart(double yOffset,
			double height, RowCanvas<R> canvas, ChartLayout layout) {

		GraphicsContext gc = canvas.getGraphicsContext2D();

		double width = getScaleWidth();

		// minor lines

		if (isMinorChartLinesVisible()) {

			gc.setStroke(getMinorChartLinesStroke());
			gc.setLineWidth(getMinorChartLinesLineWidth());

			for (double value : layout.getMinorTicks()) {
				double y = getChartValueLocation(value, yOffset, height,
						layout);
				gc.strokeLine(width - getMinorChartLinesSize(), y, width, y);
			}
		}

		// minor labels

		if (isMinorChartLabelsVisible()) {

			NumberFormat format = getNumberFormat();

			for (double value : layout.getMinorTicks()) {
				double y = getChartValueLocation(value, yOffset, height,
						layout);
				gc.setTextAlign(RIGHT);
				gc.setTextBaseline(CENTER);
				gc.setFill(getMinorChartLabelsFill());
				gc.fillText(format.format(value),
						width - getMinorChartLinesSize() - 3, y);
			}

		}

		// major lines

		if (isMajorChartLinesVisible()) {

			gc.setStroke(getMajorChartLinesStroke());
			gc.setLineWidth(getMajorChartLinesLineWidth());

			for (double value : layout.getMajorTicks()) {
				double y = getChartValueLocation(value, yOffset, height,
						layout);
				gc.strokeLine(width - getMajorChartLinesSize(), y, width, y);
			}
		}

		// major labels

		if (isMajorChartLabelsVisible()) {

			NumberFormat format = getNumberFormat();

			for (double value : layout.getMajorTicks()) {
				double y = getChartValueLocation(value, yOffset, height,
						layout);
				gc.setTextAlign(TextAlignment.RIGHT);
				gc.setTextBaseline(VPos.CENTER);
				gc.setFill(getMajorChartLabelsFill());
				gc.fillText(format.format(value),
						width - getMajorChartLinesSize() - 3, y);
			}

		}

		return getScaleWidth();
	}

	private double getChartValueLocation(double value, double yOffset,
			double height, ChartLayout layout) {

		double minChart = layout.getMinValue();
		double maxChart = layout.getMaxValue();

		double range = maxChart - minChart;
		double ppv = height / range;

		double zeroLineLocation = yOffset + layout.getMaxValue() * ppv;

		return ((int) (zeroLineLocation - value * ppv)) + .5;
	}

	// blur (milk glass effect)
	private final BoxBlur blur = new BoxBlur();

	// background image
	private WritableImage image;

	/**
	 * Updates the background. Create a snapshot of the circle container that
	 * fits exactly this pane's bounds and updates the background.
	 */
	private void updateBackground(RowCanvas<R> canvas) {
		int width = (int) getScaleWidth();
		int height = (int) canvas.getHeight();

		if (width <= 0 || height <= 0) {
			return;
		}

		/*
		 * Creates a new writable image and update background if dimensions do
		 * not match
		 */
		if (image == null || width != (int) image.getWidth()
				|| height != (int) image.getHeight()) {
			image = new WritableImage(width, height);
		}

		// create the snapshot parameters (defines viewport)
		SnapshotParameters sp = new SnapshotParameters();
		Rectangle2D rect = new Rectangle2D(0, 0, width, height);
		sp.setViewport(rect);

		// create the snaphot
		image = canvas.snapshot(sp, image);
	}

	private final ObjectProperty<Font> font = new SimpleObjectProperty<>(this, "font",
			Font.font("System", 8));

	public final ObjectProperty<Font> fontProperty() {
		return font;
	}

	public final void setFont(Font font) {
		Objects.requireNonNull(font);
		fontProperty().set(font);
	}

	public final Font getFont() {
		return fontProperty().get();
	}

	private final ObjectProperty<Paint> dividerLineStroke = new SimpleObjectProperty<>(
			this, "dividerLineStroke");

	public final ObjectProperty<Paint> dividerLineStrokeProperty() {
		return dividerLineStroke;
	}

	public final Paint getDividerLineStroke() {
		return dividerLineStrokeProperty().get();
	}

	public final void setDividerLineStroke(Paint stroke) {
		requireNonNull(stroke);
		dividerLineStrokeProperty().set(stroke);
	}

	private final BooleanProperty bluredBackground = new SimpleBooleanProperty(this,
			"bluredBackground", false);

	public final BooleanProperty bluredBackgroundProperty() {
		return bluredBackground;
	}

	public final boolean isBluredBackground() {
		return bluredBackground.get();
	}

	public final void setBluredBackground(boolean blurred) {
		bluredBackground.set(blurred);
	}

	private final ObjectProperty<Paint> backgroundFill = new SimpleObjectProperty<>(
			this, "backgroundFill");

	public final ObjectProperty<Paint> backgroundFillProperty() {
		return backgroundFill;
	}

	public final Paint getBackgroundFill() {
		return backgroundFillProperty().get();
	}

	public final void setBackgroundFill(Paint fill) {
		Objects.requireNonNull(fill);
		backgroundFillProperty().set(fill);
	}

	private final DoubleProperty prefWidth = new SimpleDoubleProperty(this,
			"prefWidth", 60);

	public final DoubleProperty prefWidthProperty() {
		return prefWidth;
	}

	public final double getPrefWidth() {
		return prefWidth.get();
	}

	public final void setPrefWidth(double width) {
		this.prefWidth.set(width);
	}

	private final ReadOnlyDoubleWrapper scaleWidth = new ReadOnlyDoubleWrapper(this,
			"width", 0);

	public final ReadOnlyDoubleProperty scaleWidthProperty() {
		return scaleWidth.getReadOnlyProperty();
	}

	public final double getScaleWidth() {
		return scaleWidth.get();
	}

	private final BooleanProperty majorChartLabelsVisible = new SimpleBooleanProperty(
			this, "majorChartLabelsVisible", true);

	public final BooleanProperty majorChartLabelsVisibleProperty() {
		return majorChartLabelsVisible;
	}

	public final void setMajorChartLabelsVisible(boolean visible) {
		majorChartLabelsVisibleProperty().set(visible);
	}

	public final boolean isMajorChartLabelsVisible() {
		return majorChartLabelsVisibleProperty().get();
	}

	private final BooleanProperty minorChartLabelsVisible = new SimpleBooleanProperty(
			this, "minorChartLabelsVisible", true);

	public final BooleanProperty minorChartLabelsVisibleProperty() {
		return minorChartLabelsVisible;
	}

	public final void setMinorChartLabelsVisible(boolean visible) {
		minorChartLabelsVisibleProperty().set(visible);
	}

	public final boolean isMinorChartLabelsVisible() {
		return minorChartLabelsVisibleProperty().get();
	}

	private final BooleanProperty majorChartLinesVisible = new SimpleBooleanProperty(
			this, "majorChartLinesVisible", true);

	public final BooleanProperty majorChartLinesVisibleProperty() {
		return majorChartLinesVisible;
	}

	public final void setMajorChartLinesVisible(boolean visible) {
		majorChartLinesVisibleProperty().set(visible);
	}

	public final boolean isMajorChartLinesVisible() {
		return majorChartLinesVisibleProperty().get();
	}

	private final ObjectProperty<Paint> majorChartLinesStroke = new SimpleObjectProperty<>(
			this, "majorChartLinesStroke");

	public final ObjectProperty<Paint> majorChartLinesStrokeProperty() {
		return majorChartLinesStroke;
	}

	public final Paint getMajorChartLinesStroke() {
		return majorChartLinesStrokeProperty().get();
	}

	public final void setMajorChartLinesStroke(Paint stroke) {
		requireNonNull(stroke);
		majorChartLinesStrokeProperty().set(stroke);
	}

	private final ObjectProperty<Paint> majorChartLabelsFill = new SimpleObjectProperty<>(
			this, "majorChartLabelsFill");

	public final ObjectProperty<Paint> majorChartLabelsFillProperty() {
		return majorChartLabelsFill;
	}

	public final Paint getMajorChartLabelsFill() {
		return majorChartLabelsFillProperty().get();
	}

	public final void setMajorChartLabelsFill(Paint fill) {
		requireNonNull(fill);
		majorChartLabelsFillProperty().set(fill);
	}

	private final ObjectProperty<Paint> minorChartLabelsFill = new SimpleObjectProperty<>(
			this, "minorChartLabelsFill");

	public final ObjectProperty<Paint> minorChartLabelsFillProperty() {
		return minorChartLabelsFill;
	}

	public final Paint getMinorChartLabelsFill() {
		return minorChartLabelsFillProperty().get();
	}

	public final void setMinorChartLabelsFill(Paint fill) {
		requireNonNull(fill);
		minorChartLabelsFillProperty().set(fill);
	}

	private final DoubleProperty majorChartLinesLineWidth = new SimpleDoubleProperty(
			this, "majorChartLinesLineWidth");

	public final DoubleProperty majorChartLinesLineWidthProperty() {
		return majorChartLinesLineWidth;
	}

	public final double getMajorChartLinesLineWidth() {
		return majorChartLinesLineWidthProperty().get();
	}

	public final void setMajorChartLinesLineWidth(double lineWidth) {
		majorChartLinesLineWidthProperty().set(lineWidth);
	}

	private final DoubleProperty majorChartLinesSize = new SimpleDoubleProperty(
			this, "majorChartLinesSize");

	public final DoubleProperty majorChartLinesSizeProperty() {
		return majorChartLinesSize;
	}

	public final double getMajorChartLinesSize() {
		return majorChartLinesSizeProperty().get();
	}

	public final void setMajorChartLinesSize(double size) {
		majorChartLinesSizeProperty().set(size);
	}

	private final BooleanProperty minorChartLinesVisible = new SimpleBooleanProperty(
			this, "minorChartLinesVisible", true);

	public final BooleanProperty minorChartLinesVisibleProperty() {
		return minorChartLinesVisible;
	}

	public final void setMinorChartLinesVisible(boolean visible) {
		minorChartLinesVisibleProperty().set(visible);
	}

	public final boolean isMinorChartLinesVisible() {
		return minorChartLinesVisibleProperty().get();
	}

	private final ObjectProperty<Paint> minorChartLinesStroke = new SimpleObjectProperty<>(
			this, "minorChartLinesStroke");

	public final ObjectProperty<Paint> minorChartLinesStrokeProperty() {
		return minorChartLinesStroke;
	}

	public final Paint getMinorChartLinesStroke() {
		return minorChartLinesStrokeProperty().get();
	}

	public final void setMinorChartLinesStroke(Paint stroke) {
		requireNonNull(stroke);
		minorChartLinesStrokeProperty().set(stroke);
	}

	private final DoubleProperty minorChartLinesLineWidth = new SimpleDoubleProperty(
			this, "minorChartLinesLineWidth");

	public final DoubleProperty minorChartLinesLineWidthProperty() {
		return minorChartLinesLineWidth;
	}

	public final double getMinorChartLinesLineWidth() {
		return minorChartLinesLineWidthProperty().get();
	}

	public final void setMinorChartLinesLineWidth(double lineWidth) {
		minorChartLinesLineWidthProperty().set(lineWidth);
	}

	private final DoubleProperty minorChartLinesSize = new SimpleDoubleProperty(
			this, "minorChartLinesSize");

	public final DoubleProperty minorChartLinesSizeProperty() {
		return minorChartLinesSize;
	}

	public final double getMinorChartLinesSize() {
		return minorChartLinesSizeProperty().get();
	}

	public final void setMinorChartLinesSize(double size) {
		minorChartLinesSizeProperty().set(size);
	}

	// Agenda settings.

	private final BooleanProperty agendaLabelsVisible = new SimpleBooleanProperty(
			this, "agendaLabelsVisible", true);

	public final BooleanProperty agendaLabelsVisibleProperty() {
		return agendaLabelsVisible;
	}

	public final void setAgendaLabelsVisible(boolean visible) {
		agendaLabelsVisibleProperty().set(visible);
	}

	public final boolean isAgendaLabelsVisible() {
		return agendaLabelsVisibleProperty().get();
	}

	private final DoubleProperty agendaLinesLineWidth = new SimpleDoubleProperty(
			this, "agendaLinesLineWidth");

	public final DoubleProperty agendaLinesLineWidthProperty() {
		return agendaLinesLineWidth;
	}

	public final double getAgendaLinesLineWidth() {
		return agendaLinesLineWidthProperty().get();
	}

	public final void setAgendaLinesLineWidth(double lineWidth) {
		agendaLinesLineWidthProperty().set(lineWidth);
	}

	private final DoubleProperty agendaLinesSize = new SimpleDoubleProperty(
			this, "agendaLinesSize");

	public final DoubleProperty agendaLinesSizeProperty() {
		return agendaLinesSize;
	}

	public final double getAgendaLinesSize() {
		return agendaLinesSizeProperty().get();
	}

	public final void setAgendaLinesSize(double size) {
		agendaLinesSizeProperty().set(size);
	}

	private final ObjectProperty<Paint> agendaLinesStroke = new SimpleObjectProperty<>(
			this, "agendaLinesStroke");

	public final ObjectProperty<Paint> agendaLinesStrokeProperty() {
		return agendaLinesStroke;
	}

	public final Paint getAgendaLinesStroke() {
		return agendaLinesStrokeProperty().get();
	}

	public final void setAgendaLinesStroke(Paint stroke) {
		requireNonNull(stroke);
		agendaLinesStrokeProperty().set(stroke);
	}

	private final ObjectProperty<Paint> agendaLabelsFill = new SimpleObjectProperty<>(
			this, "agendaLabelsFill");

	public final ObjectProperty<Paint> agendaLabelsFillProperty() {
		return agendaLabelsFill;
	}

	public final Paint getAgendaLabelsFill() {
		return agendaLabelsFillProperty().get();
	}

	public final void setAgendaLabelsFill(Paint fill) {
		requireNonNull(fill);
		agendaLabelsFillProperty().set(fill);
	}

	private final BooleanProperty agendaLinesVisible = new SimpleBooleanProperty(this,
			"agendaLinesVisible", true);

	public final BooleanProperty agendaLinesVisibleProperty() {
		return agendaLinesVisible;
	}

	public final void setAgendaLinesVisible(boolean visible) {
		agendaLinesVisibleProperty().set(visible);
	}

	public final boolean isAgendaLinesVisible() {
		return agendaLinesVisibleProperty().get();
	}

	// Date formatter support.

	private final ObjectProperty<DateTimeFormatter> dateTimeFormatter = new SimpleObjectProperty<>(
			this, "dateTimeFormatter",
			DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));

	public final ObjectProperty<DateTimeFormatter> dateTimeFormatterProperty() {
		return dateTimeFormatter;
	}

	public final void setDateTimeFormatter(DateTimeFormatter formatter) {
		dateTimeFormatterProperty().set(formatter);
	}

	public final DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatter.get();
	}

	// Number format support.

	private final ObjectProperty<NumberFormat> numberFormat = new SimpleObjectProperty<NumberFormat>(
			this, "numberFormat") {

		@Override
		public void set(NumberFormat newValue) {
			if (newValue == null) {
				throw new IllegalArgumentException("number format can not be null");
			}
			super.set(newValue);
		}

	};

	/**
	 * A property used to store a number format that will be used to format
	 * the labels shown when a chart layout is being used.
	 *
	 * @return the number format
	 * @since 1.4
	 */
	public final ObjectProperty<NumberFormat> numberFormatProperty() {
		return numberFormat;
	}

	/**
	 * Returns the value of {@link #getNumberFormat()}.
	 *
	 * @since 1.4
	 * @param format
	 *            the number format to use for chart values
	 */
	public final void setNumberFormat(NumberFormat format) {
		numberFormatProperty().set(format);
	}

	/**
	 * Returns the value of {@link #numberFormatProperty()}.
	 *
	 * @since 1.4
	 * @return the number format for chart values
	 */
	public final NumberFormat getNumberFormat() {
		return numberFormatProperty().get();
	}
}
