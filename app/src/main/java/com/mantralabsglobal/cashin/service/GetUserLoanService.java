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

    /*  @PUT("/user/alldetail")
    void updateUserAmountWantedService(@Body GetUserLoan address, Callback<GetUserLoan> callback); */

    @POST("/user/alldetail")
    void createUserAmountWantedService(@Body GetUserLoan getUserLoan, Callback<GetUserLoan> callback);

   /*   @GET("/user/alldetail")
    void getUserAmountWantedService(Callback<GetUserLoan> callback);*/

    public static class GetUserLoan {

        double userAsk;

        public double getUserAsk() {
            return userAsk;
        }

        public void setUserAsk(double userAsk) {
            this.userAsk = userAsk;
        }
    }
}