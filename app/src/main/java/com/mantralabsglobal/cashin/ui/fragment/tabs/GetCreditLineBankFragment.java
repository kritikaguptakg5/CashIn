package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.GetCreditLineBankService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.PostLoanApprovedActivity;

import butterknife.InjectView;
import butterknife.InjectViews;
import retrofit.Callback;

//import eu.livotov.zxscan.ScannerView;

/**
 * Created by pk on 13/06/2015.
 */
public class GetCreditLineBankFragment extends BaseBindableFragment<GetCreditLineBankService.GetCreditLineBank> {

    private static final String TAG = GetCreditLineBankFragment.class.getSimpleName();
    GetCreditLineBankService getCreditLineBankService;
    double totalApprovedAmount;
    int loanStatus = -1;
    final int LOAN_STATUS_APPROVED = 1;
    final int LOAN_STATUS_REJECTED = 0;

    @InjectView(R.id.loan_rejected_view)
    ViewGroup loan_rejected_view;
    @InjectView(R.id.get_credit_line_view)
    ViewGroup transunion_view;
    @InjectView(R.id.seekBar)
    SeekBar seekBar;
    @InjectView(R.id.first_amount)
    TextView first_amount;
    @InjectView(R.id.first_interest_one)
    TextView first_interest_one;
    @InjectView(R.id.first_interest_two)
    TextView first_interest_two;
    @InjectView(R.id.first_interest_three)
    TextView first_interest_three;
    @InjectView(R.id.second_amount)
    TextView second_amount;
    @InjectView(R.id.second_interest_one)
    TextView second_interest_one;
    @InjectView(R.id.second_interest_two)
    TextView second_interest_two;
    @InjectView(R.id.second_interest_three)
    TextView second_interest_three;
    @InjectView(R.id.three_amount)
    TextView three_amount;
    @InjectView(R.id.three_interest_one)
    TextView three_interest_one;
    @InjectView(R.id.three_interest_two)
    TextView three_interest_two;
    @InjectView(R.id.three_interest_three)
    TextView three_interest_three;
    @InjectView(R.id.loan_amount)
    TextView loan_amount;
    @InjectView(R.id.user_approved_limit)
    TextView user_approved_limit;
    @InjectView(R.id.user_selected_amount)
    EditText userSelectedAmount;
    @InjectView(R.id.get_loan_button)
    Button askForLoan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_credit_line_bank, container, false);
        return view;
    }


    @Override
    public void bindDataToForm(final GetCreditLineBankService.GetCreditLineBank value) {

        if (value != null && value.getStatus() == LOAN_STATUS_APPROVED) {
            transunion_view.setVisibility(View.VISIBLE);
            loan_rejected_view.setVisibility(View.GONE);
            loanStatus = LOAN_STATUS_APPROVED;
            totalApprovedAmount = value.getLoanApproves();
            user_approved_limit.setText(String.valueOf(totalApprovedAmount));

            first_amount.setText(value.getInterestSlabs().get(0).getAmount());
            first_interest_one.setText(String.valueOf(value.getInterestSlabs().get(0).getTwelve_month_interest()));
            first_interest_two.setText(String.valueOf(value.getInterestSlabs().get(0).getTwentyfour_month_interest()));
            first_interest_three.setText(String.valueOf(value.getInterestSlabs().get(0).getThirtysix_month_interest()));

            second_amount.setText(value.getInterestSlabs().get(1).getAmount());
            second_interest_one.setText(String.valueOf(value.getInterestSlabs().get(1).getTwelve_month_interest()));
            second_interest_two.setText(String.valueOf(value.getInterestSlabs().get(1).getTwentyfour_month_interest()));
            second_interest_three.setText(String.valueOf(value.getInterestSlabs().get(1).getThirtysix_month_interest()));

            three_amount.setText(value.getInterestSlabs().get(2).getAmount());
            three_interest_one.setText(String.valueOf(value.getInterestSlabs().get(2).getTwelve_month_interest()));
            three_interest_two.setText(String.valueOf(value.getInterestSlabs().get(2).getTwentyfour_month_interest()));
            three_interest_three.setText(String.valueOf(value.getInterestSlabs().get(2).getThirtysix_month_interest()));

            loan_amount.setText(String.format(getString(R.string.credit_line_pre_approved), "" + totalApprovedAmount, "ACME Bank"));
            seekBar.setMax((int) totalApprovedAmount);

        } else if (value.getStatus() == LOAN_STATUS_REJECTED) {
            loan_rejected_view.setVisibility(View.VISIBLE);
            transunion_view.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onUpdate(GetCreditLineBankService.GetCreditLineBank updatedData, Callback<GetCreditLineBankService.GetCreditLineBank> saveCallback) {
        getCreditLineBankService.createGetCreditLineBankService(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(GetCreditLineBankService.GetCreditLineBank updatedData, Callback<GetCreditLineBankService.GetCreditLineBank> saveCallback) {
        getCreditLineBankService.createGetCreditLineBankService(updatedData, saveCallback);
    }

    @Override
    public GetCreditLineBankService.GetCreditLineBank getDataFromForm(GetCreditLineBankService.GetCreditLineBank base) {

        if (base == null)
            base = new GetCreditLineBankService.GetCreditLineBank();

        if (!TextUtils.isEmpty(userSelectedAmount.getText())) {
            try {
                base.setLoanAmountActuallyAsked(Double.parseDouble(userSelectedAmount.getText().toString()));
            } catch (NumberFormatException ex) {
                Log.w(TAG, ex);
            }
        }

        return base;
    }

    @Override
    protected void loadDataFromServer(Callback<GetCreditLineBankService.GetCreditLineBank> dataCallback) {
        getCreditLineBankService.getGetCreditLineBankService(dataCallback);
    }


    @Override
    protected View getFormView() {
        return transunion_view;
    }

    @Override
    protected void handleDataNotPresentOnServer() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCreditLineBankService = ((Application) getActivity().getApplication()).getRestClient().getTransUnionService();

        userSelectedAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && s.toString().trim().length() > 0)
                seekBar.setProgress((int)Double.parseDouble(s.toString()));
            }
        });

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progresValue, boolean fromUser) {
                        progress = progresValue;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        Log.d("seek bar", String.valueOf(progress));
                        userSelectedAmount.setText("" + progress);
                        save();
                    }
                });

        askForLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostLoanApprovedActivity.class);
                startActivity(intent);
            }
        });

        reset(false);
    }

    @Override
    public boolean isFormValid() {
        return true;
    }
}
