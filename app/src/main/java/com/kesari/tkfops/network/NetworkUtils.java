package com.kesari.tkfops.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnectionOn(Context context) {

		boolean mobileFlag = false, wifiFlag = false;
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();

		if (activeNetwork != null) { // connected to the internet
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

				wifiFlag = true;

			} else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

				mobileFlag = true;

			}
		} else {
			// not connected to the internet
			return false;
		}

		if (wifiFlag == true || mobileFlag == true) {
			return true;
		}

		return false;
	}

}

