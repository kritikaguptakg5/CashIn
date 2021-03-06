package com.mantralabsglobal.cashin.ui.activity.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.event.InternetChangeListenerEvent;
import com.mantralabsglobal.cashin.service.AuthenticationService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.utils.RetrofitUtils;

import java.net.InetAddress;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 6/26/2015.
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";


    protected ProgressDialog progressDialog;
    SharedPreferences appPreference = null;

    public static final int GOOGLE_PLUS_LOGIN_REQUEST_CODE = 1000;
    public static final int MAIN_ACTIVITY_REQUEST_CODE = 2000;
    public static final int SELECT_PHOTO_FROM_GALLERY = 3000;
    public static final int SELFIE_CAPTURE = 4000;
    public static final int CROP_SELFIE = 5000;
    public static final int IMAGE_CAPTURE_PAN_CARD = 6000;
    public static final int IMAGE_CROP_PAN_CARD = 6001;
    public static final int IMAGE_CAPTURE_BUSINESS_CARD = 7000;
    public static final int IMAGE_CROP_BUSINESS_CARD = 7001;
    public static final int LINKEDIN_SIGNIN = 8000;
    public static final int FACEBOOK_SIGNIN = 9000;
    public static final int REQ_SIGN_IN_REQUIRED = 10000;
    public static final int CONTACT_PICKER = 11000;
    public static final int SEND_REFERRAL_MESSAGE = 12000;
    public static final int PICK_ACCOUNT_REQUEST = 13000;
    public static final int PERFIOS_NET_BANKING = 14000;
    public static final int AADHAR_NUMBER_LENGTH = 12;
    public static final int PAN_NUMBER_LENGTH = 10;
    public static final String NAME_VALIDATION = "[a-zA-Z]+";
    public static final String PAN_NUMBER_VALIDATION = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

    protected void putInAppPreference(String key, String value) {
        appPreference.edit().putString(key, value).apply();
    }

    protected Application getCashInApplication() {
        return (Application) getApplication();
    }

    public String getUserName() {
        return getCashInApplication().getAppUser();
    }

    public String getUserId() {
        return getCashInApplication().getAppUserId();
    }


    protected void registerAndLogin(final String userName, final String token, boolean userExists, final IAuthListener listener) {
        AuthenticationService authService = ((com.mantralabsglobal.cashin.ui.Application) getApplication()).getRestClient().getAuthenticationService();
        AuthenticationService.UserPrincipal up = new AuthenticationService.UserPrincipal();
        up.setEmail(userName);
        up.setToken(token);
        if (userExists) {

            authService.authenticateUser(up, new Callback<AuthenticationService.AuthenticatedUser>() {
                @Override
                public void success(AuthenticationService.AuthenticatedUser authenticatedUser, Response response) {
                    getCashInApplication().setAppUserId(authenticatedUser.getId());
                    getCashInApplication().setAppUserName(authenticatedUser.getEmail());
                    listener.onSuccess();
                }

                @Override
                public void failure(RetrofitError error) {
                    if (RetrofitUtils.isUserNotRegisteredError(error))
                        registerAndLogin(userName, token, false, listener);
                    else {
                        showToastOnUIThread(error.getMessage());
                        listener.onFailure(error);
                    }
                }
            });
        } else {
            AuthenticationService.NewUser nu = new AuthenticationService.NewUser(up.getEmail(), "");

            authService.registerUser(nu, new Callback<AuthenticationService.AuthenticatedUser>() {
                @Override
                public void success(AuthenticationService.AuthenticatedUser authenticatedUser, Response response) {
                    registerAndLogin(userName, token, true, listener);
                }

                @Override
                public void failure(RetrofitError error) {
                    showToastOnUIThread(error.getMessage());
                    listener.onFailure(error);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPreference = getCashInApplication().getAppPreference();
        progressDialog = new ProgressDialog(this);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        Tracker t = Application.getInstance().getAppTracker();
        t.setScreenName(getTitle() != null ? getTitle().toString() : this.getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public void onEvent(final InternetChangeListenerEvent internetChangeListener) {
        checkInternetConnectionDialog();
    }

    public void checkInternetConnectionDialog() {

        boolean isConnected = isNetConnected();

        if (!isConnected) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.error_title)
                    .setMessage(R.string.no_internet_connection_error_message)
                    .setPositiveButton(R.string.retry_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkInternetConnectionDialog();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
        }
    }

    public boolean isNetConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    protected void showProgressDialog(String title, String message, boolean indeterminate, boolean cancelable) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(indeterminate);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        progressDialog.dismiss();
    }

    protected void showToastOnUIThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected interface ServerResponseListener {
        void onSuccess();

        void onError(RetrofitError error);
    }

    public interface IAuthListener {
        void onSuccess();

        void onFailure(Exception exp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
