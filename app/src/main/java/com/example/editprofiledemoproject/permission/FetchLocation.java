package com.example.editprofiledemoproject.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.editprofiledemoproject.R;
import com.example.editprofiledemoproject.listener.LocationListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static com.example.editprofiledemoproject.utility.AppConstants.REQUEST_LOCATION;

/**
 * Created by sarabjjeet on 9/6/17.
 */

public class FetchLocation implements ActivityCompat.OnRequestPermissionsResultCallback{
Context context;
    String latitude,langitude;
    LocationListener listener;

    public FusedLocationProviderClient mFusedLocationClient;


    public FetchLocation(Context context,final LocationListener listener) {
        this.context=context;
        this.listener=listener;
    }

    //Fetch Location
    public void currentLocation(){
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(context);
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        } else {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location == null) {
                                        Log.e(" Null:- ", "");

                                        showSettingsAlert();
                                    } else {
                                        latitude = String.valueOf(location.getLatitude());
                                        langitude = String.valueOf(location.getLongitude());
                                        listener.currentLocation(latitude,langitude);
                                        Log.e("lat", latitude);
                                        Log.e("lang", langitude);
                                        Log.e("NOt Null:- ", "");
                                    }
                                }
                            }
                    );
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();

                    try {
                        currentLocation();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }

        }
    }
    //Method to show dialog
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(context.getString(R.string.alert_title));

        alertDialog.setMessage(context.getString(R.string.alert_message));

        alertDialog.setPositiveButton(context.getString(R.string.alert_positive_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentLocation();
                    }
                }, 10000);
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.alert_cancel_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
