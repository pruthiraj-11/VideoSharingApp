package com.app.videosharingapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.videosharingapp.ui.likedvideos.LikedVideosFragment;
import com.app.videosharingapp.ui.myvideos.MyVideosFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0)
            fragment = new MyVideosFragment();
        else if (position == 1)
            fragment = new LikedVideosFragment();

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
            title = "My Videos";
        else if (position == 1)
            title = "Liked";

        return title;
    }
}
