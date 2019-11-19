var exec = require('cordova/exec');
exports.loadAds = function (arg0, success, error) {
    exec(success, error, 'callPlugin', 'loadAd', [arg0]);
};
 
exports.isAdLoaded = function (arg0, success, error) {
    exec(success, error, 'callPlugin', 'isAdLoaded', [arg0]);
}; 
exports.showAds = function (arg0, success, error) {
    exec(success, error, 'callPlugin', 'showAd',[arg0]);
};


exports.initiate = function (arg0, success, error) {
    exec(success, error, 'callPlugin', 'initialize', [arg0]);
};

exports.showMessege = function (arg0, msgType, success, error){
    exec(success, error, 'callPlugin', msgType, [arg0]);
}