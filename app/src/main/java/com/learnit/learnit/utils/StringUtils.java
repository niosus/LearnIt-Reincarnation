/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.utils;

public class StringUtils {
    public static String join(final String[] array, final String divider) {
        if (array == null || divider == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (final String s: array) {
            builder.append(builder.length()==0?s:divider + s);
        }
        return builder.toString();
    }

    public static String[] splitWordFromMeaning(final String line) {
        // this is a very specialized method. It only splits lines of template:
        // <word>\t<list_of_meanings>
        String[] res = line.split("\t", 2);
        if (res.length != 2) {
            return null;
        }
        if (res[0].isEmpty() || res[1].isEmpty()) {
            return null;
        }
        if (res[0].startsWith("##")) {
            return null;
        }
        return res;
    }
}
