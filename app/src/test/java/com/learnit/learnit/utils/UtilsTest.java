/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.utils;

import android.content.Context;

import com.learnit.learnit.BuildConfig;
import com.learnit.learnit.R;
import com.pixplicity.easyprefs.library.Prefs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21, constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class UtilsTest {

    @Test
    public void testAreBothNull() throws Exception {
        assertThat(Utils.areBothNull(null, null), is(true));
        String a = "test";
        assertThat(Utils.areBothNull(a, null), is(false));
    }

    @Test
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

    @Test
    public void testLanguagesChanged() throws Exception {
        Context context = RuntimeEnvironment.application;
        Prefs.putInt(context.getResources().getString(R.string.key_language_to_learn), 1);
        Prefs.putInt(context.getResources().getString(R.string.key_language_you_know), 2);
        assertThat(Utils.languagesHaveChanged(context), is(true));
        assertThat(Utils.languagesHaveChanged(context), is(false));
        Prefs.putInt(context.getResources().getString(R.string.key_language_you_know), 3);
        assertThat(Utils.languagesHaveChanged(context), is(true));
        assertThat(Utils.languagesHaveChanged(context), is(false));
        Prefs.putInt(context.getResources().getString(R.string.key_language_to_learn), 6);
        assertThat(Utils.languagesHaveChanged(context), is(true));
        assertThat(Utils.languagesHaveChanged(context), is(false));

    }

    @Test
    public void testAutoHandling() throws Exception {
        Context context = RuntimeEnvironment.application;
        int englishLangTag = 1;
        int someTagIndex = 3;
        int autoTagIndex = 9;
        assertThat(Utils.updateLangIndexIfNeeded(context, someTagIndex), is(someTagIndex));
        assertThat(Utils.updateLangIndexIfNeeded(context, autoTagIndex), is(englishLangTag));
    }
}