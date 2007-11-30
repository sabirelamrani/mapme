package org.apache.maps;

import java.util.ArrayList;
import java.util.List;

import com.db4o.android.Db4oHelper;
import com.db4o.android.MapBookmark;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Menu.Item;
import android.view.View.MeasureSpec;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BookmarkList extends ListActivity {

    private static final int ACTIVITY_EDIT = 0;
    private static final int RESULT_GOTO_MAP = 3;

    // Menu Item order
    public static final int EDIT_BOOKMARK_INDEX = Menu.FIRST;
    public static final int DEL_BOOKMARK_INDEX = Menu.FIRST + 1;
    public static final int COUNT_BOOKMARK_INDEX = Menu.FIRST + 2;
    public static final int GOTO_BOOKMARK_INDEX = Menu.FIRST + 3;
    
    public static final String BKM_NAME = "name";
    public static final String BKM_DESC = "desc";
    
    private Db4oHelper db4oHelper;		
    private List<MapBookmark> entries;

    /** 
     * Called when the activity is first created. 
     */
    @Override
    public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.bkm_list);
		fillData();
    }
    
    private Db4oHelper dbHelper(){
    	if(db4oHelper == null)
    		db4oHelper = new Db4oHelper(this);
    	return db4oHelper;
    }

    /**
     * Populates the bookmark ListView
     */
    private void fillData() {
		List<String> items = new ArrayList<String>();
		entries = dbHelper().getBookmarkList();
		for (MapBookmark entry : entries)
			items.add(entry.name);
		ArrayAdapter<String> entries = 
		    new ArrayAdapter<String>(this, R.layout.bkm_row, items);
		setListAdapter(entries);
		setupListStripes();
    }

    /**
     * Add stripes to the list view.
     * 
     * This will alternate row colors in the list view. 100% borrowed from
     * googles notepad application.  
     */
    private void setupListStripes() {
		// Get Drawables for alternating stripes
		Drawable[] lineBackgrounds = new Drawable[2];
	
		lineBackgrounds[0] = getResources().getDrawable(R.drawable.even_stripe);
		lineBackgrounds[1] = getResources().getDrawable(R.drawable.odd_stripe);
	
		// Make and measure a sample TextView of the sort our adapter will return
		View view = getViewInflate().inflate(
			android.R.layout.simple_list_item_1, null, null);
	
		TextView v = (TextView) view.findViewById(android.R.id.text1);
		v.setText("X");
		// Make it 100 pixels wide, and let it choose its own height.
		v.measure(MeasureSpec.makeMeasureSpec(View.MeasureSpec.EXACTLY, 100),
			MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, 0));
		int height = v.getMeasuredHeight();
		getListView().setStripes(lineBackgrounds, height);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	
		menu.add(0, EDIT_BOOKMARK_INDEX, R.string.bkm_edit);
		menu.add(0, DEL_BOOKMARK_INDEX, R.string.bkm_delete);
		menu.add(0, COUNT_BOOKMARK_INDEX, R.string.bkm_count);
		menu.add(0, GOTO_BOOKMARK_INDEX, R.string.bkm_goto);
	
		return super.onCreateOptionsMenu(menu);
    }

    private void delBookmark(String name) {
		dbHelper().deleteBookmark(name);
		fillData();
    }

    public boolean onOptionsItemSelected(Item item) {
		switch(item.getId()) {
		case EDIT_BOOKMARK_INDEX:
		    editBookmark(getSelection());
		    break;
		case DEL_BOOKMARK_INDEX:
			delBookmark(entries.get(getSelection()).name);
		    break;
		case GOTO_BOOKMARK_INDEX:
			BrowseMap.bookmark = entries.get(getSelection());
			setResult(RESULT_GOTO_MAP);
			finish();
		    break;
		case COUNT_BOOKMARK_INDEX:
		    int count = dbHelper().bookamrkCount();
		    notifyUser("Entries: " + Integer.toString(count));
		    break;
		}
		return super.onOptionsItemSelected(item);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		editBookmark(position);
    }
    
    private void editBookmark(int position){
    	Intent i = new Intent(this, Bookmark.class);
    	MapBookmark mb = entries.get(position);
    	i.putExtra(BookmarkList.BKM_NAME, mb.name);
		Bookmark.current = mb;
		startSubActivity(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
	    String data, Bundle extras) {

		super.onActivityResult(requestCode, resultCode, data, extras);
		if (requestCode == ACTIVITY_EDIT && resultCode != 0){
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
		fillData();
    }

    protected void notifyUser(String msg){
    	NotificationManager nm = (NotificationManager)
			getSystemService(NOTIFICATION_SERVICE);
    	nm.notifyWithText(100, msg, NotificationManager.LENGTH_SHORT, null);
    }

}
