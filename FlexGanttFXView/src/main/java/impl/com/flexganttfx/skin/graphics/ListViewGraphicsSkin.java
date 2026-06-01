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

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.timeline.TimelineModel;
import com.flexganttfx.view.graphics.ActivityBounds;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.ListViewGraphics;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Graphics skin that renders rows in a {@link javafx.scene.control.ListView}. It creates
 * custom row cells and coordinates the automatic scrolling used while interacting with
 * activities.
 */
public class ListViewGraphicsSkin<R extends Row<?, ?, ?>> extends GraphicsBaseSkin<ListViewGraphics<R>, R> {

    private ListView<R> listView;

    /**
     * Constructs a new skin for the given list view graphics.
     *
     * @param graphics
     *            the graphics control
     */
    public ListViewGraphicsSkin(ListViewGraphics<R> graphics) {
        super(graphics);

        listView.skinProperty().addListener((observable, oldValue, newValue) -> registerListViewScrollBarListener());
        listView.skinProperty().addListener((observable, oldValue, newValue) -> getClippedContent().setClip(createClip()));
    }

    /**
     * Creates the row pane region.
     *
     * @return the row pane region
     */
    @Override
    protected Region createRowPaneRegion() {
        ListViewGraphics<R> graphics = getSkinnable();

        listView = graphics.getListView();
        listView.setSelectionModel(new DisabledSelectionModel<>());
        listView.setCellFactory(listView -> new RowCell<>(graphics));
        listView.fixedCellSizeProperty().bind(graphics.fixedCellSizeProperty());
        listView.placeholderProperty().bind(graphics.placeholderProperty());
        listView.setItems(graphics.getRows());

        listView.addEventFilter(DragEvent.DRAG_OVER, this::autoscrollIfNeeded);
        listView.addEventFilter(DragEvent.DRAG_EXITED_TARGET, this::stopAutoScrollIfNeeded);
        listView.addEventFilter(DragEvent.DRAG_DROPPED, this::stopAutoScrollIfNeeded);
        listView.addEventFilter(DragEvent.DRAG_DONE, this::stopAutoScrollIfNeeded);

        return listView;
    }

    private Node createClip() {
        Rectangle clip = new Rectangle();
        Region region = getClippedContainer();
        if (region != null) {
            clip.widthProperty().bind(region.widthProperty());
            clip.heightProperty().bind(region.heightProperty());
        }
        return clip;
    }

    private void autoscrollIfNeeded(DragEvent evt) {

        /*
         * Determine the "hot" region that will trigger automatic scrolling.
         * Ideally, we use the clipped container of the list view skin, but when
         * the rows are empty, the dimensions of the clipped container will be
         * 0x0. In this case we try to use the virtual flow.
         */
        Region hotRegion = getClippedContainer();
        if (hotRegion == null || hotRegion.getBoundsInLocal().getWidth() < 1) {
            hotRegion = getSkinnable();
            if (hotRegion == null || hotRegion.getBoundsInLocal().getWidth() < 1) {
                stopAutoScrollIfNeeded(evt);
                return;
            }
        }

        double xOffset = 0;
        double yOffset = 0;

        double proximity = getSkinnable().getAutoscrollProximity();

        // x offset

        // left edge check
        double delta = evt.getSceneX() - hotRegion.localToScene(0, 0).getX();
        if (delta < proximity) {
            xOffset = -(proximity - delta);
        }

        // right edge check
        delta = hotRegion.localToScene(0, 0).getX() + hotRegion.getWidth() - evt.getSceneX();
        if (delta < proximity) {
            xOffset = proximity - delta;
        }

        // y offset

        delta = evt.getSceneY() - hotRegion.localToScene(0, 0).getY();
        if (delta < proximity) {
            yOffset = -(proximity - delta);
        }

        delta = hotRegion.localToScene(0, 0).getY() + hotRegion.getHeight()
                - evt.getSceneY();
        if (delta < proximity) {
            yOffset = proximity - delta;
        }

        if (xOffset != 0 || yOffset != 0) {
            autoscroll(xOffset, yOffset);
        } else {
            stopAutoScrollIfNeeded(evt);
        }
    }

    private VirtualFlow<?> getVirtualFlow() {
        return (VirtualFlow<?>) getSkinnable().lookup("VirtualFlow");
    }

    private Region getClippedContainer() {

        /*
         * Safest way to find the clipped container. lookup() does not work at
         * all.
         */
        for (Node child : getVirtualFlow().getChildrenUnmodifiable()) {
            if (child.getStyleClass().contains("clipped-container")) {
                return (Region) child;
            }
        }

        return null;
    }

    class ScrollThread extends Thread {
        private boolean running = true;
        private double xOffset;
        private double yOffset;

        /**
         * Constructs a new scroll thread.
         */
        public ScrollThread() {
            super("Autoscrolling List View");
            setDaemon(true);
        }

        /**
         * Runs the auto-scrolling task.
         */
        @Override
        public void run() {

            /*
             * Some initial delay, especially useful when dragging something in
             * from the outside.
             */

            try {
                Thread.sleep(500);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            while (running) {

                Platform.runLater(() -> {
                    scrollX();
                    scrollY();
                });

                try {
                    sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void scrollX() {
            TimelineModel<?> model = getSkinnable().getTimeline().getModel();
            Instant targetTime = model.calculateTimeForLocation(xOffset);
            model.setStartTime(targetTime);
        }

        private void scrollY() {
            VirtualFlow<?> flow = getVirtualFlow();
            flow.scrollPixels(yOffset);
        }

        /**
         * Stops the auto-scrolling thread.
         */
        public void stopRunning() {
            this.running = false;
        }

        /**
         * Sets the auto-scrolling delta.
         *
         * @param xOffset the horizontal scroll delta
         * @param yOffset the vertical scroll delta
         */
        public void setDelta(double xOffset, double yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

    private ScrollThread scrollThread;

    private void autoscroll(double xOffset, double yOffset) {
        if (scrollThread == null) {
            scrollThread = new ScrollThread();
            scrollThread.start();
        }

        scrollThread.setDelta(xOffset, yOffset);
    }

    private void stopAutoScrollIfNeeded(DragEvent evt) {
        if (scrollThread != null) {

            /*
             * We do not have to stop the automatic scrolling after a DRAG_EXITED_TARGET event
             * if the drag gesture source was the row canvas.
             */
            boolean stopAutoScroll = true;

            EventType<DragEvent> eventType = evt.getEventType();

            if (eventType.equals(DragEvent.DRAG_EXITED_TARGET)) {

                Object source = evt.getGestureSource();

                if (source instanceof RowCanvas) {
                    RowCanvas<?> canvas = (RowCanvas<?>) source;
                    GraphicsBase<?> sourceGraphics = canvas.getGraphics();
                    if (sourceGraphics.equals(getSkinnable())) {
                        stopAutoScroll = false;
                    }
                }
            }

            if (stopAutoScroll) {
                scrollThread.stopRunning();
                scrollThread = null;
            }
        }
    }

    /**
     * Finds the rows inside the lasso selection.
     *
     * @return the selected rows
     */
    @Override
    protected List<Row<?, ?, ?>> findLassoSelectedRows() {
        List<Row<?, ?, ?>> rows = new ArrayList<>();

        Rectangle lasso = getLasso();
        Bounds lassoBounds = lasso.localToScene(lasso.getBoundsInLocal());

        List<RowCell<R>> cells = getVisibleRowCells();
        for (RowCell<R> cell : cells) {
            Bounds cellBounds = cell.localToScene(cell.getLayoutBounds());
            if (cellBounds.intersects(lassoBounds)) {
                RowPane<R> pane = cell.getRowPane();
                R row = pane.getRow();
                if (row != null) {
                    rows.add(row);
                }
            }
        }

        return rows;
    }

    /**
     * Finds the activities inside the lasso selection.
     *
     * @return the selected activities
     */
    @Override
    protected List<ActivityRef<?>> findLassoSelectedActivities() {
        List<ActivityRef<?>> selection = new ArrayList<>();

        Rectangle lasso = getLasso();

        Bounds lassoBounds = lasso.localToScene(lasso.getBoundsInLocal());

        List<RowCell<R>> cells = getVisibleRowCells();
        for (RowCell<R> cell : cells) {
            Bounds cellBounds = cell.localToScene(cell.getLayoutBounds());

            if (cellBounds.intersects(lassoBounds)) {
                RowPane<R> pane = cell.getRowPane();
                RowCanvas<R> canvas = pane.getCanvas();

                List<ActivityBounds> selections = canvas.getActivityBounds(lasso.getBoundsInLocal().getMinX() - getRowHeaderWidth(),
                        Math.max(0, lassoBounds.getMinY() - cellBounds.getMinY()),
                        lasso.getBoundsInLocal().getWidth(),
                        lasso.getBoundsInLocal().getHeight());

                List<ActivityRef<?>> refs = selections.stream()
                        .map(ActivityBounds::getActivityRef)
                        .collect(Collectors.toList());

                selection.addAll(refs);
            }
        }

        return selection;
    }

    /**
     * Returns the row pane at the given y coordinate.
     *
     * @param y the y coordinate
     *
     * @return the row pane at the given y coordinate
     */
    @Override
    protected final RowPane<R> getRowPaneAt(double y) {
        /*
         * Careful when checking hits
         */
        Point2D localToScene = getSkinnable().localToScene(0, y);
        List<RowCell<R>> cells = getVisibleRowCells();
        for (RowCell<R> cell : cells) {
            Bounds cellBounds = cell.localToScene(cell.getLayoutBounds());
            if (cellBounds.getMinY() <= localToScene.getY() && cellBounds.getMaxY() >= localToScene.getY()) {
                return cell.getRowPane();
            }
        }

        return null;
    }

    /**
     * Returns whether the given row is above the viewport.
     *
     * @param row the row
     *
     * @return true if the row is above the viewport
     */
    @Override
    protected boolean isRowAboveViewport(R row) {
        VirtualFlow<?> flow = (VirtualFlow<?>) listView.lookup("#virtual-flow");
        @SuppressWarnings("unchecked")
        RowCell<R> rowCell = (RowCell<R>) flow.getFirstVisibleCell();
        if (rowCell == null) {
            return false;
        }
        R firstRow = rowCell.getRowPane().getRow();
        return getSkinnable().getRows().indexOf(firstRow) > getSkinnable()
                .getRows().indexOf(row);
    }

    static class DisabledSelectionModel<T> extends MultipleSelectionModel<T> {
        DisabledSelectionModel() {
            super.setSelectedIndex(-1);
            super.setSelectedItem(null);
        }

        /**
         * Returns the selected indices.
         *
         * @return the selected indices
         */
        @Override
        public ObservableList<Integer> getSelectedIndices() {
            return FXCollections.emptyObservableList();
        }

        /**
         * Returns the selected items.
         *
         * @return the selected items
         */
        @Override
        public ObservableList<T> getSelectedItems() {
            return FXCollections.emptyObservableList();
        }

        /**
         * Selects all items.
         */
        @Override
        public void selectAll() {
        }

        /**
         * Selects the first item.
         */
        @Override
        public void selectFirst() {
        }

        /**
         * Selects the given indices.
         *
         * @param index the index
         * @param indicies the additional indices
         */
        @Override
        public void selectIndices(int index, int... indicies) {
        }

        /**
         * Selects the last item.
         */
        @Override
        public void selectLast() {
        }

        /**
         * Clears the current selection and selects the given index.
         *
         * @param index the index
         */
        @Override
        public void clearAndSelect(int index) {
        }

        /**
         * Clears the selection.
         */
        @Override
        public void clearSelection() {
        }

        /**
         * Clears the selection at the given index.
         *
         * @param index the index
         */
        @Override
        public void clearSelection(int index) {
        }

        /**
         * Returns whether the selection is empty.
         *
         * @return true if the selection is empty
         */
        @Override
        public boolean isEmpty() {
            return true;
        }

        /**
         * Returns whether the given index is selected.
         *
         * @param index the index
         *
         * @return true if the given index is selected
         */
        @Override
        public boolean isSelected(int index) {
            return false;
        }

        /**
         * Selects the item at the given index.
         *
         * @param index the index
         */
        @Override
        public void select(int index) {
        }

        /**
         * Selects the given item.
         *
         * @param item the item
         */
        @Override
        public void select(T item) {
        }

        /**
         * Selects the next item.
         */
        @Override
        public void selectNext() {
        }

        /**
         * Selects the previous item.
         */
        @Override
        public void selectPrevious() {
        }
    }

    private VirtualFlow<RowCell<R>> flow;

    private List<RowCell<R>> getVisibleRowCells() {

        List<RowCell<R>> visibleRowCells = new ArrayList<>();

        if (flow == null) {
            flow = (VirtualFlow<RowCell<R>>) listView.lookup("VirtualFlow");
        }

        // flow could still be null
        if (flow != null) {

            RowCell<R> firstCell = flow.getFirstVisibleCell();
            RowCell<R> lastCell = flow.getLastVisibleCell();

            if (firstCell != null && lastCell != null) {

                /*
                 * First and last cell calculations of VirtualFlow are not
                 * precise enough, so to make sure we subtract -1 from the first
                 * cell index and add 1 to the last cell index.
                 */
                for (int index = Math.max(0, firstCell.getIndex() - 1); index <= lastCell.getIndex() + 2; index++) {
                    RowCell<R> cell = flow.getVisibleCell(index);
                    if (cell != null) {
                        visibleRowCells.add(cell);
                    }
                }
            }
        }

        return visibleRowCells;
    }

    private void registerListViewScrollBarListener() {
        ScrollBar scrollBar = findScrollBar(listView, Orientation.VERTICAL);

        if (scrollBar != null) {

            /*
             Important to set the cursor explicitly on the scrollbar, otherwise the cursor shown while
             hovering over the scrollbar will be the one currently set for the graphics area (the scrollbar
             is a child of the graphics area and RowCanvasBehaviour sets the cursor on the whole area).
             */
            scrollBar.setCursor(Cursor.DEFAULT);

            scrollBar.valueProperty().addListener(it -> getSkinnable().drawLinks("scrollbar value changed"));
        }
    }

    private ScrollBar findScrollBar(Parent parent, Orientation orientation) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof ScrollBar) {
                ScrollBar b = (ScrollBar) node;
                if (b.getOrientation().equals(orientation)) {
                    return b;
                }
            }

            if (node instanceof Parent) {
                ScrollBar b = findScrollBar((Parent) node, orientation);
                if (b != null) {
                    return b;
                }
            }
        }

        return null;
    }
}
