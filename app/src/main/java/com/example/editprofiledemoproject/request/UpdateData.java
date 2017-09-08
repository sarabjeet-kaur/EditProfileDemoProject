package com.example.editprofiledemoproject.request;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.editprofiledemoproject.backend.ConnectionModule;
import com.example.editprofiledemoproject.listener.ResponseListener;
import com.example.editprofiledemoproject.modelclass.Info;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sarabjjeet on 9/6/17.
 */

public class UpdateData {


    public void updateData(Map<String, RequestBody> requestBodyMap, MultipartBody.Part imageFileBody, final ResponseListener listener, Context context) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Wait...");

        ConnectionModule.getApiService().updateEditProfile(requestBodyMap, imageFileBody).enqueue(new Callback<Info>() {
            @Override
            public void onResponse(Call<Info> call, Response<Info> response) {

                dialog.show();
                if (response != null && response.body() != null && response.message() != null) {

                    Log.e("response", "success");
                    listener.onSuccess(response.body().getStatus().toString(), response.body().getMessage().toString());

                } else {
                    Log.e("response", "fail");

                    listener.onError(response.body().getStatus().toString(), response.body().getMessage().toString());
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Info> call, Throwable t) {
                Log.e("on failure", "call");
                listener.onError("error", "occured");
                //listener.onError(response.body().getStatus().toString(), response.body().getMessage().toString());
            }
        });

    }
}
