Opticon-Barcode-Scanner
=======================

# Example usage
```javascript
var bcs = window.barreader;
//set-up scanner
if (bcs) {
  bcs.start();
  bcs.registerForBarcode((data) => {
    //callback for barcode data from software buttonp press or hardware button press
    console.log('This is some barcode data', data);
  }
}

//Initiate the scan using 'soft' button rather than
if (bcs) {
  bcs.scan(); // data will be captured by the registerForBarcode callback
}
//detach scanner
if (bcs) {
  bcs.unregisterBarcode();
  bcs.stop();
}
```
