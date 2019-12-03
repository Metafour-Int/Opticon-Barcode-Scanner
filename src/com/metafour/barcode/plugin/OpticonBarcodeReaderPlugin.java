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
import com.metafour.barcode.honeywell.HoneywellHandler;
import com.metafour.barcode.opticon.OpticonIntentHandler;

import android.os.Build;
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
			}
			else if("honeywell".equalsIgnoreCase(argValue)){
				Log.i(TAG, "Calling HoneywellHandler");
				intentHandler = new HoneywellHandler(cordova.getActivity().getBaseContext());
			}
			else {
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
			if(intentHandler != null){
				intentHandler.setScanCallback(null);
				if (!intentHandler.hasListeners()) {
					intentHandler.stop();
				}
			}else{
				Log.e(TAG, "intentHandler not initialised. Please call scanner.register");
			}

		} else if ("stop".equals(action)) {
			if(intentHandler != null){
				intentHandler.stop();
			}else{
				Log.e(TAG, "intentHandler not initialised. Please call scanner.register");
			}
		} else if ("scan".equals(action)){
			if(intentHandler != null){	
				intentHandler.scan();
			}else{
				Log.e(TAG, "intentHandler not initialised. Please call scanner.register");
			}
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

			if(intentHandler != null){
				intentHandler.start();
			}else{
				Log.e(TAG, "intentHandler not initialised. Please call scanner.register");
			}
		}

		return true;
	}

}
