/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.extras.util;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.text.MessageFormat;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * A manager for storing the location and dimension of a stage across user
 * sessions. Installing this manager on a stage will ensure that a stage will
 * present itself at the same location and in the same size that it had when
 * the user closed it the last time. This manager also works with multiple
 * screens and will ensure that the window becomes visible if the last used
 * screen is no longer available. In that case the stage will be shown centered
 * on the primary screen with the specified min width and min height.
 *
 * <p>Based on the original {@code StageManager} from the GemsFX library.</p>
 *
 * @since 1.0
 */
public class StageManager {

    private final Logger LOG = Logger.getLogger(StageManager.class.getSimpleName());

    private final Stage stage;
    private final Preferences preferences;

    private final double minWidth;
    private final double minHeight;

    private boolean supportFullScreenAndMaximized;

    /**
     * Installs a new manager for the given stage. The location and dimension
     * information will be stored in the user preferences at the given path.
     * The default values for the minimum width is 850 and for the minimum
     * height is 600.
     *
     * @param stage the stage to persist and restore
     * @param preferencesPath the java.util preferences path used for storing
     *                        the information
     * @return the installed manager
     */
    public static StageManager install(Stage stage, String preferencesPath) {
        return install(stage, Preferences.userRoot().node(preferencesPath));
    }

    /**
     * Installs a new manager for the given stage. The location and dimension
     * information will be stored in the given preferences. The default values
     * for the minimum width is 850 and for the minimum height is 600.
     *
     * @param stage the stage to persist and restore
     * @param preferences the java.util preferences used for storing the
     *                    information
     * @return the installed manager
     */
    public static StageManager install(Stage stage, Preferences preferences) {
        return install(stage, preferences, 850, 600);
    }

    /**
     * Installs a new manager for the given stage. The location and dimension
     * information will be stored in the user preferences at the given path.
     *
     * @param stage the stage to persist and restore
     * @param preferencesPath the java.util preferences path used for storing
     *                        the information
     * @param minWidth the minimum width that will be used for the stage
     * @param minHeight the minimum height that will be used for the stage
     * @return the installed manager
     */
    public static StageManager install(Stage stage, String preferencesPath, double minWidth, double minHeight) {
        return install(stage, Preferences.userRoot().node(preferencesPath), minWidth, minHeight);
    }

    /**
     * Installs a new manager for the given stage. The location and dimension
     * information will be stored in the given preferences.
     *
     * @param stage the stage to persist and restore
     * @param preferences the java.util preferences used for storing the
     *                    information
     * @param minWidth the minimum width that will be used for the stage
     * @param minHeight the minimum height that will be used for the stage
     * @return the installed manager
     */
    public static StageManager install(Stage stage, Preferences preferences, double minWidth, double minHeight) {
        return new StageManager(stage, preferences, minWidth, minHeight);
    }

    private StageManager(Stage stage, Preferences preferences, double minWidth, double minHeight) {
        if (minWidth <= 0) {
            throw new IllegalArgumentException("min width must be larger than 0");
        }
        if (minHeight <= 0) {
            throw new IllegalArgumentException("min height must be larger than 0");
        }

        this.stage = stage;
        this.preferences = preferences;
        this.minWidth = minWidth;
        this.minHeight = minHeight;

        restoreStage();

        InvalidationListener stageListener = it -> {
            try {
                saveStage();
            } catch (SecurityException ex) {
                LOG.throwing(StageManager.class.getName(), "init", ex);
            }
        };

        stage.xProperty().addListener(stageListener);
        stage.yProperty().addListener(stageListener);
        stage.widthProperty().addListener(stageListener);
        stage.heightProperty().addListener(stageListener);
        stage.iconifiedProperty().addListener(stageListener);
        stage.maximizedProperty().addListener(stageListener);
    }

    /**
     * Returns whether full-screen and maximized states are persisted and
     * restored.
     *
     * @return {@code true} if full-screen and maximized support is enabled
     */
    public final boolean isSupportFullScreenAndMaximized() {
        return supportFullScreenAndMaximized;
    }

    /**
     * Sets whether full-screen and maximized states should be persisted and
     * restored.
     *
     * @param supportFullScreenAndMaximized {@code true} to enable support
     */
    public final void setSupportFullScreenAndMaximized(boolean supportFullScreenAndMaximized) {
        this.supportFullScreenAndMaximized = supportFullScreenAndMaximized;
    }

    private void saveStage() throws SecurityException {
        if (supportFullScreenAndMaximized && stage.isMaximized()) {
            LOG.fine(MessageFormat.format("saving stage, iconified = {0}, maximized = {1}, fullscreen = {2}",
                    stage.isIconified(), stage.isMaximized(), stage.isFullScreen()));
        } else {
            LOG.fine(MessageFormat.format("saving stage, x = {0}, y = {1}, width = {2}, height = {3}, iconified = {4}, maximized = {5}, fullscreen = {6}",
                    stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight(),
                    stage.isIconified(), stage.isMaximized(), stage.isFullScreen()));
        }

        if (!supportFullScreenAndMaximized || (!stage.isMaximized() && !stage.isFullScreen())) {
            preferences.putDouble("x", stage.getX());
            preferences.putDouble("y", stage.getY());
            preferences.putDouble("width", stage.getWidth());
            preferences.putDouble("height", stage.getHeight());
        }

        preferences.putBoolean("iconified", stage.isIconified());
        preferences.putBoolean("maximized", stage.isMaximized());
        preferences.putBoolean("fullscreen", stage.isFullScreen());
    }

    private void restoreStage() throws SecurityException {
        double x = preferences.getDouble("x", -1);
        double y = preferences.getDouble("y", -1);
        double w = preferences.getDouble("width", stage.getWidth());
        double h = preferences.getDouble("height", stage.getHeight());

        boolean iconified = preferences.getBoolean("iconified", false);
        boolean maximized = preferences.getBoolean("maximized", false);
        boolean fullscreen = preferences.getBoolean("fullscreen", false);

        LOG.fine(MessageFormat.format("loading stage, x = {0}, y = {1}, width = {2}, height = {3}, iconified = {4}, maximized = {5}, fullscreen = {6}",
                x, y, w, h, iconified, maximized, fullscreen));

        if (x == -1 && y == -1) {
            stage.centerOnScreen();
        } else {
            stage.setX(x);
            stage.setY(y);
        }

        stage.setWidth(Math.max(minWidth, w));
        stage.setHeight(Math.max(minHeight, h));

        Platform.runLater(() -> {
            stage.setIconified(iconified);

            if (supportFullScreenAndMaximized) {
                stage.setMaximized(maximized);
                stage.setFullScreen(fullscreen);
            }

            if (isWindowOutOfBounds()) {
                LOG.fine("stage is out of bounds, moving it to primary screen");
                moveToPrimaryScreen();
            }
        });
    }

    private boolean isWindowOutOfBounds() {
        for (Screen screen : Screen.getScreens()) {
            Rectangle2D bounds = screen.getVisualBounds();
            if (stage.getX() + stage.getWidth() - minWidth >= bounds.getMinX() &&
                    stage.getX() + minWidth <= bounds.getMaxX() &&
                    bounds.getMinY() <= stage.getY() &&
                    stage.getY() + minHeight <= bounds.getMaxY()) {
                return false;
            }
        }
        return true;
    }

    private void moveToPrimaryScreen() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double centerX = bounds.getMinX() + (bounds.getWidth() - minWidth) / 2;
        double centerY = bounds.getMinY() + (bounds.getHeight() - minHeight) / 2;
        stage.setX(centerX);
        stage.setY(centerY);
        stage.setWidth(minWidth);
        stage.setHeight(minHeight);
    }
}
