package com.example.a9.myapplication2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 子fragment
 *
 * @author Liuhaijun
 * @time 2018/2/30  14:13
 */
public class TestViewFragment extends Fragment {

    private static final String TAG = "TestViewFragment";

    private TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_test_view, container, false);
        mTextView = inflate.findViewById(R.id.mTextView);
        mTextView.setText(getArguments().getString("str"));
        return inflate;
    }

}
