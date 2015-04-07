package com.learnit.learnit.interfaces;

import com.learnit.learnit.types.DbHelper;
import com.learnit.learnit.types.WordBundle;

import java.util.List;

public interface IDatabaseInteractions {
    public DbHelper.AddWordStatus addWord(final WordBundle wordBundle);
    public List<WordBundle> queryWord(final String word);
}
