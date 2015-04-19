/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.utils;

import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class UtilsTest extends TestCase {

    public void testAreBothNull() throws Exception {
        assertTrue(Utils.areBothNull(null, null));
        String a = "test";
        assertFalse(Utils.areBothNull(a, null));
    }

    public void testIsArticle() throws Exception {
        Constants.LanguageName langEn = Constants.LanguageName.ENGLISH;
        Constants.LanguageName langDe = Constants.LanguageName.GERMAN;
        assertThat(Utils.isArticle("der", langDe), is(true));
        assertThat(Utils.isArticle("die", langDe), is(true));
        assertThat(Utils.isArticle("das", langDe), is(true));

        assertThat(Utils.isArticle("blah", langDe), is(false));
        assertThat(Utils.isArticle("a", langDe), is(false));

        assertThat(Utils.isArticle("der", langEn), is(false));
        assertThat(Utils.isArticle("die", langEn), is(false));
        assertThat(Utils.isArticle("das", langEn), is(false));
    }

}