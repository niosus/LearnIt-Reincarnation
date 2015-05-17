/*
 * Copyright (c) 2015.
 * This code is written by Igor Bogoslavskyi. If you experience any issues with
 * it please contact me via email: igor.bogoslavskyi@gmail.com
 */

package com.learnit.learnit.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.learnit.learnit.async_tasks.MySmartAsyncTask;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.interfaces.IWorkerJobInput;
import com.learnit.learnit.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class TaskSchedulerFragment
        extends Fragment
        implements IWorkerJobInput {
    public static final String TAG = "task_scheduler";
    private Map<String, IAsyncTaskResultClient> mClients = null;
    private Map<String, MySmartAsyncTask> mTasks = null;
    private Map<String, String > mTaskClientBinder= null;

    public void registerClient(IAsyncTaskResultClient client) {
        // no check needed here as the old value will just be overwritten
        mClients.put(client.tag(), client);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mClients = new HashMap<>();
        mTasks = new HashMap<>();
        mTaskClientBinder = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // this fragment has no gui - therefore "headless"
        return null;
    }

    @Override
    public void newTaskForClient(MySmartAsyncTask task, IAsyncTaskResultClient client) {
        // kill the task pending for current fragment
        IAsyncTaskResultClient currentClient = mClients.get(client.tag());
        if (currentClient != null) {
            // this client is already registered
            MySmartAsyncTask currentTask = mTasks.get(client.tag());
            if (currentTask != null) {
                // there is a task that is doing something, cancel it
                // this generally takes some time. Be sure to test everything
                currentTask.cancel(true);
                // set the current task for this client to null
                mTasks.put(client.tag(), null);
            }
        } else {
            mClients.put(client.tag(), client);
        }

        // register new task
        mTasks.put(client.tag(), task);
        // bind its name to the client's name
        mTaskClientBinder.put(task.tag(), client.tag());

        // start executing the task
        task.setResulClient(client);
        task.execute();
    }

    @Override
    public <OutType> void onTaskFinished(String taskTag, OutType result) {
        if (!mTaskClientBinder.containsKey(taskTag)) {
            Log.e(Constants.LOG_TAG, "Received task for unknown client.");
            return;
        }
        String clientTag = mTaskClientBinder.get(taskTag);
        if (clientTag == null) {
            Log.e(Constants.LOG_TAG, "Something wrong with the client tag");
            return;
        }
        if (!mClients.containsKey(clientTag)) {
            Log.e(Constants.LOG_TAG, String.format("No client for tag '%s'", clientTag));
            return;
        }
        IAsyncTaskResultClient client = mClients.get(clientTag);
        if (client == null) {
            Log.e(Constants.LOG_TAG, String.format("Client for tag '%s' is null", clientTag));
            return;
        }
        // set the task connected to this client to null
        mTasks.put(clientTag, null);
        // if we are here - everything is ok
        client.onFinish(result);
    }
}
