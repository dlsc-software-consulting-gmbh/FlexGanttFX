/**
 * Copyright (C) 2014 - 2026 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.core;

import com.smardec.license4j.License;
import com.smardec.license4j.LicenseManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for setting the license key.
 *
 * @since 1.0
 */
public final class FlexGanttFX {

    private static String version;

    /**
     * Returns the FlexGanttFX version number in the format major.minor.bug
     * (1.0.0).
     *
     * @return the FlexGanttFX version number
     * @since 1.0
     */
    public static String getVersion() {
        if (version == null) {
            InputStream stream = FlexGanttFX.class.getResourceAsStream("version.properties");
            Properties props = new Properties();
            try {
                props.load(stream);
            } catch (IOException ex) {
                LoggingDomain.CONFIG.throwing(FlexGanttFX.class.getName(), "getVersion()", ex);
            }
            version = props.getProperty("flexganttfx.version", "1.0.0");

            LoggingDomain.CONFIG.info("FlexGanttFX Version: " + version);
        }
        return version;
    }

    /**
     * Represents the "Licensee" feature.
     *
     * @since 1.0
     */
    public static final String FEATURE_LICENSEE = "LIC";

    /**
     * Represents the "Product" feature.
     *
     * @since 1.0
     */
    public static final String FEATURE_PRODUCT = "PRO";

    /**
     * Represents the "Runtime" feature.
     *
     * @since 1.0
     */
    public static final String FEATURE_RUNTIME = "RUN";

    /**
     * Represents the "Vendor" feature.
     *
     * @since 1.0
     */
    public static final String FEATURE_VENDOR = "VEN";

    /**
     * Represents the "Version" feature.
     *
     * @since 1.0
     */
    public static final String FEATURE_VERSION = "VER";

    /*
     * A flag that gets used to check whether a license key was set more than
     * once.
     */
    private static boolean keySet;

    /*
     * Stores the license4j license object.
     */
    private static License license;

    /*
     * Standard logger instance.
     */
    private static final Logger LOGGER = Logger.getLogger(FlexGanttFX.class.getName());

    /*
     * Stores the public key needed by license4j.
     */
    private static String publicKey;

    /*
     * The system exit code that will be used if the license key validation
     * fails.
     */
    private static final int SYSTEM_EXIT_CODE = -1603;

    static {
        Properties props = new Properties();
        try {
            props.load(FlexGanttFX.class.getResourceAsStream("public_key.properties"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "unable to process public key property file", e);
        }
        publicKey = props.getProperty("public");
        LicenseManager.setPublicKey(publicKey);
    }

    /**
     * Returns the name of the licensee.
     *
     * @return the licensee's name
     * @since 1.0
     */
    public static String getLicensee() {
        if (getLicense() != null) {
            return (String) getLicense().getFeature(FEATURE_LICENSEE);
        }
        return "---";
    }

    /**
     * Returns the product type (e.g. "LITE", "STANDARD", "ENTERPRISE").
     *
     * @return the product type
     * @since 1.0
     */
    public static String getProduct() {
        if (getLicense() != null) {
            String licensee = (String) getLicense().getFeature(FEATURE_PRODUCT);
            return licensee;
        }
        return "---";
    }

    /**
     * Returns the public key used for decoding the license key.
     *
     * @return the public key used for decoding the license key
     * @since 1.0
     */
    public static String getPublicKey() {
        return publicKey;
    }

    /**
     * Returns the name of the vendor (e.g. ComponentSource, DLSC, Evget, ...).
     *
     * @return the vendor's name
     * @since 1.0
     */
    public static String getVendor() {
        if (getLicense() != null) {
            return (String) getLicense().getFeature(FEATURE_VENDOR);
        }
        return "---";
    }

    /**
     * Returns the version (e.g. "1", "2", ...). This number is the version
     * number used for licensing issues only. It represents the major version
     * number. A more detailed version number can be looked up by calling
     * {@link FlexGanttFX#getVersion()}.
     *
     * @return the product version
     * @since 1.0
     */
    public static String getLicensedVersion() {
        if (getLicense() != null) {
            return (String) getLicense().getFeature(FEATURE_VERSION);
        }
        return "---";
    }

    /**
     * Determines if the product uses a development license.
     *
     * @return true if the product uses a development license
     * @since 1.0
     */
    public static boolean isDevelopmentLicense() {
        return !isTrialLicense() && !isRuntimeLicense();
    }

    /**
     * Determines if the product uses a runtime license.
     *
     * @return true if the product uses a runtime license
     * @since 1.0
     */
    public static boolean isRuntimeLicense() {
        if (getLicense() != null) {
            String runtime = (String) getLicense().getFeature(FEATURE_RUNTIME);
            return runtime != null && runtime.equals("yes");
        }
        return false;
    }

    /**
     * Determines if the product is run as a trial.
     *
     * @return true if the product is run as a trial
     * @since 1.0
     */
    public static boolean isTrialLicense() {
        return getLicense() == null;
    }

    /**
     * Determines if the {@link #setLicenseKey(String)} method has been called.
     *
     * @return true if the license key has already been set
     * @since 1.0
     */
    public static boolean isLicenseKeySet() {
        return keySet;
    }

    /**
     * Sets the license key used for FlexGantt. The key determines whether the
     * product uses a development or a runtime license.
     *
     * @param key
     *            the license key
     * @throws IllegalStateException
     *             if the license key gets set more than once
     * @since 1.0
     */
    public static void setLicenseKey(String key) {
        if (keySet) {
            throw new IllegalStateException("licensing key can only be set once");
        }
        keySet = true;
        LOGGER.fine("found properties file");
        try {
            key = key.replace(';', '\n');
            ByteArrayInputStream stream = new ByteArrayInputStream(key.getBytes());
            license = LicenseManager.loadLicense(stream);
            if (!LicenseManager.isValid(license)) {
                System.err.println();
                System.err.println("#####################################");
                System.err.println("# Invalid FlexGanttFX license key!  #");
                System.err.println("# Exiting application...            #");
                System.err.println("#####################################");
                System.err.println();
                System.exit(SYSTEM_EXIT_CODE);
            }
            @SuppressWarnings("unchecked")
            List<String> featureNames = license.getFeatureList();
            LOGGER.fine("License Features:");
            for (String featureName : featureNames) {
                Object featureValue = license.getFeature(featureName);
                LOGGER.fine("   " + featureName + " = " + featureValue);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "unable to process FlexGanttFX properties file", e);
            System.err.println("Unable to process FlexGanttFX license key, exiting application");
            System.exit(SYSTEM_EXIT_CODE);
        }
    }

    public static License getLicense() {
        if (license != null) {
            return license;
        }

        String key = System.getProperty("flexganttfx.license");
        if (key != null) {
            setLicenseKey(key);
        }

        return license;
    }
}