package com.example.meetingscheduler.meeting.view;

import com.example.meetingscheduler.meeting.model.ScheduledMeetingResponse;

import java.util.List;

public interface ScheduledMeetingView {

    void onGetScheduledMeetingSuccess(List<ScheduledMeetingResponse> scheduledMeetingResponse);
}
