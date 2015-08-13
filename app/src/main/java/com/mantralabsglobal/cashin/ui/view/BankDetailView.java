package com.mantralabsglobal.cashin.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.PrimaryBankService;
import com.mantralabsglobal.cashin.ui.fragment.tabs.BankDetailFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pk on 6/21/2015.
 */
public class BankDetailView extends LinearLayout  {

    private ImageView bankName;
    public EditText accountNumber;
    private ImageView isPrimaryIcon;
    Map<String, Integer> drawableResourceMap;

    private  List<PrimaryFlagChangedListener> primaryFlagChangedListenerList = new ArrayList<>();
    private  AddMoreAccountNumberListener accountNumListener;

    private PrimaryBankService.BankDetail bankDetail;

    public BankDetailView(Context context) {
        this(context, null);
    }

    public BankDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BankDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.view_bank_account, this);

        drawableResourceMap = new HashMap<>();
        drawableResourceMap.put("HDFC", R.drawable.logo_hdfc);
        drawableResourceMap.put("CITI", R.drawable.logo_citi_bank);
        drawableResourceMap.put("YESB", R.drawable.logo_yes_bank);
        drawableResourceMap.put("PUNB", R.drawable.logo_punjab_national_bank);
        drawableResourceMap.put("SYNB", R.drawable.logo_syndicate_bank);
        drawableResourceMap.put("ICIC", R.drawable.logo_icici);

        loadViews();
    }

    private void loadViews() {
        this.bankName = (ImageView)findViewWithTag("iv_bank");
        this.isPrimaryIcon = (ImageView)findViewWithTag("iv_is_primary");
        this.accountNumber = (EditText)findViewWithTag("et_account");//  (EditText)findViewById(R.id.et_text);

        isPrimaryIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BankDetailView.this.getBankDetail().isPrimary()) {
                    //BankDetailView.this.getBankDetail().setIsPrimary(true);
                    for (PrimaryFlagChangedListener listener : primaryFlagChangedListenerList) {
                        listener.onPrimaryChanged(BankDetailView.this);
                    }
                }
            }
        });

        accountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("s",s.toString());
                if (s != null && s.toString().trim().length() > 0 &&  BankDetailView.this.getBankDetail() != null && accountNumListener != null) {
                    BankDetailView.this.getBankDetail().setAccountNumber(s.toString());
                    accountNumListener.onAccountNumberChanged(BankDetailView.this);
                }
            }
        });
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putString("currentEdit", accountNumber.getText().toString());
        bundle.putBoolean("isFocused", accountNumber.hasFocus());
        return bundle;

    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            accountNumber.setText(bundle.getString("currentEdit"));
            if (bundle.getBoolean("isFocused")) {
                accountNumber.requestFocus();
            }
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    public PrimaryBankService.BankDetail getBankDetail() {
        return bankDetail;
    }

    public void setBankDetail(PrimaryBankService.BankDetail bankDetail) {
        this.bankDetail = bankDetail;
        updateUI();
    }

    public void updateUI()
    {
        if(bankDetail != null) {
            this.accountNumber.setText(bankDetail.getAccountNumber());
            if (bankDetail.getBankName() != null && drawableResourceMap.get(bankDetail.getBankName().toUpperCase()) != null)
                this.bankName.setImageResource(drawableResourceMap.get(bankDetail.getBankName().toUpperCase()));
            if (bankDetail.isPrimary())
                this.isPrimaryIcon.setImageResource(R.drawable.ic_primary_bank_icon);
            else
                this.isPrimaryIcon.setImageResource(R.drawable.ic_bank_form_icon);
        }
    }

    public void addPrimaryFlagChangeListener(PrimaryFlagChangedListener listener)
    {
        if(!primaryFlagChangedListenerList.contains(listener))
            primaryFlagChangedListenerList.add(listener);
    }

    public void addAddMoreAccountNumberListener(AddMoreAccountNumberListener listener)
    {
        accountNumListener = listener;
    }

    public interface PrimaryFlagChangedListener
    {
        void onPrimaryChanged(BankDetailView bankDetailView);
    }

    public interface AddMoreAccountNumberListener
    {
        void onAccountNumberChanged(BankDetailView bankDetailView);
    }
}
