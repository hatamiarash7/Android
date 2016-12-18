package ir.hatamiarash.zimia;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends AppCompatActivity implements OnMapReadyCallback {

    final static int TAG_CODE_PERMISSION_LOCATION = 1337;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        CameraPosition googlePlex = CameraPosition.builder()
                .target(new LatLng(34.301496, 48.8120835))
                .zoom(14)
                .bearing(0)
                .tilt(0)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    TAG_CODE_PERMISSION_LOCATION);
        }

        /*
        map.addMarker(new MarkerOptions()
                .position(new LatLng(37.4219999, -122.0862462))
                .title("Google Plex")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

        map.addMarker(new MarkerOptions()
                .position(new LatLng(37.4629101,-122.2449094))
                .title("Facebook")
                .snippet("Facebook HQ: Menlo Park"));
                */
        map.addMarker(new MarkerOptions()
                .position(new LatLng(34.299635, 48.820467))
                .title("ترکمان")
                .snippet("رستوران / پخت غذا")).showInfoWindow();

    }


}