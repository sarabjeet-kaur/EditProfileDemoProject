package com.example.editprofiledemoproject.listener;

/**
 * Created by sarabjjeet on 9/7/17.
 */

public interface ResponseListener {
    void onSuccess(String response,String message);
    void onError(String error,String message);
}
