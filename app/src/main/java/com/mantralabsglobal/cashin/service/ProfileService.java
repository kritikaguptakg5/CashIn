package com.mantralabsglobal.cashin.service;

import com.google.gson.annotations.SerializedName;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by pk on 6/25/2015.
 */
public interface ProfileService {

    @GET("/user/next")
    void getUserDataCompleteDetail(Callback<UserDataComplete> callback);

    public static class UserDataComplete{

        private boolean isDataComplete;
        private double completePercent;

        public double getCompletePercent() {
            return completePercent;
        }

        public void setCompletePercent(double completePercent) {
            this.completePercent = completePercent;
        }

        public boolean isDataComplete() {
            return isDataComplete;
        }

        public void setIsDataComplete(boolean isDataComplete) {
            this.isDataComplete = isDataComplete;
        }
    }

}
