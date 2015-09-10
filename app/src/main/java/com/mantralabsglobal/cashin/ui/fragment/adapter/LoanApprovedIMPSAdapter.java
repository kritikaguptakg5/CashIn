package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mantralabsglobal.cashin.ui.fragment.tabs.BankDetailFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.BlankFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.EMIPaymentFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.GetUserLoanWantedFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.HistoryFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.IncomeFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.LoanDetailsFragment;
import com.mantralabsglobal.cashin.ui.fragment.tabs.TransactionsFragment;

/**
 * Created by kritika on 13/06/2015.
 */
public class LoanApprovedIMPSAdapter extends FragmentPagerAdapter {

    private FragmentManager fragmentManager;
    private String tabtitles[] = new String[] {"Loan Details",  "Transactions", "EMI & Payment"};

    private LoanDetailsFragment loanDetailsFragment;

    private TransactionsFragment transactionsFragment;

    //private EMIFragment emiFragment;

    private EMIPaymentFragment emiPaymentFragment;

    public LoanApprovedIMPSAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
        loanDetailsFragment = new LoanDetailsFragment();
        transactionsFragment = new TransactionsFragment();
        emiPaymentFragment = new EMIPaymentFragment();
        // emiFragment = new EMIFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return loanDetailsFragment;
            case 1:
                return transactionsFragment ;
            case 2:
                return new BlankFragment() ;
            // Open FragmentTab4.java
        }
        return new BlankFragment();
    }

    @Override
    public int getCount() {
        return tabtitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }
}
