package org.apache.maps;

import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Geocoder;
import android.view.MotionEvent;

import com.db4o.android.MapBookmark;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class BookmarkOverlay extends Overlay {

	private static final int BALLOON_HEIGHT = 34;
	private static final int BALLOON_WIDTH = 37;
	private static final int BOOKMARK_CLICK_TOLERANCE = 400;
	
	BrowseMap mMap;
	MapView mView;
    Paint paint = new Paint();

    public BookmarkOverlay(BrowseMap map) {
        mMap = map;
        mView = map.mMapView;
    }

    public void draw(Canvas canvas, MapView map, boolean b) {
        super.draw(canvas, map, b);

        GeoPoint mapCenter = mView.getMapCenter();
        int latitudeSpan = mView.getLatitudeSpan();
        int longitudeSpan = mView.getLongitudeSpan();
        List<MapBookmark> bookmarks = 
        	mMap.dbHelper().getNearbyBookmarks(mapCenter, latitudeSpan, longitudeSpan);
        Iterator<MapBookmark> iterator = bookmarks.iterator();
        
        while(iterator.hasNext()){
        	MapBookmark bookmark = iterator.next();
        	GeoPoint point = new GeoPoint(bookmark.getLatitude(), bookmark.getLongitude());
        	Point screenCoords = mMap.getProjection().toPixels(point, null);     
            //---add balloon---
            Bitmap bmp = BitmapFactory.decodeResource(mMap.getResources(), R.drawable.pin);            
            canvas.drawBitmap(bmp, screenCoords.x - BALLOON_HEIGHT/3, screenCoords.y - BALLOON_HEIGHT, paint);
        }
    }
    
    @Override
    public boolean onTap(GeoPoint point, MapView view) {
        //mMap.notifyUser("Tapped: " + Double.toString(point.getLatitudeE6()/1.0E6) 
        		//+ "/" + Double.toString(point.getLongitudeE6()/1.0E6));
        
        List<MapBookmark> bookmarks = 
        	mMap.dbHelper().getNearbyBookmarks(point, BOOKMARK_CLICK_TOLERANCE);
        Iterator<MapBookmark> iterator = bookmarks.iterator();
        
        while(iterator.hasNext()){
        	MapBookmark bookmark = iterator.next();
        	mMap.notifyUser(bookmark.getName());
        }
        
        return super.onTap(point, view);
    }
    
    /*
    //Geocoding example
    public boolean onTouchEvent(MotionEvent event, MapView mapView) 
    {   
    	long downTime = event.getDownTime();
    	if(downTime >= 100 && downTime <= 120)
    		mMap.notifyUser("100-120 ms push");
        return true;
    }*/
}
