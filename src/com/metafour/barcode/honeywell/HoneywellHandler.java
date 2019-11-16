package com.metafour.barcode.honeywell;

import android.content.Context;
import android.util.Log;

import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.metafour.barcode.BarcodeScan;
import com.metafour.barcode.ScanCallback;
import com.metafour.barcode.ScanningIntentHandler;
import com.metafour.barcode.opticon.OpticonIntentHandler;
import com.oem.barcode.BCRIntents;

public class HoneywellHandler implements BarcodeReader.BarcodeListener, ScanningIntentHandler {

    protected static String TAG = HoneywellHandler.class.getSimpleName();
    protected Context applicationContext;

    protected static Object stateLock = new Object();
    protected static boolean hasInitialized = false;

    private BarcodeReader barcodeReader;
    private AidcManager manager;

    private static final String INTENT_ACTION = BCRIntents.ACTION_NEW_DATA;

    protected ScanCallback<BarcodeScan> scanCallback;

    public HoneywellHandler(Context context) {
        this.TAG = this.getClass().getSimpleName();
        applicationContext = context;
    }

    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {

        String decodedBarcode = barcodeReadEvent.getBarcodeData();
        String type = barcodeReadEvent.getCodeId();

        Log.e(TAG, type + " " + decodedBarcode);

        if (scanCallback == null) {
            Log.e(TAG, "Scan data received, but callback is null.");
            return;
        }

        scanCallback.execute(new BarcodeScan(type, decodedBarcode));

    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {

        if(barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void start() {
        if(barcodeReader != null) {
            this.stop();
            try {
                barcodeReader.claim();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            }
            return;
        }else {
            AidcManager.create(applicationContext, new AidcManager.CreatedCallback() {
                @Override
                public void onCreated(AidcManager aidcManager) {
                    manager = aidcManager;
                    barcodeReader = manager.createBarcodeReader();
                    if (barcodeReader != null) {
                        barcodeReader.addBarcodeListener(HoneywellHandler.this);
                        try {
                            barcodeReader.claim();
                        } catch (ScannerUnavailableException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void stop() {
        if(barcodeReader != null) {
            try {
                barcodeReader.softwareTrigger(false);
            } catch (ScannerNotClaimedException e) {
                e.printStackTrace();
            } catch (ScannerUnavailableException e) {
                e.printStackTrace();
            } finally {
                barcodeReader.removeBarcodeListener(HoneywellHandler.this);
                barcodeReader.release();
                barcodeReader = null;
            }
        }
    }

    @Override
    public void scan() {

        try {
            barcodeReader.softwareTrigger(true);
        } catch (ScannerNotClaimedException e) {
            e.printStackTrace();
        } catch (ScannerUnavailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setScanCallback(ScanCallback<BarcodeScan> callback) {
        this.scanCallback = callback;
    }

    @Override
    public boolean hasListeners() {
        return this.scanCallback != null;
    }
}
