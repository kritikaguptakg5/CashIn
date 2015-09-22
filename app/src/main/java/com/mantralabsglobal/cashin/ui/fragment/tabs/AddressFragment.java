package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AadhaarService;
import com.mantralabsglobal.cashin.service.AddressService;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.utils.LocationAddress;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 6/30/2015.
 */
public abstract class AddressFragment extends BaseBindableFragment<AddressService.Address> {

    @InjectView(R.id.cc_street)
    CustomEditText cc_street;

    @InjectView(R.id.cc_pincode)
    CustomEditText cc_pincode;

    @InjectView(R.id.cc_city)
    CustomEditText cc_city;

    @InjectView(R.id.cc_state)
    CustomEditText cc_state;

    /* @InjectView(R.id.cs_owned_by)
     CustomSpinner cs_own;*/
    @InjectView(R.id.btn_edit_address)
    Button btn_editAddress;

    @InjectView(R.id.aadhaar_address_text)
    EditText aadhaar_address_text;

    @InjectView(R.id.ib_get_gps_location)
    ImageButton ib_get_gps_location;

    @InjectView(R.id.rb_rent)
    RadioButton rb_rented;

    @InjectView(R.id.rb_own)
    RadioButton rb_own;

    /*@InjectView(R.id.fab_get_loc_from_gps)
    FloatingActionButton btnGetLocationFromGPS;*/

    @InjectView(R.id.vg_address_form)
    ViewGroup vg_addressForm;

    @InjectView(R.id.aadhaar_address_layout)
    ViewGroup vg_aadhaar_address_layout;

//    @InjectView(R.id.vg_gps_launcher)
//    ViewGroup vg_gpsLauncher;

    CheckBox addrSameAsAadhaar;

    private AddressService mAddressService;

    //private AddressService.Address addressOnServer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);

        addrSameAsAadhaar = (CheckBox) view.findViewById(R.id.chkAadhaar);
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

        String addressLabel = getAddressLabel();
        if (!TextUtils.isEmpty(addressLabel)) {
            TextView addressLabal = (TextView) view.findViewById(R.id.adressTextView);
            addressLabal.setText(addressLabel);
        }

        return view;
    }

    public void getAadhaarAddressFromAadhaar() {
        RestClient.getInstance().getAadhaarService().getAadhaarDetail(new Callback<AadhaarService.AadhaarDetail>() {
            @Override
            public void success(AadhaarService.AadhaarDetail aadhaarDetail, Response response) {
                if (aadhaarDetail != null)
                    aadhaar_address_text.setText(aadhaarDetail.getAddress());
                else
                    showToastOnUIThread("Please first enter your aadhaar detail");
            }

            @Override
            public void failure(RetrofitError error) {
                //   showToastOnUIThread("Please first enter your aadhaar detail");
            }
        });
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

        if (addrSameAsAadhaar.isChecked()) {
            registerChildView(vg_aadhaar_address_layout, View.VISIBLE);
            registerChildView(vg_addressForm, View.GONE);
        } else {
            registerChildView(vg_aadhaar_address_layout, View.GONE);
            registerChildView(vg_addressForm, View.VISIBLE);
        }
        // registerChildView(vg_gpsLauncher, View.VISIBLE);
        //registerFloatingActionButton(btnGetLocationFromGPS, vg_addressForm);


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
                        showToastOnUIThread(getString(R.string.gps_failed) + error.getMessage());
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
                if (aadhaar_address_text.getText() == null || aadhaar_address_text.getText().toString().trim().length() < 1) {
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
                            rb_rented.setChecked(address.isHouseRented());
                            rb_own.setChecked(!address.isHouseRented());
                            // cs_own.getSpinner().setSelection(((ArrayAdapter<String>) cs_own.getAdapter()).getPosition(address.getOwn()));
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
            address.setIsHouseRented(rb_rented.isChecked());
            //address.setOwn(cs_own.getSpinner().getSelectedItem().toString());
        } else {
            address.setSameAsAadhaar(true);
            //address.setAddress(aadhaar_address_text.getText().toString());
        }
        return address;
    }

    @Override
    public boolean isFormValid() {
        AddressService.Address addressDetail = getDataFromForm(null);
        if( addressDetail.isSameAsAadhaar() ||
                (
                        !TextUtils.isEmpty(addressDetail.getStreet())
                                && !TextUtils.isEmpty(addressDetail.getCity())
                                && !TextUtils.isEmpty(addressDetail.getState())
                                && !TextUtils.isEmpty(addressDetail.getPincode())
                                && TextUtils.isDigitsOnly(addressDetail.getPincode()))){
            return true;
        }
        else {
            showToastOnUIThread("Please enter valid address information");
            return false;
        }

    }

}