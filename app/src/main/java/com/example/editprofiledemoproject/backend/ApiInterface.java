package com.example.editprofiledemoproject.backend;


import com.example.editprofiledemoproject.modelclass.Info;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by sarabjjeet on 9/6/17.
 */

public interface ApiInterface {

    @Multipart
    @POST("edit_profile/")
    Call<Info>  updateEditProfile(@PartMap() Map<String, RequestBody> requestBodyMap, @Part MultipartBody.Part file);

}
