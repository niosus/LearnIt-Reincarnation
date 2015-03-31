package com.learnit.learnit.interfaces;

import com.learnit.learnit.types.WordBundle;

public interface IDatabaseInteractions {
    public void addWord(final WordBundle wordBundle);
    public WordBundle queryWord(final String word);
}
