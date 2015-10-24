package com.learnit.learnit.types;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IFabEventHandler;
import com.learnit.learnit.interfaces.IFabStateController;
import com.learnit.learnit.utils.Constants;
import com.learnit.learnit.fragments.AddWordsCardFragment;
import com.learnit.learnit.fragments.CardFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    public static final int ADD_WORDS_ITEM = 0;
    public static final int DICT_ITEM = 1;
    public static final int LEARN_WORDS_ITEM = 2;

    private String[] TITLES = null;
    private IFabStateController mFabStateController;

    public TabsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        TITLES = new String[]{
            context.getString(R.string.add_words_frag_title),
            context.getString(R.string.dictionary_frag_title),
            context.getString(R.string.learn_words_frag_title) };
        if (context instanceof IFabStateController) {
            mFabStateController = (IFabStateController) context;
        }
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
        Fragment pickFragment = null;
        switch (position) {
            case ADD_WORDS_ITEM:
                pickFragment = AddWordsCardFragment.newInstance(position);
                break;
            case DICT_ITEM:
                pickFragment = CardFragment.newInstance(position);
                break;
            case LEARN_WORDS_ITEM:
                pickFragment =  CardFragment.newInstance(position);
                break;
        }
        if (pickFragment instanceof IFabEventHandler) {
            mFabStateController.addFabEventHandler((IFabEventHandler) pickFragment);
        }
        return pickFragment;
    }
}
