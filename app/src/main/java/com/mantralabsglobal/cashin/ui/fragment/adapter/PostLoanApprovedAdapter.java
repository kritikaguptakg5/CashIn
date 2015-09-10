package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;

/**
 * Created by pk on 6/27/2015.
 */
public class PostLoanApprovedAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private Context context;

    public Fragment loanApprovedIMPSPager = new AbstractPager(){

        LoanApprovedIMPSAdapter adapter;
        @Override
        protected FragmentPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
            if(adapter == null || fragmentManager != adapter.getFragmentManager())
                adapter = new LoanApprovedIMPSAdapter(fragmentManager);
            return adapter;
        }

        @Override
        protected void setTabLayoutMode(FragmentPagerAdapter fragmentPagerAdapter, TabLayout tabLayout)
        {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }
        @Override
        protected Context getContext()
        {
            return context;
        }
    };


    public PostLoanApprovedAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fragmentManager = fm;
        this.context = context;
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
