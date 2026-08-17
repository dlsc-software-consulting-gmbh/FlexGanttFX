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

import com.flexganttfx.editor.AgendaEntryBase.Type;
import com.flexganttfx.model.ActivityRef;
import com.flexganttfx.view.graphics.GraphicsBase;
import com.flexganttfx.view.graphics.GraphicsBase.ContextMenuParameter;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

import java.util.List;
import java.util.UUID;

/**
 * Just a prototype context menu. Not for production use.
 */
public class AgendaEditorContextMenu extends ContextMenu implements Callback<ContextMenuParameter<AgendaRow>, ContextMenu>{

	private List<ActivityRef<?>> activities;

    public AgendaEditorContextMenu(GraphicsBase<?> graphics) {

		Menu typeMenu = new Menu("Type");
		for (Type type : Type.values()) {
			final Type ftype = type;
			MenuItem item = new MenuItem(type.getDisplayName());
			typeMenu.getItems().add(item);
			item.setOnAction(evt -> {
				for (ActivityRef<?> ref : graphics.getSelectedActivities()) {
					AgendaEntryBase entry = (AgendaEntryBase) ref.getActivity();
					entry.setType(ftype);
				}
				graphics.redraw();
			});
			
			Circle circle = new Circle(6);
			Color color = Color.BLACK;
			switch (type) {
			case GERMAN:
				color = Color.GREEN;
				break;
			case BIOLOGY:
				color = Color.ORANGE;
				break;
			case CHEMISTRY:
				color = Color.CADETBLUE;
				break;
			case ENGLISH:
				color = Color.CRIMSON;
				break;
			case MATH:
				color = Color.INDIANRED;
				break;
			case PHYSICS:
				color = Color.CORNFLOWERBLUE;
				break;
			case RELIGION:
				color = Color.WHEAT;
				break;
			case SPORT:
				color = Color.CORAL;
				break;
			default:
				break;
				
			}
			circle.setFill(color);
			circle.setStroke(color);
			item.setGraphic(circle);
		}

		getItems().add(typeMenu);

        MenuItem group = new MenuItem("Group");
		group.setOnAction(evt -> {
			UUID uuid = UUID.randomUUID();

			if (graphics.getSelectedActivities().size() > 1) {
				for (ActivityRef<?> ref : graphics.getSelectedActivities()) {
					AgendaEntryBase entry = (AgendaEntryBase) ref.getActivity();
					entry.setGroupId(uuid);
				}
				graphics.redraw();
			}
		});

		getItems().add(group);

		MenuItem ungroup = new MenuItem("Ungroup All");
		ungroup.setOnAction(evt -> {
			for (ActivityRef<?> ref : graphics.getSelectedActivities()) {
				AgendaEntryBase entry = (AgendaEntryBase) ref.getActivity();
				entry.setGroupId(null);
			}
			graphics.redraw();
		});

		getItems().add(ungroup);

		MenuItem removeFromGroup = new MenuItem("Ungroup");
		removeFromGroup.setOnAction(evt -> {
			for (ActivityRef<?> ref : activities) {
				AgendaEntryBase entry = (AgendaEntryBase) ref.getActivity();
				entry.setGroupId(null);
			}
			graphics.redraw();
		});

		getItems().add(removeFromGroup);
	}

	@Override
	public ContextMenu call(ContextMenuParameter<AgendaRow> param) {
		activities = param.getActivities();
		return this;
	}
}
