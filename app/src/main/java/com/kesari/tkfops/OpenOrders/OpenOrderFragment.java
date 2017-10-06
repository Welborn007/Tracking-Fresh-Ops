package com.kesari.tkfops.OpenOrders;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kesari on 04/05/17.
 */

public class OpenOrderFragment extends Fragment {

    private static RecyclerView.Adapter adapterOrders;
    private static RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    private String TAG = this.getClass().getSimpleName();
    private static Gson gson;
    private static OrderMainPOJO orderMainPOJO;

    private static RelativeLayout relativeLayout;
    private static TextView valueTV;
    private static SwipeRefreshLayout swipeContainer;

    Spinner spinFilter;
    String ListStatus = "Pending";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_open_order, container, false);
        recListOrders = (RecyclerView) V.findViewById(R.id.recyclerView);

        relativeLayout = (RelativeLayout) V.findViewById(R.id.relativelay_reclview);
        spinFilter = (Spinner) V.findViewById(R.id.spinFilter);
        swipeContainer = (SwipeRefreshLayout) V.findViewById(R.id.swipeContainer);

        return V;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        try
        {

            gson = new Gson();

            recListOrders.setHasFixedSize(true);
            Orders = new LinearLayoutManager(getActivity());
            Orders.setOrientation(LinearLayoutManager.VERTICAL);
            recListOrders.setLayoutManager(Orders);

            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    getOrderList(getActivity(),ListStatus);
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            spinFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ListStatus = spinFilter.getSelectedItem().toString().trim();
                    getOrderList(getActivity(),ListStatus);
                    //Log.i("spinData",spinFilter.getSelectedItem().toString().trim());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getOrderList(getActivity(),ListStatus);
    }

    public static void getOrderList(final Context context,String Status)
    {
        try
        {
            String url = Constants.OrderListFilter + Status;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("OPenOrder", result.toString());

                    getOrderListResponse(result,context);

                    if(swipeContainer.isRefreshing())
                    {
                        swipeContainer.setRefreshing(false);
                    }
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i("OPenOrder", e.getMessage());
        }
    }

    private static void getOrderListResponse(String Response,Context context)
    {
        try
        {
            orderMainPOJO = gson.fromJson(Response, OrderMainPOJO.class);
            valueTV = new TextView(context);

            if(orderMainPOJO.getData().isEmpty())
            {
                recListOrders.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout.removeAllViews();
                valueTV.setText("No Orders!!!");
                valueTV.setGravity(Gravity.CENTER);
                valueTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                ((RelativeLayout) relativeLayout).addView(valueTV);

                adapterOrders = new OrdersListRecycler_Adapter(orderMainPOJO.getData(),context);
                recListOrders.setAdapter(adapterOrders);
            }
            else
            {
                relativeLayout.setVisibility(View.GONE);
                recListOrders.setVisibility(View.VISIBLE);

                adapterOrders = new OrdersListRecycler_Adapter(orderMainPOJO.getData(),context);
                recListOrders.setAdapter(adapterOrders);
            }

        } catch (Exception e) {
            Log.i("Openorder", e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.gc();
    }
}