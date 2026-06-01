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

	public static final class PathBuilderResult {

		private final Point2D start;
		private final Point2D end;
		private final Path path;
		private final boolean close;
		private final ArrowDirection arrowDirection;

		/**
		 * Constructs a new path builder result.
		 *
		 * @param start
		 *            the start point
		 * @param end
		 *            the end point
		 * @param path
		 *            the path
		 * @param arrowDirection
		 *            the arrow direction
		 * @param close
		 *            whether to close the path
		 */
		public PathBuilderResult(Point2D start, Point2D end, Path path, ArrowDirection arrowDirection, boolean close) {
			this.start = requireNonNull(start);
			this.end = requireNonNull(end);
			this.path = requireNonNull(path);
			this.arrowDirection = requireNonNull(arrowDirection);
			this.close = close;
		}

		/**
		 * Returns the start point.
		 *
		 * @return the start point
		 */
		public Point2D getStart() {
			return start;
		}

		/**
		 * Returns the end point.
		 *
		 * @return the end point
		 */
		public Point2D getEnd() {
			return end;
		}

		/**
		 * Returns the path.
		 *
		 * @return the path
		 */
		public Path getPath() {
			return path;
		}

		/**
		 * Returns the arrow direction.
		 *
		 * @return the arrow direction
		 */
		public ArrowDirection getArrowDirection() {
			return arrowDirection;
		}

		/**
		 * Returns whether the path should be closed.
		 *
		 * @return true if the path should be closed
		 */
		public boolean isClose() {
			return close;
		}

		/**
		 * Returns a string representation of this result.
		 *
		 * @return a string representation of this result
		 */
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

		PathBuilderResult result = new PathBuilderResult(startPoint, endPoint, path, ArrowDirection.RIGHT, close);

		ObservableList<PathElement> pathElements = path.getElements();
		pathElements.add(new MoveTo(sx, sy));

		switch (targetLocation) {
		case BELOW_RIGHT:
		case BELOW:
		case BELOW_LEFT:
			double x1 = Math.min(sx1, tx1);
			pathElements.add(new LineTo(x1 + curve, sy));
			pathElements.add(new QuadCurveTo(x1, sy, x1, sy + curve));
			pathElements.add(new LineTo(x1, ty - curve));
			pathElements.add(new QuadCurveTo(x1, ty, x1 + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case ABOVE_RIGHT:
		case ABOVE_LEFT:
		case ABOVE:
			double x2 = Math.min(sx1, tx1);
			pathElements.add(new LineTo(x2 + curve, sy));
			pathElements.add(new QuadCurveTo(x2, sy, x2, sy - curve));
			pathElements.add(new LineTo(x2, ty + curve));
			pathElements.add(new QuadCurveTo(x2, ty, x2 + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case RIGHT:
			double my1 = sourceRect.getMinY() + sourceRect.getHeight() + gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, my1 - curve));
			pathElements.add(new QuadCurveTo(sx1, my1, sx1 + curve, my1));
			pathElements.add(new LineTo(tx1 - curve, my1));
			pathElements.add(new QuadCurveTo(tx1, my1, tx1, my1 - curve));
			pathElements.add(new LineTo(tx1, ty + curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case LEFT:
			double my2 = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, my2 + curve));
			pathElements.add(new QuadCurveTo(sx1, my2, sx1 - curve, my2));
			pathElements.add(new LineTo(tx1 + curve, my2));
			pathElements.add(new QuadCurveTo(tx1, my2, tx1, my2 + curve));
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
			double x1 = Math.max(sx1, tx1);
			pathElements.add(new LineTo(x1 - curve, sy));
			pathElements.add(new QuadCurveTo(x1, sy, x1, sy + curve));
			pathElements.add(new LineTo(x1, ty - curve));
			pathElements.add(new QuadCurveTo(x1, ty, x1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case ABOVE_RIGHT:
		case ABOVE_LEFT:
		case ABOVE:
			double x2 = Math.max(sx1, tx1);
			pathElements.add(new LineTo(x2 - curve, sy));
			pathElements.add(new QuadCurveTo(x2, sy, x2, sy - curve));
			pathElements.add(new LineTo(x2, ty + curve));
			pathElements.add(new QuadCurveTo(x2, ty, x2 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case RIGHT:
			double my1 = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, my1 + curve));
			pathElements.add(new QuadCurveTo(sx1, my1, sx1 + curve, my1));
			pathElements.add(new LineTo(tx1 - curve, my1));
			pathElements.add(new QuadCurveTo(tx1, my1, tx1, my1 + curve));
			pathElements.add(new LineTo(tx1, ty - curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case LEFT:
			double my2 = sourceRect.getMinY() + sourceRect.getHeight() + gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, my2 - curve));
			pathElements.add(new QuadCurveTo(sx1, my2, sx1 - curve, my2));
			pathElements.add(new LineTo(tx1 + curve, my2));
			pathElements.add(new QuadCurveTo(tx1, my2, tx1, my2 - curve));
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
			double my1 = sourceRect.getMinY() + sourceRect.getHeight() + gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, my1 - curve));
			pathElements.add(new QuadCurveTo(sx1, my1, sx1 + curve, my1));
			pathElements.add(new LineTo(tx1 - curve, my1));
			pathElements.add(new QuadCurveTo(tx1, my1, tx1, my1 + curve));
			pathElements.add(new LineTo(tx1, ty - curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 - curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case ABOVE_RIGHT:
			double my2 = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, my2 + curve));
			pathElements.add(new QuadCurveTo(sx1, my2, sx1 + curve, my2));
			pathElements.add(new LineTo(tx1 - curve, my2));
			pathElements.add(new QuadCurveTo(tx1, my2, tx1, my2 - curve));
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
			double my3 = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 + curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy - curve));
			pathElements.add(new LineTo(sx1, my3 + curve));
			pathElements.add(new QuadCurveTo(sx1, my3, sx1 + curve, my3));
			pathElements.add(new LineTo(tx1 - curve, my3));
			pathElements.add(new QuadCurveTo(tx1, my3, tx1, my3 + curve));
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
			double my1 = sourceRect.getMinY() + sourceRect.getHeight() + gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			pathElements.add(new QuadCurveTo(sx1, sy, sx1, sy + curve));
			pathElements.add(new LineTo(sx1, my1 - curve));
			pathElements.add(new QuadCurveTo(sx1, my1, sx1 - curve, my1));
			pathElements.add(new LineTo(tx1 + curve, my1));
			pathElements.add(new QuadCurveTo(tx1, my1, tx1, my1 + curve));
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
			double my2 = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			double delta1 = (sy - my2) / 2 + 1;
			pathElements.add(new QuadCurveTo(sx1 - curve + delta1, sy - delta1, sx1 - curve, my2));
			pathElements.add(new LineTo(tx1 + curve, my2));
			pathElements.add(new QuadCurveTo(tx1, my2, tx1, my2 - curve));
			pathElements.add(new LineTo(tx1, ty + curve));
			pathElements.add(new QuadCurveTo(tx1, ty, tx1 + curve, ty));
			pathElements.add(new LineTo(tx, ty));
			break;
		case RIGHT:
			pathElements.add(new LineTo(tx, ty));
			break;
		case LEFT:
			double my3 = sourceRect.getMinY() - gap;
			pathElements.add(new LineTo(sx1 - curve, sy));
			double delta2 = (sy - my3) / 2 + 1;
			pathElements.add(new QuadCurveTo(sx1 - curve + delta2, sy - delta2, sx1 - curve, my3));
			pathElements.add(new LineTo(tx1 + curve, my3));
			pathElements.add(new QuadCurveTo(tx1, my3, tx1, my3 + curve));
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
