package com.flexganttfx.covid;

import javafx.scene.image.Image;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Flags {

    private static final Map<String, Image> cache = new HashMap<>();

    public static Image getFlag(String isoCode) {
        return cache.computeIfAbsent(isoCode, key -> {
            final URL resource = Flags.class.getResource("flags/" + isoCode + ".png");
            if (resource != null) {
                return new Image(resource.toExternalForm());
            }
            return null;
        });
    }
}
