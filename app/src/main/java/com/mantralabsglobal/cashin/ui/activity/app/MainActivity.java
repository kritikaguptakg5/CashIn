package com.mantralabsglobal.cashin.ui.activity.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.BuildConfig;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.activity.camera.CwacCameraActivity;
import com.mantralabsglobal.cashin.ui.fragment.AbstractPager;
import com.mantralabsglobal.cashin.ui.fragment.adapter.MainFragmentAdapter;
import com.mantralabsglobal.cashin.utils.SMSProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends BaseActivity  {

    private static final String SELECTED_TAB_INDEX = "MAINACTIVITY_SELECTED_TAB_INDEX";

    @InjectView(R.id.yourIdentityButton)
    public Button yourIdentityButton;
    @InjectView(R.id.yourPhotoButton)
    public Button yourPhotoButton;
    @InjectView(R.id.workButton)
    public Button workButton;
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
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_activity_main);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        ButterKnife.inject(this);

        buttonList.add(yourPhotoButton);
        buttonList.add(yourIdentityButton);
        buttonList.add(workButton);
        buttonList.add(financialButton);
        buttonList.add(socialButton);

        checkUserName();

        viewPager.addOnPageChangeListener(pageChangeListener);
        mainFragmentAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainFragmentAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setCurrentItem(appPreference.getInt(SELECTED_TAB_INDEX, 0), false);
        pageChangeListener.onPageSelected(appPreference.getInt(SELECTED_TAB_INDEX, 0));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_product_tour) {
            Intent intent = new Intent(getBaseContext(), IntroSliderActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
      /* if(id == R.id.action_scan_sms)
        {
            SMSProvider smsProvider = new SMSProvider(this);
            List<SMSProvider.SMSMessage> messageList = smsProvider.getTransactionList(Long.MIN_VALUE);
            openSMSDialog(messageList);
        }

        if(id == R.id.action_package_hash){
            showPckageHash();
        }
        if(id == R.id.action_launch_camera)
        {
            Intent intent = new Intent(getBaseContext(), CwacCameraActivity.class);
            startActivityForResult(intent, 237);
            //finish();
        }*/
        return super.onOptionsItemSelected(item);
    }

   /* protected void showPckageHash(){
        try {
            PackageInfo info =     getPackageManager().getPackageInfo("com.mantralabsglobal.cashin",     PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                showToastOnUIThread(sign);
                Log.i("MY KEY HASH:", sign);
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    protected void openSMSDialog(List<SMSProvider.SMSMessage> messageList)
    {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);

        final ListView listview=new ListView(MainActivity.this);
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listview);
        myDialog.setView(layout);
        ArrayAdapter<SMSProvider.SMSMessage> adapter = new ArrayAdapter<SMSProvider.SMSMessage>(this, 0, messageList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the data item for this position
                SMSProvider.SMSMessage smsMessage = getItem(position);
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.smsmessage_row, parent, false);
                }
                // Lookup view for data population
                TextView tvBody = (TextView) convertView.findViewById(R.id.tv_body);
                TextView tvFrom = (TextView) convertView.findViewById(R.id.tv_from);

                tvBody.setText(smsMessage.getBody());
                tvFrom.setText(smsMessage.getAddress());
                return convertView;
            }
        };
        //adapter.addAll(messageList);
        listview.setAdapter(adapter);
        myDialog.show();
    }*/

    protected void checkUserName()
    {
        String userName = getCashInApplication().getAppUser();
        if ("".equals(userName)) {

            Intent intent = new Intent(getBaseContext(), IntroSliderActivity.class);
            startActivity(intent);
            finish();
        }
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

}
