/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.utils;

import java.util.HashMap;
import java.util.HashSet;

public class Constants {
    public static final int UNDEFINED_INDEX = -666;
    public static final String LOG_TAG = "learn_it_logs";
    public static final HashSet<String> GERMAN_ARTICLES = new HashSet<String>() {{
        add("der"); add("die"); add("das");
    }};
    public static final HashMap<LanguageName, HashSet<String>> ARTICLES
            = new HashMap<LanguageName, HashSet<String>>() {{
        put(LanguageName.GERMAN, GERMAN_ARTICLES);
    }};

    enum LanguageName {
        GERMAN, ENGLISH
    }

    public enum AddWordReturnCode {
        SUCCESS,
        WORD_UPDATED,
        WORD_EXISTS,
        FAILURE
    }

    public enum DeleteWordReturnCode {
        SUCCESS,
        FAILURE
    }

    public enum QueryStyle {
        EXACT,
        APPROXIMATE_ENDING,
        APPROXIMATE_ALL,
        RANDOM
    }
}
