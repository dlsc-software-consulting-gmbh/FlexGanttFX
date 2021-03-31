/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.covid;

import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

class GlassPane extends StackPane {

    private final FadeTransition fadeTransition = new FadeTransition();

    public GlassPane() {
        getStyleClass().add("glass-pane");

        setMouseTransparent(false);
        setVisible(false);

        fadeTransition.setDuration(Duration.millis(200));
        fadeTransition.setNode(this);

        hideProperty().addListener((it, oldHide, newHide) -> {
            if (fadeTransition.getStatus().equals(Status.RUNNING)) {
                fadeTransition.stop();
            }

            if (isFadeInOut()) {
                setVisible(true);

                fadeTransition.setFromValue(isHide() ? .5 : 0);
                fadeTransition.setToValue(isHide() ? 0 : .5);
                fadeTransition.setOnFinished(evt -> {
                    if (newHide) {
                        setVisible(false);
                    }
                });
                fadeTransition.play();
            } else {
                setOpacity(newHide ? 0 : .5);
                setVisible(!newHide);
            }
        });
    }

    private final BooleanProperty fadeInOut = new SimpleBooleanProperty(this, "fadeInOut");

    public final boolean isFadeInOut() {
        return fadeInOut.get();
    }

    public final BooleanProperty fadeInOutProperty() {
        return fadeInOut;
    }

    private final BooleanProperty hide = new SimpleBooleanProperty(this, "hide", true);

    public final BooleanProperty hideProperty() {
        return hide;
    }

    public final boolean isHide() {
        return hide.get();
    }
}