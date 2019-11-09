package com.example.meetingscheduler.meeting.ui.fragment;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meetingscheduler.R;
import com.example.meetingscheduler.common.ui.fragment.BaseFragment;
import com.example.meetingscheduler.meeting.adapter.MeetingListAdapter;
import com.example.meetingscheduler.meeting.model.ScheduledEvents;
import com.example.meetingscheduler.meeting.model.ScheduledMeetingResponse;
import com.example.meetingscheduler.meeting.presenter.MeetingDetailsPresenter;
import com.example.meetingscheduler.meeting.view.ScheduledMeetingView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends BaseFragment implements ScheduledMeetingView,
        EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private com.google.api.services.calendar.Calendar mService = null;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY, CalendarScopes.CALENDAR};

    private GoogleAccountCredential mCredential;
    private GoogleApiAvailability mGoogleApiAvailability;
    private List<ScheduledEvents> scheduledEventsList = new ArrayList<>();
    private ProgressDialog mProgress;
    private RecyclerView mMeetingsRecyclerView;
    private HomeScreenListener mListener;
    private List<ScheduledMeetingResponse> mScheduledMeetingResponseList;
    private Exception mErrorMsg = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setLayout(R.layout.fragment_home);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (HomeScreenListener) context;
        } catch (ClassCastException exception) {
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
        mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                Objects.requireNonNull(getContext()).getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi();
        setPresenter(new MeetingDetailsPresenter(this));
    }

    private void initializeViews(View view) {
        mProgress = new ProgressDialog(getContext());
        mProgress.setMessage(getString(R.string.syncing_with_calendar));
        mMeetingsRecyclerView = view.findViewById(R.id.meeting_list);
        view.findViewById(R.id.schedule_meeting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onScheduleMeetingBtnClick();
            }
        });
    }

    private void getMeetingDetails() {
        showProgressBar();
        new MeetingDetailsPresenter(this).getMeetingsDescription(getActivity());
    }

    private void setAdapter() {
        mMeetingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mMeetingsRecyclerView.setAdapter(new MeetingListAdapter(mScheduledMeetingResponseList));
    }

    @Override
    public void onGetScheduledMeetingSuccess(List<ScheduledMeetingResponse> scheduledMeetingResponse) {
        hideProgressBar();
        mScheduledMeetingResponseList = scheduledMeetingResponse;
        setAdapter();
    }

    public void updateScheduledEvents() {
        getResultsFromApi();
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);
        if(null != connectivityManager) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        } else {
            return false;
        }
    }


    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode = mGoogleApiAvailability.isGooglePlayServicesAvailable(
                Objects.requireNonNull(getContext()));
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        final int connectionStatusCode =
                mGoogleApiAvailability.isGooglePlayServicesAvailable(Objects.requireNonNull(getContext()));
        if (mGoogleApiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        Dialog dialog = mGoogleApiAvailability.getErrorDialog(getActivity(),
                connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.GET_ACCOUNTS)) {
            String accountName = Objects.requireNonNull(getActivity()).
                    getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getContext(), getString(R.string.google_play_required_msg) ,
                            Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = Objects.requireNonNull(getActivity()).
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int i, List<String> list) {

    }

    @Override
    public void onPermissionsDenied(int i, List<String> list) {
        getMeetingDetails();
    }

    /**
     * An asynchronous task that handles the Google Calendar API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    @SuppressLint("StaticFieldLeak") // use weak reference inside this method
    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

        private MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Google Calendar API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                getDataFromApi();
            } catch (Exception e) {
                e.printStackTrace();
                mErrorMsg = e;
                cancel(true);
                return null;
            }
            return null;
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         */
        private void getDataFromApi() throws IOException {
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();
            ScheduledEvents scheduledEvents;
            scheduledEventsList.clear();
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                scheduledEvents = new ScheduledEvents();
                scheduledEvents.setDescription(event.getDescription());
                scheduledEvents.setEventSummary(event.getSummary());
                scheduledEvents.setStartDate(start.toString());
                scheduledEvents.setEndDate("");
                scheduledEventsList.add(scheduledEvents);
            }
        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (scheduledEventsList.size() <= 0) {
                getMeetingDetails();
            } else {
                mMeetingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mMeetingsRecyclerView.setAdapter(new MeetingListAdapter(scheduledEventsList, true));
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mErrorMsg != null) {
                if (mErrorMsg instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mErrorMsg)
                                    .getConnectionStatusCode());
                } else if (mErrorMsg instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mErrorMsg).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(getContext(),getString(R.string.following_error)
                            + mErrorMsg.getMessage() , Toast.LENGTH_LONG).show();
                }
            } else {
                  Toast.makeText(getContext(), R.string.request_cancelled, Toast.LENGTH_LONG).show();
            }
        }
    }

    public interface HomeScreenListener {
        void onScheduleMeetingBtnClick();
    }
}
