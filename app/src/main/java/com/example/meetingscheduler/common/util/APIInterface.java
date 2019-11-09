package com.example.meetingscheduler.common.util;

import com.example.meetingscheduler.meeting.model.ScheduledMeetingResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface APIInterface {

    @GET
    Call<List<ScheduledMeetingResponse>> getScheduledMeeting(@Url String url,
                                                             @QueryMap(encoded = true) Map<String, String> queries);

}
