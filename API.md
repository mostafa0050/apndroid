# Apndroid API #

There are 2 ways to work with Apndroid from your code. The old way (deprecated, but still supported) is using Activity and the new way uses Service.

I am considering creating compatibility utility class that would allow to run New API against older versions of Apndroid installed. If you have ideas or suggestions, email me at martin.adamek at gmail.

Sample code demonstrating both ways is here: http://code.google.com/p/apndroid/source/browse/api-examples/

Feel free to discuss at http://groups.google.com/group/apndroid

API has two functions:

  * request the state of the switch
  * change the state of the switch


---


## New API ##

Calling new API means invoking Service and listening to broadcasts for eventual responses

### Request the state of the switch (New API) ###

To get the state of the switch you have to start Service using intent with action **apndroid.intent.action.GET\_STATUS** without any extras.
Result is returned as broadcast with action **apndroid.intent.action.STATUS** with one boolean extra with key **apndroid.intent.extra.STATUS**

```
startService(new Intent("apndroid.intent.action.GET_STATUS"));

registerReceiver(new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("apndroid.intent.action.STATUS")) {
		    boolean dataEnabled = intent.getBooleanExtra("apndroid.intent.extra.STATUS", true);
		    mToggle.setChecked(dataEnabled);
		}
	}
}, new IntentFilter("apndroid.intent.action.STATUS"));
```

### Change the state of the switch (New API) ###

To change the state of the switch you need to start Service using intent with action **apndroid.intent.action.CHANGE\_STATUS**
This intent requires one boolean extra with key **apndroid.intent.extra.STATUS** and accepts also one optional boolean extra with key **apndroid.intent.extra.KEEP\_MMS\_ON**

```
Intent intent = new Intent("apndroid.intent.action.CHANGE_STATUS");
intent.putExtra("apndroid.intent.extra.STATUS", mToggle.isChecked());
intent.putExtra("apndroid.intent.extra.KEEP_MMS_ON", mMmsCheckbBox.isChecked());
startService(intent);

registerReceiver(new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("apndroid.intent.action.STATUS")) {
		    boolean dataEnabled = intent.getBooleanExtra("apndroid.intent.extra.STATUS", true);
		    mToggle.setChecked(dataEnabled);
		}
	}
}, new IntentFilter("apndroid.intent.action.STATUS"));
```


---


## Old API ##

Calling old API means invoking Activity and waiting on Activity result for eventual response.

### Request the state of the switch (Old API) ###

**WARNING - Apndroid 3.0.15 in the Market contains bug that "APN\_STATE" and "MMS\_STATE" extras are booleans instead of integers. It will be fixed with next update!**

To get the switch state, you need to start activity using intent with action **com.google.code.apndroid.intent.action.STATUS\_REQUEST** without parameters.

As a result you should receive intent with action **com.google.code.apndroid.intent.REQUEST\_RESULT**. This intent have a bundle with response. Bundle always contains integer value by key **APN\_STATE**. If value == 1 than status is ON, else status is OFF. If current status is OFF then bundle contains a key with current MMS status. It's also an integer with same semantics and the key is **MMS\_STATE**.

Code snippet for status request:

```
static final GET_STATE_REQUEST = 1;

startActivityForResult(new Intent("com.google.code.apndroid.intent.action.STATUS_REQUEST"), GET_STATE_REQUEST);

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == GET_STATE_REQUEST && resultCode == RESULT_OK && intent != null) {
        if (intent.getAction().equals("com.google.code.apndroid.intent.REQUEST_RESULT")) {
            boolean dataEnabled = (intent.getIntExtra("APN_STATE", 1) == 1);
            mToggle.setChecked(dataEnabled);
        }
    }
}
```

### Change the state of the switch (Old API) ###
To get current apn status you need to start Activity using intent with action **com.google.code.apndroid.intent.action.CHANGE\_REQUEST** with next parameters:

  1. **com.google.code.apndroid.intent.extra.TARGET\_STATE**
  1. **com.google.code.apndroid.intent.extra.TARGET\_MMS\_STATE**

It is not necessary to put all parameters. Actually there are next ways:
  * put no parameters. In this case Apndroid will perform switch to another state using default preferences (user entered in Apndroid Settings) for MMS keeping
  * put **com.google.code.apndroid.intent.extra.TARGET\_STATE** parameter only. In this case will be used default MMS keeping and switch will be performed to passed state
  * put all parameters for full control of switching. This way has a feature - if passed **com.google.code.apndroid.intent.extra.TARGET\_MMS\_STATE** differ from current keep mms setting it will be updated in app settings, so use it carefully.

If current state equals to passed target state switch will be treated as successfull.

As a result you should receive intent with action **com.google.code.apndroid.intent.REQUEST\_RESULT**. This intent have a bundle with response. Bundle always contains boolean value by key **SWITCH\_SUCCESS**. If value is `true` then switch was successfull, else unsuccessfull.

Code snippet for switch change:
```
static final CHANGE_STATE_REQUEST = 2;

Intent intent = new Intent("com.google.code.apndroid.intent.action.CHANGE_REQUEST");
intent.putExtra("com.google.code.apndroid.intent.extra.TARGET_STATE", mToggle.isChecked());
intent.putExtra("com.google.code.apndroid.intent.extra.TARGET_MMS_STATE", mMmsCheckbBox.isChecked());
startActivityForResult(intent, CHANGE_STATE_REQUEST);

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == GET_STATE_REQUEST && resultCode == RESULT_OK && intent != null) {
        if (intent.getAction().equals("com.google.code.apndroid.intent.REQUEST_RESULT")) {
            boolean dataEnabled = (intent.getIntExtra("APN_STATE", 1) == 1);
            mToggle.setChecked(dataEnabled);
        }
    }
}
```