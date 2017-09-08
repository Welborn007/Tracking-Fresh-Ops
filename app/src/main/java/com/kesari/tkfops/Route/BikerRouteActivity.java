package com.kesari.tkfops.Route;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.kesari.tkfops.Map.HttpConnection;
import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.Map.LocationServiceNew;
import com.kesari.tkfops.Map.PathJSONParser;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.FireToast;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class BikerRouteActivity extends AppCompatActivity implements OnMapReadyCallback,NetworkUtilsReceiver.NetworkResponseInt{

    private Context mContext;
    private MapFragment supportMapFragment;
    //private GPSTracker gps;
    private LatLng Current_Origin;
    private LatLng Old_Origin;
    private GoogleMap map;
    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION_NAME = "location_name";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";
    private static final String TAG_FROM_TIME = "";
    private static final String TAG_TO_TIME = "";

    private static View view;
    private String TAG = this.getClass().getSimpleName();
    private io.socket.client.Socket socket;
    private NetworkUtilsReceiver networkUtilsReceiver;
    private Gson gson;
    SocketLiveMainPOJO scoketLiveMainPOJO;
    Marker markerVehicle;
    String[] geoArray;
    LatLng oldLocation, newLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biker_route);

        try
        {

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(BikerRouteActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            FragmentManager fm = getFragmentManager();
            supportMapFragment = (MapFragment) fm.findFragmentById(R.id.map_container);
            if (supportMapFragment == null) {
                supportMapFragment = MapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
            }
            supportMapFragment.getMapAsync(this);

            Current_Origin = new LatLng(SharedPrefUtil.getLocation(BikerRouteActivity.this).getLatitude(), SharedPrefUtil.getLocation(BikerRouteActivity.this).getLongitude());

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //getData();

        try
        {

            map = googleMap;
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
            map.setMyLocationEnabled(true);
            map.animateCamera(CameraUpdateFactory.zoomTo(15));

            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(SharedPrefUtil.getLocation(BikerRouteActivity.this).getLatitude());
            location.setLongitude(SharedPrefUtil.getLocation(BikerRouteActivity.this).getLongitude());

            updateCurrentLocationMarker(location);


            map.moveCamera(CameraUpdateFactory.newLatLngZoom(Current_Origin,
                    13));

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    String Lat = String.valueOf(latLng.latitude);
                    String Long = String.valueOf(latLng.longitude);
                }
            });

            if (!NetworkUtils.isNetworkConnectionOn(BikerRouteActivity.this)) {
                FireToast.customSnackbarWithListner(BikerRouteActivity.this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;
            }
            else
            {
                startSocket();
            }

            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }
                @Override
                public View getInfoContents(Marker marker) {

                    View myContentView = getLayoutInflater().inflate(
                            R.layout.map_infolayout, null);

                    try
                    {
                        // Get extra data with marker ID
                        HashMap<String, String> marker_data = extraMarkerInfo.get(marker.getId());
                        TextView mapLoc = ((TextView) myContentView.findViewById(R.id.mapLoc));
                        TextView from_time = ((TextView) myContentView.findViewById(R.id.from_time));
                        TextView to_time = ((TextView) myContentView.findViewById(R.id.to_time));
                        View viewLine = ((View) myContentView.findViewById(R.id.viewLine));
                        TextView viewHeader = ((TextView) myContentView.findViewById(R.id.viewHeader));

                        if(!marker.getTitle().equalsIgnoreCase("TKF Vehicle"))
                        {
                            // Getting the data from Map
                            String latitude = marker_data.get(TAG_LATITUDE);
                            String longitude = marker_data.get(TAG_LONGITUDE);
                            String place = marker_data.get(TAG_LOCATION_NAME);
                            String id = marker_data.get(TAG_ID);
                            String startTime = marker_data.get(TAG_FROM_TIME);
                            String endTime = marker_data.get(TAG_TO_TIME);

                            from_time.setVisibility(View.VISIBLE);
                            to_time.setVisibility(View.VISIBLE);
                            viewLine.setVisibility(View.VISIBLE);
                            viewHeader.setVisibility(View.VISIBLE);

                            mapLoc.setText(place);
                            from_time.setText(startTime);
                            to_time.setText(endTime);
                        }
                        else
                        {
                            mapLoc.setText("TKF Vehicle");
                            from_time.setVisibility(View.GONE);
                            to_time.setVisibility(View.GONE);
                            viewLine.setVisibility(View.GONE);
                            viewHeader.setVisibility(View.GONE);
                        }

                    }catch (NullPointerException npe)
                    {
                        npe.printStackTrace();
                    }

                    return myContentView;
                }
            });
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void updateCurrentLocationMarker(Location currentLatLng){

        if(map != null){

            //getData();
            getVehicleRoute();
        }
    }

    private void getVehicleRoute()
    {
        try
        {

            String url = Constants.Vehicle_BikerRoute;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(BikerRouteActivity.this));

            ioUtils.getGETStringRequestHeader(BikerRouteActivity.this, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());
                    SetVehicleRouteResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void SetVehicleRouteResponse(String Response)
    {
        try {

            JSONObject jsonObject = new JSONObject(Response);

            JSONObject data = jsonObject.getJSONObject("data");

            JSONArray jsonArray = data.getJSONArray("routes");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                JSON_POJO js = new JSON_POJO();

                String location_name = jo_inside.getString("from_location");
                Double latitude = jo_inside.getDouble("from_lat");
                Double longitude = jo_inside.getDouble("from_lng");
                String id = jo_inside.getString("_id");
                String startTime = jo_inside.getString("startTime");
                String endTime = jo_inside.getString("endTime");

                js.setId(id);
                js.setLatitude(latitude);
                js.setLongitude(longitude);
                js.setLocation_name(location_name);
                js.setStartTime(startTime);
                js.setEndTime(endTime);

                jsonIndiaModelList.add(js);

                addMarkers(id,location_name,latitude,longitude,startTime,endTime);

                if(i > 0 )
                {
                    getMapsApiDirectionsUrl(latitude,longitude);
                }
                else
                {
                    Old_Origin = new LatLng(latitude, longitude);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startSocket()
    {
        try {
            socket = IO.socket(Constants.VehicleLiveLocation);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                try
                {
                    JSONObject obj = new JSONObject();
                    obj.put("hello", "server");
                    obj.put("binary", new byte[42]);
                    socket.emit("vehiclePosition", obj);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                //socket.disconnect();
                Log.i("Send","Data " + socket.id());
            }

        }).on("vehiclePosition", new Emitter.Listener() {

            @Override
            public void call(Object... args) {

                final JSONObject obj = (JSONObject)args[0];
                Log.i("Connect",obj.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DriverSocketLiveLocationResponse(obj.toString());
                    }
                });
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.i("DisConnect","Connect");
            }

        });
        socket.connect();
    }

    private void stopSocket()
    {
        socket.disconnect();
        Log.i("SocketService","Disconnected");
    }

    public void DriverSocketLiveLocationResponse(String resp) {
        //map.clear();
        try {

            scoketLiveMainPOJO = gson.fromJson(resp, SocketLiveMainPOJO.class);
            //nearestRouteMainPOJO = SharedPrefUtil.getNearestRouteMainPOJO(RouteActivity.this);


            if(SharedPrefUtil.getBikerUser(BikerRouteActivity.this).getData() != null)
            {
                if(!SharedPrefUtil.getBikerUser(BikerRouteActivity.this).getData().getVehicleId().isEmpty())
                {

                    if(scoketLiveMainPOJO.getData() != null)
                    {
                        String NearestVehicleRouteID = SharedPrefUtil.getBikerUser(BikerRouteActivity.this).getData().getVehicleId();
                        String SocketVehicleID = scoketLiveMainPOJO.getData().getVehicle_id();

                        Log.i("AssignedVehicle",SharedPrefUtil.getBikerUser(BikerRouteActivity.this).getData().getVehicleId());
                        Log.i("SocketVehicle",SocketVehicleID);

                        if(NearestVehicleRouteID.equalsIgnoreCase(SocketVehicleID))
                        {
                            if(markerVehicle != null)
                            {
                                    /*marker.setPosition(currentPosition);
                                    marker.setRotation((float) bearingBetweenLocations(oldLocation,newLocation));*/

                                geoArray = scoketLiveMainPOJO.getData().getGeo().getCoordinates();

                                Double cust_longitude = Double.parseDouble(geoArray[0]);
                                Double cust_latitude = Double.parseDouble(geoArray[1]);

                                newLocation = new LatLng(cust_latitude, cust_longitude);

                                final LatLng finalPosition = new LatLng(cust_latitude, cust_longitude);

                                LatLng currentPosition = new LatLng(
                                        cust_latitude,
                                        cust_longitude);

                                animateMarker(map,markerVehicle,finalPosition,false);
                                markerVehicle.setRotation((float) bearingBetweenLocations(oldLocation,newLocation));

                                oldLocation = newLocation;
                            }
                        }
                    }
                }
            }
            else
            {

            }

        } catch (Exception e) {
            //Toast.makeText(getActivity(), "exception", Toast.LENGTH_SHORT).show();
            Log.i(TAG, e.getMessage());
        }
    }

    private void addMarkers(String id,String location_name,Double latitude,Double longitude,final String startTime, final String endTime) {

        try
        {
            if(markerVehicle!=null){
                markerVehicle.remove();
            }

            LatLng dest = new LatLng(latitude, longitude);

            HashMap<String, String> data = new HashMap<String, String>();

            if (map != null) {
                Marker marker = map.addMarker(new MarkerOptions().position(dest)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker_hi))
                        .title(location_name));

                data.put(TAG_ID,id);
                data.put(TAG_LOCATION_NAME,location_name);
                data.put(TAG_LATITUDE, String.valueOf(latitude));
                data.put(TAG_LONGITUDE, String.valueOf(longitude));

                SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat sdfOutput = new SimpleDateFormat("hh:mm aa");

                Date d = sdfInput.parse(startTime);
                String startTimeFormatted = sdfOutput.format(d);

                Date d1 = sdfInput.parse(endTime);
                String endTimeFormatted = sdfOutput.format(d1);

                data.put(TAG_FROM_TIME,startTimeFormatted);
                data.put(TAG_TO_TIME,endTimeFormatted);

                extraMarkerInfo.put(marker.getId(),data);

                //String[] geoArray = nearestRouteMainPOJO.getData().get(0).getDist().getLocation().getCoordinates();

                newLocation = new LatLng(SharedPrefUtil.getLocation(BikerRouteActivity.this).getLatitude(),SharedPrefUtil.getLocation(BikerRouteActivity.this).getLongitude());

                markerVehicle = map.addMarker(new MarkerOptions().position(newLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_car))
                        .title("TKF Vehicle"));
            }

            oldLocation = newLocation;

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void getMapsApiDirectionsUrl(Double destLatitude, Double destLongitude) {

        try
        {

            String waypoints = "waypoints=optimize:true|"
                    + Old_Origin.latitude + "," + Old_Origin.longitude
                    + "|" + "|" + destLatitude + ","
                    + destLongitude;

            String sensor = "sensor=false";
            String key = "key=" + getString(R.string.googleMaps_ServerKey);
            String params = waypoints + "&" + sensor + "&" + key;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/"
                    + output + "?"+"origin="+Old_Origin.latitude + "," + Old_Origin.longitude+"&destination="+destLatitude + ","
                    + destLongitude +"&" + params;

            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);


            Old_Origin = new LatLng(destLatitude, destLongitude);

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
                //data = http.readUrl("https://maps.googleapis.com/maps/api/directions/json?origin=17.449797,78.373037&destination=17.47989,78.390095&%20waypoints=optimize:true|17.449797,78.373037||17.47989,78.390095&sensor=false");
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
            PolylineOptions polyLineOptions = null;
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
            }catch (Exception e)
            {
                e.printStackTrace();
            }
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
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);

            stopSocket();

            /*if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                // LOCATION SERVICE
                stopService(new Intent(this, LocationServiceNew.class));
                Log.e(TAG, "Location service is stopped");
            }*/

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }


    @Override
    public void NetworkOpen() {

    }

    @Override
    public void NetworkClose() {

        try {

            if (!NetworkUtils.isNetworkConnectionOn(this)) {
                FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;
            }

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }


    public static void animateMarker(final GoogleMap map, final Marker marker, final LatLng toPosition,
                                     final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

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

}
