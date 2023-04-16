package com.example.stride;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TrackRunActivity extends AppCompatActivity implements OnMapReadyCallback {

    private enum RunState {
        NOT_STARTED,
        ONGOING,
        PAUSED,
        ENDED
    }

    private enum MetricsName {
        HUNDREDTH_SECS, // In hundredth of seconds
        DISTANCE, // In meters
        HEIGHT, // In meters
        PACE, // In hundredth of seconds per meters
        CALORIES
    }

    private GoogleMap mMap;
    private MapView mapView;
    private Polyline myTrack;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastPosition = null;
    private RunState runState = RunState.NOT_STARTED;
    private HashMap<MetricsName, Long> metrics;

    /**
     * Database variables
     */
    public FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance("https://stride-99148-default-rtdb.europe-west1.firebasedatabase.app");


        // Init metrics
        metrics = new HashMap<>();
        for (MetricsName m : MetricsName.values())
            metrics.put(m, (long) 0);

        setContentView(R.layout.activity_track_run);

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
                if (runState == RunState.ENDED) {
                    Intent i = new Intent(TrackRunActivity.this, MainScreenActivity.class);
                    startActivity(i);
                }
                else
                    stopRun();
            }
        });

        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRun();
                Intent i = new Intent(TrackRunActivity.this, MainScreenActivity.class);
                startActivity(i);
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

        /**
         * Create and start the stop watch
         */
        // Get the text view.
        TextView timeView
                = (TextView)findViewById(
                R.id.stopWatchText);

        // Creates a new Handler
        Handler stopWatchHandler
                = new Handler();

        // Call the post() method,
        // passing in a new Runnable.
        // The post() method processes
        // code without a delay,
        // so the code in the Runnable
        // will run almost immediately.
        stopWatchHandler.post(new Runnable() {
            @Override

            public void run()
            {
                long hundredthSecs = metrics.get(MetricsName.HUNDREDTH_SECS) % 100;
                long secs = metrics.get(MetricsName.HUNDREDTH_SECS) / 100;
                long minutes = secs / 60;
                secs %= 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                String time = String.format(Locale.getDefault(),
                                     "%02d:%02d:%02d",
                                            minutes,
                                            secs,
                                            hundredthSecs);

                // Set the text view text.
                timeView.setText(time);

                // If running is true, increment the
                // seconds variable.
                if (runState == RunState.ONGOING) {
                    metrics.replace(MetricsName.HUNDREDTH_SECS, metrics.get(MetricsName.HUNDREDTH_SECS) + 1);
                }

                // Post the code again
                // with a delay of 0.01 second.
                stopWatchHandler.postDelayed(this, 10);
            }
        });

        /**
         * Start monitoring and updating the other metrics
         */
        Handler metricsHandler = new Handler();
        metricsHandler.post(new Runnable() {
            @Override
            public void run() {
                updateMetrics();
                // Update metrics every second
                metricsHandler.postDelayed(this, 1000);
            }
        });

        runState = RunState.ONGOING;

        findViewById(R.id.stopRunButton).setEnabled(true);
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
        ((Button) findViewById(R.id.runStateButton)).setEnabled(false);
        ((Button) findViewById(R.id.stopRunButton)).setText(R.string.back_to_main_menu_button);

        reference = database.getReference().child("Users").child(user.getUid());
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User us = dataSnapshot.getValue(User.class);
                LocalDateTime test = LocalDateTime.now();
                Run cur_run = new Run(test.toString());
                cur_run.setCalories(metrics.get(MetricsName.CALORIES));
                cur_run.setDistance(metrics.get(MetricsName.DISTANCE));
                cur_run.setHeight(metrics.get(MetricsName.HEIGHT));
                cur_run.setHundredthSecs(metrics.get(MetricsName.HUNDREDTH_SECS));
                cur_run.setPace(metrics.get(MetricsName.PACE));
                us.AddRun(cur_run);
                reference.setValue(us);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        reference.addListenerForSingleValueEvent(postListener);
    }

    /**
     * @return the length of the track in meters
     */
    private long computePolylineDistance() {
        double total_distance = 0;
        List<LatLng> points = myTrack.getPoints();
        System.out.println("\n\n" + points.size() + "\n\n\n");
        if (points.size() == 0)
            return 0;

        LatLng prev_point = points.get(0);
        LatLng cur_point = null;
        float[] result = new float[10];
        for (int i = 1; i < points.size(); i++) {
            cur_point = points.get(i);
            Location.distanceBetween(prev_point.latitude, prev_point.longitude, cur_point.latitude, cur_point.longitude, result);
            total_distance += result[0];
            prev_point = cur_point;
        }

        return (long) total_distance;
    }

    /**
     * This function will update the metrics and display their new values
     */
    private void updateMetrics() {
        // Update of the distance
        metrics.replace(MetricsName.DISTANCE, computePolylineDistance());
        float km = metrics.get(MetricsName.DISTANCE) / 1000f;

        String distance = String.format("%.2f km", km);

        ((TextView) findViewById(R.id.distanceText)).setText(distance);

        // Update of the height
        String text = String.format("%d m", metrics.get(MetricsName.HEIGHT));

        ((TextView) findViewById(R.id.heightText)).setText(text);


        // Update of the pace
        if (metrics.get(MetricsName.DISTANCE) != 0) {
            metrics.replace(MetricsName.PACE, (long) (metrics.get(MetricsName.HUNDREDTH_SECS) / (metrics.get(MetricsName.DISTANCE) / 1000f)));
        }

        long hundredthSecs = metrics.get(MetricsName.PACE) % 100;
        long secs = metrics.get(MetricsName.PACE) / 100;
        long minutes = secs / 60;
        secs %= 60;

        // Format the seconds into hours, minutes,
        // and seconds.
        String time = String.format(Locale.getDefault(),
                "%02d\'%02d\"%02d",
                minutes,
                secs,
                hundredthSecs);

        // Set the text view text
        ((TextView) findViewById(R.id.paceText)).setText(time);

        // Update of the calories consumption
        // Formulas found source tkt
        double calPerStep = 0.03753; // Assuming that your weight is 70kg and your height is 170 cm and your speed is average
        double sizeOfStep = 0.74; // In meter
        long numberOfSteps = (long) (sizeOfStep * metrics.get(MetricsName.DISTANCE));
        metrics.replace(MetricsName.CALORIES, (long) (numberOfSteps * calPerStep));
        String cal = String.format("%d kcal", metrics.get(MetricsName.CALORIES));

        ((TextView) findViewById(R.id.caloriesText)).setText(cal);
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

        mMap.setMinZoomPreference(5f);
        mMap.setMaxZoomPreference(40f);

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
                LatLng lastLatLng = null;
                if (lastPosition != null)
                    lastLatLng = new LatLng(lastPosition.getLatitude(), lastPosition.getLongitude());
                if (latLng != lastLatLng) {
                    updateCameraPosition(latLng);
                    drawTrack(latLng);
                    lastPosition = location;
                    if (lastPosition.getAltitude() < location.getAltitude())
                        metrics.replace(MetricsName.HEIGHT, metrics.get(MetricsName.HEIGHT) + (long) (location.getAltitude() - lastPosition.getAltitude()));
                }
            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    @SuppressLint("MissingPermission")
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
