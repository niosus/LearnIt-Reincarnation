package com.learnit.learnit.types;

import com.learnit.learnit.utils.StringUtils;
import com.learnit.learnit.utils.Utils;

public class WordBundle {
    public static final String TRANS_DIVIDER = "___,___";

    private int mId;
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

    @Override
    public boolean equals(Object object) {
        if (object instanceof WordBundle) {
            WordBundle other = (WordBundle) object;
            if (this.mWord != null || other.word() != null) {
                if (!this.mWord.equals(other.word())) {
                    return false;
                }
            }
            if (this.mWord != null || other.word() != null) {
                if (!this.mWord.equals(other.word())) {
                    return false;
                }
            }
            boolean wordsEqual = (Utils.areBothNull(this.mWord, other.word()))
                    || this.mWord.equals(other.word());
            boolean transEqual = (Utils.areBothNull(this.transAsString(), other.transAsString()))
                    || this.transAsString().equals(other.transAsString());
            boolean articlesEqual = (Utils.areBothNull(this.mArticle, other.article()))
                    || this.mArticle.equals(other.article());
            boolean prefixesEqual = (Utils.areBothNull(this.mPrefix, other.prefix()))
                    || this.mPrefix.equals(other.prefix());
            return wordsEqual && transEqual && prefixesEqual && articlesEqual;
        }
        return false;
    }

}
