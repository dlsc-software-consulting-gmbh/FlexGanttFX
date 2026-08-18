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
package com.flexganttfx.demo;

import com.flexganttfx.model.activity.MutableCompletableActivityBase;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DemoActivity extends MutableCompletableActivityBase<String> {

	private int lineIndex;
	private Color color;

	public DemoActivity(String name) {
		super(name);
		
		Instant start = Instant.now().plus((int) (Math.random() * 365), ChronoUnit.DAYS);
		Instant end = start.plus((int) (Math.random() * 3), ChronoUnit.DAYS);

		setStartTime(start);
		setEndTime(end);
	}

	public DemoActivity() {
		this(null);
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setLineIndex(int lineIndex) {
		this.lineIndex = lineIndex;
	}

	public int getLineIndex() {
		return lineIndex;
	}
}
