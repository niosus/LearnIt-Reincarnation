package com.learnit.learnit.types;

import com.learnit.learnit.utils.StringUtils;
import com.learnit.learnit.utils.Utils;

public class WordBundle {
    public static final String TRANS_DIVIDER = "___,___";
    private int mId;
    private int mWordType;
    private String mArticle;
    private String mPrefix;
    private String mWord;
    private String[] mTrans;
    private float mWeight;

    public WordBundle() {
        mId = -1;
        mArticle = null;
        mPrefix = null;
        mWord = null;
        mTrans = null;
        mWeight = -1;
        mWordType = WordType.NONE;
    }

    public WordBundle setWord(final String word) {
        this.mWord = word;
        return this;
    }

    public WordBundle setArticle(final String article) {
        this.mArticle = article;
        return this;
    }

    public WordBundle setPrefix(final String prefix) {
        this.mPrefix = prefix;
        return this;
    }

    public WordBundle setWeight(final float weight) {
        this.mWeight = weight;
        return this;
    }

    public WordBundle setId(final int id) {
        this.mId = id;
        return this;
    }

    public WordBundle setTransFromString(final String trans) {
        this.mTrans = trans.split(TRANS_DIVIDER);
        return this;
    }

    public WordBundle setTransFromStringArray(final String[] trans) {
        this.mTrans = trans;
        return this;
    }

    public WordBundle setWordType(final int wordType) {
        this.mWordType = wordType;
        return this;
    }

    public String word() {
        return mWord;
    }

    public String transAsString() {
        return StringUtils.join(mTrans, TRANS_DIVIDER);
    }

    public String[] transAsArray() {
        return mTrans;
    }

    public String prefix() {
        return mPrefix;
    }

    public String article() {
        return mArticle;
    }

    public float weight() {
        return mWeight;
    }

    public int id() {
        return mId;
    }

    public int wordType() {
        return mWordType;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof WordBundle) {
            WordBundle other = (WordBundle) object;
            boolean wordsEqual = (Utils.areBothNull(this.mWord, other.word()))
                    || this.mWord.equals(other.word());
            boolean transEqual = (Utils.areBothNull(this.transAsString(), other.transAsString()))
                    || this.transAsString().equals(other.transAsString());
            boolean articlesEqual = (Utils.areBothNull(this.mArticle, other.article()))
                    || this.mArticle.equals(other.article());
            boolean prefixesEqual = (Utils.areBothNull(this.mPrefix, other.prefix()))
                    || this.mPrefix.equals(other.prefix());
            boolean wordTypesEqual = this.mWordType == other.wordType();
            return wordsEqual && transEqual && prefixesEqual && articlesEqual && wordTypesEqual;
        }
        return false;
    }

    public final class WordType {
        public static final int NOUN = 0;
        public static final int VERB = 1;
        public static final int ADJECTIVE = 2;
        public static final int ADVERB = 3;
        public static final int PREPOSITION = 4;
        public static final int NONE = 666;
    }

}
