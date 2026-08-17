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
package com.flexganttfx.demo.showcase;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

/**
 * Fancy welcome screen shown when the Showcase first opens.
 */
public class WelcomeView extends BorderPane {

    public WelcomeView(Runnable onExplore) {
        getStyleClass().add("welcome-root");

        VBox hero = buildHero(onExplore);
        HBox cards = buildFeatureCards();

        VBox center = new VBox(0, hero, cards);
        center.setAlignment(Pos.TOP_CENTER);

        setCenter(center);
    }

    private VBox buildHero(Runnable onExplore) {
        Label productName = new Label("FlexGanttFX");
        productName.getStyleClass().add("welcome-product-name");

        Label tagline = new Label("The Ultimate Gantt Chart Framework for JavaFX");
        tagline.getStyleClass().add("welcome-tagline");

        Label description = new Label(
            "FlexGanttFX is a professional Gantt chart library that delivers high-performance, " +
            "fully customizable scheduling views for desktop and web applications. " +
            "Explore the demos on the left to discover its capabilities.");
        description.getStyleClass().add("welcome-description");

        Button exploreBtn = new Button("Explore Demos  →");
        exploreBtn.getStyleClass().add("welcome-cta-btn");
        exploreBtn.setOnAction(e -> onExplore.run());

        Region spacer = new Region();
        spacer.setPrefHeight(12);

        VBox hero = new VBox(14, productName, tagline, description, spacer, exploreBtn);
        hero.getStyleClass().add("welcome-hero");
        hero.setAlignment(Pos.CENTER);
        return hero;
    }

    private HBox buildFeatureCards() {
        HBox cards = new HBox();
        cards.getStyleClass().add("welcome-cards-area");
        cards.setAlignment(Pos.CENTER);
        cards.setSpacing(16);
        cards.setPadding(new Insets(0, 60, 60, 60));
        cards.setFillHeight(true);

        cards.getChildren().addAll(
            featureCard(MaterialDesign.MDI_SPEEDOMETER, "#4A90D9",
                "High Performance",
                "Canvas-based rendering handles tens of thousands of activities at 60 fps without virtualization compromises."),
            featureCard(MaterialDesign.MDI_PALETTE, "#7B68EE",
                "Fully Customizable",
                "Custom renderers, CSS styling, and a rich API let you tailor every visual detail to your brand."),
            featureCard(MaterialDesign.MDI_CALENDAR_CLOCK, "#E64980",
                "Flexible Timeline",
                "ChronoUnit and SimpleUnit timelines support anything from milliseconds to decades with configurable grids."),
            featureCard(MaterialDesign.MDI_VIEW_GRID, "#50C878",
                "Container Views",
                "Dual, Quad, and Multi chart containers synchronize scrolling and zooming across multiple Gantt panes."),
            featureCard(MaterialDesign.MDI_LINK_VARIANT, "#FF8C00",
                "Activity Links",
                "Model dependency relationships with Finish-to-Start and other link types, rendered with customizable arrows.")
        );

        return cards;
    }

    private VBox featureCard(MaterialDesign icon, String iconColor, String title, String description) {
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.getStyleClass().add("feature-card-icon");
        fontIcon.setIconColor(Color.web(iconColor));
        fontIcon.setIconSize(36);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("feature-card-title");
        titleLabel.setWrapText(true);

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("feature-card-desc");
        descLabel.setWrapText(true);
        descLabel.setPrefWidth(170);
        descLabel.setMinHeight(Region.USE_PREF_SIZE);

        HBox iconBox = new HBox(fontIcon);
        iconBox.setAlignment(Pos.CENTER);

        VBox card = new VBox(12, iconBox, titleLabel, descLabel);
        card.getStyleClass().add("feature-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(190);
        VBox.setVgrow(descLabel, Priority.ALWAYS);

        return card;
    }
}
