package com.learnit.learnit.types;

import junit.framework.TestCase;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
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

    public void testTransNull() throws Exception {
        WordBundle testWordBundle = new WordBundle();
        assertThat(testWordBundle.transAsString() == null, is(true));
    }

    public void testEquals() throws Exception {
        WordBundle bundle = new WordBundle();
        bundle.setId(2)
                .setWord("fallen")
                .setTransFromString("fall")
                .setWeight(0.5f)
                .setArticle("blah");
        WordBundle bundle_same = new WordBundle();
        bundle_same.setId(2)
                .setWord("fallen")
                .setTransFromString("fall")
                .setWeight(0.5f)
                .setArticle("blah");
        assertThat(bundle, is(bundle_same));

        WordBundle bundle_diff = new WordBundle();
        assertThat(bundle, is(not(bundle_diff)));
        assertThat(bundle_diff, is(bundle_diff));

        // test that word type plays a difference
        bundle_diff = new WordBundle();
        bundle_diff.setId(2)
                .setWord("fallen")
                .setTransFromString("fall")
                .setWeight(0.5f)
                .setArticle("blah")
                .setWordType(WordBundle.WordType.NOUN);
        assertThat(bundle, is(not(bundle_diff)));
    }
}