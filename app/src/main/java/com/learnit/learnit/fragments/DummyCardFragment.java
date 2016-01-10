package com.learnit.learnit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.learnit.learnit.R;

import butterknife.ButterKnife;
import butterknife.Bind;

public class DummyCardFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    @Bind(R.id.textView)
    TextView textView;

    private int position;

    public static DummyCardFragment newInstance(int position) {
        DummyCardFragment f = new DummyCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card,container,false);
        ButterKnife.bind(this, rootView);
        ViewCompat.setElevation(rootView,50);
        textView.setText("CARD "+position);
        return rootView;
    }
}