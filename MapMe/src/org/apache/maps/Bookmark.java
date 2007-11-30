package org.apache.maps;

import com.db4o.android.Db4oHelper;
import com.db4o.android.MapBookmark;
//import com.google.googlenav.map.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

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
                setResult(
                		RESULT_OK, 
                		((EditText)findViewById(R.id.name)).getText().toString()
                		+ "/n"
                		+ ((EditText)findViewById(R.id.description)).getText().toString());
                finish();
            } });

    }
    
    private Db4oHelper dbHelper(){
    	if(db4oHelper == null)
    		db4oHelper = new Db4oHelper(this);
    	return db4oHelper;
    }
    
    private void populateFields(){
    	if(name != null){
    		MapBookmark entry = dbHelper().getBookmark(name);
		    if (entry != null) {
		    	nameText.setText(entry.name);
			    descriptionText.setText(entry.description);
			    latitudeText.setText(Integer.toString(entry.getLatitude()));
			    longitudeText.setText(Integer.toString(entry.getLongitude()));
		    }
    	} else {
    		latitudeText.setText(Integer.toString(Bookmark.current.getLatitude()));
		    longitudeText.setText(Integer.toString(Bookmark.current.getLongitude()));
    	}
    }
}