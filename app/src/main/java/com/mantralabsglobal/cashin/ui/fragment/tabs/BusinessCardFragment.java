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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.BusinessCardService;
import com.mantralabsglobal.cashin.service.OCRServiceProvider;
import com.mantralabsglobal.cashin.ui.Application;
import com.mantralabsglobal.cashin.ui.activity.app.BaseActivity;
import com.mantralabsglobal.cashin.ui.activity.camera.CwacCameraActivity;
import com.mantralabsglobal.cashin.ui.fragment.camera.CwacCameraFragment;
import com.mantralabsglobal.cashin.ui.view.BirthDayView;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.utils.BusinessCardUtils;
import com.mantralabsglobal.cashin.utils.RetrofitUtils;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by pk on 13/06/2015.
 */
public class BusinessCardFragment extends BaseBindableFragment<BusinessCardService.BusinessCardDetail> implements OCRServiceProvider<BusinessCardService.BusinessCardDetail> {

    private static final String TAG = "BusinessCardFragment";
    private static final String BUSINESS_CARD_IMAGE_PATH = "BUSINESS_CARD_IMAGE_PATH";
    @InjectView(R.id.ll_business_card_snap)
    public ViewGroup vg_snap;

    static boolean imageButtonClicked;

    @InjectView(R.id.ll_business_card_detail)
    public ViewGroup vg_form;

    @InjectView(R.id.success_capture)
    public ViewGroup success_capture;

    @InjectView(R.id.type_of_employement)
    RadioGroup employement_type;

    @InjectView(R.id.enterWorkDetailsButton)
    public Button btn_enter_details;

    @InjectView(R.id.ib_launch_camera)
    public ImageButton camera_capture;

    @Pattern(regex = BaseActivity.NAME_VALIDATION, message = "Enter valid name")
    @NotEmpty(trim = true, message = "Employer name cannot be empty")
    @InjectView(R.id.cc_employer_name)
    public CustomEditText employerName;

    @NotEmpty(trim = true, message = "EmailID cannot be empty")
    @Email
    @InjectView(R.id.cc_work_email_id)
    public CustomEditText emailId;

    @NotEmpty(trim = true, message = "Employee ID cannot be empty")
    @InjectView(R.id.cc_employee_id)
    public CustomEditText employeeID;

    @NotEmpty(trim = true, message = "Experience cannot be empty")
    @InjectView(R.id.total_work_experience)
    public CustomEditText total_work_experience;

    @InjectView(R.id.photo_viewer)
    ImageView photoViewer;

    @NotEmpty(trim = true, message = "Joining date cannot be empty")
    @InjectView(R.id.joining_date)
    public BirthDayView joining_date;

  /*  @InjectView(R.id.fab_launch_camera)
    public FloatingActionButton fab_launchCamera;*/

   /* @NotEmpty
    @InjectView(R.id.cc_work_addess)
    public CustomEditText workAddress;*/

    private BusinessCardService businessCardService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the view from fragmenttab1.xml
        View view = inflater.inflate(R.layout.fragment_business_card, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        businessCardService = ((Application) getActivity().getApplication()).getRestClient().getBusinessCardService();

        registerChildView(vg_snap, View.GONE);
        registerChildView(vg_form, View.VISIBLE);
        // registerFloatingActionButton(fab_launchCamera, vg_form);
        //joining_date.et_amount.setVisibility(View.GONE);
        employement_type.check(R.id.full_time);

        if (imageButtonClicked) {
            camera_capture.setVisibility(View.GONE);
            success_capture.setVisibility(View.VISIBLE);
        }

        reset(false);
    }

    @Override
    protected View getFormView() {
        return vg_form;
    }

    @Override
    protected void onUpdate(BusinessCardService.BusinessCardDetail updatedData, Callback<BusinessCardService.BusinessCardDetail> saveCallback) {
        businessCardService.updateBusinessCardDetail(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(BusinessCardService.BusinessCardDetail updatedData, Callback<BusinessCardService.BusinessCardDetail> saveCallback) {
        businessCardService.createBusinessCardDetail(updatedData, saveCallback);
    }

    @Override
    protected void loadDataFromServer(Callback<BusinessCardService.BusinessCardDetail> dataCallback) {
        businessCardService.getBusinessCardDetail(dataCallback);
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        setVisibleChildView(vg_form);
    }


    @OnClick(R.id.enterWorkDetailsButton)
    public void onEnterDetailClick() {

        bindDataToForm(new BusinessCardService.BusinessCardDetail());
    }

    @Override
    public void bindDataToForm(BusinessCardService.BusinessCardDetail value) {
        setVisibleChildView(vg_form);
        if (value != null) {
            camera_capture.setVisibility(View.GONE);
            success_capture.setVisibility(View.VISIBLE);

            String path = Application.getInstance().getAppPreference().getString(BUSINESS_CARD_IMAGE_PATH, null);
            if (!TextUtils.isEmpty(path)) {
                Bitmap colouredBinary = BitmapFactory.decodeFile(path);
                photoViewer.setImageBitmap(colouredBinary);
            } else {
                Picasso.with(getActivity())
                        .load(value.getBusinessCardUrl())
                        .fit()
                        .centerCrop()
                        .into(photoViewer, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                hideProgressDialog();
                            }

                            @Override
                            public void onError() {
                                camera_capture.setVisibility(View.VISIBLE);
                                success_capture.setVisibility(View.GONE);
                                //showToastOnUIThread(getString(R.string.failed_to_load_image));
                            }
                        });
            }

            if (value.getEmployerName() != null)
                employerName.setText(value.getEmployerName());
            String concatAddress = StringUtils.join(value.getaddressLines(), ",");
            //  workAddress.setText(concatAddress);
            if (value.getEmail() != null)
                emailId.setText(value.getEmail());
            if (value.getWorkExperience() != null)
                total_work_experience.setText(value.getWorkExperience());
            //   joining_date.setText(value.getMonth());
            //  joining_date.setYear(value.getYear());
            if (value.getEmployeeID() != null)
                employeeID.setText(value.getEmployeeID());
            if (value.getJoiningDate() != null)
                joining_date.setText(value.getJoiningDate());

            employement_type.check(value.isEmployementType() ? R.id.contract_basis : R.id.full_time);
        }
    }

    @Override
    public BusinessCardService.BusinessCardDetail getDataFromForm(BusinessCardService.BusinessCardDetail base) {
        if (base == null)
            base = new BusinessCardService.BusinessCardDetail();

        base.setEmployerName(employerName.getText().toString());
        // base.setAddress(Arrays.asList(workAddress.getText().toString()));
        base.setEmail(emailId.getText().toString());
        base.setEmployementType(employement_type.getCheckedRadioButtonId() == R.id.contract_basis);
        // base.setYear(joining_date.getYear());
        //base.setMonth(joining_date.getMonth());
        base.setWorkExperience(total_work_experience.getText().toString());
        base.setJoiningDate(joining_date.getText().toString());
        base.setEmployeeID(employeeID.getText().toString());

        return base;
    }

    /*@OnClick( {R.id.ib_launch_camera, R.id.fab_launch_camera})*/
    @OnClick({R.id.ib_launch_camera, R.id.edit_button})
    public void launchCamera() {
        Intent intent = new Intent(getActivity(), CwacCameraActivity.class);
        intent.putExtra(CwacCameraActivity.SHOW_CAMERA_SWITCH, false);
        intent.putExtra(CwacCameraActivity.DEFAULT_CAMERA, CwacCameraActivity.STANDARD);
        intent.putExtra(CwacCameraFragment.SHOW_BOUNDS, true);
        intent.putExtra(CwacCameraFragment.ASPECT_RATIO, Double.parseDouble("0.66666666"));
        intent.putExtra(CwacCameraFragment.SHOW_INFO, getResources().getString(R.string.position_card_inside_frame));
        intent.putExtra("FLASH", false);
        intent.putExtra("FLIP_CAMERA", false);
        getActivity().startActivityForResult(intent, BaseActivity.IMAGE_CAPTURE_BUSINESS_CARD);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + this);
        Log.d(TAG, "requestCode " + requestCode + " , resultCode=" + resultCode);

        if (requestCode == BaseActivity.IMAGE_CAPTURE_BUSINESS_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                showToastOnUIThread(data.getStringExtra("file_path"));

                Uri destination = Uri.fromFile(new File(getActivity().getExternalFilesDir(null), "business-card-cropped.jpg"));
                Crop.of(Uri.fromFile(new File(data.getStringExtra("file_path")))
                        , destination).asSquare().withAspect(2, 1).withMaxSize(600, 300).start(getActivity(), BaseActivity.IMAGE_CROP_BUSINESS_CARD);

                Log.d(TAG, "onActivityResult, resultCode " + resultCode + " filepath = " + data.getStringExtra("file_path"));
            } else if (resultCode == Activity.RESULT_CANCELED) {
                reset(false);
            }
        } else if (requestCode == BaseActivity.IMAGE_CROP_BUSINESS_CARD) {
            handleCrop(resultCode, data);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            //showProgressDialog(getString(R.string.processing_image));
            String path = Crop.getOutput(result).getPath();
            Application.getInstance().putInAppPreference(BUSINESS_CARD_IMAGE_PATH, path);
            Bitmap colouredBinary = BitmapFactory.decodeFile(path);
            /*
            OCR not in use currently
            Bitmap binary = new ImageUtils().binarize(colouredBinary);
            uploadImageToServerForOCR(binary, BusinessCardFragment.this);
            */
            //Upload colored image to server
            uploadBusinessCardImage(colouredBinary);

            imageButtonClicked = true;
            camera_capture.setVisibility(View.GONE);
            success_capture.setVisibility(View.VISIBLE);
            photoViewer.setImageBitmap(colouredBinary);

        } else if (resultCode == Crop.RESULT_ERROR) {
            hideProgressDialog();
            showToastOnUIThread(Crop.getError(result).getMessage());
            reset(false);
        }
    }

    protected void uploadBusinessCardImage(Bitmap bmp) {
        CardImage cardImage = new CardImage();
        cardImage.setBase64encodedImage(base64(bmp));
        businessCardService.updateBusinessCardImage(cardImage, new Callback<RetrofitUtils.ServerMessage>() {
            @Override
            public void success(RetrofitUtils.ServerMessage serverMessage, Response response) {
                //Ignore success
            }

            @Override
            public void failure(RetrofitError error) {
                showToastOnUIThread(error.getMessage());
            }
        });
    }

    @Override
    protected void preProcessOCRData(BusinessCardService.BusinessCardDetail detail) {
        BusinessCardUtils.enrich(detail.getContentArr(), detail);
    }


    @Override
    public void getDetailFromImage(CardImage image, Callback<BusinessCardService.BusinessCardDetail> callback) {
        businessCardService.getBusinessCardDetailFromImage(image, callback);
    }
}
