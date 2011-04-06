package com.example.apndroid;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.code.apndroid.Constants;
import com.google.code.apndroid.IActionService;

/**
 * User: Zelgadis Date: 27.11.2009
 */
public class MyActivity extends Activity {
	private IActionService actionService = null;

	// The connection to the service. The service is only bound when the user checks the relevant checkbox.
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			actionService = IActionService.Stub.asInterface(service);
			Toast.makeText(MyActivity.this, "Connected to service",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			actionService = null;
			Toast.makeText(MyActivity.this, "Connection to service lost",
					Toast.LENGTH_LONG).show();
		}
	};

	public static final int STATE_REQUEST = 0;
	public static final int CHANGE_REQUEST = 1;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button stateRequest = (Button) findViewById(R.id.state_request);
		stateRequest.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				sendRequest(Constants.STATUS_REQUEST, new Bundle(), STATE_REQUEST);
			}
		});
		Button changeRequet = (Button) findViewById(R.id.switch_request);
		changeRequet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				boolean targetState = getCheckBoxState(R.id.target_state);
				boolean keepMms = getCheckBoxState(R.id.keep_mms);
				boolean showNotification = getCheckBoxState(R.id.show_notification);
				Bundle extras = new Bundle();
				int onState = Constants.STATE_ON;
				int offState = Constants.STATE_OFF;
				extras.putInt(Constants.TARGET_MMS_STATE,
						keepMms ? onState : offState);
				extras.putInt(Constants.TARGET_APN_STATE,
						targetState ? onState : offState);
				extras.putBoolean(Constants.SHOW_NOTIFICATION,
						showNotification);
				sendRequest(Constants.CHANGE_STATUS_REQUEST, extras,
						CHANGE_REQUEST);
			}
		});
		final CheckBox useService = (CheckBox) findViewById(R.id.use_service);
		useService.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (useService.isChecked()) {
					if (actionService == null) {
						bindService(new Intent(
								Constants.ACTION_SERVICE),
								connection, BIND_AUTO_CREATE);
					}
				} else {
					if (actionService != null) {
						unbindService(connection);
						actionService = null;
						Toast.makeText(MyActivity.this, "Disconnected from service",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		// onStart reconnects the service if the checkbox is checked.
		super.onStart();
		final CheckBox useService = (CheckBox) findViewById(R.id.use_service);
		if (useService.isChecked() && actionService == null) {
			bindService(new Intent(
					Constants.ACTION_SERVICE),
					connection, BIND_AUTO_CREATE);
		}
	}
	
	@Override
	protected void onStop() {
		// onStop disconnects the service. It will be reconnected in onStart if the checkbox is checked.
		super.onStop();
		if (actionService != null) {
			unbindService(connection);
			actionService = null;
		}
	}

	private void sendRequest(String intentString, Bundle extras, int requestType) {
		Intent intent = new Intent(intentString);
		intent.putExtras(extras);
		if (getCheckBoxState(R.id.use_service)) {
			if (actionService == null) {
				Toast.makeText(this, "Not bound to service", Toast.LENGTH_LONG).show();
			}
			try {
				switch (requestType) {
				case STATE_REQUEST:
					handleResult(STATE_REQUEST, actionService.getStatus());
					break;
				case CHANGE_REQUEST:
					handleResult(CHANGE_REQUEST, actionService
							.switchStatus(extras));
					break;
				}
			} catch (RemoteException e) {
				Toast.makeText(this, "Service error", Toast.LENGTH_LONG).show();
			}
		} else {
			startActivityForResult(intent, requestType);
		}
	}

	private boolean getCheckBoxState(int id) {
		CheckBox cb = (CheckBox) findViewById(id);
		return cb.isChecked();
	}

	@Override
	protected void onActivityResult(int requestedCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestedCode, resultCode, intent);
		if (resultCode == RESULT_OK
				&& intent != null
				&& Constants.APN_DROID_RESULT.equals(intent
						.getAction())) {
			handleResult(requestedCode, intent.getExtras());
		}

	}

	private void handleResult(int requestedCode, Bundle bundle) {
		switch (requestedCode) {
		case STATE_REQUEST:
			int onState = Constants.STATE_ON;
			int offState = Constants.STATE_OFF;

			int state = bundle.getInt(Constants.RESPONSE_APN_STATE,
					-1);

			String currentState = "Current state is "
					+ (state == onState ? "on" : (state == offState ? "off"
							: "unknown"));
			if (state != -1) {
				currentState += "\nMms state is "
						+ (bundle.getInt(
								Constants.RESPONSE_MMS_STATE,
								onState) == onState ? "on" : "off");	
			}
			Toast toast = Toast.makeText(this, currentState,
					Toast.LENGTH_LONG);
			toast.show();
			break;
		case CHANGE_REQUEST:
			String switchSuccess;
			if (bundle.containsKey(Constants.RESPONSE_SWITCH_SUCCESS)) {
				switchSuccess = "Switch was "
					+ (bundle.getBoolean(
							Constants.RESPONSE_SWITCH_SUCCESS, true) ? "successful"
							: "unsuccessful");
			} else {
				switchSuccess = "Switch success is unknown";
			}
			toast = Toast
					.makeText(this, switchSuccess, Toast.LENGTH_LONG);
			toast.show();

			break;
		}
	}
}
