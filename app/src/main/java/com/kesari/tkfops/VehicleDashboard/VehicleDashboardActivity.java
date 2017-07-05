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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kesari.tkfops.VehicleDeliveredOrders.VehicleDeliveredOrderActivity;
import com.kesari.tkfops.SelectLogin.SelectLoginActivity;
import com.kesari.tkfops.OpenOrders.OpenOrderFragment;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Route.RouteActivity;
import com.kesari.tkfops.AssignedStock.AssignedStockActivity;
import com.kesari.tkfops.Utilities.LocationServiceNew;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.VehicleStockList.StockListActivity;
import com.kesari.tkfops.network.FireToast;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class VehicleDashboardActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, NetworkUtilsReceiver.NetworkResponseInt{

    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    boolean mShowingBack = false;
    TextView order_open,order_delivered,route;
    private String TAG = this.getClass().getSimpleName();
    private NetworkUtilsReceiver networkUtilsReceiver;
    ImageView filter;
    String name;
    TextView name_Login;

    RelativeLayout assigned_stock_holder,stock_holder;

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
            route = (TextView) findViewById(R.id.route);
            filter = (ImageView) findViewById(R.id.filter);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);

            name_Login = (TextView) header.findViewById(R.id.name_Login);
            assigned_stock_holder = (RelativeLayout) header.findViewById(R.id.assigned_stock_holder);
            stock_holder = (RelativeLayout) header.findViewById(R.id.stock_holder);

            try
            {
                name = SharedPrefUtil.getVehicleUser(VehicleDashboardActivity.this).getVehicleData().getVehicleNo();
                name_Login.setText(name);

            }catch (Exception e)
            {
                name = "Guest";
            }

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

            filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupWindow popupwindow_obj = popupDisplay();
//                popupwindow_obj.showAsDropDown(profile);
                    popupwindow_obj.showAtLocation(filter, Gravity.TOP| Gravity.RIGHT, 50, 150);
                }
            });


            route.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VehicleDashboardActivity.this, RouteActivity.class);
                    startActivity(intent);
                }
            });

            order_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
               /* Intent intent = new Intent(DashboardActivity.this, OpenOrderActivity.class);
                startActivity(intent);*/

                    flipMapCard();

                    String order_text = order_open.getText().toString();

                    if(order_text.equalsIgnoreCase("Order Open"))
                    {
                        order_open.setText("Map");
                    }
                    else if(order_text.equalsIgnoreCase("Map"))
                    {
                        order_open.setText("Order Open");
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

        /*my_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });*/

        CircleImageView imgUserimage = (CircleImageView) view.findViewById(R.id.imgUserimage);

        /*if(SharedPrefUtil.getUser(VehicleDashboardActivity.this).getData().getProfileImage() != null)
        {
            Picasso
                    .with(VehicleDashboardActivity.this)
                    .load(SharedPrefUtil.getUser(VehicleDashboardActivity.this).getData().getProfileImage())
                    .into(imgUserimage);
        }*/

        Button logout = (Button) view.findViewById(R.id.btnLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"Logged Out", Toast.LENGTH_SHORT).show();
                finish();
                Intent i=new Intent(VehicleDashboardActivity.this,SelectLoginActivity.class);
                startActivity(i);

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
