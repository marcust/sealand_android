package org.rhokk.hh.sealand;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Sealand extends BaseLocationBasedActivity {
    private Location _lastKnownLocation;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button sendButton = findSendButton();
        sendButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					doAsyncDataSend( getName(), getDescription(), _lastKnownLocation );
			}

		} );
    }
    

	protected void doAsyncDataSend( final String name, final String description,
			final Location lastKnownLocation ) {
		//  {
		//   'locations' => [{
		//     'langitude' => 53.3151,
		//     'longitude' => 8.2351,
		//     'accuracy' => 12.4,
		//     'description' => ''
		//   }],
		//   'material' => {
		//     'name' => 'Material name'
		//     'description' => 'optional description'
		//   }
		// }
		
		
		final JSONObject request = new JSONObject(); 
		final JSONArray locations = new JSONArray();
		locations.put( makeJSONLocationObject( lastKnownLocation ) );
		
		safePut( request, "locations", locations );
		safePut( request, "material", makeJSONMaterialObject( name, description ) );
		
		asyncSendWithProgress( request );
		clearInputFields();
	}


	private void safePut(JSONObject request, String key,
			JSONObject object ) {
		try {
			request.put( key, object );
		} catch (JSONException e) {
			throw new RuntimeException("JSON Exception: " + e.getMessage() , e );
		}
	}
	
	private void safePut(JSONObject request, String key,
			String value ) {
		try {
			request.put( key, value );
		} catch (JSONException e) {
			throw new RuntimeException("JSON Exception: " + e.getMessage() , e );
		}
	}
	
	private void safePut(JSONObject request, String key,
			float value ) {
		try {
			request.put( key, value );
		} catch (JSONException e) {
			throw new RuntimeException("JSON Exception: " + e.getMessage() , e );
		}
	}

	private void safePut(JSONObject request, String key,
			double value ) {
		try {
			request.put( key, value );
		} catch (JSONException e) {
			throw new RuntimeException("JSON Exception: " + e.getMessage() , e );
		}
	}

	protected void clearInputFields() {
		clearTextField( R.id.descriptionInput );
		clearTextField( R.id.nameInput );
	}

	private void clearTextField(int id) {
		final EditText textField = (EditText) findViewById( id );
		textField.setText("");
	}


	private JSONObject makeJSONMaterialObject(String name, String description) {
		final JSONObject materialObject = new JSONObject();
		
		safePut( materialObject, "name", name );
		safePut( materialObject, "description", description );
		
		return materialObject;
		
	}


	private JSONObject makeJSONLocationObject(Location location) {
		final JSONObject locationObject = new JSONObject();
		
		safePut( locationObject, "latitude", location != null ? location.getLatitude() : 0.0 );
		safePut( locationObject, "longitude", location != null ? location.getLongitude() : 0.0 );
		safePut( locationObject, "accuracy", location != null ? location.getAccuracy() : 0.0 );
		safePut( locationObject, "description", "" );

		return locationObject;
	}


	private String getName() {
		return getTextFieldContent( R.id.nameInput );
	}

	private String getDescription() {
		return getTextFieldContent( R.id.descriptionInput );
	}
	
	private String getTextFieldContent( final int id ) {
		final EditText textField = (EditText) findViewById( id );
		final String name = textField.getText().toString();
		return name;
	}

	
	@Override
	protected void onNewLocation( final Location lastKnownLocation ) {
		 final Button sendButton = findSendButton();
		 sendButton.setEnabled( true );
		 sendButton.setText( R.string.send );
		
		 _lastKnownLocation = lastKnownLocation;
	}

	private Button findSendButton() {
		final Button sendButton = (Button) findViewById(R.id.sendButton);
		return sendButton;
	}
}
