package com.kesari.tkfops.AssignedStock.AcceptedList;

import android.app.Fragment;
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

public class AcceptedStockFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();

    private static View view;

    public RecyclerView.Adapter adapterAccepted;
    public RecyclerView recListAcceptedAccepted;
    private LinearLayoutManager Accepted;
    public Gson gson;
    public AcceptedListMain acceptedListMain;

    private RelativeLayout relativeLayout;
    private TextView valueTV;

    private SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_accepted_stock, container, false);

            gson = new Gson();
            recListAcceptedAccepted = (RecyclerView) view.findViewById(R.id.recyclerViewaccepted);

            recListAcceptedAccepted.setHasFixedSize(true);
            Accepted = new LinearLayoutManager(getActivity());
            Accepted.setOrientation(LinearLayoutManager.VERTICAL);
            recListAcceptedAccepted.setLayoutManager(Accepted);

            relativeLayout = (RelativeLayout) view.findViewById(R.id.relativelay_reclview);

            swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
            // Setup refresh listener which triggers new data loading
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    // Your code to refresh the list here.
                    // Make sure you call swipeContainer.setRefreshing(false)
                    // once the network request has completed successfully.
                    getAcceptedStockList();
                }
            });
            // Configure the refreshing colors
            swipeContainer.setColorSchemeResources(R.color.colorAccent,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            getAcceptedStockList();
            
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

    private void getAcceptedStockList()
    {
        try
        {

            String url = Constants.StockAcceptedList /*+ "593fe8634d0a046b730c3a14"*/;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

            ioUtils.getGETStringRequestHeader(getActivity(), url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    getAcceptedStockListResponse(result);

                    if(swipeContainer.isRefreshing())
                    {
                        swipeContainer.setRefreshing(false);
                    }
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getAcceptedStockListResponse(String Response)
    {
        try
        {
            acceptedListMain = gson.fromJson(Response, AcceptedListMain.class);
            valueTV = new TextView(getActivity());

            if(acceptedListMain.getData().isEmpty())
            {
                //FireToast.customSnackbar(getActivity(), "No Products!!!", "");

                adapterAccepted = new AcceptedStockRecyclerAdapter(acceptedListMain.getData(),getActivity());
                recListAcceptedAccepted.setAdapter(adapterAccepted);

                recListAcceptedAccepted.setVisibility(View.GONE);
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
                recListAcceptedAccepted.setVisibility(View.VISIBLE);

                adapterAccepted = new AcceptedStockRecyclerAdapter(acceptedListMain.getData(),getActivity());
                recListAcceptedAccepted.setAdapter(adapterAccepted);
            }


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

}
