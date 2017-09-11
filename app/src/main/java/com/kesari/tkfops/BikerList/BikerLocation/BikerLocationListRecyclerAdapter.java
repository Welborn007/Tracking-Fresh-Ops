package com.kesari.tkfops.BikerList.BikerLocation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kesari.tkfops.BikerList.BikerAssign.BikerListSubPOJO;
import com.kesari.tkfops.R;

import java.util.List;

/**
 * Created by kesari on 30/06/17.
 */

public class BikerLocationListRecyclerAdapter extends RecyclerView.Adapter<BikerLocationListRecyclerAdapter.RecyclerViewHolder>{

    private List<BikerListSubPOJO> OrdersListReView;
    Context context;
    private String TAG = this.getClass().getSimpleName();
    private String orderID;

    public BikerLocationListRecyclerAdapter(List<BikerListSubPOJO> OrdersListReView, Context context, String orderID)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
        this.orderID = orderID;
    }

    @Override
    public BikerLocationListRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.biker_location_rowlayout,parent,false);

        BikerLocationListRecyclerAdapter.RecyclerViewHolder recyclerViewHolder = new BikerLocationListRecyclerAdapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(BikerLocationListRecyclerAdapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.bikerName.setText(OrdersListReView.get(position).getBikerName());
            holder.bikeNo.setText(OrdersListReView.get(position).getBikeNo());
            holder.bikeCompany.setText(OrdersListReView.get(position).getBikeCompany());
            holder.bikeModel.setText(OrdersListReView.get(position).getBikeModel());
            holder.licenseNo.setText(OrdersListReView.get(position).getLicenseNo());
            holder.email.setText(OrdersListReView.get(position).getEmailId());

            holder.viewLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BikerMapLocationActivity.class);
                    intent.putExtra("BikerID",OrdersListReView.get(position).get_id());
                    context.startActivity(intent);
                }
            });

            holder.call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + OrdersListReView.get(position).getMobileNo()));
                    context.startActivity(callIntent);
                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return OrdersListReView.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView bikerName,bikeNo,bikeCompany,bikeModel,licenseNo,email;

        Button viewLocation,call;

        public RecyclerViewHolder(View view)
        {
            super(view);

            bikerName = (TextView)view.findViewById(R.id.bikerName);
            bikeNo = (TextView)view.findViewById(R.id.bikeNo);
            bikeCompany = (TextView)view.findViewById(R.id.bikeCompany);
            bikeModel = (TextView)view.findViewById(R.id.bikeModel);
            licenseNo = (TextView) view.findViewById(R.id.licenseNo);
            email = (TextView) view.findViewById(R.id.email);

            viewLocation = (Button) view.findViewById(R.id.viewLocation);
            call = (Button) view.findViewById(R.id.call);
        }
    }
}
