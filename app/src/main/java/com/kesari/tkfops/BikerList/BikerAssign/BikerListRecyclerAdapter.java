package com.kesari.tkfops.BikerList.BikerAssign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kesari on 30/06/17.
 */

public class BikerListRecyclerAdapter extends RecyclerView.Adapter<BikerListRecyclerAdapter.RecyclerViewHolder>{

    private List<BikerListSubPOJO> OrdersListReView;
    Context context;
    private String TAG = this.getClass().getSimpleName();
    private String orderID;

    public BikerListRecyclerAdapter(List<BikerListSubPOJO> OrdersListReView,Context context,String orderID)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
        this.orderID = orderID;
    }

    @Override
    public BikerListRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.biker_list_rowlayout,parent,false);

        BikerListRecyclerAdapter.RecyclerViewHolder recyclerViewHolder = new BikerListRecyclerAdapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(BikerListRecyclerAdapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.bikerName.setText(OrdersListReView.get(position).getBikerName());
            holder.bikeNo.setText(OrdersListReView.get(position).getBikeNo());
            holder.bikeCompany.setText(OrdersListReView.get(position).getBikeCompany());
            holder.bikeModel.setText(OrdersListReView.get(position).getBikeModel());
            holder.licenseNo.setText(OrdersListReView.get(position).getLicenseNo());
            holder.email.setText(OrdersListReView.get(position).getEmailId());

            holder.assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AssignBikerToOrder(OrdersListReView.get(position).get_id());
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

        Button assign,call;

        public RecyclerViewHolder(View view)
        {
            super(view);

            bikerName = (TextView)view.findViewById(R.id.bikerName);
            bikeNo = (TextView)view.findViewById(R.id.bikeNo);
            bikeCompany = (TextView)view.findViewById(R.id.bikeCompany);
            bikeModel = (TextView)view.findViewById(R.id.bikeModel);
            licenseNo = (TextView) view.findViewById(R.id.licenseNo);
            email = (TextView) view.findViewById(R.id.email);

            assign = (Button) view.findViewById(R.id.assign);
            call = (Button) view.findViewById(R.id.call);
        }
    }

    public void AssignBikerToOrder(String BikerID){

        try
        {

            String url = Constants.AssignOrderBiker;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("bikerId",BikerID);
                postObject.put("orderId",orderID);

                jsonObject.put("post",postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequestHeader(context,url, params ,jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());
                    AssignOrderResponse(result);
                }
            });


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public void AssignOrderResponse(String Response)
    {
        try
        {
            JSONObject json = new JSONObject(Response);

            JSONObject Message = json.getJSONObject("message");

            String _id = Message.getString("_id");

            if(!_id.isEmpty())
            {
                Toast.makeText(context, "Order assigned to Biker!!!", Toast.LENGTH_SHORT).show();

              /*  new SweetAlertDialog(context)
                        .setTitleText("Order assigned to Biker!!!")
                        .show();*/
                ((Activity)context).finish();
            }

        }catch (Exception e)
        {

        }
    }

}
