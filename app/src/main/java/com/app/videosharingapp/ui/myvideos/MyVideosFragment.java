package com.app.videosharingapp.ui.myvideos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.videosharingapp.R;

public class MyVideosFragment extends Fragment {

    public MyVideosFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_videos, container, false);
    }
}