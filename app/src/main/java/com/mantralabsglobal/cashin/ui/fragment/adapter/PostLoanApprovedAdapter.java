package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.tabs.LoanApprovedIMPSFragment;

/**
 * Created by pk on 6/27/2015.
 */
public class PostLoanApprovedAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;

    public Fragment loanApprovedIMPSPager = new LoanApprovedIMPSFragment();

    public PostLoanApprovedAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fragmentManager = fm;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return loanApprovedIMPSPager;

            /*case 4:
                return socialPager ;*/
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }
}
