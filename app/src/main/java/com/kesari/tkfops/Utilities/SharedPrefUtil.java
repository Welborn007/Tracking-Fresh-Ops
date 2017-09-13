package com.kesari.tkfops.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import com.google.gson.Gson;
import com.kesari.tkfops.BikerLogin.BikerProfileMain;
import com.kesari.tkfops.VehicleLogin.VehicleProfileMain;

/**
 * Created by kesari on 26/04/17.
 */

public class SharedPrefUtil {
    public static String PREF_NAME = "Media";
    private static String KEY_USER = "user";
    private static String KEY_LAT = "latitude";
    private static String KEY_LONGI = "longitude";

    public static String KEY_USER_TOKEN = "token";
    public static String KEY_LOGIN_TYPE = "login_type";
    public static String KEY_FIREBASE_TOKEN = "firebase_token";

    public static BikerProfileMain getBikerUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String data = preferences.getString(KEY_USER, null);
        Gson gson = new Gson();
        if (data == null)
            return null;
        else
            return gson.fromJson(data, BikerProfileMain.class);
    }


    public static void setBikerUser(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_USER, value).apply();

    }

    public static VehicleProfileMain getVehicleUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String data = preferences.getString(KEY_USER, null);
        Gson gson = new Gson();
        if (data == null)
            return null;
        else
            return gson.fromJson(data, VehicleProfileMain.class);
    }


    public static void setVehicleUser(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_USER, value).apply();

    }

    public static void setToken(Context context, String Token) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_USER_TOKEN, Token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String Token = preferences.getString(KEY_USER_TOKEN, "");

        return Token;
    }

    public static void setKeyLoginType(Context context, String type) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_LOGIN_TYPE, type).apply();
    }

    public static String getKeyLoginType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String Token = preferences.getString(KEY_LOGIN_TYPE, "");

        return Token;
    }

    public static void setFirebaseToken(Context context, String Token) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_FIREBASE_TOKEN, Token).apply();
    }

    public static String getFirebaseToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String Token = preferences.getString(KEY_FIREBASE_TOKEN, "");

        return Token;
    }

    public static void setLocation(Context context, float lat, float lon) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putFloat(KEY_LAT, lat).putFloat(KEY_LONGI, lon).commit();
    }

    public static Location getLocation(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(preferences.getFloat(KEY_LAT, 0.0f));
        location.setLongitude(preferences.getFloat(KEY_LONGI, 0.0f));

        return location;
    }

    public static void setClear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_USER).remove(KEY_USER_TOKEN).remove(KEY_LOGIN_TYPE).remove(KEY_FIREBASE_TOKEN).commit();
    }

}
