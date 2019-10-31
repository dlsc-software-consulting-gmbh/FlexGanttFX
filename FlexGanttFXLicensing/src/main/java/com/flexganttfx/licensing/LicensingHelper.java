/**
 * Copyright (C) 2014 - 2019 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.licensing;

import com.flexganttfx.core.FlexGanttFX;
import com.smardec.license4j.License;
import com.smardec.license4j.LicenseManager;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class LicensingHelper {

    public static void createVendorLicenseKeys(Version v) {
        createLicenses("ComponentSource", "CO_FLEXFX", false, v);
        createLicenses("ComponentSource", "CO_FLEXFX", true, v);
    }

    public static void initPrivateKey(Version version) throws IOException {
        Properties props = new Properties();
        props.load(LicensingHelper.class.getResourceAsStream(
                "private_key_version" + version.getText() + ".properties"));
        LicenseManager.setPrivateKey(props.getProperty("private"));
    }

    /**
     * Creates license from scratch.
     *
     * @param vendor
     *            the vendor who sold the license
     * @param customerPrefix
     *            a prefix for each license
     * @param runtime
     *            a flag indicating whether the license is a runtime license or
     *            not
     * @param version
     *            the framework version
     */
    public static void createLicenses(String vendor, String customerPrefix,
            boolean runtime, Version version) {
        File file = null;
        if (runtime) {
            file = new File("keys_runtime_" + vendor + "_version_"
                    + version.getText() + ".txt");
        } else {
            file = new File("keys_development_" + vendor + "_version_"
                    + version.getText() + ".txt");
        }
        try {
            initPrivateKey(version);
            FileWriter fileWriter = new FileWriter(file);
            for (int i = 0; i < 999; i++) {
                String license = createSingleLicense(customerPrefix + "_" + i,
                        vendor, runtime, "STANDARD", version.getText(), 0);
                fileWriter.append(license);
                fileWriter.append(System.getProperty("line.separator"));
            }
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String createSingleLicense(String licensee, String vendor,
            boolean runtime, String version, String product, int count)
                    throws IllegalArgumentException, GeneralSecurityException,
                    IOException {

        License license = new License();
        license.addFeature(FlexGanttFX.FEATURE_LICENSEE, licensee);
        license.addFeature(FlexGanttFX.FEATURE_VENDOR, vendor);
        license.addFeature(FlexGanttFX.FEATURE_VERSION, version);
        license.addFeature(FlexGanttFX.FEATURE_PRODUCT, product);
        if (runtime) {
            license.addFeature(FlexGanttFX.FEATURE_RUNTIME, "yes");
        } else {
            license.addFeature(FlexGanttFX.FEATURE_RUNTIME, "no");
        }
        license.addFeature("CTR", Integer.toString(count));
        LicenseManager.setSerializeStrings(false);
        String fileName = licensee + ".lic";
        LicenseManager.saveLicense(license, fileName);
        File tmpFile = new File(fileName);
        FileReader reader = new FileReader(tmpFile);
        BufferedReader buffer = new BufferedReader(reader);
        String line = buffer.readLine();
        StringBuffer sb = new StringBuffer();
        while (line != null) {
            sb.append(line);
            line = buffer.readLine();
            if (line != null) {
                sb.append(";");
            }
        }
        buffer.close();
        tmpFile.delete();
        return sb.toString();
    }

    public static void main(String[] args) {
        createVendorLicenseKeys(Version.VERSION_11);
    }
}
