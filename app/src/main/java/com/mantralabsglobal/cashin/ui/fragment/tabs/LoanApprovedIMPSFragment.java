package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.adapter.LoanApprovedIMPSAdapter;
import com.mantralabsglobal.cashin.ui.fragment.adapter.SocialPagerAdapter;

/**
 * Created by praveen on 02/10/15.
 */
public class LoanApprovedIMPSFragment extends AbstractPager {

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
    
}
