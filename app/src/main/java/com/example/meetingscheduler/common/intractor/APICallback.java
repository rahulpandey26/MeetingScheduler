package com.example.meetingscheduler.common.intractor;

import android.content.Context;
import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class APICallback<T> implements Callback<T> {

    protected APICallback(Context context) {
    }

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
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