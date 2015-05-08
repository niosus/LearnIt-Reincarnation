/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.interfaces;

import com.learnit.learnit.async_tasks.MySmartAsyncTask;

public interface IWorkerJobInput {
    void newTaskForClient(MySmartAsyncTask task, IAsyncTaskResultClient client);
    <OutType> void onTaskFinished(final String taskTag, OutType result);
}
