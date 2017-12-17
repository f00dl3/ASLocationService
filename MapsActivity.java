package astump.aslocationservice;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    final File asToolsBaseFolder = SharedMethods.sharedPath;

    int minTimeBetweenUpdates = 2 * 1000;
    int minDistanceBetweenUpdates = 25;
    int defaultZoomLevel = 17;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SharedMethods.makeDir(asToolsBaseFolder);
        startService(new Intent(this, LocationService.class));
        startService(new Intent(this, DeviceSensorServices.class));
        startService(new Intent(this, SNMPShit.class));
        startService(new Intent(this, CapPhoto.class));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeBetweenUpdates, minDistanceBetweenUpdates, locationListener);

        mMap = googleMap;
        LatLng home = new LatLng(/* Put your lat lon here!*/);
        mMap.addMarker(new MarkerOptions().position(home).title("Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(home));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(defaultZoomLevel));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                String latLonStr = "LAT: " + point.latitude + "\nLON: " + point.longitude + "\n";
                Geocoder geoCoder = new Geocoder(
                        getBaseContext(),
                        Locale.getDefault()
                );
                try {
                    List<Address> addresses = geoCoder.getFromLocation(
                            point.latitude,
                            point.longitude,
                            1
                    );
                    String add = "";
                    if (addresses.size() > 0) {
                        for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                            add += addresses.get(0).getAddressLine(i) + "\n";
                        }
                        String combAddGeoStr = latLonStr + "\n" + add;
                        Toast.makeText(getBaseContext(), combAddGeoStr, Toast.LENGTH_LONG).show();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        });

    }

    private class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location loc) {
            if(loc != null) {
                long locTime = loc.getTime();
                LatLng curLatLon = new LatLng(loc.getLatitude(), loc.getLongitude());
                String currentLocationString = "Current Location:\nLAT: " + loc.getLatitude() + "\nLON: " + loc.getLongitude();
                Log.i("via MapsActivity", "Location changed: " + currentLocationString);
                /* Toast.makeText(getBaseContext(), currentLocationString, Toast.LENGTH_SHORT).show(); */
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curLatLon));
                mMap.addMarker(new MarkerOptions().position(curLatLon).title("Location"));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(defaultZoomLevel));

            } else {
                /* Toast.makeText(getBaseContext(), "No current location!", Toast.LENGTH_SHORT).show(); */
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}

    }

}
