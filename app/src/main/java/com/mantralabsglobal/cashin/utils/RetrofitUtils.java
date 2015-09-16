package com.mantralabsglobal.cashin.utils;

import com.google.gson.Gson;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

/**
 * Created by pk on 7/3/2015.
 */
public class RetrofitUtils {

    public static String USER_NOT_REGISTERED_ERROR =  "Given email does not exist";

    public static boolean isUserNotRegisteredError(RetrofitError error)
    {
        return USER_NOT_REGISTERED_ERROR.equalsIgnoreCase(getErrorMessage(error).message);
    }

    public static boolean isDataNotOnServerError(RetrofitError error){
        if(error != null && error.getResponse() != null) {
            return error.getResponse().getStatus() == 404;
        }
        return false;
    }

    public static ServerMessage getErrorMessage(RetrofitError error)
    {
        if(error.getResponse() != null) {
            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
            Gson gson = new Gson();
            return gson.fromJson(json, ServerMessage.class);
        }
        else {
            ServerMessage message = new ServerMessage();
            message.setMessage(error.getMessage());
            return message;
        }
    }

    public static class ServerMessage {
        public String getMessage() {
            return message;
        }

        private String message;

        public void setMessage(String message) {
            this.message = message;
        }
    }

}
