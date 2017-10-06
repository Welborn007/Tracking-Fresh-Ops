package com.kesari.tkfops.OpenOrders;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kesari.tkfops.BikerList.BikerAssign.DriverListActivity;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.RecyclerItemClickListener;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.VehicleOrderReview.VehicleOrderReviewActivity;
import com.kesari.tkfops.network.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by kesari-Aniket on 8/1/16.
 */

public class OrdersListRecycler_Adapter extends RecyclerView.Adapter<OrdersListRecycler_Adapter.RecyclerViewHolder>{

    private List<OrderSubPOJO> OrdersListReView;
    Context context;
    private String TAG = this.getClass().getSimpleName();
    String ReasonsValue = "";
    RecyclerView recyclerView;
    private RejectReasonMainPOJO reasonMainPOJO;
    Gson gson;
    private RejectReasons_RecyclerAdapter rejectReasons_recyclerAdapter;
    String ReasonData = "";


    public OrdersListRecycler_Adapter(List<OrderSubPOJO> OrdersListReView,Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public OrdersListRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list_rowlayout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(OrdersListRecycler_Adapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.order_number.setText(String.valueOf(position + 1));
            holder.customer_name.setText(OrdersListReView.get(position).getCreatedBy());
            holder.orderNo.setText(OrdersListReView.get(position).getOrderNo());

            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd-MM-yyyy");
            Date d = sdfInput.parse(OrdersListReView.get(position).getCreatedAt());
            String orderDateFormatted = sdfOutput.format(d);
            holder.orderDate.setText(orderDateFormatted);

            if(OrdersListReView.get(position).getPayment_Status() == null)
            {
                holder.payment_confirmHolder.setVisibility(View.GONE);
            }
            else
            {
                holder.payment_confirmHolder.setVisibility(View.VISIBLE);
                holder.payment_confirm.setText(OrdersListReView.get(position).getPayment_Status());
            }

            if(OrdersListReView.get(position).getPayment_Mode() == null)
            {
                holder.payment_modeHolder.setVisibility(View.GONE);
            }
            else
            {
                holder.payment_modeHolder.setVisibility(View.VISIBLE);
                holder.payment_mode.setText(OrdersListReView.get(position).getPayment_Mode());
            }

            if(OrdersListReView.get(position).getPickUp() != null)
            {
                if(OrdersListReView.get(position).getPickUp().equalsIgnoreCase("true"))
                {
                    holder.orderType.setText("Pick Up");
                }
                else
                {
                    holder.orderType.setText("Delivery");
                }
            }
            else
            {
                holder.orderType.setText("Delivery");
            }

            holder.total_price.setText(OrdersListReView.get(position).getTotal_price());

            if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Rejected"))
            {
                holder.order_status.setImageResource(R.drawable.rejected);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Accepted"))
            {
                holder.order_status.setImageResource(R.drawable.accepted);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Pending"))
            {
                holder.order_status.setImageResource(R.drawable.pending);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Cancelled"))
            {
                holder.order_status.setImageResource(R.drawable.cancel);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Delivered"))
            {
                holder.order_status.setImageResource(R.drawable.delivered);
            }

            if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Rejected"))
            {
                holder.cancelHolder.setVisibility(View.GONE);
                holder.rejectHolder.setVisibility(View.VISIBLE);
                holder.assign.setVisibility(View.GONE);
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.GONE);
                holder.path.setVisibility(View.GONE);

                if(OrdersListReView.get(position).getRejectReason() != null)
                {
                    if(!OrdersListReView.get(position).getRejectReason().isEmpty())
                    {
                        holder.rejectReason.setText(OrdersListReView.get(position).getRejectReason());
                        holder.rejectHolder.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        holder.rejectHolder.setVisibility(View.GONE);
                    }
                }
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Accepted"))
            {
                holder.rejectHolder.setVisibility(View.GONE);
                holder.cancelHolder.setVisibility(View.GONE);
                holder.reject.setVisibility(View.VISIBLE);
                holder.accept.setVisibility(View.GONE);
                holder.path.setVisibility(View.VISIBLE);

                if(OrdersListReView.get(position).getPickUp() != null)
                {
                    if(OrdersListReView.get(position).getPickUp().equalsIgnoreCase("true"))
                    {
                        holder.assign.setVisibility(View.GONE);
                    }
                    else
                    {
                        if(OrdersListReView.get(position).getBikerId() != null)
                        {
                            holder.assign.setVisibility(View.GONE);
                        }
                        else
                        {
                            holder.assign.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else
                {
                    if(OrdersListReView.get(position).getBikerId() != null)
                    {
                        holder.assign.setVisibility(View.GONE);
                    }
                    else
                    {
                        holder.assign.setVisibility(View.VISIBLE);
                    }
                }


            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Pending"))
            {
                holder.rejectHolder.setVisibility(View.GONE);
                holder.cancelHolder.setVisibility(View.GONE);
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);
                holder.assign.setVisibility(View.GONE);
                holder.path.setVisibility(View.VISIBLE);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Cancelled"))
            {
                holder.rejectHolder.setVisibility(View.GONE);
                holder.cancelHolder.setVisibility(View.VISIBLE);
                holder.accept.setVisibility(View.GONE);
                holder.reject.setVisibility(View.GONE);
                holder.assign.setVisibility(View.GONE);
                holder.path.setVisibility(View.GONE);

                if(OrdersListReView.get(position).getCancelReason() != null)
                {
                    if(!OrdersListReView.get(position).getCancelReason().isEmpty())
                    {
                        holder.cancelReason.setText(OrdersListReView.get(position).getCancelReason());
                        holder.cancelHolder.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        holder.cancelHolder.setVisibility(View.GONE);
                    }
                }
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Delivered"))
            {
                holder.rejectHolder.setVisibility(View.GONE);
                holder.order_status.setImageResource(R.drawable.delivered);
                holder.cancelHolder.setVisibility(View.GONE);
                holder.accept.setVisibility(View.GONE);
                holder.reject.setVisibility(View.GONE);
                holder.assign.setVisibility(View.GONE);
                holder.path.setVisibility(View.GONE);
            }


            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderDetails(OrdersListReView.get(position).get_id(),"Accepted","");
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fetchCancellationReasons(context,position);

                    //updateOrderDetails(OrdersListReView.get(position).get_id(),"Rejected");
                }
            });

            holder.assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderID = OrdersListReView.get(position).get_id();

                    Intent intent = new Intent(context, DriverListActivity.class);
                    intent.putExtra("orderID",orderID);
                    context.startActivity(intent);
                }
            });

            holder.path.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String uri = "http://maps.google.com/maps?f=d&hl=en"+"&daddr="+OrdersListReView.get(position).getAddress().getLatitude()+","+OrdersListReView.get(position).getAddress().getLongitude();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                    context.startActivity(Intent.createChooser(intent, "Select an application"));

                    /*Intent intent = new Intent(context, CustomerMapActivity.class);
                    intent.putExtra("place",OrdersListReView.get(position).getAddress().getFullName());
                    intent.putExtra("id",OrdersListReView.get(position).getAddressId());
                    intent.putExtra("Lat", Double.parseDouble(OrdersListReView.get(position).getAddress().getLatitude()));
                    intent.putExtra("Lon", Double.parseDouble(OrdersListReView.get(position).getAddress().getLongitude()));
                    context.startActivity(intent);*/
                }
            });

            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderID = OrdersListReView.get(position).get_id();

                    Intent intent = new Intent(context, VehicleOrderReviewActivity.class);
                    intent.putExtra("orderID",orderID);
                    context.startActivity(intent);
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
        TextView order_number,customer_name,payment_confirm,payment_mode,total_price,rejectReason,cancelReason,orderDate,orderNo,orderType;
        CardView subItemCard_view;
        ImageView order_status;
        LinearLayout payment_confirmHolder,payment_modeHolder,rejectHolder,cancelHolder;

        FancyButton accept,reject,pending,cancel,assign,path;

        public RecyclerViewHolder(View view)
        {
            super(view);
            order_number = (TextView)view.findViewById(R.id.order_number);
            customer_name = (TextView)view.findViewById(R.id.customer_name);
            payment_confirm = (TextView)view.findViewById(R.id.payment_confirm);
            payment_mode = (TextView)view.findViewById(R.id.payment_mode);
            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
            total_price = (TextView) view.findViewById(R.id.total_price);
            rejectReason = (TextView) view.findViewById(R.id.rejectReason);
            cancelReason = (TextView) view.findViewById(R.id.cancelReason);
            orderType = (TextView) view.findViewById(R.id.orderType);

            payment_confirmHolder = (LinearLayout) view.findViewById(R.id.payment_confirmHolder);
            payment_modeHolder = (LinearLayout) view.findViewById(R.id.payment_modeHolder);
            rejectHolder = (LinearLayout) view.findViewById(R.id.rejectHolder);
            cancelHolder = (LinearLayout) view.findViewById(R.id.cancelHolder);

            order_status = (ImageView) view.findViewById(R.id.order_status);

            accept = (FancyButton) view.findViewById(R.id.accept);
            reject = (FancyButton) view.findViewById(R.id.reject);
            pending = (FancyButton) view.findViewById(R.id.pending);
            cancel = (FancyButton) view.findViewById(R.id.cancel);

            assign = (FancyButton) view.findViewById(R.id.assign);
            path = (FancyButton) view.findViewById(R.id.path);

            orderDate = (TextView) view.findViewById(R.id.orderDate);
            orderNo = (TextView) view.findViewById(R.id.orderNo);
        }
    }

    private void fetchCancellationReasons(final Context context, final int pos) {
        try {

            String url = Constants.Reasons + "reject";

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.getGETStringRequestHeader(context, url, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());
                    CancelReasonsResponse(result,pos);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void CancelReasonsResponse(String Reasons, final int position)
    {
        try
        {
            gson = new Gson();
            reasonMainPOJO = gson.fromJson(Reasons,RejectReasonMainPOJO.class);

            // Create custom dialog object
            final Dialog dialog = new Dialog(context);
            // Include dialog.xml file
            dialog.setContentView(R.layout.rejection_reasons);
            // Set dialog title
            dialog.setTitle("Custom Dialog");

            Button cancel = (Button) dialog.findViewById(R.id.cancel);
            final EditText editText = (EditText) dialog.findViewById(R.id.other);

            recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setHasFixedSize(true);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);

            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {

                            ReasonData = reasonMainPOJO.getData().get(position).getReason();

                            if(ReasonData.equalsIgnoreCase("Other"))
                            {
                                editText.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                editText.setVisibility(View.GONE);
                                ReasonsValue = reasonMainPOJO.getData().get(position).getReason();
                            }
                        }
                    })
            );

            rejectReasons_recyclerAdapter = new RejectReasons_RecyclerAdapter(reasonMainPOJO.getData(), context);
            recyclerView.setAdapter(rejectReasons_recyclerAdapter);
            rejectReasons_recyclerAdapter.notifyDataSetChanged();

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(ReasonData.equalsIgnoreCase("Other"))
                    {
                        ReasonsValue = editText.getText().toString().trim();
                    }

                    if(!ReasonsValue.isEmpty())
                    {
                        updateOrderDetails(OrdersListReView.get(position).get_id(),"Rejected",ReasonsValue);
                        //Toast.makeText(context, ReasonsValue, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    else
                    {
                        //Toast.makeText(context, "Please mention reason!!!", Toast.LENGTH_SHORT).show();

                        new SweetAlertDialog(context)
                                .setTitleText("Please mention reason!!!")
                                .show();
                    }
                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());

            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            dialog.show();

        }catch (Exception w)
        {
            w.printStackTrace();
        }
    }

    private void updateOrderDetails(String orderID, String OrderStatus,String Remarks) {
        try {

            String url = Constants.UpdateOrder;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", orderID);
                postObject.put("status",OrderStatus);

                if(OrderStatus.equalsIgnoreCase("Rejected"))
                {
                    postObject.put("rejectReason",Remarks);
                }

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.sendJSONObjectPutRequestHeader(context, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    PaymentUpdateResponse(result);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void PaymentUpdateResponse(String Response)
    {
        try
        {

            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("Updated Successfull!!"))
            {
                OpenOrderFragment.getOrderList(context,"Pending");
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

}
