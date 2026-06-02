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
package com.flexganttfx.msproject;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import com.flexganttfx.extras.util.StageManager;
import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.extras.GanttChartStatusBar;
import com.flexganttfx.extras.GanttChartToolBar;
import com.flexganttfx.extras.properties.view.GanttChartConfigurationView;
import com.flexganttfx.model.ActivityLink;
import com.flexganttfx.msproject.model.MSProjectTaskRow;
import com.flexganttfx.msproject.view.MSProjectGanttChart;
import com.flexganttfx.view.GanttChartBase;
import com.flexganttfx.view.graphics.renderer.CurvedLinkRenderer;
import com.flexganttfx.view.util.Messages;
import com.jpro.webapi.WebAPI;
import javafx.util.StringConverter;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.prefs.Preferences;

public class MSProjectApp extends Application {

	private static final Theme MODENA = Theme.of("Modena", Application.STYLESHEET_MODENA, false);

	private static final List<Theme> THEMES = List.of(
		new PrimerDark(),
		new PrimerLight(),
		new NordDark(),
		new NordLight(),
		new CupertinoDark(),
		new CupertinoLight(),
		new Dracula(),
		MODENA
	);

	private static final Preferences PREFS = Preferences.userNodeForPackage(MSProjectApp.class);
	private static final String PREF_THEME = "theme";

	private static Theme resolvePersistedTheme() {
		String saved = PREFS.get(PREF_THEME, null);
		if (saved != null) {
			for (Theme t : THEMES) {
				if (t.getName().equals(saved)) {
					return t;
				}
			}
		}
		return THEMES.get(0); // default: PrimerDark
	}

	private static final String STAGE_TITLE = "MSProject Reader";
	private MSProjectGanttChart gantt;
	private FileChooser fileChooser;
	private Stage stage;

	@Override
	public void start(Stage stage) {
		if (!FlexGanttFX.isLicenseKeySet()) {
			FlexGanttFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=12;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302C02142BD7F914E6633D7DBA0B8564D8FC20EC249BCFD702142558B5C6FF46325A0A698A1E8036828E54D6FEC8");
		}

		this.stage = stage;
		this.stage.setTitle(STAGE_TITLE);

		gantt = new MSProjectGanttChart();
		gantt.setScrollBarType(GanttChartBase.ScrollBarType.INFINITE);
		gantt.getGraphics().setLinkRenderer(ActivityLink.class, new CurvedLinkRenderer<>(gantt.getGraphics(), "Custom Link Renderer") {
			@Override
			public void draw(ActivityLink<?> link, GraphicsContext gc, Rectangle2D sourceBounds, Rectangle2D targetBounds) {
				if (link.getTargetActivityRef().getActivity().getStartTime().isBefore(link.getSourceActivityRef().getActivity().getEndTime())) {
					setStrokeColor(Color.CRIMSON);
					setArrowHeadColor(Color.CRIMSON);
				} else {
					setStrokeColor(Color.SLATEGRAY);
					setArrowHeadColor(Color.SLATEGRAY);
				}

				super.draw(link, gc, sourceBounds, targetBounds);
			}
		});

		// Load the first sample project as the default
		SampleProject defaultProject = SampleProjectFactory.ALL.get(0);
		gantt.load(defaultProject.getFactory().get());
		gantt.setDetail(new GanttChartConfigurationView(gantt));

		VBox.setVgrow(gantt, Priority.ALWAYS);

		VBox vbox = new VBox(0);

		MenuBar menuBar = createMenuBar();
		vbox.getChildren().add(menuBar);

		GanttChartStatusBar<MSProjectTaskRow> statusBar = new GanttChartStatusBar<>(gantt);

		Scene scene = new Scene(vbox);
		scene.setUserAgentStylesheet(resolvePersistedTheme().getUserAgentStylesheet());

		if (WebAPI.isBrowser()) {
			vbox.getChildren().addAll(gantt, statusBar);
		} else {
			GanttChartToolBar<MSProjectTaskRow> toolBar = new GanttChartToolBar<>(gantt);
			vbox.getChildren().addAll(toolBar, gantt, statusBar);

			ComboBox<GanttChartBase.ScrollBarType> box = new ComboBox<>();
			box.setConverter(new StringConverter<>() {
				@Override
				public String toString(GanttChartBase.ScrollBarType type) {
					if (type == null) return "";
					switch (type) {
						case FIXED_HORIZON: return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_FIXED_HORIZON");
						case INFINITE:      return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_INFINITE");
						case NONE:
						default:            return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_NONE");
					}
				}

				@Override
				public GanttChartBase.ScrollBarType fromString(String string) {
					return null;
				}
			});
			box.getItems().setAll(GanttChartBase.ScrollBarType.values());
			box.valueProperty().bindBidirectional(gantt.scrollBarTypeProperty());
			box.valueProperty().addListener((obs, oldType, newType) -> {
				if (oldType != null && newType != null) {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle(Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_DIALOG_TITLE"));
					alert.setHeaderText(box.getConverter().toString(newType));
					alert.setContentText(scrollBarTypeDescription(newType));
					alert.initOwner(box.getScene().getWindow());
					alert.show();
				}
			});
			toolBar.getItems().add(1, box);
		}

		stage.setScene(scene);
		stage.sizeToScene();
		stage.centerOnScreen();

		StageManager.install(stage, "msproject-gantt", 1000, 800);

		ScenicView.show(scene);

		stage.show();
	}

	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");
		MenuItem openItem = new MenuItem("Open...");
		openItem.setOnAction(event -> openFile());
		fileMenu.getItems().add(openItem);

		fileMenu.getItems().add(new SeparatorMenuItem());

		ToggleGroup projectGroup = new ToggleGroup();
		for (SampleProject project : SampleProjectFactory.ALL) {
			RadioMenuItem item = new RadioMenuItem(project.getName());
			item.setToggleGroup(projectGroup);
			item.setOnAction(evt -> {
				gantt.load(project.getFactory().get());
				stage.setTitle(STAGE_TITLE + " – " + project.getName());
			});
			fileMenu.getItems().add(item);
		}
		// Select the first item to match the project loaded at startup
		((RadioMenuItem) fileMenu.getItems().get(2)).setSelected(true);

		menuBar.getMenus().add(fileMenu);

		Menu themeMenu = new Menu("Theme");
		ToggleGroup themeGroup = new ToggleGroup();
		Theme activeTheme = resolvePersistedTheme();
		for (Theme t : THEMES) {
			RadioMenuItem item = new RadioMenuItem(t.getName());
			item.setToggleGroup(themeGroup);
			item.setSelected(t.getName().equals(activeTheme.getName()));
			item.setOnAction(evt -> {
				this.stage.getScene().setUserAgentStylesheet(t.getUserAgentStylesheet());
				PREFS.put(PREF_THEME, t.getName());
			});
			themeMenu.getItems().add(item);
		}
		menuBar.getMenus().add(themeMenu);

		return menuBar;
	}

	protected void openFile() {
		if (fileChooser == null) {
			fileChooser = new FileChooser();
		}

		File file = fileChooser.showOpenDialog(gantt.getScene().getWindow());
		if (file != null) {
			try {
				gantt.load(file);
				stage.setTitle(STAGE_TITLE + ": " + file.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	private static String scrollBarTypeDescription(GanttChartBase.ScrollBarType type) {
		switch (type) {
			case FIXED_HORIZON: return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_FIXED_HORIZON_DESCRIPTION");
			case INFINITE:      return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_INFINITE_DESCRIPTION");
			case NONE:
			default:            return Messages.getString("GanttChartBase.SCROLL_BAR_TYPE_NONE_DESCRIPTION");
		}
	}
}
