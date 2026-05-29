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

import com.flexganttfx.model.activity.MutableActivityBase;

import java.time.Instant;

import static com.flexganttfx.editor.AgendaEntryBase.Type.GERMAN;

/**
 * A base implementation of the {@link AgendaEntry} interface. This class is
 * only used for the showcase application.
 */
public class AgendaEntryBase extends MutableActivityBase<String> implements
		AgendaEntry {

	public enum Type {
		ENGLISH("English"), GERMAN("German"), MATH("Math"), SPORT("Sport"), CHEMISTRY(
				"Chemistry"), PHYSICS("Physics"), BIOLOGY("Biology"), RELIGION(
				"Religion");

		private final String displayName;

		Type(String name) {
			this.displayName = name;
		}

		public String getDisplayName() {
			return displayName;
		}
	}

	private Instant originalStartTime;
	private Instant originalEndTime;

	private Type type = GERMAN;
	private Object groupId;

	public AgendaEntryBase(Type type) {
		this.type = type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public void setGroupId(Object id) {
		this.groupId = id;
	}

	@Override
	public Object getGroupId() {
		return groupId;
	}

	@Override
	public void setOriginalStartTime(Instant originalStartTime) {
		this.originalStartTime = originalStartTime;
	}

	@Override
	public Instant getOriginalStartTime() {
		return originalStartTime;
	}

	@Override
	public void setOriginalEndTime(Instant originalEndTime) {
		this.originalEndTime = originalEndTime;
	}

	@Override
	public Instant getOriginalEndTime() {
		return originalEndTime;
	}

	private AgendaEntry pusher;

	@Override
	public void setPusher(AgendaEntry pusher) {
		this.pusher = pusher;
	}

	@Override
	public AgendaEntry getPusher() {
		return pusher;
	}

	private PushDirection pushDirection = PushDirection.NONE;

	@Override
	public void setPushDirection(PushDirection pushDirection) {
		this.pushDirection = pushDirection;
	}

	@Override
	public PushDirection getPushDirection() {
		return pushDirection;
	}
}