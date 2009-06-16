package org.apache.maps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.db4o.android.Db4oHelper;
import com.db4o.android.MapBookmark;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class BrowseMap extends MapActivity {
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
	public static final int SATELLITE_INDEX = Menu.FIRST + 5;
	public static final int TRAFFIC_INDEX = Menu.FIRST + 6;
	public static final int CENTER_GPS_INDEX = Menu.FIRST + 7;
	public static final int TRACK_GPS_INDEX = Menu.FIRST + 8;
	

	public static final String BM_NAME = "name";
	public static final String BM_DESC = "desc";

	protected static MapBookmark bookmark;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		GeoPoint p = new GeoPoint((int) (37.524393 * 1e6),
				(int) (-122.256527255 * 1e6)); // Versant (Redwood City)
		MapController mc = mMapView.getController();
		mc.animateTo(p);
		//mc.zoomToSpan(p.getLatitudeE6(), p.getLongitudeE6());
		mc.setZoom(17);
		mMapView.setSatellite(true);
		mMapView.getOverlays().add(new MyOverlay(this));
		dbHelper();
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
		menu.add(0, FIND_INDEX, FIND_INDEX, R.string.find);
		menu.add(0, SAVE_INDEX, SAVE_INDEX, R.string.save);
		menu.add(0, EDIT_INDEX, EDIT_INDEX, R.string.edit);
		menu.add(0, SATELLITE_INDEX, SATELLITE_INDEX, R.string.satellite);
		menu.add(0, TRAFFIC_INDEX, TRAFFIC_INDEX, R.string.traffic);
		menu.add(0, CENTER_GPS_INDEX, CENTER_GPS_INDEX, R.string.center_gps);
		menu.add(0, TRACK_GPS_INDEX, TRACK_GPS_INDEX, R.string.track_gps);

		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
		mMapView.getController().setZoom(level + 1);
		return true;
	}

	public boolean performZoomOut() {
		// Zoom Out
		int level = mMapView.getZoomLevel();
		mMapView.getController().setZoom(level - 1);
		return true;
	}

	public boolean performToggleSatellite() {
		// Switch on the satellite images
		mMapView.setSatellite(!mMapView.isSatellite());
		return true;
	}

	public boolean performToggleTraffic() {
		// Switch on traffic overlays
		mMapView.setTraffic(!mMapView.isTraffic());
		return true;
	}

	public boolean performFindLocation() {
		Intent intent = new Intent(BrowseMap.this, org.apache.maps.Search.class);
		startActivityForResult(intent, GET_SEARCH_TEXT);
		return true;
	}

	public boolean performCenterOnGPS() {
		// Get handler to system location manager
		LocationManager locMan = (LocationManager) getSystemService(LOCATION_SERVICE);

		// Get the first provider available
		List<String> providerNames = locMan.getProviders(true);

		if (providerNames.size() == 0) {
			notifyUser("No GPS providers available!");
			return false;
		}

		// Returns a new location fix from the given provider
		Location curLoc = locMan.getLastKnownLocation(providerNames.get(0));

		// Get point
		GeoPoint curLocAsPoint = new GeoPoint(
				(int) (curLoc.getLatitude() * 1000000), (int) (curLoc
						.getLongitude() * 1000000));

		// Center on map
		MapController mc = mMapView.getController();
		mc.animateTo(curLocAsPoint);

		return true;
	}

	public boolean performTrackGPS() {
		this.notifyUser("Not available yet");
		return true;
	}

	public boolean performCreateBookmark() {
		Intent intent = new Intent(BrowseMap.this,
				org.apache.maps.Bookmark.class);
		GeoPoint loc = mMapView.getMapCenter();
		Bookmark.current = new MapBookmark();
		Bookmark.current.setLatitude(loc.getLatitudeE6());
		Bookmark.current.setLongitude(loc.getLongitudeE6());
		Bookmark.current.setZoomLevel(mMapView.getZoomLevel());
		Bookmark.current.setSatellite(mMapView.isSatellite());
		Bookmark.current.setTraffic(mMapView.isTraffic());
		startActivityForResult(intent, GET_BOOKMARK_INFO);
		return true;
	}

	public boolean performEditBookmarks() {
		Intent intent = new Intent(BrowseMap.this,
				org.apache.maps.BookmarkList.class);
		startActivityForResult(intent, EDIT_BOOKMARKS);
		return true;
	}

	public boolean performFindPizza() {
		startSearch("Pizza");
		return true;
	}

	private void startSearch(final String text) {
		mGeocoder = new Geocoder(this.getApplicationContext(), Locale
				.getDefault());

		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					addresses = mGeocoder
							.getFromLocationName(text, MAX_RESULTS);
					if (addresses.size() > 0) {
						goTo(0);
					} 
				} catch (IOException ioe) {
					//Could not find any location: + ioe.getMessage()
				}
			}

		});
		t.start();

	}

	private void goTo(int itemNo) {
		Address addr = addresses.get(itemNo);

		GeoPoint p = new GeoPoint(((int) (1e6 * addr.getLatitude())),
				((int) (1e6 * addr.getLongitude())));
		MapController mc = mMapView.getController();
		mc.animateTo(p);
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
					notifyUser("Can't save without a name");
				}
			} else {
				notifyUser("Can't save without a name");
			}
		} else if (requestCode == EDIT_BOOKMARKS) {
			if (resultCode == RESULT_GOTO_MAP) {
				MapController mc = mMapView.getController();
				if (mMapView.isSatellite() != BrowseMap.bookmark.isSatellite())
					mMapView.setSatellite(true);
				if (mMapView.isTraffic() != BrowseMap.bookmark.isTraffic())
					mMapView.setTraffic(true);
				// Navigate to bookmarked point
				GeoPoint p = new GeoPoint(BrowseMap.bookmark.getLatitude(),
						BrowseMap.bookmark.getLongitude());
				mc.animateTo(p);
				mc.setZoom(BrowseMap.bookmark.getZoomLevel());
			}
		}
	}

	void notifyUser(String message) {
		Toast.makeText(BrowseMap.this, message, Toast.LENGTH_SHORT).show();
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
	/*
	 * @Override protected void onPause() { super.onPause(); dbHelper().close();
	 * db4oHelper = null; }
	 */

	/*
	 * @Override protected void onResume() { super.onResume(); dbHelper();
	 * //populateFields(); }
	 */
}
