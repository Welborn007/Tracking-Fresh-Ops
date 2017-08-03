package com.kesari.tkfops.BikerMap;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.kesari.tkfops.Map.HttpConnection;
import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.Map.PathJSONParser;
import com.kesari.tkfops.OpenOrders.OrderMainPOJO;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.FireToast;
import com.kesari.tkfops.network.IOUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by kesari on 03/07/17.
 */

public class BikerMapFragment extends Fragment implements OnMapReadyCallback {

    View f1;

    private Context mContext;
    private MapFragment supportMapFragment;
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
    private static View view;

    private Gson gson;
    private OrderMainPOJO orderMainPOJO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_biker_map, container, false);
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
            supportMapFragment = (MapFragment) fm.findFragmentById(R.id.map_container_biker);
            if (supportMapFragment == null) {
                supportMapFragment = MapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_container_biker, supportMapFragment).commit();
            }
            supportMapFragment.getMapAsync(this);

            //gps = new GPSTracker(getActivity());

            Current_Origin = new LatLng(SharedPrefUtil.getLocation(getActivity()).getLatitude(),SharedPrefUtil.getLocation(getActivity()).getLongitude());

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
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            map.setTrafficEnabled(true);

            CameraPosition cameraPosition = new CameraPosition.Builder().
                    target(Current_Origin).
                    tilt(0).
                    zoom(18).
                    bearing(0).
                    build();

            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(SharedPrefUtil.getLocation(getActivity()).getLatitude());
            location.setLongitude(SharedPrefUtil.getLocation(getActivity()).getLongitude());

            updateCurrentLocationMarker(location);

           //map.moveCamera(CameraUpdateFactory.newLatLngZoom(Current_Origin,18));

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    String Lat = String.valueOf(latLng.latitude);
                    String Long = String.valueOf(latLng.longitude);

                }
            });

            map.addMarker(new MarkerOptions().position(Current_Origin)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_biker))
                    .title("TKF Biker"));

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

                        Intent intent = new Intent(getActivity(), BikerMapActivity.class);
                        intent.putExtra("place",place);
                        intent.putExtra("id",id);
                        intent.putExtra("Lat", Double.parseDouble(latitude));
                        intent.putExtra("Lon", Double.parseDouble(longitude));
                        startActivity(intent);

                    }catch (NullPointerException npe)
                    {

                    }

                    return false;
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void updateCurrentLocationMarker(Location currentLatLng){

        if(map != null){


            //getData();

            getOrderList(getActivity());
        }
    }

    public void getOrderList(final Context context)
    {
        try
        {
            String url = Constants.BikerOrderListFilter + "Pending";

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    getOrderListResponse(result,context);
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
                FireToast.customSnackbar(getActivity(),"No Order Assigned!!!","Swipe");
            }
            else
            {
                JSONObject jsonObject = new JSONObject(Response);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

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


    private void addMarkers(String id,String location_name,Double latitude,Double longitude) {

        try
        {

            LatLng dest = new LatLng(latitude, longitude);

            HashMap<String, String> data = new HashMap<String, String>();

            if (map != null) {
                Marker marker = map.addMarker(new MarkerOptions().position(dest)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_customer))
                        .title(location_name));

                data.put(TAG_ID,id);
                data.put(TAG_LOCATION_NAME,location_name);
                data.put(TAG_LATITUDE, String.valueOf(latitude));
                data.put(TAG_LONGITUDE, String.valueOf(longitude));

                extraMarkerInfo.put(marker.getId(),data);

                map.addMarker(new MarkerOptions().position(Current_Origin)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_biker))
                        .title("TKF Vehicle"));
            }

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
