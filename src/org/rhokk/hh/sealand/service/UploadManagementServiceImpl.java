package org.rhokk.hh.sealand.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.rhokk.hh.sealand.R;
import org.rhokk.hh.sealand.helper.GlobalBackgroundExecutor;
import org.rhokk.hh.sealand.http.HttpHelper;
import org.rhokk.hh.sealand.http.ServerResponse;
import org.rhokk.hh.sealand.util.IOUtils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.RemoteException;

public class UploadManagementServiceImpl extends Service  {

	private static final long UPDATE_INTERVAL_MILLIS = 1000 * 60 * 15;
	private final UploadManagementService.Stub _binder = new UploadManagementService.Stub() {

		@Override
		public void scheduleUpload(String payload) throws RemoteException {
			safePayload( payload );
			doUploadIfPossible();
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return _binder; 
	}

	protected void doUploadIfPossible() {
		if ( !isConnected() ) {
			return;
		}

		final File dir = getFilesDir();

		if ( lockDir() ) {
			try {
				final String[] fileList = fileList();

				for ( final String filename : fileList ) {
					if ( filename.startsWith("data_") ) {
						uploadFile( new File( dir, filename ) );
					}
				}
			} finally {
				unlockDir();
			}
		} 
	}

	private boolean isConnected() {
		ConnectivityManager cm =
				(ConnectivityManager)getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork.isConnected();
	}

	private void uploadFile( final File file) {
		try {
			final String content = IOUtils.toString( file );

			GlobalBackgroundExecutor.execute( 
					new Runnable() {

						public void run() {

							final String url = "http://10.0.2.2:3000/api/material";



							try {

								final ServerResponse response = HttpHelper.getServerResponse( content, url, "Sealand" );


								if ( response.isSuccess() ) {
									showSuccess( mapMessage( response.getResponseCode() ) );
									file.delete();
								} else {
									showError( mapMessage( response.getResponseCode() )  );
								}


							} catch (ClientProtocolException e) {
								showError( e );
								e.printStackTrace();
							} catch (IOException e) {
								showError( e );
								e.printStackTrace();
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



		} catch (IOException e) {
			throw new RuntimeException( e );
		}



	}

	protected void showError(Exception e) {
		showMessage(e.getMessage());
	}

	protected void showError(String message ) {
		showMessage( message );
	}

	protected void showSuccess(String message) {
		showMessage(message);
	}

	private void showMessage(String message) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = message;
		long when = System.currentTimeMillis();

		final Notification notification = new Notification(icon, tickerText, when);

		Context context = getApplicationContext();
		CharSequence contentTitle = "Sealand";
		CharSequence contentText = message;
		Intent notificationIntent = new Intent(this, UploadManagementServiceImpl.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		mNotificationManager.notify(1, notification);
	}

	private void unlockDir() {
		File file = new File( getFilesDir(), "lock_file" );
		if ( file.exists() ) {
			file.delete();
		}
	}

	private boolean lockDir() {
		try {
			FileInputStream openFileInput = openFileInput("lock_file");
			IOUtils.closeQuietly( openFileInput );
		} catch (FileNotFoundException e) {
			try {
				FileOutputStream openFileOutput = openFileOutput( "lock_file", Context.MODE_PRIVATE );
				openFileOutput.write( 1 );
				IOUtils.closeQuietly( openFileOutput );
				return true;
			} catch (FileNotFoundException e1) {
				throw new RuntimeException( e1 );
			} catch (IOException ex) {
				throw new RuntimeException( ex );
			}
		}

		return false;
	}

	protected void safePayload(String payload) {
		final long currentTimeMillis = System.currentTimeMillis();
		try {
			final FileOutputStream openFileOutput = openFileOutput( "data_" + currentTimeMillis + ".json", Context.MODE_PRIVATE );

			IOUtils.write( payload, openFileOutput );
			IOUtils.closeQuietly( openFileOutput );

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();

		final AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		final Intent intent = new Intent(getApplicationContext(), UploadManagementServiceImpl.class);
		intent.putExtra("update", true );
		PendingIntent pendingUpdateIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
		am.setRepeating( AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + UPDATE_INTERVAL_MILLIS,
				UPDATE_INTERVAL_MILLIS, pendingUpdateIntent);


	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		if ( intent.hasExtra("update") ) {
			doUploadIfPossible();
		}
	}
}
