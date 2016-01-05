package com.learnit.learnit.interfaces;

public interface IAnimationEventListener {
    void onAnimationStarted(final int id, final int targetVisibility);
    void onAnimationFinished(final int id, final int targetVisibility);
}
