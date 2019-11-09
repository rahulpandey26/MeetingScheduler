package com.example.meetingscheduler.meeting.ui.activity;

import android.os.Bundle;
import com.example.meetingscheduler.R;
import com.example.meetingscheduler.common.ui.activity.BaseActivityWithoutTransition;
import com.example.meetingscheduler.common.util.FragmentHelper;
import com.example.meetingscheduler.meeting.ui.fragment.HomeFragment;
import com.example.meetingscheduler.meeting.ui.fragment.ScheduleMeetingFragment;


public class HomeActivity extends BaseActivityWithoutTransition implements
        HomeFragment.HomeScreenListener, ScheduleMeetingFragment.ScheduleMeetingScreenListener {

    private HomeFragment mHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadHomeFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    private void loadHomeFragment() {
        mHomeFragment = new HomeFragment();
        FragmentHelper.addFragmentWithoutAnimation(this, mHomeFragment, R.id.fragment_container,
                "Home_fragment");
    }

    @Override
    public void onScheduleMeetingBtnClick() {
        ScheduleMeetingFragment scheduleMeetingFragment = new ScheduleMeetingFragment();
        FragmentHelper.addFragmentWithoutAnimation(this, scheduleMeetingFragment,
                R.id.fragment_container, "Schedule_Meeting_Fragment");
    }

    @Override
    public void onSubmitMeetingBtnClick() {
        FragmentHelper.popBackStackImmediate(this);
        mHomeFragment.updateScheduledEvents();
    }
}
