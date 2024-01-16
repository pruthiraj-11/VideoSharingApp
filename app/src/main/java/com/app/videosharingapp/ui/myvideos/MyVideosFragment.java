package com.app.videosharingapp.ui.myvideos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.videosharingapp.databinding.FragmentMyVideosBinding;

public class MyVideosFragment extends Fragment {

    FragmentMyVideosBinding binding;

    public MyVideosFragment() {}
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyVideosBinding.inflate(inflater, container, false);

        return binding.getRoot();
//        RequestOptions requestOptions = new RequestOptions();
//        Glide.with(requireContext())
//                .load("Your URL")
//                .apply(requestOptions)
//                .thumbnail(Glide.with(requireContext()).load("Your URL"))
//                .into(binding.img_video_attachment_preview);
    }
}