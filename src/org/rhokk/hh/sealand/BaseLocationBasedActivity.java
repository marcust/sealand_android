package org.rhokk.hh.sealand;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public abstract class BaseLocationBasedActivity extends BaseActivity {

    private static final int TWENTY_MINUTES_IN_MILLIS = 20 * 60 * 1000;
	public static final long LOCATION_TIMEOUT_MS = 10 * 60 * 1000;

    private final class LocationListenerImpl implements LocationListener {

        public void onLocationChanged(Location location) {
            if ( location != null ) {
                if ( getLastLocation() == null ) {
                    applyLocation( location );
                    
                } else {
                    if ( location.getAccuracy() < getLastLocation().getAccuracy() ) {
                        applyLocation( location );
                    } else if ( getLastLocation().getTime() + LOCATION_TIMEOUT_MS < location.getTime() ) {
                        applyLocation( location );
                    }
                }
            }
        }

        private void applyLocation( Location location ) {
            setLastLocation( location ); 
            onNewLocation( location );
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }
    

    private LocationManager _locationManager;
    private LocationListener _gpsLocationListener;
    private LocationListenerImpl _networkLocationListener;
    

    private Location _lastLocation;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        
        _locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        _gpsLocationListener = new LocationListenerImpl();
        _networkLocationListener = new LocationListenerImpl();
    }

    protected void useLastKnownLocation( final LocationManager manager ) {
        Location lastKnownLocation = manager.getLastKnownLocation( LocationManager.GPS_PROVIDER );

        if ( lastKnownLocation == null ) {
            lastKnownLocation = manager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );
        }

        if ( lastKnownLocation != null && lastKnownLocation.getTime() > System.currentTimeMillis() - TWENTY_MINUTES_IN_MILLIS ) {
            setLastLocation( lastKnownLocation );
            onNewLocation( lastKnownLocation );
        }
    }

    protected abstract void onNewLocation( Location lastKnownLocation );

    @Override
    protected void onResume() {
        super.onResume();
        if ( _lastLocation != null ) {
            if ( _lastLocation.getTime() > System.currentTimeMillis() - ( 1000 * 60 * 10 ) ) {
                _lastLocation = null;
            }
        }
        
        
        useLastKnownLocation( _locationManager );

        enableListeners();

    }

    protected void enableListeners() {
        _locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 15000, 5, _gpsLocationListener );
        _locationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 15000, 5, _networkLocationListener );
    }

    @Override
    protected void onPause() {
        super.onPause();
        

        disableListeners();
    
    }

    protected void disableListeners() {
        _locationManager.removeUpdates( _gpsLocationListener );
        _locationManager.removeUpdates( _networkLocationListener );
    }

    public void setLastLocation( Location lastLocation ) {
        _lastLocation = lastLocation;
    }

    public Location getLastLocation() {
        return _lastLocation;
    }

    
    
}

