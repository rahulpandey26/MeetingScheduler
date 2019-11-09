package com.example.meetingscheduler.common.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.meetingscheduler.R;
import com.example.meetingscheduler.common.presenter.BasePresenter;
import com.example.meetingscheduler.common.ui.activity.BaseActivityWithoutTransition;
import com.example.meetingscheduler.common.view.BaseFragmentView;


public class BaseFragment extends Fragment implements BaseFragmentView {

    private int mLayoutId;
    private BasePresenter mPresenter;
    private Toolbar mToolbar;
    private View mViewProgressBarContainer;
    private View mNetworkErrorLayout;
    private View mServerErrorLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        FrameLayout fragmentLayoutContainer = view.findViewById(R.id.layout_container);
        inflater.inflate(mLayoutId, fragmentLayoutContainer);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mViewProgressBarContainer = view.findViewById(R.id.progress_bar_container);
        mNetworkErrorLayout = view.findViewById(R.id.network_error_layout);
        mToolbar = view.findViewById(R.id.toolbar);
    }

    protected void setLayout(int layoutId) {
        mLayoutId = layoutId;
    }

    @Override
    public void showFullScreenProgressDialog() {
        if (getActivity() != null && getActivity() instanceof BaseActivityWithoutTransition) {
            ((BaseActivityWithoutTransition) getActivity()).showProgressBar();
        }
    }

    @Override
    public void hideFullScreenProgressDialog() {
         if (getActivity() != null && getActivity() instanceof BaseActivityWithoutTransition) {
            ((BaseActivityWithoutTransition) getActivity()).hideProgressBar();
        }
    }

    @Override
    public void showNoNetworkAlert() {
        if (getActivity() != null && !getActivity().isDestroyed()) {
            hideFullScreenProgressDialog();
            hideProgressBar();
            mNetworkErrorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showFullScreenNetworkAlert() {

    }

    @Override
    public void showFullScreenError() {

    }

    public int isNetWorkAlertVisible() {
        return mNetworkErrorLayout.getVisibility();
    }

    @Override
    public void showProgressBar() {
        if (mViewProgressBarContainer != null) {
            mViewProgressBarContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgressBar() {
        if (mViewProgressBarContainer != null) {
            mViewProgressBarContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError() {
        hideFullScreenProgressDialog();
        hideProgressBar();
        mServerErrorLayout.setVisibility(View.VISIBLE);
    }

    public void showServerError() {
        hideFullScreenProgressDialog();
        hideProgressBar();
        mServerErrorLayout.setVisibility(View.VISIBLE);
    }

    public BasePresenter getPresenter() {
        return mPresenter;
    }

    public void setPresenter(BasePresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        hideFullScreenProgressDialog();
        super.onDestroy();
    }
}
