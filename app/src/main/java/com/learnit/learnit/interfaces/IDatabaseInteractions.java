package com.learnit.learnit.interfaces;

import com.learnit.learnit.types.WordBundle;
import com.learnit.learnit.utils.Constants;

import java.util.List;

public interface IDatabaseInteractions {
    Constants.AddWordReturnCode addWord(final WordBundle wordBundle);
    List<WordBundle> queryWord(final String word,
                               final Constants.QueryStyle queryStyle,
                               final Integer limit);
    List<WordBundle> queryWord(final String word,
                               final Constants.QueryStyle queryStyle);
    void deleteDatabase();
}
