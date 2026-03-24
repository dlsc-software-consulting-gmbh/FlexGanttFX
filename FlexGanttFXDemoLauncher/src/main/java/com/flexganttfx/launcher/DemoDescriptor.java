/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.launcher;

import org.kordamp.ikonli.Ikon;

/**
 * Immutable descriptor for a single demo entry shown as a tile in the launcher.
 */
public final class DemoDescriptor {

    private final String title;
    private final String subtitle;
    private final String description;
    private final Ikon icon;
    /** CSS linear-gradient string for the card header, e.g. "linear-gradient(to bottom, #0A2654, #1565C0)" */
    private final String headerGradient;
    /** Hex colour string used for the Launch button accent, e.g. "#1565C0" */
    private final String accentColor;
    /** Runnable that opens the demo in a new Stage, or {@code null} for online-only demos. */
    private final Runnable launcher;
    /** URL of the online/JPro demo, or {@code null} if not available. */
    private final String onlineUrl;

    public DemoDescriptor(String title,
                          String subtitle,
                          String description,
                          Ikon icon,
                          String headerGradient,
                          String accentColor,
                          Runnable launcher,
                          String onlineUrl) {
        this.title          = title;
        this.subtitle       = subtitle;
        this.description    = description;
        this.icon           = icon;
        this.headerGradient = headerGradient;
        this.accentColor    = accentColor;
        this.launcher       = launcher;
        this.onlineUrl      = onlineUrl;
    }

    public String getTitle()          { return title; }
    public String getSubtitle()       { return subtitle; }
    public String getDescription()    { return description; }
    public Ikon   getIcon()           { return icon; }
    public String getHeaderGradient() { return headerGradient; }
    public String getAccentColor()    { return accentColor; }
    public Runnable getLauncher()     { return launcher; }
    public String getOnlineUrl()      { return onlineUrl; }

    public boolean hasLauncher()  { return launcher  != null; }
    public boolean hasOnlineUrl() { return onlineUrl != null; }
}
