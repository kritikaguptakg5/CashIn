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
public interface TransUnionService {

    @POST("/user/alldetail")
    void createTransUnionsService(@Body TransUnion address, Callback<TransUnion> callback);

    @PUT("/user/alldetail")
    void updateTrnasUnionService(@Body TransUnion address, Callback<TransUnion> callback);

    @GET("/user/tuscorecard")
    void getTransUnionService(Callback<TransUnion> callback);

    public static class TransUnion {

        int LoanApproves;
        int status = -1;
        List<InterestSlabs> InterestSlabs;
        String message;

        public List<TransUnionService.InterestSlabs> getInterestSlabs() {
            return InterestSlabs;
        }

        public void setInterestSlabs(List<TransUnionService.InterestSlabs> interestSlabs) {
            InterestSlabs = interestSlabs;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getLoanApproves() {
            return LoanApproves;
        }

        public void setLoanApproves(int loanApproves) {
            LoanApproves = loanApproves;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public class InterestSlabs {
        String amount;
        double twelve_month_interest;
        double twentyfour_month_interest;
        double thirtysix_month_interest;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public double getTwelve_month_interest() {
            return twelve_month_interest;
        }

        public void setTwelve_month_interest(double twelve_month_interest) {
            this.twelve_month_interest = twelve_month_interest;
        }

        public double getTwentyfour_month_interest() {
            return twentyfour_month_interest;
        }

        public void setTwentyfour_month_interest(double twentyfour_month_interest) {
            this.twentyfour_month_interest = twentyfour_month_interest;
        }

        public double getThirtysix_month_interest() {
            return thirtysix_month_interest;
        }

        public void setThirtysix_month_interest(double thirtysix_month_interest) {
            this.thirtysix_month_interest = thirtysix_month_interest;
        }
    }
}