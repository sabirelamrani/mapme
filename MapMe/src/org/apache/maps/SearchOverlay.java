package org.apache.maps;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class SearchOverlay extends Overlay {
	
	BrowseMap mMap;
    Paint paint1 = new Paint();
    Paint paint2 = new Paint();

    public SearchOverlay(BrowseMap map) {
        mMap = map;
        paint2.setARGB(255, 255, 255, 255);
    }

    public void draw(Canvas canvas, MapView map, boolean b) {
        super.draw(canvas, map, b);

        List<Address> addresses = mMap.getAddresses();
        if (addresses != null && addresses.size() > 0) {
            for (int i = 0; i < addresses.size(); i++) {
                Address addr = addresses.get(i);
                GeoPoint point = new GeoPoint((((int)(addr.getLatitude() * 1E6))),
                        (((int)(1E6 * addr.getLongitude()))));
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
        mMap.notifyUser("Tapped: " + Double.toString(point.getLatitudeE6()/1.0E6) 
        		+ "/" + Double.toString(point.getLongitudeE6()/1.0E6));
        return super.onTap(point, view);
    }
    
    /*
    //Geocoding example
    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) 
    {   
        if (event.getAction() == MotionEvent.ACTION_UP) {                
            GeoPoint p = mapView.getProjection().fromPixels(
                (int) event.getX(),
                (int) event.getY());
            Geocoder geoCoder = new Geocoder(
                mMap.getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(
                    p.getLatitudeE6()  / 1E6, 
                    p.getLongitudeE6() / 1E6, 1);
                String add = "";
                if (addresses.size() > 0) 
                {
                	//Display all lines of first address
                    for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
                       add += addresses.get(0).getAddressLine(i) + "\n";
                }
                mMap.notifyUser(add);
            }
            catch (IOException e) {                
                e.printStackTrace();
            }   
            return true;
        }
        else                
            return false;
    }*/
}
