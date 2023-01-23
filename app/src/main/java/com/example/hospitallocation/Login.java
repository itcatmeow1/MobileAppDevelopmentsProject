package com.example.hospitallocation;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Login extends AppCompatActivity {

    EditText e1, e2;
    FirebaseAuth mAuth;
    Button button;
    Double Lat, Lon;
    String name, addr;


    Geocoder geocoder;
    DatabaseReference databaseUsers;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        e1 = (EditText)findViewById(R.id.editText);
        e2 = (EditText)findViewById(R.id.editText2);

        mAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");

        button = (Button) findViewById(R.id.button3) ;

        client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Lat = location.getLatitude();
                Lon = location.getLongitude();
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null){
                    Toast.makeText(getApplicationContext(), "Location not detected", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Lat = location.getLatitude();
                    Lon = location.getLongitude();

                    Geocoder geocoder = new Geocoder(getApplicationContext());

                    List<Address> addressList = null;
                    try {
                        addressList = geocoder.getFromLocation(Lat, Lon, 1);

                        Address address = addressList.get(0);

                        String line = address.getAddressLine(0);
                        String area = address.getAdminArea();
                        String locality = address.getLocality();
                        String country = address.getCountryName();
                        String postcode = address.getPostalCode();

                        addr = (line + area + locality + postcode + country).toString();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

    }

   public void loginUser(){
        if(e1.getText().toString().equals("") && e2.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Blank not allowed", Toast.LENGTH_SHORT).show();

        }else{
            geocoder = new Geocoder(getApplicationContext());

            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(Lat, Lon,1);

                Address address = addressList.get(0);

                String line = address.getAddressLine(0);

                addr = line;

            } catch (IOException e) {
                e.printStackTrace();
            }

            mAuth.signInWithEmailAndPassword(e1.getText().toString(),e2.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                if (firebaseUser == null){
                                    Toast.makeText(getApplicationContext(), "Not available", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String uid = firebaseUser.getUid();

                                    databaseUsers.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            User readUser = snapshot.getValue(User.class);
                                            if (readUser != null){
                                                name = readUser.getUserName();

                                                SimpleDateFormat sdf = new SimpleDateFormat("'Date\n'dd-MM-yyyy '\n\nand\n\nTime\n'HH:mm:ss z");
                                                String currentTime = sdf.format(new Date());

                                                readUser = new User(name, Lat, Lon, addr, currentTime);
                                                databaseUsers.child(uid).setValue(readUser);

                                                Toast.makeText(getApplicationContext(), "Welcome " + readUser.getUserName(), Toast.LENGTH_SHORT).show();
                                                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                                                startActivity(i);

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                            }else{
                                Toast.makeText(getApplicationContext(),"User could not be login",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}