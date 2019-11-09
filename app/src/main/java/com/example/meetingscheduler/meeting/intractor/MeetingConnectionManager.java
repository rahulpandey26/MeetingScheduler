package com.example.meetingscheduler.meeting.intractor;

import android.content.Context;

import com.example.meetingscheduler.R;
import com.example.meetingscheduler.common.intractor.APICallback;
import com.example.meetingscheduler.common.intractor.APIServiceUtil;
import com.example.meetingscheduler.common.util.Constant;
import com.example.meetingscheduler.meeting.model.ScheduledMeetingResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;

public class MeetingConnectionManager {

    public static void getScheduledMeeting(Context context,
                                           APICallback<List<ScheduledMeetingResponse>> callback) {
        String url = context.getString(R.string.api_get_meetings_deatils);
        Map<String, String> queries = new HashMap<>();
        queries.put(Constant.HttpReqParamKey.DATE, "7/8/2015");
        Call<List<ScheduledMeetingResponse>> getCoverDescriptionResponseCall =
                APIServiceUtil.getInstance(context).getApiInterface().getScheduledMeeting(url, queries);
        getCoverDescriptionResponseCall.enqueue(callback);
    }
}
