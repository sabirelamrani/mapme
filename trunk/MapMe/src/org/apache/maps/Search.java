package org.apache.maps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Search extends Activity {
    public Search() {
    }

    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.main);

        Button addButton = (Button) findViewById(R.id.searchButton);
        addButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
            	Intent callbackIntent = new Intent();
				// Return the search string to the caller.
				callbackIntent.putExtra("searchString", ((EditText)findViewById(R.id.searchText)).getText().toString());
				setResult(RESULT_OK, callbackIntent);
            	finish();
            } });

    }
}
