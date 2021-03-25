package com.example.mapdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final int REQUEST_CODE = 0;
    private GoogleMap mMap;
    Button map, satellite, hybrid, terrain, location;
    RadioGroup options;
    Circle circle;
    String[] permissions;
    FusedLocationProviderClient currentLocation;
    Marker marker;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        map = findViewById(R.id.bMap);
        satellite = findViewById(R.id.bSatellite);
        hybrid = findViewById(R.id.bHybrid);
        terrain = findViewById(R.id.bTerrain);
        location = findViewById(R.id.bLocation);
        options = findViewById(R.id.radioGroup);

        permissions = new String[2];

        locationRequest = new LocationRequest();
        currentLocation = new FusedLocationProviderClient(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permission1 = ContextCompat.checkSelfPermission(MapsActivity.this, "android.permission.ACCESS_COARSE_LOCATION");
                int permission2 = ContextCompat.checkSelfPermission(MapsActivity.this, "android.permission.ACCESS_FINE_LOCATION");

                if (permission1 == PERMISSION_GRANTED && permission2 == PERMISSION_GRANTED) {
                    locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
                    currentLocation.requestLocationUpdates(locationRequest, locationCallback, null);
                } else {
                    permissions[0] = "android.permission.ACCESS_COARSE_LOCATION";
                    permissions[1] = "android.permission.ACCESS_FINE_LOCATION";

                    ActivityCompat.requestPermissions(MapsActivity.this, permissions, REQUEST_CODE);
                }
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    setMarker(location);
                }
            }
        };
    }

    private void setMarker(Location location) {
        currentLocation.removeLocationUpdates(locationCallback);
        marker.remove();
        LatLng currentPos = new LatLng(location.getLatitude(), location.getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(currentPos).title("Marker current position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos));
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == map.getId()) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (i == satellite.getId()) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (i == hybrid.getId()) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                } else if (i == terrain.getId()) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
            }
        });

        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    currentLocation.requestLocationUpdates(locationRequest, locationCallback, null);
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    break;
                }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (circle != null) {
            circle.remove();
        }
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(300000);

        circle = mMap.addCircle(circleOptions);
    }

}