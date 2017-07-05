package com.kesari.tkfops.AssignedStock.AcceptedList;

import android.app.Fragment;
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

public class AcceptedStockFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();

    private static View view;

    public RecyclerView.Adapter adapterAccepted;
    public RecyclerView recListAcceptedAccepted;
    private LinearLayoutManager Accepted;
    public Gson gson;
    public AcceptedListMain acceptedListMain;

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

            getAcceptedStockList();

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

            if(acceptedListMain.getData().isEmpty())
            {
                FireToast.customSnackbar(getActivity(), "No Products!!!", "");
            }
            else
            {
                adapterAccepted = new AcceptedStockRecyclerAdapter(acceptedListMain.getData(),getActivity());
                recListAcceptedAccepted.setAdapter(adapterAccepted);
            }


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

}
