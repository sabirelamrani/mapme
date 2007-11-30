package org.apache.maps;

import java.util.List;

import android.app.NotificationManager;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Menu.Item;

import com.db4o.android.Db4oHelper;
import com.db4o.android.MapBookmark;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MyMapView;
import com.google.android.maps.Point;
import com.google.googlenav.Search;
import com.google.googlenav.map.MapPoint;

public class BrowseMap extends MapActivity {
    private MyMapView mMapView;
    private Search mSearch;
    private Db4oHelper db4oHelper;

    private String LOG_TAG = "AndroidMaps";
    private static final int GET_SEARCH_TEXT = 0;
    private static final int GET_BOOKMARK_INFO = 1;
    private static final int EDIT_BOOKMARKS = 2;
    private static final int RESULT_GOTO_MAP = 3;
    
    public static final String BKM_LATITUDE = "lat";
    public static final String BKM_LONGITUDE = "lon";
    
    
 // Menu Item order
    public static final int ZOOM_IN_INDEX = Menu.FIRST;
    public static final int ZOOM_OUT_INDEX = Menu.FIRST + 1;
    public static final int SATELLITE_INDEX = Menu.FIRST + 2;
    public static final int TRAFFIC_INDEX = Menu.FIRST + 3;
    public static final int FIND_INDEX = Menu.FIRST + 4;
    public static final int CENTER_GPS_INDEX = Menu.FIRST + 5;
    public static final int TRACK_GPS_INDEX = Menu.FIRST + 6;
    public static final int SAVE_INDEX = Menu.FIRST + 7;
    public static final int EDIT_INDEX = Menu.FIRST + 8;
    
    protected static MapBookmark bookmark;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mMapView = new MyMapView(this);
        dbHelper();

        // Use Yahoo Geo code to find the lat/long.
        // Click on the Sample Request URL here for example
        // http://developer.yahoo.com/maps/rest/V1/geocode.html
        Point p = new Point((int) (37.553000 * 1000000), (int)
                (-122.292060 * 1000000)); //db4objects, Inc. (San Mateo)
        MapController mc = mMapView.getController();
        mc.animateTo(p);
        mc.zoomTo(14);
        setContentView(mMapView);

        mMapView.createOverlayController().add(new MyOverlay(this), true);
    }
    
    private Db4oHelper dbHelper(){
    	if(db4oHelper == null){
    		db4oHelper = new Db4oHelper(this);
    		db4oHelper.db();
    	}
    	return db4oHelper;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	
		menu.add(0, ZOOM_IN_INDEX, R.string.zoom_in);
		menu.add(0, ZOOM_OUT_INDEX, R.string.zoom_out);
		menu.add(0, SATELLITE_INDEX, R.string.satellite);
		menu.add(0, TRAFFIC_INDEX, R.string.traffic);
		menu.add(0, FIND_INDEX, R.string.find);
		menu.add(0, CENTER_GPS_INDEX, R.string.center_gps);
		menu.add(0, TRACK_GPS_INDEX, R.string.track_gps);
		menu.add(0, SAVE_INDEX, R.string.save);
		menu.add(0, EDIT_INDEX, R.string.edit);
	
		return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(Item item) {
		switch(item.getId()) {
		case ZOOM_IN_INDEX:
		    return performZoomIn();
		case ZOOM_OUT_INDEX:
			return performZoomOut();
		case SATELLITE_INDEX:
			return performToggleSatellite();
		case TRAFFIC_INDEX:
			return performToggleTraffic();
		case FIND_INDEX:
			return performFindLocation();
		case CENTER_GPS_INDEX:
			return performCenterOnGPS();
		case TRACK_GPS_INDEX:
			return performTrackGPS();
		case SAVE_INDEX:
			return performCreateBookmark();
		case EDIT_INDEX:
			return performEditBookmarks();
		}
		return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_I) {
            return performZoomIn();
        } else if (keyCode == KeyEvent.KEYCODE_O) {
        	return performZoomOut();
        } else if (keyCode == KeyEvent.KEYCODE_S) {
            return performToggleSatellite();
        } else if (keyCode == KeyEvent.KEYCODE_T) {
            return performToggleTraffic();
        } else if (keyCode == KeyEvent.KEYCODE_F) {
            return performFindLocation();
        } else if (keyCode == KeyEvent.KEYCODE_G) {
            return performCenterOnGPS();
        } else if (keyCode == KeyEvent.KEYCODE_X) {
            return performTrackGPS();
        } else if (keyCode == KeyEvent.KEYCODE_C) {
            return performCreateBookmark();
        } else if (keyCode == KeyEvent.KEYCODE_E) {
            return performEditBookmarks();
        } else if (keyCode == KeyEvent.KEYCODE_P) {
            return performFindPizza();
        } else if (keyCode >= KeyEvent.KEYCODE_1 && keyCode <= KeyEvent.KEYCODE_9) {
            int item = keyCode - KeyEvent.KEYCODE_1;
            if (mSearch != null && mSearch.numPlacemarks() > item) {
                notifyUser(999, mSearch.getPlacemark(item).getDetailsDescriptor());
                goTo(item);
            }
        }
        return false;
    }

    public Search getSearch() {
        return mSearch;
    }
    
    public boolean performZoomIn(){
    	// Zoom In
    	int level = mMapView.getZoomLevel();
        mMapView.getController().zoomTo(level + 1);
        return true;
    }
    
    public boolean performZoomOut(){
    	// Zoom Out
        int level = mMapView.getZoomLevel();
        mMapView.getController().zoomTo(level - 1);
        return true;
    }
    
    public boolean performToggleSatellite(){
    	// Switch on the satellite images
        mMapView.toggleSatellite();
        return true;
    }
    
    public boolean performToggleTraffic(){
    	// Switch on traffic overlays
        mMapView.toggleTraffic();
        return true;
    }
    
    public boolean performFindLocation(){
    	Intent intent = new Intent(BrowseMap.this, org.apache.maps.Search.class);
        startSubActivity(intent, GET_SEARCH_TEXT);
        return true;
    }
    
    public boolean performCenterOnGPS(){
    	//Get handler to system location manager 
    	LocationManager locMan = (LocationManager) 
    		getSystemService(LOCATION_SERVICE);
    	
    	// Get the first provider available 
    	List<LocationProvider> providers = locMan.getProviders(); 
    	LocationProvider provider = providers.get(0); 
    	
    	//Returns a new location fix from the given provider 
    	Location curLoc = locMan.getCurrentLocation(provider.getName());
    	
    	//Get point
    	Point curLocAsPoint = new Point((int)(curLoc.getLatitude() * 1000000), 
    			(int)(curLoc.getLongitude() * 1000000)); 
    	
    	//Center on map
    	MapController mc = mMapView.getController();
        mc.animateTo(curLocAsPoint);

        return true;
    }
    
    public boolean performTrackGPS(){
    	this.notifyUser(23, "Not available yet");
    	return true;
    }
    
    public boolean performCreateBookmark(){
    	Intent intent = new Intent(BrowseMap.this, org.apache.maps.Bookmark.class);
    	Point loc = mMapView.getMapCenter();
    	Bookmark.current = new MapBookmark();
    	Bookmark.current.setLatitude(loc.getLatitudeE6());
    	Bookmark.current.setLongitude(loc.getLongitudeE6());
    	Bookmark.current.setZoomLevel(mMapView.getZoomLevel());
    	Bookmark.current.setSatellite(mMapView.isSatellite());
    	Bookmark.current.setTraffic(mMapView.isTraffic());
        startSubActivity(intent, GET_BOOKMARK_INFO);
        return true;
    }
    
    public boolean performEditBookmarks(){
    	Intent intent = new Intent(BrowseMap.this, org.apache.maps.BookmarkList.class);
        startSubActivity(intent, EDIT_BOOKMARKS);
        return true;
    }
    
    public boolean performFindPizza(){
    	startSearch("Pizza");
        return true;
    }

    private void startSearch(String text) {
        // Search for "text" near the center of the map
        mSearch = new Search(text, mMapView.getMap(), 0);

        // add the request the dispatcher
        getDispatcher().addDataRequest(mSearch);

        Thread t = new Thread(new Runnable() {
            public void run() {
                while (!mSearch.isComplete()) {
                    Log.i(LOG_TAG, ".");
                }
                if (mSearch.numPlacemarks() > 0) {
                	notifyUser(999, "Found " + mSearch.numPlacemarks() + " locations");
                    goTo(0);
                } else
                	notifyUser(1000, "Could not find any location");
            }
            
            void notifyUser(int id, String message){
            	NotificationManager nm = (NotificationManager)
                	getSystemService(NOTIFICATION_SERVICE);
            	nm.notifyWithText(
            			id,
            			message,
            			NotificationManager.LENGTH_LONG, 
            			null);
            }
        });
        t.start();
    }

    private void goTo(int itemNo) {
        MapPoint location = mSearch.getPlacemark(itemNo).getLocation();
        Point p = new Point(location.getLatitude(), location.getLongitude());
        MapController mc = mMapView.getController();
        mc.animateTo(p);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    String data, Bundle extras) {
        if (requestCode == GET_SEARCH_TEXT) {
            startSearch(data);
        } else if (requestCode == GET_BOOKMARK_INFO){
        	if(data != null){
        		String name = data.split("/n")[0];
        		String desc;
        		try{
        			desc = data.split("/n")[1];
        		}
        		catch(Exception e){
        			desc = "";
        		}
        		dbHelper().setBookmark(
        					name, 
        					desc, 
        					Bookmark.current.getLatitude(),
        					Bookmark.current.getLongitude(),
        					Bookmark.current.getZoomLevel(),
        					Bookmark.current.isSatellite(),
        					Bookmark.current.isTraffic());
        	}
        	else{
        		notifyUser(567, "Can't save without a name");
        	}
        } else if (requestCode == EDIT_BOOKMARKS){
        	if(resultCode == RESULT_GOTO_MAP){
        		MapController mc = mMapView.getController();
            	if(mMapView.isSatellite() != BrowseMap.bookmark.isSatellite())
            		mMapView.toggleSatellite();
            	if(mMapView.isTraffic() != BrowseMap.bookmark.isTraffic())
            		mMapView.toggleTraffic();
            	//Navigate to bookmarked point
            	Point p = new Point(BrowseMap.bookmark.getLatitude(), BrowseMap.bookmark.getLongitude());
            	mc.animateTo(p);
            	mc.zoomTo(BrowseMap.bookmark.getZoomLevel());
        	}
        }
    }
    
    void notifyUser(int id, String message){
    	NotificationManager nm = (NotificationManager)
        	getSystemService(NOTIFICATION_SERVICE);
    	nm.notifyWithText(
    			id,
    			message,
    			NotificationManager.LENGTH_LONG, 
    			null);
    }
    
    @Override
    protected void onFreeze(Bundle outState) {
		super.onFreeze(outState);
		dbHelper().close();
		db4oHelper = null;
    }

    /*@Override
    protected void onPause() {
		super.onPause();
		dbHelper().close();
		db4oHelper = null;
    }*/

    /*@Override
    protected void onResume() {
		super.onResume();
		dbHelper();
		//populateFields();
    }*/
}
