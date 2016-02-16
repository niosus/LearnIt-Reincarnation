package com.learnit.learnit.types;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.utils.StringUtils;
import com.learnit.learnit.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordBundle implements Parcelable {
    public static final String TRANS_DIVIDER = "___,___";
    public static final String HUMAN_TRANS_DIVIDER = "; ";
    public static final float MAX_WEIGHT = 1.0f;
    public static final float DEFAULT_WEIGHT = 0.5f;
    private int mId;
    private int mWordType;
    private String mArticle;
    private String mPrefix;
    private String mWord;
    private String[] mTrans;
    private float mWeight;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mWordType);
        dest.writeString(mArticle);
        dest.writeString(mPrefix);
        dest.writeString(mWord);
        dest.writeStringArray(mTrans);
        dest.writeFloat(mWeight);
    }

    public static final Parcelable.Creator<WordBundle> CREATOR
            = new Parcelable.Creator<WordBundle>() {
        public WordBundle createFromParcel(Parcel in) {
            return new WordBundle(in);
        }

        public WordBundle[] newArray(int size) {
            return new WordBundle[size];
        }
    };

    private WordBundle(Parcel in) {
        mId = in.readInt();
        mWordType = in.readInt();
        mArticle = in.readString();
        mPrefix = in.readString();
        mWord = in.readString();
        mTrans = in.createStringArray();
        mWeight = in.readFloat();
    }


    public enum ParseStyle {
        BABYLON,
        STARDICT
    }

    public static class Constructor {
        private int mNestedId;
        private int mNestedWordType;
        private String mNestedArticle;
        private String mNestedPrefix;
        private String mNestedWord;
        private String[] mNestedTrans;
        private float mNestedWeight;
        Context mContext;

        public Constructor() {
            setDefaultValues();
        }

        public Constructor(Context context) {
            setDefaultValues();
            mContext = context;
        }

        private void setDefaultValues() {
            mNestedId = -1;
            mNestedArticle = null;
            mNestedPrefix = null;
            mNestedWord = null;
            mNestedTrans = null;
            mNestedWeight = DEFAULT_WEIGHT;
            mNestedWordType = WordType.NONE;

            mContext = null;
        }

        public WordBundle construct() {
            WordBundle tempBundle = new WordBundle();
            tempBundle.setWord(mNestedWord);
            tempBundle.setTrans(mNestedTrans);
            tempBundle.setArticle(mNestedArticle);
            tempBundle.setPrefix(mNestedPrefix);
            tempBundle.setWordType(mNestedWordType);
            tempBundle.setId(mNestedId);
            tempBundle.setWeight(mNestedWeight);
            setDefaultValues();
            return tempBundle;
        }

        public WordBundle.Constructor setWord(final String word) {
            mNestedWord = word;
            return this;
        }

        public WordBundle.Constructor setArticle(final String article) {
            mNestedArticle = article;
            return this;
        }

        public WordBundle.Constructor setPrefix(final String prefix) {
            mNestedPrefix = prefix;
            return this;
        }

        public WordBundle.Constructor setWeight(final float weight) {
            mNestedWeight = weight;
            return this;
        }

        public WordBundle.Constructor setId(final int id) {
            mNestedId = id;
            return this;
        }

        public WordBundle.Constructor setTrans(final String trans) {
            return setTrans(trans, TRANS_DIVIDER);
        }

        public WordBundle.Constructor setTrans(final String trans, final String divider) {
            if (trans == null) { return this; }
            String[] translations = trans.split(divider);
            for (int i = 0; i < translations.length; ++i) {
                translations[i] = translations[i].trim();
            }
            this.mNestedTrans = translations;
            return this;
        }

        public WordBundle.Constructor parseTrans(final String trans, final ParseStyle style) {
            switch (style) {
                case BABYLON:
                    // parse word type
                    Log.d(Constants.LOG_TAG, "parsing translation: " + trans);
                    Pattern wordTypePattern = Pattern.compile("\\(.\\)");
                    Matcher wordTypeMatcher = wordTypePattern.matcher(trans);
                    if (wordTypeMatcher.find()) {
                        String wordType = wordTypeMatcher.group();
                        mNestedWordType = StringUtils.wordTypeFromString(wordType);
                    }
                    if (wordTypeMatcher.find()) {
                        // heeey, we also have an article over here!
                        String sex = wordTypeMatcher.group();
                        mNestedArticle = StringUtils.articleFromString(mContext, sex);
                        Log.d(Constants.LOG_TAG, "nested article is: " + mNestedArticle);
                    }
                    // parse translations
                    Pattern translationsPattern = Pattern.compile("\\s\\p{L}[\\p{L}\\s,()'.]*");
                    Matcher translationsMatcher = translationsPattern.matcher(trans);
                    String tempTrans = "";
                    while (translationsMatcher.find()) {
                        tempTrans += translationsMatcher.group().trim() + WordBundle.TRANS_DIVIDER;
                    }
                    mNestedTrans = tempTrans.split(TRANS_DIVIDER);
                    break;
                case STARDICT:
                    Log.e(Constants.LOG_TAG, "not implemented yet");
                    break;
                default:
                    Log.e(Constants.LOG_TAG, "unknown parser style.");
            }
            return this;
        }

        public WordBundle.Constructor setTrans(final String[] trans) {
            mNestedTrans = trans;
            return this;
        }

        public WordBundle.Constructor setWordType(final int wordType) {
            mNestedWordType = wordType;
            return this;
        }
    }

    public WordBundle() {
        mId = -1;
        mArticle = null;
        mPrefix = null;
        mWord = null;
        mTrans = null;
        mWeight = DEFAULT_WEIGHT;
        mWordType = WordType.NONE;
    }

    public void setWord(final String word) {
        this.mWord = word;
    }

    public void setArticle(final String article) {
        this.mArticle = article;
    }

    public void setPrefix(final String prefix) {
        this.mPrefix = prefix;
    }

    public void setWeight(final float weight) {
        if (weight > MAX_WEIGHT) {
            throw new RuntimeException("weight cannot be higher than 1.0");
        }
        this.mWeight = weight;
    }

    public void setId(final int id) {
        this.mId = id;
    }

    public void setTrans(final String trans) {
        if (trans == null) { return; }
        this.mTrans = trans.split(TRANS_DIVIDER);
    }

    public void setTrans(final String[] trans) {
        this.mTrans = trans;
    }

    public void setWordType(final int wordType) {
        this.mWordType = wordType;
    }

    public String word() {
        return mWord;
    }

    public String transAsString() {
        return StringUtils.join(mTrans, TRANS_DIVIDER);
    }

    public String transAsHumanString() {
        return StringUtils.join(mTrans, HUMAN_TRANS_DIVIDER);
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
        public static final int CONJUNCTION = 5;
        public static final int NONE = 666;
    }

}
