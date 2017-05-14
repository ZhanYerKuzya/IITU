package com.nmn.app.iitu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;


public class ScheduleFragment extends Fragment {

    private View scheduleFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        scheduleFragment = inflater.inflate(R.layout.schedule, container, false);

        WebView webView = (WebView) scheduleFragment.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://schedule.iitu.kz/#/");

        return scheduleFragment;
    }
}
