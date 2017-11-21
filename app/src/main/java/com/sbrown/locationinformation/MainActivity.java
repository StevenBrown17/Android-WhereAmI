package com.sbrown.locationinformation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView txtLatitude, txtLongitude, txtAccuracy, txtAltitude, txtAddress;

    LocationManager locationManager;
    LocationListener locationListener;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLatitude = (TextView)findViewById(R.id.txtLatitude);
        txtLongitude = (TextView)findViewById(R.id.txtLongitude);
        txtAccuracy = (TextView)findViewById(R.id.txtAccuracy);
        txtAltitude = (TextView)findViewById(R.id.txtAltitude);
        txtAddress = (TextView)findViewById(R.id.txtAddress);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                txtLatitude.setText("Latitude: " + location.getLatitude()+"");
                txtLongitude.setText("Longitude: " + location.getLongitude()+"");
                txtAccuracy.setText("Accuracy: " + location.getAccuracy()+"");
                txtAltitude.setText("Altitude: " + Math.round(location.getAltitude()/0.3048) +"'");

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    String address="";

                    if(listAddresses != null && listAddresses.size() > 0){

                        address+=listAddresses.get(0).getAddressLine(0);// + ", "+ listAddresses.get(0).getLocality() +", "+ listAddresses.get(0).getAdminArea();
                        Log.d("test", address);

                        txtAddress.setText("Address:\n" + listAddresses.get(0).getAddressLine(0));
                    }else{
                        txtAccuracy.setText("Address:\nNO ADDRESS AVAILABLE");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ask for permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }



    }//end onCreate




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
        }
    }//end onRequestPermissionsResult


}
