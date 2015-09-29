package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.AvtarService;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.app.MainActivity;
import com.mantralabsglobal.cashin.ui.activity.camera.CwacCameraActivity;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by pk on 13/06/2015.
 */
public class YourPhotoFragment extends BaseBindableFragment<AvtarService.AvtarImage>  {

    private static final String TAG = "YourPhotoFragment";
    @InjectView(R.id.photo_viewer)
    ImageView photoViewer;

    @InjectView(R.id.vg_image_picker)
    ViewGroup imagePicker;

    @InjectView(R.id.vg_image_viewer)
    ViewGroup imageViewer;

    AvtarService avtarService;

    AvtarService.AvtarImage dirtyImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_photo, container, false);
        BootstrapButton backButton = (BootstrapButton) view.findViewById(R.id.btn_back);
        if(backButton != null)
            backButton.setBootstrapButtonEnabled(false);

        return view;
    }

    @Override
    protected View getFormView() {
        return imageViewer;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avtarService = ((Application) getActivity().getApplication()).getRestClient().getAvtarService();

        registerChildView(imagePicker, View.VISIBLE);
        registerChildView(imageViewer, View.GONE);
        reset(false);
    }

    @Override
    protected void onUpdate(AvtarService.AvtarImage updatedData, Callback<AvtarService.AvtarImage> saveCallback) {
        if(updatedData != null && updatedData.getFilePath() != null && updatedData.getFilePath().length()>0) {
            TypedFile typedFile = new TypedFile("multipart/form-data", new File(updatedData.getFilePath()));
            if(serverCopy != null && !TextUtils.isEmpty(serverCopy.getAvatar()))
            {
                Picasso.with(getActivity()).invalidate(serverCopy.getAvatar());
            }
            avtarService.uploadAvtarImage(typedFile, saveCallback);
        }
    }

    @Override
    protected void onCreate(AvtarService.AvtarImage updatedData, Callback<AvtarService.AvtarImage> saveCallback) {
        onUpdate(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<AvtarService.AvtarImage> dataCallback) {
        AvtarService.AvtarUtil.getAvtarImage(dataCallback, avtarService, getActivity());
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        setVisibleChildView(imagePicker);
    }

    @OnClick(R.id.choose_from_gallery_button)
    public void pickImageFromGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");

        getActivity().startActivityForResult(photoPickerIntent, BaseActivity.SELECT_PHOTO_FROM_GALLERY);
    }

    @OnClick(R.id.launch_camera_button)
    public void launchCamera()
    {
        Intent intent = new Intent(getActivity(), CwacCameraActivity.class);
        intent.putExtra(CwacCameraActivity.SHOW_CAMERA_SWITCH, true);
        intent.putExtra(CwacCameraActivity.DEFAULT_CAMERA, CwacCameraActivity.FFC);
        intent.putExtra("FLASH", true);
        intent.putExtra("FLIP_CAMERA", true);
        getActivity().startActivityForResult(intent, BaseActivity.SELFIE_CAPTURE);
    }

    @OnClick(R.id.edit_selfie_button)
    public void editSelfie(){
        setVisibleChildView(imagePicker);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case BaseActivity.SELECT_PHOTO_FROM_GALLERY:
                if(resultCode == Activity.RESULT_OK){
                    final Uri imageUri = imageReturnedIntent.getData();
                    beginCrop(imageUri, "selfie-cropped.jpg");
                    //bindDataToForm(imageUri);
                }
                break;
            case BaseActivity.SELFIE_CAPTURE:
                if(resultCode == Activity.RESULT_OK){
                    String path = imageReturnedIntent.getStringExtra("file_path");
                    File file = new File(path);
                    Uri imageUri = Uri.fromFile(file);
                    beginCrop(imageUri, "selfie-cropped.jpg");
                    //bindDataToForm(imageUri);
                }
                break;
            case BaseActivity.CROP_SELFIE:
                if (resultCode == Activity.RESULT_OK) {
                    AvtarService.AvtarImage avtarImage = new AvtarService.AvtarImage();
                    avtarImage.setFilePath(Crop.getOutput(imageReturnedIntent).getPath());
                    bindDataToForm(avtarImage);
                    dirtyImage = avtarImage;
                    save();

                } else if (resultCode == Crop.RESULT_ERROR) {
                    showToastOnUIThread(Crop.getError(imageReturnedIntent).getMessage());
                    reset(false);
                }
                break;

        }
    }

    private void beginCrop(Uri source, String fileName) {

        Uri destination = Uri.fromFile(new File(getActivity().getExternalFilesDir(null), fileName));
        Crop.of(source, destination).asSquare().withAspect(10,12).withMaxSize(1000, 1200).start(getActivity(), BaseActivity.CROP_SELFIE);
    }

    @Override
    public void bindDataToForm(AvtarService.AvtarImage value) {

        setVisibleChildView(imageViewer);

        if(value != null && value.getFilePath() != null && value.getFilePath().length()>0) {
            try{
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(value.getImageUri());
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                photoViewer.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        else if(value != null && value.getAvatar() != null && value.getAvatar().length()>0)
        {
            showProgressDialog("");
            dirtyImage = null;
            Picasso.with(getActivity())
                    .load(value.getAvatar())
                    .fit()
                    .centerCrop()
                    .into(photoViewer, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            hideProgressDialog();
                        }

                        @Override
                        public void onError() {
                            showSnackBarOnUIWithoutAction(R.string.failed_to_load_avtar);
                        }
                    });
        }
    }

    @Override
    public AvtarService.AvtarImage getDataFromForm(AvtarService.AvtarImage base) {
        if(dirtyImage != null)
            return dirtyImage;
        return base;
    }

    public boolean isFormValid()
    {
        return dirtyImage != null;
    }
}
