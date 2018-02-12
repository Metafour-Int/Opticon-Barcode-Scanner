package com.metafour.barcode.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metafour.barcode.BarcodeScan;
import com.metafour.barcode.ScanCallback;
import com.metafour.barcode.ScanningIntentHandler;
import com.metafour.barcode.datawedge.DatawedgeIntentHandler;
import com.metafour.barcode.opticon.OpticonIntentHandler;

import android.util.Log;

public class OpticonBarcodeReaderPlugin extends CordovaPlugin {

	private ScanningIntentHandler intentHandler;
	protected static String TAG = "OpticonBarcodeReaderPlugin";

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		
	}

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		
		Log.i(TAG, "Action: " + action);
		Log.i(TAG, "Args: " + args); //["datawedge"]
		
		Log.i(TAG, android.os.Build.BOARD);
		Log.i(TAG, android.os.Build.BOOTLOADER);
		Log.i(TAG, android.os.Build.BRAND);
		Log.i(TAG, android.os.Build.CPU_ABI);
		Log.i(TAG, android.os.Build.CPU_ABI2);
		Log.i(TAG, android.os.Build.DEVICE);
		Log.i(TAG, android.os.Build.DISPLAY);
		Log.i(TAG, android.os.Build.FINGERPRINT);
		Log.i(TAG, android.os.Build.HARDWARE);
		Log.i(TAG, android.os.Build.HOST);
		Log.i(TAG, android.os.Build.ID);
		Log.i(TAG, android.os.Build.MANUFACTURER);
		Log.i(TAG, android.os.Build.MODEL);
		Log.i(TAG, android.os.Build.PRODUCT);
		Log.i(TAG, android.os.Build.RADIO);
		Log.i(TAG, android.os.Build.SERIAL);
		Log.i(TAG, android.os.Build.TAGS);
		Log.i(TAG, android.os.Build.TYPE);
		Log.i(TAG, android.os.Build.UNKNOWN);
		Log.i(TAG, android.os.Build.USER);

		if ("scanner.register".equals(action)) {
			
			Log.i(TAG, "In the scanner.register");
			
			String argValue = "DEFAULT";
			try {
				argValue = args.getString(0);
			}catch(Exception e) {
				Log.e(TAG, "Exception getting argument");
			}

						
			if("datawedge".equalsIgnoreCase(argValue)) {
				Log.i(TAG, "Calling DatawedgeIntentHandler");
				intentHandler = new DatawedgeIntentHandler(cordova.getActivity().getBaseContext());
			}else {
				Log.i(TAG, "Calling OpticonIntentHandler");
				intentHandler = new OpticonIntentHandler(cordova.getActivity().getBaseContext());
			}
			
			
			intentHandler.setScanCallback(new ScanCallback<BarcodeScan>() {
				@Override
				public void execute(BarcodeScan scan) {
					Log.i(TAG, "Scan result [" + scan.LabelType + "-"
							+ scan.Barcode + "].");

					try {
						JSONObject obj = new JSONObject();
						obj.put("type", scan.LabelType);
						obj.put("barcode", scan.Barcode);
						PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
						pluginResult.setKeepCallback(true);
						callbackContext.sendPluginResult(pluginResult);
						
					} catch (JSONException e) {
						Log.e(TAG, "Error building json object", e);

					}
				}
			});
			
		} else if ("scanner.unregister".equals(action)) {
			
			intentHandler.setScanCallback(null);
			if (!intentHandler.hasListeners()) {
				intentHandler.stop();
			}
		

		} else if ("stop".equals(action)) {
			intentHandler.stop();
		} else if ("scan".equals(action)){
			intentHandler.scan();
		}

		// start plugin now if not already started
		if ("start".equals(action)) {
			Log.i(TAG, "Calling Start. Status of intentHandler is " + intentHandler);
			
			String argValue = null;
			try {
				argValue = args.getString(0);
			}catch(Exception e) {
				Log.e(TAG, "Exception getting argument");
			}
			
			if(argValue != null) {
				Log.i(TAG, "Setting custom intent action " + argValue);
				intentHandler.setIntentAction(argValue);
			}
			
			
			intentHandler.start();
		}

		return true;
	}

}
