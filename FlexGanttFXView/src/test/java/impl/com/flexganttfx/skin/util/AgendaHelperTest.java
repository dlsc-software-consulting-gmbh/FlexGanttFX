/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package impl.com.flexganttfx.skin.util;

import com.flexganttfx.model.layout.AgendaLayout;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AgendaHelperTest {

	@Test
	public void shouldReturnStepRate1For10Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.of(0, 0));
		layout.setEndTime(LocalTime.of(10, 0));
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 100);

		// then
		assertThat(stepRate, is(equalTo(1L)));
	}
	
	@Test
	public void shouldReturnStepRate2For10Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.of(0, 0));
		layout.setEndTime(LocalTime.of(10, 0));
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 50);

		// then
		assertThat(stepRate, is(equalTo(2L)));
	}
	
	@Test
	public void shouldReturnStepRate5For10Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.of(0, 0));
		layout.setEndTime(LocalTime.of(10, 0));
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 20);

		// then
		assertThat(stepRate, is(equalTo(5L)));
	}
	
	@Test
	public void shouldReturnStepRate1For24Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.MIN);
		layout.setEndTime(LocalTime.MAX);
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 240);

		// then
		assertThat(stepRate, is(equalTo(1L)));
	}

	@Test
	public void shouldReturnStepRate2For24Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.MIN);
		layout.setEndTime(LocalTime.MAX);
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 120);

		// then
		assertThat(stepRate, is(equalTo(2L)));
	}

	@Test
	public void shouldReturnStepRate3For24Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.MIN);
		layout.setEndTime(LocalTime.MAX);
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 80);

		// then
		assertThat(stepRate, is(equalTo(3L)));
	}

	@Test
	public void shouldReturnStepRate4For24Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.MIN);
		layout.setEndTime(LocalTime.MAX);
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 60);

		// then
		assertThat(stepRate, is(equalTo(4L)));
	}

	@Test
	public void shouldReturnStepRate6For24Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.MIN);
		layout.setEndTime(LocalTime.MAX);
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 40);

		// then
		assertThat(stepRate, is(equalTo(6L)));
	}
	
	@Test
	public void shouldReturnStepRate8For24Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.MIN);
		layout.setEndTime(LocalTime.MAX);
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 30);

		// then
		assertThat(stepRate, is(equalTo(8L)));
	}
	
	@Test
	public void shouldReturnStepRate12For24Hours() {
		// given
		AgendaLayout layout = new AgendaLayout();
		layout.setStartTime(LocalTime.MIN);
		layout.setEndTime(LocalTime.MAX);
		layout.setMinLineSpacing(10);

		// when
		long stepRate = AgendaHelper.computeStepRate(layout, 20);

		// then
		assertThat(stepRate, is(equalTo(12L)));
	}
}
