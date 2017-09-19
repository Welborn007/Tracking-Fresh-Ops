package com.kesari.tkfops.VehicleOrderReview;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.tkfops.Map.LocationServiceNew;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

public class VehicleOrderReviewActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    String OrderID = "";
    private RecyclerView recListProducts;
    private LinearLayoutManager ProductsLayout;
    private Gson gson;
    VehicleOrderReviewMainPOJO vehicleOrderReviewMainPOJO;
    private RecyclerView.Adapter adapterProducts;

    TextView total_price,payment_status,payment_mode,fullName,buildingName,landmark,address,mobileNo,bikerName,deliveryCharge,orderDate,orderDeliverDate,delivery_textData,orderNo;
    LinearLayout BikerHolder,deliveryDateHolder;
    FancyButton btnCall,btnCallCustomer;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_order_review);

        try
        {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            gson = new Gson();

            OrderID = getIntent().getStringExtra("orderID");
            recListProducts = (RecyclerView) findViewById(R.id.recyclerView);

            recListProducts.setHasFixedSize(true);
            ProductsLayout = new LinearLayoutManager(VehicleOrderReviewActivity.this);
            ProductsLayout.setOrientation(LinearLayoutManager.VERTICAL);
            recListProducts.setLayoutManager(ProductsLayout);

            total_price = (TextView) findViewById(R.id.total_price);
            payment_status = (TextView) findViewById(R.id.payment_status);
            payment_mode = (TextView) findViewById(R.id.payment_mode);
            fullName = (TextView) findViewById(R.id.fullName);
            buildingName = (TextView) findViewById(R.id.buildingName);
            landmark = (TextView) findViewById(R.id.landmark);
            address = (TextView) findViewById(R.id.address);
            mobileNo = (TextView) findViewById(R.id.mobileNo);
            deliveryCharge = (TextView) findViewById(R.id.deliveryCharge);
            orderNo = (TextView) findViewById(R.id.orderNo);
            orderDate = (TextView) findViewById(R.id.orderDate);
            orderDeliverDate = (TextView) findViewById(R.id.orderDeliverDate);
            delivery_textData = (TextView) findViewById(R.id.delivery_textData);

            BikerHolder = (LinearLayout) findViewById(R.id.BikerHolder);
            deliveryDateHolder = (LinearLayout) findViewById(R.id.deliveryDateHolder);
            bikerName = (TextView) findViewById(R.id.bikerName);
            btnCall = (FancyButton) findViewById(R.id.btnCall);
            btnCallCustomer = (FancyButton) findViewById(R.id.btnCallCustomer);



            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(VehicleOrderReviewActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    getOrderDetailsfromID();
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            getOrderDetailsfromID();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getOrderDetailsfromID() {
        try {

            String url = Constants.VehicleOrderDetails + OrderID;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(VehicleOrderReviewActivity.this));

            ioUtils.getGETStringRequestHeader(VehicleOrderReviewActivity.this, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());
                    swipeContainer.setRefreshing(false);
                    OrderDetailsResponse(result);
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

    private void OrderDetailsResponse(String Response)
    {
        try
        {
            vehicleOrderReviewMainPOJO = gson.fromJson(Response, VehicleOrderReviewMainPOJO.class);

            adapterProducts = new VehicleOrderReViewRecyclerAdapter(vehicleOrderReviewMainPOJO.getData().getOrders(),VehicleOrderReviewActivity.this);
            recListProducts.setAdapter(adapterProducts);

            total_price.setText(vehicleOrderReviewMainPOJO.getData().getTotal_price() + " .Rs");
            deliveryCharge.setText(vehicleOrderReviewMainPOJO.getData().getDelivery_charge() + " .Rs");

            if(vehicleOrderReviewMainPOJO.getData().getPayment_Status() != null)
            {
                payment_status.setText(vehicleOrderReviewMainPOJO.getData().getPayment_Status());
            }

            if(vehicleOrderReviewMainPOJO.getData().getPayment_Mode() != null)
            {
                payment_mode.setText(vehicleOrderReviewMainPOJO.getData().getPayment_Mode());
            }

            fullName.setText(vehicleOrderReviewMainPOJO.getData().getAddress().getFullName());
            buildingName.setText(vehicleOrderReviewMainPOJO.getData().getAddress().getFlat_No() + ", " + vehicleOrderReviewMainPOJO.getData().getAddress().getBuildingName());
            landmark.setText(vehicleOrderReviewMainPOJO.getData().getAddress().getLandmark());
            address.setText(vehicleOrderReviewMainPOJO.getData().getAddress().getCity() + ", " + vehicleOrderReviewMainPOJO.getData().getAddress().getState() + ", " + vehicleOrderReviewMainPOJO.getData().getAddress().getPincode());
            mobileNo.setText(vehicleOrderReviewMainPOJO.getData().getAddress().getMobileNo());

            btnCallCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = vehicleOrderReviewMainPOJO.getData().getAddress().getMobileNo();
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });

            orderNo.setText(vehicleOrderReviewMainPOJO.getData().getOrderNo());

            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd-MM-yyyy");
            Date d = sdfInput.parse(vehicleOrderReviewMainPOJO.getData().getCreatedAt());
            String orderDateFormatted = sdfOutput.format(d);
            orderDate.setText(orderDateFormatted);

            if(vehicleOrderReviewMainPOJO.getData().getStatus().equalsIgnoreCase("Delivered"))
            {
                deliveryDateHolder.setVisibility(View.VISIBLE);
                SimpleDateFormat deliverInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat deliverOutput = new SimpleDateFormat("dd-MM-yyyy");
                Date deliver = deliverInput.parse(vehicleOrderReviewMainPOJO.getData().getEditedAt());
                String orderdeliverDateFormatted = deliverOutput.format(deliver);
                orderDeliverDate.setText(orderdeliverDateFormatted);

                delivery_textData.setText(" delivered the order.");
            }
            else
            {
                deliveryDateHolder.setVisibility(View.GONE);
                delivery_textData.setText(" will deliver the order.");
            }

            if(vehicleOrderReviewMainPOJO.getData().getBiker() != null)
            {
                BikerHolder.setVisibility(View.VISIBLE);
                bikerName.setText(vehicleOrderReviewMainPOJO.getData().getBiker().getBikerName());

                btnCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phone = vehicleOrderReviewMainPOJO.getData().getBiker().getMobileNo();
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intent);
                    }
                });
            }



        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
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
                /*FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
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
