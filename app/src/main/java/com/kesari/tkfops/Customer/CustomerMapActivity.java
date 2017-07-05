package com.kesari.tkfops.Customer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import com.kesari.tkfops.Utilities.LocationServiceNew;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CustomerMapActivity extends AppCompatActivity implements OnMapReadyCallback , NetworkUtilsReceiver.NetworkResponseInt{

    View f1;
    private Context mContext;
    private SupportMapFragment supportMapFragment;
    //private GPSTracker gps;
    private LatLng Current_Origin;
    private GoogleMap map;
    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();

    private static final String TAG_ID = "id";
    private static final String TAG_LOCATION_NAME = "location_name";
    private static final String TAG_LATITUDE = "latitude";
    private static final String TAG_LONGITUDE = "longitude";

    private TextView instructions;

    private String TAG = this.getClass().getSimpleName();

    private NetworkUtilsReceiver networkUtilsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);

        try
        {

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(CustomerMapActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            f1 = (View) findViewById(R.id.map_customer);
            instructions = (TextView) findViewById(R.id.instructions);

            FragmentManager fm = getSupportFragmentManager();
            supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_customer);
            if (supportMapFragment == null) {
                supportMapFragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_customer, supportMapFragment).commit();
            }
            supportMapFragment.getMapAsync(this);

            //gps = new GPSTracker(CustomerMapActivity.this);

            Current_Origin = new LatLng(SharedPrefUtil.getLocation(CustomerMapActivity.this).getLatitude(), SharedPrefUtil.getLocation(CustomerMapActivity.this).getLongitude());

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
                return;
            }
            //map.setMyLocationEnabled(true);
            map.animateCamera(CameraUpdateFactory.zoomTo(15));

            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(SharedPrefUtil.getLocation(CustomerMapActivity.this).getLatitude());
            location.setLongitude(SharedPrefUtil.getLocation(CustomerMapActivity.this).getLongitude());

            //updateCurrentLocationMarker(location);

        /*Bundle bundle = this.getArguments();
        if (bundle != null) {

        }*/
            Double latitude = getIntent().getDoubleExtra("Lat", SharedPrefUtil.getLocation(CustomerMapActivity.this).getLatitude());
            Double longitude = getIntent().getDoubleExtra("Lon", SharedPrefUtil.getLocation(CustomerMapActivity.this).getLongitude());

            String place = getIntent().getStringExtra("place");
            String id = getIntent().getStringExtra("id");

            Log.i("place",place);

            addMarkers(id,place,latitude,longitude);
            getMapsApiDirectionsUrl(latitude,longitude);

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

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

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

        try
        {

            LatLng dest = new LatLng(latitude, longitude);

            HashMap<String, String> data = new HashMap<String, String>();

            if (map != null) {
                Marker marker = map.addMarker(new MarkerOptions().position(dest)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_man_icon))
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

            String distance = "";
            String duration = "";

            try {

                JSONObject jsonObjectMain = new JSONObject(result);

                JSONArray jsonArray = jsonObjectMain.getJSONArray("routes");

                JSONObject jsonObject = jsonArray.getJSONObject(0);

                JSONArray legs = jsonObject.getJSONArray("legs");

                JSONObject jsonObject1 = legs.getJSONObject(1);

                JSONObject jsonObject2 = jsonObject1.getJSONObject("distance");
                JSONObject jsonObject3 = jsonObject1.getJSONObject("duration");

                distance = jsonObject2.getString("text");
                duration = jsonObject3.getString("text");

                Log.i("Distance", String.valueOf(distance));
                //kilometre.setText(distance);

                Log.i("time", String.valueOf(duration));
                //ETA.setText("Estimated Delivery Time: " + duration);

                String EndAddress = jsonObject1.getString("end_address");
                //kilometre.setText("Vehicle is " + distance + " away at " + EndAddress);

                String StartAddress = jsonObject1.getString("start_address");
                //GuestAddress.setText("Your Address: " + StartAddress);

                JSONArray jsonArray1 = jsonObject1.getJSONArray("steps");
                JSONObject jsonObject4 = jsonArray1.getJSONObject(0);

                String Instructions = jsonObject4.getString("html_instructions");

                try {
                    // Convert from Unicode to UTF-8
                    //String string = "abc\u5639\u563b";
                    byte[] utf8 = Instructions.getBytes("UTF-8");

                    // Convert from UTF-8 to Unicode
                    Instructions = new String(utf8, "UTF-8");

                } catch (UnsupportedEncodingException e) {}

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.i("Direction",String.valueOf(Html.fromHtml(Instructions,Html.FROM_HTML_MODE_LEGACY)));
                    instructions.setText(String.valueOf(Html.fromHtml(Instructions,Html.FROM_HTML_MODE_LEGACY)));
                }
                else
                {
                    Log.i("Direction",String.valueOf(Html.fromHtml(Instructions)));
                    instructions.setText(String.valueOf(Html.fromHtml(Instructions)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);

            if (IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                // LOCATION SERVICE
                stopService(new Intent(this, LocationServiceNew.class));
                Log.e(TAG, "Location service is stopped");
            }

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
}
