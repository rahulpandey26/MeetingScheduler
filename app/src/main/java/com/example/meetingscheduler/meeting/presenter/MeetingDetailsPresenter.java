package com.example.meetingscheduler.meeting.presenter;

import android.content.Context;
import android.provider.SyncStateContract;

import com.example.meetingscheduler.common.intractor.APICallback;
import com.example.meetingscheduler.common.presenter.BasePresenter;
import com.example.meetingscheduler.common.util.Constant;
import com.example.meetingscheduler.common.view.BaseFragmentView;
import com.example.meetingscheduler.meeting.intractor.MeetingConnectionManager;
import com.example.meetingscheduler.meeting.model.ScheduledMeetingResponse;
import com.example.meetingscheduler.meeting.view.ScheduledMeetingView;

import java.util.List;

public class MeetingDetailsPresenter extends BasePresenter {

    public MeetingDetailsPresenter(BaseFragmentView view) {
        super(view);
    }

    public void getMeetingsDescription(Context context) {
        if (!isNetworkAvailable(context)) {
            showNetworkAlert();
            return;
        }

        MeetingConnectionManager.getScheduledMeeting(context,
                new APICallback<List<ScheduledMeetingResponse>>(context) {

                    @Override
                    public void onResponseSuccess(List<ScheduledMeetingResponse> response) {
                        ScheduledMeetingView view = (ScheduledMeetingView) getView();
                        if (view == null) {
                            return;
                        }
                        view.onGetScheduledMeetingSuccess(response);
                    }

                    @Override
                    public void onResponseFailure(String errorMessage) {
                         handleError(Constant.UNKNOWN_ERROR);
                    }
                });
    }
}
