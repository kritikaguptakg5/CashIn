package com.mantralabsglobal.cashin.ui.activity.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.mantralabsglobal.cashin.BuildConfig;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.event.ProfileUpdateEvent;
import com.mantralabsglobal.cashin.service.ProfileService;
import com.mantralabsglobal.cashin.service.RestClient;
import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.adapter.MainFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends BaseActivity  {

    private static final String SELECTED_TAB_INDEX = "MAINACTIVITY_SELECTED_TAB_INDEX";

    @InjectView(R.id.yourIdentityButton)
    public Button yourIdentityButton;
    @InjectView(R.id.yourPhotoButton)
    public Button yourPhotoButton;
    @InjectView(R.id.workButton)
    public Button workButton;
    @InjectView(R.id.submit_button)
    Button submitButton;
    @InjectView(R.id.financialButton)
    public Button financialButton;
    @InjectView(R.id.socialButton)
    public Button socialButton;
    @InjectView(R.id.main_frame)
    public ViewPager viewPager;


    private MainFragmentAdapter mainFragmentAdapter;
    private Toolbar toolbar;

    private List<Button> buttonList = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isUserLoggedIn()) {
            setContentView(R.layout.activity_main);
            setTitle(R.string.title_activity_main);
            toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
            setSupportActionBar(toolbar);

            ButterKnife.inject(this);

            EventBus.getDefault().register(this);
            buttonList.add(yourPhotoButton);
            buttonList.add(yourIdentityButton);
            buttonList.add(workButton);
            buttonList.add(financialButton);
            buttonList.add(socialButton);

            checkNetworkAvailability(new SimpleNetworkResultHandler() {
                @Override
                protected void onNetworkAvailable() {
                    viewPager.addOnPageChangeListener(pageChangeListener);
                    mainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager(), MainActivity.this);
                    viewPager.setAdapter(mainFragmentAdapter);
                    viewPager.setCurrentItem(0, false);
                    pageChangeListener.onPageSelected(0);
                    //To check for the first time app is loading
                    EventBus.getDefault().post(new ProfileUpdateEvent());
                }

                @Override
                protected void onNetworkUnavailable() {
                    final SimpleNetworkResultHandler handler = this;
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.error_title)
                            .setMessage(R.string.no_internet_connection_error_message)
                            .setPositiveButton(R.string.retry_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    checkNetworkAvailability(handler);
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
            });
        }

    }

    @OnClick(R.id.submit_button)
    public void submitButton(){
        Intent intent = new Intent(MainActivity.this ,SubmitDetailsActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getCashInApplication().putInAppPreference(SELECTED_TAB_INDEX, ((ViewPager) findViewById(R.id.main_frame)).getCurrentItem());
    }

    private ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        public void onPageSelected(int position) {
            Button v = buttonList.get(position);
            if (v == yourPhotoButton)
                yourPhotoButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_photoselected, 0, 0);
            else
                yourPhotoButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_photo, 0, 0);

            if (v == yourIdentityButton)
                yourIdentityButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_identityselected, 0, 0);
            else
                yourIdentityButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_identity, 0, 0);

            if (v == workButton)
                workButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_workselected, 0, 0);
            else
                workButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_work, 0, 0);

            if (v == financialButton)
                financialButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_financialselected, 0, 0);
            else
                financialButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_financial, 0, 0);

            if (v == socialButton)
                socialButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_socialselected, 0, 0);
            else
                socialButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_social, 0, 0);

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state == ViewPager.SCROLL_STATE_IDLE) {
               InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
                }
            }
        }
    };

    public void nextTab()
    {
        int currentIndex = viewPager.getCurrentItem();
        AbstractPager currentPager = (AbstractPager) mainFragmentAdapter.getItem(currentIndex);
        if(!currentPager.nextTab() && mainFragmentAdapter.getCount()-1>currentIndex)
        {
            currentIndex++;
            viewPager.setCurrentItem(currentIndex, true);
            currentPager = (AbstractPager) mainFragmentAdapter.getItem(currentIndex);
            currentPager.moveToFirstTab();
        }
    }

    public void previousTab()
    {
        int currentIndex = viewPager.getCurrentItem();
        AbstractPager currentPager = (AbstractPager) mainFragmentAdapter.getItem(currentIndex);
        if(!currentPager.previousTab() && currentIndex > 0)
        {
            currentIndex--;
            viewPager.setCurrentItem(currentIndex, true);
            currentPager = (AbstractPager) mainFragmentAdapter.getItem(currentIndex);
            currentPager.moveToLastTab();
        }
    }

    @OnClick({R.id.yourPhotoButton, R.id.yourIdentityButton, R.id.workButton, R.id.financialButton, R.id.socialButton})
    public void onClick(final View v) {

        final ViewPager viewPager = ((ViewPager)findViewById(R.id.main_frame));

        final int newIndex = buttonList.indexOf(v);

        viewPager.postDelayed(new Runnable() {

            @Override
            public void run() {
                viewPager.setCurrentItem(newIndex);
            }
        }, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(BuildConfig.DEBUG)
        {
            getMenuInflater().inflate(R.menu.menu_main_debug, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }



    /*@Optional
    @OnClick(R.id.submit_button)
    public void submitButton(){
        Intent intent = new Intent(MainActivity.this ,SubmitActivity.class);
        startActivity(intent);
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_product_tour) {
            Intent intent = new Intent(getBaseContext(), IntroSliderActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        else if (id == R.id.perfios) {
            Intent intent = new Intent(getBaseContext(), PerfiosActivity.class);
            startActivityForResult(intent, 15000);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isUserLoggedIn()
    {
        String userName = getCashInApplication().getAppUser();
        if ("".equals(userName)) {

            Intent intent = new Intent(getBaseContext(), IntroSliderActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mainFragmentAdapter.getItem(viewPager.getCurrentItem()).onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void onEvent(ProfileUpdateEvent profileUpdatedEvent) {
        RestClient.getInstance().getProfileService().getUserDataCompleteDetail(new Callback<ProfileService.UserDataComplete>() {
            @Override
            public void success(ProfileService.UserDataComplete userDataComplete, Response response) {
                if (userDataComplete != null && userDataComplete.isDataComplete()) {
                    submitButton.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("ProfileDetail Info", "Failed to retrieve profile completion detail");
            }
        });
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
