package com.learnit.learnit.utils;

public class StringUtils {
    public static String join(final String[] array, final String divider) {
        String result = "";
        for (final String s: array) {
            result+=s + divider;
        }
        return result;
    }
}
