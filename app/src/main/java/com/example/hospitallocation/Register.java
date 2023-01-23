package com.example.hospitallocation;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Register extends AppCompatActivity {

    EditText e1, e2, e3;
    FirebaseAuth mAuth;
    Button button;
    Double lat, lon;
    String addr, line;

    DatabaseReference databaseUsers;

    private LocationCallback locationCallback;
    private FusedLocationProviderClient client;
    Geocoder geocoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        e1 = (EditText)findViewById(R.id.editText);
        e2 = (EditText)findViewById(R.id.editText2);
        e3 = (EditText)findViewById(R.id.editText3);

        mAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(this);

        databaseUsers = FirebaseDatabase.getInstance().getReference("Users");

        button = (Button) findViewById(R.id.button3);

        client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
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
                    lat = location.getLatitude();
                    lon = location.getLongitude();



                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

    }

    public void createUser(){
        if(e1.getText().toString().equals("") && e2.getText().toString().equals("") &&e3.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Blank not allowed", Toast.LENGTH_SHORT).show();
        }else{
            geocoder = new Geocoder(getApplicationContext());

            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocation(lat, lon,1);

                Address address = addressList.get(0);

                String line = address.getAddressLine(0);

                addr = line;

            } catch (IOException e) {
                e.printStackTrace();
            }

            String email = e1.getText().toString();
            String password = e2.getText().toString();
            String name = e3.getText().toString();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                SimpleDateFormat sdf = new SimpleDateFormat("'Date\n'dd-MM-yyyy '\n\nand\n\nTime\n'HH:mm:ss z");
                                String currentTime = sdf.format(new Date());
                                User user = new User(name , lat, lon , addr,currentTime);
                                databaseUsers.child(firebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getApplicationContext(),"Welcome " + name,Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                                startActivity(i);
                            }else{
                                Toast.makeText(getApplicationContext(),"User could not be found",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}