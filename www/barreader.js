var cordova = require('cordova');
var exec = require('cordova/exec');

 /**
 * Constructor.
 *
 * @returns {BarcodeReader}
 */
function BarcodeReader() {

};

BarcodeReader.prototype.start = function () {
    exec(null, null, 'OpticonBarcodeReader', 'start', []);
};

BarcodeReader.prototype.stop = function () {
    exec(null, null, 'OpticonBarcodeReader', 'stop', []);
};

BarcodeReader.prototype.registerForBarcode = function (callback) {
    exec(callback, null, 'OpticonBarcodeReader', 'scanner.register', []);
};

BarcodeReader.prototype.unregisterBarcode = function () {
    exec(null, null, 'OpticonBarcodeReader', 'scanner.unregister', []);
};

//create instance
var BarcodeReader = new BarcodeReader();

module.exports = BarcodeReader;