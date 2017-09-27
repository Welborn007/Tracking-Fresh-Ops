package com.kesari.tkfops.BikerList.BikerLocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.kesari.tkfops.Map.LocationServiceNew;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Route.BikerSocketLivePOJO;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class BikerMapLocationActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt,OnMapReadyCallback {

    private NetworkUtilsReceiver networkUtilsReceiver;
    private SupportMapFragment supportMapFragment;
    private LatLng Current_Origin,Delivery_Origin;
    //GoogleMap googleMap;
    private String TAG = this.getClass().getSimpleName();
    //private GPSTracker gps;
    private Location Current_Location;
    LatLng oldLocation,oldLocationBiker, newLocation;
    private static final int DURATION = 3000;
    private GoogleMap map;
    String[] geoArray;
    private Gson gson;
    String BikerID;
    private Socket socketBiker;
    BikerSocketLivePOJO bikerSocketLivePOJO;
    boolean connectedBiker = false;
    boolean isDirectionSet = true;
    //ScheduledExecutorService scheduleTaskExecutor;
    Marker marker,vehicle;
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION_NAME = "location_name";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";

    private BroadcastReceiver _refreshReceiver = new MyReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biker_map_location);

        try
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            IntentFilter filter = new IntentFilter("SOMEACTION");
            registerReceiver(_refreshReceiver, filter);

            gson = new Gson();

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(BikerMapLocationActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            BikerID = getIntent().getStringExtra("BikerID");


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        try
        {

            FragmentManager fm = getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
            if (supportMapFragment == null) {
                supportMapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
            }
            supportMapFragment.getMapAsync(this);

            Current_Location = SharedPrefUtil.getLocation(BikerMapLocationActivity.this);

            Current_Origin = new LatLng(Current_Location.getLatitude(), Current_Location.getLongitude());

            Log.i("latitude", String.valueOf(Current_Location.getLatitude()));
            Log.i("longitude", String.valueOf(Current_Location.getLongitude()));



        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        try
        {

            //getData();

            map = googleMap;
            if (ActivityCompat.checkSelfPermission(BikerMapLocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(BikerMapLocationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            map.setMyLocationEnabled(true);

            if (!NetworkUtils.isNetworkConnectionOn(BikerMapLocationActivity.this)) {
                /*FireToast.customSnackbarWithListner(BikerMapLocationActivity.this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;*/

                new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Oops! No internet access")
                        .setContentText("Please Check Settings")
                        .setConfirmText("Enable the Internet?")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();

            }
            else
            {
                startBikerSocket();
                setVehicleEmpty();

                oldLocation = Current_Origin;
                oldLocationBiker = Current_Origin;

                vehicle = map.addMarker(new MarkerOptions().position(Current_Origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_car))
                        //.rotation((float) bearingBetweenLocations(vehicle.getPosition(),Current_Origin))
                        .title("TKF Vehicle"));

                /*scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

                // This schedule a task to run every 10 minutes:
                scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Current_Origin = new LatLng(SharedPrefUtil.getLocation(BikerMapLocationActivity.this).getLatitude(), SharedPrefUtil.getLocation(BikerMapLocationActivity.this).getLongitude());
                                vehicle.setPosition(Current_Origin);
                                vehicle.setRotation((float) bearingBetweenLocations(oldLocationBiker,Current_Origin));

                                oldLocationBiker = Current_Origin;
                            }
                        });
                    }
                }, 0, 2, TimeUnit.SECONDS);*/
            }

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    return false;
                }
            });


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }



    private void startBikerSocket()
    {
        try {
            socketBiker = IO.socket(Constants.BikerLiveLocation);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socketBiker.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                try
                {
                    JSONObject obj = new JSONObject();
                    obj.put("hello", "server");
                    obj.put("binary", new byte[42]);
                    socketBiker.emit("bikerPosition", obj);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                //socket.disconnect();

                connectedBiker = true;
                Log.i("Send","Data " + socketBiker.id());
            }

        }).on("bikerPosition", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                final JSONObject obj = (JSONObject)args[0];
                Log.i("Connect",obj.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    BikerSocketLiveLocationResponse(obj.toString());
                    }
                });
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.i("DisConnect","Connect");
                connectedBiker = false;
            }

        });
        socketBiker.connect();
    }

    private void stopBikerSocket()
    {
        socketBiker.disconnect();
        connectedBiker = false;
        Log.i("SocketService","Disconnected");
    }

    public void BikerSocketLiveLocationResponse(String resp) {
        //map.clear();
        try {

            bikerSocketLivePOJO = gson.fromJson(resp, BikerSocketLivePOJO.class);

            if(bikerSocketLivePOJO.getData() != null)
            {
                String SocketVehicleID = bikerSocketLivePOJO.getData().getBiker_id();

                if(BikerID.equalsIgnoreCase(SocketVehicleID))
                {

                    geoArray = bikerSocketLivePOJO.getData().getGeo().getCoordinates();

                    Double cust_longitude = Double.parseDouble(geoArray[0]);
                    Double cust_latitude = Double.parseDouble(geoArray[1]);

                    Location location = new Location(LocationManager.GPS_PROVIDER);
                    location.setLatitude(cust_latitude);
                    location.setLongitude(cust_longitude);

                    if(Current_Location.distanceTo(location) < 5000) {

                        //final LatLng startPosition = marker.getPosition();
                        final LatLng finalPosition = new LatLng(cust_latitude, cust_longitude);

                        LatLng currentPosition = new LatLng(
                                cust_latitude,
                                cust_longitude);

                        map.setTrafficEnabled(true);

                        CameraPosition cameraPosition = new CameraPosition.Builder().
                                target(finalPosition).
                                tilt(60).
                                zoom(18).
                                bearing(0).
                                build();

                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        newLocation = currentPosition;

                        if(isDirectionSet)
                        {
                            addBikerMarkers("1", "TKF Vehicle", cust_latitude, cust_longitude);
                        }

                        if(marker != null)
                        {
                                    /*marker.setPosition(currentPosition);
                                    marker.setRotation((float) bearingBetweenLocations(oldLocation,newLocation));*/

                            animateMarker(map,marker,finalPosition,false);
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_biker));
                            marker.setRotation((float) bearingBetweenLocations(oldLocation,newLocation));

                            oldLocation = newLocation;
                        }

                        //getMapsApiDirectionsUrl(cust_latitude, cust_longitude);
                    }else
                    {
                        setVehicleEmpty();
                    }
                }
                else
                {
                    setVehicleEmpty();
                }
            }
            else
            {
                setVehicleEmpty();
            }

        } catch (Exception e) {
            //Toast.makeText(getActivity(), "exception", Toast.LENGTH_SHORT).show();
            Log.i(TAG, e.getMessage());
        }
    }

    private void addBikerMarkers(String id, String location_name, Double latitude, Double longitude) {

        try
        {
            if(marker!=null){
                marker.remove();
            }

            final LatLng dest = new LatLng(latitude, longitude);

            HashMap<String, String> data = new HashMap<String, String>();

            if (map != null) {
                marker = map.addMarker(new MarkerOptions().position(dest)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_biker))
                        .rotation((float) bearingBetweenLocations(oldLocation,newLocation))
                        .title(location_name));

                data.put(TAG_ID, id);
                data.put(TAG_LOCATION_NAME, location_name);
                data.put(TAG_LATITUDE, String.valueOf(latitude));
                data.put(TAG_LONGITUDE, String.valueOf(longitude));

                extraMarkerInfo.put(marker.getId(), data);

                IOUtils.showRipples(dest,map,DURATION);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        IOUtils.showRipples(dest,map,DURATION);
                    }
                }, DURATION - 500);

            }

            oldLocation = newLocation;
            isDirectionSet = false;

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    public static void animateMarker(final GoogleMap map, final Marker marker, final LatLng toPosition,
                                     final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 3000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;

                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private void setVehicleEmpty()
    {

        Current_Location = SharedPrefUtil.getLocation(BikerMapLocationActivity.this);
        Current_Origin = new LatLng(Current_Location.getLatitude(), Current_Location.getLongitude());

        map.setTrafficEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(Current_Origin).
                tilt(0).
                zoom(18).
                bearing(0).
                build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

       /* map.addMarker(new MarkerOptions().position(Current_Origin)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_customer))
                .title("Origin"));*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);
            unregisterReceiver(this._refreshReceiver);

            if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                // LOCATION SERVICE
                stopService(new Intent(this, LocationServiceNew.class));
                Log.e(TAG, "Location service is stopped");
            }

            /*if(!scheduleTaskExecutor.isShutdown())
            {
                scheduleTaskExecutor.shutdown();
            }*/

            stopBikerSocket();

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void NetworkOpen() {

    }

    @Override
    public void NetworkClose() {

        try {

            if (!NetworkUtils.isNetworkConnectionOn(this)) {
               /* FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;*/

                new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Oops! No internet access")
                        .setContentText("Please Check Settings")
                        .setConfirmText("Enable the Internet?")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();

            Double lat = intent.getDoubleExtra("lat",0.0);
            Double lon = intent.getDoubleExtra("lon",0.0);

            Log.i("LatReceiver_BikerMap", String.valueOf(lat));
            Log.i("LonReceiver_BikerMap", String.valueOf(lon));

            Current_Origin = new LatLng(lat, lon);
            //vehicle.setPosition(Current_Origin);
            vehicle.setRotation((float) bearingBetweenLocations(oldLocation,Current_Origin));
            animateMarker(map,vehicle,Current_Origin,false);

            oldLocation = Current_Origin;
        }
    }
}
