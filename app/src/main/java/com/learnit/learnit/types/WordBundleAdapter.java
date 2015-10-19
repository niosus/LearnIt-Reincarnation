package com.learnit.learnit.types;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.learnit.R;

import java.util.List;

public class WordBundleAdapter extends RecyclerView.Adapter<WordBundleAdapter.ViewHolder>{

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