/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.view.util;

import com.flexganttfx.core.FlexGanttFX;
import com.flexganttfx.core.LoggingDomain;
import com.flexganttfx.view.GanttChart;
import javafx.scene.control.Control;

import java.time.Instant;
//import java.util.prefs.BackingStoreException;
//import java.util.prefs.Preferences;

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

	/**
	 * A helper method that ensures that the resource based lookup of the user
	 * agent stylesheet only happens once. Caches the external form of the
	 * resource.
	 *
	 * @param clazz
	 *            the clazz used for the resource lookup
	 * @param fileName
	 *            the name of the user agent stylesheet
	 * @return the external form of the user agent stylesheet (the path)
	 * @since 1.3
	 */
	protected String getUserAgentStylesheet(Class<?> clazz, String fileName) {
		if (stylesheet == null) {
			stylesheet = clazz.getResource(fileName).toExternalForm();
		}

		return stylesheet;
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

			System.out.println("(c) 2013-2019 Dirk Lemmermann Software & Consulting");
			System.out.println("http://www.dlsc.com");
		}
	}

	/*
	 * Show a nagging screen, but only once per day.
	 */
	private void showNaggingNotification() {
//		Preferences prefs = Preferences.userNodeForPackage(GanttChart.class);
//		long systemTime = Instant.now().toEpochMilli();
//
//		long naggingScreenLastShownOn = prefs.getLong(NAGGING_DATE, -1);
//		if (naggingScreenLastShownOn == -1) {
//			naggingScreenLastShownOn = systemTime;
//			prefs.putLong(NAGGING_DATE, naggingScreenLastShownOn);
//			try {
//				prefs.flush();
//			} catch (BackingStoreException ex) {
//				LoggingDomain.CONFIG.throwing(GanttChart.class.getName(), "showNaggingScreen()", ex);
//			}
//		}
//		long duration = systemTime - naggingScreenLastShownOn;
//
//		if (duration > (4 * 60 * 60 * 1000)) {
//			prefs.putLong(NAGGING_DATE, systemTime);
//			try {
//				prefs.flush();
//			} catch (BackingStoreException ex) {
//				LoggingDomain.CONFIG.throwing(GanttChart.class.getName(), "showNaggingScreen()", ex);
//			}
//
//            Alert alert = new Alert(AlertType.WARNING);
//            alert.setTitle("FlexGanttFX");
//            alert.setHeaderText("Missing License");
//            alert.setContentText("This is a trial version of FlexGanttFX. Do not use in production systems!\n\nFor more information about licensing options, please visit http://www.dlsc.com");
//            alert.show();
//		}
	}

	/*
	 * Cause a system exit after 30 minutes.
	 */
	private void exitAfter30Minutes() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(30 * 60 * 1000); // 30 minutes
					System.err.println("### Trial duration expired after 30 minutes.");
					System.err.println("### Exiting application.");
					System.exit(0);
				} catch (InterruptedException e) {
					LoggingDomain.CONFIG.throwing(GanttChart.class.getName(), "run()", e);
				}
			}
		};

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
//		Preferences prefs = Preferences.userNodeForPackage(GanttChart.class);
//		long instDate = prefs.getLong(INSTALLATION_DATE, -1);
//		if (instDate == -1) {
//			prefs.putLong(INSTALLATION_DATE, systemTime);
//			try {
//				prefs.flush();
//			} catch (BackingStoreException ex) {
//				LoggingDomain.CONFIG.throwing(GanttChart.class.getName(), "checkEvaluationPeriod()", ex);
//			}
//			instDate = systemTime;
//		}

		long instDate = 0;
		long daysUsed = (systemTime - instDate) / (24 * 60 * 60 * 1000);
		return 90 - daysUsed;
	}
}
