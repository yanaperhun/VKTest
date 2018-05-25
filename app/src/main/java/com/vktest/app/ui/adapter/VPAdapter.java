package com.vktest.app.ui.adapter;
//use v13 FragmentPagerAdapter without support v4 fragments

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.vktest.app.ui.fragment.SmsListFragment;
import com.vktest.app.ui.fragment.VkFragment;

public class VPAdapter extends FragmentPagerAdapter {

    private final String[] titles;

    public VPAdapter(String[] titles, FragmentManager childFragmentManager) {
        super(childFragmentManager);
        this.titles = titles;
    }


    @Override
    public Fragment getItem(int position) {
        return  position == 0 ? new VkFragment() : new SmsListFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return 2;
    }

}