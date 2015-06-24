package com.mantralabsglobal.cashin.views;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.fragment.DatepickerDialogFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by pk on 6/22/2015.
 */
public class BirthDayView extends LinearLayout {

    private EditText et_dob;
    private TextView tv_age;
    private TextView tv_dob;
    private ImageView iv_dob;
    private FragmentManager fragmentManager;

    public BirthDayView(Context context) {
        this(context, null);
    }

    public BirthDayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BirthDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BirthDayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.custom_view, 0, 0);
        try {
            tv_dob.setText(ta.getString(R.styleable.custom_view_field_label));
            Drawable drawable = ta.getDrawable(R.styleable.custom_view_field_icon);
            if (drawable != null)
                iv_dob.setBackground(drawable);


            et_dob.setText(ta.getString(R.styleable.custom_view_field_text));
        } finally {
            ta.recycle();
        }
        if(context instanceof  FragmentActivity)
            fragmentManager =((FragmentActivity)context).getSupportFragmentManager();
    }

    private void init(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.birthday_field_view, this);
        loadViews();

        et_dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
                Date defaultDate = null;
                try {
                    defaultDate = dateFormat.parse(et_dob.getText().toString());
                } catch (ParseException e) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                        defaultDate = sdf.parse(et_dob.getText().toString());
                    } catch (ParseException e1) {

                        e1.printStackTrace();
                    }
                }

                FragmentTransaction ft =  fragmentManager.beginTransaction();
                DialogFragment newFragment = new DatepickerDialogFragment(new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());

                        et_dob.setText(dateFormat.format(cal.getTime()));
                        tv_age.setText(computeAge(year, monthOfYear, dayOfMonth) + " Years");
                    }
                }, defaultDate);
                newFragment.show(ft, "date_dialog");

            }
        });
    }

    private int computeAge(int year, int monthOfYear, int dayOfMonth)
    {
        Calendar myBirthDate = Calendar.getInstance();
        myBirthDate.clear();
        myBirthDate.set(year, monthOfYear, dayOfMonth);
        Calendar now = Calendar.getInstance();
        Calendar clone = (Calendar) myBirthDate.clone(); // Otherwise changes are been reflected.
        int years = -1;
        while (!clone.after(now)) {
            clone.add(Calendar.YEAR, 1);
            years++;
        }
        return years;
    }

    private void loadViews() {
        et_dob = (EditText)findViewById(R.id.et_dob);
        tv_age = (TextView)findViewById(R.id.tv_age);
        tv_dob =  (TextView)findViewById(R.id.tv_dob);
        iv_dob = (ImageView)findViewById(R.id.iv_dob);
    }
}