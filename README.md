Opticon-Barcode-Scanner
=======================

# Example usage
```javascript
var bcs = window.barreader;
//set-up scanner
if (bcs) {
  bcs.start();

  bcs.registerForBarcode((barcode) => {
    //callback for barcode data from software button press or hardware button press
    console.log('This is a barcode object', barcode);
    console.log('This is the barcode data', barcode.data);
    console.log('This is the barcode type', barcode.type);

  }
}

//Initiate the scan using 'software' button rather than hardware button
if (bcs) {
  bcs.scan(); // data will be captured by the registerForBarcode callback
}
//detach scanner
if (bcs) {
  bcs.unregisterBarcode();
  bcs.stop();
}
```
