package com.tringuyen.anzi.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tringuyen.anzi.Constants;
import com.tringuyen.anzi.R;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{
    private LatLng mInitialLocation;
    private GoogleMap mMap;
    private Toolbar mToolbar;
    private int mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //get the initial location from intent
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra(Constants.INITIAL_LAT_LOCATION,0.0);
        double lng = intent.getDoubleExtra(Constants.INITIAL_LNG_LOCATION,0.0);
        mInitialLocation = new LatLng(lat,lng);
        intializedView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }
        // Add a marker and move the camera
        if(mInitialLocation != null)
        {
            mMap.addMarker(new MarkerOptions().position(mInitialLocation).title("Location search")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mInitialLocation));
        }
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void intializedView()
    {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(Constants.TOOLBAR_TITLE);
        setSupportActionBar(mToolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
}
