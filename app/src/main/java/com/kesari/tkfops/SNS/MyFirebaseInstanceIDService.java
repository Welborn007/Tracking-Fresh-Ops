package com.kesari.tkfops.SNS;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kesari.tkfops.Utilities.SharedPrefUtil;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private String TAG = this.getClass().getSimpleName();

    SharedPreferences sharedpreferencesLogin;
    SharedPreferences.Editor editorLogin;
    String token;
    public static final String MyPREFERENCES_LOGIN = "MyPrefsLogin";

    @Override
    public void onTokenRefresh() {

        try
        {

            //Getting registration token
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("Inside"," firebase");
            //Displaying token on logcat
            Log.d(TAG, "Refreshed token: " + refreshedToken);

            token = FirebaseInstanceId.getInstance().getToken();

            SharedPrefUtil.setFirebaseToken(this,token);

            sharedpreferencesLogin = getSharedPreferences(MyPREFERENCES_LOGIN, Context.MODE_PRIVATE);
            editorLogin = sharedpreferencesLogin.edit();
            editorLogin.putString("token", token);
            editorLogin.apply();

        /*try {
            Navigation.myFireBaseToken = refreshedToken;
            Navigation.editor.putString("firebasetoken", refreshedToken);
            Navigation.editor.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }

}
