package com.kesari.tkfops.VehicleProfileData;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kesari.tkfops.Map.LocationServiceNew;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class VehicleProfileActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    private static View view;
    private String TAG = this.getClass().getSimpleName();

    private NetworkUtilsReceiver networkUtilsReceiver;
    CircleImageView profile_image;
    TextView driverName,vehicleNo,vehicleCompany,vehicleModel,vehicleRegNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_profile);

        try
        {

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(VehicleProfileActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            driverName = (TextView) findViewById(R.id.driverName);
            vehicleNo = (TextView) findViewById(R.id.vehicleNo);
            vehicleCompany = (TextView) findViewById(R.id.vehicleCompany);
            vehicleModel = (TextView) findViewById(R.id.vehicleModel);
            vehicleRegNo = (TextView) findViewById(R.id.vehicleRegNo);
            profile_image = (CircleImageView) findViewById(R.id.profile_image);

            getProfileData();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getProfileData() {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(VehicleProfileActivity.this));

            ioUtils.getPOSTStringRequestHeader(VehicleProfileActivity.this, Constants.VehicleProfile, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("profile_result",result);

                    profileDataResponse(result);

                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void profileDataResponse(String Response)
    {
        try
        {
            SharedPrefUtil.setVehicleUser(getApplicationContext(), Response.toString());

            driverName.setText(SharedPrefUtil.getVehicleUser(VehicleProfileActivity.this).getVehicleData().getDriverName());
            vehicleNo.setText(SharedPrefUtil.getVehicleUser(VehicleProfileActivity.this).getVehicleData().getVehicleNo());
            vehicleCompany.setText(SharedPrefUtil.getVehicleUser(VehicleProfileActivity.this).getVehicleData().getVehicleCompany());
            vehicleModel.setText(SharedPrefUtil.getVehicleUser(VehicleProfileActivity.this).getVehicleData().getVehicleModel());
            vehicleRegNo.setText(SharedPrefUtil.getVehicleUser(VehicleProfileActivity.this).getVehicleData().getVehicleRegNo());

            if(SharedPrefUtil.getVehicleUser(VehicleProfileActivity.this).getVehicleData().getVehicleImage() != null)
            {
                Picasso
                        .with(VehicleProfileActivity.this)
                        .load(SharedPrefUtil.getVehicleUser(VehicleProfileActivity.this).getVehicleData().getVehicleImage())
                        .into(profile_image);
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
