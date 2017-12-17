package astump.aslocationservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;

/**
 * Created by astump on 11/13/16.
 */

public class LocationService extends Service {

    final public static File locLogFile = new File(SNMPShit.snmpOutPath.toString(), "asls.log");

    public static final String BROADCAST_ACTION = "ASLS Poll";
    private static final int POLLINT = 1000 * 60 * 1;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    long count = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);
        Toast.makeText(getApplicationContext(), "ASLS Location Service Started!", Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > POLLINT;
        boolean isSignificantlyOlder = timeDelta < -POLLINT;
        boolean isNewer = timeDelta > 0;
        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            if (isBetterLocation(loc, previousBestLocation)) {
                count++;
                long locTime = loc.getTime();
                double locLat = loc.getLatitude();
                double locLon = loc.getLongitude();
                String locSrc = loc.getProvider();
                double locAlti = loc.getAltitude();
                double locSpeed = loc.getSpeed();
                float locBearing = loc.getBearing();

                String locationUpdateString = "Location Update: " + count
                        + ", Time: " + locTime
                        + ", Latitude: " + locLat
                        + ", Longitude: " + locLon
                        + ", Source: " + locSrc
                        + ", Altitude: " + locAlti
                        + ", Speed: " + locSpeed
                        + ", Bearing: " + locBearing
                        + ", EndLine\n";
                SharedMethods.writeOutputToFile(locLogFile, locationUpdateString, "Location Logfile");
                intent.putExtra("Latitude", locLat);
                intent.putExtra("Longitude", locLon);
                intent.putExtra("Provider", locSrc);
                sendBroadcast(intent);
                /* Toast.makeText(getApplicationContext(), "ASLS Update: " + locationUpdate, Toast.LENGTH_LONG).show(); */
                LatLng curLatLon = new LatLng(loc.getLatitude(), loc.getLongitude());
                Log.i("LocationService.LocationListener", "Location " + locationUpdateString);
            }
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS Disabled", Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int Status, Bundle extras) {
        }
    }

}
