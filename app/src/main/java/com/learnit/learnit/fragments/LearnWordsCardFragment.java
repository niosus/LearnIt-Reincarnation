package com.learnit.learnit.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.learnit.learnit.R;
import com.learnit.learnit.animators.ZoomInNoFade;
import com.learnit.learnit.animators.ZoomOutNoFade;
import com.learnit.learnit.async_tasks.GetRandomUserWordsTask;
import com.learnit.learnit.interfaces.IAnimationEventListener;
import com.learnit.learnit.interfaces.IAnswerChecker;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.interfaces.IRefreshable;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.types.LearnCorrectnessValidator;
import com.learnit.learnit.utils.AnimationUtils;
import com.learnit.learnit.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.grantland.widget.AutofitHelper;

public class LearnWordsCardFragment
        extends Fragment
        implements IAsyncTaskResultClient,
        IRefreshable,
        IAnimationEventListener,
        IAnswerChecker {

    private static final String ARG_POSITION = "position";
    public static final String TAG = LearnWordsCardFragment.class.getSimpleName();

    @Bind(R.id.query_word)
    TextView mQueryWord;

    @Bind(R.id.query_word_card)
    CardView mQueryWordCard;

    @Bind(R.id.left_bottom_button)
    android.support.v7.widget.AppCompatButton mLeftBottomButton;
    @Bind(R.id.left_top_button)
    android.support.v7.widget.AppCompatButton mLeftTopButton;
    @Bind(R.id.right_bottom_button)
    android.support.v7.widget.AppCompatButton mRightBottomButton;
    @Bind(R.id.right_top_button)
    android.support.v7.widget.AppCompatButton mRightTopButton;

    private WordBundle mCurrentQueryWord;

    private TaskSchedulerFragment mTaskScheduler;

    private List<android.support.v7.widget.AppCompatButton> mButtons;
    private List<WordBundle> mWordsOnButtons;

    private LearnCorrectnessValidator mButtonOnClickListener;

    private int mAnimationDuration = 300;

    public static LearnWordsCardFragment newInstance(int position) {
        LearnWordsCardFragment f = new LearnWordsCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(Constants.LOG_TAG, "fragment is attached");
        initTaskScheduler();
    }

    private void initTaskScheduler() {
        FragmentManager fragmentManager = getFragmentManager();
        mTaskScheduler = (TaskSchedulerFragment)
                fragmentManager.findFragmentByTag(TaskSchedulerFragment.TAG);
        if (mTaskScheduler == null) {
            mTaskScheduler = new TaskSchedulerFragment();
            fragmentManager.beginTransaction()
                    .add(mTaskScheduler, TaskSchedulerFragment.TAG)
                    .commit();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_learn_words, container, false);
        ButterKnife.bind(this, rootView);
        mQueryWord.setMaxLines(3);
        mQueryWord.setSingleLine();
        AutofitHelper.create(mQueryWord);

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateWordsAsync();
    }

    private void updateWordsAsync() {
        int numOfWords = 4;
        mTaskScheduler.newTaskForClient(
                new GetRandomUserWordsTask(this.getContext(), numOfWords), this);
    }

    private void setNewWord(final List<WordBundle> words, final int correctId) {
        mCurrentQueryWord = words.get(correctId);
        if (mQueryWordCard.getVisibility() == View.VISIBLE) {
            updateWordCardVisualization(View.INVISIBLE);
        } else {
            updateWordCardVisualization(View.VISIBLE);
        }
    }

    private void updateWordCardVisualization(final int visibility) {
        try {
            AnimationUtils.animateToVisibilityCircular(mQueryWordCard, visibility, mAnimationDuration, this, AnimationUtils.MotionOrigin.TOP_CENTER);
        } catch (IllegalStateException e) {
            Log.w(Constants.LOG_TAG, "trying to run animation on a detached view. Not sure what exactly causes it.");
            this.setViewVisibilityState(mQueryWordCard.getId(), visibility);
        }
    }

    private void setViewVisibilityState(int id, int visibility) {
        switch (id) {
            case R.id.query_word_card:
                mQueryWordCard.setVisibility(visibility);
                break;
            default:
                Log.e(Constants.LOG_TAG, "unhandled switch setViewVisibilityState");
        }
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
                setNewWord(mWordsOnButtons, correctWordIndex);
            }
        }
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void refresh() {
        updateWordsAsync();
    }

    @Override
    public void onAnimationStarted(int id, int targetVisibility) {
        switch (id) {
            case R.id.query_word_card:
                if (targetVisibility == View.VISIBLE) {
                    mQueryWord.setText(mCurrentQueryWord.word());
                    this.setViewVisibilityState(id, targetVisibility);
                }
                break;
        }
    }

    @Override
    public void onAnimationFinished(int id, int targetVisibility) {
        Random rand = new Random();
        switch (id) {
            case R.id.query_word_card:
                if (targetVisibility == View.INVISIBLE) {
                    this.setViewVisibilityState(id, targetVisibility);
                    this.updateWordCardVisualization(View.VISIBLE);
                    int i = 0;
                    for (; i < mWordsOnButtons.size(); ++i) {
                        int numOfTranslations = mWordsOnButtons.get(i).transAsArray().length;
                        String trans = mWordsOnButtons.get(i).transAsArray()[rand.nextInt(numOfTranslations)];
                        Button btn = mButtons.get(i);
                        btn.setText(trans);
                        YoYo.with(new ZoomInNoFade()).duration(mAnimationDuration).playOn(btn);
                        btn.setVisibility(View.VISIBLE);
                    }
                    for (; i < mButtons.size(); ++i) {
                        mButtons.get(i).setVisibility(View.INVISIBLE);
                    }
                }
                break;
        }
    }

    @Override
    public void onCorrectViewClicked(View v) {
        int delay;
        int duration;
        int maxDuration = mAnimationDuration;
        Random random = new Random();
        for (int i = 0; i < mWordsOnButtons.size(); ++i) {
            Button btn = mButtons.get(i);
            if (btn.getId() == v.getId()) {
                delay = 200;
            } else {
                delay = random.nextInt(mButtons.size()) * 50;
            }
            duration = maxDuration - delay;
            YoYo.with(new ZoomOutNoFade()).duration(duration).delay(delay).playOn(btn);
        }
        updateWordsAsync();
    }

    @Override
    public void onWrongViewClicked(View v) {
        YoYo.with(Techniques.Shake).duration(3 * mAnimationDuration).playOn(v);
    }
}