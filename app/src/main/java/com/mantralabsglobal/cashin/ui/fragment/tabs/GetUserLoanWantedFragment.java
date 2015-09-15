package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.GetUserLoanService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;

import butterknife.InjectView;
import retrofit.Callback;
//import eu.livotov.zxscan.ScannerView;

/**
 * Created by kritika on 31/08/2015.
 */
public class GetUserLoanWantedFragment extends BaseBindableFragment<GetUserLoanService.GetUserLoan> {

    GetUserLoanService getUserLoanService;
    int totalAmountAsked;

    @InjectView(R.id.cc_ask_user_amount_view)
    ViewGroup getUserView ;

    CustomEditText userSelectedAmount ;
    Button loanApplyButton ;
    String loanAmount = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.fragment_get_user_loan_wanted, container, false);

        userSelectedAmount = (CustomEditText)view.findViewById(R.id.cc_loan_amount_user);
        loanApplyButton = (Button)view.findViewById(R.id.apply_loan_button);

            userSelectedAmount.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
            userSelectedAmount.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId,
                                              KeyEvent event) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                         loanAmount = v.getText().toString();
                        InputMethodManager imm = (InputMethodManager) (getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
                        imm.hideSoftInputFromWindow(userSelectedAmount.getWindowToken(), 0);
                        handled = true;
                    }
                    return handled;
                }
            });

        loanApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loanAmount != null) {
                    getUserView.setVisibility(View.GONE);
                    GetCreditLineBankFragment transUnion = new GetCreditLineBankFragment();
                    FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
                    transact.replace(R.id.loan_approved_view_fragment, transUnion);
                    transact.commit();
                } else {
                    Toast.makeText(getActivity(), "Please enter the amount you want!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    @Override
    public void bindDataToForm(final GetUserLoanService.GetUserLoan value) {
        if (value != null ) {
            userSelectedAmount.setText(String.valueOf(value.getUserAsk()));
        }
    }

    @Override
    protected void onUpdate(GetUserLoanService.GetUserLoan updatedData, Callback<GetUserLoanService.GetUserLoan> saveCallback) {
       // getUserLoanService.createUserAmountWantedService(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(GetUserLoanService.GetUserLoan updatedData, Callback<GetUserLoanService.GetUserLoan> saveCallback) {
        getUserLoanService.createUserAmountWantedService(updatedData, saveCallback);
    }

    @Override
    public GetUserLoanService.GetUserLoan getDataFromForm(GetUserLoanService.GetUserLoan base) {

        if(base == null)
            base = new GetUserLoanService.GetUserLoan();
        if(userSelectedAmount.getText() != null || userSelectedAmount.getText().toString().trim().length() > 0)
        base.setUserAsk(Integer.parseInt(userSelectedAmount.getText().toString()));
        return base;
    }

    @Override
    protected void loadDataFromServer(Callback<GetUserLoanService.GetUserLoan> dataCallback) {
        Log.d("GetUserLoanWantedAmount", "Reached get user amount");
        //getUserLoanService.getUserAmountWantedService(dataCallback);
    }

    @Override
    protected View getFormView() {
        return getUserView ;
    }

    @Override
    protected void handleDataNotPresentOnServer() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserLoanService = ((Application) getActivity().getApplication()).getRestClient().getGetUserLoanService();
        reset(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        save(false);
    }

    @Override
    public boolean isFormValid() {
        return true;
    }
}
