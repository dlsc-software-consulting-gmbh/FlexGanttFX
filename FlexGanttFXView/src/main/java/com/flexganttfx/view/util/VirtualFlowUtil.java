package com.flexganttfx.view.util;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Cell;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.skin.VirtualFlow;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VirtualFlowUtil {


    public static void bindVirtualFlows(Control node1, Control node2) {

        AtomicReference<InvalidationListener> skinListener = new AtomicReference<>();

        AtomicBoolean isBound = new AtomicBoolean(false);

        Runnable maybeBind = () -> {
            if (isBound.get()) {
                return;
            }
            VirtualFlow<?> leftFlow = findVirtualFlow(node1);
            VirtualFlow<?> rightFlow = findVirtualFlow(node2);
            if (leftFlow != null && rightFlow != null) {
                doRealBidirectionalBinding(leftFlow, rightFlow);
                node1.skinProperty().removeListener(skinListener.get());
                node2.skinProperty().removeListener(skinListener.get());
                isBound.set(true);
            }
        };

        skinListener.set(it -> maybeBind.run());

        node1.skinProperty().addListener(skinListener.get());
        node2.skinProperty().addListener(skinListener.get());
        maybeBind.run();
    }

    public static void doRealBidirectionalBinding(VirtualFlow<?> leftFlow, VirtualFlow<?> rightFlow) {
        AtomicBoolean isUpdating = new AtomicBoolean(false);
        doRealBinding(isUpdating, leftFlow, rightFlow);
        doRealBinding(isUpdating, rightFlow, leftFlow);
    }

    public static void doRealBinding(AtomicBoolean isUpdating, VirtualFlow<?> flow1, VirtualFlow<?> flow2) {
        AtomicReference<Cell> lastCell = new AtomicReference(null);

        Runnable doUpdate = () -> {
            if (isUpdating.get()) {
                return;
            }
            isUpdating.set(true);
            Platform.runLater(() -> {
                // The runLater ensures that we are outside of the layout pass
                // It's verified via video, that both flows are updated in the same layout pass
                try {
                    updatePosition(flow1, flow2);
                } finally {
                    isUpdating.set(false);
                }
            });
        };
        ChangeListener doUpdateListener = (obs, oldVal, newVal) -> {
            doUpdate.run();
        };

        Runnable updateCellListener = () -> {
            if(lastCell.get() != null) {
                lastCell.get().layoutYProperty().removeListener(doUpdateListener);
            }
            Cell newCell = flow1.getLastVisibleCell();

            if (newCell != null) {
                newCell.layoutYProperty().addListener(doUpdateListener);
            }
            lastCell.set(newCell);
        };

        flow1.positionProperty().addListener((obs, oldVal, newVal) -> {
            updateCellListener.run();
            doUpdate.run();
        });
    }

    public static void updatePosition(VirtualFlow<?> fromFlow, VirtualFlow<?> toFlow) {
        var pos2 = getVFlowPosition(fromFlow);
        setVFlowPosition(toFlow, pos2);
    }

    public static VirtualFlowPosition getVFlowPosition(VirtualFlow<?> flow) {
        flow.applyCss();
        flow.layout();
        IndexedCell cell = flow.getFirstVisibleCell();
        int index = cell.getIndex();
        double offset = -cell.getLayoutY();

        return new VirtualFlowPosition(index, offset);
    }

    public static void setVFlowPosition(VirtualFlow<?> flow, VirtualFlowPosition pos) {
        try {
            flow.scrollToTop(pos.index);
            flow.layout();
            flow.scrollPixels(pos.offset);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static VirtualFlow findVirtualFlow(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof VirtualFlow) {
                return (VirtualFlow) node;
            }

            if (node instanceof Parent) {
                VirtualFlow b = findVirtualFlow((Parent) node);
                if (b != null) {
                    return b;
                }
            }
        }

        return null;
    }

    static class VirtualFlowPosition {
        int index;
        double offset;

        public VirtualFlowPosition(int index, double offset) {
            this.index = index;
            this.offset = offset;
        }

        @Override
        public String toString() {
            return "VBosPosition{" + "index=" + index + ", offset=" + offset + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VirtualFlowPosition that = (VirtualFlowPosition) o;
            return index == that.index &&
                    Double.compare(that.offset, offset) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, offset);
        }
    }
}
