/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.interfaces;

public interface IUiEvents {
    void wordTextChanged();

    void setViewVisibilityState(final int id, final int visibility);

    void clearWord();
}
