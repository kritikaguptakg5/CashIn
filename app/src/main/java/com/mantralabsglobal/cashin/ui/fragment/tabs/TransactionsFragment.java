package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.GetUserLoanService;
import com.mantralabsglobal.cashin.service.TransactionsService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.TransactionsView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import retrofit.Callback;
//import eu.livotov.zxscan.ScannerView;

/**
 * Created by kritika on 31/08/2015.
 */
public class TransactionsFragment extends BaseBindableFragment<TransactionsService.Transactions> {

    TransactionsService transactionService ;

    @InjectView(R.id.transactions_by_card)
    ViewGroup transactionViewGroup;

    @InjectView(R.id.remaining_amount_tobe_transferred)
    TextView remainingFundsToBeTransferred;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.fragment_transactions, container, false);


        return view;
    }

    @Override
    public void bindDataToForm(final TransactionsService.Transactions value) {
        if (value != null ) {

            remainingFundsToBeTransferred.setText(value.getRemainingAmount());
            for (final TransactionsService.TransactionDescriptionMessage transactionDescMsg : value.getTransactionMessages()) {
                TransactionsView transactionView = new TransactionsView(getActivity());
                transactionView.setTransactionDescMsg(transactionDescMsg);
                transactionViewGroup.addView(transactionView);
            }
        }
    }

    @Override
    protected void onUpdate(TransactionsService.Transactions updatedData, Callback<TransactionsService.Transactions> saveCallback) {
       // getUserLoanService.createUserAmountWantedService(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(TransactionsService.Transactions updatedData, Callback<TransactionsService.Transactions> saveCallback) {
     //   transactionService.createTransactions(updatedData, saveCallback);
    }

    @Override
    public TransactionsService.Transactions getDataFromForm(TransactionsService.Transactions base) {
        //base.setUserAsk(Integer.parseInt(userSelectedAmount.getText().toString()));
        return base;
    }

    @Override
    protected void loadDataFromServer(Callback<TransactionsService.Transactions> dataCallback) {
        Log.d("GetUserLoanWantedAmount", "Reached get user amount");
        transactionService.getTransactions(dataCallback);
    }

    @Override
    protected View getFormView() {
        return transactionViewGroup ;
    }

    @Override
    protected void handleDataNotPresentOnServer() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transactionService = ((Application) getActivity().getApplication()).getRestClient().getTransactionsService();
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
    public boolean isFormValid() {
        return true;
    }
}
