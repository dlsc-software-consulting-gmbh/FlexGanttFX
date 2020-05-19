/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 * <p>
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics.renderer;

import com.flexganttfx.view.graphics.renderer.LinkRenderer.ArrowDirection;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class RenderingBug extends Application {

    @Override
    public void start(Stage primaryStage) {
        MyCanvas canvas = new MyCanvas();
        StackPane stackPane = new StackPane(canvas);

        canvas.widthProperty().bind(stackPane.widthProperty());
        canvas.heightProperty().bind(stackPane.heightProperty());

        Scene scene = new Scene(stackPane);
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(800);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    class MyCanvas extends Canvas {


        private Rectangle2D sourceRect = new Rectangle2D(100, 100, 100, 10);
        private Rectangle2D targetRect = new Rectangle2D(300, 300, 100, 10);

        private double startX;

        public MyCanvas() {
            widthProperty().addListener(it -> draw());
            heightProperty().addListener(it -> draw());

            setOnMousePressed(evt -> startX = evt.getX());

            sceneProperty().addListener(it -> {
                Scene scene = getScene();
                scene.addPostLayoutPulseListener(() -> {
                    System.out.println("pulse");
                    draw();
                });
            });

            setOnMouseDragged(evt -> {
                System.out.println(evt.getX() - startX);
                targetRect = new Rectangle2D(targetRect.getMinX() + (evt.getX() - startX), targetRect.getMinY(), targetRect.getWidth(), targetRect.getHeight());
                startX = evt.getX();
                setTranslateX(getTranslateX() + .5);
            });
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }

        private void draw() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
            drawIt();
            drawIt();
        }

        private void drawIt() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.setStroke(Color.GREEN);
            gc.strokeRect(0, 0, getWidth(), getHeight());

            gc.setStroke(Color.RED);
            gc.strokeRect(sourceRect.getMinX(), sourceRect.getMinY(), sourceRect.getWidth(), sourceRect.getHeight());

            gc.setStroke(Color.BLUE);
            gc.strokeRect(targetRect.getMinX(), targetRect.getMinY(), targetRect.getWidth(), targetRect.getHeight());

            double offset = 8;
            double curve = 6;

            double sx = snapPositionX(sourceRect.getMinX() + sourceRect.getWidth());
            double sx1 = snapPositionX(sx + offset);

            double tx = snapPositionX(targetRect.getMinX()) + .5;

            double sy = snapPositionY(sourceRect.getMinY() + sourceRect.getHeight() / 2);
            double ty = snapPositionY(targetRect.getMinY() + targetRect.getHeight() / 2);

            gc.setStroke(Color.BLACK);

            gc.beginPath();
            gc.moveTo(sx, sy);

            gc.lineTo(sx1 - curve, sy);
            gc.quadraticCurveTo(sx1, sy, sx1, sy + curve);
            gc.lineTo(sx1, ty - curve);
            gc.quadraticCurveTo(sx1, ty, sx1 + curve, ty);
            gc.lineTo(tx, ty);

            gc.stroke();

            drawArrowHead(ArrowDirection.RIGHT, gc, tx, ty);
        }

        protected void drawArrowHead(ArrowDirection direction, GraphicsContext gc, double x, double y) {
            final int s = 6;

            gc.setStroke(Color.BLACK);
            gc.setFill(Color.BLACK);

            switch (direction) {
                case LEFT:
                    gc.fillPolygon(new double[]{x, x + s * 1.5, x + s * 1.5}, new double[]{y, y - s, y + s}, 3);
                    gc.strokePolygon(new double[]{x, x + s * 1.5, x + s * 1.5}, new double[]{y, y - s, y + s}, 3);
                    break;
                case RIGHT:
                    gc.fillPolygon(new double[]{x, x - s * 1.5, x - s * 1.5}, new double[]{y, y - s, y + s}, 3);
                    gc.strokePolygon(new double[]{x, x - s * 1.5, x - s * 1.5}, new double[]{y, y - s, y + s}, 3);
                    break;
            }
        }

        // snap to pixel

        private final BooleanProperty snapToPixel = new SimpleBooleanProperty(this, "snapToPixel", true);

        public final BooleanProperty snapToPixelProperty() {
            return snapToPixel;
        }

        public final void setSnapToPixel(boolean snap) {
            snapToPixel.set(snap);
        }

        public final boolean isSnapToPixel() {
            return snapToPixel.get();
        }


        /**
         * If this renderer's snapToPixel property is true, returns a value rounded
         * to the nearest pixel in the horizontal direction, else returns the
         * same value.
         *
         * @param value the space value to be snapped
         * @return value rounded to nearest pixel
         */
        public double snapSpaceX(double value) {
            return snapSpaceX(value, isSnapToPixel());
        }

        /**
         * If this renderer's snapToPixel property is true, returns a value rounded
         * to the nearest pixel in the vertical direction, else returns the
         * same value.
         *
         * @param value the space value to be snapped
         * @return value rounded to nearest pixel
         */
        public double snapSpaceY(double value) {
            return snapSpaceY(value, isSnapToPixel());
        }

        /**
         * If this renderer's snapToPixel property is true, returns a value ceiled
         * to the nearest pixel in the horizontal direction, else returns the
         * same value.
         *
         * @param value the size value to be snapped
         * @return value ceiled to nearest pixel
         */
        public double snapSizeX(double value) {
            return snapSizeX(value, isSnapToPixel());
        }

        /**
         * If this renderer's snapToPixel property is true, returns a value ceiled
         * to the nearest pixel in the vertical direction, else returns the
         * same value.
         *
         * @param value the size value to be snapped
         * @return value ceiled to nearest pixel
         */
        public double snapSizeY(double value) {
            return snapSizeY(value, isSnapToPixel());
        }

        /**
         * If this renderer's snapToPixel property is true, returns a value rounded
         * to the nearest pixel in the horizontal direction, else returns the
         * same value.
         *
         * @param value the position value to be snapped
         * @return value rounded to nearest pixel
         */
        public double snapPositionX(double value) {
            return snapPositionX(value, isSnapToPixel());
        }

        /**
         * If this renderer's snapToPixel property is true, returns a value rounded
         * to the nearest pixel in the vertical direction, else returns the
         * same value.
         *
         * @param value the position value to be snapped
         * @return value rounded to nearest pixel
         */
        public double snapPositionY(double value) {
            return snapPositionY(value, isSnapToPixel());
        }

        private double getSnapScaleX() {
            return 1.5;
        }

        private double getSnapScaleY() {
            return 1.5;
        }

        private double scaledRound(double value, double scale) {
            return Math.round(value * scale) / scale;
        }

        private double scaledCeil(double value, double scale) {
            return Math.ceil(value * scale) / scale;
        }

        /**
         * If snapToPixel is true, then the value is rounded using Math.round. Otherwise,
         * the value is simply returned. This method will surely be JIT'd under normal
         * circumstances, however on an interpreter it would be better to inline this
         * method. However the use of Math.round here, and Math.ceil in snapSize is
         * not obvious, and so for code maintenance this logic is pulled out into
         * a separate method.
         *
         * @param value       The value that needs to be snapped
         * @param snapToPixel Whether to snap to pixel
         * @return value either as passed in or rounded based on snapToPixel
         */
        private double snapSpaceX(double value, boolean snapToPixel) {
            return snapToPixel ? scaledRound(value, getSnapScaleX()) : value;
        }

        private double snapSpaceY(double value, boolean snapToPixel) {
            return snapToPixel ? scaledRound(value, getSnapScaleY()) : value;
        }

        /**
         * If snapToPixel is true, then the value is ceil'd using Math.ceil. Otherwise,
         * the value is simply returned.
         *
         * @param value       The value that needs to be snapped
         * @param snapToPixel Whether to snap to pixel
         * @return value either as passed in or ceil'd based on snapToPixel
         */
        private double snapSizeX(double value, boolean snapToPixel) {
            return snapToPixel ? scaledCeil(value, getSnapScaleX()) : value;
        }

        private double snapSizeY(double value, boolean snapToPixel) {
            return snapToPixel ? scaledCeil(value, getSnapScaleY()) : value;
        }

        /**
         * If snapToPixel is true, then the value is rounded using Math.round. Otherwise,
         * the value is simply returned.
         *
         * @param value       The value that needs to be snapped
         * @param snapToPixel Whether to snap to pixel
         * @return value either as passed in or rounded based on snapToPixel
         */
        private double snapPositionX(double value, boolean snapToPixel) {
            return snapToPixel ? scaledRound(value, getSnapScaleX()) : value;
        }

        private double snapPositionY(double value, boolean snapToPixel) {
            return snapToPixel ? scaledRound(value, getSnapScaleY()) : value;
        }
    }
}
