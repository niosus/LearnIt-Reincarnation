/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.utils.Constants;

public abstract class MySmartAsyncTask<InType, OutType>
        extends AsyncTask<InType, Float, OutType> {
    protected IAsyncTaskResultClient mAsyncTaskResultClient;
    protected Context mContext;
    protected InType mInputData;

    protected String TAG = null;

    public MySmartAsyncTask(
            Context context,
            InType data) {
        super();
        mContext = context;
        mInputData = data;
    }

    public MySmartAsyncTask<InType, OutType> setResultClient(
            IAsyncTaskResultClient resultClient) {
        mAsyncTaskResultClient = resultClient;
        return this;
    }

    public MySmartAsyncTask<InType, OutType> setContext(
            Context context) {
        mContext = context;
        return this;
    }

    // just a stub for children to implement and to avoid
    // warnings about the vararg array of generics
    public abstract void execute();

    // children need to have a distinct tag
    public abstract String tag();

    @Override
    protected void onPostExecute(OutType result) {
        super.onPostExecute(result);
        mAsyncTaskResultClient.onProgressUpdate(100f);
        mAsyncTaskResultClient.onFinish(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncTaskResultClient.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mAsyncTaskResultClient.onCancelled();
    }
}
