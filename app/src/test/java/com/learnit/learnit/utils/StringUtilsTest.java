package com.learnit.learnit.utils;

import junit.framework.TestCase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.learnit.learnit.utils.Constants.LanguageName;

public class StringUtilsTest extends TestCase {

    public void testJoin() throws Exception {
        String a = "    some!@#%%   ";
        String b = "123fff!!!!....";
        String c = "<<><SADADFast1123123";
        String connector = "!<>___";
        String[] testArray = new String[] {a, b, c};
        assertThat(StringUtils.join(testArray, connector), is(a + connector + b + connector + c));
    }

    public void testIsArticle() throws Exception {
        LanguageName langEn = LanguageName.ENGLISH;
        LanguageName langDe = LanguageName.GERMAN;
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