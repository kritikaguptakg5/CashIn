package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;
import com.mantralabsglobal.cashin.businessobjects.AadhaarDetail;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by pk on 6/25/2015.
 */
public interface AadhaarService {

    @GET("/user/aadhar")
    void getAadhaarDetail( Callback<AadhaarDetail> callback);

    @POST("/user/aadhar")
    void createAadhaarDetail(@Body AadhaarDetail aadhaar, Callback<AadhaarDetail> callback);

    @PUT("/user/aadhar")
    void updateAadhaarDetail(@Body AadhaarDetail aadhaar, Callback<AadhaarDetail> callback);


    public static class AadhaarDetail{

        private String name;
        private String address;
        private String gender;
        @SerializedName(value="aadhar_no")
        private String aadhaarNumber;
        @SerializedName(value = "sonOf")
        private String sonOf;
        private String dob;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getAadhaarNumber() {
            return aadhaarNumber;
        }

        public void setAadhaarNumber(String aadhaarNumber) {
            this.aadhaarNumber = aadhaarNumber;
        }

        public String getSonOf() {
            return sonOf;
        }

        public void setSonOf(String sonOf) {
            this.sonOf = sonOf;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }
    }

}
