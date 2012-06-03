package org.rhokk.hh.sealand;

import org.json.JSONObject;
import org.rhokk.hh.sealand.service.UploadManagementService;
import org.rhokk.hh.sealand.service.UploadManagementServiceImpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

public class BaseActivity extends Activity {

	public static final String LOG_ID = "Sealand";

	private UploadManagementService mService;

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className,
                IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = UploadManagementService.Stub.asInterface(service);
        }

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mService = null;
			
		}
    };
    
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		ComponentName startService = startService(new Intent(this,UploadManagementServiceImpl.class));
//		
//		if ( startService == null ) {
//			showError("Kann service nicht starten");
//			}
		
		boolean bindService = getApplicationContext().bindService(new Intent(this,UploadManagementServiceImpl.class),
				mConnection, Context.BIND_AUTO_CREATE);
		
		if ( !bindService ) {
			showError("Kann Service nicht binden");
		}
		
	}

	protected void asyncSendWithProgress( final JSONObject payload ) {

		try {
			mService.scheduleUpload( payload.toString() );
		} catch (RemoteException e) {
			throw new RuntimeException( e );
		}

	}
	
	public BaseActivity() {

	}


	protected ProgressDialog showProgress( final String message ) {
		final ProgressDialog progress = ProgressDialog.show(this, "", 
				message , false );
		return progress;
	}

	protected void hideProgress( final ProgressDialog progress ) {
		runOnUiThread( new Runnable() {

			public void run() {
				if ( progress.isShowing() ) {
					try {
						progress.dismiss();
					} catch ( IllegalArgumentException e ) {
						// do nothing, sometimes thit causes a "View not attached to window manager" exception
					}
				}

			}} );
	}


	protected void toastInUiThread(final String string) {
		runOnUiThread(new Runnable() {

			public void run() {
				toast(string);
			}
		});
	}

	protected void toastInUiThread(final Exception e) {
		toastInUiThread("Error: " + e.getMessage() );
	}


	protected void toast(CharSequence text) {
		final Context context = getApplicationContext();
		final int duration = Toast.LENGTH_SHORT;

		final Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	protected void showErrorInUiThread( final Exception e ) {
		showErrorInUiThread( "Error: " + e.getMessage() );
	}

	protected void showErrorInUiThread( final String message ) {
		runOnUiThread( new Runnable() {

			@Override
			public void run() {
				showError( message );
			}
		} );

	}

	protected void showError( final String message ) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage( message )
		.setCancelable(false)
		.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

    protected static boolean isRunningInEmulator() {
        return "google_sdk".equals( Build.PRODUCT );
    }

}
