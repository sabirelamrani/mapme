package org.apache.maps;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class BookmarkOverlay extends Overlay {

	private static final int BALLOON_HEIGHT = 34;
	//private static final int BALLOON_WIDTH = 37;
	
	BrowseMap mMap;
    Paint paint = new Paint();

    public BookmarkOverlay(BrowseMap map) {
        mMap = map;
    }

    public void draw(Canvas canvas, MapView map, boolean b) {
        super.draw(canvas, map, b);

        //List<Address> addresses = mMap.getAddresses();
        //if (addresses != null && addresses.size() > 0) {
            //for (int i = 0; i < addresses.size(); i++) {
                //Address addr = addresses.get(i);
                //GeoPoint point = new GeoPoint((((int)(addr.getLatitude() * 1e6))),
                        //(((int)(1e6 * addr.getLongitude()))));
        		GeoPoint point = BrowseMap.HOME_POINT;
                Point screenCoords = mMap.getProjection().toPixels(point, null);     
                //---add balloon---
                Bitmap bmp = BitmapFactory.decodeResource(mMap.getResources(), R.drawable.pin);            
                canvas.drawBitmap(bmp, screenCoords.x - BALLOON_HEIGHT/3, screenCoords.y - BALLOON_HEIGHT, paint);
            //}
        //}
    }
    /*
    @Override
    public boolean onTap(GeoPoint point, MapView view) {
        mMap.notifyUser("Tapped: " + Double.toString(point.getLatitudeE6()/1000000.0) 
        		+ "/" + Double.toString(point.getLongitudeE6()/1000000.0));
        return super.onTap(point, view);
    }
    
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
