package com.flexganttfx.view.util;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.skin.VirtualFlow;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VirtualFlowUtil
{

    public static AtomicBoolean bindVirtualFlows(Control control1, Control control2)
    {

        AtomicReference<InvalidationListener> skinListener = new AtomicReference<>();

        AtomicBoolean isBound = new AtomicBoolean(false);

        AtomicBoolean isUpdating = new AtomicBoolean(false);

        Runnable maybeBind = () -> {
            if (isBound.get())
            {
                return;
            }

            VirtualFlow<?> leftFlow = (VirtualFlow) control1.lookup("VirtualFlow");
            VirtualFlow<?> rightFlow = (VirtualFlow) control2.lookup("VirtualFlow");

            if (leftFlow != null && rightFlow != null)
            {
                doRealBidirectionalBinding(isUpdating, leftFlow, rightFlow);
                control1.skinProperty().removeListener(skinListener.get());
                control2.skinProperty().removeListener(skinListener.get());
                isBound.set(true);
            }
        };

        skinListener.set(it -> maybeBind.run());

        control1.skinProperty().addListener(skinListener.get());
        control2.skinProperty().addListener(skinListener.get());

        maybeBind.run();
        return isUpdating;
    }

    private static void doRealBidirectionalBinding(AtomicBoolean isUpdating, VirtualFlow<?> leftFlow, VirtualFlow<?> rightFlow)
    {
        doRealBinding(isUpdating, leftFlow, rightFlow);
        doRealBinding(isUpdating, rightFlow, leftFlow);
    }

    private static void doRealBinding(AtomicBoolean isUpdating, VirtualFlow<?> flow1, VirtualFlow<?> flow2)
    {
        AtomicReference<Cell> lastCell = new AtomicReference(null);

        Runnable doUpdate = () -> {
            if (isUpdating.get())
            {
                return;
            }
            isUpdating.set(true);
            addPostLayoutAction(flow1.getScene(), () -> {
                try
                {
                    updatePosition(flow1, flow2);
                }
                finally
                {
                    isUpdating.set(false);
                }
            });
        };
        ChangeListener doUpdateListener = (obs, oldVal, newVal) -> {
            doUpdate.run();
        };

        Runnable updateCellListener = () -> {
            if (lastCell.get() != null)
            {
                lastCell.get().layoutYProperty().removeListener(doUpdateListener);
            }
            Cell newCell = flow1.getLastVisibleCell();

            if (newCell != null)
            {
                newCell.layoutYProperty().addListener(doUpdateListener);
            }
            lastCell.set(newCell);
        };

        flow1.positionProperty().addListener((obs, oldVal, newVal) -> {
            updateCellListener.run();
            doUpdate.run();
        });
    }

    private static VirtualFlowPosition flowLockPosition = null;

    public static void storeCurrentPosition(VirtualFlow<?> flow)
    {
        flowLockPosition = getVFlowPosition(flow);
        lockPosTimestamp.set(Instant.now());
    }

    private static final AtomicReference<Instant> lockPosTimestamp = new AtomicReference<>(Instant.now());
    private static final Duration keepLockPosDuration = Duration.ofMillis(300);

    private static void updatePosition(VirtualFlow<?> fromFlow, VirtualFlow<?> toFlow)
    {

        VirtualFlowPosition pos;
        Instant now = Instant.now();
        Duration duratSinceLastLockTime = Duration.between(lockPosTimestamp.get(), now).abs();

        if (duratSinceLastLockTime.compareTo(keepLockPosDuration) < 0)
        {
            // need to stick to the expand / collapse position for an arbitrary time (because of other rendering updates triggered by TreeTableView while expanding)
            System.out.println("between: " + duratSinceLastLockTime);
            pos = flowLockPosition;
            lockPosTimestamp.set(now);
            // set to from flow
            setVFlowPosition(fromFlow, pos);
            System.err.println("updatePosition restore active, using curPos: " + pos + ", fromFlow: " + fromFlow.hashCode() + ", toFlow: " + toFlow.hashCode());
        }
        else
        {
            pos = getVFlowPosition(fromFlow);
        }
        setVFlowPosition(toFlow, pos);
        System.err.println("updatePosition: " + pos + ", fromFlow: " + fromFlow.hashCode() + ", toFlow: " + toFlow.hashCode());
    }

    private static VirtualFlowPosition getVFlowPosition(VirtualFlow<?> flow)
    {
        flow.applyCss();
        flow.layout();

        IndexedCell cell = flow.getFirstVisibleCell();
        int index = cell.getIndex();
        double offset = -cell.getLayoutY();

        return new VirtualFlowPosition(index, offset);
    }

    private static void setVFlowPosition(VirtualFlow<?> flow, VirtualFlowPosition pos)
    {
        try
        {
            flow.scrollToTop(pos.index);
            flow.layout();
            flow.scrollPixels(pos.offset);
            /*
            VirtualFlowPosition curPos = getVFlowPosition(flow);
            double diff = curPos.offset - pos.offset;
            if (curPos.index != pos.index)
            {
                flow.scrollToTop(pos.index);
                flow.layout();
                flow.scrollPixels(pos.offset);
            }
            else
            {
                if (diff != 0d)
                {
                    flow.scrollPixels(-diff);
                }
            }*/
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    private static class VirtualFlowPosition
    {

        private int index;
        private double offset;

        public VirtualFlowPosition(int index, double offset)
        {
            this.index = index;
            this.offset = offset;
        }

        @Override
        public String toString()
        {
            return "VBosPosition{" + "index=" + index + ", offset=" + offset + '}';
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            VirtualFlowPosition that = (VirtualFlowPosition) o;
            return index == that.index && Double.compare(that.offset, offset) == 0;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(index, offset);
        }
    }

    private static void addPostLayoutAction(Scene scene, Runnable action)
    {
        AtomicReference<Runnable> listener = new AtomicReference<>();

        listener.set(() -> {
            scene.removePostLayoutPulseListener(listener.get());
            action.run();
        });

        scene.addPostLayoutPulseListener(listener.get());
    }
}
