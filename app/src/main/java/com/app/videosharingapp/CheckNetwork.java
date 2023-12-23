package com.app.videosharingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

public class CheckNetwork {
    private final Context context;
    public CheckNetwork(Context context) {
        this.context = context;
    }
    public void registerNetworkCallback() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                                                                   @Override
                                                                   public void onAvailable(Network network) {
                                                                       Variables.isNetworkConnected = true;
                                                                   }
                                                                   @Override
                                                                   public void onLost(Network network) {
                                                                       Variables.isNetworkConnected = false;
                                                                   }
                                                               });
            Variables.isNetworkConnected = false;
        }catch (Exception e){
            Variables.isNetworkConnected = false;
        }
    }
}
