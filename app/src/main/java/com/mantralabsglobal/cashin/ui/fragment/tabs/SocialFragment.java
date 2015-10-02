package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.support.v4.app.FragmentManager;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.adapter.SocialPagerAdapter;
import com.mantralabsglobal.cashin.ui.fragment.adapter.WorkPagerAdapter;

/**
 * Created by praveen on 02/10/15.
 */
public class SocialFragment extends AbstractPager {

    SocialPagerAdapter adapter;
    @Override
    protected SocialPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
        if(adapter == null || fragmentManager != adapter.getFragmentManager())
            adapter = new SocialPagerAdapter(fragmentManager);
        return adapter;
    }
    
}
