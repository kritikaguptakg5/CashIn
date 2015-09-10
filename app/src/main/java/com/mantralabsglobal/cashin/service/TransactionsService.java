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

        String remainingAmount;
        List<TransactionDescriptionMessage> transactionMessages;

        public String getRemainingAmount() {
            return remainingAmount;
        }

        public void setRemainingAmount(String remainingAmount) {
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
        String transactionDescription;

        public String getTransactionDescription() {
            return transactionDescription;
        }

        public void setTransactionDescription(String transactionDescription) {
            this.transactionDescription = transactionDescription;
        }
    }
}