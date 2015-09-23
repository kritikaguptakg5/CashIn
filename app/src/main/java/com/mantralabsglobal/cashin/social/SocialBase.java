package com.mantralabsglobal.cashin.social;

import android.content.Context;
import android.util.Log;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pk on 7/4/2015.
 */
public abstract class SocialBase<T> {

    public abstract OAuthService getOAuthService(Context context);

    protected abstract String getProfileUrl();

    public T getSocialProfile(Context context, Token accessToken)
    {
        OAuthRequest request = new OAuthRequest(Verb.GET, getProfileUrl());
        getOAuthService(context).signRequest(accessToken, request);
        Response response = request.send();

        return getProfileFromResponse(response.getBody());
    }

    protected String changeDateFormat(String fromDate) {
        SimpleDateFormat fromUser = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = null;
        try {
            Date date = fromUser.parse(fromDate);
            formattedDate = myFormat.format(date);
        } catch (ParseException e) {
            Log.d("Date parse exception",e.getMessage());
        }
    return formattedDate;
    }

    protected abstract T getProfileFromResponse(String responseBody);

    public Token getAccessToken(String token, String secret){
        return new Token(token, secret);
    }

    public Token getRequestToken(OAuthService service)
    {
        return service.getRequestToken();
    }

    public abstract String getCallBackUrl();

    public abstract String getVerifierCode(String url1);

    public static interface SocialListener<T>
    {
        void onSuccess(T t);
        void onFailure(String message);
    }

}
