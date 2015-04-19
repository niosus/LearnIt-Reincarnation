package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.learnit.learnit.interfaces.IAsyncTaskEvents;

public abstract class MySmartAsyncTask<InType, OutType> extends AsyncTask<InType, Float, OutType> {
    protected IAsyncTaskEvents<OutType> mAsyncEventHandler;
    protected Context mContext;

    public MySmartAsyncTask(
            Context context,
            IAsyncTaskEvents<OutType> asyncEventHandler) {
        super();
        mContext = context;
        mAsyncEventHandler = asyncEventHandler;
    }

    public MySmartAsyncTask<InType, OutType> setAsyncEventHandler(
            IAsyncTaskEvents<OutType> asyncEventHandler) {
        mAsyncEventHandler = asyncEventHandler;
        return this;
    }

    public MySmartAsyncTask<InType, OutType> setContext(
            Context context) {
        mContext = context;
        return this;
    }

    @Override
    protected void onPostExecute(OutType result) {
        super.onPostExecute(result);
        mAsyncEventHandler.onFinish(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAsyncEventHandler.onPreExecute();
    }
}
