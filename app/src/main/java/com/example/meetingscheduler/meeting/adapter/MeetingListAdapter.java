package com.example.meetingscheduler.meeting.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meetingscheduler.R;
import com.example.meetingscheduler.meeting.model.ScheduledEvents;
import com.example.meetingscheduler.meeting.model.ScheduledMeetingResponse;
import java.util.List;

public class MeetingListAdapter extends RecyclerView.Adapter<MeetingListAdapter.ViewHolder> {

    private List<ScheduledMeetingResponse> mScheduledMeetingList;
    private List<ScheduledEvents> mScheduledEventList;
    private boolean mIsFromUserEntry;

    public MeetingListAdapter(List<ScheduledMeetingResponse> scheduledMeetingResponses){
        mScheduledMeetingList = scheduledMeetingResponses;
    }

    public MeetingListAdapter(List<ScheduledEvents> scheduledEvents, boolean isFromUserEntry){
        mScheduledEventList = scheduledEvents;
        mIsFromUserEntry = isFromUserEntry;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_list_item_layout, parent, false);
        return new MeetingListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mIsFromUserEntry){
            if(mScheduledEventList.size() > 0) {
                holder.mTitle.setText(String.format("%s - %s", mScheduledEventList.get(position).
                        getStartDate(), mScheduledEventList.get(position).getEndDate()));
                holder.mDesc.setText(mScheduledEventList.get(position).getDescription());
            }
        } else {
            if (mScheduledMeetingList.size() > 0) {
                holder.mTitle.setText(String.format("%s - %s", mScheduledMeetingList.get(position).
                        getStartTime(), mScheduledMeetingList.get(position).getEndTime()));
                holder.mDesc.setText(mScheduledMeetingList.get(position).getDescription());
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mIsFromUserEntry){
            return mScheduledEventList.size();
        } else {
            return mScheduledMeetingList.size();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mTitle;
        private TextView mDesc;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mDesc = itemView.findViewById(R.id.desc);

        }
    }
}
