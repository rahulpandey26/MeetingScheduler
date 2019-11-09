package com.example.meetingscheduler.common.ui.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerFragmentListener mDatePickerFragmentListener;
    private Date mSelectedDate;
    private Date mMinDate;
    private Date mMaxDate;
    private String[] mMonthName = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    public static DatePickerFragment newInstance(DatePickerFragmentListener datePickerFragmentListener) {

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(datePickerFragmentListener);
        return fragment;
    }

    /**
     * @param datePickerFragmentListener datePickerFragmentListener is and instance of DatePickerFragmentListener
     * @param minDate                    send minDate "null" if you don't want to set .
     * @param maxDate                    send maxDate "null" if you don't want to set
     * @return datePickerFragment is and instance instance
     */
    public static DatePickerFragment newInstance(DatePickerFragmentListener datePickerFragmentListener,
                                                 Date minDate, Date maxDate) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(datePickerFragmentListener);
        fragment.mMinDate = minDate;
        fragment.mMaxDate = maxDate;
        return fragment;
    }

    public static DatePickerFragment newInstance(DatePickerFragmentListener datePickerFragmentListener,
                                                 Date minDate, Date maxDate, Date selectedDate) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setListener(datePickerFragmentListener);
        fragment.mMinDate = minDate;
        fragment.mMaxDate = maxDate;
        fragment.mSelectedDate = selectedDate;
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        if (null != mSelectedDate) {
            c.setTime(mSelectedDate);
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getActivity()),
                AlertDialog.THEME_HOLO_DARK, this, year, month, day);
        if (mMinDate != null) {
            datePickerDialog.getDatePicker().setMinDate(mMinDate.getTime());
        }
        if (mMaxDate != null) {
            datePickerDialog.getDatePicker().setMaxDate(mMaxDate.getTime());
        }
        return datePickerDialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatePickerFragmentListener = null;
    }

    public void setListener(DatePickerFragmentListener datePickerFragmentListener) {
        mDatePickerFragmentListener = datePickerFragmentListener;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        if (mDatePickerFragmentListener != null) {
            mDatePickerFragmentListener.onDateSet(Integer.toString(year),
                    mMonthName[month],
                    new DecimalFormat("00").format(dayOfMonth),
                    calendar.getTime());
        }
    }

    public interface DatePickerFragmentListener {

        /**
         * @param year  Ex. 1990
         * @param month Ex. January
         * @param day   Ex. o1
         * @param date  Complete Date object.
         */
        void onDateSet(String year, String month, String day, Date date);
    }
}
