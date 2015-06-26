package com.mantralabsglobal.cashin.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by pk on 6/26/2015.
 */
public class BaseActivity extends FragmentActivity {

    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
    }

    protected void showProgressDialog(String title, String message, boolean indeterminate, boolean cancelable)
    {
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(indeterminate);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
    }

    protected void hideProgressDialog()
    {
        progressDialog.dismiss();
    }
}
