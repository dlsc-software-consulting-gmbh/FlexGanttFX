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
package com.flexganttfx.editor;

import com.flexganttfx.model.Row;
import com.flexganttfx.model.layout.AgendaLayout;

import java.time.LocalTime;

import static com.flexganttfx.model.layout.AgendaLayout.LayoutStrategy.OVERLAPPING;

public class AgendaRow extends Row<AgendaRow, AgendaRow, AgendaEntry> {

	public AgendaRow(String name) {
		super(name);

		setHeight(300);
		setMinHeight(100);
		setMaxHeight(1200);

		AgendaLayout agendaLayout = new AgendaLayout();
		agendaLayout.setLayoutStrategy(OVERLAPPING);
		agendaLayout.setStartTime(LocalTime.of(7, 0));
		agendaLayout.setEndTime(LocalTime.of(17, 0));
		agendaLayout.setPadding(30);
		agendaLayout.setOverlapOffset(0);

		setLayout(agendaLayout);
	}
}
