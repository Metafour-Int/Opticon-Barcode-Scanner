package com.metafour.barcode.opticon;

import com.metafour.barcode.BarcodeScan;
import com.metafour.barcode.ScanCallback;
import com.metafour.barcode.ScanningIntentHandler;
import com.oem.barcode.BCRIntents;
import com.oem.barcode.BCRManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class OpticonIntentHandler implements ScanningIntentHandler {
	
	protected static String TAG = OpticonIntentHandler.class.getSimpleName();
	protected Context applicationContext;
	
	protected static Object stateLock = new Object();
	protected static boolean hasInitialized = false;
	
	protected ScanCallback<BarcodeScan> scanCallback;
	
	public OpticonIntentHandler(Context context) {
		this.TAG = this.getClass().getSimpleName();
		applicationContext = context;
		
	}
	
    public boolean hasListeners(){
        return this.scanCallback != null;
    }
	
    
    public void setScanCallback(ScanCallback<BarcodeScan> callback){
        scanCallback = callback;
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

            Log.i(TAG, "Register for Opticon intent: " + BCRIntents.ACTION_NEW_DATA);

            applicationContext.registerReceiver(dataReceiver, new IntentFilter(BCRIntents.ACTION_NEW_DATA));
            
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
    
    public void scan(){
    	BCRManager.getDefault().BCRTriggerPress();
    }

	
    private BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
	        if (intent != null) {
	            final String action = intent.getAction();
	            
	            if (BCRIntents.ACTION_NEW_DATA.equals(action)) {
	            	byte[] data = intent.getByteArrayExtra(BCRIntents.EXTRA_BCR_DATA);
	            	int type = intent.getIntExtra(BCRIntents.EXTRA_BCR_TYPE, -1);
	            	String decodedBarcode = intent.getStringExtra(BCRIntents.EXTRA_BCR_STRING);
	            	String charset = intent.getStringExtra(BCRIntents.EXTRA_BCR_CHARSET);
	            	
	            	if (scanCallback == null) {
                        Log.e(TAG, "Scan data received, but callback is null.");
                        return;
                    }
	            	
	            	scanCallback.execute(new BarcodeScan(Integer.toString(type), decodedBarcode));

	            }
	        }
	        
        }
    };

}
