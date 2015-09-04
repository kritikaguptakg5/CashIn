package com.mantralabsglobal.cashin.service;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by pk on 6/25/2015.
 */
public interface GetUserLoanService {

    @POST("/user/alldetail")
    void updateUserAmountWantedService(@Body GetUserLoan address, Callback<GetUserLoan> callback);

    @PUT("/user/alldetail")
    void createUserAmountWantedService(@Body GetUserLoan address, Callback<GetUserLoan> callback);

    @GET("/user/alldetail")
    void getUserAmountWantedService(Callback<GetUserLoan> callback);

    public static class GetUserLoan {

        int userAsk;

        public int getUserAsk() {
            return userAsk;
        }

        public void setUserAsk(int userAsk) {
            this.userAsk = userAsk;
        }
    }
}