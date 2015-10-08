package com.mantralabsglobal.cashin.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.BankProvider;
import com.mantralabsglobal.cashin.service.PrimaryBankService;
import com.mantralabsglobal.cashin.service.TransactionsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pk on 6/21/2015.
 */
public class TransactionsView extends LinearLayout  {

    private TextView transactionDescription;
    private Button convertToEMI;

    private TransactionsService.TransactionDescriptionMessage transactionDescMsg;

    public TransactionsView(Context context) {
        this(context, null);
    }

    public TransactionsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransactionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.view_transactions, this);
        loadViews();
    }

    private void loadViews() {
        this.transactionDescription = (TextView)findViewWithTag("iv_transaction_description");
        this.convertToEMI = (Button)findViewWithTag("convert_to_emi");
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putString("currentEdit", transactionDescription.getText().toString());
        bundle.putBoolean("isFocused", transactionDescription.hasFocus());
        return bundle;

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            transactionDescription.setText(bundle.getString("currentEdit"));
            if (bundle.getBoolean("isFocused")) {
                transactionDescription.requestFocus();
            }
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    public TransactionsService.TransactionDescriptionMessage getTransactionDescMsg() {
        return transactionDescMsg;
    }

    public void setTransactionDescMsg(TransactionsService.TransactionDescriptionMessage transactionDescMsg) {
        this.transactionDescMsg = transactionDescMsg;
        updateUI();
    }

    public void updateUI()
    {
        if(transactionDescMsg != null) {

            if(transactionDescMsg.getTransactionMode() != null && transactionDescMsg.getTransactionMode().equals("CARD") )
            this.transactionDescription.setText(transactionDescMsg.getAmountTransacted()+" swiped "+transactionDescMsg.getTransactionMode()
           +" on "+transactionDescMsg.getTransactionDate() );

            else {
                this.transactionDescription.setText(transactionDescMsg.getAmountTransacted()+" transferred to your "+transactionDescMsg.getBankName()
                        +" on "+transactionDescMsg.getTransactionDate() );
            }
        }
    }

}
