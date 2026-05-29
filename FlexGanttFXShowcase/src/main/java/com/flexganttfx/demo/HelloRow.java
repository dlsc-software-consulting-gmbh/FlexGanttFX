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
package com.flexganttfx.demo;

import com.flexganttfx.model.Layer;
import com.flexganttfx.model.Row;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class HelloRow extends Row<HelloRow, HelloRow, HelloActivity> {
	public static final Layer layer = new Layer("Hello Layer");
	
	public HelloRow(String name, int activityCount) {
		super(name);
		
		Instant time = Instant.now();
		
		for (int i = 0; i < activityCount; i++) {
			long days = (long) (Math.random() * 10);
			
			Instant st = Instant.from(time);
			Instant et = st.plus(days, ChronoUnit.DAYS);

			HelloActivity activity = new HelloActivity();
			activity.setStartTime(st);
			activity.setEndTime(et);
			
			days = Math.min(1, (long) (Math.random() * 5));
			time = et.plus(days, ChronoUnit.DAYS);
			
			addActivity(layer, activity);
		}
	}
	
	public HelloRow(String name) {
		super(name);
	}
}
