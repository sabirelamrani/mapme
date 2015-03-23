# Important #

This demo will show a blank map if you don't provide your own Android Maps API key.

The procedure to generate it is described here:
http://developer.android.com/guide/topics/location/geo/mapkey.html

Once you get your API key edit the following file:

res/layout/mapview.xml

and replace _YOUR\_API\_KEY\_HERE_ with your generated key.

The key must be generated using the same fingerprint in your Eclipse Android debug store
(more info here: http://mobiforge.com/developing/story/using-google-maps-android):

1) Find your debug.keystore file. It's usually in directory USER\_HOME\Local Settings\Application Data\android

2) Use the keytool to retrieve your certificate fingerprint (MD5). Use the following command on the command prompt:

keytool -list -alias androiddebugkey -keystore .keystore -storepass android -keypass android

3) Go to this page: http://code.google.com/android/maps-api-signup.html. Enter your certificate fingerprint (MD5) and get your API key for your Android maps application.

4) Replace _YOUR\_API\_KEY\_HERE_ with your API key.