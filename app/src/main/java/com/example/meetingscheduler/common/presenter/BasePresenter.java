package com.example.meetingscheduler.common.presenter;

import android.content.Context;

import com.example.meetingscheduler.common.util.NetworkUtil;
import com.example.meetingscheduler.common.view.BaseFragmentView;


public class BasePresenter {

    private BaseFragmentView mView;

    protected BasePresenter(BaseFragmentView view) {
        mView = view;
    }

    public void onDestroy() {
        mView = null;
    }

    public void cancelRequest() {

    }

    public void showNetworkAlert() {
        if (null != mView) {
            mView.showNoNetworkAlert();
        }
    }

    public void showFullScreenNetworkAlert() {
        if (null != mView) {
            mView.showFullScreenNetworkAlert();
        }
    }

    public boolean isNetworkAvailable(Context ctx) {
        return NetworkUtil.isAvailable(ctx);
    }


    // Base implementation to handle error. If you need more control, override it.
    public void handleError(String error) {
        if (mView != null) {
            mView.showError();
        }
    }

    public void handleFullScreenError() {
        if (mView != null) {
            mView.showFullScreenError();
        }
    }

    public BaseFragmentView getView() {
        return mView;
    }
}
