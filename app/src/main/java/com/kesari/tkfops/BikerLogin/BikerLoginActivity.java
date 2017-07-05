package com.kesari.tkfops.BikerLogin;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kesari.tkfops.BikerDashboard.BikerDashboardActivity;
import com.kesari.tkfops.R;
import com.kesari.tkfops.SelectLogin.LoginMain;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.FireToast;
import com.kesari.tkfops.network.IOUtils;
import com.kesari.tkfops.network.NetworkUtils;
import com.kesari.tkfops.network.NetworkUtilsReceiver;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BikerLoginActivity extends AppCompatActivity implements NetworkUtilsReceiver.NetworkResponseInt{

    Button btnLogin;
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

        btnLogin = (Button) findViewById(R.id.btnLogin);
        user_name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = user_name.getText().toString();
                String pass = password.getText().toString();

                if(!username.isEmpty() && !pass.isEmpty())
                {
                    sendSimpleLoginData(username,pass);

                }
                else if(username.isEmpty())
                {
                    Toast.makeText(BikerLoginActivity.this, "Username is empty", Toast.LENGTH_SHORT).show();
                }
                else if(pass.isEmpty())
                {
                    Toast.makeText(BikerLoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, loginMain.getMessage(), Toast.LENGTH_SHORT).show();
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
                FireToast.customSnackbarWithListner(this, "No internet access", "Settings", new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
                return;
            }

        }catch (Exception e)
        {
            Log.i(TAG,e.getMessage());
        }
    }
}