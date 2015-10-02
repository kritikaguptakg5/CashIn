package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.tabs.FinanceFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.IdentityFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.PhotoFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.SocialFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.WorkFragment;

import butterknife.OnEditorAction;

/**
 * Created by pk on 6/27/2015.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;

    public Fragment financePager = new FinanceFragment();

    public Fragment identityPager = new IdentityFragment();

    public Fragment workPager = new WorkFragment();

   public Fragment socialPager = new SocialFragment();

    public Fragment photoPager = new PhotoFragment();

    public MainFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fragmentManager = fm;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return photoPager;
            case 1:
                return identityPager;
            case 2:
                return workPager ;
            case 3:
                return financePager ;
            case 4:
                return socialPager ;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
