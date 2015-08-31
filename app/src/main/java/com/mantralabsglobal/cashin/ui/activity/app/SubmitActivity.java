package com.mantralabsglobal.cashin.ui.activity.app;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.fragment.tabs.TransUnionFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SubmitActivity extends BaseActivity {

    @InjectView(R.id.ask_user_amount)
    ViewGroup askUserView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        setTitle(R.string.app_name);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        ButterKnife.inject(this);
        Button loanApply = (Button)findViewById(R.id.apply_loan_button);
        loanApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransUnionFragment transUnion = new TransUnionFragment();
                FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
                transact.add(R.id.container_id, transUnion);
                askUserView.setVisibility(View.GONE);
                transact.commit();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
