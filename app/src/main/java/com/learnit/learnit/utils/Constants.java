package com.learnit.learnit.utils;

import java.util.HashMap;
import java.util.HashSet;

public class Constants {
    enum LanguageName {
        GERMAN, ENGLISH
    }

    public static final String LOG_TAG = "my_logs";

    public static final HashSet<String> GERMAN_ARTICLES = new HashSet<String>() {{
        add("der"); add("die"); add("das");
    }};

    public static final HashMap<LanguageName, HashSet<String>> ARTICLES
            = new HashMap<LanguageName, HashSet<String> >(){{
        put(LanguageName.GERMAN, GERMAN_ARTICLES);
    }};
}
