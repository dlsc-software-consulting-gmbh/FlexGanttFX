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

import com.flexganttfx.model.Row;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.RowEditorParameter;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Orientation;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import static java.lang.Double.MAX_VALUE;
import static java.util.Objects.requireNonNull;

public class FlipPane<R extends Row<?, ?, ?>> extends StackPane {
    private final StackPane front;
    private final StackPane back;
    private final Rotate rotate;
    private final Rotate backRotate;
    private final Timeline flipToFront;
    private final Timeline flipToBack;
    private final Orientation flipDirection;
    private double flipTime;
    private RowPane<R> rowPane;
    private R row;
    private double previousRowHeight;
    private Node controlsNode;

    FlipPane(RowPane<R> rowCell) {
        this(Orientation.VERTICAL);

        requireNonNull(rowCell);

        this.rowPane = rowCell;

        getStyleClass().add("flip-pane");
    }

    private FlipPane(final Orientation FLIP_DIRECTION) {
        rotate = new Rotate(0, Rotate.Y_AXIS);
        getTransforms().add(rotate);

        backRotate = new Rotate(180, Orientation.HORIZONTAL == FLIP_DIRECTION ? Rotate.Y_AXIS : Rotate.X_AXIS);

        front = new StackPane();
        back = new StackPane();

        front.setMinWidth(0);
        back.setMinWidth(0);

        front.setPrefHeight(Region.USE_PREF_SIZE);
        back.setPrefHeight(Region.USE_PREF_SIZE);

        front.setPrefWidth(0);
        back.setPrefWidth(0);

        back.setVisible(false);

        getChildren().setAll(back, front);

        flipToFront = new Timeline();
        flipToBack = new Timeline();
        flipTime = 666;
        flipDirection = FLIP_DIRECTION;

        registerListeners();

        /*
         * Needed so that the row width / height never depends on the min or max
         * size of the flip pane.
         */
        setMinSize(0, 0);
        setMaxSize(MAX_VALUE, MAX_VALUE);
    }

    public final void setRow(R row) {
        if (this.row != row) {
            this.row = row;

            getBack().getChildren().clear();
            if (row != null) {
                GraphicsBase<R> graphics = rowPane.getGraphics();
                RowEditorParameter<R> param = new RowEditorParameter<>(graphics, row);
                Node node = graphics.getRowEditorFactory().call(param);
                if (node != null) {
                    getBack().getChildren().add(node);
                }
            }
        }

        this.row = row;
    }

    // ******************** Methods *******************************************

    private void registerListeners() {
        InvalidationListener adjustRotationListener = it -> adjustRotationAxis();

        front.widthProperty().addListener(adjustRotationListener);
        front.heightProperty().addListener(adjustRotationListener);
        back.widthProperty().addListener(adjustRotationListener);
        back.heightProperty().addListener(adjustRotationListener);
        rotate.angleProperty().addListener((ov, o, n) -> {
            if (Double.compare(o.doubleValue(), 90) < 0 && Double.compare(n.doubleValue(), 90) >= 0) {
                front.setVisible(false);
                back.setVisible(true);
            }
            if (Double.compare(o.doubleValue(), 90) > 0 && Double.compare(n.doubleValue(), 90) <= 0) {
                back.setVisible(false);
                front.setVisible(true);
            }
        });
    }

    private void hideControlsNode() {
        controlsNode = rowPane.getControlsNode();
        if (controlsNode != null) {
            controlsNode.setVisible(false);
        }
    }

    private void showControlsNode() {
        if (controlsNode != null) {
            controlsNode.setVisible(true);
        }
    }

    public final StackPane getFront() {
        return front;
    }

    public final StackPane getBack() {
        return back;
    }

    public final void flipToFrontImmediately() {
        showControlsNode();

        row.setHeight(previousRowHeight);

        rotate.setAngle(0);
        back.setVisible(false);
        front.setVisible(true);
    }

    public final void flipToBackImmediately() {
        hideControlsNode();

        previousRowHeight = row.getHeight();

        row.setHeight(calculatePrefHeightOfBack());

        rotate.setAngle(180);
        back.setVisible(true);
        front.setVisible(false);
    }

    public final void flipToFront() {
        if (Double.compare(rotate.getAngle(), 0) == 0) {
            return;
        }

        KeyValue kvRowHeight = new KeyValue(row.heightProperty(), previousRowHeight);

        KeyValue kvRotateStart = new KeyValue(rotate.angleProperty(), 180, Interpolator.EASE_IN);
        KeyValue kvRotateStop = new KeyValue(rotate.angleProperty(), 0, Interpolator.EASE_OUT);

        KeyValue kvScaleXStart = new KeyValue(scaleXProperty(), 1);
        KeyValue kvScaleYStart = new KeyValue(scaleYProperty(), 1);

        KeyValue kvScaleXMiddle = new KeyValue(scaleXProperty(), .8, Interpolator.EASE_BOTH);
        KeyValue kvScaleYMiddle = new KeyValue(scaleYProperty(), .8, Interpolator.EASE_BOTH);

        KeyValue kvScaleXStop = new KeyValue(scaleXProperty(), 1);
        KeyValue kvScaleYStop = new KeyValue(scaleYProperty(), 1);

        KeyFrame kfStart = new KeyFrame(Duration.ZERO, kvRotateStart, kvScaleXStart, kvScaleYStart);
        KeyFrame kfMiddle = new KeyFrame(Duration.millis(flipTime / 4), kvScaleXMiddle, kvScaleYMiddle);
        KeyFrame kfStop = new KeyFrame(Duration.millis(flipTime), kvRotateStop, kvScaleXStop, kvScaleYStop, kvRowHeight);

        flipToFront.getKeyFrames().setAll(kfStart, kfMiddle, kfStop);
        front.setCache(true);
        front.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        back.setCache(true);
        back.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        flipToFront.setOnFinished(event -> {
            front.setCache(false);
            back.setCache(false);
            fireEvent(new FlipEvent(FlipPane.this, FlipPane.this, FlipEvent.FLIP_TO_FRONT_FINISHED));
            getScene().setCamera(null);
            showControlsNode();
        });
        flipToFront.play();
    }

    public final void flipToBack() {
        hideControlsNode();

        if (Double.compare(rotate.getAngle(), 180) == 0) {
            return;
        }

        previousRowHeight = row.getHeight();

        KeyValue kvRowHeight = new KeyValue(row.heightProperty(), calculatePrefHeightOfBack());

        KeyValue kvRotateStart = new KeyValue(rotate.angleProperty(), 0, Interpolator.EASE_IN);
        KeyValue kvRotateStop = new KeyValue(rotate.angleProperty(), 180, Interpolator.EASE_OUT);

        KeyValue kvScaleXStart = new KeyValue(scaleXProperty(), 1);
        KeyValue kvScaleYStart = new KeyValue(scaleYProperty(), 1);

        KeyValue kvScaleXMiddle = new KeyValue(scaleXProperty(), .8, Interpolator.EASE_BOTH);
        KeyValue kvScaleYMiddle = new KeyValue(scaleYProperty(), .8, Interpolator.EASE_BOTH);

        KeyValue kvScaleXStop = new KeyValue(scaleXProperty(), 1);
        KeyValue kvScaleYStop = new KeyValue(scaleYProperty(), 1);

        KeyFrame kfStart = new KeyFrame(Duration.ZERO, kvRotateStart, kvScaleXStart, kvScaleYStart);
        KeyFrame kfMiddle = new KeyFrame(Duration.millis(flipTime / 4), kvScaleXMiddle, kvScaleYMiddle);
        KeyFrame kfStop = new KeyFrame(Duration.millis(flipTime), kvRotateStop, kvScaleXStop, kvScaleYStop, kvRowHeight);

        flipToBack.getKeyFrames().setAll(kfStart, kfMiddle, kfStop);

        front.setCache(true);
        front.setCacheHint(CacheHint.SCALE_AND_ROTATE);
        back.setCache(true);
        back.setCacheHint(CacheHint.SCALE_AND_ROTATE);

        flipToBack.setOnFinished(event -> {
            front.setCache(false);
            back.setCache(false);
            fireEvent(new FlipEvent(FlipPane.this, FlipPane.this, FlipEvent.FLIP_TO_BACK_FINISHED));
            getScene().setCamera(null);
        });

        flipToBack.play();
    }

    private double calculatePrefHeightOfBack() {
        return getBack().getChildren().get(0).prefHeight(rowPane.getWidth());
    }

    public final boolean isFrontVisible() {
        return front.isVisible();
    }

    public final boolean isBackVisible() {
        return back.isVisible();
    }

    public final void setFlipTime(final double FLIP_TIME) {
        flipTime = clamp(100, 2000, FLIP_TIME);
    }

    private void adjustRotationAxis() {
        if (front.getWidth() < 0 || back.getWidth() < 0 || front.getHeight() < 0 || back.getHeight() < 0) {
            return;
        }

        double width = front.getWidth() > back.getWidth() ? front.getWidth() : back.getWidth();
        double height = front.getHeight() > back.getHeight() ? front.getHeight() : back.getHeight();
        setPrefSize(width, height);

        if (Orientation.HORIZONTAL == flipDirection) {
            backRotate.setAngle(0);
            backRotate.setAxis(Rotate.Y_AXIS);
            backRotate.setPivotX(0.5 * width);
            backRotate.setAngle(180);
            back.getTransforms().setAll(backRotate);

            rotate.setAxis(Rotate.Y_AXIS);
            rotate.setPivotX(0.5 * width);
        } else {
            backRotate.setAngle(0);
            backRotate.setAxis(Rotate.X_AXIS);
            backRotate.setPivotY(0.5 * height);
            backRotate.setAngle(180);
            back.getTransforms().setAll(backRotate);

            rotate.setAxis(Rotate.X_AXIS);
            rotate.setPivotY(0.5 * height);
        }
    }

    private double clamp(final double MIN, final double MAX, final double VALUE) {
        if (VALUE < MIN) {
            return MIN;
        }
        if (VALUE > MAX) {
            return MAX;
        }
        return VALUE;
    }

    public static class FlipEvent extends Event {

        public static final EventType<FlipEvent> FLIP_TO_FRONT_FINISHED = new EventType<>(ANY, "flipToFrontFinished");
        public static final EventType<FlipEvent> FLIP_TO_BACK_FINISHED = new EventType<>(ANY, "flipToBackFinished");
        private static final long serialVersionUID = 2323146240921802205L;

        public FlipEvent(final Object SOURCE, final EventTarget TARGET, final EventType<FlipEvent> EVENT_TYPE) {
            super(SOURCE, TARGET, EVENT_TYPE);
        }
    }
}