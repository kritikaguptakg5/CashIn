package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AadhaarService;
import com.mantralabsglobal.cashin.service.AddressService;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.utils.LocationAddress;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Digits;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 6/30/2015.
 */
public abstract class AddressFragment extends BaseBindableFragment<AddressService.Address> {

    @NotEmpty(trim = true, message = "Street cannot be empty")
    @InjectView(R.id.cc_street)
    CustomEditText cc_street;

    @NotEmpty(trim = true, message = "Pincode cannot be empty")
    @Digits(integer = 6)
    @InjectView(R.id.cc_pincode)
    CustomEditText cc_pincode;

    @NotEmpty(trim = true, message = "City cannot be empty")
    @InjectView(R.id.cc_city)
    CustomEditText cc_city;

    @NotEmpty(trim = true, message = "State cannot be empty")
    @InjectView(R.id.cc_state)
    CustomEditText cc_state;

    /* @InjectView(R.id.cs_owned_by)
     CustomSpinner cs_own;*/
    @InjectView(R.id.btn_edit_address)
    Button btn_editAddress;

    @InjectView(R.id.cc_aadhaar_address)
    CustomEditText aadhaar_address_text;

    @InjectView(R.id.ib_get_gps_location)
    ImageButton ib_get_gps_location;

    @Checked
    @InjectView(R.id.ownOrRentedRadioGroup)
    RadioGroup houseRentedorOwn;

   /* @InjectView(R.id.rb_rent)
    RadioButton rb_rented;

    @InjectView(R.id.rb_own)
    RadioButton rb_own;*/

    /*@InjectView(R.id.fab_get_loc_from_gps)
    FloatingActionButton btnGetLocationFromGPS;*/

    @InjectView(R.id.vg_address_form)
    ViewGroup vg_addressForm;

    @InjectView(R.id.aadhaar_address_layout)
    ViewGroup vg_aadhaar_address_layout;

//    @InjectView(R.id.vg_gps_launcher)
//    ViewGroup vg_gpsLauncher;
//    ViewGroup vg_gpsLauncher;

    @InjectView(R.id.chkAadhaar)
    CheckBox addrSameAsAadhaar;

    private AddressService mAddressService;

    //private AddressService.Address addressOnServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        String addressLabel = getAddressLabel();
        if (!TextUtils.isEmpty(addressLabel)) {
            TextView addressLabal = (TextView) view.findViewById(R.id.adressTextView);
            addressLabal.setText(addressLabel);
        }

        return view;
    }

    public void getAadhaarAddressFromAadhaar() {

        String aadhaarAddress = Application.getInstance().getAppPreference().getString(AADHAR_ADDRESS, null);
        if(!TextUtils.isEmpty(aadhaarAddress)){
            aadhaar_address_text.setText(aadhaarAddress);
        }
        else {
            RestClient.getInstance().getAadhaarService().getAadhaarDetail(new Callback<AadhaarService.AadhaarDetail>() {
                @Override
                public void success(AadhaarService.AadhaarDetail aadhaarDetail, Response response) {
                    if (aadhaarDetail != null && aadhaarDetail.getAddress() != null)
                        aadhaar_address_text.setText(aadhaarDetail.getAddress());
                    else {
                        if (getActivity() != null) {
                            String error = getActivity().getResources().getString(R.string.enter_aadhar_address);
                            aadhaar_address_text.getEditText().setError(error);
                        }
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                    showErrorWithAction(R.string.failed_to_query_server, R.string.retry_button, new OnActionListener() {
                        @Override
                        public void performAction() {
                            getAadhaarAddressFromAadhaar();
                        }
                    });
                }
            });
        }

    }

    protected String getAddressLabel() {
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*CustomSpinner spinner = (CustomSpinner) view.findViewById(R.id.cs_owned_by);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.own_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); */

        //aadhaar_address_text.getEditText().setClickable(false);
        aadhaar_address_text.getEditText().setKeyListener(null);
        addrSameAsAadhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    setVisibleChildView(vg_aadhaar_address_layout);
                    getAadhaarAddressFromAadhaar();
                } else {
                    setVisibleChildView(vg_addressForm);
                    getLocationFromGPS();
                }
            }
        });


        if (addrSameAsAadhaar.isChecked()) {
            registerChildView(vg_aadhaar_address_layout, View.VISIBLE);
            registerChildView(vg_addressForm, View.GONE);
        } else {
            registerChildView(vg_aadhaar_address_layout, View.GONE);
            registerChildView(vg_addressForm, View.VISIBLE);
        }

    }


    @Override
    public void onStart (){
        super.onStart();
        reset(false);
    }

    @Override
    protected View getFormView() {

        if (addrSameAsAadhaar.isChecked())
            return vg_aadhaar_address_layout;
        else
            return vg_addressForm;

    }

    @Override
    protected void handleDataNotPresentOnServer() {
        if (addrSameAsAadhaar.isChecked()) {
            setVisibleChildView(vg_aadhaar_address_layout);
            getAadhaarAddressFromAadhaar();
        } else {
            showAddressForm();
            getLocationFromGPS();
        }
    }

    /*@OnClick(R.id.btn_edit_address)*/
    public void showAddressForm() {
        setVisibleChildView(vg_addressForm);
    }

    /*@OnClick(R.id.fab_get_loc_from_gps)
    public void showGPSLauncher()
    {
        setVisibleChildView(vg_gpsLauncher);
    }*/

    @OnClick(R.id.ib_get_gps_location)
    public void getLocationFromGPS() {
        showProgressDialog(getString(R.string.waiting_for_gps));

        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getCurrentAddress(getActivity(), new LocationAddress.AddressListener() {
            @Override
            public void onAddressAquired(final AddressService.Address address) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AddressFragment.this.onGPSAddressAquired(address);
                        hideProgressDialog();
                    }
                });
            }

            @Override
            public void onError(final Throwable error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showErrorOnUIWithoutAction(R.string.gps_failed);
                        hideProgressDialog();
                    }
                });
            }
        });

    }

    public void onGPSAddressAquired(AddressService.Address address) {
        if (address != null) {
            bindDataToForm(address);
            showAddressForm();
        }
    }

    public AddressService getAddressService() {
        if (mAddressService == null) {
            mAddressService = ((Application) getActivity().getApplication()).getRestClient().getAddressService();
        }
        return mAddressService;
    }

    public void setAddressService(AddressService addressService) {
        this.mAddressService = addressService;
    }

    @Override
    public void bindDataToForm(final AddressService.Address address) {

        if (address != null) {

            if (address.isSameAsAadhaar()) {
                addrSameAsAadhaar.setChecked(true);
                setVisibleChildView(vg_aadhaar_address_layout);
                if (aadhaar_address_text.getText() == null ||
                        aadhaar_address_text.getText().toString().trim().length() < 1) {
                    getAadhaarAddressFromAadhaar();
                }

            } else {
                addrSameAsAadhaar.setChecked(false);
                setVisibleChildView(vg_addressForm);
            }

            if (getActivity() != null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!address.isSameAsAadhaar()) {
                            cc_city.setText(address.getCity());
                            cc_state.setText(address.getState());
                            cc_pincode.setText(address.getPincode());
                            cc_street.setText(address.getStreet());
                            houseRentedorOwn.check(address.isHouseRented() ? R.id.rb_rent : R.id.rb_own);

                            // cs_own.getSpinner().setSelection(((ArrayAdapter<String>) cs_own.getAdapter()).getPosition(address.getOwn()));
                        } else {
                            houseRentedorOwn.check(address.isHouseRented() ? R.id.rb_rent : R.id.rb_own);

                        }
                    }
                });
        }
    }

    @Override
    public AddressService.Address getDataFromForm(AddressService.Address address) {
        if (address == null)
            address = new AddressService.Address();

        if (!addrSameAsAadhaar.isChecked()) {
            address.setSameAsAadhaar(false);
            address.setStreet(cc_street.getText().toString());
            address.setPincode(cc_pincode.getText().toString());
            address.setCity(cc_city.getText().toString());
            address.setState(cc_state.getText().toString());
            address.setIsHouseRented(houseRentedorOwn.getCheckedRadioButtonId() == R.id.rb_rent);
            //address.setOwn(cs_own.getSpinner().getSelectedItem().toString());
        } else {
            address.setSameAsAadhaar(true);
            address.setIsHouseRented(houseRentedorOwn.getCheckedRadioButtonId() == R.id.rb_rent);
            if (aadhaar_address_text.getText() != null)
                address.setAadhaarAddress(aadhaar_address_text.getText().toString());
            //address.setAddress(aadhaar_address_text.getText().toString());
        }
        return address;
    }

    @Override
    public boolean isFormValid() {
        AddressService.Address addressDetail = getDataFromForm(null);
        if (!addressDetail.isSameAsAadhaar()) {
            return super.isFormValid();
        } else {
            if (aadhaar_address_text.getText() != null
                    && aadhaar_address_text.getText().toString().trim().length() > 0
                    && houseRentedorOwn.getCheckedRadioButtonId() != -1) {
                return true;
            } else {
                aadhaar_address_text.getEditText().setError(Application.getInstance().getResources().getString(R.string.enter_aadhar_address));
            }
        }
        return false;
    }

}