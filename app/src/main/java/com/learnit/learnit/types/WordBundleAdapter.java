package com.learnit.learnit.types;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.utils.Constants;

import java.util.List;

public class WordBundleAdapter
        extends RecyclerView.Adapter<WordBundleAdapter.ViewHolder>
        implements IAsyncTaskResultClient {

    private static String TAG = "word_bundle_adapter";
    private List<WordBundle> mWordBundles;
    private int mRowLayout;

    public WordBundleAdapter(List<WordBundle> mWordBundles, int mRowLayout, Context context) {
        this.mWordBundles = mWordBundles;
        this.mRowLayout = mRowLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        WordBundle wordBundle = mWordBundles.get(i);
        viewHolder.mWordText.setText(wordBundle.word());
        viewHolder.mTransText.setText(wordBundle.transAsString());
    }

    @Override
    public int getItemCount() {
        return mWordBundles == null ? 0 : mWordBundles.size();
    }

    @Override
    public String tag() {
        return TAG;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(Float progress) {

    }

    @Override
    public <OutType> void onFinish(OutType result) {
        Log.d(Constants.LOG_TAG, "get help words task finished " + result);
        if (result == null) {
            mWordBundles = null;
        } else if (result instanceof List) {
            mWordBundles = (List<WordBundle>) result;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onCancelled() {
        Log.d(Constants.LOG_TAG, "get help words task canceled");
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mWordText;
        public TextView mTransText;

        public ViewHolder(View itemView) {
            super(itemView);
            mWordText = (TextView) itemView.findViewById(R.id.word_row);
            mTransText = (TextView) itemView.findViewById(R.id.trans_row);
        }

    }
}