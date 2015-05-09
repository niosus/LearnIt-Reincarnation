package com.learnit.learnit.interfaces;

public interface IAsyncTaskResultClient {
    String tag();
    void onPreExecute();
    void onProgressUpdate(Float progress);
    <OutType> void onFinish(OutType result);
    void onCancelled();
}
