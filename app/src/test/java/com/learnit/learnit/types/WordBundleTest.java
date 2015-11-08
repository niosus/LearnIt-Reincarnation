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
        testWordBundle.setTrans(tempTransString);
        assertThat(testWordBundle.transAsArray(), is(fullTrans));
    }

    public void testTransFromStringNull() throws Exception {
        WordBundle testWordBundle = new WordBundle();
        testWordBundle.setTrans((String) null);
        assertThat(testWordBundle.transAsString(), is(""));
    }

    public void testTransFromStringNullArray() throws Exception {
        WordBundle testWordBundle = new WordBundle();
        testWordBundle.setTrans((String[]) null);
        assertThat(testWordBundle.transAsString(), is(""));
    }

    public void testTransFromStringCustomDivider() throws Exception {
        WordBundle testWordBundle = new WordBundle();
        testWordBundle.setTrans((String[]) null);
        assertThat(testWordBundle.transAsString(), is(""));
    }

    public void testTrans() throws Exception {
        String trans = "trans2.1, trans2.2; trans2.3";
        String trans1 = "trans2.1, trans2.2";
        String trans2 = "trans2.3";
        String[] fullTrans = new String[] {trans1, trans2};
        WordBundle testWordBundle = new WordBundle.Constructor().setTrans(trans, ";").construct();
        assertThat(testWordBundle.transAsArray(), is(fullTrans));
    }

    public void testEmptyTrans() throws Exception {
        WordBundle testWordBundle = new WordBundle();
        assertThat(testWordBundle.transAsString().isEmpty(), is(true));
    }

    public void testEquals() throws Exception {
        WordBundle bundle;
        WordBundle.Constructor constructor = new WordBundle.Constructor();
        bundle = constructor.setId(2)
                .setWord("fallen")
                .setTrans("fall")
                .setWeight(0.5f)
                .setArticle("blah")
                .construct();
        WordBundle bundle_same =constructor.setId(2)
                .setWord("fallen")
                .setTrans("fall")
                .setWeight(0.5f)
                .setArticle("blah")
                .construct();
        assertThat(bundle, is(bundle_same));

        WordBundle bundle_diff = new WordBundle();
        assertThat(bundle, is(not(bundle_diff)));
        assertThat(bundle_diff, is(bundle_diff));

        // test that word type plays a difference
        bundle_diff = constructor.setId(2)
                .setWord("fallen")
                .setTrans("fall")
                .setWeight(0.5f)
                .setArticle("blah")
                .setWordType(WordBundle.WordType.NOUN)
                .construct();
        assertThat(bundle.equals(bundle_diff), is(false));
    }

    public void testParser() throws Exception {
        String trans = "(v) анулювати; зменшитися; зменшуватися; знижувати; знизити; ослабити; ослабляти; припинити; припиняти; скасовувати; скасувати";
        WordBundle bundle = new WordBundle.Constructor().parseTrans(trans, WordBundle.ParseStyle.BABYLON).construct();
        assertThat(bundle.wordType(), is(WordBundle.WordType.VERB));

        trans = "(0) консультація, порада";
        bundle = new WordBundle.Constructor().parseTrans(trans, WordBundle.ParseStyle.BABYLON).construct();
        assertThat(bundle.wordType(), is(WordBundle.WordType.NONE));
        assertThat(bundle.transAsArray()[0], is("консультація, порада"));

        trans = "(a) деспотичний; довільний; примхливий; свавільний";
        bundle = new WordBundle.Constructor().parseTrans(trans, WordBundle.ParseStyle.BABYLON).construct();
        assertThat(bundle.wordType(), is(WordBundle.WordType.ADJECTIVE));
        assertThat(bundle.transAsArray()[0], is("деспотичний"));
        assertThat(bundle.transAsArray()[1], is("довільний"));
        assertThat(bundle.transAsArray()[2], is("примхливий"));
        assertThat(bundle.transAsArray()[3], is("свавільний"));


        trans = "(d) коли; оскільки; як";
        bundle = new WordBundle.Constructor().parseTrans(trans, WordBundle.ParseStyle.BABYLON).construct();
        assertThat(bundle.wordType(), is(WordBundle.WordType.PREPOSITION));
        assertThat(bundle.transAsArray()[0], is("коли"));
        assertThat(bundle.transAsArray()[1], is("оскільки"));
        assertThat(bundle.transAsArray()[2], is("як"));
    }
}