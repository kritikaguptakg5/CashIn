package com.mantralabsglobal.cashin.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;

import com.mantralabsglobal.cashin.service.AddressService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by pk on 6/24/2015.
 */
public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static final String STREET = "STREET";
    public static final String CITY = "CITY";
    public static final String STATE = "STATE";
    public static final String PINCODE = "PINCODE";
    public static final String ERROR = "ERROR";
    public static final String POSTAL_CODE_PATTERN = "([0-9]{6}|[0-9]{3}\\s[0-9]{3})";


    public void getCurrentAddress(final Context context, final AddressListener listener) {
        AppLocationService appLocationService = new AppLocationService(context);
        Message message = Message.obtain();
        final Location location = getLocation(appLocationService);

        Thread t = new Thread()
        {
            public void run()
            {
                Address address = null;
                try {
                    if(location != null) {
                        address = getAddressFromLocation(location.getLatitude(), location.getLongitude(), context);
                    }
                    else
                    {
                        listener.onError(new RuntimeException("Failed to get GPS Location"));
                        return;
                    }
                } catch (IOException e) {
                    listener.onError(e);
                    return;
                }

                AddressService.Address myAddress = new AddressService.Address();

                String city="unknown";
                if (address.getLocality() != null)
                    city=address.getLocality();
                else if (address.getSubAdminArea() != null)
                    city=address.getSubAdminArea();
                myAddress.setCity(city);

                myAddress.setState(address.getAdminArea());


                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (i = 0; i < address.getMaxAddressLineIndex(); i++) {

                    String addressLne =  address.getAddressLine(i);
                    if( addressLne != null && addressLne.length()>0 && myAddress.getCity() != null && addressLne.indexOf(myAddress.getCity())>=0) {
                        break;
                    }
                    else {
                        if(sb.length()>0)
                            sb.append(", ");
                        sb.append(addressLne);
                    }
                }

                if(!TextUtils.isEmpty(address.getPostalCode()))
                    myAddress.setPincode(address.getPostalCode());
                else
                {
                    myAddress.setPincode(findPostalCode(address.getAddressLine(i)));
                }

                myAddress.setStreet(sb.toString());

                listener.onAddressAquired(myAddress);
            }
        };

        t.start();

    }

    private String findPostalCode(String... lines){
        // Create a Pattern object
        final Pattern postalCodePattern = Pattern.compile(POSTAL_CODE_PATTERN);
        for(String line: lines) {
            Matcher matcher = postalCodePattern.matcher(line);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    private Location getLocation(AppLocationService appLocationService)
    {
        Location location = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
        if(location == null)
            location = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }
    private Address getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context) throws IOException {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Bundle bundle = new Bundle();
        Address address = null;

        List<Address> addressList = geocoder.getFromLocation(
                latitude, longitude, 1);
        if (addressList != null && addressList.size() > 0)
            address = addressList.get(0);

       return address;

    }

    public interface AddressListener{
        public void onAddressAquired(AddressService.Address address);
        public void onError(Throwable error);
    }

}