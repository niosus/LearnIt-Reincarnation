package com.learnit.learnit.types;

public class LanguagePair {
    public static class LangNames {
        String mLanguageToLearn = null;
        String mLanguageYouKnow = null;

        public LangNames() {
            mLanguageToLearn = null;
            mLanguageYouKnow = null;
        }

        public LangNames setLangToLearn(final String langToLearn) {
            mLanguageToLearn = langToLearn;
            return this;
        }

        public LangNames setLangYouKnow(final String langYouKnow) {
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

    public static class LangTags {
        String mLanguageToLearnTag = null;
        String mLanguageYouKnowTag = null;

        public LangTags() {
            mLanguageToLearnTag = null;
            mLanguageYouKnowTag = null;
        }

        public LangTags setLangToLearnTag(final String langToLearnTag) {
            mLanguageToLearnTag = langToLearnTag;
            return this;
        }

        public LangTags setLangYouKnowTag(final String langYouKnow) {
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
