var cordova = require('cordova');
var exec = require('cordova/exec');

 /**
 * Constructor.
 *
 * @returns {DataWedge}
 */
function BarcodeReader() {

};

BarcodeReader.prototype.start = function (intentAction) {
    var args = [];
    if (intentAction) {
        args[0] = intentAction;
    }
    exec(null, null, 'OpticonBarcodeReader', 'start', args);
};

BarcodeReader.prototype.stop = function (intentAction) {
    var args = [];
    if (intentAction) {
        args[0] = intentAction;
    }
    exec(null, null, 'OpticonBarcodeReader', 'stop', args);
};

BarcodeReader.prototype.registerForBarcode = function (callback) {
    
    exec(callback, null, 'OpticonBarcodeReader', 'scanner.register', []);
};

BarcodeReader.prototype.unregisterMagstripe = function () {
    
    exec(null, null, 'OpticonBarcodeReader', 'magstripe.unregister', []);
};

//create instance
var BarcodeReader = new BarcodeReader();

module.exports = BarcodeReader;