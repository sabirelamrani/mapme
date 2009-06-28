package org.apache.maps;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.location.Address;
import android.location.Geocoder;

import com.db4o.android.MapBookmark;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class BookmarkOverlay extends Overlay {

	private static final int BALLOON_HEIGHT = 34;
	private static final int BALLOON_WIDTH = 37;
	private static final int BOOKMARK_CLICK_TOLERANCE = 400;
	
	private BrowseMap mMap;
	private MapView mView;
    private Paint paint = new Paint();
    private static Paint innerPaint, borderPaint, textPaint;

    public BookmarkOverlay(BrowseMap map) {
        mMap = map;
        mView = map.mMapView;
    }

    public void draw(Canvas canvas, MapView map, boolean shadow) {
        super.draw(canvas, map, shadow);
        if(!shadow){
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
	        	//Draw balloons
	            Bitmap bmp = BitmapFactory.decodeResource(mMap.getResources(), R.drawable.pin);            
	            canvas.drawBitmap(bmp, screenCoords.x - BALLOON_HEIGHT/3, screenCoords.y - BALLOON_HEIGHT, paint);
	            drawInfoWindow(canvas, map, bookmark);
	        }
        }
    }
    
    private void drawInfoWindow(Canvas canvas, MapView	mapView, MapBookmark selectedMapLocation) {
    	if (selectedMapLocation != null) {
			//  First determine the screen coordinates of the selected MapLocation
			Point selDestinationOffset = new Point();
			mapView.getProjection().toPixels(selectedMapLocation.getPoint(), selDestinationOffset);    	
	    	//  Setup the info window with the right size & location
			int INFO_WINDOW_WIDTH = selectedMapLocation.getName().length()*9;//125;
			int INFO_WINDOW_HEIGHT = 25;
			RectF infoWindowRect = new RectF(0, 0, INFO_WINDOW_WIDTH, INFO_WINDOW_HEIGHT);				
			int infoWindowOffsetX = selDestinationOffset.x - INFO_WINDOW_WIDTH/2;
			int infoWindowOffsetY = selDestinationOffset.y - INFO_WINDOW_HEIGHT - BALLOON_HEIGHT - 2;
			infoWindowRect.offset(infoWindowOffsetX, infoWindowOffsetY);
			//  Draw inner info window
			canvas.drawRoundRect(infoWindowRect, 5, 5, getInnerPaint());
			//  Draw border for info window
			canvas.drawRoundRect(infoWindowRect, 5, 5, getBorderPaint());
			//  Draw the MapLocation's name
			int TEXT_OFFSET_X = INFO_WINDOW_WIDTH/2;
			int TEXT_OFFSET_Y = 15;
			canvas.drawText(selectedMapLocation.getName(), infoWindowOffsetX + TEXT_OFFSET_X, infoWindowOffsetY + TEXT_OFFSET_Y, getTextPaint());
    	}
    }
    
    @Override
    public boolean onTap(GeoPoint point, MapView view) {
        //mMap.notifyUser("Tapped: " + Double.toString(point.getLatitudeE6()/1.0E6) 
        		//+ "/" + Double.toString(point.getLongitudeE6()/1.0E6)); 
        /*List<MapBookmark> bookmarks = 
        	mMap.dbHelper().getNearbyBookmarks(point, BOOKMARK_CLICK_TOLERANCE);
        Iterator<MapBookmark> iterator = bookmarks.iterator();
        while(iterator.hasNext()){
        	MapBookmark bookmark = iterator.next();
        	mMap.notifyUser(bookmark.getName());
        }*/
        Geocoder geoCoder = new Geocoder(mMap.getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                point.getLatitudeE6()  / 1E6, 
                point.getLongitudeE6() / 1E6, 1);
            String address = "";
          //Display all lines of first address
            if (addresses.size() > 0) 
                for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
                   address += addresses.get(0).getAddressLine(i) + "\n";
            mMap.notifyUser(address);
        }
        catch (IOException e) {                
            mMap.notifyUser("GeoCoding error");
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
    
    public Paint getInnerPaint() {
		if (innerPaint == null) {
			innerPaint = new Paint();
			innerPaint.setARGB(225, 75, 75, 75); //grey
			innerPaint.setAntiAlias(true);
		}
		return innerPaint;
	}

	public Paint getBorderPaint() {
		if (borderPaint == null) {
			borderPaint = new Paint();
			borderPaint.setARGB(255, 255, 255, 255);
			borderPaint.setAntiAlias(true);
			borderPaint.setStyle(Style.STROKE);
			borderPaint.setStrokeWidth(2);
		}
		return borderPaint;
	}

	public Paint getTextPaint() {
		if (textPaint == null) {
			textPaint = new Paint();
			textPaint.setARGB(255, 255, 255, 255);
			textPaint.setAntiAlias(true);
			textPaint.setTextAlign(Align.CENTER);
		}
		return textPaint;
	}
}
