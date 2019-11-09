package com.example.meetingscheduler.meeting.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.meetingscheduler.R;
import com.example.meetingscheduler.common.ui.fragment.BaseFragment;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


public class ScheduleMeetingFragment extends BaseFragment {

    private com.google.api.services.calendar.Calendar mService = null;
    private TimePicker startTime;
    private DatePicker startDate;
    private TimePicker endTime;
    private DatePicker endDate;
    private EditText mMeetingDescTxt;
    private ScheduleMeetingScreenListener mListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayout(R.layout.fragment_schedule_meeting);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ScheduleMeetingScreenListener) context;
        } catch (ClassCastException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
    }

    private void initializeViews(View view) {
        mMeetingDescTxt = view.findViewById(R.id.edit_text_meeting_desc);
        startDate = view.findViewById(R.id.startDate);
        startTime = view.findViewById(R.id.startTime);
        endTime = view.findViewById(R.id.endTime);
        endDate = view.findViewById(R.id.endDate);

        view.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.set(Calendar.DAY_OF_MONTH, startDate.getDayOfMonth());
                startCalendar.set(Calendar.MONTH, startDate.getMonth());
                startCalendar.set(Calendar.YEAR, startDate.getYear());
                startCalendar.set(Calendar.HOUR_OF_DAY, startTime.getCurrentMinute());
                startCalendar.set(Calendar.MINUTE, startTime.getCurrentMinute());
                Date startDate = startCalendar.getTime();
                DateTime start = new DateTime(startDate);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.set(Calendar.DAY_OF_MONTH, endDate.getDayOfMonth());
                endCalendar.set(Calendar.MONTH, endDate.getMonth());
                endCalendar.set(Calendar.YEAR, endDate.getYear());
                endCalendar.set(Calendar.HOUR_OF_DAY, endTime.getCurrentMinute());
                endCalendar.set(Calendar.MINUTE, endTime.getCurrentMinute());
                DateTime end = new DateTime(startDate);

                if(meetingsAreValid()) {
                    createEventAsync(mMeetingDescTxt.getText().toString(), start, end);
                }
            }
        });
    }

    private boolean meetingsAreValid() {
        if(TextUtils.isEmpty(mMeetingDescTxt.getText().toString())){
            Toast.makeText(getContext(), R.string.please_fill_desc, Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    @SuppressLint("StaticFieldLeak") // use weak reference inside this method
    private void createEventAsync(final String description, final DateTime startDate, final DateTime endDate) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    insertEvent(description, startDate, endDate);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mListener.onSubmitMeetingBtnClick();
            }
        }.execute();
    }

    private void insertEvent(String description, DateTime startDate, DateTime endDate) throws IOException {
        Event event = new Event()
                .setDescription(description);

        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDate)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=1"};
        event.setRecurrence(Arrays.asList(recurrence));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        if (mService != null)
            mService.events().insert(calendarId, event).setSendNotifications(true).execute();

    }

    public interface ScheduleMeetingScreenListener {
        void onSubmitMeetingBtnClick();
    }
}

