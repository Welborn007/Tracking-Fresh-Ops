package com.kesari.tkfops.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkUtilsReceiver extends BroadcastReceiver{
	public NetworkResponseInt networkResponseInt;


	public interface NetworkResponseInt{

		public void NetworkOpen();
		public void NetworkClose();

	}

	public NetworkUtilsReceiver(NetworkResponseInt networkResponseInt) {
		this.networkResponseInt = networkResponseInt;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
	if(NetworkUtils.isNetworkConnectionOn(context)){
		networkResponseInt.NetworkOpen();
	}else{
		networkResponseInt.NetworkClose();
	}
	}
}

