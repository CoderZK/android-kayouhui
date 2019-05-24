package com.kyh.app.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.kyh.app.R;
import com.kyh.app.adapter.ScreenSlideAdapter;

public class IntroScreenActivity  extends FragmentActivity implements  ViewPager.OnPageChangeListener{
    private ViewPager viewpager;
    private PagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_intro_screen);
        viewpager = (ViewPager) findViewById(R.id.pager);
        initAdapter();
    }

    private void initAdapter() {
        pagerAdapter = new ScreenSlideAdapter(getSupportFragmentManager());
        viewpager.setAdapter(pagerAdapter);
        viewpager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
