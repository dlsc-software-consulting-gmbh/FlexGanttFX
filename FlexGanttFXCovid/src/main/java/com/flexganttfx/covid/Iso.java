/**
 * Copyright (C) 2014 - 2021 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.covid;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Iso {

    private static Map<String, Locale> localeMap;

    static {
        String[] countries = Locale.getISOCountries();
        localeMap = new HashMap<>(countries.length);
        for (String country : countries) {
            Locale locale = new Locale("", country);
            localeMap.put(locale.getISO3Country().toUpperCase(), locale);
        }
    }

    public static String convertIso3CountryCodeToIso2CountryCode(String iso3CountryCode) {
        if (iso3CountryCode != null) {
            final Locale locale = localeMap.get(iso3CountryCode);
            if (locale != null) {
                return locale.getCountry();
            }
        }

        return "";
    }
}
