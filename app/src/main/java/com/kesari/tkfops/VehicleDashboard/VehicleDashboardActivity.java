package com.kesari.tkfops.VehicleDashboard;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kesari.tkfops.AssignedStock.AssignedStockActivity;
import com.kesari.tkfops.BikerList.BikerLocation.BikerLocationListActivity;
import com.kesari.tkfops.Map.LocationServiceNew;
import com.kesari.tkfops.OpenOrders.OpenOrderFragment;
import com.kesari.tkfops.OrderAssignedToBiker.OrderBikerAssignedActivity;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Route.VehicleRouteActivity;
import com.kesari.tkfops.SelectLogin.SelectLoginActivity;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.VehicleDeliveredOrders.VehicleDeliveredOrderActivity;
import com.kesari.tkfops.VehicleProfileData.VehicleProfileActivity;
import com.kesari.tkfops.VehicleStockList.StockListActivity;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class VehicleDashboardActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, NetworkUtilsReceiver.NetworkResponseInt{

    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    boolean mShowingBack = false;
    TextView order_open,order_delivered,order_assign;
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;
    ImageView filter;
    String name;
    TextView name_Login;

    RelativeLayout assigned_stock_holder,stock_holder,route_holder,profile_holder,biker_holder;
    CircleImageView profile_image;
    TextView statVehicle;
    Switch vehicleStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_dashboard);

        try
        {
            final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            startService(new Intent(getBaseContext(), LocationServiceNew.class));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(VehicleDashboardActivity.this);
            }
            else
            {
                if (!IOUtils.isServiceRunning(LocationServiceNew.class, this)) {
                    // LOCATION SERVICE
                    startService(new Intent(this, LocationServiceNew.class));
                    Log.e(TAG, "Location service is already running");
                }
            }

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            /*toggle.setDrawerIndicatorEnabled(false);
            toggle.setHomeAsUpIndicator(R.drawable.ic_action_filter_list_order_sequence_sort_sorting_outline_512);*/
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            order_open = (TextView) findViewById(R.id.order_open);
            order_delivered = (TextView) findViewById(R.id.order_delivered);
            order_assign = (TextView) findViewById(R.id.order_assign);
            filter = (ImageView) findViewById(R.id.filter);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);

            name_Login = (TextView) header.findViewById(R.id.name_Login);
            assigned_stock_holder = (RelativeLayout) header.findViewById(R.id.assigned_stock_holder);
            stock_holder = (RelativeLayout) header.findViewById(R.id.stock_holder);
            route_holder = (RelativeLayout) header.findViewById(R.id.route_holder);
            profile_holder = (RelativeLayout) header.findViewById(R.id.profile_holder);
            biker_holder = (RelativeLayout) header.findViewById(R.id.biker_holder);
            profile_image = (CircleImageView) header.findViewById(R.id.profile_image);

            statVehicle = (TextView) findViewById(R.id.statVehicle);
            vehicleStatus = (Switch) findViewById(R.id.vehicleStatus);

            getProfileData();

            vehicleStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        statVehicle.setText("ON");
                        VehicleStatusOnOFF("ON");
                    }
                    else
                    {
                        statVehicle.setText("OFF");
                        VehicleStatusOnOFF("OFF");
                    }
                }
            });

            try
            {
                name = SharedPrefUtil.getVehicleUser(VehicleDashboardActivity.this).getVehicleData().getVehicleNo();
                name_Login.setText(name);

            }catch (Exception e)
            {
                name = "Guest";
            }

            profile_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VehicleDashboardActivity.this, VehicleProfileActivity.class);
                    startActivity(intent);
                }
            });

            assigned_stock_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VehicleDashboardActivity.this, AssignedStockActivity.class);
                    startActivity(intent);
                }
            });

            stock_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VehicleDashboardActivity.this, StockListActivity.class);
                    startActivity(intent);
                }
            });

            biker_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VehicleDashboardActivity.this, BikerLocationListActivity.class);
                    startActivity(intent);
                }
            });

            filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupWindow popupwindow_obj = popupDisplay();
//                popupwindow_obj.showAsDropDown(profile);
                    popupwindow_obj.showAtLocation(filter, Gravity.TOP| Gravity.RIGHT, 50, 150);
                }
            });

            order_assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VehicleDashboardActivity.this, OrderBikerAssignedActivity.class);
                    startActivity(intent);
                }
            });

            route_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VehicleDashboardActivity.this, VehicleRouteActivity.class);
                    startActivity(intent);
                }
            });

            order_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    flipMapCard();

                    String order_text = order_open.getText().toString();

                    if(order_text.equalsIgnoreCase("Show Order"))
                    {
                        order_open.setText("Show Map");
                    }
                    else if(order_text.equalsIgnoreCase("Show Map"))
                    {
                        order_open.setText("Show Order");
                    }

                }
            });

            order_delivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VehicleDashboardActivity.this, VehicleDeliveredOrderActivity.class);
                    startActivity(intent);

                    //flipCard();
                }
            });

            //flipCard();

            Log.i("vehicleTOken",SharedPrefUtil.getToken(VehicleDashboardActivity.this));

            if (savedInstanceState == null) {
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_holder, new MainMapFragment())
                        .commit();
            } else {
                mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
            }

            getFragmentManager().addOnBackStackChangedListener(this);

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getProfileData() {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(VehicleDashboardActivity.this));

            ioUtils.getPOSTStringRequestHeader(VehicleDashboardActivity.this, Constants.VehicleProfile, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("profile_result",result);

                    profileDataResponse(result);

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

            if(SharedPrefUtil.getVehicleUser(VehicleDashboardActivity.this).getVehicleData().getVehicleImage() != null)
            {
                Picasso
                        .with(VehicleDashboardActivity.this)
                        .load(SharedPrefUtil.getVehicleUser(VehicleDashboardActivity.this).getVehicleData().getVehicleImage())
                        .into(profile_image);
            }

            if(SharedPrefUtil.getVehicleUser(this).getVehicleData().getVehicleStatus().equalsIgnoreCase("ON"))
            {
                statVehicle.setText("ON");
                vehicleStatus.setChecked(true);
            }
            else
            {
                statVehicle.setText("OFF");
                vehicleStatus.setChecked(false);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void flipMapCard() {
        try
        {

            if (mShowingBack) {
                getFragmentManager().popBackStack();
                return;
            }

            // Flip to the back.

            mShowingBack = true;

            // Create and commit a new fragment transaction that adds the fragment for
            // the back of the card, uses custom animations, and is part of the fragment
            // manager's back stack.

            getFragmentManager()
                    .beginTransaction()

                    // Replace the default fragment animations with animator resources
                    // representing rotations when switching to the back of the card, as
                    // well as animator resources representing rotations when flipping
                    // back to the front (e.g. when the system Back button is pressed).
                    .setCustomAnimations(
                            R.animator.card_flip_right_in,
                            R.animator.card_flip_right_out,
                            R.animator.card_flip_left_in,
                            R.animator.card_flip_left_out)

                    // Replace any fragments currently in the container view with a
                    // fragment representing the next page (indicated by the
                    // just-incremented currentPage variable).
                    .replace(R.id.fragment_holder, new OpenOrderFragment())
                    .addToBackStack(null)
                    // Add this transaction to the back stack, allowing users to press
                    // Back to get to the front of the card.
                    // Commit the transaction.
                    .commit();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }


    @Override
    public void onBackStackChanged() {

        try
        {

            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);

            // When the back stack changes, invalidate the options menu (action bar).
            invalidateOptionsMenu();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public PopupWindow popupDisplay()
    {

        final PopupWindow popupWindow = new PopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_header_user, null);

        TextView nameTxt = (TextView) view.findViewById(R.id.name);
        nameTxt.setText("Hello " + name);

        TextView my_account = (TextView) view.findViewById(R.id.my_account);

        my_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleDashboardActivity.this, VehicleProfileActivity.class);
                startActivity(intent);
            }
        });

        CircleImageView imgUserimage = (CircleImageView) view.findViewById(R.id.imgUserimage);

        if(SharedPrefUtil.getVehicleUser(VehicleDashboardActivity.this).getVehicleData().getVehicleImage() != null)
        {
            Picasso
                    .with(VehicleDashboardActivity.this)
                    .load(SharedPrefUtil.getVehicleUser(VehicleDashboardActivity.this).getVehicleData().getVehicleImage())
                    .into(imgUserimage);
        }

        FancyButton logout = (FancyButton) view.findViewById(R.id.btnLogout);
        FancyButton NotifyUser = (FancyButton) view.findViewById(R.id.Notify);

        NotifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NearbyVehiclePushNotification();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopService(new Intent(getBaseContext(), LocationServiceNew.class));

                //Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(getApplicationContext())
                        .setTitleText("Logged Out")
                        .show();

                Intent i=new Intent(VehicleDashboardActivity.this,SelectLoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

                SharedPrefUtil.setClear(VehicleDashboardActivity.this);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        popupWindow.setFocusable(true);
        popupWindow.setWidth(width-140);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }

    private void NearbyVehiclePushNotification() {
        try {

            String url = Constants.NearbyVehiclePush;

            JSONObject jsonObject = new JSONObject();

            /*try {

                JSONObject postObject = new JSONObject();
                //postObject.put("status",VehicleStatus);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(VehicleDashboardActivity.this));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(VehicleDashboardActivity.this,url, params ,jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("Push Send Successfull", result.toString());
                }
            });


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void VehicleStatusOnOFF(final String VehicleStatus) {
        try {

            String url = Constants.VisibleInVisibleMode;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();
                postObject.put("status",VehicleStatus);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(VehicleDashboardActivity.this));

            ioUtils.sendJSONObjectPutRequestHeader(VehicleDashboardActivity.this, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());
                    VehicleStatusResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void VehicleStatusResponse(String Response)
    {
        //{"message":"OFF"}

        try
        {
            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");
            if(message.equalsIgnoreCase("ON"))
            {
                getProfileData();
            }
            else
            {
                getProfileData();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
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
