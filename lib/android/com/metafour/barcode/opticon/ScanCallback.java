package com.metafour.barcode.opticon;

public interface ScanCallback<ResultT> {

    public void execute(ResultT result);
}