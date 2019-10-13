package com.salil.shawrtn.util;


import org.apache.commons.validator.routines.UrlValidator;

public class UrlUtility {

    private static UrlValidator urlValidator = new UrlValidator();

    public static boolean isUrlValid(String url) {
        return urlValidator.isValid(url);
    }
}
