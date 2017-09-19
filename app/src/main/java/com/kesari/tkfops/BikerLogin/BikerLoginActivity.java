package com.kesari.tkfops.BikerLogin;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.kesari.tkfops.BikerDashboard.BikerDashboardActivity;
import com.kesari.tkfops.R;
import com.kesari.tkfops.SelectLogin.LoginMain;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;

public class BikerLoginActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    FancyButton btnLogin;
    EditText user_name,password;
    private String TAG = this.getClass().getSimpleName();

    private NetworkUtilsReceiver networkUtilsReceiver;

    private Gson gson;
    LoginMain loginMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biker_login);

        /*Register receiver*/
        networkUtilsReceiver = new NetworkUtilsReceiver(this);
        registerReceiver(networkUtilsReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        gson = new Gson();

        btnLogin = (FancyButton) findViewById(R.id.btnLogin);
        user_name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);

        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        password.setFocusable(true);
                        password.requestFocus();

                        if(password.getTransformationMethod() == PasswordTransformationMethod.getInstance())
                        {
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                        else if(password.getTransformationMethod() == HideReturnsTransformationMethod.getInstance())
                        {
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }

                        return true;
                    }
                }
                return false;
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = user_name.getText().toString();
                String pass = password.getText().toString();

                if(!username.isEmpty() && !pass.isEmpty())
                {
                    sendSimpleLoginData(username,pass);
                    btnLogin.setClickable(false);
                }
                else if(username.isEmpty())
                {
                    user_name.setError("Username is empty");
                    //Toast.makeText(BikerLoginActivity.this, "Username is empty", Toast.LENGTH_SHORT).show();
                }
                else if(pass.isEmpty())
                {
                    password.setError("Password is empty");
                    //Toast.makeText(BikerLoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void sendSimpleLoginData(String vehicleNo, String Password) {

        try
        {
            String url = Constants.LoginBiker;

            Log.i("url", url);

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("bikeNo", vehicleNo);
                postObject.put("password", Password);

                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            ioUtils.sendJSONObjectRequest(BikerLoginActivity.this, url, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    SimpleResponse(result.toString());
                    btnLogin.setClickable(true);
                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {
                    btnLogin.setClickable(true);
                }
            });


        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    public void SimpleResponse(String Response) {
        try {

            loginMain = gson.fromJson(Response, LoginMain.class);

            if(loginMain.getUser().getOk().equalsIgnoreCase("true"))
            {

                SharedPrefUtil.setToken(BikerLoginActivity.this,loginMain.getUser().getToken());
                SharedPrefUtil.setKeyLoginType(BikerLoginActivity.this,"Biker");
                getProfileData(loginMain.getUser().getToken());
            }
            else if(loginMain.getUser().getOk().equalsIgnoreCase("false"))
            {
                //Toast.makeText(this, loginMain.getMessage(), Toast.LENGTH_SHORT).show();

                new SweetAlertDialog(this)
                        .setTitleText(loginMain.getMessage())
                        .show();
                user_name.setText("");
                password.setText("");
            }

        } catch (Exception jse) {
            Log.i(TAG, jse.getMessage());
        }
    }

    private void getProfileData(String Token) {
        try {

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + Token);

            ioUtils.getPOSTStringRequestHeader(BikerLoginActivity.this, Constants.BikerProfile, params, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("profile_result",result);

                    profileDataResponse(result);

                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void profileDataResponse(String Response)
    {
        try
        {
            SharedPrefUtil.setBikerUser(getApplicationContext(), Response.toString());
            Intent startMainActivity = new Intent(getApplicationContext(),BikerDashboardActivity.class);
            startActivity(startMainActivity);
            finish();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(networkUtilsReceiver);
        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }


    @Override
    public void NetworkOpen() {

    }

    @Override
    public void NetworkClose() {

        try {

            if (!NetworkUtils.isNetworkConnectionOn(this)) {
                /*FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;*/

                new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Oops! No internet access")
                        .setContentText("Please Check Settings")
                        .setConfirmText("Enable the Internet?")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }
}
