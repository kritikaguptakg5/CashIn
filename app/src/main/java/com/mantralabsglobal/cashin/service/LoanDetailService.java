package com.mantralabsglobal.cashin.service;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface LoanDetailService {

    /*  @PUT("/user/alldetail")
    void updateUserAmountWantedService(@Body GetUserLoan address, Callback<GetUserLoan> callback); */

    @POST("/user/alldetail")
    void createLoanDetail(@Body LoanDetail loanDetail, Callback<LoanDetail> callback);

    @GET("/user/loan")
    void getLoanDetail(Callback<LoanDetail> callback);

    public static class LoanDetail {

        double creditAmount;
        double remainingAmount;
        List<LoanTenure> EMITable;
        String bankName;
        String account_no;
        double amountToBeTransferred;


        public double getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(double remainingAmount) {
            this.remainingAmount = remainingAmount;
        }

        public double getAmountToBeTransferred() {
            return amountToBeTransferred;
        }

        public void setAmountToBeTransferred(double amountToBeTransferred) {
            this.amountToBeTransferred = amountToBeTransferred;
        }

        public double getCreditAmount() {
            return creditAmount;
        }

        public void setCreditAmount(double creditAmount) {
            this.creditAmount = creditAmount;
        }

        public List<LoanTenure> getEMITable() {
            return EMITable;
        }

        public void setEMITable(List<LoanTenure> EMITable) {
            this.EMITable = EMITable;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAccount_no() {
            return account_no;
        }

        public void setAccount_no(String account_no) {
            this.account_no = account_no;
        }
    }

    public class LoanTenure{
        int Tenure;
        double Interest;
        double EMI;

        public int getTenure() {
            return Tenure;
        }

        public void setTenure(int tenure) {
            Tenure = tenure;
        }

        public double getInterest() {
            return Interest;
        }

        public void setInterest(double interest) {
            Interest = interest;
        }

        public double getEMI() {
            return EMI;
        }

        public void setEMI(double EMI) {
            this.EMI = EMI;
        }
    }
}