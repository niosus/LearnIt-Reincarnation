package com.learnit.learnit.fragments;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.interfaces.ILearnFragmentUiEventHandler;
import com.learnit.learnit.interfaces.IRefreshable;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.types.LearnCorrectnessValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.grantland.widget.AutofitHelper;

public class LearnWordsCardFragment
        extends AbstractLearnFragment
        implements IAsyncTaskResultClient,
        IRefreshable,
        ILearnFragmentUiEventHandler {

    public static final String TAG = LearnWordsCardFragment.class.getSimpleName();

    @Bind(R.id.query_word)
    TextView mQueryWord;
    @Bind(R.id.left_bottom_button)
    Button mLeftBottomButton;
    @Bind(R.id.left_top_button)
    Button mLeftTopButton;
    @Bind(R.id.right_bottom_button)
    Button mRightBottomButton;
    @Bind(R.id.right_top_button)
    Button mRightTopButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn_words, container, false);
        ButterKnife.bind(this, rootView);

        mQueryWord.setMaxLines(3);
        mQueryWord.setSingleLine();
        AutofitHelper.create(mQueryWord);

        // sadly, I don't know how to bind to an existing view...
        mQueryWordCard = (CardView) rootView.findViewById(R.id.query_word_card);

        AutofitHelper.create(mLeftBottomButton);
        AutofitHelper.create(mLeftTopButton);
        AutofitHelper.create(mRightBottomButton);
        AutofitHelper.create(mRightTopButton);

        mButtons = new ArrayList<>();
        mButtons.add(mLeftTopButton);
        mButtons.add(mRightTopButton);
        mButtons.add(mLeftBottomButton);
        mButtons.add(mRightBottomButton);
        mButtonOnClickListener = new LearnCorrectnessValidator(this);
        for (Button button: mButtons) {
            button.setOnClickListener(mButtonOnClickListener);
            button.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public String tag() {
        return "learn_words_fragment";
    }

    @Override
    public void onPreExecute() {}

    @Override
    public void onProgressUpdate(Float progress) {}

    @Override @SuppressWarnings("unchecked")
    public <OutType> void onFinish(OutType result) {
        if (result instanceof List) {
            mWordsOnButtons = (List<WordBundle>) result;
            Random rand = new Random();
            int correctWordIndex = rand.nextInt(mWordsOnButtons.size());
            int correctBtnId = mButtons.get(correctWordIndex).getId();
            mButtonOnClickListener.setCorrectAnswerId(correctBtnId);
            if (!mWordsOnButtons.isEmpty()) {
                // set and show the query word
                updateQueryWordCaption(mWordsOnButtons.get(correctWordIndex), mQueryWord);
                circularRevealToVisibility(mQueryWordCard, View.VISIBLE);
                // set and show buttons
                updateButtonCaptions(mWordsOnButtons, mButtons);
                showButtons();
            }
        }
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void refresh() {
        fetchNewRandomWordsAsync();
    }

    @Override
    public void onAllViewsHidden() {
        // all view have been hidden, so we generate a new word to learn
        fetchNewRandomWordsAsync();
    }
}