package com.mantralabsglobal.cashin.ui.activity.app;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.fragment.adapter.SubmitDetailsAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SubmitDetailsActivity extends BaseActivity {

    private Toolbar toolbar;
    private SubmitDetailsAdapter submitDetailsAdapter;

    @InjectView(R.id.container_id)
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        setTitle(R.string.app_name);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        ButterKnife.inject(this);
        submitDetailsAdapter = new SubmitDetailsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(submitDetailsAdapter);
      /*  loanApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCreditLineBankFragment transUnion = new GetCreditLineBankFragment();
                FragmentTransaction transact = getSupportFragmentManager().beginTransaction();
                transact.add(R.id.container_id, transUnion);
                askUserView.setVisibility(View.GONE);
                transact.commit();
            }
        });*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

}
