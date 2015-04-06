package com.learnit.learnit.types;

import junit.framework.TestCase;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class WordBundleTest extends TestCase {

    public void testTransFromString() throws Exception {
        WordBundle testWordBundle = new WordBundle();
        String trans1 = "trans1.1, trans1.2";
        String trans2 = "trans2.1, trans2.2; trans2.3";
        String[] fullTrans = new String[] {trans1, trans2};
        String tempTransString = fullTrans[0] + WordBundle.TRANS_DIVIDER + fullTrans[1];
        testWordBundle.setTransFromString(tempTransString);
        assertThat(testWordBundle.transAsArray(), is(fullTrans));
    }

    public void testTrans() throws Exception {
        WordBundle testWordBundle = new WordBundle();
        String trans1 = "trans1.1, trans1.2";
        String trans2 = "trans2.1, trans2.2; trans2.3";
        String[] fullTrans = new String[] {trans1, trans2};
        testWordBundle.setTransFromStringArray(fullTrans);
        assertThat(testWordBundle.transAsArray(), is(fullTrans));
    }
}