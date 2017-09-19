package com.kesari.tkfops.BikerList.BikerAssign;

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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.Map.LocationServiceNew;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DriverListActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    private RecyclerView.Adapter adapterOrders;
    private RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    private Gson gson;
    //private OrderMainPOJO orderMainPOJO;
    private BikerListMainPOJO bikerListMainPOJO;
    private String orderID;

    private RelativeLayout relativeLayout;
    private TextView valueTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_list);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();
            orderID = getIntent().getStringExtra("orderID");
            recListOrders = (RecyclerView) findViewById(R.id.recyclerView);

            recListOrders.setHasFixedSize(true);
            Orders = new LinearLayoutManager(DriverListActivity.this);
            Orders.setOrientation(LinearLayoutManager.VERTICAL);
            recListOrders.setLayoutManager(Orders);

            relativeLayout = (RelativeLayout) findViewById(R.id.relativelay_reclview);

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(DriverListActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            getBikerList();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getBikerList()
    {
        try
        {

            String url = Constants.BikerList;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(DriverListActivity.this));

            ioUtils.getGETStringRequestHeader(DriverListActivity.this, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    getOrderListResponse(result);
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

    private void getOrderListResponse(String Response)
    {
        try
        {
            bikerListMainPOJO = gson.fromJson(Response,BikerListMainPOJO.class);

            valueTV = new TextView(DriverListActivity.this);

            if(bikerListMainPOJO.getData().isEmpty())
            {
                recListOrders.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout.removeAllViews();
                valueTV.setText("No Biker Assigned!!!");
                valueTV.setGravity(Gravity.CENTER);
                valueTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                ((RelativeLayout) relativeLayout).addView(valueTV);

                adapterOrders = new BikerListRecyclerAdapter(bikerListMainPOJO.getData(),DriverListActivity.this,orderID);
                recListOrders.setAdapter(adapterOrders);
            }
            else
            {
                relativeLayout.setVisibility(View.GONE);
                recListOrders.setVisibility(View.VISIBLE);

                adapterOrders = new BikerListRecyclerAdapter(bikerListMainPOJO.getData(),DriverListActivity.this,orderID);
                recListOrders.setAdapter(adapterOrders);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
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
            }
*/
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
}
