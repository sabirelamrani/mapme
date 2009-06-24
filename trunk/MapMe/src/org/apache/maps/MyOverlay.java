package org.apache.maps;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MyOverlay extends Overlay {
    BrowseMap mMap;
    Paint paint1 = new Paint();
    Paint paint2 = new Paint();

    public MyOverlay(BrowseMap map) {
        mMap = map;
        paint2.setARGB(255, 255, 255, 255);
    }

    public void draw(Canvas canvas, MapView map, boolean b) {
        super.draw(canvas, map, b);

        List<Address>addresses= mMap.getAddresses();
        if (addresses != null && addresses.size() > 0) {
            for (int i = 0; i < addresses.size(); i++) {
                Address addr = addresses.get(i);
                GeoPoint point = new GeoPoint((((int)(addr.getLatitude() * 1e6))),
                        (((int)(1e6 * addr.getLongitude()))));
                Point screenCoords = mMap.getProjection().toPixels(point, null);
                canvas.drawCircle(screenCoords.x, screenCoords.y, 9, paint1);
                canvas.drawText(Integer.toString(i + 1),
                        screenCoords.x - 4,
                        screenCoords.y + 4, paint2);
            }
        }
    }
    
    @Override
    public boolean onTap(GeoPoint point, MapView view) {
        mMap.notifyUser("Tapped: " + Double.toString(point.getLatitudeE6()/1000000.0) 
        		+ "/" + Double.toString(point.getLongitudeE6()/1000000.0));
        return super.onTap(point, view);
    }
}
