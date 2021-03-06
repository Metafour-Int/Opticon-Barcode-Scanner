var cordova = require('cordova');
var exec = require('cordova/exec');

 /**
 * Constructor.
 *
 * @returns {BarcodeReader}
 */
function BarcodeReader() {

};

BarcodeReader.prototype.start = function (actionIntent) {
    exec(null, null, 'OpticonBarcodeReader', 'start', [actionIntent]);
};

BarcodeReader.prototype.stop = function () {
    exec(null, null, 'OpticonBarcodeReader', 'stop', []);
};

BarcodeReader.prototype.scan = function () {
    exec(null, null, 'OpticonBarcodeReader', 'scan', []);
};

BarcodeReader.prototype.registerForBarcode = function (callback, scanner) {
    exec(callback, null, 'OpticonBarcodeReader', 'scanner.register', [scanner]);
};

BarcodeReader.prototype.unregisterBarcode = function () {
    exec(null, null, 'OpticonBarcodeReader', 'scanner.unregister', []);
};

//create instance
var BarcodeReader = new BarcodeReader();

module.exports = BarcodeReader;
