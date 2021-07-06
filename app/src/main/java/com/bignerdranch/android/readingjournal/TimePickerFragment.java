package com.bignerdranch.android.readingjournal;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.bignerdranch.android.readingjournal.DatePickerFragment.EXTRA_DATE;

public class TimePickerFragment extends DialogFragment {
    private static final String ARG_TIME = "time";
    public static final String EXTRA_TIME = "com.bignerdranch.readingjournal.time";
    private TimePicker mTimePicker;
    private int mYear, mMonth, mDay, mHourOfDay, mMinute;
    Calendar mCalendar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_timepicker);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mHourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        boolean is24HourView = false;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), callback,
                mHourOfDay, mMinute, is24HourView);
        return timePickerDialog;


    }
    private TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hour, int minute) {
            mHourOfDay = hour;
            mMinute = minute;
            mCalendar.set(Calendar.HOUR_OF_DAY, mHourOfDay);
            mCalendar.set(Calendar.MINUTE, mMinute);
            Date date = new GregorianCalendar(mYear,mMonth,mDay,mHourOfDay,minute).getTime();
            sendResult(Activity.RESULT_OK, date);
        }
    };

    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultcode, Date date){
        if (getTargetFragment()== null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(),resultcode,intent);
    }
}