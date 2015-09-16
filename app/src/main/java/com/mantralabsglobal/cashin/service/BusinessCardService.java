package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;
import com.mantralabsglobal.cashin.utils.RetrofitUtils;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by pk on 6/25/2015.
 */
public interface BusinessCardService {

    @GET("/user/businessCard")
    void getBusinessCardDetail(Callback<BusinessCardDetail> callback);

    @POST("/user/businessCard")
    void createBusinessCardDetail(@Body BusinessCardDetail businessCardDetail, Callback<BusinessCardDetail> callback);

    @PUT("/user/businessCard")
    void updateBusinessCardDetail(@Body BusinessCardDetail businessCardDetail, Callback<BusinessCardDetail> callback);

    @POST("/user/businessCard/image")
    void updateBusinessCardImage(@Body OCRServiceProvider.CardImage businessCardImage, Callback<RetrofitUtils.ServerMessage> callback);

    @POST("/user/ocr/businessCard")
    void getBusinessCardDetailFromImage(@Body OCRServiceProvider.CardImage businessCardImage, Callback<BusinessCardDetail> callback);

    public static class BusinessCardDetail{

        @SerializedName("BusinessCardDetail")
        private List<String> contentArr;

        @SerializedName("Address")
        private List<String> addressLines;

        @SerializedName("Email")
        private String email;

        @SerializedName("CompanyName")
        private String employerName;

        @SerializedName("Name")
        private String name;

        @SerializedName("TotalExperience")
        private String workExperience;

        @SerializedName("TypeofEmployement")
        private boolean employementType;

        @SerializedName("Year")
        private int year;
        @SerializedName("Month")
        private int month;

        String joiningDate;

        @SerializedName("cardImageUrl")
        private String businessCardUrl;

        public String getJoiningDate() {
            return joiningDate;
        }

        public void setJoiningDate(String joiningDate) {
            this.joiningDate = joiningDate;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public boolean isEmployementType() {

            return employementType;
        }

        public void setEmployementType(boolean employementType) {
            this.employementType = employementType;
        }

        public String getWorkExperience() {
            return workExperience;
        }

        public void setWorkExperience(String workExperience) {
            this.workExperience = workExperience;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public List<String> getaddressLines() {
            return addressLines;
        }

        public void setAddress(List<String> addressLines) {
            this.addressLines = addressLines;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmployerName() {
            return employerName;
        }

        public void setEmployerName(String employerName) {
            this.employerName = employerName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getContentArr() {
            return contentArr;
        }

        public void setContentArr(List<String> contentArr) {
            this.contentArr = contentArr;
        }

        public String getBusinessCardUrl() {
            return businessCardUrl;
        }

        public void setBusinessCardUrl(String businessCardUrl) {
            this.businessCardUrl = businessCardUrl;
        }
    }

}
