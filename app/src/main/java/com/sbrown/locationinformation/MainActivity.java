package com.sbrown.locationinformation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This application uses your location to display the exact location you are at. Clicking on the info will open a map.
 */
public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView txtLatitude, txtLongitude, txtAccuracy, txtAltitude, txtAddress;

    LocationManager locationManager;
    LocationListener locationListener;
    double lat=0.0, lon=0.0;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        txtLatitude = (TextView)findViewById(R.id.txtLatitude);
        txtLongitude = (TextView)findViewById(R.id.txtLongitude);
        txtAccuracy = (TextView)findViewById(R.id.txtAccuracy);
        txtAltitude = (TextView)findViewById(R.id.txtAltitude);
        txtAddress = (TextView)findViewById(R.id.txtAddress);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat=location.getLatitude();//update global variables. Need to do this for onClick()
                lon=location.getLongitude();

                txtLatitude.setText("Latitude: " +lat);
                txtLongitude.setText("Longitude: " + lon);
                txtAccuracy.setText("Accuracy: " + location.getAccuracy()+"");
                txtAltitude.setText("Altitude: " + Math.round(location.getAltitude()/0.3048) +"'");

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());//used to get current Address
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

            }//end onLocationChanged

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //unused
            }

            @Override
            public void onProviderEnabled(String provider) {
                //unused
            }

            @Override
            public void onProviderDisabled(String provider) {
                //unused
            }

        };//end Location Listener

        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //ask for permissions
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }//end permissions block

    }//end onCreate


    /**
     * A empty TextView is overlaid the information. When this textview is clicked, this method will run.
     * The latitude and longitude are recorded in a global variable. These are passed into a Uri that will be used
     * to open a webpage showing the location of the given latitude and longitude
     * @param view
     */
    public void onClick(View view){

        Uri uri = Uri.parse("https://www.google.com/maps/search/"+lat+",+-"+lon);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }//end onClick


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }//end onRequestPermissionsResult

    /**
     * prevents redundant code.
     */
    public void startListening(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,0, locationListener);
    }//end startListening()




}//end MainActivity
