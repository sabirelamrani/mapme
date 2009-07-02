package org.apache.maps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.db4o.android.Db4oHelper;
import com.db4o.android.MapBookmark;

public class SearchList extends ListActivity {
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
		entries = BrowseMap.foundBookmarks;
		//entries = new ArrayList<MapBookmark>();
		//Iterator<MapBookmark> bookmarkIterator = BrowseMap.foundBookmarks.iterator();
		//while(bookmarkIterator.hasNext())
			//entries.add(bookmarkIterator.next());
		if(BrowseMap.foundAddresses != null){
			Iterator<Address> addressIterator = BrowseMap.foundAddresses.iterator();
			while(addressIterator.hasNext())
				entries.add(new MapBookmark(addressIterator.next()));
		}
		if(entries.isEmpty())
			notifyUser("No entries");
		else
			for (MapBookmark entry : entries)
				items.add(entry.name);
		ArrayAdapter<String> entries = 
		    new ArrayAdapter<String>(this, R.layout.bkm_row, items);
		setListAdapter(entries);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	
		/*menu.add(0, EDIT_BOOKMARK_INDEX, EDIT_BOOKMARK_INDEX, R.string.bkm_edit);
		menu.add(0, DEL_BOOKMARK_INDEX, DEL_BOOKMARK_INDEX, R.string.bkm_delete);
		menu.add(0, COUNT_BOOKMARK_INDEX, COUNT_BOOKMARK_INDEX, R.string.bkm_count);
		menu.add(0, GOTO_BOOKMARK_INDEX, GOTO_BOOKMARK_INDEX, R.string.bkm_goto);*/
	
		return super.onCreateOptionsMenu(menu);
    }

    private void delBookmark(String name) {
		dbHelper().deleteBookmark(name);
		fillData();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
    	int position = item.getItemId();
    	int itemPosition = getSelectedItemPosition() < 0 ? 0 : getSelectedItemPosition();
    	if (entries.size() == 0) {
    		position = COUNT_BOOKMARK_INDEX;
    	} 
		switch(position) {
		case EDIT_BOOKMARK_INDEX:
		    editBookmark(itemPosition);
		    break;
		case DEL_BOOKMARK_INDEX:
			delBookmark(entries.get(itemPosition).name);
		    break;
		case GOTO_BOOKMARK_INDEX:
			BrowseMap.bookmark = entries.get(itemPosition);
			setResult(RESULT_GOTO_MAP);
			finish();
		    break;
		case COUNT_BOOKMARK_INDEX:
		    int count = dbHelper().bookmarkCount();
		    notifyUser("Entries: " + Integer.toString(count));
		    break;
		}
		return super.onOptionsItemSelected(item);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		BrowseMap.bookmark = entries.get(position);
		setResult(RESULT_GOTO_MAP);
		finish();
    }
    
    private void editBookmark(int position){
    	Intent i = new Intent(this, Bookmark.class);
    	MapBookmark mb = entries.get(position);
    	i.putExtra(BookmarkList.BKM_NAME, mb.name);
		Bookmark.current = mb;
		startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
	    Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTIVITY_EDIT && resultCode != 0){
        	String name = data.getStringExtra(BKM_NAME);
        	String desc;
        	try{
        		desc = data.getStringExtra(BKM_DESC);
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
    	Toast.makeText(SearchList.this, msg, Toast.LENGTH_SHORT).show();
    }
}
