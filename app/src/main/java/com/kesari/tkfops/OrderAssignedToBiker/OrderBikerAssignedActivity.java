package com.kesari.tkfops.OrderAssignedToBiker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.tkfops.Map.LocationServiceNew;
import com.kesari.tkfops.OpenOrders.Order_POJO;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.FireToast;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderBikerAssignedActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt {

    private RecyclerView.Adapter adapterOrders;
    private RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    List<Order_POJO> jsonIndiaModelList = new ArrayList<>();
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    private Gson gson;
    private OrderAssignedMainPOJO orderAssignedMainPOJO;
    TextView pending,delivered,rejected;

    private RelativeLayout relativeLayout;
    private TextView valueTV;
    private SwipeRefreshLayout swipeContainer;
    private String valueOrder = "Pending";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_biker_assigned);

        try
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            setTitle("Biker Assigned Order");

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(OrderBikerAssignedActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            pending = (TextView) findViewById(R.id.pending);
            delivered = (TextView) findViewById(R.id.delivered);
            rejected = (TextView) findViewById(R.id.rejected);

            relativeLayout = (RelativeLayout) findViewById(R.id.relativelay_reclview);

            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

            recListOrders = (RecyclerView) findViewById(R.id.recyclerView);
            recListOrders.setHasFixedSize(true);
            Orders = new LinearLayoutManager(this);
            Orders.setOrientation(LinearLayoutManager.VERTICAL);
            recListOrders.setLayoutManager(Orders);


            pending.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.colorHighlight));

            pending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pending.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.colorHighlight));
                    delivered.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.white));
                    rejected.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.white));
                    getBikerAssignedOrderList("Pending");
                    valueOrder = "Pending";
                }
            });

            delivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getBikerAssignedOrderList("Delivered");
                    pending.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.white));
                    delivered.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.colorHighlight));
                    rejected.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.white));
                    valueOrder = "Delivered";
                }
            });

            rejected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getBikerAssignedOrderList("Rejected");
                    pending.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.white));
                    delivered.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.white));
                    rejected.setBackgroundColor(ContextCompat.getColor(OrderBikerAssignedActivity.this,R.color.colorHighlight));
                    valueOrder = "Rejected";
                }
            });

            getBikerAssignedOrderList(valueOrder);

            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    getBikerAssignedOrderList(valueOrder);
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            gson = new Gson();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void getBikerAssignedOrderList(String OrderStatus)
    {
        try
        {
            String url = Constants.BikerAssignedOrderList + OrderStatus;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(OrderBikerAssignedActivity.this));

            ioUtils.getGETStringRequestHeader(OrderBikerAssignedActivity.this, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    getOrderListResponse(result);

                    if(swipeContainer.isRefreshing())
                    {
                        swipeContainer.setRefreshing(false);
                    }
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getOrderListResponse(String Response)
    {
        try
        {
            orderAssignedMainPOJO = gson.fromJson(Response, OrderAssignedMainPOJO.class);

            valueTV = new TextView(OrderBikerAssignedActivity.this);

            if(orderAssignedMainPOJO.getData().isEmpty())
            {
                //FireToast.customSnackbar(OrderBikerAssignedActivity.this,"No Order Assigned!!!","Swipe");

                recListOrders.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout.removeAllViews();
                valueTV.setText("No Order Assigned!!!");
                valueTV.setGravity(Gravity.CENTER);
                valueTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                ((RelativeLayout) relativeLayout).addView(valueTV);

                adapterOrders = new BikerOrderAssignedRecycler_Adapter(orderAssignedMainPOJO.getData(),OrderBikerAssignedActivity.this);
                recListOrders.setAdapter(adapterOrders);
            }
            else
            {
                relativeLayout.setVisibility(View.GONE);
                recListOrders.setVisibility(View.VISIBLE);

                adapterOrders = new BikerOrderAssignedRecycler_Adapter(orderAssignedMainPOJO.getData(),OrderBikerAssignedActivity.this);
                recListOrders.setAdapter(adapterOrders);
            }

        } catch (Exception e) {
            Log.i("Openorder", e.getMessage());
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
}
