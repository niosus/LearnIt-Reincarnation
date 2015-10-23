package com.learnit.learnit.types;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.learnit.learnit.R;
import com.learnit.learnit.interfaces.IAsyncTaskResultClient;
import com.learnit.learnit.utils.Constants;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WordBundleAdapter
        extends RecyclerView.Adapter<WordBundleAdapter.WordBundleViewHolder>
        implements IAsyncTaskResultClient {

    private static String TAG = "word_bundle_adapter";
    private List<WordBundle> mWordBundles;
    private int mRowLayout;
    private SparseBooleanArray mSelectedItems;

    private void refreshSelectedMarkers() {
        mSelectedItems = new SparseBooleanArray((mWordBundles == null)? 0: mWordBundles.size());
    }

    public WordBundleAdapter(List<WordBundle> wordBundles, int rowLayout) {
        mWordBundles = wordBundles;
        mRowLayout = rowLayout;
        refreshSelectedMarkers();
    }

    @Override
    public WordBundleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new WordBundleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(WordBundleViewHolder wordBundleViewHolder, int position) {
        WordBundle wordBundle = mWordBundles.get(position);
        wordBundleViewHolder.setWordBundle(wordBundle);
        wordBundleViewHolder.mLayout.setSelected(mSelectedItems.get(position, false));
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
        if (result == null) {
            mWordBundles = null;
        } else if (result instanceof List) {
            mWordBundles = (List<WordBundle>) result;
        }
        notifyDataSetChanged();
        refreshSelectedMarkers();
    }

    @Override
    public void onCancelled() {
        Log.d(Constants.LOG_TAG, "get help words task canceled");
    }


    public class WordBundleViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @Bind(R.id.word_row) TextView mWordText;
        @Bind(R.id.trans_row) TextView mTransText;
        @Bind(R.id.word_bundle_layout) RelativeLayout mLayout;
        private WordBundle mWordBundle;

        public WordBundleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        public void setWordBundle(final WordBundle wordBundle) {
            mWordBundle = wordBundle;
            updateViewFromWordBundle(mWordBundle);
        }

        private void updateViewFromWordBundle(final WordBundle wordBundle) {
            mWordText.setText(wordBundle.word());
            mTransText.setText(wordBundle.transAsString());
        }

        @Override
        public void onClick(View v) {
            if (mSelectedItems.get(getAdapterPosition(), false)) {
                mSelectedItems.delete(getAdapterPosition());
                mLayout.setSelected(false);
            }
            else {
                mSelectedItems.put(getAdapterPosition(), true);
                mLayout.setSelected(true);
            }
            Log.d(Constants.LOG_TAG, "item clicked. Word is: " + mWordBundle.word());
        }
    }
}