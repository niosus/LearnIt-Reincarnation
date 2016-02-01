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
    public static final String QUERY_WORD_KEY = "query_word";
    public static final String TRANS_DIRECTION_KEY = "trans_direction";
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

    public enum LearnType {
        TRANSLATIONS,
        ARTICLES,
        MIXED
    }

    public enum DirectionOfTranslation {
        NEW_TO_KNOWN,
        KNOWN_TO_NEW,
        MIXED
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
        APPROXIMATE_WORD_ENDING,
        APPROXIMATE_WORD,
        APPROXIMATE_WORD_TRANS,
    }
}
