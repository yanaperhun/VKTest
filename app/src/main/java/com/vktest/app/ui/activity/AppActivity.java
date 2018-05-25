package com.vktest.app.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.vktest.app.R;
import com.vktest.app.ui.adapter.VPAdapter;

import butterknife.BindView;

/**
 * Created by qati on 25.05.18.
 */

public class AppActivity extends BaseActivity {

    @BindView(R.id.tabs) TabLayout tabs;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.toolBar) android.support.v7.widget.Toolbar toolBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        bindUI(this);
    }


    @Override
    public void init() {
        toolBar.setTitle(getString(R.string.app_name));
        viewPager.setAdapter(new VPAdapter(getTitles(), getFragmentManager()));
        tabs.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }


    public String[] getTitles() {
        return getResources().getStringArray(R.array.titles);

    }

}
