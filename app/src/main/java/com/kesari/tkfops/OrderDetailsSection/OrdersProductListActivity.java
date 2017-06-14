package com.kesari.tkfops.OrderDetailsSection;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.kesari.tkfops.Map.HttpConnection;
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
import java.util.ArrayList;
import java.util.List;

public class OrdersProductListActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private RecyclerView.Adapter adapterOrders;
    private RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    List<Products_POJO> jsonIndiaModelList = new ArrayList<>();
    TextView order_name, customer_name, payment_mode, payment_confirm,distance_txt,time_txt;

    String distance = "";
    String duration = "";
    private String TAG = this.getClass().getSimpleName();

    private NetworkUtilsReceiver networkUtilsReceiver;
    //private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_product_list);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(OrdersProductListActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            setTitle("Products List");

            //gps = new GPSTracker(OrdersProductListActivity.this);

            order_name = (TextView) findViewById(R.id.order_number);
            order_name.setText(getIntent().getStringExtra("order_id"));

            customer_name = (TextView) findViewById(R.id.customer_name);
            payment_mode = (TextView) findViewById(R.id.payment_mode);
            payment_confirm = (TextView) findViewById(R.id.payment_confirm);
            time_txt = (TextView) findViewById(R.id.time_txt);
            distance_txt = (TextView) findViewById(R.id.distance_txt);

            customer_name.setText(getIntent().getStringExtra("customer_name"));
            payment_mode.setText(getIntent().getStringExtra("payment_mode"));
            payment_confirm.setText(getIntent().getStringExtra("payment_confirm"));

            getMapsApiDirectionsUrl(getIntent().getDoubleExtra("latitude",0),getIntent().getDoubleExtra("longitude",0));

            recListOrders = (RecyclerView) findViewById(R.id.recyclerView);
            recListOrders.setHasFixedSize(true);
            Orders = new LinearLayoutManager(this);
            Orders.setOrientation(LinearLayoutManager.VERTICAL);
            recListOrders.setLayoutManager(Orders);

            getData();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void getData() {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                Products_POJO js = new Products_POJO();

                String order_id = jo_inside.getString("Product_id");
                String id = jo_inside.getString("id");

                js.setId(id);
                js.setProduct_id(order_id);

                jsonIndiaModelList.add(js);

            }

            adapterOrders = new Products_RecyclerViewAdapter(jsonIndiaModelList);
            recListOrders.setAdapter(adapterOrders);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("products");
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

    public void getMapsApiDirectionsUrl(Double destLatitude, Double destLongitude) {

        try
        {

            String waypoints = "waypoints=optimize:true|"
                    + SharedPrefUtil.getLocation(OrdersProductListActivity.this).getLatitude() + "," + SharedPrefUtil.getLocation(OrdersProductListActivity.this).getLongitude()
                    + "|" + "|" + destLatitude + ","
                    + destLongitude;

            String sensor = "sensor=false";
            String key = "key=" + getString(R.string.googleMaps_ServerKey);
            String params = waypoints + "&" + sensor+ "&" + key;
            String output = "json";
            String url = "https://maps.googleapis.com/maps/api/directions/"
                    + output + "?" + "origin=" + SharedPrefUtil.getLocation(OrdersProductListActivity.this).getLatitude() + "," + SharedPrefUtil.getLocation(OrdersProductListActivity.this).getLongitude() + "&destination=" + destLatitude + ","
                    + destLongitude + "&" + params;

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
            Log.i("ReadTaskResult", result);



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

                distance_txt.setText(distance);
                time_txt.setText(duration);

                /*Log.i("Distance", String.valueOf(distance));
                kilometre.setText(distance);

                Log.i("time", String.valueOf(duration));
                ETA.setText("Estimated Delivery Time: " + duration);*/

               /* String EndAddress = jsonObject1.getString("end_address");
                kilometre.setText("Vehicle is " + distance + " away at " + EndAddress);

                String StartAddress = jsonObject1.getString("start_address");
                GuestAddress.setText("Your Address: " + StartAddress);*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
