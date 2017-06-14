package com.kesari.tkfops.DeliveredOrders;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.kesari.tkfops.OpenOrders.Order_POJO;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.LocationServiceNew;
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

public class DeliveredOrderActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private RecyclerView.Adapter adapterOrders;
    private RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    List<Order_POJO> jsonIndiaModelList = new ArrayList<>();
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered_order);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            setTitle("Order Delivered");

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(DeliveredOrderActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }


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

    public void getData()
    {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                Order_POJO js = new Order_POJO();

                String order_id = jo_inside.getString("order_id");
                String id = jo_inside.getString("id");

                js.setId(id);
                js.setOrder_id(order_id);

                jsonIndiaModelList.add(js);

            }

            adapterOrders = new DeliveredOrdersRecycler_Adapter(jsonIndiaModelList);
            recListOrders.setAdapter(adapterOrders);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("orders");
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
