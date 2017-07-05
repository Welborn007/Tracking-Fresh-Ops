package com.kesari.tkfops.AssignedStock.AssignedStockList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.FireToast;
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

            getStockList(getActivity());

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

            if(stockListMain.getData().isEmpty())
            {
                FireToast.customSnackbar(context, "No Products!!!", "");
            }
            else
            {
                adapterStockList = new StockReViewRecyclerAdapter(stockListMain.getData(),context);
                recListStockList.setAdapter(adapterStockList);
            }

        } catch (Exception e) {
            Log.i("StockListFragment", e.getMessage());
        }
    }
}
