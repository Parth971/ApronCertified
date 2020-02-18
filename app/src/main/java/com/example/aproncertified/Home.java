package com.example.aproncertified;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.ClusterManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Home extends AppCompatActivity implements OnMapReadyCallback {

//    , NavigationView.OnNavigationItemSelectedListener

    private static final String TAG = "Home";

    private GoogleMap mMap;

    private FirebaseAuth firebaseAuth;
    private GestureDetectorCompat gestureDetectorCompat;
    private String currentUserId;

    public static final int MY_PERMISSIONS_REQUEST_CODE = 99;
    public static final int MY_PERMISSIONS_REQUEST_CODE_2 = 100;
    public static final int LOCATION_SERVICE = 55;


    public static final int CALL_PHONE = 310;
    Location currentLocation;
    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};


    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    LocationManager manager;

    ImageButton setting;
    ImageButton refresh_home;
    CheckBox open;
    Button signIn, apply;
    RadioGroup types;
    RatingBar ratingBar;


    LatLngBounds mBounds = new LatLngBounds(
            new LatLng(28.70, -127.50),
            new LatLng(48.85, -55.90));

    LatLngBounds curBounds;

    private ClusterManager<MyItem> mClusterManager;

    AutocompleteSupportFragment autocompleteFragment;

    AlertDialog.Builder builder;
    AlertDialog startingDialog;

    SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        check();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.clear();

        if (!isInternetAvailable()) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.rootLayout), "Internet not available", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            displayOthers displayOthers = new displayOthers();
            displayOthers.execute();
        }

    }

    private boolean check() {
        if (!isInternetAvailable()) {
            Toast.makeText(getApplicationContext(), "opps!", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_nextscreen_noiternet);
            gestureDetectorCompat = new GestureDetectorCompat(Home.this, new gestureListener());
            return false;

        } else {
            setContentView(R.layout.activity_next);
            process();
            return true;
        }

    }


    private class gestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            check();

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public void txtRetryClicked(View view) {

        check();

    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void process() {
        builder = new AlertDialog.Builder(Home.this, R.style.DialogTheme);
        final View view = LayoutInflater.from(Home.this).inflate(R.layout.logo, null);

        builder.setCancelable(false);
        builder.setView(view);

        startingDialog = builder.create();
        if (startingDialog.getWindow() != null)
            startingDialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        startingDialog.show();


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (startingDialog.isShowing()) {
                    startingDialog.dismiss();
                    mapping();
                }
            }
        }, 2000);


        navigationBar();
        autocomplete();

        setting = (ImageButton) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Setting.class);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    startActivity(i);
                } else {
                    Toast.makeText(Home.this, "Not Signed In", Toast.LENGTH_SHORT).show();
                }

            }
        });

        open = (CheckBox) findViewById(R.id.open);
        types = (RadioGroup) findViewById(R.id.types);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        apply = (Button) findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VariableReached = true;
                ratingFilter = ratingBar.getRating();
                mapFragment.getMapAsync(Home.this);
            }
        });


        signIn = (Button) findViewById(R.id.signIn);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
            dr.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if( dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                        signIn.setText("Sign Out");
                        Log.d(TAG, "onDataChange: ");
                    }
                    else {
                        FirebaseAuth.getInstance().signOut();
                        signIn.setText("Sign In");
                        Log.d(TAG, "onDataChange: 2");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            signIn.setText("Sign In");
        }
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this);
                    dialog.setTitle("Are you sure, you want to sign out?");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("Yup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            currentUserId = null;
                            signIn.setText("Sign In");
                        }
                    }).setNegativeButton("Nope", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                } else {
                    startActivityForResult(new Intent(Home.this, MainActivity.class), 100);
                }

            }
        });

        ImageButton goToCurrentLocation = findViewById(R.id.goToCurrentLocation);
        goToCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapping();
                EditText editText = (EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_input);
                View view1 = (View) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_clear_button);
                view1.setVisibility(View.GONE);
                editText.setText("");

            }
        });

        refresh_home = findViewById(R.id.refresh_home);
        refresh_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getMapAsync(Home.this);
                try {
                    currentUserId = firebaseAuth.getCurrentUser().getUid();
                } catch (Exception e) {
                    Log.d("TAG", "MSG: " + e.toString());
                    currentUserId = null;
                }

                ratingBar.setRating(0);
                open.setChecked(false);
                types.check(R.id.all);
                VariableReached = false;

//                navigationBar();
            }
        });


    }


    public void mapping() {
        currentLocation = null;
        Log.d(TAG, "mapping: ");
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "mapping: Permssion already granted");
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CODE);
            return;
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return;
        }

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1000, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: " + location.getLongitude());
                currentLocation = location;

                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                        .title("I am here")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                mMap.setMyLocationEnabled(true);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(Home.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(Home.this, "GPS Diabled", Toast.LENGTH_SHORT).show();
            }
        });
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1000, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if(currentLocation==null){
                    Log.d(TAG, "onLocationChanged:2 " + location.getLongitude());
                    currentLocation = location;

                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                            .title("I am here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    mMap.setMyLocationEnabled(true);
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(Home.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(Home.this, "GPS Diabled", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_SERVICE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == 101) {
                Toast.makeText(Home.this, "101 Pass", Toast.LENGTH_SHORT).show();
                if (mapFragment != null) {
                    mapFragment.getMapAsync(Home.this);
                    signIn.setText("Sign Out");
//                    navigationBar();
                }
            }
        }
        if (requestCode == LOCATION_SERVICE){
            mapping();
        }
    }

    //
//    private SettingsClient mSettingsClient;
//    private LocationSettingsRequest mLocationSettingsRequest;
//    private static final int REQUEST_CHECK_SETTINGS = 214;
//    private static final int REQUEST_ENABLE_GPS = 516;
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CHECK_SETTINGS) {
//            switch (resultCode) {
//                case Activity.RESULT_OK:
//                    break;
//                case Activity.RESULT_CANCELED:
//                    Log.e("GPS","User denied to access location");
//                    openGpsEnableSetting();
//                    break;
//            }
//        } else if (requestCode == REQUEST_ENABLE_GPS) {
//            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//            if (!isGpsEnabled) {
//                openGpsEnableSetting();
//            }
//
//        }
//    }
//    private void openGpsEnableSetting() {
//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivityForResult(intent, REQUEST_ENABLE_GPS);
//    }


//    private void requestingLocation() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(permissions, MY_PERMISSIONS_REQUEST_CODE);
//                return;
//            }
//
//        }
//        locationManager.requestLocationUpdates("gps", 0, 0, locationListener);
//
//
//    }
//    private void fetchLastLocation() {
//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CODE);
//            return;
//        }
//        Task<Location> task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if(location != null){
//                    currentLocation = location;
//                    Toast.makeText(Home.this, location.getLatitude()+", "+location.getLongitude(), Toast.LENGTH_SHORT).show();
//                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                            .findFragmentById(R.id.map);
//                    mapFragment.getMapAsync(Home.this);
//
//
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: " + e.getMessage());
//            }
//        });
//    }

    Marker marker_place;

    private void autocomplete() {

        // Initialize the SDK
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyBLCXlQ-H4e6zAKeGT2vlr9Jy1IhJaYRsc");
        }
        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);


        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.getView().findViewById(R.id.places_autocomplete_search_button).setVisibility(View.GONE);


        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (place != null) {
                    LatLng latLng = new LatLng(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng)
                            .title(place.getName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    autocompleteFragment.setText(place.getAddress());

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));


                    if (marker_place != null) {
                        marker_place.remove();
                        marker_place = mMap.addMarker(markerOptions);
                    } else {
                        marker_place = mMap.addMarker(markerOptions);
                    }
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

    public void navigationBar() {

        Log.d(TAG, "navigationBar: ");

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.rootLayout);


//        navCheck();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
//        navigationView.setNavigationItemSelectedListener(this);
    }

//    public void navCheck() {
//        Log.d(TAG, "navCheck: ");
//        navigationView = findViewById(R.id.nv);
//        if (currentUserId != null) {
//            navigationView.getMenu().clear();
//            if (currentUserId.equals("jjPsZ3cIP3O0jEZwDvTOK4vBEbv1") || currentUserId.equals("m0GQzz2q5BOWds4xu1CzX2j6wOB3")) {
//                navigationView.inflateMenu(R.menu.menubar_admin_signout);
//            } else {
//                navigationView.inflateMenu(R.menu.menubar_signout);
////                else if(USER_STATUS.equals("Denied")){
////                    navigationView.inflateMenu(R.menu.menubar_signout);
////
//            }
//        } else {
//            navigationView.getMenu().clear();
//            navigationView.inflateMenu(R.menu.menubar_signup);
//        }
//    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.setting:
//
//                break;
//            case R.id.sign_up:
//                startActivityForResult(new Intent(Home.this, MainActivity.class), 100);
//
//                break;
//            case R.id.sign_out:
//                if (isInternetAvailable()) {
//                    firebaseAuth.signOut();
//                    currentUserId = null;
//                    mapFragment.getMapAsync(Home.this);
////                    navigationBar();
//
//                } else {
//                    Snackbar snackbar = Snackbar
//                            .make(findViewById(R.id.rootLayout), "Cannot SignOut, Intenet!!", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//
//                break;
//
//            case R.id.form:
//                if (currentUserId != null) {
//                    Intent i = new Intent(Home.this, FormActivity.class);
//
////                    if(USER_STATUS!=null){
////                        i.putExtra("USER_STATUS", USER_STATUS);
////                        if (!USER_STATUS.equals("NOT FILLED FORM YET")) {
////                            i.putExtra("currentUserDetails", currentUserDetails);
////
////                        }
////                        startActivityForResult(i, 100);
////                    }
////                    else {
////                        Toast.makeText(Home.this, "Wait, then try again", Toast.LENGTH_SHORT).show();
////                    }
////
//
//                } else {
//                    startActivity(new Intent(Home.this, MainActivity.class));
//
//                }
//                break;
//            case R.id.admin:
//                if (isInternetAvailable()) {
//                    Intent i = new Intent(Home.this, Admin.class);
//                    startActivity(i);
//                } else {
//                    Snackbar snackbar = Snackbar
//                            .make(findViewById(R.id.rootLayout), "Intenet!!", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//                break;
//            case R.id.cancel_application:
//                if (isInternetAvailable()) {
//                    Intent i = new Intent(Home.this, AproveCancellation.class);
//                    startActivity(i);
//                } else {
//                    Snackbar snackbar = Snackbar
//                            .make(findViewById(R.id.rootLayout), "Intenet!!", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }
//                break;
//            case R.id.profile:
//                Intent i = new Intent(Home.this, Profile.class);
////                i.putExtra("USER_STATUS", USER_STATUS);
////                i.putExtra("currentUserDetails", currentUserDetails);
//                startActivity(i);
//                break;
//        }
//
//        return true;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission  granted");
                    mapping();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_CODE_2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    directionClicked(mMyItem, dialog);

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.d(TAG, "onRequestPermissionsResult: default");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onResume() {
        if (mapFragment != null) {
            mapFragment.getMapAsync(Home.this);
//            navigationBar();
        }


        Log.d(TAG, "onResume: ");
        firebaseAuth = FirebaseAuth.getInstance();
        try {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        } catch (Exception e) {
            Log.d("TAG", "MSG: " + e.toString());
            currentUserId = null;
        }


        super.onResume();
    }

    MyItem mMyItem;
    AlertDialog dialog;

    boolean VariableReached = false;
    float ratingFilter;

    public boolean closed(List<Timings> list){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
        String currentDateandTime = sdf.format(new Date());
        for(Timings t : list){
            if(t.getDay().equals(currentDateandTime)){
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                Log.d(TAG, "closed: " + formatter.format(date) + ", " + t.getToHr() + ":" +t.getToMin()
                        + firstIsGreater(formatter.format(date), t.getToHr() + ":" +t.getToMin() )) ;
                return firstIsGreater(formatter.format(date), t.getToHr() + ":" +t.getToMin() );
            }
        }
        return false;
    }
    public boolean firstIsGreater(String current, String variable){
        String[] c = current.split(":");
        String[] v = variable.split(":");
        String currentHr = c[0];
        String variableHr = v[0];
        String currentMin = c[1];
        String variableMin = v[1];

        Log.d(TAG, "firstIsGreater: " + currentHr + ":" + currentMin + ", " + variableHr + ":" + variableMin);
        if(Integer.valueOf(currentHr)>Integer.valueOf(variableHr)){
            return true;
        }
        else if(Integer.valueOf(currentHr)==Integer.valueOf(variableHr)){
            return Integer.valueOf(currentMin) > Integer.valueOf(variableMin);
        }
        return false;
    }

    class displayOthers extends AsyncTask<Void, DataSnapshot, Void> {
        GoogleMap gMaps;

        @Override
        protected void onPreExecute() {
            mClusterManager = new ClusterManager<MyItem>(Home.this, mMap);
            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);

            gMaps = mMap;


        }

        @Override
        protected Void doInBackground(Void... voids) {


            FirebaseDatabase fd = FirebaseDatabase.getInstance();
            DatabaseReference dr = fd.getReference();


            dr.child("Approved").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot i : dataSnapshot.getChildren()) {

                        if(VariableReached){
                            if(i.getValue(FormDetails.class).getRatings() >= ratingFilter ){

                                if(open.isChecked()){
                                    if(i.getValue(FormDetails.class).getTimingList()!=null){
                                        if(!closed(i.getValue(FormDetails.class).getTimingList())){
                                            switch (types.getCheckedRadioButtonId()){
                                                case R.id.restaurants:
                                                    if(i.getValue(FormDetails.class).getBusinessType().equals("Restaurant")){
                                                        publishProgress(i);
                                                    }
                                                    break;
                                                case R.id.foodCart:
                                                    if(i.getValue(FormDetails.class).getBusinessType().equals("Food Cart")){
                                                        publishProgress(i);
                                                    }
                                                    break;
                                                case R.id.all:
                                                    publishProgress(i);
                                                    break;
                                            }
                                        }
                                    }else {
                                        switch (types.getCheckedRadioButtonId()){
                                            case R.id.restaurants:
                                                if(i.getValue(FormDetails.class).getBusinessType().equals("Restaurant")){
                                                    publishProgress(i);
                                                }
                                                break;
                                            case R.id.foodCart:
                                                if(i.getValue(FormDetails.class).getBusinessType().equals("Food Cart")){
                                                    publishProgress(i);
                                                }
                                                break;
                                            case R.id.all:
                                                publishProgress(i);
                                                break;
                                        }
                                    }

                                } else {
                                    switch (types.getCheckedRadioButtonId()){
                                        case R.id.restaurants:
                                            if(i.getValue(FormDetails.class).getBusinessType().equals("Restaurant")){
                                                publishProgress(i);
                                            }
                                            break;
                                        case R.id.foodCart:
                                            if(i.getValue(FormDetails.class).getBusinessType().equals("Food Cart")){
                                                publishProgress(i);
                                            }
                                            break;
                                        case R.id.all:
                                            publishProgress(i);
                                            break;
                                    }
                                }
                            }
                        }else {
                            publishProgress(i);
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }


        @Override
        protected void onProgressUpdate(DataSnapshot... values) {
            for (DataSnapshot f : values) {
                FormDetails ff = f.getValue(FormDetails.class);
                mClusterManager.addItem(new MyItem(ff.getLatitude(), ff.getLongitude(), ff.getBusinessName(), ff, f.getKey()));
            }
            final CustomClusterRenderer renderer = new CustomClusterRenderer(Home.this, mMap, mClusterManager);
            mClusterManager.setRenderer(renderer);

            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                @Override
                public boolean onClusterItemClick(final MyItem myItem) {

                    List<Offers> list = new ArrayList<>();
                    list.add(new Offers("1"));
                    list.add(new Offers("2"));
                    list.add(new Offers("3"));
                    list.add(new Offers("4"));

                    mMyItem = myItem;
                    Log.d(TAG, "onClusterItemClick: " + myItem.getFormDetails().getfNameOfSeller());

                    View content = LayoutInflater.from(Home.this).inflate(R.layout.show_details_dialog, null);

                    final TextView phone = (TextView) content.findViewById(R.id.phone);

                    TextView title = content.findViewById(R.id.dialog_title);
                    TextView ratingText = content.findViewById(R.id.ratingText);
                    RatingBar ratings = content.findViewById(R.id.rating);


                    Button directions = (Button) content.findViewById(R.id.directions);
                    Button reviews = (Button) content.findViewById(R.id.reviews);


                    final ImageButton optionsForAdmin = (ImageButton) content.findViewById(R.id.optionsForAdmin);



                    LinearLayout TimeingLayout = (LinearLayout) content.findViewById(R.id.TimeingLayout);

                    TextView mondayTime = (TextView) content.findViewById(R.id.mondayTime);
                    TextView tuesdayTime = (TextView) content.findViewById(R.id.tuesdayTime);
                    TextView wednesdayTime = (TextView) content.findViewById(R.id.wednesdayTime);
                    TextView thursdayTime = (TextView) content.findViewById(R.id.thursdayTime);
                    TextView fridayTime = (TextView) content.findViewById(R.id.fridayTime);
                    TextView saturdayTime = (TextView) content.findViewById(R.id.saturdayTime);
                    TextView sundayTime = (TextView) content.findViewById(R.id.sundayTime);

                    TextView openOrClose = (TextView) content.findViewById(R.id.openOrClose);
                    if(myItem.getFormDetails().getTimingList()!=null){
                        if(closed(myItem.getFormDetails().getTimingList())){
                            openOrClose.setText("CLOSED");
                        }
                        else {
                            openOrClose.setText("OPEN");
                        }
                    }
                    else {
                        openOrClose.setText("No Timing Given");
                    }

                    if (myItem.getFormDetails().getTimingList() != null) {
                        TimeingLayout.setVisibility(View.VISIBLE);
                        mondayTime.setText(getTime(myItem.getFormDetails().getTimingList().get(0).getFromHr(),
                                myItem.getFormDetails().getTimingList().get(0).getFromMin() )
                                + "-"
                                + getTime(myItem.getFormDetails().getTimingList().get(0).getToHr(),
                                myItem.getFormDetails().getTimingList().get(0).getToMin())
                        );
                        tuesdayTime.setText(getTime(myItem.getFormDetails().getTimingList().get(1).getFromHr(),
                                myItem.getFormDetails().getTimingList().get(1).getFromMin())
                                + "-"
                                +getTime( myItem.getFormDetails().getTimingList().get(1).getToHr(),
                                myItem.getFormDetails().getTimingList().get(1).getToMin())
                        );
                        wednesdayTime.setText(getTime(myItem.getFormDetails().getTimingList().get(2).getFromHr(),
                                myItem.getFormDetails().getTimingList().get(2).getFromMin())
                                + "-"
                                +getTime( myItem.getFormDetails().getTimingList().get(2).getToHr(),
                                myItem.getFormDetails().getTimingList().get(2).getToMin())
                        );
                        thursdayTime.setText(getTime(myItem.getFormDetails().getTimingList().get(3).getFromHr(),
                                myItem.getFormDetails().getTimingList().get(3).getFromMin())
                                + "-"
                                +getTime( myItem.getFormDetails().getTimingList().get(3).getToHr(),
                                myItem.getFormDetails().getTimingList().get(3).getToMin())
                        );
                        fridayTime.setText(getTime(myItem.getFormDetails().getTimingList().get(4).getFromHr(),
                                myItem.getFormDetails().getTimingList().get(4).getFromMin())
                                + "-"
                                +getTime( myItem.getFormDetails().getTimingList().get(4).getToHr(),
                                myItem.getFormDetails().getTimingList().get(4).getToMin())
                        );
                        saturdayTime.setText(getTime(myItem.getFormDetails().getTimingList().get(5).getFromHr(),
                                myItem.getFormDetails().getTimingList().get(5).getFromMin())
                                + "-"
                                +getTime( myItem.getFormDetails().getTimingList().get(5).getToHr(),
                                myItem.getFormDetails().getTimingList().get(5).getToMin())
                        );
                        sundayTime.setText(getTime(myItem.getFormDetails().getTimingList().get(6).getFromHr(),
                                myItem.getFormDetails().getTimingList().get(6).getFromMin())
                                + "-"
                                +getTime( myItem.getFormDetails().getTimingList().get(6).getToHr(),
                                myItem.getFormDetails().getTimingList().get(6).getToMin())
                        );
                    }

                    final PopupMenu popup = new PopupMenu(Home.this, optionsForAdmin);
                    if(currentUserId!=null){
                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                        dr.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot d : dataSnapshot.getChildren()){

                                    if(d.getKey().equals(currentUserId)){
                                        Details details = d.getValue(Details.class);
                                        if(!details.getUserType().equals("User")){
                                            optionsForAdmin.setVisibility(View.VISIBLE);
                                            popup.getMenu().add(1, R.id.cancelCertificate, 1, "Cancel Certificate2");
                                            popup.getMenu().add(1, R.id.approveCheckUp, 2, "Approve Checkup");
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.cancelCertificate:
                                    DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                                    dr.child("Reviews").child(myItem.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        List<ReviewDataSnapshot> list = new ArrayList<>();

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                                list.add(new ReviewDataSnapshot(dataSnapshot.getKey(), d.getValue(ReviewClass.class)));
                                            }
                                            Intent i = new Intent(Home.this, CancelRequest.class);
                                            i.putExtra("list", (ArrayList<ReviewDataSnapshot>) list);
                                            i.putExtra("myItemFormdetails", myItem.getFormDetails());
                                            i.putExtra("myItemKey", myItem.getKey());
                                            startActivity(i);

                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d(TAG, "onCancelled: " + databaseError);
                                        }
                                    });
                                    return true;
                                case R.id.approveCheckUp:

                                    Intent i = new Intent(Home.this, CheckUpApproval.class);
                                    i.putExtra("myItemKey", myItem.getKey());
                                    startActivity(i);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    optionsForAdmin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popup.show();
                        }
                    });




                    title.setText(myItem.getFormDetails().getBusinessName());
                    ratingText.setText(String.valueOf(myItem.getFormDetails().getRatings()));
                    ratings.setRating(myItem.getFormDetails().getRatings());


                    if (myItem.getFormDetails().getSecurity().equals("Public")) {
                        phone.setText(myItem.getFormDetails().getPhoneNum());
                    } else {
                        phone.setVisibility(View.GONE);
                    }


                    directions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            directionClicked(myItem, dialog);
                        }
                    });
                    reviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentUserId != null) {
                                Intent i = new Intent(Home.this, Reviews.class);
                                i.putExtra("MarkersId", myItem.getKey());
                                startActivity(i);
                            } else {
                                startActivityForResult(new Intent(Home.this, MainActivity.class), 100);

                            }

                        }
                    });
                    phone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + phone.getText().toString()));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(Home.this, new String[] {Manifest.permission.CALL_PHONE}, CALL_PHONE);
                                    return;
                                }
                            }
                            startActivity(callIntent);
                        }
                    });

                    AlertDialog.Builder newAlert = new AlertDialog.Builder(Home.this);

                    View next = LayoutInflater.from(Home.this).inflate(R.layout.next, null);

                    ViewPager vp =(ViewPager) next.findViewById(R.id.viewPager);
                    vp.setAdapter(new MyPagesAdapter(Home.this, list, content));


                    newAlert.setView(next);
                    dialog = newAlert.create();
                    dialog.show();
//
                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//                    layoutParams.copyFrom(dialog.getWindow().getAttributes());
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    layoutParams.height = 1000;
                    dialog.getWindow().setAttributes(layoutParams);


                    return true;
                }
            });


        }
    }
    public String getTime(int hr, int min){

        if(hr >=12){
            int newHr = hr -12, newMin = min + (newHr*60), tempMin = 0, tempHr = 0;
            for(; newMin>0 ;newMin--){
                tempMin = tempMin + 1;
                if(tempMin==60){
                    tempHr = tempHr + 1;
                    tempMin = 0;
                }
            }
            return tempHr+":"+ tempMin + " PM";
        }
        else {
            return hr +":"+ min +" AM";
        }


    }


    public void directionClicked(final MyItem myItem, final AlertDialog dialog){
        currentLocation = null;
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "mapping: Permssion already granted");
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CODE_2);
            return;
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return;
        }

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: 2" + location.getLongitude());
                currentLocation = location;

                if(currentLocation.getLatitude() ==  myItem.getFormDetails().getLatitude() &&
                        currentLocation.getLongitude() ==  myItem.getFormDetails().getLongitude() ){
                    Toast.makeText(Home.this, "You Are already There", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG, "onClick:1 "+ currentLocation.getLatitude()+","+currentLocation.getLongitude() );
                    Log.d(TAG, "onClick:2 "+ myItem.getFormDetails().getLatitude()+","+ myItem.getFormDetails().getLongitude() );
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+
                                    currentLocation.getLatitude()+","+currentLocation.getLongitude()
                                    +"&daddr="+
                                    myItem.getFormDetails().getLatitude()+","+ myItem.getFormDetails().getLongitude() ) );
//                                    dialog.dismiss();
                    startActivity(intent);

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
        });
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(currentLocation==null){
                    Log.d(TAG, "onLocationChanged: 2" + location.getLongitude());
                    currentLocation = location;

                    if(currentLocation.getLatitude() ==  myItem.getFormDetails().getLatitude() &&
                            currentLocation.getLongitude() ==  myItem.getFormDetails().getLongitude() ){
                        Toast.makeText(Home.this, "You Are already There", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d(TAG, "onClick:1 "+ currentLocation.getLatitude()+","+currentLocation.getLongitude() );
                        Log.d(TAG, "onClick:2 "+ myItem.getFormDetails().getLatitude()+","+ myItem.getFormDetails().getLongitude() );
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr="+
                                        currentLocation.getLatitude()+","+currentLocation.getLongitude()
                                        +"&daddr="+
                                        myItem.getFormDetails().getLatitude()+","+ myItem.getFormDetails().getLongitude() ) );
//                                    dialog.dismiss();
                        startActivity(intent);

                    }
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
        });
    }


}
