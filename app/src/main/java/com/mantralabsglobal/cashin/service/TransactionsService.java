package com.mantralabsglobal.cashin.service;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pk on 6/25/2015.
 */
public interface TransactionsService {

   /* @POST("/user/transactions")
    void createTransactions(@Body Transactions getTransactions, Callback<Transactions> callback);*/

    @GET("/user/transaction")
    void getTransactions(Callback<TransactionsService.Transactions> callback);

    public static class Transactions {

        int remainingAmount;
        List<TransactionDescriptionMessage> transactionMessages;

        public int getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(int remainingAmount) {
            this.remainingAmount = remainingAmount;
        }

        public List<TransactionDescriptionMessage> getTransactionMessages() {
            return transactionMessages;
        }

        public void setTransactionMessages(List<TransactionDescriptionMessage> transactionMessages) {
            this.transactionMessages = transactionMessages;
        }
    }

    public class TransactionDescriptionMessage {

        double amountTransacted;
        double remainingAmount;
        String transactionMode;
        String transactionType;
        String transactionDate;
        String bankName;

        public String getTransactionMode() {
            return transactionMode;
        }

        public void setTransactionMode(String transactionMode) {
            this.transactionMode = transactionMode;
        }

        public double getAmountTransacted() {
            return amountTransacted;
        }

        public void setAmountTransacted(double amountTransacted) {
            this.amountTransacted = amountTransacted;
        }

        public double getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(double remainingAmount) {
            this.remainingAmount = remainingAmount;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }
    }
}