package com.flexganttfx.covid;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class AboutPane extends StackPane {

    public AboutPane(CovidUI ui) {
        getStyleClass().add("about-pane");

        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(750);
        imageView.setImage(new Image(AboutPane.class.getResource("about.jpg").toExternalForm()));
        getChildren().add(imageView);
        imageView.setOnMouseClicked(evt -> ui.setShowAbout(false));
    }
}
