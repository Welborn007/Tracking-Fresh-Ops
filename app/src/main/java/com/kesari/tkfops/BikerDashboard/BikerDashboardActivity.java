package com.kesari.tkfops.BikerDashboard;

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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kesari.tkfops.BikerDeliveredOrder.BikerDeliveredOrderActivity;
import com.kesari.tkfops.BikerMap.BikerMapFragment;
import com.kesari.tkfops.BikerOrderList.BikerOpenOrderFragment;
import com.kesari.tkfops.BikerProfileData.BikerProfileActivity;
import com.kesari.tkfops.Map.LocationServiceNew;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Route.BikerRouteActivity;
import com.kesari.tkfops.SelectLogin.SelectLoginActivity;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

public class BikerDashboardActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener,NetworkUtilsReceiver.NetworkResponseInt{

    private String TAG = this.getClass().getSimpleName();
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    private NetworkUtilsReceiver networkUtilsReceiver;
    ImageView filter;
    String name;
    TextView name_Login;
    boolean mShowingBack = false;
    TextView order_open,order_delivered,route;
    RelativeLayout profile_holder,route_holder;
    CircleImageView profile_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biker_dashboard);

        try
        {

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            /*Register receiver*/
            networkUtilsReceiver = new NetworkUtilsReceiver(this);
            registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            startService(new Intent(getBaseContext(), LocationServiceNew.class));

            final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) )
            {
                IOUtils.buildAlertMessageNoGps(BikerDashboardActivity.this);
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

            filter = (ImageView) findViewById(R.id.filter);
            order_open = (TextView) findViewById(R.id.order_open);
            order_delivered = (TextView) findViewById(R.id.order_delivered);

            filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupWindow popupwindow_obj = popupDisplay();
//                popupwindow_obj.showAsDropDown(profile);
                    popupwindow_obj.showAtLocation(filter, Gravity.TOP| Gravity.RIGHT, 50, 150);
                }
            });

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);

            name_Login = (TextView) header.findViewById(R.id.name_Login);
            profile_holder = (RelativeLayout) header.findViewById(R.id.profile_holder);
            route_holder = (RelativeLayout) header.findViewById(R.id.route_holder);
            profile_image = (CircleImageView) header.findViewById(R.id.profile_image);

            try
            {
                name = SharedPrefUtil.getBikerUser(BikerDashboardActivity.this).getData().getBikerName();
                name_Login.setText(name);

            }catch (Exception e)
            {
                name = "Guest";
            }

            profile_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BikerDashboardActivity.this, BikerProfileActivity.class);
                    startActivity(intent);
                }
            });

            route_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BikerDashboardActivity.this, BikerRouteActivity.class);
                    startActivity(intent);
                }
            });

            if(SharedPrefUtil.getBikerUser(BikerDashboardActivity.this).getData().getBikerImage() != null)
            {
                Picasso.with(BikerDashboardActivity.this)
                        .load(SharedPrefUtil.getBikerUser(BikerDashboardActivity.this).getData().getBikerImage())
                        .into(profile_image);
            }

            order_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
               /* Intent intent = new Intent(DashboardActivity.this, OpenOrderActivity.class);
                startActivity(intent);*/

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
                    Intent intent = new Intent(BikerDashboardActivity.this, BikerDeliveredOrderActivity.class);
                    startActivity(intent);

                    //flipCard();
                }
            });

            if (savedInstanceState == null) {
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.Biker_holder, new BikerMapFragment())
                        .commit();
            } else {
                mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
            }

            getFragmentManager().addOnBackStackChangedListener(this);

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
                    .replace(R.id.Biker_holder, new BikerOpenOrderFragment())
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

        View view = inflater.inflate(R.layout.custom_header_user_biker, null);

        TextView nameTxt = (TextView) view.findViewById(R.id.name);
        nameTxt.setText("Hello " + name);

        TextView my_account = (TextView) view.findViewById(R.id.my_account);

        my_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BikerDashboardActivity.this, BikerProfileActivity.class);
                startActivity(intent);
            }
        });

        CircleImageView imgUserimage = (CircleImageView) view.findViewById(R.id.imgUserimage);

        if(SharedPrefUtil.getBikerUser(BikerDashboardActivity.this).getData().getBikerImage() != null)
        {
            Picasso.with(BikerDashboardActivity.this)
                    .load(SharedPrefUtil.getBikerUser(BikerDashboardActivity.this).getData().getBikerImage())
                    .into(imgUserimage);
        }

        FancyButton logout = (FancyButton) view.findViewById(R.id.btnLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopService(new Intent(getBaseContext(), LocationServiceNew.class));

                //Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(getApplicationContext())
                        .setTitleText("Logged Out")
                        .show();

                Intent i=new Intent(BikerDashboardActivity.this,SelectLoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

                SharedPrefUtil.setClear(BikerDashboardActivity.this);
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
