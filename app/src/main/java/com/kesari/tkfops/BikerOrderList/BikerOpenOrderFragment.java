package com.kesari.tkfops.BikerOrderList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kesari on 03/07/17.
 */

public class BikerOpenOrderFragment extends Fragment {

    private static RecyclerView.Adapter adapterOrders;
    private static RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    //private GPSTracker gps;
    private String TAG = this.getClass().getSimpleName();

    private static Gson gson;
    private static BikerOrderMainPOJO bikerOrderMainPOJO;

    private static RelativeLayout relativeLayout;
    private static TextView valueTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_biker_open_order, container, false);
        recListOrders = (RecyclerView) V.findViewById(R.id.recyclerView);
        relativeLayout = (RelativeLayout) V.findViewById(R.id.relativelay_reclview);

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

            //getData();
            getOrderList(getActivity());

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public static void getOrderList(final Context context)
    {
        try
        {
            String url = Constants.BikerOrderListFilter + "Pending";

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("OPenOrder", result.toString());

                    getOrderListResponse(result,context);
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
            bikerOrderMainPOJO = gson.fromJson(Response, BikerOrderMainPOJO.class);
            valueTV = new TextView(context);

            if(bikerOrderMainPOJO.getData().isEmpty())
            {
                recListOrders.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout.removeAllViews();
                valueTV.setText("No Order Assigned!!!");
                valueTV.setGravity(Gravity.CENTER);
                valueTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                ((RelativeLayout) relativeLayout).addView(valueTV);

                adapterOrders = new BikerOrderRecyclerAdapter(bikerOrderMainPOJO.getData(),context);
                recListOrders.setAdapter(adapterOrders);
            }
            else
            {
                relativeLayout.setVisibility(View.GONE);
                recListOrders.setVisibility(View.VISIBLE);

                adapterOrders = new BikerOrderRecyclerAdapter(bikerOrderMainPOJO.getData(),context);
                recListOrders.setAdapter(adapterOrders);
            }

        } catch (Exception e) {
            Log.i("Openorder", e.getMessage());
        }
    }

}