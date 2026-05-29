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
package com.flexganttfx.msproject.model;

import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.repository.ListActivityRepository;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.sf.mpxj.Task;

import java.time.Instant;
import java.util.Map;

import static com.flexganttfx.model.repository.ListActivityRepository.IteratorType.SIMPLE_ITERATOR;

public class MSProjectTaskRow
        extends Row<MSProjectTaskRow, MSProjectTaskRow, MSProjectTaskActivity> {

    public MSProjectTaskRow() {
    }

    public MSProjectTaskRow(Layer layer, net.sf.mpxj.Task task, Map<Task, ActivityRef<MSProjectTaskActivity>> taskMap) {

        setName(task.getName());
        setUserObject(task);
        setExpanded(true);

        setRepository(new ListActivityRepository<>(SIMPLE_ITERATOR));

        for (net.sf.mpxj.Task childTask : task.getChildTasks()) {
            getChildren().add(new MSProjectTaskRow(layer, childTask, taskMap));
        }

        startTime.set(task.getStart().toInstant());
        finishTime.set(task.getFinish().toInstant());
        if (task.getPercentageComplete() != null) {
            percentageComplete.set(task.getPercentageComplete().doubleValue());
        }

        MSProjectTaskActivity activity = new MSProjectTaskActivity(this);
        addActivity(layer, activity);

        taskMap.put(task, new ActivityRef<>(this, layer, activity));
    }

    public final net.sf.mpxj.Task getTask() {
        return (net.sf.mpxj.Task) getUserObject();
    }

    private final ObjectProperty<Instant> startTime = new SimpleObjectProperty<>();

    public final ObjectProperty<Instant> starTimeProperty() {
        return startTime;
    }

    public final Instant getStartTime() {
        return startTime.get();
    }

    public final void setStartTime(Instant instant) {
        startTime.set(instant);
    }

    // finish time

    public final ObjectProperty<Instant> finishTime = new SimpleObjectProperty<>();

    public final ObjectProperty<Instant> finishTimeProperty() {
        return finishTime;
    }

    public final Instant getFinishTime() {
        return finishTime.get();
    }

    public final void setFinishTime(Instant instant) {
        finishTime.set(instant);
    }

    // percentage complete

    public final DoubleProperty percentageComplete = new SimpleDoubleProperty();

    public final DoubleProperty percentageCompleteProperty() {
        return percentageComplete;
    }

    public final void setPercentageComplete(double complete) {
        percentageComplete.set(complete);
    }

    public final double getPercentageComplete() {
        return percentageComplete.get();
    }
}
