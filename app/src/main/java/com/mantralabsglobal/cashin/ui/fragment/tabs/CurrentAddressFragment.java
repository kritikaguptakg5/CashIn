package com.mantralabsglobal.cashin.ui.fragment.tabs;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AddressService;

import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class CurrentAddressFragment extends AddressFragment{


    @Override
    protected void getAddressFromServer(Callback<AddressService.Address> serverCallback) {
        getAddressService().getCurrentAddress(serverCallback);
    }

    @Override
    public String getProgressDialogSaveText() {
        return getString(R.string.saving_current_address);
    }

    @Override
    public void createAddress(AddressService.Address address, Callback<AddressService.Address> callback) {
        getAddressService().createCurrentAddress(address, callback);
    }

    @Override
    public void updateAddress(AddressService.Address address, Callback<AddressService.Address> callback) {
        getAddressService().updateCurrentAddress(address, callback);
    }
}
