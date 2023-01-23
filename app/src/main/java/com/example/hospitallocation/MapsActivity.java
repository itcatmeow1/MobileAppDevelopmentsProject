package com.example.hospitallocation;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.hospitallocation.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Vector;

public class MapsActivity extends FragmentActivity {

    MarkerOptions marker;
    LatLng latLng;

    Vector<MarkerOptions> markerOptions;

    FusedLocationProviderClient client;
    SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }
        else{
            ActivityCompat.requestPermissions(MapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        markerOptions = new Vector<>();

        markerOptions.add(new MarkerOptions().title("Shah Alam Hospital")
                .position(new LatLng(3.07153,101.48990))

        );

        markerOptions.add(new MarkerOptions().title("Shah Alam Health Clinic")
                .position(new LatLng(3.07240,101.49094))

        );

        markerOptions.add(new MarkerOptions().title("Pusat Kesihatan UiTM")
                .position(new LatLng(3.06847,101.49359))

        );

        markerOptions.add(new MarkerOptions().title("Klinik Kesihatan Padang Jawa")
               .position(new LatLng(3.05495,101.49056))

        );

        markerOptions.add(new MarkerOptions().title("Klinik Kesihatan Bukit Kuda")
                .position(new LatLng(3.05255,101.45868))

        );

        markerOptions.add(new MarkerOptions().title("Klinik Kesihatan UTC Selangor")
                .position(new LatLng(3.07295,101.51971))

        );

        markerOptions.add(new MarkerOptions().title("Klinik Nik Isahak Kompleks PKNS")
                .position(new LatLng(3.07141,101.51719))

        );

        markerOptions.add(new MarkerOptions().title("Poliklinik Komuniti Shah Alam")
                .position(new LatLng(3.07252,101.49114))

        );
    }

    private void getCurrentLocation(){
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            latLng = new LatLng(location.getLatitude(), location.getLongitude());

                            MarkerOptions options = new MarkerOptions().position(latLng).title("I am here");
                            for (MarkerOptions mark : markerOptions) {
                                googleMap.addMarker(mark);
                            }

                            googleMap.setMyLocationEnabled(true);

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
    };

}
