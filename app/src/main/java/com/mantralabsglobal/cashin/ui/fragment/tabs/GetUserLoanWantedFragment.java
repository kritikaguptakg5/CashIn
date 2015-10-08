package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.GetUserLoanService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import butterknife.InjectView;
import retrofit.Callback;

/**
 * Created by kritika on 31/08/2015.
 */
public class GetUserLoanWantedFragment extends BaseBindableFragment<GetUserLoanService.GetUserLoan> {

    GetUserLoanService getUserLoanService;

    @InjectView(R.id.cc_ask_user_amount_view)
    ViewGroup getUserView;

    boolean loamAmountValid = false;

    @NotEmpty(message = "Loan amount cannot be empty")
    @InjectView(R.id.cc_loan_amount_user)
    CustomEditText userSelectedAmount;

    @InjectView(R.id.apply_loan_button)
    Button loanApplyButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_user_loan_wanted, container, false);
        return view;
    }

    @Override
    public void bindDataToForm(final GetUserLoanService.GetUserLoan value) {
        if (value != null) {
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

        double userAmountEntered = 0;
        if (base == null)
            base = new GetUserLoanService.GetUserLoan();
        if (userSelectedAmount.getText() != null && userSelectedAmount.getText().toString().trim().length() > 0) {
            boolean commaPresent = userSelectedAmount.getText().toString().contains(",");
            boolean spacePresent = userSelectedAmount.getText().toString().contains(" ");
            boolean hyphenPresent = userSelectedAmount.getText().toString().contains("-");
            if (!spacePresent && !hyphenPresent) {
                if (!userSelectedAmount.getText().toString().trim().contains(".") ||
                        (userSelectedAmount.getText().toString().trim().contains(".")
                                && userSelectedAmount.getText().toString().trim().length() > 1)) {
                    loamAmountValid = true;
                    if (commaPresent)
                        if (userSelectedAmount.getText().toString().trim().length() > 1)
                            userAmountEntered = Double.valueOf(userSelectedAmount.getText().toString().trim()
                                    .replaceAll(",", ""));
                        else
                            loamAmountValid = false;
                    else
                        userAmountEntered = Double.valueOf(userSelectedAmount.getText().toString().trim());
                }
            } else {
                loamAmountValid = false;
            }
            base.setUserAsk(userAmountEntered);
        }
        return base;
    }

    @Override
    protected void loadDataFromServer(Callback<GetUserLoanService.GetUserLoan> dataCallback) {
        //getUserLoanService.getUserAmountWantedService(dataCallback);
    }

    @Override
    protected View getFormView() {
        return getUserView;
    }

    @Override
    protected void handleDataNotPresentOnServer() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserLoanService = ((Application) getActivity().getApplication()).getRestClient().getGetUserLoanService();

        userSelectedAmount.getEditText().setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        userSelectedAmount.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        userSelectedAmount.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //loanAmount = v.getText().toString();
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
                getDataFromForm(null);
                if (userSelectedAmount != null && userSelectedAmount.getText() != null
                        && userSelectedAmount.getText() != null && userSelectedAmount.getText().toString().trim().length() > 0) {

                    if (!loamAmountValid)
                        userSelectedAmount.getEditText().setError(Application.getInstance().getResources().getString(R.string.loan_amount_special_char));
                    else {
                        getUserView.setVisibility(View.GONE);
                        GetCreditLineBankFragment transUnion = new GetCreditLineBankFragment();
                        FragmentTransaction transact = getActivity().getSupportFragmentManager().beginTransaction();
                        transact.replace(R.id.loan_approved_view_fragment, transUnion);
                        transact.commit();
                    }
                } else {
                    userSelectedAmount.getEditText().setError(Application.getInstance().getResources().getString(R.string.empty_loan_amount));
                }
            }
        });

    }
}
