package com.learnit.learnit.interfaces;

public interface IAsyncTaskEvents<OutType> {
    public void onProgressUpdate(Float progress);

    public void onFinish(OutType result);

    public void onPreExecute();
}
