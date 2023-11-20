package com.app.videosharingapp.ui.myvideos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.videosharingapp.R;
import com.app.videosharingapp.databinding.FragmentMyVideosBinding;
import com.app.videosharingapp.databinding.FragmentProfileBinding;

public class MyVideosFragment extends Fragment {

    FragmentMyVideosBinding binding;

    public MyVideosFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyVideosBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }
}