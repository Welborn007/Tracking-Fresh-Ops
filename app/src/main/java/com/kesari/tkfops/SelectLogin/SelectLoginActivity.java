package com.kesari.tkfops.SelectLogin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kesari.tkfops.BikerLogin.BikerLoginActivity;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Map.LocationServiceNew;
import com.kesari.tkfops.VehicleLogin.VehicleLoginActivity;
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

public class SelectLoginActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    RadioGroup loginGroup;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    boolean permission = false;
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_login);

        /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        loginGroup = (RadioGroup) findViewById(R.id.loginGroup);

        loginGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch(checkedId)
                {
                    case R.id.vehicle_login:
                        Intent startMainActivity = new Intent(getApplicationContext(),VehicleLoginActivity.class);
                        startActivity(startMainActivity);
                        finish();
                        break;
                    case R.id.biker_login:
                        Intent intent = new Intent(getApplicationContext(),BikerLoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {

            if(checkAndRequestPermissions())
            {
                final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
                {
                    IOUtils.buildAlertMessageNoGps(SelectLoginActivity.this);
                }
                else
                {
                    if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                        // LOCATION SERVICE
                        startService(new Intent(this, LocationServiceNew.class));
                        Log.e(TAG, "Location service is already running");
                    }
                }
            }
            else
            {

            }

            // Marshmallow+
            //permissionCheck();

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(permission)
        {
            Log.i("permission","backPressed");
            if(checkAndRequestPermissions())
            {
                final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
                {
                    IOUtils.buildAlertMessageNoGps(SelectLoginActivity.this);
                }
                else
                {
                    if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                        // LOCATION SERVICE
                        startService(new Intent(this, LocationServiceNew.class));
                        Log.e(TAG, "Location service is already running");
                    }
                }
            }
        }

    }

    private  boolean checkAndRequestPermissions() {
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        //int readPhonePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);


        List<String> listPermissionsNeeded = new ArrayList<>();
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        /*if (readPhonePermission != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }*/

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        String TAG = "PERMISSION";
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                //perms.put(android.Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);

                    // Check for both permissions
                    if (perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            /*&& perms.get(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED*/)
                    {
                        Log.d(TAG, "All permission granted");
                        Toast.makeText(getApplicationContext(),"All permission granted",Toast.LENGTH_LONG).show();

                        final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
                        {
                            IOUtils.buildAlertMessageNoGps(SelectLoginActivity.this);
                        }
                        else
                        {
                            if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                                // LOCATION SERVICE
                                startService(new Intent(this, LocationServiceNew.class));
                                Log.e(TAG, "Location service is already running");
                            }
                        }
                        //showDeviceDetails();
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                /*|| ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)*/)
                        {
                            if(checkAndRequestPermissions()) {
                                // carry on the normal flow, as the case of  permissions  granted.
                                final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

                                if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
                                {
                                    IOUtils.buildAlertMessageNoGps(SelectLoginActivity.this);
                                }
                                else
                                {
                                    if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                                        // LOCATION SERVICE
                                        startService(new Intent(this, LocationServiceNew.class));
                                        Log.e(TAG, "Location service is already running");
                                    }
                                }
                            }
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            //Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                            //proceed with logic by disabling the related features or quit the app.
                            showMessageForNeverAskAgain("Some core functionalities of the app might not work correctly without these permission. Go to settings and enable these for " + getString(R.string.app_name),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                    Uri.fromParts("package", getPackageName(), null)));

                                            permission = true;
                                        }
                                    }
                            );
                        }
                    }
                }
            }
        }
    }

    private void showMessageForNeverAskAgain(String message, DialogInterface.OnClickListener settingsListener) {
        new AlertDialog.Builder(SelectLoginActivity.this)
                .setTitle("Permission Necessary")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Settings", settingsListener)
                .create()
                .show();
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
