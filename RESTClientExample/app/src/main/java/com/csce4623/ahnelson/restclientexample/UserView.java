package com.csce4623.ahnelson.restclientexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class UserView extends AppCompatActivity implements OnMapReadyCallback {

    User user;
    private MapView mMapView;
    private LatLng userLocation;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        user = (User) this.getIntent().getSerializableExtra("postUser");

        TextView tvName = (TextView)findViewById(R.id.tvUserrawname);
        TextView tvUsername = (TextView)findViewById(R.id.tvUsername);
        TextView tvUseremail = (TextView)findViewById(R.id.tvUseremail);
        TextView tvUserphone = (TextView)findViewById(R.id.tvUserphone);
        TextView tvUserwebsite = (TextView)findViewById(R.id.tvUserwebsite);

        tvName.setText(user.getName());
        tvUsername.setText(user.getUsername());
        tvUseremail.setText(user.getEmail());
        tvUserphone.setText(user.getPhone());
        tvUserwebsite.setText(user.getWebsite());

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        Address address = user.getAddress();
        Geo geo = address.getGeo();
        userLocation = new LatLng(geo.getLat(),geo.getLng());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 3));  //move camera to location

        if (mMap != null) {
            Marker hamburg = mMap.addMarker(new MarkerOptions().position(userLocation));
        }
    }
}