package com.example.stride;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;


import java.util.HashMap;
import java.util.List;

public class TrackRunActivity extends AppCompatActivity implements OnMapReadyCallback {

    private enum RunState {
        NOT_STARTED,
        ONGOING,
        PAUSED,
        ENDED
    }

    private enum MetricsName {
        TIME,
        DISTANCE
    }

    private GoogleMap mMap;
    private MapView mapView;
    private Polyline myTrack;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng lastPosition = null;
    private RunState runState = RunState.NOT_STARTED;

    private HashMap<MetricsName, Float> metrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init metrics
        metrics = new HashMap<>();
        for (MetricsName m : MetricsName.values())
            metrics.put(m, (float) 0);

        setContentView(R.layout.activity_choose_route);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Button stateButton = findViewById(R.id.runStateButton);
        stateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (runState) {
                    case NOT_STARTED:
                        stateButton.setText(R.string.pause_run_button);
                        startRun();
                        break;

                    case ONGOING:
                        stateButton.setText(R.string.resume_run_button);
                        pauseRun();
                        break;

                    case PAUSED:
                        stateButton.setText(R.string.pause_run_button);
                        resumeRun();
                        break;
                }
            }
        });

        Button stopButton = findViewById(R.id.stopRunButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRun();
            }
        });
    }

    /**
     * This function is called at the beginning of the run
     */
    private void startRun() {
        myTrack = mMap.addPolyline(new PolylineOptions().clickable(false));
        myTrack.setJointType(JointType.ROUND);
        myTrack.setColor(Color.BLUE);
        myTrack.setStartCap(new RoundCap());
        myTrack.setEndCap(new ButtCap());

        // Activate the stop button
        findViewById(R.id.stopRunButton).setActivated(true);

        runState = RunState.ONGOING;
    }

    /**
     * This function is called when the run is paused
     */
    private void pauseRun() {
        runState = RunState.PAUSED;
    }

    /**
     * This function is called when the run is resumed
     */
    private void resumeRun() {
        runState = RunState.ONGOING;
    }

    /**
     * This function is called when the run is stopped
     */
    private void stopRun() {
        runState = RunState.ENDED;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMinZoomPreference(1f);
        mMap.setMaxZoomPreference(20f);
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        //getDeviceLocation();
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (latLng != lastPosition) {
                    updateCameraPosition(latLng);
                    drawTrack(latLng);
                    lastPosition = latLng;
                }
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);

                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void drawTrack(LatLng latLng) {
        if (runState == RunState.ONGOING) {
            List<LatLng> prevPoints = myTrack.getPoints();
            prevPoints.add(latLng);
            myTrack.setPoints(prevPoints);
        }
    }

    private void updateCameraPosition(LatLng latLng) {
        if (mMap == null)
            return;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMap.animateCamera(cameraUpdate);
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }




    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
