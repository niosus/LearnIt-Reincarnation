package com.learnit.learnit.interfaces;

import com.learnit.learnit.types.DbHelper;
import com.learnit.learnit.types.WordBundle;

import java.util.List;

public interface IDatabaseInteractions {
    DbHelper.AddWordReturnCode addWord(final WordBundle wordBundle);
    List<WordBundle> queryWord(final String word);
    void deleteDatabase();
}
