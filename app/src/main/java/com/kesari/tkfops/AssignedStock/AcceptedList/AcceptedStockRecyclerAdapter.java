package com.kesari.tkfops.AssignedStock.AcceptedList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kesari.tkfops.R;
import com.kesari.tkfops.network.IOUtils;

import java.util.List;

/**
 * Created by kesari on 27/06/17.
 */

public class AcceptedStockRecyclerAdapter extends RecyclerView.Adapter<AcceptedStockRecyclerAdapter.RecyclerViewHolder>{

    private List<AcceptedListSubPojo> OrdersListReView;
    Context context;
    private String TAG = this.getClass().getSimpleName();

    public AcceptedStockRecyclerAdapter(List<AcceptedListSubPojo> OrdersListReView, Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public AcceptedStockRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_accepted_rejected_row,parent,false);

        AcceptedStockRecyclerAdapter.RecyclerViewHolder recyclerViewHolder = new AcceptedStockRecyclerAdapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(AcceptedStockRecyclerAdapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.product_name.setText(OrdersListReView.get(position).getProductName());
            holder.quantity.setText(OrdersListReView.get(position).getAssigned_quantity());
            holder.price.setText(OrdersListReView.get(position).getSelling_price());

            holder.images.setController(IOUtils.getFrescoImageController(context,OrdersListReView.get(position).getProductImage()));
            holder.images.setHierarchy(IOUtils.getFrescoImageHierarchy(context));


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
        TextView product_name,quantity,price;
        SimpleDraweeView images;

        public RecyclerViewHolder(View view)
        {
            super(view);
            product_name = (TextView)view.findViewById(R.id.product_name);
            quantity = (TextView)view.findViewById(R.id.quantity);
            price = (TextView)view.findViewById(R.id.price);

            images = (SimpleDraweeView) view.findViewById(R.id.images);
        }
    }



}