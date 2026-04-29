/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.util;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.core.StringUtils;
import com.flexganttfx.view.GanttChart;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Control;

import java.net.URL;
import java.time.Instant;
import java.time.Year;
import java.util.Locale;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * The super class of most controls found inside FlexGanttFX. Used to support
 * licensing and to improve stylesheet lookup performance.
 *
 * @since 1.3
 */
public abstract class FlexGanttFXControl extends Control {

	/**
	 * Constructs a new control.
	 */
	protected FlexGanttFXControl() {
		nagging();
	}

	private String stylesheet;
	private String stylesheetAtlantaFX;

	/**
	 * Returns {@code true} when the application is currently using an AtlantaFX
	 * theme. Detection is done by checking whether the user-agent stylesheet URL
	 * contains the string {@code "atlantafx"}. No compile-time dependency on the
	 * AtlantaFX library is required.
	 *
	 * @param scene optional
	 */
	public static boolean isAtlantaFXActive(Scene scene) {
		String uas = null;

		if (scene != null) {
			uas = scene.getUserAgentStylesheet();
		}

		if (StringUtils.isBlank(uas)) {
			uas = Application.getUserAgentStylesheet();
		}

		if (uas == null) {
			return false;
		}

		return uas.toLowerCase(Locale.ROOT).contains("atlantafx");
	}

	/**
	 * A helper method that ensures that the resource based lookup of the user
	 * agent stylesheet only happens once per theme type. When an AtlantaFX theme
	 * is active it tries to load {@code <name>-atlantafx.css} from the same
	 * package as {@code clazz} and falls back to the regular {@code fileName}
	 * when no AtlantaFX variant exists.
	 *
	 * @param clazz
	 *            the clazz used for the resource lookup
	 * @param fileName
	 *            the name of the default user agent stylesheet (e.g. {@code "gantt.css"})
	 * @return the external form of the user agent stylesheet (the path)
	 * @since 1.3
	 */
	protected String getUserAgentStylesheet(Class<?> clazz, String fileName) {
		if (isAtlantaFXActive(getScene())) {
			if (stylesheetAtlantaFX == null) {
				String afxFileName = toAtlantaFXFileName(fileName);
				URL afxResource = clazz.getResource(afxFileName);
				if (afxResource != null) {
					stylesheetAtlantaFX = afxResource.toExternalForm();
				} else {
					stylesheetAtlantaFX = resolveDefaultStylesheet(clazz, fileName);
				}
			}
			return stylesheetAtlantaFX;
		}
		return resolveDefaultStylesheet(clazz, fileName);
	}

	private String resolveDefaultStylesheet(Class<?> clazz, String fileName) {
		if (stylesheet == null) {
			stylesheet = Objects.requireNonNull(clazz.getResource(fileName)).toExternalForm();
		}
		return stylesheet;
	}

	private static String toAtlantaFXFileName(String fileName) {
		int dot = fileName.lastIndexOf('.');
		if (dot >= 0) {
			return fileName.substring(0, dot) + "-atlantafx" + fileName.substring(dot);
		}
		return fileName + "-atlantafx";
	}

	/*
	 * Used as a key for the preference, which stores the date when the nagging
	 * screen was last shown.
	 */
	private static final String NAGGING_DATE = "NAG_DATE";

	/*
	 * Used as a key for the preference, which stores the date when the code was
	 * run for the first time.
	 */
	private static final String INSTALLATION_DATE = "INST_DATE";

	private static boolean naggingShown;

	private synchronized void nagging() {
		if (!naggingShown && !FlexGanttFX.isRuntimeLicense()) {
			naggingShown = true;
			System.out.println("FlexGanttFX user interface framework for Java. Version " + FlexGanttFX.getVersion());
			if (FlexGanttFX.isTrialLicense()) {
				System.out.println("Unlicensed evaluation / trial version.");

				/*
				 * Show a nagging dialog, but only once every four hours. The
				 * dialog will cause a delay of several seconds.
				 */
				showNaggingNotification();

				/*
				 * First check whether the user is still within the 60 days
				 * evaluation period. If not cause a system exit.
				 */
				checkEvaluationPeriod();

				/*
				 * Cause an automatic system exit after thirty minutes.
				 */
				exitAfter30Minutes();

				System.out.println("Application will automatically shut down in 30 minutes");

			} else if (FlexGanttFX.isDevelopmentLicense()) {
				System.out.println("Development License (do not use in production systems)");
			}

			System.out.println("(c) 2013-" + Year.now() + " DLSC Software & Consulting GmbH");
			System.out.println("https://www.dlsc.com, https://www.flexganttfx.com");
		}
	}

	/*
	 * Show a nagging screen, but only once per day.
	 */
	private void showNaggingNotification() {
		Preferences preferences = Preferences.userNodeForPackage(GanttChart.class);
		long systemTime = Instant.now().toEpochMilli();

		long naggingScreenLastShownOn = preferences.getLong(NAGGING_DATE, -1);
		if (naggingScreenLastShownOn == -1) {
			naggingScreenLastShownOn = systemTime;
			preferences.putLong(NAGGING_DATE, naggingScreenLastShownOn);
			try {
				preferences.flush();
			} catch (BackingStoreException ex) {
				LoggingDomain.CONFIG.throwing(GanttChart.class.getName(), "showNaggingScreen()", ex);
			}
		}
		long duration = systemTime - naggingScreenLastShownOn;

		if (duration > (4 * 60 * 60 * 1000)) {
			preferences.putLong(NAGGING_DATE, systemTime);
			try {
				preferences.flush();
			} catch (BackingStoreException ex) {
				LoggingDomain.CONFIG.throwing(GanttChart.class.getName(), "showNaggingScreen()", ex);
			}

            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("FlexGanttFX");
            alert.setHeaderText("Missing License");
            alert.setContentText("This is a trial version of FlexGanttFX. Do not use in production systems!\n\nFor more information about licensing options, please visit http://www.dlsc.com");
            alert.show();
		}
	}

	/*
	 * Cause a system exit after 30 minutes.
	 */
	private void exitAfter30Minutes() {
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(30 * 60 * 1000); // 30 minutes
				System.err.println("### Trial duration expired after 30 minutes.");
				System.err.println("### Exiting application.");
				System.exit(0);
			} catch (InterruptedException e) {
				LoggingDomain.CONFIG.throwing(GanttChart.class.getName(), "run()", e);
			}
		});

		thread.setDaemon(true);
		thread.setName("LicenseThread");
		thread.start();
	}

	/*
	 * Check whether the user is still within the 30 day evaluation period.
	 */
	private void checkEvaluationPeriod() {
		long remainingDays = getRemainingDays();
		if (remainingDays > 0) {
			System.out.println("Evaluation period ends in " + remainingDays + " days!");
		} else {
			System.err.println("#############################################################");
			System.err.println("#                                                           #");
			System.err.println("#   FlexGanttFX evaluation period has ended after 90 days!  #");
			System.err.println("#                                                           #");
			System.err.println("#############################################################");
			System.exit(0);
		}
	}

	private long getRemainingDays() {
		long systemTime = Instant.now().toEpochMilli();
		Preferences prefs = Preferences.userNodeForPackage(GanttChart.class);
		long instDate = prefs.getLong(INSTALLATION_DATE, -1);
		if (instDate == -1) {
			prefs.putLong(INSTALLATION_DATE, systemTime);
			try {
				prefs.flush();
			} catch (BackingStoreException ex) {
				LoggingDomain.CONFIG.throwing(GanttChart.class.getName(), "checkEvaluationPeriod()", ex);
			}
			instDate = systemTime;
		}

		long daysUsed = (systemTime - instDate) / (24 * 60 * 60 * 1000);
		return 90 - daysUsed;
	}
}
