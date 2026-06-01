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
package impl.com.flexganttfx.skin.timeline;

import com.flexganttfx.view.timeline.Dateline;
import com.flexganttfx.view.timeline.Eventline;
import com.flexganttfx.view.timeline.Timeline;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Locale;

import static java.time.format.TextStyle.FULL;

/**
 * Skin for {@link com.flexganttfx.view.timeline.Timeline}. It composes the dateline and
 * eventline nodes, updates the time-zone label, and handles keyboard-driven timeline
 * scrolling.
 */
public class TimelineSkin extends SkinBase<Timeline> {

    private final Dateline dateline;
    private final Eventline eventline;
    private final Label zoneIdLabel;

    private final VBox vbox;

    public TimelineSkin(final Timeline timeline) {
        super(timeline);

        this.dateline = timeline.getDateline();
        this.eventline = timeline.getEventline();

        this.zoneIdLabel = new Label();
        this.zoneIdLabel.getStyleClass().add("zone-id-label");
        this.zoneIdLabel.setLayoutX(0);
        this.zoneIdLabel.setLayoutY(0);
        this.zoneIdLabel.setVisible(timeline.isZoneIdVisible());
        this.zoneIdLabel.setManaged(false);

        updateZoneIdLabel(false);

        EventHandler<KeyEvent> arrowKeysHandler = event -> {
            switch (event.getCode()) {
                case RIGHT:
                    if (event.isShiftDown()) {
                        getSkinnable().scrollRightFast();
                    } else {
                        getSkinnable().scrollRight();
                    }
                    event.consume();
                    break;
                case LEFT:
                    if (event.isShiftDown()) {
                        getSkinnable().scrollLeftFast();
                    } else {
                        getSkinnable().scrollLeft();
                    }
                    event.consume();
                    break;
                default:
                    break;
            }
        };

        EventHandler<KeyEvent> plusMinusKeyHandler = event -> {
            switch (event.getCharacter()) {
                case "-":
                    timeline.zoomOut();
                    break;
                case "+":
                    timeline.zoomIn();
                    break;
                default:
                    break;
            }
        };

        timeline.addEventHandler(KeyEvent.KEY_PRESSED, arrowKeysHandler);
        timeline.addEventHandler(KeyEvent.KEY_TYPED, plusMinusKeyHandler);

        VBox.setVgrow(dateline, Priority.ALWAYS);
        VBox.setVgrow(eventline, Priority.NEVER);

        vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getStyleClass().add("dateline-eventline-wrapper");
        vbox.setFillWidth(true);

        /*
         * Very important to set the pref and the min width here as otherwise the eventline
         * will be as wide as the dateline, including the dateline's buffer. But then the graphics
         * inside the eventline will use the dateline buffer AND the canvas buffer causing
         * activities to be drawn incorrectly. Run "HelloGlobalActivities" for an example.
         */
        vbox.setPrefWidth(0);
        vbox.setMinWidth(0);

        getChildren().addAll(vbox, zoneIdLabel);

        dateline.minWidthProperty().bind(vbox.widthProperty().add(dateline.datelineBufferProperty().multiply(2)));

        final Rectangle clip = new Rectangle();
        clip.widthProperty().bind(vbox.widthProperty());
        clip.heightProperty().bind(vbox.heightProperty());

        if (!Boolean.getBoolean("timeline.no.clip")) {
            vbox.setClip(clip);
        }

        dateline.zoneIdProperty().addListener(
                (value, oldZoneId, newZoneId) -> {
                    updateZoneIdLabel(true);
                    getSkinnable().requestLayout();
                });

        timeline.zoneIdVisibleProperty().addListener(
                (value, oldValue, newValue) -> {
                    if (newValue) {
                        showZoneIdLabel();
                    } else {
                        hideZoneIdLabel();
                    }
                });

        dateline.visibleProperty().addListener(it -> updateVisibilities());
        eventline.visibleProperty().addListener(it -> updateVisibilities());

        updateVisibilities();
    }

    /**
     * Lays out the children of this skin.
     *
     * @param contentX the content x coordinate
     * @param contentY the content y coordinate
     * @param contentWidth the content width
     * @param contentHeight the content height
     */
    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        double w = Math.min(contentWidth, zoneIdLabel.prefWidth(-1));
        double h = zoneIdLabel.prefHeight(-1);

        zoneIdLabel.resizeRelocate(contentWidth - w - 10, zoneIdLabel.getLayoutY(), w, h);

    }

    private void updateVisibilities() {
        vbox.getChildren().clear();

        if (dateline.isVisible()) {
            vbox.getChildren().add(dateline);
        }

        if (eventline.isVisible()) {
            vbox.getChildren().add(eventline);
        }
    }

    /**
     * Returns the timeline control.
     *
     * @return the timeline control
     */
    public Timeline getTimeline() {
        return getSkinnable();
    }

    private void showZoneIdLabel() {
        zoneIdLabel.setOpacity(0);
        zoneIdLabel.setVisible(true);
        double labelHeight = zoneIdLabel.prefHeight(-1);

        TranslateTransition tt = new TranslateTransition(Duration.millis(500), zoneIdLabel);
        tt.setFromY(-labelHeight);
        tt.setToY(0);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(.5), zoneIdLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ParallelTransition parallelTransition = new ParallelTransition(fadeIn, tt);
        parallelTransition.play();
    }

    private void hideZoneIdLabel() {
        double labelHeight = zoneIdLabel.prefHeight(-1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), zoneIdLabel);
        tt.setToY(-labelHeight);
        tt.setFromY(zoneIdLabel.getLayoutY());

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(.5), zoneIdLabel);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(evt -> updateZoneIdText());

        ParallelTransition parallelTransition = new ParallelTransition(tt, fadeOut);
        parallelTransition.play();
    }

    private void updateZoneIdLabel(boolean animate) {
        if (animate) {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(.5), zoneIdLabel);
            fadeOut.setToValue(.1);
            fadeOut.setOnFinished(evt -> updateZoneIdText());

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(.5), zoneIdLabel);
            fadeIn.setToValue(1);

            SequentialTransition sequentialTransition = new SequentialTransition(fadeOut, fadeIn);
            sequentialTransition.play();
        } else {
            updateZoneIdText();
        }
    }

    private void updateZoneIdText() {
        String text = getSkinnable().getDateline().getZoneId().getDisplayName(FULL, Locale.getDefault());
        zoneIdLabel.setText(text);
        getSkinnable().requestLayout();
    }
}
