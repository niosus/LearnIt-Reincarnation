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
}