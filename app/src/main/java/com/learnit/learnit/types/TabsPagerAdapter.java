package com.learnit.learnit.types;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.learnit.learnit.R;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.fragments.AddWordsCardFragment;
import com.learnit.learnit.fragments.CardFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {
    private static final int ADD_WORDS_ITEM = 0;
    private static final int DICT_ITEM = 1;
    private static final int LEARN_WORDS_ITEM = 2;

    private String[] TITLES = null;
    private Context mContext;

    public TabsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
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
                return CardFragment.newInstance(position);
            case LEARN_WORDS_ITEM:
                return CardFragment.newInstance(position);
        }
        return CardFragment.newInstance(position);
    }

    @Override
    public View getCustomTabView(ViewGroup viewGroup, int i) {
        if (mContext == null) {
            Log.e(Constants.LOG_TAG, "getCustomTabView --> context == null");
            return null;
        }
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(R.layout.custom_tab, null);
    }
}
