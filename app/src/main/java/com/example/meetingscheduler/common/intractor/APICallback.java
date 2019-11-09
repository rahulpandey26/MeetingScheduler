package com.example.meetingscheduler.common.intractor;

import android.content.Context;

import androidx.annotation.NonNull;



import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class APICallback<T> implements Callback<T> {
    private Context mContext;
    private long startNs;

    public APICallback(Context context) {
        mContext = context;
        startNs = System.nanoTime();
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        if (response.isSuccessful()) {
            onResponseSuccess(response.body());
        }  else {
            onResponseFailure(response.message());
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        onResponseFailure(t.getMessage());
    }

    public abstract void onResponseSuccess(T response);

    public abstract void onResponseFailure(String errorMessage);
}