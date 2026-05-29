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
package com.flexganttfx.project.model.business;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

public class Task {

	private String title = "";
	private LocalDate startDate = LocalDate.now().plusDays(1);
	private LocalDate endDate = LocalDate.now().plusDays(6);

	public Task() {
	}

	public Task(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		requireNonNull(title);
		this.title = title;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate date) {
		requireNonNull(date);
		this.startDate = date;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate date) {
		requireNonNull(date);
		this.endDate = date;
	}
}
