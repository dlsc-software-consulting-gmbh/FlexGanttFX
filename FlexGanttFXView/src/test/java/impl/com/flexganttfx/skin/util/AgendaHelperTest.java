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
