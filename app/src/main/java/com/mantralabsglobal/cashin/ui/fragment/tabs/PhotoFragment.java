package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.support.v4.app.FragmentManager;

import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.adapter.SocialPagerAdapter;
import com.mantralabsglobal.cashin.ui.fragment.adapter.YourPhotoPagerAdapter;

/**
 * Created by praveen on 02/10/15.
 */
public class PhotoFragment extends AbstractPager {

    YourPhotoPagerAdapter adapter;
    @Override
    protected YourPhotoPagerAdapter getPagerAdapter(FragmentManager fragmentManager) {
        if(adapter == null || fragmentManager != adapter.getFragmentManager())
            adapter = new YourPhotoPagerAdapter(fragmentManager);
        return adapter;
    }
    
}
