package com.example.meetingscheduler.common.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.meetingscheduler.R;

public abstract class BaseActivityWithoutTransition extends AppCompatActivity {

    private FrameLayout mProgressbarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_base_activity);
        FrameLayout fragmentLayoutContainer = findViewById(R.id.layout_container);
        getLayoutInflater().inflate(getLayoutResourceId(), fragmentLayoutContainer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView();
    }

    private void initView() {
        mProgressbarContainer = findViewById(R.id.progress_bar_container);
    }

    protected abstract int getLayoutResourceId();

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showProgressBar() {
        if (mProgressbarContainer != null) {
            mProgressbarContainer.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressbarContainer != null) {
            mProgressbarContainer.setVisibility(View.GONE);
        }
    }
}
