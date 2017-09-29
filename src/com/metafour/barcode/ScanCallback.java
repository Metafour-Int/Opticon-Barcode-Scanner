package com.metafour.barcode;

public interface ScanCallback<ResultT> {

    public void execute(ResultT result);
}