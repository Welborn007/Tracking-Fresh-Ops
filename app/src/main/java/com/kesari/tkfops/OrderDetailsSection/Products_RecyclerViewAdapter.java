package com.kesari.tkfops.OrderDetailsSection;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.kesari.tkfops.R;

import java.util.List;

/**
 * Created by kesari on 04/05/17.
 */

public class Products_RecyclerViewAdapter extends RecyclerView.Adapter<Products_RecyclerViewAdapter.RecyclerViewHolder>{

    private List<Products_POJO> OrdersListReView;

    public Products_RecyclerViewAdapter(List<Products_POJO> OrdersListReView)
    {
        this.OrdersListReView = OrdersListReView;

    }

    @Override
    public Products_RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_rowlayout,parent,false);

        Products_RecyclerViewAdapter.RecyclerViewHolder recyclerViewHolder = new Products_RecyclerViewAdapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(Products_RecyclerViewAdapter.RecyclerViewHolder holder, int position) {

        try {
            holder.order_name.setText(OrdersListReView.get(position).getProduct_id());
            
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
        CheckBox order_name;
        public RecyclerViewHolder(View view)
        {
            super(view);
            order_name = (CheckBox)view.findViewById(R.id.products_name);
        }
    }
}
