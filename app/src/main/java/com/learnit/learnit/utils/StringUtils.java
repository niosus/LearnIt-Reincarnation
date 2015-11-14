/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.utils;

import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.types.WordBundleAdapter;

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

    public static int wordTypeFromString(final String wordType) {
        switch (wordType) {
            case "(0)":
                return WordBundle.WordType.NONE;
            case "(n)":
               return WordBundle.WordType.NOUN;
            case "(v)":
                return  WordBundle.WordType.VERB;
            case "(a)":
                return WordBundle.WordType.ADJECTIVE;
            case "(p)":
                return WordBundle.WordType.PREPOSITION;
            case "(d)":
                return WordBundle.WordType.ADVERB;
            default:
                return WordBundle.WordType.NONE;
        }
    }
}
