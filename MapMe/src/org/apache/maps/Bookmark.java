package org.apache.maps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.db4o.android.Db4oHelper;
import com.db4o.android.MapBookmark;

public class Bookmark extends Activity {

	private EditText nameText;
	private EditText descriptionText;
	private EditText latitudeText;
	private EditText longitudeText;

	private String name;
	public static MapBookmark current;

	private Db4oHelper db4oHelper;

	public Bookmark() {
	}

	/**
	 * Called with the activity is first created.
	 */
	@Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.bookmark);
        
		nameText = (EditText) findViewById(R.id.name);
        descriptionText = (EditText) findViewById(R.id.description);
		latitudeText = (EditText) findViewById(R.id.latitude);
		longitudeText = (EditText) findViewById(R.id.longitude);
		latitudeText.setEnabled(false);
		longitudeText.setEnabled(false);

        Button addButton = (Button) findViewById(R.id.save);
        
        try{
			Bundle extras = getIntent().getExtras();
			name = extras.getString(BookmarkList.BKM_NAME);
		}
		catch(Exception e){
			name = null;
		}
		
		populateFields();
        
        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	Intent result = new Intent();
            	result.putExtra(BrowseMap.BM_NAME, nameText.getText().toString());
            	result.putExtra(BrowseMap.BM_DESC, descriptionText.getText().toString());
                setResult(
                		RESULT_OK, result);
                finish();
            } });

    }

	private Db4oHelper dbHelper() {
		if (db4oHelper == null)
			db4oHelper = new Db4oHelper(this);
		return db4oHelper;
	}

	private void populateFields() {
		if (name != null) {
			MapBookmark entry = dbHelper().getBookmark(name);
			if (entry != null) {
				nameText.setText(entry.name);
				descriptionText.setText(entry.description);
				latitudeText.setText(Double.toString(entry.getLatitude()/1000000.0));
				longitudeText.setText(Double.toString(entry.getLongitude()/1000000.0));
			}
		} else {
			latitudeText.setText(Double.toString(Bookmark.current
					.getLatitude()/1000000.0));
			longitudeText.setText(Double.toString(Bookmark.current
					.getLongitude()/1000000.0));
		}
	}
}