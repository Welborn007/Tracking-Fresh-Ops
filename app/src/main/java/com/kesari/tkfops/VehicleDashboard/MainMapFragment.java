package com.kesari.tkfops.VehicleDashboard;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.kesari.tkfops.Map.HttpConnection;
import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.Map.PathJSONParser;
import com.kesari.tkfops.OpenOrders.OrderMainPOJO;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by kesari on 18/04/17.
 */

public class MainMapFragment extends Fragment implements OnMapReadyCallback {

    View f1;

    private Context mContext;
    private MapFragment supportMapFragment;
    //private GPSTracker gps;
    private LatLng Current_Origin,oldLocation;
    private GoogleMap map;
    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION_NAME= "location_name";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";

    private static final String TAG = "driver_Parameters";
    private static View view;
    Marker vehicle;
    private  Gson gson;
    private  OrderMainPOJO orderMainPOJO;
    private Location Current_Location,old_Location;
    PolylineOptions polyLineOptions = null;
    ScheduledExecutorService scheduleTaskExecutor/*,scheduleTaskExecutorOnlyVehicle*/;
    List<Polyline> polylines = new ArrayList<Polyline>();

    private BroadcastReceiver _refreshReceiver = new MyReceiver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try
        {

            mContext = getActivity();
            gson = new Gson();
            FragmentManager fm = getActivity().getFragmentManager();
            supportMapFragment = (MapFragment) fm.findFragmentById(R.id.map_container);
            if (supportMapFragment == null) {
                supportMapFragment = MapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
            }
            supportMapFragment.getMapAsync(this);

            Current_Origin = new LatLng(SharedPrefUtil.getLocation(getActivity()).getLatitude(), SharedPrefUtil.getLocation(getActivity()).getLongitude());
            oldLocation = Current_Origin;

            old_Location = new Location(LocationManager.GPS_PROVIDER);
            old_Location.setLatitude(Current_Origin.latitude);
            old_Location.setLongitude(Current_Origin.longitude);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try
        {

            map = googleMap;
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            map.setTrafficEnabled(true);
            map.getUiSettings().setRotateGesturesEnabled(false);

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(Current_Origin).
                    tilt(0).
                    zoom(17).
                    bearing(0).
                    build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            updateCurrentLocationMarker();

            getOrderList(getActivity());

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    String Lat = String.valueOf(latLng.latitude);
                    String Long = String.valueOf(latLng.longitude);

                    //sendVehicleLocationData(Lat,Long);
                }
            });


            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    try
                    {
                        // Get extra data with marker ID
                        HashMap<String, String> marker_data = extraMarkerInfo.get(marker.getId());

                        // Getting the data from Map
                        String latitude = marker_data.get(TAG_LATITUDE);
                        String longitude = marker_data.get(TAG_LONGITUDE);
                        String place = marker_data.get(TAG_LOCATION_NAME);
                        String id = marker_data.get(TAG_ID);

                /*Double latitude = marker.getPosition().latitude;
                Double longitude = marker.getPosition().longitude;*/

                        /*Intent intent = new Intent(getActivity(), CustomerMapActivity.class);
                        intent.putExtra("place",place);
                        intent.putExtra("id",id);
                        intent.putExtra("Lat", Double.parseDouble(latitude));
                        intent.putExtra("Lon", Double.parseDouble(longitude));
                        startActivity(intent);*/

                        String uri = "http://maps.google.com/maps?f=d&hl=en"+"&daddr="+latitude+","+longitude;
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(Intent.createChooser(intent, "Select an application"));

                    }catch (NullPointerException npe)
                    {

                    }

                    return false;
                }
            });

            if(vehicle == null)
            {
                vehicle = map.addMarker(new MarkerOptions().position(Current_Origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_car))
                        //.rotation((float) bearingBetweenLocations(vehicle.getPosition(),Current_Origin))
                        .title("TKF Vehicle"));
            }

            //showOnlyVehicle();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

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
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(getActivity(),url, params ,jsonObject, new IOUtils.VolleyCallback() {
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
            Log.i("Driver_Updates_Exception", e.getMessage());
        }
    }

    public void updateCurrentLocationMarker(){

        if(map != null){

            //map.clear();
            scheduleTaskExecutor = Executors.newScheduledThreadPool(1);

            // This schedule a task to run every 10 minutes:
            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Current_Origin = new LatLng(SharedPrefUtil.getLocation(getActivity()).getLatitude(),SharedPrefUtil.getLocation(getActivity()).getLongitude());
                            getOrderList(getActivity());
                        }
                    });
                }
            }, 0, 1, TimeUnit.MINUTES);

        }
    }

    public void getOrderList(final Context context)
    {
        try
        {
            String url = Constants.OrderList /*+ "Pending"*/;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    getOrderListResponse(result,context);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getOrderListResponse(String Response,Context context)
    {
        try
        {
            orderMainPOJO = gson.fromJson(Response, OrderMainPOJO.class);

            if(orderMainPOJO.getData().isEmpty())
            {
                //FireToast.customSnackbar(context,"No Orders!!!","Swipe");

                /*new SweetAlertDialog(context)
                        .setTitleText("No Orders!!!")
                        .show();*/

                polylines.clear();

                //showOnlyVehicle();
            }
            else
            {
                polylines.clear();
                JSONObject jsonObject = new JSONObject(Response);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                //showOnlyVehicle();

               /* vehicle = map.addMarker(new MarkerOptions().position(Current_Origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_car))
                        //.rotation((float) bearingBetweenLocations(vehicle.getPosition(),Current_Origin))
                        .title("TKF Vehicle"));*/

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo_inside = jsonArray.getJSONObject(i);

                    JSONObject jsonObject1Address = jo_inside.getJSONObject("address");

                    String location_name = jsonObject1Address.getString("fullName");
                    Double latitude = jsonObject1Address.getDouble("latitude");
                    Double longitude = jsonObject1Address.getDouble("longitude");
                    String id = jsonObject1Address.getString("_id");

                    addMarkers(id,location_name,latitude,longitude);
                    getMapsApiDirectionsUrl(latitude,longitude);
                }

            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

   /* private void showOnlyVehicle()
    {


        scheduleTaskExecutorOnlyVehicle = Executors.newScheduledThreadPool(1);

        // This schedule a task to run every 10 minutes:
        scheduleTaskExecutorOnlyVehicle.scheduleAtFixedRate(new Runnable() {
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });
            }
        }, 0, 2, TimeUnit.SECONDS);
    }*/

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

    private void addMarkers(String id,String location_name,Double latitude,Double longitude) {

        try
        {

            LatLng dest = new LatLng(latitude, longitude);

            HashMap<String, String> data = new HashMap<String, String>();

            if (map != null) {
                Marker marker = map.addMarker(new MarkerOptions().position(dest)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_customer_marker))
                        .title(location_name));

                data.put(TAG_ID,id);
                data.put(TAG_LOCATION_NAME,location_name);
                data.put(TAG_LATITUDE, String.valueOf(latitude));
                data.put(TAG_LONGITUDE, String.valueOf(longitude));

                extraMarkerInfo.put(marker.getId(),data);

                /*vehicle = map.addMarker(new MarkerOptions().position(Current_Origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_car))
                        .title("TKF Vehicle"));*/
            }

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(Current_Origin).
                    tilt(0).
                    zoom(17).
                    bearing(0).
                    build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void getMapsApiDirectionsUrl(Double destLatitude, Double destLongitude) {

        try
        {

            String waypoints = "waypoints=optimize:true|"
                    + Current_Origin.latitude + "," + Current_Origin.longitude
                    + "|" + "|" + destLatitude + ","
                    + destLongitude;

            String sensor = "sensor=false";
            String key = "key=" + getString(R.string.googleMaps_ServerKey);
            String params = waypoints + "&" + sensor + "&" + key;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/"
                    + output + "?"+"origin="+Current_Origin.latitude + "," + Current_Origin.longitude+"&destination="+destLatitude + ","
                    + destLongitude +"&" + params;

            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("ReadTaskResult",result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            String distance = "";
            String duration = "";

            // traversing through routes
            try {
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        if(j==0){    // Get distance from the list
                            distance = (String)point.get("distance");
                            continue;
                        }else if(j==1){ // Get duration from the list
                            duration = (String)point.get("duration");
                            continue;
                        }

                        points.add(position);
                    }

                    polyLineOptions.addAll(points);
                    polyLineOptions.width(8);

                    Random rnd = new Random();
                    int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                    polyLineOptions.color(Color.BLUE);
                }

                map.addPolyline(polyLineOptions);
                polylines.add(map.addPolyline(polyLineOptions));
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter("SOMEACTION");
        getActivity().registerReceiver(_refreshReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        try
        {
            getActivity().unregisterReceiver(this._refreshReceiver);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(!scheduleTaskExecutor.isShutdown())
        {
            scheduleTaskExecutor.shutdown();
        }

        /*if(!scheduleTaskExecutorOnlyVehicle.isShutdown())
        {
            scheduleTaskExecutorOnlyVehicle.shutdown();
        }*/

        try
        {
            getActivity().unregisterReceiver(this._refreshReceiver);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.gc();

        if(!scheduleTaskExecutor.isShutdown())
        {
            scheduleTaskExecutor.shutdown();
        }

        /*if(!scheduleTaskExecutorOnlyVehicle.isShutdown())
        {
            scheduleTaskExecutorOnlyVehicle.shutdown();
        }*/

        try
        {
            getActivity().unregisterReceiver(this._refreshReceiver);
        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();

            Double lat = intent.getDoubleExtra("lat",0.0);
            Double lon = intent.getDoubleExtra("lon",0.0);

            Log.i("ChangedLatReceiver_Main", String.valueOf(lat));
            Log.i("ChangedLonReceiver_Main", String.valueOf(lon));

            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(lat);
            location.setLongitude(lon);

            if(location.distanceTo(old_Location) > 40) {
                Current_Origin = new LatLng(lat, lon);
                //vehicle.setPosition(Current_Origin);

                CameraPosition cameraPosition = new CameraPosition.Builder().
                        target(Current_Origin).
                        tilt(0).
                        zoom(17).
                        bearing(0).
                        build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                vehicle.setRotation((float) bearingBetweenLocations(oldLocation,Current_Origin));
                animateMarker(map,vehicle,Current_Origin,false);

                oldLocation = Current_Origin;

                old_Location = new Location(LocationManager.GPS_PROVIDER);
                old_Location.setLatitude(lat);
                old_Location.setLongitude(lon);
            }

        }
    }

}
