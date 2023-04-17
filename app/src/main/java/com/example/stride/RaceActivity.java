package com.example.stride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;

    private Button btnDirection;
    private Polyline track;

    private Button btnClear;
    private List<LatLng> points = new ArrayList<>();

    private PolylineOptions poly = new PolylineOptions();

    private String serializedLastRoute = "";
    Address origin = null;

    private SearchView fromSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        fromSearchView = findViewById(R.id.mapSearch);

        btnClear = findViewById(R.id.btnClear);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);

        fromSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String location = fromSearchView.getQuery().toString();
                List<Address> addressList = null;

                if(location!=null){
                    Geocoder geocoder = new Geocoder(RaceActivity.this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                    if (addressList.size() != 0){
                        origin = addressList.get(0);
                        LatLng latLng = new LatLng(origin.getLatitude(), origin.getLongitude());
                        points.add(latLng);
                        //poly.color(Color.BLUE);
                        //poly.add(latLng);
                        //myMap.addPolyline(poly);
                        myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        if (points.size() > 1) {
                            try {
                                drawPolyline();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else {
                        Toast.makeText(RaceActivity.this, "Location not found", Toast.LENGTH_LONG).show();
                        fromSearchView.setQuery("", false);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        btnClear.setOnClickListener(view -> {
            myMap.clear();
            points.clear();
            poly.getPoints().clear();
        });

        /*toSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String location = toSearchView.getQuery().toString();
                List<Address> addressList = null;

                if(mark2!=null){
                    mark2.remove();
                }

                if(location!=null){
                    Geocoder geocoder = new Geocoder(RaceActivity.this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e){
                        e.printStackTrace();
                    }

                    if (addressList.size() != 0){
                        dest = addressList.get(0);
                        LatLng latLng = new LatLng(dest.getLatitude(), dest.getLongitude());
                        mark2 = myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                    else {
                        myMap.clear();
                        Toast.makeText(RaceActivity.this, "Location not found", Toast.LENGTH_LONG).show();
                        toSearchView.setQuery("", false);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/

        /*
        btnDirection.setOnClickListener(view -> {
            try {
                getDirection(origin, dest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

         */

        Button btnStartRun = findViewById(R.id.planToStartRunButton);
        btnStartRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RaceActivity.this, TrackRunActivity.class);
                i.putExtra("Planned", serializedLastRoute);
                startActivity(i);
            }
        });

        mapFragment.getMapAsync(RaceActivity.this);
    }

    private void drawPolyline() throws IOException, JSONException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        if (points.size() > 1) {
            String intermediatePoints = "";
            for (int i = 1; i < points.size() - 1; i++) {
                intermediatePoints += "via:" + points.get(i).latitude + "," + points.get(i).longitude;
                if (i != points.size() - 2) {
                    intermediatePoints += "|";
                }
            }

            String formattedRequest = String.format(
                    "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&waypoints=%s&key=%s",
                    points.get(1).latitude + "," + points.get(1).longitude,
                    points.get(points.size() - 1).latitude + "," + points.get(points.size() - 1).longitude,
                    intermediatePoints,
                    BuildConfig.MAPS_API_KEY);
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(formattedRequest)
                    .post(body)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String jsonResp = response.body().string();
                Log.e("LOLILOL", formattedRequest);
                Log.e("LOLILOL", jsonResp);
                JSONObject resp = new JSONObject(jsonResp);
                JSONArray routeObject = resp.getJSONArray("routes");
                JSONObject routes = routeObject.getJSONObject(0);
                JSONObject overviewPolylines = routes
                        .getJSONObject("overview_polyline");
                serializedLastRoute = overviewPolylines.getString("points");
                track = myMap.addPolyline(new PolylineOptions().clickable(false)
                        .color(getResources().getColor(R.color.stride)));
                for (LatLng l: PolyUtil.decode(serializedLastRoute)) {
                    List<LatLng> prevPoints = track.getPoints();
                    prevPoints.add(l);
                    track.setPoints(prevPoints);
                }
            }
        }
        else{
            myMap.clear();
            Toast.makeText(this, "Please select at least two points", Toast.LENGTH_SHORT).show();
        }
    }


    /*private void getDirection(Address from, Address to){
        try{
            Uri uri = Uri.parse("https://www.google.com/maps/dir/" + from.toString() + "/" + to.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            LatLng a = new LatLng(from.getLatitude(), from.getLongitude());
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(a,10));
            startActivity(intent);
        } catch (ActivityNotFoundException exception){
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }*/

    /*private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentlocation = location;
                }
            }
        });
    }*/

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        /*LatLng limerick = new LatLng(52.673855, -8.574333);
        myMap.addMarker(new MarkerOptions().position(limerick).title("Limerick"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(limerick));*/

        myMap.getUiSettings().setCompassEnabled(true);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setRotateGesturesEnabled(true);
    }
}
