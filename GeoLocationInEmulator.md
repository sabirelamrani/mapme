In order to center map on current location while you're testing MapMe in the Android emulator you'll have to do one of the following:

a) Manually pass a location via telnet, eg:

> telnet localhost 5554

> geo fix -122.08 37.41

However, there's a bug in Android that prevents geo fix to work properly with the emulator.
See: http://code.google.com/p/android/issues/detail?id=1701

b) Select Window>>Open Perspective>>DDMS (You may have to click other and browse for it, if it isn’t there). Under the Emulator Control tab scroll down until you see the Location Controls. This control allows you to push one location or a series of locations in GPX or KML format. Enter the latitude and longitude into the box and click send. This will simulate a GPS location coming into the device.

c) Code a mock location provider (more info here: http://androidcommunity.com/forums/showthread.php?t=55)