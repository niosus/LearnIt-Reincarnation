package com.learnit.learnit.types;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.learnit.learnit.R;
import com.learnit.learnit.fragments.MyDictCardFragment;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.fragments.AddWordsCardFragment;
import com.learnit.learnit.fragments.DummyCardFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    public static final int ADD_WORDS_ITEM = 0;
    public static final int DICT_ITEM = 1;
    public static final int LEARN_WORDS_ITEM = 2;

    private String[] TITLES = null;

    public TabsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        TITLES = new String[]{
            context.getString(R.string.add_words_frag_title),
            context.getString(R.string.dictionary_frag_title),
            context.getString(R.string.learn_words_frag_title) };
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(Constants.LOG_TAG, "asking frag at pos " + position);
        switch (position) {
            case ADD_WORDS_ITEM:
                return AddWordsCardFragment.newInstance(position);
            case DICT_ITEM:
                return MyDictCardFragment.newInstance(position);
            case LEARN_WORDS_ITEM:
                return DummyCardFragment.newInstance(position);
        }
        return DummyCardFragment.newInstance(position);
    }
}
