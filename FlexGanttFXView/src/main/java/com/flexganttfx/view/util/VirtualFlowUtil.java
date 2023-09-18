package com.flexganttfx.view.util;

import com.flexganttfx.core.LoggingDomain;
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
import java.util.logging.Level;

public class VirtualFlowUtil
{
    public enum MODE
    {
        NORMAL,
        POS_LOCKING
    }

    private static final AtomicReference<Instant> lockPosTimestamp = new AtomicReference<>(Instant.now());
    private static final AtomicReference<VirtualFlowPosition> flowLockPosition = new AtomicReference<>();
    private static Duration keepLockPosDuration = Duration.ofMillis(300);
    private static boolean flowPosOptActive = false;

    public static void setFlowPosOptActive(boolean flowPosOptActive)
    {
        VirtualFlowUtil.flowPosOptActive = flowPosOptActive;
    }
    private static boolean isFlowPosOptActive()
    {
        return flowPosOptActive;
    }


    private static MODE mode = MODE.POS_LOCKING;


    public static void setMode(MODE mode)
    {
        VirtualFlowUtil.mode = mode;
    }

    public static boolean isMode(MODE mode)
    {
        return VirtualFlowUtil.mode == mode;
    }

    public static Duration getKeepLockPosDuration()
    {
        return keepLockPosDuration;
    }

    public static void setKeepLockPosDuration(Duration keepLockPosDuration)
    {
        VirtualFlowUtil.keepLockPosDuration = keepLockPosDuration == null || keepLockPosDuration.isNegative() ? Duration.ZERO : keepLockPosDuration;
    }

    public static void bindVirtualFlows(Control control1, Control control2)
    {

        AtomicReference<InvalidationListener> skinListener = new AtomicReference<>();

        AtomicBoolean isBound = new AtomicBoolean(false);

        Runnable maybeBind = () -> {
            if (isBound.get())
            {
                return;
            }
            AtomicBoolean isUpdating = new AtomicBoolean(false);
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

    public static void storeCurrentPosition(VirtualFlow<?> flow)
    {
        if (LoggingDomain.NAVIGATION.isLoggable(Level.FINE))
        {
            LoggingDomain.NAVIGATION.fine("mode: " + VirtualFlowUtil.mode + ", flow: " + flow.hashCode());
        }
        flowLockPosition.set(getVFlowPosition(flow));
        lockPosTimestamp.set(Instant.now());
    }

    private static void updatePosition(VirtualFlow<?> fromFlow, VirtualFlow<?> toFlow)
    {
        VirtualFlowPosition pos = null;
        boolean lockedPos = false;
        if (isMode(MODE.POS_LOCKING))
        {
            try
            {
                Instant now = Instant.now();
                Duration duratSinceLastLockTime = Duration.between(lockPosTimestamp.get(), now).abs();

                if (duratSinceLastLockTime.compareTo(keepLockPosDuration) < 0)
                {
                    if (LoggingDomain.NAVIGATION.isLoggable(Level.FINEST))
                    {
                        LoggingDomain.NAVIGATION.finest("POS_LOCKING active, for another " + duratSinceLastLockTime.minus(keepLockPosDuration) + " (fromFlow: " + fromFlow.hashCode() + ", toFlow: " + toFlow.hashCode() + ")");
                    }
                    // need to stick to the expand / collapse position for an arbitrary time (because of other rendering updates triggered by TreeTableView while expanding)
                    pos = flowLockPosition.get();
                    if (pos != null)
                    {
                        lockedPos = true;
                        lockPosTimestamp.set(now);
                        // set to from flow
                        setVFlowPosition(fromFlow, pos);
                        if (LoggingDomain.NAVIGATION.isLoggable(Level.FINE))
                        {
                            LoggingDomain.NAVIGATION.fine("POS_LOCKING active, set stored pos: " + pos + ", set to fromFlow. (fromFlow: " + fromFlow.hashCode() + ", toFlow: " + toFlow.hashCode() + ")");
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                LoggingDomain.NAVIGATION.log(Level.WARNING, "POS_LOCKING active, using stored pos: " + pos + ", fromFlow: " + fromFlow.hashCode() + ", toFlow: " + toFlow.hashCode(), e);
            }
        }
        if (pos == null)
        {
            pos = getVFlowPosition(fromFlow);
        }
        setVFlowPosition(toFlow, pos);
        if (lockedPos && LoggingDomain.NAVIGATION.isLoggable(Level.FINE))
        {
            LoggingDomain.NAVIGATION.fine("POS_LOCKING active, set stored pos: " + pos + ", set to toFlow. (fromFlow: " + fromFlow.hashCode() + ", toFlow: " + toFlow.hashCode() + ")");
        }
        else if (LoggingDomain.NAVIGATION.isLoggable(Level.FINEST))
        {
            LoggingDomain.NAVIGATION.finest("set pos: " + pos + ", to toFlow. (fromFlow: " + fromFlow.hashCode() + ", toFlow: " + toFlow.hashCode() + ")");
        }
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
            if (!isFlowPosOptActive())
            {
                flow.scrollToTop(pos.index);
                flow.layout();
                flow.scrollPixels(pos.offset);
            }
            else
            {
                /* is this faster (just scrolling if the index is already the same?) */

                IndexedCell cell = flow.getFirstVisibleCell();
                int index = cell.getIndex();
                double offset = -cell.getLayoutY();

                VirtualFlowPosition curPos = new VirtualFlowPosition(index, offset);
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
                        if (LoggingDomain.NAVIGATION.isLoggable(Level.FINEST))
                        {
                            LoggingDomain.NAVIGATION.log(Level.FINEST, "flowPosOptActive: " + true);
                        }
                    }
                }
            }
        }
        catch (Throwable e)
        {
            LoggingDomain.NAVIGATION.log(Level.WARNING, "Exception", e);
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
            return "VFlowPosition{" + "index=" + index + ", offset=" + offset + '}';
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
