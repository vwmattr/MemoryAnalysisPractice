package com.mattrein.memoryanalysispractice.activities;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.mattrein.memoryanalysispractice.R;

import java.util.concurrent.TimeUnit;

import butterknife.Unbinder;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;

/**
 * This Activity registers as a listener with the LocationManager but never unregisters, and so
 * causes a memory leak of this Activity.
 *
 * Most of this code was borrowed from :
 * https://medium.com/freenet-engineering/memory-leaks-in-android-identify-treat-and-avoid-d0b1233acc8
 */
public class ListenerLeakActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listener_leak);
        Timber.d("Starting ListenerLeakActivity!");
        unbinder = bind(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Here we're registering this activity as a listener for location updates... but we never
        //unregister it at the end of the Activity's lifecycle... so it introduces a leak of this
        //Activity instance which can be observed with Memory Analysis Tools
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                TimeUnit.MINUTES.toMillis(5), 100, this);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        //Uncommenting this will remove the memory leak.
//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//        }

        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location Changed: " + location.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Status Changed: " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Provider Enabled: " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Provider Disabled: " + provider, Toast.LENGTH_SHORT).show();
    }
}
