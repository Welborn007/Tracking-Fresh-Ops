package com.kesari.tkfops.OrderReview;

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
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.LocationServiceNew;
import com.kesari.tkfops.network.FireToast;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.HashMap;
import java.util.Map;

public class OrderReviewActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    String OrderID = "";
    private RecyclerView recListProducts;
    private LinearLayoutManager ProductsLayout;
    private Gson gson;
    OrderReviewMainPOJO orderReviewMainPOJO;
    private RecyclerView.Adapter adapterProducts;

    TextView total_price,payment_status,payment_mode,fullName,buildingName,landmark,address,mobileNo;

    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_review);

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
            ProductsLayout = new LinearLayoutManager(OrderReviewActivity.this);
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

            btnSubmit = (Button) findViewById(R.id.btnSubmit);

            final String orderID = getIntent().getStringExtra("orderID");

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(OrderReviewActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            getOrderDetailsfromID();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getOrderDetailsfromID() {
        try {

            String url = Constants.OrderDetails + OrderID;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IndlbGJvcm5tYWNoYWRvQGdtYWlsLmNvbSIsImlhdCI6MTQ5NzI1ODU0Miwic3ViIjoiY2oyZnUxOXZ1MDAwMm1uMXR0cGVjZ2hyeiJ9.LO3nS6c_Epckw5g7wquxatGyAGgrOlSrIbydbl6U02o");

            ioUtils.getGETStringRequestHeader(OrderReviewActivity.this, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    OrderDetailsResponse(result);
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
            orderReviewMainPOJO = gson.fromJson(Response, OrderReviewMainPOJO.class);

            adapterProducts = new OrderReViewRecyclerAdapter(orderReviewMainPOJO.getData().getOrder(),OrderReviewActivity.this);
            recListProducts.setAdapter(adapterProducts);

            payment_status = (TextView) findViewById(R.id.payment_status);
            payment_mode = (TextView) findViewById(R.id.payment_mode);
            fullName = (TextView) findViewById(R.id.fullName);
            buildingName = (TextView) findViewById(R.id.buildingName);
            landmark = (TextView) findViewById(R.id.landmark);
            address = (TextView) findViewById(R.id.address);
            mobileNo = (TextView) findViewById(R.id.mobileNo);

            total_price.setText(orderReviewMainPOJO.getData().getTotal_price() + " .Rs");

            if(orderReviewMainPOJO.getData().getPayment_Status() != null)
            {
                payment_status.setText(orderReviewMainPOJO.getData().getPayment_Status());
            }

            if(orderReviewMainPOJO.getData().getPayment_Mode() != null)
            {
                payment_mode.setText(orderReviewMainPOJO.getData().getPayment_Mode());
            }

            fullName.setText(orderReviewMainPOJO.getData().getAddress().getFullName());
            buildingName.setText(orderReviewMainPOJO.getData().getAddress().getFlat_No() + ", " + orderReviewMainPOJO.getData().getAddress().getBuildingName());
            landmark.setText(orderReviewMainPOJO.getData().getAddress().getLandmark());
            address.setText(orderReviewMainPOJO.getData().getAddress().getCity() + ", " + orderReviewMainPOJO.getData().getAddress().getState() + ", " + orderReviewMainPOJO.getData().getAddress().getPincode());
            mobileNo.setText(orderReviewMainPOJO.getData().getAddress().getMobileNo());

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
