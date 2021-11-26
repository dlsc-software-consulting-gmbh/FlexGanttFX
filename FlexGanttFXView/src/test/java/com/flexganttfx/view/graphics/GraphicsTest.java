/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.graphics;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.model.Activity;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;
import com.flexganttfx.model.activity.ActivityBase;
import com.flexganttfx.view.GanttChart;
import de.sandec.jmemorybuddy.JMemoryBuddy;
import impl.com.flexganttfx.skin.graphics.RowCanvas;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class GraphicsTest extends ApplicationTest implements EventHandler<ActivityEvent> {

    private GraphicsBase<?> view;
    private ActivityEvent event;
    private final Activity activity = new ActivityBase<>();
    private ActivityRef<Activity> activityRef;
    private RowCanvas<?> canvas;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302D021442068CF635B84BFC157478E2D60932F52AFBD59E021500952B8FD690A764EC20AE70A3D8655029BDD66532");

        GanttChart<?> gantt = new GanttChart<>();
        view = gantt.getGraphics();
        canvas = new RowCanvas<>(view);
        Row<Row<?, ?, ?>, Row<?, ?, ?>, Activity> row = new Row<>() {};
        Layer layer = new Layer("Layer");
        activityRef = new ActivityRef<>(row, layer, activity);
        event = null;
    }

	@Test
	public void shouldGCGraphicsListView() {
		JMemoryBuddy.memoryTest(checker -> {
			ListViewGraphics notReferenced = new ListViewGraphics();
			checker.assertCollectable(notReferenced);
		});
	}

    @Test
    public void shouldReceiveActivityChangeEvent() {

        // given
        view.setOnActivityChange(this);

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.ACTIVITY_CHANGE));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.ACTIVITY_CHANGE)));
    }

    @Test
    public void shouldReceiveActivityChartValueChangeFinishedEvent() {

        // given
        view.setOnActivityChartValueChangeFinished(this);
        double value = 33.33;

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_VALUE_CHANGE_FINISHED, value));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.CHART_VALUE_CHANGE_FINISHED)));
        assertThat(event.getOldValue(), is(equalTo(value)));
    }

    @Test
    public void shouldReceiveActivityChartValueChangeOngoingEvent() {

        // given
        view.setOnActivityChartValueChangeOngoing(this);
        double value = 33.33;

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_VALUE_CHANGE_ONGOING, value));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.CHART_VALUE_CHANGE_ONGOING)));
        assertThat(event.getOldValue(), is(equalTo(value)));
    }

    @Test
    public void shouldReceiveActivityChartHighValueChangeFinishedEvent() {

        // given
        view.setOnActivityChartHighValueChangeFinished(this);
        double value = 33.33;

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_HIGH_VALUE_CHANGE_FINISHED, value));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.CHART_HIGH_VALUE_CHANGE_FINISHED)));
        assertThat(event.getOldValue(), is(equalTo(value)));
    }

    @Test
    public void shouldReceiveActivityChartHighValueChangeOngoingEvent() {

        // given
        view.setOnActivityChartHighValueChangeOngoing(this);
        double value = 33.33;

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_HIGH_VALUE_CHANGE_ONGOING, value));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.CHART_HIGH_VALUE_CHANGE_ONGOING)));
        assertThat(event.getOldValue(), is(equalTo(value)));
    }

    @Test
    public void shouldReceiveActivityChartLowValueChangeFinishedEvent() {

        // given
        view.setOnActivityChartLowValueChangeFinished(this);
        double value = 33.33;

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_LOW_VALUE_CHANGE_FINISHED, value));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.CHART_LOW_VALUE_CHANGE_FINISHED)));
        assertThat(event.getOldValue(), is(equalTo(value)));
    }

    @Test
    public void shouldReceiveActivityChartLowValueChangeOngoingEvent() {

        // given
        view.setOnActivityChartLowValueChangeOngoing(this);
        double value = 33.33;

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.CHART_LOW_VALUE_CHANGE_ONGOING, value));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.CHART_LOW_VALUE_CHANGE_ONGOING)));
        assertThat(event.getOldValue(), is(equalTo(value)));
    }

    @Test
    public void shouldReceiveActivityPercentageChangeFinishedEvent() {

        // given
        view.setOnActivityPercentageChangeFinished(this);

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.PERCENTAGE_CHANGE_FINISHED));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.PERCENTAGE_CHANGE_FINISHED)));
    }

    @Test
    public void shouldReceiveActivityPercentageChangeOngoingEvent() {

        // given
        view.setOnActivityPercentageChangeOngoing(this);

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.PERCENTAGE_CHANGE_ONGOING));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.PERCENTAGE_CHANGE_ONGOING)));
    }

    @Test
    public void shouldReceiveActivityDragFinishedEvent() {

        // given
        view.setOnActivityDragFinished(this);

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.DRAG_FINISHED));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.DRAG_FINISHED)));
    }

    @Test
    public void shouldReceiveActivityDragOngoingEvent() {

        // given
        view.setOnActivityDragOngoing(this);

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.DRAG_ONGOING));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.DRAG_ONGOING)));
    }

    @Test
    public void shouldReceiveActivityHorizontalDragFinishedEvent() {

        // given
        view.setOnActivityHorizontalDragFinished(this);

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.HORIZONTAL_DRAG_FINISHED));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.HORIZONTAL_DRAG_FINISHED)));
    }

    @Test
    public void shouldReceiveActivityHorizontalDragOngoingEvent() {

        // given
        view.setOnActivityHorizontalDragOngoing(this);

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.HORIZONTAL_DRAG_ONGOING));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.HORIZONTAL_DRAG_ONGOING)));
    }

    @Test
    public void shouldReceiveActivityVerticalDragFinishedEvent() {

        // given
        view.setOnActivityVerticalDragFinished(this);

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.VERTICAL_DRAG_FINISHED));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.VERTICAL_DRAG_FINISHED)));
    }

    @Test
    public void shouldReceiveActivityVerticalDragOngoingEvent() {

        // given
        view.setOnActivityVerticalDragOngoing(this);

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.VERTICAL_DRAG_ONGOING));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.VERTICAL_DRAG_ONGOING)));
    }

    @Test
    public void shouldReceiveActivityStartTimeChangeFinishedEvent() {

        // given
        view.setOnActivityStartTimeChangeFinished(this);
        Instant startTime = Instant.now();

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.START_TIME_CHANGE_FINISHED, startTime));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.START_TIME_CHANGE_FINISHED)));
        assertThat(event.getOldTime(), is(equalTo(startTime)));
    }

    @Test
    public void shouldReceiveActivityStartTimeChangeOngoingEvent() {

        // given
        view.setOnActivityStartTimeChangeOngoing(this);
        Instant startTime = Instant.now();

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.START_TIME_CHANGE_ONGOING, startTime));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.START_TIME_CHANGE_ONGOING)));
        assertThat(event.getOldTime(), is(equalTo(startTime)));
    }

    @Test
    public void shouldReceiveActivityEndTimeChangeFinishedEvent() {

        // given
        view.setOnActivityEndTimeChangeFinished(this);
        Instant endTime = Instant.now();

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.END_TIME_CHANGE_FINISHED, endTime));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.END_TIME_CHANGE_FINISHED)));
        assertThat(event.getOldTime(), is(equalTo(endTime)));
    }

    @Test
    public void shouldReceiveActivityEndTimeChangeOngoingEvent() {

        // given
        view.setOnActivityEndTimeChangeOngoing(this);
        Instant endTime = Instant.now();

        // when
        view.fireEvent(new ActivityEvent(activityRef, canvas, ActivityEvent.END_TIME_CHANGE_ONGOING, endTime));

        // then
        assertThat(event, is(notNullValue()));
        assertThat(event.getActivityRef(), is(equalTo(activityRef)));
        assertThat(event.getSource(), is(equalTo(view)));
        assertThat(event.getTarget(), is(equalTo(view)));
        assertThat(event.getEventType(), is(equalTo(ActivityEvent.END_TIME_CHANGE_ONGOING)));
        assertThat(event.getOldTime(), is(equalTo(endTime)));
    }

    @Override
    public void handle(ActivityEvent event) {
        this.event = event;
    }
}
