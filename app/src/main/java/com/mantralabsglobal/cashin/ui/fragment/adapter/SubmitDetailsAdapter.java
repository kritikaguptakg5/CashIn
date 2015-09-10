package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.tabs.GetUserLoanWantedFragment;

/**
 * Created by kritika on 13/06/2015.
 */
public class SubmitDetailsAdapter extends FragmentPagerAdapter {

    private final FragmentManager fragmentManager;
    private GetUserLoanWantedFragment getUserLoanWantedFragment;

    public SubmitDetailsAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        getUserLoanWantedFragment = new GetUserLoanWantedFragment();
    }

    @Override
    public Fragment getItem(int position) {
        return new GetUserLoanWantedFragment();
    }

    @Override
    public int getCount() {
        return 1;
    }

}
