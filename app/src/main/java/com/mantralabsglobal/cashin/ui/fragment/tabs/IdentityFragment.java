package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.adapter.FinancePagerAdapter;
import com.mantralabsglobal.cashin.ui.fragment.adapter.IdentityPagerAdapter;

/**
 * Created by praveen on 02/10/15.
 */
public class IdentityFragment extends AbstractPager {

    IdentityPagerAdapter adapter;
    @Override
    protected IdentityPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
        if(adapter == null || fragmentManager != adapter.getFragmentManager())
            adapter = new IdentityPagerAdapter(fragmentManager);
        return adapter;
    }
    @Override
    protected void setTabLayoutMode(FragmentPagerAdapter fragmentPagerAdapter, TabLayout tabLayout)
    {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }
    
}
