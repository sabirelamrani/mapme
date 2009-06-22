MapMe basically displays 2D maps on Google Android but also adds additional features.

For more info see: http://code.google.com/p/mapme/wiki/MapMe

IMPORTANT: this demo will show a blank map if you don't provide your own Android Maps API key.

The procedure to generate it is described here:
http://developer.android.com/guide/topics/location/geo/mapkey.html

Once you get your API key edit the following files:
res/layout/mapview.xml
and replace YOUR_API_KEY_HERE with your generated key.

The key must be generated using the same fingerprint in your Eclipse Android debug store
(more info here: http://mobiforge.com/developing/story/using-google-maps-android)

This demo is based on BrowseMap by Davanum Srinivas:
http://davanum.wordpress.com/2007/11/15/working-with-google-androids-maps-aka-search-for-pizza/
