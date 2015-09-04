package com.mantralabsglobal.cashin.service;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface NetBankingService {

    @GET("/user/netBankingperfios")
    void getNetBankingDetail(Callback<NetBanking> callback);

    @GET("/user/next?type=obj")
    void getNextDetail( Callback<NetBanking> callback);

    @POST("/user/netBankingperfios")
    void createNetBankingService(@Body NetBanking emiDetail, Callback<NetBanking> callback);

    public static class NetBanking{

        int status = -1;
        String message ;
        private boolean isDataComplete;

        public boolean getIsDataComplete() {
            return isDataComplete;
        }

        public void setIsDataComplete(boolean isDataComplete) {
            this.isDataComplete = isDataComplete;
        }
        public NetBanking() {
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
