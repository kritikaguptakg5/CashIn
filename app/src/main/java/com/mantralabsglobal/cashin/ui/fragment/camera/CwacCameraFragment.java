package com.mantralabsglobal.cashin.ui.fragment.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraFragment;
import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraUtils;
import com.commonsware.cwac.camera.PictureTransaction;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.activity.camera.CwacCameraActivity;
import com.mantralabsglobal.cashin.ui.view.ScanBorderView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;

import butterknife.ButterKnife;

/**
 * Created by pk on 7/9/2015.
 */
public class CwacCameraFragment extends CameraFragment /*implements
        SeekBar.OnSeekBarChangeListener*/ {
    private static final String KEY_USE_FFC=
            "com.commonsware.cwac.camera.demo.USE_FFC";
    public static final String SHOW_BOUNDS = "SHOW_BOUNDS";
    public static final String ASPECT_RATIO = "ASPECT_RATIO";
    public static final String SHOW_INFO = "SHOW_INFO";
    private MenuItem singleShotItem=null;
    private MenuItem autoFocusItem=null;
    private MenuItem flashItem=null;
    // private MenuItem recordItem=null;
    private MenuItem stopRecordItem=null;
    private MenuItem mirrorFFC=null;
    private boolean singleShotProcessing=false;
    //private SeekBar zoom=null;
    private long lastFaceToast=0L;
    String flashMode=null;
    private File mFile;
    boolean showBounds =false;
    double aspectRatio = 1;
    ScanBorderView scanView;
    private CwacCameraHost cameraHost;


    public static CwacCameraFragment newInstance(boolean useFFC) {
        CwacCameraFragment f=new CwacCameraFragment();
        Bundle args=new Bundle();

        args.putBoolean(KEY_USE_FFC, useFFC);
        f.setArguments(args);

        return(f);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
        cameraHost = new CwacCameraHost(getActivity());
        SimpleCameraHost.Builder builder=
                new SimpleCameraHost.Builder(cameraHost);

        setHost(builder.useFullBleedPreview(true).build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View cameraView=
                super.onCreateView(inflater, container, savedInstanceState);
        View results=inflater.inflate(R.layout.cwac_camera_fragment, container, false);
        final ViewGroup vgcamera = ((ViewGroup)results.findViewById(R.id.camera));
        vgcamera.addView(cameraView);

        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoFocus();

            }
        });

        showBounds = getActivity().getIntent().getBooleanExtra(SHOW_BOUNDS, false);
        aspectRatio = getActivity().getIntent().getDoubleExtra(ASPECT_RATIO, 1);

        String contextInfo = getActivity().getIntent().getStringExtra(SHOW_INFO);

        if(contextInfo != null && contextInfo.length()>0)
        {
            TextView tv = new TextView(getActivity());
            tv.setGravity(Gravity.CENTER | Gravity.TOP);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 100, 0, 0);
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            tv.setLayoutParams(params);

            tv.setBackgroundColor(getResources().getColor(R.color.translucent));
            tv.setTextColor(getResources().getColor(R.color.white_max));
            tv.setVisibility(View.VISIBLE);
            tv.setText(contextInfo);

            vgcamera.addView(tv);
        }

        if(showBounds) {

            scanView = new ScanBorderView(getActivity(), null);
            scanView.setAspectRatio(aspectRatio);
            vgcamera.addView(scanView);
        }

        /*zoom=(SeekBar)results.findViewById(R.id.zoom);
        zoom.setKeepScreenOn(true);*/

        //    setRecordingItemVisibility();

        ButterKnife.inject(this, results);

        return(results);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                takeSimplePicture();

                return(true);

            case R.id.autofocus:
                autoFocus();
                return(true);

            case R.id.flash:
            case R.id.mirror_ffc:
                item.setChecked(!item.isChecked());

                return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    public boolean isSingleShotProcessing() {
        return(singleShotProcessing);
    }

    Contract getContract() {
        return((Contract)getActivity());
    }

    public void takeSimplePicture() {

        if(cameraHost != null && cameraHost.isPreviewReady()) {

            if (singleShotItem != null && singleShotItem.isChecked()) {
                singleShotProcessing = true;
//            takePictureItem.setEnabled(false);
            }

            PictureTransaction xact = new PictureTransaction(getHost());

            if (CwacCameraActivity.flashOn) {
                xact.flashMode(flashMode);
            }

            takePicture(xact);
        }
        else
        {
            Toast.makeText(getActivity(), getString(R.string.camera_preview_not_ready), Toast.LENGTH_SHORT).show();
        }
    }


    public interface Contract {
        boolean isSingleShotMode();

        void setSingleShotMode(boolean mode);
    }

    class CwacCameraHost extends SimpleCameraHost implements
            Camera.FaceDetectionListener {
        boolean supportsFaces=false;
        private boolean previewReady = false;
        String filePath;

        public CwacCameraHost(Context _ctxt) {
            super(_ctxt);
        }

        @Override
        protected File getPhotoPath() {
            File file = new File(getActivity().getExternalFilesDir(null), this.getPhotoFilename());
            filePath = file.getPath();
            return file;
        }

        @Override
        public boolean useFrontFacingCamera() {
            if (getArguments() == null) {
                return(false);
            }

            return(getArguments().getBoolean(KEY_USE_FFC));
        }

        @Override
        public boolean useSingleShotMode() {
            return(getContract().isSingleShotMode());
        }

        @Override
        public void saveImage(PictureTransaction xact, byte[] image) {
            if (useSingleShotMode()) {

               /* if(showBounds)
                {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                    bitmap = Bitmap.createBitmap(bitmap,(int)(scanView.getScanX()*bitmap.getWidth()) ,(int)(scanView.getScanY()*bitmap.getHeight())
                            ,(int)(scanView.getScanWidth()*bitmap.getWidth()),(int)(scanView.getScanHeight()*bitmap.getHeight()));

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    super.saveImage(xact, stream.toByteArray());
                }
                else {*/

                super.saveImage(xact, image);
                /*}*/

                Intent resultIntent = new Intent();
                // TODO Add extras or a data URI to this intent as appropriate.
                resultIntent.putExtra("file_path", filePath);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
            }
            else {
                super.saveImage(xact, image);
            }
        }

        @Override
        public void autoFocusAvailable() {
            if (autoFocusItem != null) {
                autoFocusItem.setEnabled(true);

                if (supportsFaces)
                    startFaceDetection();
            }
            previewReady = true;
        }

        @Override
        public void autoFocusUnavailable() {
            if (autoFocusItem != null) {
                stopFaceDetection();

                if (supportsFaces)
                    autoFocusItem.setEnabled(false);
            }
        }

        @Override
        public void onCameraFail(CameraHost.FailureReason reason) {
            super.onCameraFail(reason);

            Toast.makeText(getActivity(),
                    "Sorry, but you cannot use the camera now!",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public Camera.Parameters adjustPreviewParameters(Camera.Parameters parameters) {
            flashMode=
                    CameraUtils.findBestFlashModeMatch(parameters,
                            Camera.Parameters.FLASH_MODE_RED_EYE,
                            Camera.Parameters.FLASH_MODE_AUTO,
                            Camera.Parameters.FLASH_MODE_ON);

          /*  if (doesZoomReallyWork() && parameters.getMaxZoom() > 0) {
                zoom.setMax(parameters.getMaxZoom());
                zoom.setOnSeekBarChangeListener(CwacCameraFragment.this);
            }
            else {
                zoom.setEnabled(false);
            }*/

            if (parameters.getMaxNumDetectedFaces() > 0) {
                supportsFaces=true;
            }
            else {
                Toast.makeText(getActivity(),
                        "Face detection not available for this camera",
                        Toast.LENGTH_LONG).show();
            }

            return(super.adjustPreviewParameters(parameters));
        }

        @Override
        public void onFaceDetection(Face[] faces, Camera camera) {
            if (faces.length > 0) {
                long now=SystemClock.elapsedRealtime();

                if (now > lastFaceToast + 10000) {
                    Toast.makeText(getActivity(), "I see your face!",
                            Toast.LENGTH_LONG).show();
                    lastFaceToast=now;
                }
            }
        }

        @Override
        @TargetApi(16)
        public void onAutoFocus(boolean success, Camera camera) {
            super.onAutoFocus(success, camera);
        }

        public boolean isPreviewReady() {
            return previewReady;
        }
    }

}
