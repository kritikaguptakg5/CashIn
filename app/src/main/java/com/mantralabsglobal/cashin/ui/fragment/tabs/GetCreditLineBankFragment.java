package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.GetCreditLineBankService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.PostLoanApprovedActivity;

import retrofit.Callback;

//import eu.livotov.zxscan.ScannerView;

/**
 * Created by pk on 13/06/2015.
 */
public class GetCreditLineBankFragment extends BaseBindableFragment<GetCreditLineBankService.GetCreditLineBank> {

    GetCreditLineBankService getCreditLineBankService;
    int totalApprovedAmount;
    int loanStatus = -1;
    final int LOAN_STATUS_APPROVED = 1;
    final int LOAN_STATUS_REJECTED = 0;

    ViewGroup   loan_rejected_view, transunion_view;
    SeekBar seekBar;
    static int status = -1;
    TextView first_amount,first_interest_one, first_interest_two , first_interest_three,second_amount,second_interest_one, second_interest_two , second_interest_three,
            three_amount,three_interest_one, three_interest_two , three_interest_three, loan_amount;
    TextView userSelectedAmount, user_approved_limit;
    Button askForLoan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_credit_line_bank, container, false);
         loan_rejected_view = (ViewGroup)view.findViewById(R.id.loan_rejected_view);

         transunion_view = (ViewGroup)view.findViewById(R.id.get_credit_line_view);
        askForLoan = (Button)view.findViewById(R.id.get_loan_button);
         userSelectedAmount= (TextView)view.findViewById(R.id.user_selected_amount);
         user_approved_limit = (TextView)view.findViewById(R.id.user_approved_limit);

         seekBar = (SeekBar)view.findViewById(R.id.seekBar);

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
                        Log.d("seek bar",String.valueOf(progress));
                        userSelectedAmount.setText("" + progress);
                        save();
                    }
                });

        askForLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PostLoanApprovedActivity.class);
                startActivity(intent);
            }
        });

         first_amount= (TextView)view.findViewById(R.id.first_amount);

         first_interest_one= (TextView)view.findViewById(R.id.first_interest_one);

         first_interest_two= (TextView)view.findViewById(R.id.first_interest_two);

         first_interest_three= (TextView)view.findViewById(R.id.first_interest_three);

         second_amount= (TextView)view.findViewById(R.id.second_amount);

         second_interest_one= (TextView)view.findViewById(R.id.second_interest_one);

         second_interest_two= (TextView)view.findViewById(R.id.second_interest_two);

         second_interest_three= (TextView)view.findViewById(R.id.second_interest_three);

         three_amount= (TextView)view.findViewById(R.id.three_amount);

         three_interest_one= (TextView)view.findViewById(R.id.three_interest_one);

         three_interest_two= (TextView)view.findViewById(R.id.three_interest_two);

         three_interest_three= (TextView)view.findViewById(R.id.three_interest_three);

         loan_amount = (TextView)view.findViewById(R.id.loan_amount);

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

            loan_amount.setText("A credit line of up to Rs " + totalApprovedAmount + " has been preapproved to you by RBL Bank");
            seekBar.setMax(totalApprovedAmount);

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

        if(base == null)
            base = new GetCreditLineBankService.GetCreditLineBank();

        if(userSelectedAmount.getText() != null)
            base.setLoanAmountActuallyAsked(Integer.parseInt(userSelectedAmount.getText().toString()));

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
}
