package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.adapter.IdentityPagerAdapter;
import com.mantralabsglobal.cashin.ui.fragment.adapter.WorkPagerAdapter;

/**
 * Created by praveen on 02/10/15.
 */
public class WorkFragment extends AbstractPager {

    WorkPagerAdapter adapter;
    @Override
    protected WorkPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
        if(adapter == null || fragmentManager != adapter.getFragmentManager())
            adapter = new WorkPagerAdapter(fragmentManager);
        return adapter;
    }
    
}
