package com.flexganttfx.view.util;

import com.flexganttfx.core.LoggingDomain;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Cell;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.skin.VirtualFlow;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
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

    private static MODE mode = MODE.POS_LOCKING;
    private static boolean debugLog = false;

    public static void setMode(MODE mode)
    {
        VirtualFlowUtil.mode = mode;
    }

    public static boolean isMode(MODE mode)
    {
        return VirtualFlowUtil.mode == mode;
    }

    @SuppressWarnings("unused")
    public static void setDebugLog(boolean enableLog)
    {
        VirtualFlowUtil.debugLog = enableLog;
    }

    public static boolean isDebugLog()
    {
        return debugLog;
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
            VirtualFlow<?> leftFlow = (VirtualFlow<?>) control1.lookup("VirtualFlow");
            VirtualFlow<?> rightFlow = (VirtualFlow<?>) control2.lookup("VirtualFlow");

            if (leftFlow != null && rightFlow != null)
            {
                FlowBinding.addFlowBinding(leftFlow, rightFlow);
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
        AtomicReference<Cell<?>> lastCell = new AtomicReference<>(null);
        final FlowBinding fb = FlowBinding.get(flow1);
        Runnable doUpdate = () -> {
            try
            {
                if (isUpdating.get())
                {
                    return;
                }
                isUpdating.set(true);
                addPostLayoutAction(flow1.getScene(), () -> {
                    try
                    {
                        // if there are pos layout actions during skipping we stick to restore pos
                        updatePosition(flow1, flow2, fb.restorePos);
                    }
                    finally
                    {
                        isUpdating.set(false);
                    }
                });
            }
            catch (Throwable t)
            {
                isUpdating.set(false);
                LoggingDomain.NAVIGATION.log(Level.WARNING, "Exception while updating!", t);
            }
        };
        Runnable updateCellListener = getRunnable(flow1, doUpdate, lastCell);

        flow1.positionProperty().addListener((obs, oldVal, newVal) -> {
            if (fb.isSkipUpdatePos())
            {
                if (debugLog)
                {
                    System.out.println("Skipped update because FlowBinding.skipUpdatePos: " + fb.isSkipUpdatePos());
                }
                return;
            }
            // prevent infinite updates between flow1 and flow2
            // has no visible effect but much better performance
            double threshold = 0.000001;
            double diff = Math.abs(oldVal.doubleValue() - newVal.doubleValue());
            if (diff > threshold)
            {
                if (debugLog)
                {
                    System.out.println("Update! diff:" + diff);
                }
                updateCellListener.run();
                doUpdate.run();
            }
            else
            {
                if (debugLog)
                {
                    System.out.println("Skipped update because threshold not reached! diff:" + diff);
                }
            }
        });
    }

    private static Runnable getRunnable(VirtualFlow<?> flow1, Runnable doUpdate, AtomicReference<Cell<?>> lastCell)
    {
        ChangeListener<Number> doUpdateListener = (obs, oldVal, newVal) -> doUpdate.run();
        return () -> {
            if (lastCell.get() != null)
            {
                lastCell.get().layoutYProperty().removeListener(doUpdateListener);
            }
            Cell<?> newCell = flow1.getLastVisibleCell();

            if (newCell != null)
            {
                newCell.layoutYProperty().addListener(doUpdateListener);
            }
            lastCell.set(newCell);
        };
    }

    public static VirtualFlowPosition storeCurrentPosition(VirtualFlow<?> flow)
    {
        if (debugLog && LoggingDomain.NAVIGATION.isLoggable(Level.FINE))
        {
            LoggingDomain.NAVIGATION.fine("mode: " + VirtualFlowUtil.mode + ", flow: " + flow.hashCode());
        }
        FlowBinding fb = isMode(MODE.POS_LOCKING) ? FlowBinding.get(flow) : null;
        if (fb != null)
        {
            fb.setSkipUpdatePos(true);
        }
        return null;
    }

    private static void updatePosition(VirtualFlow<?> fromFlow, VirtualFlow<?> toFlow, VirtualFlowPosition requestedPosition)
    {
        // take restore position if provided - preventing jumping while expanding/collapsing tree nodes
        VirtualFlowPosition pos = requestedPosition != null ? requestedPosition : getVFlowPosition(fromFlow);
        setVFlowPosition(toFlow, pos);
        if (requestedPosition != null)
        {
            setVFlowPosition(fromFlow, pos);
        }
        if (debugLog)
        {
            System.out.println("set pos: " + pos + ", isRestorePos: " + (requestedPosition != null) + ", to toFlow. (fromFlow: " + fromFlow.hashCode() + ", toFlow: " + toFlow.hashCode() + ")");
        }
    }

    private static VirtualFlowPosition getVFlowPosition(VirtualFlow<?> flow)
    {
        flow.applyCss();
        flow.layout();

        IndexedCell<?> cell = flow.getFirstVisibleCell();
        int index = cell == null ? 0 : cell.getIndex();
        double offset = cell == null ? 0 : -cell.getLayoutY();

        return new VirtualFlowPosition(index, offset);
    }

    public static void setVFlowPosition(VirtualFlow<?> flow, VirtualFlowPosition pos)
    {
        try
        {
            flow.scrollToTop(pos.index);
            flow.layout();
            flow.scrollPixels(pos.offset);
        }
        catch (Throwable e)
        {
            if (debugLog)
            {
                LoggingDomain.NAVIGATION.log(Level.WARNING, "Exception", e);
            }
        }
    }

    public static class VirtualFlowPosition
    {
        private final int index;
        private final double offset;

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

    static class FlowBinding
    {
        private static final HashSet<FlowBinding> flowBindings = new HashSet<>();
        VirtualFlow<?> leftFlow;
        VirtualFlow<?> rightFlow;

        final AtomicBoolean skipUpdatePos = new AtomicBoolean(false);
        Instant skipUpdatePosSetTime = Instant.now().minusSeconds(1);
        Duration skipUpdatePosRetentionTime = Duration.ofMillis(50);
        Duration skipUpdatePosMaxRetentionTime = Duration.ofMillis(300); // for unlocking if lock takes too long

        VirtualFlowPosition restorePos = null;
        boolean restoring = false;

        final Timeline restoreTimer = new Timeline();

        private FlowBinding(VirtualFlow<?> leftFlow, VirtualFlow<?> rightFlow)
        {
            this.leftFlow = leftFlow;
            this.rightFlow = rightFlow;
            javafx.util.Duration durat = new javafx.util.Duration(skipUpdatePosRetentionTime.toMillis());
            restoreTimer.getKeyFrames().add(new KeyFrame(durat, ae -> restorePosition()));
        }

        @SuppressWarnings("UnusedReturnValue")
        public static FlowBinding addFlowBinding(VirtualFlow<?> leftFlow, VirtualFlow<?> rightFlow)
        {
            FlowBinding fb = new FlowBinding(leftFlow, rightFlow);
            flowBindings.add(fb);
            return fb;
        }

        public static FlowBinding get(VirtualFlow<?> flow)
        {
            return flowBindings.stream()
                    .filter(fb -> fb.leftFlow == flow || fb.rightFlow == flow)
                    .findFirst()
                    .orElse(null);
        }

        public boolean isSkipUpdatePos()
        {
            if (this.skipUpdatePos.get())
            {
                Duration duratSinceLastSkipTime = Duration.between(skipUpdatePosSetTime, Instant.now());
                if (duratSinceLastSkipTime.compareTo(skipUpdatePosMaxRetentionTime) < 0)
                {
                    if (debugLog)
                    {
                        System.out.println("isSkipUpdatePos: true, for another: " + skipUpdatePosMaxRetentionTime.minus(duratSinceLastSkipTime.abs()));
                    }
                }
                else
                {
                    unlock(); // unlock for next run
                }
                return true;
            }

            return false;

        }

        private void unlock()
        {
            this.restorePos = null;
            this.skipUpdatePos.set(false);
            if (debugLog)
            {
                System.err.println("Unlock! Because too long locked!");
            }
        }

        public void setSkipUpdatePos(boolean skipUpdatePos)
        {
            this.skipUpdatePos.set(skipUpdatePos);
            if (!skipUpdatePos)
            {
                this.restorePos = null;
            }
            else if (this.restorePos == null) // do not update restore pos for subsequent calls
            {
                this.restorePos = getVFlowPosition(leftFlow);
            }
            if (skipUpdatePos)
            {
                this.skipUpdatePosSetTime = Instant.now();
                try
                {
                    restoreTimer.stop();
                    restoreTimer.play();
                }
                catch (Exception ex)
                {
                    if (debugLog)
                    {
                        System.err.println("error stopping timeLine: " + ex.getMessage());
                    }
                }
            }
        }

        @SuppressWarnings("unused")
        public static boolean isSkipUpdatePos(VirtualFlow<?> flow)
        {
            FlowBinding fb = get(flow);
            if (fb != null)
            {
                return fb.isSkipUpdatePos();
            }
            return false;
        }

        @SuppressWarnings("unused")
        public static void setSkipUpdatePos(boolean skipUpdatePos, VirtualFlow<?> flow)
        {
            FlowBinding fb = get(flow);
            if (fb != null)
            {
                fb.skipUpdatePos.set(skipUpdatePos);
            }
        }

        public void restorePosition()
        {
            final VirtualFlowPosition pos = restorePos;
            restorePos = null;
            if (pos != null) // first update after lock
            {
                restoring = true;
                try
                {
                    // scroll down to get the virtual flow initialized
                    int lastRowIndex = Math.max(leftFlow.getCellCount() - 1, 0);
                    if (debugLog)
                    {
                        System.out.println("restorePosition, restore, lastRowIndex: " + lastRowIndex);
                    }
                    VirtualFlowPosition lastPos = new VirtualFlowPosition(lastRowIndex, 0);
                    setVFlowPosition(rightFlow, lastPos);
                    setVFlowPosition(leftFlow, lastPos);

                    // restore the current position
                    int index = Math.min(pos.index, lastRowIndex);
                    VirtualFlowPosition restorePos = new VirtualFlowPosition(index, pos.index != index ? 0 : pos.offset);
                    if (debugLog)
                    {
                        System.out.println("restorePosition, restorePos: " + pos);
                    }

                    setVFlowPosition(rightFlow, restorePos);
                    setVFlowPosition(leftFlow, restorePos);
                }
                catch (Exception ex)
                {
                    if (debugLog)
                    {
                        System.err.println("error stopping timeLine: " + ex.getMessage());
                    }
                }
                finally
                {
                    restorePos = null;
                }
            }
            restoring = false;
            if (debugLog)
            {
                System.out.println("restorePosition: unlocking");
            }
            skipUpdatePos.set(false);
        }
    }
}
