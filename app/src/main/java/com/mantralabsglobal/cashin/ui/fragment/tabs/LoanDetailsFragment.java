package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.BankProvider;
import com.mantralabsglobal.cashin.service.GetUserLoanService;
import com.mantralabsglobal.cashin.service.LoanDetailService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;

import java.util.List;

import butterknife.InjectView;
import retrofit.Callback;
//import eu.livotov.zxscan.ScannerView;

/**
 * Created by kritika on 31/08/2015.
 */
public class LoanDetailsFragment extends BaseBindableFragment<LoanDetailService.LoanDetail> {

    LoanDetailService loanDetailService;

    @InjectView(R.id.loan_approved_view_fragment)
    ViewGroup loanApproved;

    @InjectView(R.id.loan_access_funds)
    ViewGroup loanAccessFundsView;

    @InjectView(R.id.footer_transfer_amount)
    ViewGroup footerTransferAmount;

    @InjectView(R.id.emi_table)
    TableLayout emiTable;

    @InjectView(R.id.credit_line_amount)
    TextView creditAmount;


    @InjectView(R.id.account_number)
    TextView accountNo;

    Button accessFundsButton;

    @InjectView(R.id.bank_logo)
    ImageView bankLogo;

    @InjectView(R.id.bank_logo_access_funds)
    ImageView bankLogoAccessFunds;

    @InjectView(R.id.credit_line_amount_access_funds)
    TextView credit_line_amount_access_funds;

    SeekBar seekTransferAmount;

    @InjectView(R.id.user_selected_transferred_amount)
    TextView userSelectedTransferredAmount;

    @InjectView(R.id.maximum_actual_loan_amount)
    TextView maximumActualLoanAmount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_loan_details, container, false);

        accessFundsButton = (Button) view.findViewById(R.id.access_funds);

        seekTransferAmount = (SeekBar) view.findViewById(R.id.seekBar_for_tranfer_amount);
        seekTransferAmount.setOnSeekBarChangeListener(
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
                        userSelectedTransferredAmount.setText("" + progress);
                        save();
                    }
                });


        accessFundsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loanAccessFundsView.setVisibility(View.VISIBLE);
                footerTransferAmount.setVisibility(View.VISIBLE);
                loanApproved.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void createTable(List<LoanDetailService.LoanTenure> EMITable){

        TextView tenureText,interestText, emiText;

        TableRow tr_head = new TableRow(getActivity());
        tr_head.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,3));
        tr_head.setGravity(Gravity.CENTER);

         tenureText = new TextView(getActivity());

        tenureText.setText("Tenure");
         interestText = new TextView(getActivity());

        interestText.setText("Interest");
         emiText = new TextView(getActivity());

        emiText.setText("EMI");

        tr_head.addView(tenureText,0);
        tr_head.addView(interestText,1);
        tr_head.addView(emiText,2);

        emiTable.addView(tr_head,0);


       // TableRow tr=null;
        for (int row_no=0; row_no < EMITable.size(); row_no++) {
       //     tr_head.removeAllViews();
            tr_head = new TableRow(getActivity());
            tr_head.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 3));
            tr_head.setGravity(Gravity.CENTER);
            tenureText = new TextView(getActivity());

            interestText = new TextView(getActivity());

            emiText = new TextView(getActivity());

            tenureText.setText(EMITable.get(row_no).getTenure());
            interestText.setText(String.valueOf(EMITable.get(row_no).getInterest()));
            emiText.setText(String.valueOf(EMITable.get(row_no).getEMI()));

            tr_head.addView(tenureText, 0);
            tr_head.addView(interestText, 1);
            tr_head.addView(emiText, 2);

            emiTable.addView(tr_head,row_no+1);


            //);
       //     tr_head.removeAllViews();

       }

       /* TableRow tr=null;
        for (int row_size=0; row_size < EMITable.size(); row_size++) {
                *//* maybe you want to do something special with the data from the server here ? *//*

            if (row_size == 0) {
                tr = new TableRow(getActivity());
                tr.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            CheckBox check = new CheckBox(getActivity());
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        *//* add your code here *//*
                }
            });
            check.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            tr.addView(check);


        }*/
    }

    @Override
    public void bindDataToForm(final LoanDetailService.LoanDetail value) {
        if (value != null) {
            creditAmount.setText(String.valueOf(value.getCreditAmount()));
            credit_line_amount_access_funds.setText(String.valueOf(value.getCreditAmount()));
            maximumActualLoanAmount.setText(String.valueOf(value.getCreditAmount()));
            seekTransferAmount.setMax(value.getCreditAmount());
            accountNo.setText(value.getAccount_no().substring(value.getAccount_no().length()-4,value.getAccount_no().length()));
            BankProvider.Bank bank = BankProvider.getInstance().getBanks().getByCodeOrName(value.getBankName());
            if (bank != null && bank.getLogo() != null) {
                bankLogo.setImageResource(getActivity().getResources().getIdentifier(bank.getLogo(), "drawable", getActivity().getPackageName()));
                bankLogoAccessFunds.setImageResource(getActivity().getResources().getIdentifier(bank.getLogo(), "drawable", getActivity().getPackageName()));
            }
            createTable(value.getEMITable());
        }
    }

    @Override
    protected void onUpdate(LoanDetailService.LoanDetail updatedData, Callback<LoanDetailService.LoanDetail> saveCallback) {
        // getUserLoanService.createUserAmountWantedService(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(LoanDetailService.LoanDetail updatedData, Callback<LoanDetailService.LoanDetail> saveCallback) {
        loanDetailService.createLoanDetail(updatedData, saveCallback);
    }

    @Override
    public LoanDetailService.LoanDetail getDataFromForm(LoanDetailService.LoanDetail base) {

        if(base == null)
            base = new LoanDetailService.LoanDetail();

        base.setAmountToBeTransferred(Integer.parseInt(userSelectedTransferredAmount.getText().toString()));
        return base;
    }

    @Override
    protected void loadDataFromServer(Callback<LoanDetailService.LoanDetail> dataCallback) {
        loanDetailService.getLoanDetail(dataCallback);
    }

    @Override
    protected View getFormView() {
        return loanApproved;
    }

    @Override
    protected void handleDataNotPresentOnServer() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loanDetailService = ((Application) getActivity().getApplication()).getRestClient().getLoanDetailService();
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
