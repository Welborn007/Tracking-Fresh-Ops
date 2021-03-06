package com.kesari.tkfops.Map;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kesari on 24/05/17.
 */

public class LocationServiceNew extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    IBinder mBinder = new LocalBinder();

    private String TAG = this.getClass().getSimpleName();
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    public class LocalBinder extends Binder {
        public LocationServiceNew getServerInstance() {
            return LocationServiceNew.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createLocationRequest();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        } else {
            mGoogleApiClient.connect();
        }
        return START_STICKY;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {

    }

    @Override
    public void onConnected(Bundle arg0) {
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    private void newLocation(Location location) {
        Log.d(TAG, "New location:" + location.toString());
        double lat, lon;
        lat = (double) location.getLatitude();
        lon = (double) location.getLongitude();
        SharedPrefUtil.setLocation(LocationServiceNew.this, lat, lon);

		/*// BROADCAST
		sendBroadcast(new Intent(Constants.INTENT_ACTION_LOCATION_CHANGED));*/


    }

    public static LatLng LocationCoords(Double lat, Double lon)
    {
        LatLng Current_Origin = new LatLng(lat, lon);

        return Current_Origin;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location arg0) {
        newLocation(arg0);

        LocationCoords(arg0.getLatitude(),arg0.getLongitude());

        String lat = String.valueOf(arg0.getLatitude());
        String lon = String.valueOf(arg0.getLongitude());



        broadcastIntent(arg0.getLatitude(),arg0.getLongitude());

        try
        {
            if (SharedPrefUtil.getToken(this) != null) {
                if (!SharedPrefUtil.getToken(this).isEmpty()) {

                    if (SharedPrefUtil.getKeyLoginType(this) != null) {

                        if(SharedPrefUtil.getKeyLoginType(this).equalsIgnoreCase("Vehicle"))
                        {
                            if(SharedPrefUtil.getVehicleUser(this).getVehicleData().getVehicleStatus().equalsIgnoreCase("ON"))
                            {
                                sendVehicleLocationData(lat,lon);
                            }
                        }
                        else if(SharedPrefUtil.getKeyLoginType(this).equalsIgnoreCase("Biker"))
                        {
                            sendBikerLocationData(lat,lon);
                        }
                    }
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        //sendLocationData(lat,lon);
    }

    public void broadcastIntent(Double lat,Double lon){
        Intent intent = new Intent();
        intent.setAction("SOMEACTION");
        intent.putExtra("lat",lat);
        intent.putExtra("lon",lon);
        sendBroadcast(intent);
    }

    public void sendVehicleLocationData(String LAT,String LON){

        try
        {

            String url = Constants.DriverLocationApi;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("latitude",LAT);
                postObject.put("longitude",LON);

                jsonObject.put("post",postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(this,url, params ,jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("Driver_Updates_Send", result.toString());
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Driver_Update_Exception", e.getMessage());
        }
    }

    public void sendBikerLocationData(String LAT,String LON){

        try
        {

            String url = Constants.BikerLocation;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("latitude",LAT);
                postObject.put("longitude",LON);

                jsonObject.put("post",postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(this,url, params ,jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("Biker_Updates_Send", result.toString());
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Biker_Updates_Exception", e.getMessage());
        }
    }
}