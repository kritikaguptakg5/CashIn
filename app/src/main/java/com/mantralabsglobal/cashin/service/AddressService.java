package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by pk on 6/25/2015.
 */
public interface AddressService {

    @GET("/user/address/current")
    void getCurrentAddress(Callback<Address> callback);

    @POST("/user/address/current")
    void createCurrentAddress(@Body Address address ,Callback<Address> callback);

    @PUT("/user/address/current")
    void updateCurrentAddress(@Body Address address ,Callback<Address> callback);

    @GET("/user/address/permanent")
    void getPermanentAddress(Callback<Address> callback);

    @POST("/user/address/permanent")
    void createPermanentAddress(@Body Address address ,Callback<Address> callback);

    @PUT("/user/address/permanent")
    void updatePermanentAddress(@Body Address address ,Callback<Address> callback);


    public static class Address{

        private boolean sameAsAadhaar;
        private String street;
        private String city;
        private String state;
        private String pincode;
        private boolean isHouseRented;
        private String type;
        @SerializedName(value = "address")
        private String aadhaarAddress;

        public String getAadhaarAddress() {
            return aadhaarAddress;
        }

        public void setAadhaarAddress(String aadhaarAddress) {
            this.aadhaarAddress = aadhaarAddress;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public boolean isHouseRented() {
            return isHouseRented;
        }

        public void setIsHouseRented(boolean isHouseRented) {
            this.isHouseRented = isHouseRented;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isSameAsAadhaar() {
            return sameAsAadhaar;
        }

        public void setSameAsAadhaar(boolean sameAsAadhaar) {
            this.sameAsAadhaar = sameAsAadhaar;
        }
    }


}
