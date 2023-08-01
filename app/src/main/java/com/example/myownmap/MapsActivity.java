package com.example.myownmap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.myownmap.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private EditText mapSearchView ;
    String informationalLocation  ;
    Marker marker ;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION ;
    public static final int FINE_LOCATION_REQ_CODE = 100;
    public static final int COARSE_LOCATION_REQ_CODE = 200;
    GPSTracker liveLocation = new GPSTracker(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mapSearchView =findViewById(R.id.search_bar);
//        search_Btn = (Button) findViewById(R.id.search_btn);
//        liveLocationButton = findViewById(R.id.liveLocation_btn);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (ActivityCompat.checkSelfPermission(this,PERMISSION_ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(this,
                PERMISSION_ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_DENIED){
            requestRuntimePermission();

        }




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        String loc ;

        if (informationalLocation != null)
        {
            loc = informationalLocation ;
        }
        else
        {
            loc = "Kanpur";
        }


        showMap(googleMap,loc );




    }
    public void showMap(GoogleMap googleMap )
    {
        liveLocation.getLocation();
        if(liveLocation.canGetLocation) {
            marker.remove();
            mMap = googleMap;
            LatLng latLng = new LatLng(liveLocation.getLatitude(), liveLocation.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Current Location."));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
        else {
            Toast.makeText(this, "Live Location Not Fetched!!!", Toast.LENGTH_SHORT).show();
        }
    }
    public void showMap(GoogleMap googleMap, String location){
        String loc ;

        if (location == ""|| location == null)
        {
            loc = "Kanpur";

        }
        else
        {
            loc = location ;
        }
        mMap = googleMap;


        Geocoder geocoder =new Geocoder(MapsActivity.this);
        List<Address> address = null ;
        try {
            address = geocoder.getFromLocationName(loc,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng lat_lng = new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
         marker = mMap.addMarker(new MarkerOptions().position(lat_lng).title(loc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lat_lng,9));


    }
    public void Btn_tapped(View view){


        informationalLocation = mapSearchView.getText().toString();
        if (informationalLocation.isEmpty())
        {
            informationalLocation = "Lucknow";
        }
        marker.remove();
        Toast.makeText(this, informationalLocation, Toast.LENGTH_LONG).show();
        mapSearchView.setText("");
        showMap(mMap, informationalLocation);


    }
    public void liveLocationBtnOnClick(View view)
    {
        showMap(mMap);
    }
    private void requestRuntimePermission(){
        if (ActivityCompat.checkSelfPermission(this,PERMISSION_ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED||ActivityCompat.checkSelfPermission(this,
                PERMISSION_ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Location Permission Granted!", Toast.LENGTH_LONG).show();
        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this,PERMISSION_ACCESS_COARSE_LOCATION))
        {
            if( ActivityCompat.shouldShowRequestPermissionRationale(this,PERMISSION_ACCESS_FINE_LOCATION))

            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This app requires LOCATION_PERMISSION to show your current location.")
                        .setTitle("Permission Required")
                        .setCancelable(false)
                        .setPositiveButton("Ok", (dialog, which) -> {
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{PERMISSION_ACCESS_FINE_LOCATION, PERMISSION_ACCESS_COARSE_LOCATION}, FINE_LOCATION_REQ_CODE);
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
                builder.show();


            }
        }else
        {
            ActivityCompat.requestPermissions(this,new String[]{PERMISSION_ACCESS_FINE_LOCATION,PERMISSION_ACCESS_COARSE_LOCATION},FINE_LOCATION_REQ_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_REQ_CODE || requestCode == FINE_LOCATION_REQ_CODE)
        {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED||
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)){

                Toast.makeText(this, "Location Permission Granted!", Toast.LENGTH_LONG).show();
            }else if(!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    PERMISSION_ACCESS_FINE_LOCATION)) {
                if(!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        PERMISSION_ACCESS_COARSE_LOCATION)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This app requires location permission that you have" +
                                    " denied , Please allow these permission from settings to procced further..")
                            .setTitle("Permission Required")
                            .setCancelable(false)
                            .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                            .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }

            }
            else {
                requestRuntimePermission();
            }
        }




    }

}