var exec = require('cordova/exec');

exports.coolMethod = function (arg0, success, error) {
alert('OK');
    exec(success, error, 'gedi800', 'coolMethod', [arg0]);
};
