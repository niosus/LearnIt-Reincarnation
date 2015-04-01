package com.learnit.learnit.utils;

public class StringUtils {
    public static String join(final String[] array, final String divider) {
        StringBuilder builder = new StringBuilder();
        for (final String s: array) {
            builder.append(builder.length()==0?s:divider + s);
        }
        return builder.toString();
    }
}
