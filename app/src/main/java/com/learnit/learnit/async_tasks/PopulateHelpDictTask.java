package com.learnit.learnit.async_tasks;

import android.content.Context;
import android.util.Log;

import com.learnit.learnit.interfaces.IAsyncTaskEvents;
import com.learnit.learnit.utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PopulateHelpDictTask extends MySmartAsyncTask<String, Integer> {


    public PopulateHelpDictTask(Context context,
                                IAsyncTaskEvents<Integer> asyncEventHandler) {
        super(context, asyncEventHandler);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        // we expect exactly one string - name of the input file
        if (strings.length != 1) {
            return null;
        }
        String filePath = strings[0];
        File file = new File(filePath);
        long fullLength = file.length();
        long currentLength = 0;
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            String line;

            while ((line = br.readLine()) != null) {
                currentLength += line.getBytes().length;
                Float percent = 100f * ((float) currentLength / (float) fullLength);
                mAsyncEventHandler.onProgressUpdate(percent);
//                System.out.println(String.format("read %s percent", percent));
            }
            br.close();
        } catch (IOException e) {
            Log.e(Constants.LOG_TAG, "Error: cannot find dictionary file");
        }
        return 0;
    }
}
