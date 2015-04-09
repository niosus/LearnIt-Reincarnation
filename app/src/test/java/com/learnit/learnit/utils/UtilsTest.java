package com.learnit.learnit.utils;

import junit.framework.TestCase;


public class UtilsTest extends TestCase {

    public void testAreBothNull() throws Exception {
        assertTrue(Utils.areBothNull(null, null));
        String a = "test";
        assertFalse(Utils.areBothNull(a, null));
    }
}