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
package com.flexganttfx.editor;

import com.flexganttfx.model.activity.MutableActivity;

import java.time.Duration;
import java.time.Instant;

/**
 * The interface required by activities so that they can take part in agenda
 * editing and conflict resolution.
 */
public interface AgendaEntry extends MutableActivity {

	/**
	 * An enum used to indicate into which direction the entry is currently
	 * being pushed.
	 */
    enum PushDirection {
		UP, DOWN, NONE
    }

	/**
	 * Sets a group ID on the entry. All entries with the same ID are members of
	 * the same group.
	 */
    void setGroupId(Object id);

	/**
	 * Returns the group ID of the entry.
	 */
    Object getGroupId();

	/**
	 * Sets the original start time on the entry just before it is being pushed
	 * out of the way.
	 */
    void setOriginalStartTime(Instant time);

	/**
	 * Returns the original start time of the entry.
	 */
    Instant getOriginalStartTime();

	/**
	 * Sets the original end time on the entry just before it is being pushed
	 * out of the way.
	 */
    void setOriginalEndTime(Instant time);

	/**
	 * Returns the original end time of the entry.
	 */
    Instant getOriginalEndTime();

	/**
	 * Sets the pusher of the entry. 
	 */
    void setPusher(AgendaEntry pusher);

	/**
	 * Returns the pusher of the entry.
	 */
    AgendaEntry getPusher();

	/**
	 * Sets the push direction (UP, DOWN, NONE) on the entry. 
	 */
    void setPushDirection(PushDirection direction);

	/**
	 * Returns the push direction of the entry.
	 */
    PushDirection getPushDirection();

	/**
	 * Returns the duration of the entry.
	 */
    Duration getDuration();
}