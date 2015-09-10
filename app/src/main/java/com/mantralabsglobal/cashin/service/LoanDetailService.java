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

        int creditAmount;
        List<LoanTenure> EMITable;
        String bankName;
        String account_no;
        int amountToBeTransferred;

        public int getAmountToBeTransferred() {
            return amountToBeTransferred;
        }

        public void setAmountToBeTransferred(int amountToBeTransferred) {
            this.amountToBeTransferred = amountToBeTransferred;
        }

        public int getCreditAmount() {
            return creditAmount;
        }

        public void setCreditAmount(int creditAmount) {
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
        String Tenure;
        int Interest;
        double EMI;

        public String getTenure() {
            return Tenure;
        }

        public void setTenure(String tenure) {
            Tenure = tenure;
        }

        public int getInterest() {
            return Interest;
        }

        public void setInterest(int interest) {
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