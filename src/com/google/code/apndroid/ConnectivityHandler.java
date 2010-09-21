package com.google.code.apndroid;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.code.apndroid.dao.ConnectionDao;
import com.google.code.apndroid.dao.DaoFactory;

public class ConnectivityHandler {

	private final IntentFilter mFilter;
	private final Context mContext;
	private final ConnectivityManager mConnManager;
	private final TextView mInfoText;
	private final TextView mReconnectText;
	private final ImageView mBar;
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				handleConnectivityAction(intent.getExtras());
				checkReconnect();
			}
		}
		
	};
	
	public ConnectivityHandler(Context context, TextView infoText, TextView reconnectText, ImageView bar) {
		mContext = context;
		mConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		mInfoText = infoText;
		mReconnectText = reconnectText;
		mBar = bar;
		mFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
	}
	
	void resume() {
		mContext.registerReceiver(mReceiver, mFilter);
	}

	void pause() {
		mContext.unregisterReceiver(mReceiver);
	}
	
	void refresh() {
		NetworkInfo networkInfo = mConnManager.getActiveNetworkInfo();
		checkOurToggleIsCorrect(networkInfo);
		handleNetworkInfoInUI(networkInfo);
		checkReconnect();
	}
	
	private void handleConnectivityAction(Bundle extras) {
//		StringBuilder sb = new StringBuilder("");
//		sb.append("[Is failover] ").append(extras.getBoolean(ConnectivityManager.EXTRA_IS_FAILOVER)).append("\n");
//		sb.append("[Extra info] ").append(extras.getString(ConnectivityManager.EXTRA_EXTRA_INFO)).append("\n");
//		sb.append("[No connectivity] ").append(extras.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY)).append("\n");
//		sb.append("[Extra reason] ").append(extras.getString(ConnectivityManager.EXTRA_REASON)).append("\n");
//		sb.append("[Extra network info] ").append(extras.getParcelable(ConnectivityManager.EXTRA_NETWORK_INFO));
//		mInfoText.setText(sb.toString());
		
		NetworkInfo networkInfo = (NetworkInfo) extras.getParcelable(ConnectivityManager.EXTRA_NETWORK_INFO);
		checkOurToggleIsCorrect(networkInfo);
		handleNetworkInfoInUI(networkInfo);
	}

	/**
	 * Handle the case when we are storing OFF state in our preferences,
	 * but mobile internet is ON (turned on by system toggle or by other application).
	 * In this case, change our stored preference to ON and make
	 * appropriate change also in the UI.
	 * 
	 * @param info
	 */
	private void checkOurToggleIsCorrect(NetworkInfo info) {
		if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE && info.isConnectedOrConnecting()) {
			ConnectionDao dao = DaoFactory.getDao(mContext);
			if (dao.isDataEnabled()) {
				MainActivity.updateIndicatorAndData(mContext, true, mBar);
			}
		}
	}
	
	private void handleNetworkInfoInUI(NetworkInfo networkInfo) {
		if (networkInfo == null) {
			mInfoText.setText("No active network");
		} else {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				mInfoText.setText("WiFi connection is active");
			} else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
				mInfoText.setText("Mobile connection is active");
			} else {
				mInfoText.setText("Unknown connection is active");
			}
			if (!networkInfo.isConnectedOrConnecting()) {
				mInfoText.append(" but disconnected.");
			} else if(networkInfo.isConnected()) {
				mInfoText.append(" and connected.");
			} else {
				mInfoText.append(" and connecting.");
			}
		}
	}
	
	private void checkReconnect() {
        String net_dns1 = exec("getprop net.dns1");
        String net_dns2 = exec("getprop net.dns2");
        String net_rmnet0_dns1 = exec("getprop net.rmnet0.dns1");
        String net_rmnet0_dns2 = exec("getprop net.rmnet0.dns2");
        
        NetworkInfo info = mConnManager.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
        	mReconnectText.setText("");
        	return;
        }
        
		if (!net_dns1.equals(net_rmnet0_dns1) || !net_dns2.equals(net_rmnet0_dns2)) {
			mReconnectText.setText("Reconnect failed. Caused by Android bug 2207. Check http://bit.ly/issue2207");
			mReconnectText.append("\n" + net_dns1);
			mReconnectText.append("\n" + net_dns2);
			mReconnectText.append("\n" + net_rmnet0_dns1);
			mReconnectText.append("\n" + net_rmnet0_dns2);
		} else {
			mReconnectText.setText("");
		}
	}
	
    private String exec(String command) {
        String line = null;
        try { 
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); 
            line = reader.readLine();
            reader.close();
        } catch (Exception err) { 
            line = "error"; 
        }
        return line;
    }    

}
