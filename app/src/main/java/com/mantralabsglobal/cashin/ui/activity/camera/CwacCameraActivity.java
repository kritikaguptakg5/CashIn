package com.mantralabsglobal.cashin.ui.activity.camera;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.fragment.camera.CwacCameraFragment;

import java.io.File;

/**
 * Created by pk on 7/9/2015.
 */
public class CwacCameraActivity extends AppCompatActivity implements
        ActionBar.OnNavigationListener, CwacCameraFragment.Contract {
    private static final String STATE_SELECTED_NAVIGATION_ITEM=
            "selected_navigation_item";
    private static final String STATE_SINGLE_SHOT="single_shot";
    private static final String STATE_LOCK_TO_LANDSCAPE=
            "lock_to_landscape";
    private static final int CONTENT_REQUEST=1337;
    private CwacCameraFragment std=null;
    private CwacCameraFragment ffc=null;
    private CwacCameraFragment current=null;
    private boolean hasTwoCameras=(Camera.getNumberOfCameras() > 1);
    private boolean singleShot=true;
    private boolean isLockedToLandscape=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cwac_main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        if (hasTwoCameras) {
            final ActionBar actionBar=getSupportActionBar();

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

            ArrayAdapter<CharSequence> adapter=
                    ArrayAdapter.createFromResource(actionBar.getThemedContext(),
                            R.array.cwac_nav,
                            android.R.layout.simple_list_item_1);

            actionBar.setListNavigationCallbacks(adapter, this);
        }
        else {
            current= CwacCameraFragment.newInstance(false);

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, current).commit();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (hasTwoCameras) {
            if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
                getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
            }
        }

        isLockedToLandscape=
                savedInstanceState.getBoolean(STATE_LOCK_TO_LANDSCAPE);

        if (current != null) {
            current.lockToLandscape(isLockedToLandscape);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (hasTwoCameras) {
            outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                    getSupportActionBar().getSelectedNavigationIndex());
        }
        outState.putBoolean(STATE_LOCK_TO_LANDSCAPE, isLockedToLandscape);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        if (position == 0) {
            if (std == null) {
                std= CwacCameraFragment.newInstance(false);
            }

            current=std;
        }
        else {
            if (ffc == null) {
                ffc= CwacCameraFragment.newInstance(true);
            }

            current=ffc;
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.container, current).commit();

        findViewById(android.R.id.content).post(new Runnable() {
            @Override
            public void run() {
                current.lockToLandscape(isLockedToLandscape);
            }
        });

        return(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.cwac_main, menu);

        menu.findItem(R.id.landscape).setChecked(isLockedToLandscape);

        return(super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.content) {
            Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir=
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File output=new File(dir, "CameraContentDemo.jpeg");

            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));

            startActivityForResult(i, CONTENT_REQUEST);
        }
        else */if (item.getItemId() == R.id.landscape) {
            item.setChecked(!item.isChecked());
            current.lockToLandscape(item.isChecked());
            isLockedToLandscape=item.isChecked();
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // do nothing
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA && current != null
                && !current.isSingleShotProcessing()) {
            current.takePicture();

            return(true);
        }

        return(super.onKeyDown(keyCode, event));
    }

    @Override
    public boolean isSingleShotMode() {
        return(singleShot);
    }

    @Override
    public void setSingleShotMode(boolean mode) {
        singleShot=mode;
    }
}