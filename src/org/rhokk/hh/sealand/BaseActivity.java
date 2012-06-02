package org.rhokk.hh.sealand;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.rhokk.hh.sealand.helper.GlobalBackgroundExecutor;
import org.rhokk.hh.sealand.http.AsyncCallback;
import org.rhokk.hh.sealand.http.HttpHelper;
import org.rhokk.hh.sealand.http.ServerResponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class BaseActivity extends Activity {

	public static final String LOG_ID = "Sealand";

	protected void asyncSendWithProgress( final JSONObject payload,
			final String progressMessage,
			final AsyncCallback callback ) {
		final ProgressDialog progress = showProgress( progressMessage );

		GlobalBackgroundExecutor.execute( 
				new Runnable() {

					public void run() {

						final String url = "http://10.0.2.2:3000/api/materials";



						try {

							final ServerResponse response = HttpHelper.getServerResponse( payload, url, "Sealand" );


							if ( response.isSuccess() ) {
								toastInUiThread( mapMessage( response.getResponseCode() ) );
							} else {
								showErrorInUiThread( mapMessage( response.getResponseCode() )  );
							}

							runOnUiThread( new Runnable() {

								@Override
								public void run() {
									callback.onResponse( response );
								}
							} );

						} catch (ClientProtocolException e) {
							showErrorInUiThread( e );
							e.printStackTrace();
						} catch (IOException e) {
							showErrorInUiThread( e );
							e.printStackTrace();
						} finally {
							hideProgress( progress );
						}
					}

					private String mapMessage(int responseCode) {
						int responseId;

						switch ( responseCode ) {
						case 201: responseId = R.string.created; break;
						case 422: responseId = R.string.unprocessable; break;
						case 412: responseId = R.string.precondition_failed; break; 
						case 401: responseId = R.string.authentication_error; break;
						default: responseId = R.string.unknown_error; break;
						}

						return getString( responseId );
					}

				} );
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



}
