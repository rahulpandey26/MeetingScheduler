package com.example.meetingscheduler.common.ui.fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * A time picker dialog which allows you to set a min and max time.
 */
public class RangeTimePickerDialog extends TimePickerDialog {

    private int mMinHour = -1;
    private int mMinMinute = -1;
    private int mMaxHour = 25;
    private int mMaxMinute = 25;
    private int mCurrentHour = 0;
    private int mCurrentMinute = 0;
    private Calendar mCalendar = Calendar.getInstance();
    private DateFormat mDateFormat;

    public RangeTimePickerDialog(Context context, OnTimeSetListener rangeTimePickerFragmentListener, int hourOfDay, int minute, boolean is24HourView) {
        super(context, AlertDialog.THEME_HOLO_DARK, rangeTimePickerFragmentListener, hourOfDay, minute, is24HourView);
        mCurrentHour = hourOfDay;
        mCurrentMinute = minute;
        mDateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

        try {
            Class<?> superclass = getClass().getSuperclass();
            Field timePickerField = superclass.getDeclaredField("mTimePicker");
            timePickerField.setAccessible(true);
            TimePicker timePicker = (TimePicker) timePickerField.get(this);
            timePicker.setOnTimeChangedListener(this);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {

        }
    }

    public void setMin(int hour, int minute) {
        mMinHour = hour;
        mMinMinute = minute;
    }

    public void setMax(int hour, int minute) {
        mMaxHour = hour;
        mMaxMinute = minute;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        boolean validTime = true;
        if (hourOfDay < mMinHour || (hourOfDay == mMinHour && minute < mMinMinute)) {
            validTime = false;
        }

        if (hourOfDay > mMaxHour || (hourOfDay == mMaxHour && minute > mMaxMinute)) {
            validTime = false;
        }

        if (validTime) {
            mCurrentHour = hourOfDay;
            mCurrentMinute = minute;
        }

        updateTime(mCurrentHour, mCurrentMinute);
        updateDialogTitle(view, mCurrentHour, mCurrentMinute);
    }

    private void updateDialogTitle(TimePicker timePicker, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        String title = mDateFormat.format(mCalendar.getTime());
        setTitle(title);
    }
}
