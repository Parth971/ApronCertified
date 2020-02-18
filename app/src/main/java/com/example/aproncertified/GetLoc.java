package com.example.aproncertified;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Objects;

public class GetLoc extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "GetLoc";

    LatLng mlatLng;
    GoogleMap gMap;

    LocationManager manager;
    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int MY_PERMISSIONS_REQUEST_CODE = 1;
    private static final int LOCATION_CODE = 1;

    Activity context;

    public GetLoc(Activity context, double latitude, double longitude) {
        this.context = context;
        if(latitude==0.0 && longitude==0.0){
            mlatLng = null;
        }
        else {
            mlatLng = new LatLng(latitude, longitude);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gMap = googleMap;
        if(mlatLng!=null){
            gMap.clear();
            gMap.addMarker(new MarkerOptions().draggable(true).title("give Location").position(mlatLng));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mlatLng, 17));
        }
        else{
            curLocation();
        }


        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                gMap.clear();
                mlatLng = latLng;
                gMap.addMarker(new MarkerOptions().title("Clicked Place").position(latLng).draggable(true));
            }
        });
        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mlatLng = marker.getPosition();
            }
        });

    }

    public interface onSomeEventListener {
        void someEvent(LatLng latLng);
    }

    private onSomeEventListener someEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_get_location, container, false);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.getLocationMapFragment);
        supportMapFragment.getMapAsync(this);

        ImageButton button = (ImageButton) v.findViewById(R.id.setThisLocation);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(mlatLng!=null){
                    someEventListener.someEvent(mlatLng);
                }
                else{
                    Snackbar snackbar = Snackbar
                            .make(v, "Not selected any place", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }

            }
        });
        ImageButton btnCurrentLocation = (ImageButton) v.findViewById(R.id.currentLocation);
        btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curLocation();
            }
        });




        autocomplete();

        return v;
    }


    private void autocomplete() {

        // Initialize the SDK
        if(!Places.isInitialized()){
            Places.initialize(context, "AIzaSyBLCXlQ-H4e6zAKeGT2vlr9Jy1IhJaYRsc");
        }
        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(context);



        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);
        EditText t = (EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
        t.setTextSize(14);
        t.setBackgroundResource(R.drawable.border_toolbar);





        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if(place!=null){
                    LatLng latLng = new LatLng(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                            .title(place.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).draggable(true);
                    autocompleteFragment.setText(place.getAddress());
                    gMap.clear();
                    gMap.addMarker(markerOptions);
                    mlatLng = latLng;
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

                }
            }

            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                Log.d(TAG, "onError: " + status.toString());
            }



        });


//        AutoCompleteTextView textView = findViewById(R.id.search);
//
//        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
//
//        // Create a RectangularBounds object.
//        RectangularBounds bounds = RectangularBounds.newInstance(
//                new LatLng(28.70, -127.50),
//                new LatLng(48.85, -55.90));
//        // Use the builder to create a FindAutocompletePredictionsRequest.
//        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                // Call either setLocationBias() OR setLocationRestriction().
//                .setLocationBias(bounds)
//                //.setLocationRestriction(bounds)
//                .setCountry("au")
//                .setTypeFilter(TypeFilter.ADDRESS)
//                .setSessionToken(token)
//                .setQuery("keyur Park")
//                .build();
//
//        Log.d(TAG, "autocomplete: ");
//        placesClient.findAutocompletePredictions(request)
//                .addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
//                    @Override
//                    public void onSuccess(FindAutocompletePredictionsResponse findAutocompletePredictionsResponse) {
//                        for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
//                            Log.i(TAG, prediction.getPlaceId());
//                            Log.i(TAG, prediction.getPrimaryText(null).toString());
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: ");
//                    }
//                });
    }
    Location lastLocation;
    public void curLocation(){
        lastLocation = null;
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "mapping: Permssion already granted");
            ActivityCompat.requestPermissions(context, permissions, MY_PERMISSIONS_REQUEST_CODE);
            return;
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLongitude());

                lastLocation = location;
                if(lastLocation!=null){
                    mlatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    gMap.clear();
                    gMap.addMarker(new MarkerOptions().draggable(true).title("Current Location").position(mlatLng));

                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mlatLng, 17));

                    Toast.makeText(context, "Found Current Location", Toast.LENGTH_SHORT).show();
                }
                else{
                    mlatLng = null;
                    Toast.makeText(context, "Current Location Not Found", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(context, "GPS Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(context, "GPS Diabled", Toast.LENGTH_SHORT).show();
            }
        });
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(lastLocation == null){
                    Log.d(TAG, "onLocationChanged: " + location.getLongitude());

                    lastLocation = location;
                    if(lastLocation!=null){
                        mlatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        gMap.clear();
                        gMap.addMarker(new MarkerOptions().draggable(true).title("Current Location").position(mlatLng));

                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mlatLng, 17));

                        Toast.makeText(context, "Found Current Location", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mlatLng = null;
                        Toast.makeText(context, "Current Location Not Found", Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(context, "GPS Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(context, "GPS Diabled", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOCATION_CODE){
            curLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ){
                curLocation();
            }
        }
    }

    private void buildAlertMessageNoGps()  {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_CODE);
//                        LocationRequest mLocationRequest = LocationRequest.create()
//                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                                .setInterval(10 * 1000)
//                                .setFastestInterval(1 * 1000);
//
//                        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
//                                .addLocationRequest(mLocationRequest);
//                        settingsBuilder.setAlwaysShow(true);
//                        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(Home.this)
//                                .checkLocationSettings(settingsBuilder.build());
//
//                        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
//                            @Override
//                            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
//                                try {
//                                    LocationSettingsResponse response =
//                                            task.getResult(ApiException.class);
//                                } catch (ApiException ex) {
//                                    switch (ex.getStatusCode()) {
//                                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                                            try {
//                                                ResolvableApiException resolvableApiException =
//                                                        (ResolvableApiException) ex;
//                                                resolvableApiException
//                                                        .startResolutionForResult(Home.this,
//                                                                REQUEST_CHECK_SETTINGS);
//                                            } catch (IntentSender.SendIntentException e) {
//
//                                            }
//                                            break;
//                                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//
//                                            break;
//                                    }
//                                }
//                            }
//                        }).addOnCanceledListener(new OnCanceledListener() {
//                                    @Override
//                                    public void onCanceled() {
//                                        Log.e("GPS","checkLocationSettings -> onCanceled");
//                                    }
//                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            someEventListener = (onSomeEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }
}
