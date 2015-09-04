package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.EMIService;
import com.mantralabsglobal.cashin.ui.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.FacebookFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.GetUserLoanWantedFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.ReferencesFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.TransUnionFragment;

/**
 * Created by kritika on 13/06/2015.
 */
public class SubmitAdapter extends FragmentPagerAdapter {

    private final FragmentManager fragmentManager;
    private GetUserLoanWantedFragment getUserLoanWantedFragment;

    public SubmitAdapter(FragmentManager fm) {
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
