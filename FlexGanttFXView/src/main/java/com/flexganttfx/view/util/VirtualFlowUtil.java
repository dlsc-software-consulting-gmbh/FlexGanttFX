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
package com.flexganttfx.view.util;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.skin.VirtualFlow;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Utility methods for working with JavaFX {@link VirtualFlow} instances.
 * These helpers keep the visible row position of multiple virtualized controls in sync.
 */
public class VirtualFlowUtil {

    /**
     * Constructs a new utility instance. All methods of this class are static,
     * hence instances of it are normally not needed.
     */
    public VirtualFlowUtil() {
    }

    /**
     * Binds the vertical virtual flows of the given controls so they scroll together.
     *
     * @param control1 the first control
     * @param control2 the second control
     */
    public static void bindVirtualFlows(Control control1, Control control2) {

        AtomicReference<InvalidationListener> skinListener = new AtomicReference<>();

        AtomicBoolean isBound = new AtomicBoolean(false);

        Runnable maybeBind = () -> {
            if (isBound.get()) {
                return;
            }

            VirtualFlow<?> leftFlow = (VirtualFlow) control1.lookup("VirtualFlow");
            VirtualFlow<?> rightFlow = (VirtualFlow) control2.lookup("VirtualFlow");

            if (leftFlow != null && rightFlow != null) {
                doRealBidirectionalBinding(leftFlow, rightFlow);
                control1.skinProperty().removeListener(skinListener.get());
                control2.skinProperty().removeListener(skinListener.get());
                isBound.set(true);
            }
        };

        skinListener.set(it -> maybeBind.run());

        control1.skinProperty().addListener(skinListener.get());
        control2.skinProperty().addListener(skinListener.get());

        maybeBind.run();
    }

    private static void doRealBidirectionalBinding(VirtualFlow<?> leftFlow, VirtualFlow<?> rightFlow) {
        AtomicBoolean isUpdating = new AtomicBoolean(false);
        doRealBinding(isUpdating, leftFlow, rightFlow);
        doRealBinding(isUpdating, rightFlow, leftFlow);
    }

    private static void doRealBinding(AtomicBoolean isUpdating, VirtualFlow<?> flow1, VirtualFlow<?> flow2) {
        AtomicReference<Cell> lastCell = new AtomicReference(null);

        Runnable doUpdate = () -> {
            if (isUpdating.get()) {
                return;
            }
            isUpdating.set(true);
            addPostLayoutAction(flow1.getScene(), () -> {
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
            if (lastCell.get() != null) {
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

    private static void updatePosition(VirtualFlow<?> fromFlow, VirtualFlow<?> toFlow) {
        var pos2 = getVFlowPosition(fromFlow);
        setVFlowPosition(toFlow, pos2);
    }

    private static VirtualFlowPosition getVFlowPosition(VirtualFlow<?> flow) {
        flow.applyCss();
        flow.layout();

        IndexedCell cell = flow.getFirstVisibleCell();
        int index = cell.getIndex();
        double offset = -cell.getLayoutY();

        return new VirtualFlowPosition(index, offset);
    }

    private static void setVFlowPosition(VirtualFlow<?> flow, VirtualFlowPosition pos) {
        try {
            flow.scrollToTop(pos.index);
            flow.layout();
            flow.scrollPixels(pos.offset);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the scroll position of a {@link VirtualFlow} as a visible cell index and pixel offset.
     * Instances of this class are used to transfer the current viewport position to another flow.
     */
    private static class VirtualFlowPosition {

        private final int index;
        private final double offset;

        public VirtualFlowPosition(int index, double offset) {
            this.index = index;
            this.offset = offset;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return "VBosPosition{" + "index=" + index + ", offset=" + offset + '}';
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            VirtualFlowPosition that = (VirtualFlowPosition) o;
            return index == that.index && Double.compare(that.offset, offset) == 0;
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            return Objects.hash(index, offset);
        }
    }

    private static void addPostLayoutAction(Scene scene, Runnable action) {
        AtomicReference<Runnable> listener = new AtomicReference<>();

        listener.set(() -> {
            scene.removePostLayoutPulseListener(listener.get());
            action.run();
        });

        scene.addPostLayoutPulseListener(listener.get());
    }
}
