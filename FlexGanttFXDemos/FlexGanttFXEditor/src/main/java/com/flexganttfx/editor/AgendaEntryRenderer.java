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
package com.flexganttfx.editor;

import com.flexganttfx.editor.AgendaEntryBase.Type;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.renderer.ActivityRenderer;
import com.flexganttfx.view.util.Position;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

/**
 * The renderer used by the showcase application. Not for production use.
 */
public class AgendaEntryRenderer extends ActivityRenderer<AgendaEntryBase> {

	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

	private final Map<Type, Color> fillColorMap = new HashMap<>();
	private final Map<Type, Color> strokeColorMap = new HashMap<>();
	private final Map<Type, Color> textColorMap = new HashMap<>();
	private final Map<Type, Image> imageMap = new HashMap<>();

	private final Font font = Font.font("system", FontWeight.BOLD, 10);

	private final Image linkImage = new Image(AgendaEntryRenderer.class.getResourceAsStream("link.png"));
	private final Image upArrow = new Image(AgendaEntryRenderer.class.getResourceAsStream("arrow_up.png"));
	private final Image downArrow = new Image(AgendaEntryRenderer.class.getResourceAsStream("arrow_down.png"));

	public AgendaEntryRenderer(GraphicsBase<?> graphics) {
		super(graphics, "Agenda Entries");

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

		textColorMap.put(Type.GERMAN, Color.GREEN.darker().darker().darker());
		textColorMap.put(Type.ENGLISH, Color.CRIMSON.darker().darker().darker());
		textColorMap.put(Type.BIOLOGY, Color.BISQUE.darker().darker().darker());
		textColorMap.put(Type.PHYSICS, Color.CORNFLOWERBLUE.darker().darker().darker());
		textColorMap.put(Type.CHEMISTRY, Color.CADETBLUE.darker().darker().darker());
		textColorMap.put(Type.MATH, Color.INDIANRED.darker().darker().darker());
		textColorMap.put(Type.RELIGION, Color.WHEAT.darker().darker().darker());
		textColorMap.put(Type.SPORT, Color.CORAL.darker().darker().darker());

		imageMap.put(
				Type.GERMAN,
				new Image(AgendaEntryRenderer.class
						.getResourceAsStream("german.png")));
		imageMap.put(
				Type.ENGLISH,
				new Image(AgendaEntryRenderer.class
						.getResourceAsStream("english.png")));
		imageMap.put(
				Type.BIOLOGY,
				new Image(AgendaEntryRenderer.class
						.getResourceAsStream("biology.png")));

		imageMap.put(
				Type.PHYSICS,
				new Image(AgendaEntryRenderer.class
						.getResourceAsStream("physics.png")));

		imageMap.put(
				Type.CHEMISTRY,
				new Image(AgendaEntryRenderer.class
						.getResourceAsStream("chemistry.png")));

		imageMap.put(
				Type.MATH,
				new Image(AgendaEntryRenderer.class
						.getResourceAsStream("math.png")));

		imageMap.put(
				Type.RELIGION,
				new Image(AgendaEntryRenderer.class
						.getResourceAsStream("religion.png")));

		imageMap.put(
				Type.SPORT,
				new Image(AgendaEntryRenderer.class
						.getResourceAsStream("sport.png")));

		setCornerRadius(6);
		setPadding(new Insets(0, 3, 0, 2));

		redrawObservable(showReflections);
		redrawObservable(showIcons);
		redrawObservable(showDebugInfo);
	}

	private final BooleanProperty showReflections = new SimpleBooleanProperty(this, "showReflections", false);

	public final BooleanProperty showReflectionsProperty() {
		return showReflections;
	}

	public final void setShowReflections(boolean b) {
		showReflectionsProperty().set(b);
	}

	public final boolean isShowReflections() {
		return showReflectionsProperty().get();
	}

	private final BooleanProperty showDebugInfo = new SimpleBooleanProperty(this, "showDebugInfo", false);

	public final BooleanProperty showDebugInfoProperty() {
		return showDebugInfo;
	}

	public final void setisShowDebugInfo(boolean b) {
		showDebugInfoProperty().set(b);
	}

	public final boolean isShowDebugInfo() {
		return showDebugInfoProperty().get();
	}

	private final BooleanProperty showIcons = new SimpleBooleanProperty(this, "showIcons", true);

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
			ActivityRef<AgendaEntryBase> activityRef, Position position,
			GraphicsContext gc, double x, double y, double w, double h,
			boolean selected, boolean hover, boolean highlighted,
			boolean pressed) {

		AgendaEntryBase entry = activityRef.getActivity();

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

		ActivityBounds bounds = super.drawActivity(activityRef, position, gc,
				x, y, w, h, selected, hover, highlighted, pressed);

		if (w > 50) {
			if (isShowDebugInfo()) {
				gc.fillText(entry.getName(), x + 4, y + h - 16);
			}

			double yy = y + 4;

			ActivityRef<?> hoverActivityRef = getGraphics().getHoverActivity();
			if (hoverActivityRef != null) {
				AgendaEntryBase hoverEntry = (AgendaEntryBase) hoverActivityRef.getActivity();
				Object groupId = hoverEntry.getGroupId();
				if (groupId != null && groupId.equals(entry.getGroupId())) {
					gc.drawImage(linkImage, x + 4, yy);
				}
			}

			yy = y + h / 2;

			if (isShowDebugInfo()) {
				switch (entry.getPushDirection()) {
				case UP:
					gc.drawImage(upArrow, x + 4, yy);
					if (entry.getPusher() != null) {
						gc.fillText(entry.getPusher().getName(), x + 4, yy + 20);
					}
					break;
				case DOWN:
					gc.drawImage(downArrow, x + 4, yy);
					if (entry.getPusher() != null) {
						gc.fillText(entry.getPusher().getName(), x + 4, yy + 20);
					}
					break;
				default:
					break;
				}
			}

			if (isShowIcons()) {
				Image img = imageMap.get(entry.getType());
				double imgWidth = img.getWidth();
				double imgHeight = img.getHeight();

				if (w > imgWidth + 8 && h > imgHeight + 20) {
					gc.drawImage(img, x + (w - imgWidth) / 2, y + (h - imgHeight) / 2);
				}
			}

			gc.setFont(font);

			Row<?, ?, ?> row = activityRef.getRow();
			ZonedDateTime zonedStart = ZonedDateTime.ofInstant(entry.getStartTime(), row.getZoneId());
			ZonedDateTime zonedEnd = ZonedDateTime.ofInstant(entry.getEndTime(), row.getZoneId());

			String startText = timeFormatter.format(zonedStart);
			String endText = timeFormatter.format(zonedEnd);

			String originalStartText = null;
			String originalEndText = null;

			ZonedDateTime originalZonedStart = null;
			if (entry.getOriginalStartTime() != null) {
				originalZonedStart = ZonedDateTime.ofInstant(entry.getOriginalStartTime(), row.getZoneId());
				originalStartText = timeFormatter.format(originalZonedStart);
			}

			ZonedDateTime originalZonedEnd = null;
			if (entry.getOriginalEndTime() != null) {
				originalZonedEnd = ZonedDateTime.ofInstant(entry.getOriginalEndTime(), row.getZoneId());
				originalEndText = timeFormatter.format(originalZonedEnd);
			}

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

				if (isShowDebugInfo() && originalStartText != null) {
					gc.setFill(Color.RED);
					gc.setTextAlign(TextAlignment.LEFT);
					gc.fillText(originalStartText, snapPositionX(x + 4), snapPositionY(y + 4));
				}
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

					if (isShowDebugInfo() && originalEndText != null) {
						gc.setFill(Color.RED);
						gc.setTextAlign(TextAlignment.LEFT);
						gc.fillText(originalEndText, snapPositionX(x + 4), snapPositionY(y + h - 4));
					}
					break;
				default:
					break;
				}
			}
		}

		return bounds;
	}
}