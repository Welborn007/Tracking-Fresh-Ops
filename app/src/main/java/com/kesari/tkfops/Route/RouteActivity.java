package com.kesari.tkfops.Route;

import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kesari.tkfops.Map.GPSTracker;
import com.kesari.tkfops.Map.HttpConnection;
import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.Map.PathJSONParser;
import com.kesari.tkfops.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Context mContext;
    private MapFragment supportMapFragment;
    private GPSTracker gps;
    private LatLng Current_Origin;
    private LatLng Old_Origin;
    private GoogleMap map;
    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION_NAME= "location_name";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";

    private static final String TAG = "driver_Parameters";
    private static View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        FragmentManager fm = getFragmentManager();
        supportMapFragment = (MapFragment) fm.findFragmentById(R.id.map_container);
        if (supportMapFragment == null) {
            supportMapFragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);

        gps = new GPSTracker(RouteActivity.this);

        Current_Origin = new LatLng(gps.getLatitude(), gps.getLongitude());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //getData();

        map = googleMap;
        map.setMyLocationEnabled(true);
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(gps.getLatitude());
        location.setLongitude(gps.getLongitude());

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


                }catch (NullPointerException npe)
                {

                }


                return false;
            }
        });
    }

    public void updateCurrentLocationMarker(Location currentLatLng){

        if(map != null){

            getData();
        }
    }

    public void getData()
    {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                JSON_POJO js = new JSON_POJO();

                String location_name = jo_inside.getString("location_name");
                Double latitude = jo_inside.getDouble("latitude");
                Double longitude = jo_inside.getDouble("longitude");
                String id = jo_inside.getString("id");

                js.setId(id);
                js.setLatitude(latitude);
                js.setLongitude(longitude);
                js.setLocation_name(location_name);

                jsonIndiaModelList.add(js);

                addMarkers(id,location_name,latitude,longitude);

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

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("mock_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void addMarkers(String id,String location_name,Double latitude,Double longitude) {

        LatLng dest = new LatLng(latitude, longitude);

        HashMap<String, String> data = new HashMap<String, String>();

        if (map != null) {
            Marker marker = map.addMarker(new MarkerOptions().position(dest)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_icon))
                    .title(location_name));

            data.put(TAG_ID,id);
            data.put(TAG_LOCATION_NAME,location_name);
            data.put(TAG_LATITUDE, String.valueOf(latitude));
            data.put(TAG_LONGITUDE, String.valueOf(longitude));

            extraMarkerInfo.put(marker.getId(),data);

            map.addMarker(new MarkerOptions().position(Current_Origin)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_van))
                    .title("TKF Vehicle"));
        }
    }

    public void getMapsApiDirectionsUrl(Double destLatitude, Double destLongitude) {

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
}
