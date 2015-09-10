package com.mantralabsglobal.cashin.ui.fragment.tabs;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AddressService;

import retrofit.Callback;

/**
 * Created by pk on 13/06/2015.
 */
public class CurrentAddressFragment extends AddressFragment{

       @Override
    protected void onUpdate(AddressService.Address updatedData, Callback<AddressService.Address> saveCallback) {
        getAddressService().updateCurrentAddress(updatedData, saveCallback);
           getAddressService().getNextDetail(saveCallback);
    }

    @Override
    protected void onCreate(AddressService.Address updatedData, Callback<AddressService.Address> saveCallback) {
        getAddressService().createCurrentAddress(updatedData, saveCallback);
        getAddressService().getNextDetail(saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<AddressService.Address> dataCallback) {
        getAddressService().getCurrentAddress(dataCallback);
    }

    @Override
    protected String getAddressLabel() {
        return getString(R.string.residence);
    }
}
