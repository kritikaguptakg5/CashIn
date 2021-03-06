package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.zxing.BarcodeFormat;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.app.MainActivity;
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.utils.AadhaarDAO;
import com.mantralabsglobal.cashin.service.AadhaarService;
import com.mantralabsglobal.cashin.ui.activity.scanner.ScannerActivity;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.CustomSpinner;
import com.mantralabsglobal.cashin.ui.view.SonOfSpinner;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

//import eu.livotov.zxscan.ScannerView;

/**
 * Created by pk on 13/06/2015.
 */
public class AadhaarCardFragment extends BaseBindableFragment<AadhaarService.AadhaarDetail> {

    @NotEmpty(trim = true, message= "Name cannot be empty")
    @InjectView(R.id.cc_name)
    //@Pattern(regex =  BaseActivity.NAME_VALIDATION, message = "Enter valid name")
    CustomEditText name;

    @NotEmpty(trim = true, message= "Address cannot be empty")
    @InjectView(R.id.cc_address)
    CustomEditText address;

    @NotEmpty(trim = true)
    @Digits(integer = 12, message= "Invalid Aadhaar Card Number")
    @InjectView(R.id.cc_aadhaar)
    CustomEditText aadhaarNumber;

    @InjectView(R.id.cs_gender)
    CustomSpinner gender;

    @NotEmpty(trim = true, message= "Father/Spouse name cannot be empty")
    //@Pattern(regex =  BaseActivity.NAME_VALIDATION, message = "Enter valid name")
    @InjectView(R.id.cc_father_name)
    CustomEditText fatherName;

    @NotEmpty(trim = true, message="Date of birth cannot be empty")
    @InjectView(R.id.cc_dob)
    BirthDayView birthDay;

    @InjectView(R.id.ib_launchScanner)
    ImageButton btn_scanner;

    @InjectView(R.id.bt_edit_aadhaar_detail)
    Button btn_edit_aadhaar_detail;

    @InjectView(R.id.fab_launchScanner)
    FloatingActionButton fab_launchScanner;

    @InjectView(R.id.ll_aadhaar_camera)
    ViewGroup vg_camera;

    @InjectView(R.id.rl_aadhaar_detail)
    ViewGroup vg_form;

    @InjectView(R.id.btn_next)
    BootstrapButton btnNext;

    static final int SCAN_AADHAR_CARD = 99;

    AadhaarService aadhaarService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        return inflater.inflate(R.layout.fragment_aadhaar_card, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aadhaarService = ((Application) getActivity().getApplication()).getRestClient().getAadhaarService();
        gender.setAdapter(getGenderAdapter());

        address.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if( !TextUtils.isEmpty(address.getText())){
                        Application.getInstance().putInAppPreference(AADHAR_ADDRESS, address.getText().toString());
                    }
                }
            }
        });
        //sonOfSpinner.setAdapter(sonOfSpinner.getAdapter());

        registerChildView(vg_camera, View.VISIBLE);
        registerChildView(vg_form, View.GONE);
        registerFloatingActionButton(fab_launchScanner, vg_form);

        reset(false);
    }

    @Override
    protected void onUpdate(AadhaarService.AadhaarDetail updatedData, Callback<AadhaarService.AadhaarDetail> saveCallback) {
        aadhaarService.updateAadhaarDetail(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(AadhaarService.AadhaarDetail updatedData, Callback<AadhaarService.AadhaarDetail> saveCallback) {
        aadhaarService.createAadhaarDetail(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<AadhaarService.AadhaarDetail> dataCallback) {
        aadhaarService.getAadhaarDetail(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        setVisibleChildView(vg_camera);
    }

    @Override
    protected View getFormView() {
        return vg_form;
    }


    @OnClick(R.id.bt_edit_aadhaar_detail)
    public void loadAadhaarForm() {
        bindDataToForm(null);
    }

    @OnClick({R.id.ib_launchScanner, R.id.fab_launchScanner})
    public void loadAadhaarScanner() {
        ArrayList<String> formatList = new ArrayList<>();
        formatList.add(BarcodeFormat.QR_CODE.toString());
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        intent.putExtra("FORMATS", formatList);
        intent.putExtra("FLASH", false);
        intent.putExtra("AUTO_FOCUS", true);
        getActivity().startActivityForResult(intent, SCAN_AADHAR_CARD);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("AadhaarCardFragment", "onActivityResult: " + this);
        Log.d("AadhaarCardFragment", "requestCode " + requestCode + " , resultCode=" + resultCode);

        if (requestCode == SCAN_AADHAR_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("AadhaarCardFragment", "onActivityResult: " + data.getStringExtra("aadhaar_xml"));
                AadhaarService.AadhaarDetail aadhaarDetail = AadhaarDAO.getAadhaarDetailFromXML(data.getStringExtra("aadhaar_xml"));
                bindDataToForm(aadhaarDetail);

            }
        }
    }

    @Override
    public void bindDataToForm(AadhaarService.AadhaarDetail value) {
        setVisibleChildView(vg_form);
        //TODO: Replace with form binding
        if (value != null) {

            if (value.getName() != null)
                name.setText(value.getName());
            if (value.getAddress() != null)
                address.setText(value.getAddress());
            if (value.getAadhaarNumber() != null)
                aadhaarNumber.setText(value.getAadhaarNumber());
            gender.setSelection(getGenderAdapter().getPosition(value.getGender()));
            if (value.getSonOf() != null)
                fatherName.setText(value.getSonOf());
            if (value.getDob() != null) {
                birthDay.setText(value.getDob());
            }
        }
    }

    @Override
    public AadhaarService.AadhaarDetail getDataFromForm(AadhaarService.AadhaarDetail detail) {
        if (detail == null)
            detail = new AadhaarService.AadhaarDetail();

        detail.setAddress(address.getText().toString());
        detail.setDob(birthDay.getText().toString());
        detail.setName(name.getText().toString());
        detail.setAadhaarNumber(aadhaarNumber.getText().toString());
        detail.setGender(gender.getSpinner().getSelectedItem().toString());
        detail.setSonOf(fatherName.getText().toString());
        return detail;
    }
}
