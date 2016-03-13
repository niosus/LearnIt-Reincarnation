/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.interfaces;

import com.learnit.learnit.types.WordBundle;

import java.util.List;

public interface IActionModeController {
    void showSelected(List<WordBundle> selectedItems);
}
