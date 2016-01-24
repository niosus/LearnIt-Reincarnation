package com.learnit.learnit.fragments.learn_fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.learnit.learnit.R;
import com.learnit.learnit.animators.ZoomInNoFade;
import com.learnit.learnit.animators.ZoomOutNoFade;
import com.learnit.learnit.async_tasks.GetRandomUserWordsTask;
import com.learnit.learnit.fragments.TaskSchedulerFragment;
import com.learnit.learnit.interfaces.IAnimationEventListener;
import com.learnit.learnit.interfaces.IAnswerChecker;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.interfaces.ILearnFragmentUiEventHandler;
import com.learnit.learnit.types.LearnCorrectnessValidator;
import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.AnimationUtils;
import com.learnit.learnit.utils.Constants;

import java.util.List;
import java.util.Random;

public abstract class AbstractLearnFragment extends Fragment
        implements IAsyncTaskResultClient, IAnimationEventListener, IAnswerChecker {

    protected List<Button> mButtons;
    protected List<WordBundle> mWordsOnButtons;
    protected LearnCorrectnessValidator mButtonOnClickListener;
    protected CardView mQueryWordCard;
    protected WordBundle mQueryWordBundle;

    protected TaskSchedulerFragment mTaskScheduler;
    private ILearnFragmentUiEventHandler mUiEventHandler;
    private int mAnimationDuration = 300;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        if (arguments == null) {
            Log.d(Constants.LOG_TAG, "arguments are null, nothing to get from them");
            return;
        }
        mQueryWordBundle = arguments.getParcelable(Constants.QUERY_WORD_KEY);
        if (mQueryWordBundle == null) {
            Log.e(Constants.LOG_TAG, "fragment received word that is null. Not reading extras then.");
            return;
        }
        Log.d(Constants.LOG_TAG, "trying to show word " + mQueryWordBundle.word());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTaskScheduler();
    }

    @Override
    public void onCorrectViewClicked(View correctView) {
        hideButtons(mAnimationDuration, correctView.getId());
        this.circularRevealToVisibility(mQueryWordCard, View.INVISIBLE);
    }

    @Override
    public void onWrongViewClicked(View v) {
        YoYo.with(Techniques.Shake).duration(3 * mAnimationDuration).playOn(v);
    }

    @Override
    public void onAnimationStarted(int id, int targetVisibility) {
        switch (id) {
            case R.id.query_word_card:
                if (targetVisibility == View.VISIBLE) {
                    mQueryWordCard.setVisibility(targetVisibility);
                }
                break;
        }
    }

    @Override
    public void onAnimationFinished(int id, int targetVisibility) {
        switch (id) {
            case R.id.query_word_card:
                if (targetVisibility == View.INVISIBLE) {
                    mQueryWordCard.setVisibility(targetVisibility);
                    mUiEventHandler.onAllViewsHidden();
                }
                break;
        }
    }

    public void setLearnFragmentUiEventHandler(ILearnFragmentUiEventHandler handler) {
        mUiEventHandler = handler;
    }

    protected void fetchNewRandomWordsAsync(int numOfWords, int avoidId) {
        mTaskScheduler.newTaskForClient(
                new GetRandomUserWordsTask(this.getContext(), numOfWords, avoidId), this);
    }

    protected void fetchNewRandomWordsAsync(int numOfWords) {
        mTaskScheduler.newTaskForClient(
                new GetRandomUserWordsTask(this.getContext(), numOfWords), this);
    }

    protected void circularRevealToVisibility(View view, final int visibility) {
        try {
            AnimationUtils.animateToVisibilityCircular(
                    view, visibility, mAnimationDuration, this, AnimationUtils.MotionOrigin.TOP_CENTER);
        } catch (IllegalStateException e) {
            Log.w(Constants.LOG_TAG, "trying to run animation on a detached view. Not sure what exactly causes it.");
            view.setVisibility(visibility);
        }
    }

    protected void updateButtonCaptions(final List<WordBundle> captions, List<Button> buttons) {
        Random rand = new Random();
        for (int i = 0; i < captions.size(); ++i) {
            int numOfTranslations = captions.get(i).transAsArray().length;
            String trans = captions.get(i).transAsArray()[rand.nextInt(numOfTranslations)];
            Button btn = buttons.get(i);
            btn.setText(trans);
        }
    }

    protected void updateQueryWordCaption(final WordBundle wordBundle, TextView queryWordTextView) {
        queryWordTextView.setText(wordBundle.word());
        queryWordTextView.setVisibility(View.VISIBLE);
    }

    protected void showButtons() {
        int i = 0;
        for (; i < mWordsOnButtons.size(); ++i) {
            Button btn = mButtons.get(i);
            YoYo.with(new ZoomInNoFade()).duration(mAnimationDuration).playOn(btn);
            btn.setVisibility(View.VISIBLE);
        }
        for (; i < mButtons.size(); ++i) {
            mButtons.get(i).setVisibility(View.INVISIBLE);
        }
    }

    protected void hideButtons(int animationDuration, int correctButtonId) {
        int delay;
        int duration;
        Random random = new Random();
        for (int i = 0; i < mWordsOnButtons.size(); ++i) {
            Button btn = mButtons.get(i);
            if (btn.getId() == correctButtonId) {
                delay = 200;
            } else {
                delay = random.nextInt(mButtons.size()) * 50;
            }
            duration = animationDuration - delay;
            YoYo.with(new ZoomOutNoFade()).duration(duration).delay(delay).playOn(btn);
        }
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
}
