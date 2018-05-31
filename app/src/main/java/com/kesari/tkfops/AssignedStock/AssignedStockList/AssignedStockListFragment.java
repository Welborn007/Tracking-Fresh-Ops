package com.kesari.tkfops.AssignedStock.AssignedStockList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kesari on 27/06/17.
 */

public class AssignedStockListFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();
    private static View view;

    public static RecyclerView.Adapter adapterStockList;
    public static RecyclerView recListStockList;
    private LinearLayoutManager StockList;
    public static Gson gson;
    public static AssignedStockListMain stockListMain;

    public static RelativeLayout relativeLayout;
    public static TextView valueTV;

    private static SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_stock_list, container, false);

            gson = new Gson();
            recListStockList = (RecyclerView) view.findViewById(R.id.recyclerView);
            recListStockList.setHasFixedSize(true);
            StockList = new LinearLayoutManager(getActivity());
            StockList.setOrientation(LinearLayoutManager.VERTICAL);
            recListStockList.setLayoutManager(StockList);

            relativeLayout = (RelativeLayout) view.findViewById(R.id.relativelay_reclview);

            swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    getStockList(getActivity());
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            getStockList(getActivity());

        } catch (InflateException e) {
    /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try
        {



        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public static void getStockList(final Context context)
    {
        try
        {

            String url = Constants.AssignedStockList /*+ "593fe8634d0a046b730c3a14"*/;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("StockListFragment", result.toString());

                    getStockListResponse(result,context);

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
            Log.i("StockListFragment", e.getMessage());
        }
    }

    public static void getStockListResponse(String Response,Context context)
    {
        try
        {
            stockListMain = gson.fromJson(Response, AssignedStockListMain.class);
            valueTV = new TextView(context);

            if(stockListMain.getData().isEmpty())
            {
                //FireToast.customSnackbar(context, "No Products!!!", "");
                adapterStockList = new StockReViewRecyclerAdapter(stockListMain.getData(),context);
                recListStockList.setAdapter(adapterStockList);
                adapterStockList.notifyDataSetChanged();

                recListStockList.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
                relativeLayout.removeAllViews();
                valueTV.setText("No Products!!!");
                valueTV.setGravity(Gravity.CENTER);
                valueTV.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                ((RelativeLayout) relativeLayout).addView(valueTV);
            }
            else
            {
                relativeLayout.setVisibility(View.GONE);
                recListStockList.setVisibility(View.VISIBLE);

                adapterStockList = new StockReViewRecyclerAdapter(stockListMain.getData(),context);
                recListStockList.setAdapter(adapterStockList);
                adapterStockList.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.i("StockListFragment", e.getMessage());
        }
    }
}
