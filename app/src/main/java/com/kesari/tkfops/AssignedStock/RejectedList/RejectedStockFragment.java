package com.kesari.tkfops.AssignedStock.RejectedList;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class RejectedStockFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();

    private static View view;

    public RecyclerView.Adapter adapterOrders;
    public RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    public Gson gson;
    public RejectedListMain rejectedListMain;

    private RelativeLayout relativeLayout;
    private TextView valueTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_rejected_stock, container, false);

            gson = new Gson();
            recListOrders = (RecyclerView) view.findViewById(R.id.recyclerView);

            recListOrders.setHasFixedSize(true);
            Orders = new LinearLayoutManager(getActivity());
            Orders.setOrientation(LinearLayoutManager.VERTICAL);
            recListOrders.setLayoutManager(Orders);

            relativeLayout = (RelativeLayout) view.findViewById(R.id.relativelay_reclview);

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

        getRejectedStockList();

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void getRejectedStockList()
    {
        try
        {

            String url = Constants.StockRejectedList /*+ "593fe8634d0a046b730c3a14"*/;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(getActivity()));

            ioUtils.getGETStringRequestHeader(getActivity(), url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    getRejectedStockListResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void getRejectedStockListResponse(String Response)
    {
        try
        {
            rejectedListMain = gson.fromJson(Response, RejectedListMain.class);
            valueTV = new TextView(getActivity());

            if(rejectedListMain.getData().isEmpty())
            {
                //FireToast.customSnackbar(getActivity(), "No Products!!!", "");

                adapterOrders = new RejectedStockRecyclerAdapter(rejectedListMain.getData(),getActivity());
                recListOrders.setAdapter(adapterOrders);

                recListOrders.setVisibility(View.GONE);
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
                recListOrders.setVisibility(View.VISIBLE);

                adapterOrders = new RejectedStockRecyclerAdapter(rejectedListMain.getData(),getActivity());
                recListOrders.setAdapter(adapterOrders);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
