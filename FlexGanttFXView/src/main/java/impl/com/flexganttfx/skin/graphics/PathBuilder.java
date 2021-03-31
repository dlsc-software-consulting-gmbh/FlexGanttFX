/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.graphics;

import com.flexganttfx.model.ActivityLink;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.*;

import static java.util.Objects.requireNonNull;

/**
 * The path builder is used to compute path nodes for instances of type
 * {@link ActivityLink}. It is only used by the {@link LinksCanvas}.
 *
 * @since 1.0
 */
public class PathBuilder {

	final class PathBuilderResult {

		private final Point2D start;
		private final Point2D end;
		private final Path path;
		private final boolean close;
		private final ArrowDirection arrowDirection;

		public PathBuilderResult(Point2D start, Point2D end, Path path, ArrowDirection arrowDirection, boolean close) {
			this.start = requireNonNull(start);
			this.end = requireNonNull(end);
			this.path = requireNonNull(path);
			this.arrowDirection = requireNonNull(arrowDirection);
			this.close = close;
		}

		public final Point2D getStart() {
			return start;
		}

		public final Point2D getEnd() {
			return end;
		}

		public final Path getPath() {
			return path;
		}

		public ArrowDirection getArrowDirection() {
			return arrowDirection;
		}

		public boolean isClose() {
			return close;
		}

		@Override
		public String toString() {
			return "start = " + start.toString() + ", end = " + end.toString()
					+ ", path = " + path.toString();
		}
	}

	/**
	 * An enumerator of possible locations that the target object can have
	 * relative to the source object. If the target object is for example in a
	 * row above the source object and the x-coordinate of its start time is
	 * before the x-coordinate of the end time of the source object then it is
	 * located {@link TargetLocation#ABOVE_LEFT}.
	 *
	 * @since 1.0
	 */
	public enum TargetLocation {

		/**
		 * A enumerator value indicating that the target object is located in a
		 * row below the source object and that the x-coordinate of its start
		 * time is larger than the x-coordinate of the end time of the source
		 * object.
		 *
		 * @since 1.0
		 */
		BELOW_RIGHT,

		/**
		 * A enumerator value indicating that the target object is located in a
		 * row below the source object and that the x-coordinate of its start
		 * time is equal to the x-coordinate of the end time of the source
		 * object.
		 *
		 * @since 1.0
		 */
		BELOW,

		/**
		 * A enumerator value indicating that the target object is located in a
		 * row below the source object and that the x-coordinate of its start
		 * time is smaller than the x-coordinate of the end time of the source
		 * object.
		 *
		 * @since 1.0
		 */
		BELOW_LEFT,

		/**
		 * A enumerator value indicating that the target object is located in a
		 * row above the source object and that the x-coordinate of its start
		 * time is larger than the x-coordinate of the end time of the source
		 * object.
		 *
		 * @since 1.0
		 */
		ABOVE_RIGHT,

		/**
		 * A enumerator value indicating that the target object is located in a
		 * row above the source object and that the x-coordinate of its start
		 * time is equal to the x-coordinate of the end time of the source
		 * object.
		 *
		 * @since 1.0
		 */
		ABOVE,

		/**
		 * A enumerator value indicating that the target object is located in a
		 * row above the source object and that the x-coordinate of its start
		 * time is smaller than the x-coordinate of the end time of the source
		 * object.
		 *
		 * @since 1.0
		 */
		ABOVE_LEFT,

		/**
		 * A enumerator value indicating that the target object is located in
		 * the same row as the source object and that the x-coordinate of its
		 * start time is smaller than the x-coordinate of the end time of the
		 * source object.
		 *
		 * @since 1.0
		 */
		LEFT,

		/**
		 * A enumerator value indicating that the target object is located in
		 * the same row as the source object and that the x-coordinate of its
		 * start time is larger than the x-coordinate of the end time of the
		 * source object.
		 *
		 * @since 1.0
		 */
		RIGHT,

		/**
		 * A enumerator value indicating that the target object is located in
		 * the same row as the source object and that the x-coordinate of its
		 * start time is equal to the x-coordinate of the end time of the source
		 * object.
		 *
		 * @since 1.0
		 */
		SAME_LOCATION
	}

	private double offset = 8;

	private double gap = 4;

	private double curve = 6;

	/**
	 * An enum listing the various directions the arrow can be painted.
	 *
	 * @since 1.0
	 */
	public enum ArrowDirection {

		/**
		 * Draws the arrow pointing up.
		 *
		 * @since 1.0
		 */
		UP,

		/**
		 * Draws the arrow pointing down.
		 *
		 * @since 1.0
		 */
		DOWN,

		/**
		 * Draws the arrow pointing left.
		 *
		 * @since 1.0
		 */
		LEFT,

		/**
		 * Draws the arrow pointing right.
		 *
		 * @since 1.0
		 */
		RIGHT
	}

	/**
	 * Constructs a new path calculator.
	 *
	 * @since 1.0
	 */
	public PathBuilder() {
	}

	private TargetLocation calculateTargetLocation(double sx, double sy,
			double tx, double ty) {

		double xDelta = tx - sx;
		if (sy < ty) {
			if (xDelta > 0) {
				return TargetLocation.BELOW_RIGHT;
			} else if (xDelta < 0) {
				return TargetLocation.BELOW_LEFT;
			} else {
				return TargetLocation.BELOW;
			}
		} else if (sy > ty) {
			if (xDelta > 0) {
				return TargetLocation.ABOVE_RIGHT;
			} else if (xDelta < 0) {
				return TargetLocation.ABOVE_LEFT;
			} else {
				return TargetLocation.ABOVE;
			}
		} else {
			if (xDelta > 0) {
				return TargetLocation.RIGHT;
			} else if (xDelta < 0) {
				return TargetLocation.LEFT;
			} else {
				return TargetLocation.SAME_LOCATION;
			}
		}
	}

	/**
	 * Builds a path in the given graphics context from the start of the source
	 * rectangle to the start of the target rectangle.
	 *
	 * @param sourceRect
	 *            the source rectangle
	 * @param targetRect
	 *            the target rectangle
	 * @return the result path
	 *
	 * @since 1.0
	 */
	public PathBuilderResult buildPathStartToStart(
			Rectangle2D sourceRect, Rectangle2D targetRect) {

		double sx = sourceRect.getMinX();
		double sx1 = sx - offset;

		double tx = targetRect.getMinX();
		double tx1 = tx - offset;

		double sy = sourceRect.getMinY() + sourceRect.getHeight() / 2;
		double ty = targetRect.getMinY() + targetRect.getHeight() / 2;

		Point2D startPoint = new Point2D(sx, sy);
		Point2D endPoint = new Point2D(tx, ty);
		Path path = new Path();
		boolean close = false;

		TargetLocation targetLocation = calculateTargetLocation(sx1, sy, tx1, ty);

		/*
		 * Some optimization in case the start and end are on the same y coordinate / same row
		 */
		if (sy == ty && (targetLocation.equals(TargetLocation.RIGHT) || targetLocation.equals(TargetLocation.LEFT))) {
			TargetLocation targetLocationOriginalLocations = calculateTargetLocation(sx, sy, tx, ty);
			if (!targetLocation.equals(targetLocationOriginalLocations)) {
				// source and target rectangles are too close to each other. We can not use the offset.
				sx1 = sx;
				tx1 = tx;
				targetLocation = targetLocationOriginalLocations;
				close = true;
			}
		}

		PathBuilderResult result = new PathBuilderResult(startPoint, endPoint,
				path, ArrowDirection.RIGHT, close);

		ObservableList<PathElement> pathElements = path.getElements();
		pathElements.add(new MoveTo(sx, sy));

		switch (targetLocation) {
		case BELOW_RIGHT:
		case BELOW:
		case BELOW_LEFT:
			double x = Math.min(sx1, tx1);
			pathElements.add(new LineTo(x + curve, sy));
			pathElements.add(new QuadCurveTo(x, sy, x, sy + curve));
			pathElements.add(new LineTo(x, ty - curve));
			pathElements.add(new QuadCurveTo(x, ty, x + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case ABOVE_RIGHT:
		case ABOVE_LEFT:
		case ABOVE:
			x = Math.min(sx1, tx1);
			pathElements.add(new LineTo(x + curve, sy));
			pathElements.add(new QuadCurveTo(x, sy, x, sy - curve));
			pathElements.add(new LineTo(x, ty + curve));
			pathElements.add(new QuadCurveTo(x, ty, x + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case RIGHT:
			double my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, my - curve));
			pathElements.add(new QuadCurveTo(sx1, my, sx1 + curve, my));
			pathElements.add(new LineTo(tx1 - curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my - curve));
			pathElements.add(new LineTo(tx1, ty + curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case LEFT:
			my = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, my + curve));
			pathElements.add(new QuadCurveTo(sx1, my, sx1 - curve, my));
			pathElements.add(new LineTo(tx1 + curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my + curve));
			pathElements.add(new LineTo(tx1, ty - curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case SAME_LOCATION:
			break;
		}

		return result;
	}

	/**
	 * Builds a path in the given graphics context from the end of the source
	 * rectangle to the end of the target rectangle.
	 *
	 * @param sourceRect
	 *            the source rectangle
	 * @param targetRect
	 *            the target rectangle
	 * @return the result path
	 *
	 * @since 1.0
	 */
	public PathBuilderResult buildPathEndToEnd(
			Rectangle2D sourceRect, Rectangle2D targetRect) {

		double sx = sourceRect.getMinX() + sourceRect.getWidth();
		double sx1 = sx + offset;

		double tx = targetRect.getMinX() + targetRect.getWidth();
		double tx1 = tx + offset;

		double sy = sourceRect.getMinY() + sourceRect.getHeight() / 2;
		double ty = targetRect.getMinY() + targetRect.getHeight() / 2;

		Point2D startPoint = new Point2D(sx, sy);
		Point2D endPoint = new Point2D(tx, ty);
		Path path = new Path();

		TargetLocation targetLocation = calculateTargetLocation(sx1, sy, tx1, ty);

		boolean close = false;

		/*
		 * Some optimization in case the start and end are on the same y coordinate / same row
		 */
		if (sy == ty && (targetLocation.equals(TargetLocation.RIGHT) || targetLocation.equals(TargetLocation.LEFT))) {
			TargetLocation targetLocationOriginalLocations = calculateTargetLocation(sx, sy, tx, ty);
			if (!targetLocation.equals(targetLocationOriginalLocations)) {
				// source and target rectangles are too close to each other. We can not use the offset.
				sx1 = sx;
				tx1 = tx;
				targetLocation = targetLocationOriginalLocations;
				close = true;
			}
		}

		PathBuilderResult result = new PathBuilderResult(startPoint, endPoint,
				path, ArrowDirection.LEFT, close);

		ObservableList<PathElement> pathElements = path.getElements();
		pathElements.add(new MoveTo(sx, sy));

		switch (targetLocation) {
		case BELOW_RIGHT:
		case BELOW:
		case BELOW_LEFT:
			double x = Math.max(sx1, tx1);
			pathElements.add(new LineTo(x - curve, sy));
			pathElements.add(new QuadCurveTo(x, sy, x, sy + curve));
			pathElements.add(new LineTo(x, ty - curve));
			pathElements.add(new QuadCurveTo(x, ty, x - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case ABOVE_RIGHT:
		case ABOVE_LEFT:
		case ABOVE:
			x = Math.max(sx1, tx1);
			pathElements.add(new LineTo(x - curve, sy));
			pathElements.add(new QuadCurveTo(x, sy, x, sy - curve));
			pathElements.add(new LineTo(x, ty + curve));
			pathElements.add(new QuadCurveTo(x, ty, x - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case RIGHT:
			double my = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, my + curve));
			pathElements.add(new QuadCurveTo(sx1, my, sx1 + curve, my));
			pathElements.add(new LineTo(tx1 - curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my + curve));
			pathElements.add(new LineTo(tx1, ty - curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case LEFT:
			my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, my - curve));
			pathElements.add(new QuadCurveTo(sx1, my, sx1 - curve, my));
			pathElements.add(new LineTo(tx1 + curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my - curve));
			pathElements.add(new LineTo(tx1, ty + curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case SAME_LOCATION:
			break;
		}

		return result;
	}

	/**
	 * Builds a path in the given graphics context from the start of the source
	 * rectangle to the end of the target rectangle.
	 *
	 * @param sourceRect
	 *            the source rectangle
	 * @param targetRect
	 *            the target rectangle
	 * @return the result path
	 *
	 * @since 1.0
	 */
	public PathBuilderResult buildPathStartToEnd(
			Rectangle2D sourceRect, Rectangle2D targetRect) {

		double sx = sourceRect.getMinX();
		double sx1 = sx - offset;

		double tx = targetRect.getMinX() + targetRect.getWidth();
		double tx1 = tx + offset;

		double sy = sourceRect.getMinY() + sourceRect.getHeight() / 2;
		double ty = targetRect.getMinY() + targetRect.getHeight() / 2;

		Point2D startPoint = new Point2D(sx, sy);
		Point2D endPoint = new Point2D(tx, ty);
		Path path = new Path();

		boolean close = false;

		TargetLocation targetLocation = calculateTargetLocation(sx1, sy, tx1, ty);

		/*
		 * Some optimization in case the start and end are on the same y coordinate / same row
		 */
		if (sy == ty && (targetLocation.equals(TargetLocation.RIGHT) || targetLocation.equals(TargetLocation.LEFT))) {
			TargetLocation targetLocationOriginalLocations = calculateTargetLocation(sx, sy, tx, ty);
			if (!targetLocation.equals(targetLocationOriginalLocations)) {
				// source and target rectangles are too close to each other. We can not use the offset.
				sx1 = sx;
				tx1 = tx;
				targetLocation = targetLocationOriginalLocations;
				close = true;
			}
		}

		PathBuilderResult result = new PathBuilderResult(startPoint, endPoint,
				path, ArrowDirection.LEFT, close);
		ObservableList<PathElement> pathElements = path.getElements();
		pathElements.add(new MoveTo(sx, sy));

		switch (targetLocation) {
		case BELOW:
		case BELOW_LEFT:
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, ty - curve));
			pathElements.add(new QuadCurveTo(sx1, ty, sx1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case BELOW_RIGHT:
			double my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, my - curve));
			pathElements.add(new QuadCurveTo(sx1, my, sx1 + curve, my));
			pathElements.add(new LineTo(tx1 - curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my + curve));
			pathElements.add(new LineTo(tx1, ty - curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case ABOVE_RIGHT:
			my = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, my + curve));
			pathElements.add(new QuadCurveTo(sx1, my, sx1 + curve, my));
			pathElements.add(new LineTo(tx1 - curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my - curve));
			pathElements.add(new LineTo(tx1, ty + curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case ABOVE_LEFT:
		case ABOVE:
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, ty + curve));
			pathElements.add(new QuadCurveTo(sx1, ty, sx1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case RIGHT:
			my = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, my + curve));
			pathElements.add(new QuadCurveTo(sx1, my, sx1 + curve, my));
			pathElements.add(new LineTo(tx1 - curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my + curve));
			pathElements.add(new LineTo(tx1, ty - curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case LEFT:
			pathElements.add(new LineTo(tx, ty));
			break;
		case SAME_LOCATION:
			break;
		}

		return result;
	}

	/**
	 * Builds a path in the given graphics context from the end of the source
	 * rectangle to the start of the target rectangle.
	 *
	 * @param sourceRect
	 *            the source rectangle
	 * @param targetRect
	 *            the target rectangle
	 * @return the result path
	 *
	 * @since 1.0
	 */
	public PathBuilderResult buildPathEndToStart(Rectangle2D sourceRect,
			Rectangle2D targetRect) {

		double sx = snapLocation(sourceRect.getMinX() + sourceRect.getWidth());
		double sx1 = snapLocation(sx + offset);

		double tx = snapLocation(targetRect.getMinX()) + .5;
		double tx1 = snapLocation(tx - offset);

		double sy = snapLocation(sourceRect.getMinY() + sourceRect.getHeight()
				/ 2);
		double ty = snapLocation(targetRect.getMinY() + targetRect.getHeight() / 2);

		Point2D startPoint = new Point2D(sx, sy);
		Point2D endPoint = new Point2D(tx, ty);
		Path path = new Path();

		TargetLocation targetLocation = calculateTargetLocation(sx1, sy, tx1, ty);

		boolean close = false;

		/*
		 * Some optimization in case the start and end are on the same y coordinate / same row
		 */
		if (sy == ty && (targetLocation.equals(TargetLocation.RIGHT) || targetLocation.equals(TargetLocation.LEFT))) {
			TargetLocation targetLocationOriginalLocations = calculateTargetLocation(sx, sy, tx, ty);
			if (!targetLocation.equals(targetLocationOriginalLocations)) {
				// source and target rectangles are too close to each other. We can not use the offset.
				sx1 = sx;
				tx1 = tx;
				targetLocation = targetLocationOriginalLocations;
				close = true;
			}
		}

		PathBuilderResult result = new PathBuilderResult(startPoint, endPoint,
				path, ArrowDirection.RIGHT, close);

		ObservableList<PathElement> pathElements = path.getElements();
		pathElements.add(new MoveTo(sx, sy));

		switch (targetLocation) {
		case BELOW_RIGHT:
			pathElements.add(new LineTo(sx1 - curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, ty - curve));
			pathElements.add(new QuadCurveTo(sx1, ty, sx1 + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case BELOW_LEFT:
		case BELOW:
			double my = sourceRect.getMinY() + sourceRect.getHeight() + gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, my - curve));
			pathElements.add(new QuadCurveTo(sx1, my, sx1 - curve, my));
			pathElements.add(new LineTo(tx1 + curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my + curve));
			pathElements.add(new LineTo(tx1, ty - curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case ABOVE_RIGHT:
			pathElements.add(new LineTo(sx1 - curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, ty + curve));
			pathElements.add(new QuadCurveTo(sx1, ty, sx1 + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case ABOVE_LEFT:
		case ABOVE:
			my = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			double delta = (sy - my) / 2 + 1;
			pathElements.add(
					new QuadCurveTo(sx1 - curve + delta, sy - delta, sx1
							- curve, my));
			pathElements.add(new LineTo(tx1 + curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my - curve));
			pathElements.add(new LineTo(tx1, ty + curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case RIGHT:
			pathElements.add(new LineTo(tx, ty));
			break;
		case LEFT:
			my = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			delta = (sy - my) / 2 + 1;
			pathElements.add(
					new QuadCurveTo(sx1 - curve + delta, sy - delta, sx1
							- curve, my));
			pathElements.add(new LineTo(tx1 + curve, my));
			pathElements.add(new QuadCurveTo(tx1, my, tx1, my + curve));
			pathElements.add(new LineTo(tx1, ty - curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 + curve, ty));
			break;
		case SAME_LOCATION:
			break;
		}

		return result;
	}

	/**
	 * The offset determines the end location of the first segment of the
	 * calculated path. The first segment is used to move away from the start or
	 * end bounds before continuing to draw up or down.
	 *
	 * @return the offset
	 * @since 1.0
	 */
	public final double getOffset() {
		return offset;
	}

	/**
	 * The offset determines the end location of the first segment of the
	 * calculated path. The first segment is used to move away from the start or
	 * end bounds before continuing to draw up or down.
	 *
	 * @param offset
	 *            the offset in pixels
	 * @since 1.0
	 */
	public final void setOffset(double offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("offset can not be negative");
		}
		if (curve > offset) {
			throw new IllegalArgumentException(
					"curve can not be larger than the offset (requested offset = "
							+ offset + ", current curve = " + curve);
		}
		this.offset = offset;
	}

	/**
	 * Sets the radius for the curve. The radius can not be larger than the
	 * offset (see {@link #setOffset(double)}). Setting this value to 0 results
	 * in corners instead of curves.
	 *
	 * @param curve
	 *            the radius of the curve
	 * @since 1.0
	 */
	public final void setCurve(double curve) {
		if (curve < 0) {
			throw new IllegalArgumentException("curve can not be negative");
		}
		if (curve > offset) {
			throw new IllegalArgumentException(
					"curve can not be larger than the offset (current offset = "
							+ offset + ", requested curve = " + curve);
		}
		this.curve = curve;
	}

	/**
	 * Returns the radius of the curve.
	 *
	 * @return the curve radius
	 * @since 1.0
	 */
	public final double getCurve() {
		return curve;
	}

	/**
	 * The gap determines how far the line is drawn away from the bounds of the
	 * source or target timeline object.
	 *
	 * @return the gap between line and timeline objects
	 * @since 1.0
	 */
	public final double getGap() {
		return gap;
	}

	/**
	 * The gap determines how far the line is drawn away from the bounds of the
	 * source or target timeline object.
	 *
	 * @param gap
	 *            the distance between line and timeline objects
	 * @since 1.0
	 */
	public final void setGap(double gap) {
		this.gap = gap;
	}

	private double snapLocation(double location) {
		return ((int) location) + .5;
	}
}
