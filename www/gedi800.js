var exec = require( 'cordova/exec' );

exports.printText = function ( arg0, success, error ) 
{
    exec( success, error, 'gedi800', 'printText', [ arg0 ] );
};

exports.printBarcode = function ( arg0, success, error ) 
{
    exec( success, error, 'gedi800', 'printBarcode', [ arg0 ] );
};
