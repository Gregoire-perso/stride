package com.example.stride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.location.LocationProvider;
import android.os.Looper;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;

    private Button btnDirection;

    Marker mark = null;
    Marker mark2 = null;

    Address origin = null;
    Address dest = null;

    private SearchView fromSearchView;

    private SearchView toSearchView;

    Location currentlocation;
    //FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        fromSearchView = findViewById(R.id.mapSearch);

        toSearchView = findViewById(R.id.mapSearch2);

        btnDirection = findViewById(R.id.btnDirection);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);

        /*fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RaceActivity.this);
        getLastLocation();*/
        //direction();

        fromSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String location = fromSearchView.getQuery().toString();
                List<Address> addressList = null;

                if(mark!=null){
                    mark.remove();
                }

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
                        mark = myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
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

        toSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        });

        btnDirection.setOnClickListener(view -> {
                            getDirection(origin, dest);
        });

        mapFragment.getMapAsync(RaceActivity.this);
    }

    private void getDirection(Address from, Address to){
        if (from != null && to != null){
            LatLng a = new LatLng(from.getLatitude(), from.getLongitude());
            LatLng b = new LatLng(to.getLatitude(), to.getLongitude());
            myMap.addPolyline(new PolylineOptions().add(a, b)
                    .width(5)
                    .color(Color.BLUE)
                    .geodesic(true));
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(a, 10));
        }
        else{
            Toast.makeText(this, "Please select a start and a destination", Toast.LENGTH_SHORT).show();
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


        /*LatLng loc = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
        myMap.addMarker(new MarkerOptions().position(loc).title("Your position"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(loc));*/
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}