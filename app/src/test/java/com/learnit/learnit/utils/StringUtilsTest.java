/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.utils;

import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class StringUtilsTest extends TestCase {

    public void testJoin() throws Exception {
        String a = "    some!@#%%   ";
        String b = "123fff!!!!....";
        String c = "<<><SADADFast1123123";
        String connector = "!<>___";
        String[] testArray = new String[] {a, b, c};
        assertThat(StringUtils.join(testArray, connector), is(a + connector + b + connector + c));
    }

    public void testJoinNull() throws Exception {
        String a = "    some!@#%%   ";
        String b = "123fff!!!!....";
        String c = "<<><SADADFast1123123";
        String connector = "!<>___";
        String[] testArray = new String[] {a, b, c};
        assertThat(StringUtils.join(null, connector), is(""));
        assertThat(StringUtils.join(testArray, null), is(""));
        assertThat(StringUtils.join(null, null), is(""));
    }

    public void testSplitTxt() throws Exception {
        String line = "air route\t(n) авіалінія; повітряна траса";
        String[] expectedResult = new String[]{"air route", "(n) авіалінія; повітряна траса"};
        String[] result = StringUtils.splitWordFromMeaning(line);
        assertThat(result[0], is(expectedResult[0]));
        assertThat(result[1], is(expectedResult[1]));

        String badLine = "##sourceLang\tEnglish";
        result = StringUtils.splitWordFromMeaning(badLine);
        assertNull(result);

        badLine = "test null\t";
        result = StringUtils.splitWordFromMeaning(badLine);
        assertNull(result);

        badLine = "test null";
        result = StringUtils.splitWordFromMeaning(badLine);
        assertNull(result);
    }
}