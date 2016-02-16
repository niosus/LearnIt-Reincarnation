/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.utils;

import com.learnit.learnit.types.LanguagePair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Constants {
    public static final int UNDEFINED_INDEX = -666;
    public static final String LOG_TAG = "learn_it_logs";
    public static final String QUERY_WORD_KEY = "query_word";
    public static final String TRANS_DIRECTION_KEY = "trans_direction";

    public static final String GERMAN_TAG = "de";
    public static final String ENGLISH_TAG = "en";

    public static final Map<String, String> GERMAN_ARTICLES = new HashMap<String, String>() {{
        put("(n)", "das");
        put("(f)", "die");
        put("(m)", "der");
    }};

    public static final Map<String, String> ENGLISH_ARTICLES = new HashMap<String, String>() {{
        put("(n)", "the");
        put("(f)", "the");
        put("(m)", "the");
    }};

    public static final Map<String, Map<String, String>> ARTICLES
            = new HashMap<String, Map<String, String>>() {{
        put(GERMAN_TAG, GERMAN_ARTICLES);
        put(ENGLISH_TAG, ENGLISH_ARTICLES);
    }};

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
