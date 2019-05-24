package com.kyh.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kyh.app.R;
import com.kyh.app.fragment.IntroScreenFragment;

public class ScreenSlideAdapter extends FragmentStatePagerAdapter {
    static final int TOTAL_PAGES = 1;
    public ScreenSlideAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        IntroScreenFragment introScreenFragment = null;
        switch(position) {
            case 0:
                introScreenFragment = new IntroScreenFragment().newInstance(R.layout.fragment_screen1);
                break;
//				case 1:
//					introScreenFragment = new IntroScreenFragment().newInstance(R.layout.fragment_screen3);
//					break;
//				case 2:
//					introScreenFragment = new IntroScreenFragment().newInstance(R.layout.fragment_screen3);
//					break;
//				case 3:
//					introScreenFragment = new IntroScreenFragment().newInstance(R.layout.fragment_screen4);
//					break;
        }
        return introScreenFragment;
    }

    @Override
    public int getCount() {
        return TOTAL_PAGES;
    }
}
