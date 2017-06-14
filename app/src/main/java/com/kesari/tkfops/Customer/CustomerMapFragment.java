package com.kesari.tkfops.Customer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kesari.tkfops.Map.HttpConnection;
import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.Map.PathJSONParser;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.SharedPrefUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by kesari on 03/05/17.
 */

public class CustomerMapFragment extends Fragment implements OnMapReadyCallback {

    View f1;
    private Context mContext;
    private SupportMapFragment supportMapFragment;
    //private GPSTracker gps;
    private LatLng Current_Origin;
    private GoogleMap map;
    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION_NAME= "location_name";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";

    private static final String TAG = "driver_Parameters";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_customer_map, container, false);
        f1 = (View) V.findViewById(R.id.map_customer);

        return V;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        FragmentManager fm = getActivity().getSupportFragmentManager();
        supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_customer);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_customer, supportMapFragment).commit();
        }
        supportMapFragment.getMapAsync(this);

        //gps = new GPSTracker(getActivity());

        Current_Origin = new LatLng(SharedPrefUtil.getLocation(getActivity()).getLatitude() ,SharedPrefUtil.getLocation(getActivity()).getLongitude());

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {

                        getFragmentManager().popBackStack();

                        return true;
                    }
                }
                return false;
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //getData();

        map = googleMap;
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(SharedPrefUtil.getLocation(getActivity()).getLatitude());
        location.setLongitude(SharedPrefUtil.getLocation(getActivity()).getLongitude());

        //updateCurrentLocationMarker(location);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Double latitude = bundle.getDouble("Lat", SharedPrefUtil.getLocation(getActivity()).getLatitude());
            Double longitude = bundle.getDouble("Lon", SharedPrefUtil.getLocation(getActivity()).getLongitude());

            String place = bundle.getString("place");
            String id = bundle.getString("id");

            Log.i("place",place);

            addMarkers(id,place,latitude,longitude);
            getMapsApiDirectionsUrl(latitude,longitude);
        }


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



                return false;
            }
        });
    }



    public void updateCurrentLocationMarker(Location currentLatLng){

        if(map != null){

            /*LatLng latLng = new LatLng(currentLatLng.getLatitude(),currentLatLng.getLongitude());
            if(currentPositionMarker == null){
                currentPositionMarker = new MarkerOptions();

                currentPositionMarker.position(latLng)
                        .title("My Location").
                        icon(BitmapDescriptorFactory.fromResource(R.drawable.van));
                currentLocationMarker = map.addMarker(currentPositionMarker);
            }

            if(currentLocationMarker != null)
                currentLocationMarker.setPosition(latLng);

            ///currentPositionMarker.position(latLng);
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));*/

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
                getMapsApiDirectionsUrl(latitude,longitude);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("mock_data.json");
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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_delivery_van))
                    .title("TKF Vehicle"));
        }
    }

    public void getMapsApiDirectionsUrl(Double destLatitude, Double destLongitude) {

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