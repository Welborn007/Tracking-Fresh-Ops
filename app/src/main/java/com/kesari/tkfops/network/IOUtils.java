package com.kesari.tkfops.network;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.gson.Gson;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.ErrorPOJO;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by kesari on 13/04/17.
 */

public class IOUtils {

    private Gson gson;
    ErrorPOJO errorPOJO;

    public static DraweeController getFrescoImageController(Context context,String url) {

        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(
                    String id,
                    @Nullable ImageInfo imageInfo,
                    @Nullable Animatable anim) {

            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {

            }

            @Override
            public void onFailure(String id, Throwable throwable) {

            }
        };

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(url)
                // other setters
                .build();

        return controller;
    }

    public static GenericDraweeHierarchy getFrescoImageHierarchy(Context context) {


        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                .build();


        return hierarchy;
    }


    // Volley Get Request
    public void getGETStringRequest(String url,final VolleyCallback callback) {

        Log.i("url", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response.toString());

                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.toString());

                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);
                }catch (Exception e)
                {
                    Log.d("Error", e.getMessage());
                }
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(stringRequest, "");
    }

    public interface VolleyCallback{
        void onSuccess(String result);
    }

    //Volley JSON Object Post Request
    public void sendJSONObjectRequest(final Context context,String url, JSONObject jsonObject, final VolleyCallback callback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());

                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());

                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                    ErrorResponse(json,context);
                }catch (Exception e)
                {
                    Log.d("Error", e.getMessage());
                }
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    // Volley String Get Request with Header
    public void getGETStringRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders , final VolleyCallback callback) {

        //RequestQueue queue = Volley.newRequestQueue(this);
        Log.i("url", url);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        dialog.dismiss();
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //Log.d("ERROR","error => "+error.toString());
                        dialog.dismiss();

                        try{
                            String json = null;
                            NetworkResponse response = error.networkResponse;
                            json = new String(response.data);
                            Log.d("Error", json);

                            ErrorResponse(json,context);

                        }catch (Exception e)
                        {
                            //Log.d("Error", e.getMessage());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(postRequest, "");
    }

    //Volley JSON Object Put Request
    public void sendJSONObjectPutRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        dialog.dismiss();
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());
                dialog.dismiss();

                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                    ErrorResponse(json,context);

                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                    FireToast.customSnackbar(context, "Oops Something Went Wrong!!", "");
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    //Volley JSON Object Post Request
    public void sendJSONObjectRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders, JSONObject jsonObject, final VolleyCallback callback) {

        Log.i("url", url);
        Log.i("JSON CREATED", jsonObject.toString());
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response", response.toString());
                        dialog.dismiss();
                        callback.onSuccess(response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d("Error", "Error: " + error.getMessage());
                dialog.dismiss();

                try{
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    json = new String(response.data);
                    Log.d("Error", json);

                    ErrorResponse(json,context);

                }catch (Exception e)
                {
                    //Log.d("Error", e.getMessage());
                    FireToast.customSnackbar(context, "Oops Something Went Wrong!!", "");
                }
            }
        })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };;

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to request queue
        MyApplication.getInstance().addRequestToQueue(jsonObjReq, "");

    }

    // Volley String POST Request with Header
    public void getPOSTStringRequestHeader(final Context context, String url, final Map<String, String> paramsHeaders , final VolleyCallback callback) {

        //RequestQueue queue = Volley.newRequestQueue(this);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        Log.i("url", url);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        dialog.dismiss();
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        //Log.d("ERROR","error => "+error.toString());
                        dialog.dismiss();

                        try{
                            String json = null;
                            NetworkResponse response = error.networkResponse;
                            json = new String(response.data);


                            ErrorResponse(json,context);

                        }catch (Exception e)
                        {
                            //Log.d("Error", e.getMessage());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                /*Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", "Nintendo Gameboy");*/

                return paramsHeaders;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addRequestToQueue(postRequest, "");
    }

    private void ErrorResponse(String Response,Context context)
    {
        gson = new Gson();
        errorPOJO = gson.fromJson(Response, ErrorPOJO.class);
        Log.d("Error", Response);

        if(errorPOJO.getErrors() != null)
        {
            String[] error = errorPOJO.getErrors();
            String errorString = error[0];

            FireToast.customSnackbar(context, errorString,"");

        }
        else if(errorPOJO.getMessage() != null)
        {
            FireToast.customSnackbar(context, errorPOJO.getMessage(),"");
        }
        else
        {
            FireToast.customSnackbar(context, "Oops Something Went Wrong!!","");
        }


    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
