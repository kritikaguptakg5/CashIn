package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.BankProvider;
import com.mantralabsglobal.cashin.service.LoanDetailService;
import com.mantralabsglobal.cashin.ui.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @InjectView(R.id.remaining_amount)
    TextView remainingAmount;

    List<LoanDetailService.LoanTenure> loanTenureSelected;

    @InjectView(R.id.account_number)
    TextView accountNo;

    @InjectView(R.id.access_funds)
    Button accessFundsButton;

    Map<CheckBox, LoanDetailService.LoanTenure> checkBoxMap;

    @InjectView(R.id.bank_logo)
    ImageView bankLogo;    String loanApprovedBankName;


    @InjectView(R.id.bank_logo_access_funds)
    ImageView bankLogoAccessFunds;

    @InjectView(R.id.credit_line_amount_access_funds)
    TextView credit_line_amount_access_funds;

    @InjectView(R.id.seekBar_for_tranfer_amount)
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
        View view = inflater.inflate(R.layout.fragment_loan_details, container, false);
        return view;
    }

    private void createTable(List<LoanDetailService.LoanTenure> EMITable) {

        TextView tenureText, interestText, emiText;
        CheckBox checkBox;

        if (getActivity() != null) {
            TableRow tr_head = new TableRow(getActivity());
            tr_head.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT, 3));
            tr_head.setGravity(Gravity.CENTER);
            tr_head.setBackground(Application.getInstance().getResources().getDrawable(R.drawable.table_border));
            checkBoxMap = new HashMap<CheckBox, LoanDetailService.LoanTenure>();

            checkBox = new CheckBox(getActivity());
            checkBox.setVisibility(View.INVISIBLE);

            tenureText = new TextView(getActivity());
            tenureText.setGravity(Gravity.CENTER);
            tenureText.setText("Tenure");

            interestText = new TextView(getActivity());
            interestText.setGravity(Gravity.CENTER);
            interestText.setText("Interest");

            emiText = new TextView(getActivity());
            emiText.setGravity(Gravity.CENTER);
            emiText.setText("EMI");

            tr_head.addView(checkBox, 0);
            tr_head.addView(tenureText, 1);
            tr_head.addView(interestText, 2);
            tr_head.addView(emiText, 3);

            emiTable.addView(tr_head, 0);

            if (EMITable != null) {
                // TableRow tr=null;
                for (int row_no = 0; row_no < EMITable.size(); row_no++) {
                    //     tr_head.removeAllViews();
                    tr_head = new TableRow(getActivity());
                    tr_head.setBackground(Application.getInstance().getResources().getDrawable(R.drawable.table_border));
                    checkBox = new CheckBox(getActivity());
                    tr_head.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT, 3));
                    tr_head.setGravity(Gravity.CENTER);

                    tenureText = new TextView(getActivity());
                    interestText = new TextView(getActivity());
                    emiText = new TextView(getActivity());

                    tenureText.setText(EMITable.get(row_no).getTenure()+" Months");
                    tenureText.setGravity(Gravity.CENTER);
                    interestText.setText(String.valueOf(EMITable.get(row_no).getInterest()));
                    interestText.setGravity(Gravity.CENTER);
                    emiText.setText(String.valueOf((int)EMITable.get(row_no).getEMI()));
                    emiText.setGravity(Gravity.CENTER);

                    checkBox.setChecked(false);
                    checkBoxMap.put(checkBox, EMITable.get(row_no));
                    addCheckBoxListener(checkBox);

                    tr_head.addView(checkBox, 0);
                    tr_head.addView(tenureText, 1);
                    tr_head.addView(interestText, 2);
                    tr_head.addView(emiText, 3);

                    emiTable.addView(tr_head, row_no + 1);


                    //);
                    //     tr_head.removeAllViews();
                }
            }
        }
    }

    private void addCheckBoxListener(final CheckBox checkBoxClicked) {
        checkBoxClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {

                    for (Iterator<Map.Entry<CheckBox, LoanDetailService.LoanTenure>> it = checkBoxMap.entrySet().iterator(); it.hasNext(); ) {
                        Map.Entry<CheckBox, LoanDetailService.LoanTenure> entry = it.next();
                        if (!checkBoxClicked.equals(entry.getKey()) && entry.getKey().isChecked()) {
                            entry.getKey().setChecked(false);
                        } else {
                            loanTenureSelected = new ArrayList<LoanDetailService.LoanTenure>();
                            loanTenureSelected.add(entry.getValue());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void bindDataToForm(final LoanDetailService.LoanDetail value) {
        if (value != null) {
            creditAmount.setText(String.valueOf(value.getCreditAmount()));
            remainingAmount.setText(String.valueOf(value.getCreditAmount()));
            credit_line_amount_access_funds.setText(String.valueOf(value.getCreditAmount()));
            maximumActualLoanAmount.setText(String.valueOf(value.getRemainingAmount()));
            seekTransferAmount.setMax((int)value.getRemainingAmount());
            if (value.getAccount_no() != null && value.getAccount_no().length() >= 4)
                accountNo.setText(value.getAccount_no().substring(value.getAccount_no().length() - 4, value.getAccount_no().length()));
            BankProvider.Bank bank = BankProvider.getInstance().getBanks().getByCodeOrName(value.getBankName());
            loanApprovedBankName = value.getBankName();
            if (bank != null && bank.getLogo() != null && getActivity() != null) {
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

        if (base == null)
            base = new LoanDetailService.LoanDetail();

        if (userSelectedTransferredAmount.getText() != null && userSelectedTransferredAmount.getText().toString().trim().length() > 0) {
            base.setAmountToBeTransferred(Double.parseDouble(userSelectedTransferredAmount.getText().toString()));
            base.setEMITable(loanTenureSelected);
            base.setAccount_no(accountNo.getText().toString());
            base.setBankName(loanApprovedBankName);
            if (remainingAmount.getText() != null && remainingAmount.getText().toString().trim().length() > 0)
                base.setRemainingAmount(Double.parseDouble(remainingAmount.getText().toString()));
            if (creditAmount.getText() != null && creditAmount.getText().toString().trim().length() > 0)
                base.setCreditAmount(Double.parseDouble(creditAmount.getText().toString()));
                base.setCreditAmount(Double.parseDouble(creditAmount.getText().toString()));
        }
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
                        //save();
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

        reset(false);
    }

    @Override
    public boolean isFormValid() {
        return true;
    }
}
