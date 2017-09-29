package com.metafour.barcode.zebra;

import com.metafour.barcode.BarcodeScan;
import com.metafour.barcode.ScanCallback;
import com.metafour.barcode.ScanningIntentHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ZebraIntentHandler implements ScanningIntentHandler {
	
	protected static String TAG = ZebraIntentHandler.class.getSimpleName();
	protected Context applicationContext;
	
	protected static Object stateLock = new Object();
	protected static boolean hasInitialized = false;
	
	// This intent string contains the barcode symbology as a string  
    private static final String LABEL_TYPE_TAG = "com.motorolasolutions.emdk.datawedge.label_type";  
    // This intent string contains the captured data as a string  
    // (in the case of MSR this data string contains a concatenation of the track data)  
    private static final String DATA_STRING_TAG = "com.motorolasolutions.emdk.datawedge.data_string";
    // DataWedge Action receiver
    private static final String ACTION_NEW_DATA = "com.symbol.datawedge.DWDEMO";
    // Scanning actions
    private static final String ACTION_SOFTSCANTRIGGER = "com.motorolasolutions.emdk.datawedge.api.ACTION_SOFTSCANTRIGGER";  
    private static final String EXTRA_PARAM = "com.motorolasolutions.emdk.datawedge.api.EXTRA_PARAMETER";  
    private static final String DWAPI_TOGGLE_SCANNING = "TOGGLE_SCANNING";
	
	protected ScanCallback<BarcodeScan> scanCallback;
	
	public ZebraIntentHandler(Context context) {
		this.TAG = this.getClass().getSimpleName();
		applicationContext = context;
		
	}
	
	public void start() {
        Log.i(TAG, "Open called");
        if (hasInitialized) {
            return;
        }
        synchronized (stateLock) {
            if (hasInitialized) {
                return;
            }

            Log.i(TAG, "Register for Datawedge intent: " + this.ACTION_NEW_DATA);
            
            applicationContext.registerReceiver(dataReceiver, new IntentFilter(this.ACTION_NEW_DATA));
            
            hasInitialized = true;
        }
		
	}

	
	public void stop() {
        if (!hasInitialized) {
            return;
        }
        synchronized (stateLock) {
            if (!hasInitialized) {
                return;
            }

            Log.i(TAG, "Running close plugin intent");


            try {
                applicationContext.unregisterReceiver(dataReceiver);
            } catch(Exception ex) {
                Log.e(TAG, "Exception while unregistering data receiver. Was start ever called?", ex);
            }

            hasInitialized = false;
        }
		
	}

	
	public void scan() {
        // Create a new intent  
        Intent i = new Intent();  
        // set the intent action using soft scan trigger action string declared earlier  
        i.setAction(ACTION_SOFTSCANTRIGGER);  
        // add a string parameter to tell DW that we want to toggle the soft scan trigger  
        i.putExtra(EXTRA_PARAM, DWAPI_TOGGLE_SCANNING);  
        // now broadcast the intent  
        this.applicationContext.sendBroadcast(i);
		
	}

	
	public void setScanCallback(ScanCallback<BarcodeScan> callback) {
		scanCallback = callback;		
	}

	private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
	        if (intent != null) {
	            
	        	String type = intent.getStringExtra(LABEL_TYPE_TAG);
	        	String decodedBarcode = intent.getStringExtra(DATA_STRING_TAG);
	        	
	        	if (scanCallback == null) {
	                Log.e(TAG, "Scan data received, but callback is null.");
	                return;
	            }
	        	
	        	scanCallback.execute(new BarcodeScan(type, decodedBarcode));
	            
	        }
	        
        }
    };

	@Override
	public boolean hasListeners() {
		return this.scanCallback != null;
	}


}
