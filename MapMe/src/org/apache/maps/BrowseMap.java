package org.apache.maps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.db4o.android.Db4oHelper;
import com.db4o.android.MapBookmark;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class BrowseMap extends MapActivity implements LocationListener {
	private MapView mMapView;
	private Db4oHelper db4oHelper;

	private static final int GET_SEARCH_TEXT = 0;
	private static final int GET_BOOKMARK_INFO = 1;
	private static final int EDIT_BOOKMARKS = 2;
	private static final int RESULT_GOTO_MAP = 3;

	public static int MAX_RESULTS = 9;

	public static final String BKM_LATITUDE = "lat";
	public static final String BKM_LONGITUDE = "lon";

	Geocoder mGeocoder = null;
	List<Address> addresses = new ArrayList<Address>();

	// Menu Item order
	public static final int ZOOM_IN_INDEX = Menu.FIRST;
	public static final int ZOOM_OUT_INDEX = Menu.FIRST + 1;
	public static final int FIND_INDEX = Menu.FIRST + 2;
	public static final int SAVE_INDEX = Menu.FIRST + 3;
	public static final int EDIT_INDEX = Menu.FIRST + 4;
	public static final int MAP_MODE_INDEX = Menu.FIRST + 5;
	public static final int SATELLITE_INDEX = Menu.FIRST + 6;
	public static final int TRAFFIC_INDEX = Menu.FIRST + 7;
	public static final int STREETVIEW_INDEX = Menu.FIRST + 8;
	public static final int CENTER_LOCATION_INDEX = Menu.FIRST + 9;
	public static final int TRACK_LOCATION_INDEX = Menu.FIRST + 10;
	
	public static final String BM_NAME = "name";
	public static final String BM_DESC = "desc";

	protected static MapBookmark bookmark;
	
	protected static boolean trackLocation = false;
	
	protected static final GeoPoint HOME_POINT = new GeoPoint((int) (37.524393 * 1e6), 
			(int) (-122.256527255 * 1e6)); //Versant (Redwood City)

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		resetToHomePoint();
		//mMapView.invalidate(); //necessary?
		mapOverlays().add(new MyOverlay(this));
		dbHelper();
	}
	
	protected List<Overlay> mapOverlays(){
		return mMapView.getOverlays();
	}
	
	protected void animateTo(GeoPoint point){
		mapController().animateTo(point);
		//mapController().zoomToSpan(point.getLatitudeE6(), point.getLongitudeE6());
	}
	
	protected void animateTo(GeoPoint point, Message msg){
		mapController().animateTo(point, msg);
	}
	
	protected void resetToHomePoint(){
		mMapView.setSatellite(false);
		mMapView.setTraffic(false);
		mMapView.setStreetView(false);
		animateTo(HOME_POINT);
		mapController().setZoom(17);
	}
	
	protected MapController mapController(){
		return mMapView.getController();
	}

	private Db4oHelper dbHelper() {
		if (db4oHelper == null) {
			db4oHelper = new Db4oHelper(this);
			db4oHelper.db();
		}
		return db4oHelper;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		//menu.add(0, ZOOM_IN_INDEX, ZOOM_IN_INDEX, R.string.zoom_in);
		//menu.add(0, ZOOM_OUT_INDEX, ZOOM_OUT_INDEX, R.string.zoom_out);
		menu.add(0, FIND_INDEX, FIND_INDEX, R.string.find).setIcon(R.drawable.findonmap);
		menu.add(0, SAVE_INDEX, SAVE_INDEX, R.string.save).setIcon(R.drawable.save);
		menu.add(0, EDIT_INDEX, EDIT_INDEX, R.string.edit).setIcon(R.drawable.bookmarks);
		//menu.add(0, MAP_MODE_INDEX, MAP_MODE_INDEX, R.string.map_mode);
		//menu.add(0, SATELLITE_INDEX, SATELLITE_INDEX, R.string.satellite);
		//menu.add(0, TRAFFIC_INDEX, TRAFFIC_INDEX, R.string.traffic);
		//menu.add(0, STREETVIEW_INDEX, STREETVIEW_INDEX, R.string.streetview);
		menu.add(0, CENTER_LOCATION_INDEX, CENTER_LOCATION_INDEX, R.string.center_gps).setIcon(R.drawable.mylocation);
		menu.add(0, TRACK_LOCATION_INDEX, TRACK_LOCATION_INDEX, R.string.track_gps).setIcon(R.drawable.trackme);
		//Map Mode submenu
		SubMenu subMenu = menu.addSubMenu(0, MAP_MODE_INDEX, MAP_MODE_INDEX, R.string.map_mode).setIcon(R.drawable.mapmode);
		subMenu.add(0, SATELLITE_INDEX, SATELLITE_INDEX, R.string.satellite);
		subMenu.add(0, TRAFFIC_INDEX, TRAFFIC_INDEX, R.string.traffic);
		subMenu.add(0, STREETVIEW_INDEX, STREETVIEW_INDEX, R.string.streetview);
		subMenu.setGroupCheckable(0, true, true);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ZOOM_IN_INDEX:
			return performZoomIn();
		case ZOOM_OUT_INDEX:
			return performZoomOut();
		case SATELLITE_INDEX:
			if (item.isChecked()) 
				item.setChecked(false);
			else 
				item.setChecked(true);
			return performToggleSatellite();
		case TRAFFIC_INDEX:
			if (item.isChecked()) 
				item.setChecked(false);
			else 
				item.setChecked(true);
			return performToggleTraffic();
		case STREETVIEW_INDEX:
			if (item.isChecked()) 
				item.setChecked(false);
			else 
				item.setChecked(true);
			return performToggleStreetView();
		case FIND_INDEX:
			return performFindLocation();
		case CENTER_LOCATION_INDEX:
			return performCenterOnLocation();
		case TRACK_LOCATION_INDEX:
			return performTrackLocation();
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
		} else if (keyCode == KeyEvent.KEYCODE_V) {
			return performToggleStreetView();
		} else if (keyCode == KeyEvent.KEYCODE_F) {
			return performFindLocation();
		} else if (keyCode == KeyEvent.KEYCODE_G) {
			return performCenterOnLocation();
		} else if (keyCode == KeyEvent.KEYCODE_X) {
			return performTrackLocation();
		} else if (keyCode == KeyEvent.KEYCODE_C) {
			return performCreateBookmark();
		} else if (keyCode == KeyEvent.KEYCODE_E) {
			return performEditBookmarks();
		} else if (keyCode == KeyEvent.KEYCODE_P) {
			return performFindPizza();
		} else if (keyCode >= KeyEvent.KEYCODE_1
				&& keyCode <= KeyEvent.KEYCODE_9) {
			int item = keyCode - KeyEvent.KEYCODE_1;
			if (addresses.size() > item) {
				notifyUser(addresses.get(item).toString());
				goTo(item);
			}
		}
		return false;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public boolean performZoomIn() {
		// Zoom In
		int level = mMapView.getZoomLevel();
		mapController().setZoom(level + 1);
		return true;
	}

	public boolean performZoomOut() {
		// Zoom Out
		int level = mMapView.getZoomLevel();
		mapController().setZoom(level - 1);
		return true;
	}

	public boolean performToggleSatellite() {
		// Switch on/off the satellite images
		mMapView.setSatellite(!mMapView.isSatellite());
		//mMapView.invalidate();
		return true;
	}

	public boolean performToggleTraffic() {
		// Switch on/off traffic overlays
		mMapView.setTraffic(!mMapView.isTraffic());
		//mMapView.invalidate();
		return true;
	}
	
	public boolean performToggleStreetView() {
		// Switch on/off StreetView mode
		mMapView.setStreetView(!mMapView.isStreetView());
		//mMapView.invalidate();
		return true;
	}

	public boolean performFindLocation() {
		Intent intent = new Intent(BrowseMap.this, org.apache.maps.Search.class);
		startActivityForResult(intent, GET_SEARCH_TEXT);
		return true;
	}

	//Get last known location and center map on it
	public boolean performCenterOnLocation() {
		//Get location manager
	 	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	 	//Register this as location listener
	 	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);
	 	//Select best provider (will try GPS first, then network triangulation)
	 	String provider = locationManager.getBestProvider(new Criteria(), true);
	 	
	 	if (provider == null) {
			notifyUser("No location provider found");
			return false;
		}
	 	
	 	if(!locationManager.isProviderEnabled(provider)){
			notifyUser(provider + " is disabled");
			return false;
		}
	 	
		Location currentLocation = locationManager.getLastKnownLocation(provider);
		
		if(currentLocation == null){
			notifyUser("Unknown location");
			return false;
		}
		// Get point
		GeoPoint point = new GeoPoint(
				(int) (currentLocation.getLatitude() * 1e6), 
				(int) (currentLocation.getLongitude() * 1e6));
		// Center on map
		animateTo(point);

		return true;
	}
	
	public void onLocationChanged(Location location) {
		if (location != null) {
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			GeoPoint p = new GeoPoint((int) lat * 1000000, (int) lon * 1000000);
			//notifyUser("Location: " + Double.toString(lat) + "/" + Double.toString(lon));
			if(trackLocation)
				animateTo(p);
		}
	}

	public boolean performTrackLocation() {
		trackLocation = !trackLocation;
		return true;
	}

	public boolean performCreateBookmark() {
		Intent intent = new Intent(BrowseMap.this, org.apache.maps.Bookmark.class);
		GeoPoint loc = mMapView.getMapCenter();
		MapBookmark mb = new MapBookmark();
		mb.setLatitude(loc.getLatitudeE6());
		mb.setLongitude(loc.getLongitudeE6());
		mb.setZoomLevel(mMapView.getZoomLevel());
		mb.setSatellite(mMapView.isSatellite());
		mb.setTraffic(mMapView.isTraffic());
		Bookmark.current = mb;
		startActivityForResult(intent, GET_BOOKMARK_INFO);
		return true;
	}

	public boolean performEditBookmarks() {
		Intent intent = new Intent(BrowseMap.this, org.apache.maps.BookmarkList.class);
		startActivityForResult(intent, EDIT_BOOKMARKS);
		return true;
	}

	public boolean performFindPizza() {
		startSearch("Pizza");
		return true;
	}

	private void startSearch(final String text) {
		mGeocoder = new Geocoder(this.getApplicationContext(), Locale.getDefault());
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					addresses = mGeocoder.getFromLocationName(text, MAX_RESULTS);
					if (addresses.size() > 0) 
						goTo(0);
					else
						notifyUser("No location found!");
				} catch (IOException ioe) {
					notifyUser("Search failed:" + ioe.getMessage());
				}
			}

		});
		t.start();

	}

	private void goTo(int itemNo) {
		Address addr = addresses.get(itemNo);
		GeoPoint p = new GeoPoint(((int) (1e6 * addr.getLatitude())),
				((int) (1e6 * addr.getLongitude())));
		animateTo(p);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GET_SEARCH_TEXT) {
			String searchString = data.getStringExtra("searchString");
			startSearch(searchString);
			
		} else if (requestCode == GET_BOOKMARK_INFO) {
			if (data != null) {
				String name = data.getStringExtra(BM_NAME);
				if (name != null && name.length() > 0) {
					String desc;
					try {
						desc = data.getStringExtra(BM_DESC);
					} catch (Exception e) {
						desc = "";
					}
					dbHelper().setBookmark(name, desc,
							Bookmark.current.getLatitude(),
							Bookmark.current.getLongitude(),
							Bookmark.current.getZoomLevel(),
							Bookmark.current.isSatellite(),
							Bookmark.current.isTraffic());
				} else {
					notifyUser("Please enter a name");
				}
			} else {
				notifyUser("Please enter a name");
			}
		} else if (requestCode == EDIT_BOOKMARKS) {
			if (resultCode == RESULT_GOTO_MAP) {
				if (mMapView.isSatellite() != BrowseMap.bookmark.isSatellite())
					mMapView.setSatellite(true);
				if (mMapView.isTraffic() != BrowseMap.bookmark.isTraffic())
					mMapView.setTraffic(true);
				// Navigate to bookmarked point
				GeoPoint p = new GeoPoint(BrowseMap.bookmark.getLatitude(),
						BrowseMap.bookmark.getLongitude());
				animateTo(p);
				mapController().setZoom(BrowseMap.bookmark.getZoomLevel());
			}
		}
	}

	//Popup window to quickly pass user notifications
	void notifyUser(Context ctx, String message){
		Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
	}
	
	void notifyUser(String message) {
		notifyUser(BrowseMap.this, message);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper().close();
		db4oHelper = null;
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public Projection getProjection() {
		return mMapView.getProjection();
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * @Override protected void onPause() { super.onPause(); dbHelper().close();
	 * db4oHelper = null; }
	 */

	/*
	 * @Override protected void onResume() { super.onResume(); dbHelper();
	 * //populateFields(); }
	 */
}
