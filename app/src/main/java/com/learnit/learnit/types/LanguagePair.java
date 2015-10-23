package com.learnit.learnit.types;

public class LanguagePair {
    public static class Names {
        String mLanguageToLearn = null;
        String mLanguageYouKnow = null;

        public Names() {
            mLanguageToLearn = null;
            mLanguageYouKnow = null;
        }

        public Names setLangToLearn(final String langToLearn) {
            mLanguageToLearn = langToLearn;
            return this;
        }

        public Names setLangYouKnow(final String langYouKnow) {
            mLanguageYouKnow = langYouKnow;
            return this;
        }

        public String langToLearn() {
            return mLanguageToLearn;
        }

        public String langYouKnow() {
            return mLanguageYouKnow;
        }
    }

    public static class Tags {
        String mLanguageToLearnTag = null;
        String mLanguageYouKnowTag = null;

        public Tags() {
            mLanguageToLearnTag = null;
            mLanguageYouKnowTag = null;
        }

        public Tags setLangToLearnTag(final String langToLearnTag) {
            mLanguageToLearnTag = langToLearnTag;
            return this;
        }

        public Tags setLangYouKnowTag(final String langYouKnow) {
            mLanguageYouKnowTag = langYouKnow;
            return this;
        }

        public String langToLearnTag() {
            return mLanguageToLearnTag;
        }

        public String langYouKnowTag() {
            return mLanguageYouKnowTag;
        }
    }
}
