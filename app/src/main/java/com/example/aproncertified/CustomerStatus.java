package com.example.aproncertified;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CustomerStatus extends AppCompatActivity {
    private static final String TAG = "CustomerStatus";
    private static final int MY_PERMISSIONS_REQUEST_CODE = 1;

    private static final int LOCATION_CODE = 1;

    TextView sName, bName, phone, date;
    String currentkey;

    Spinner locationMarks, foodMarks, chefMarks;
    EditText inspectionComment;

    Button goTOThatLocation, btnApproved, btnApprovalDenied;

    FirebaseAuth fa;
    FirebaseDatabase fd;
    DatabaseReference dr;

    Location currentLocation;

    LocationManager manager;

    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    FormDetails f;

    ArrayAdapter<CharSequence> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        btnApproved = (Button) findViewById(R.id.btnApproved);
        btnApprovalDenied = (Button) findViewById(R.id.btnApprovalDenied);
        goTOThatLocation = (Button) findViewById(R.id.goTOThatLocation);

        locationMarks = (Spinner) findViewById(R.id.locationMarks);
        foodMarks = (Spinner) findViewById(R.id.foodMarks);
        chefMarks = (Spinner) findViewById(R.id.chefMarks);
        inspectionComment = (EditText) findViewById(R.id.inspectionComment);

        sName = (TextView) findViewById(R.id.sName);
        bName = (TextView) findViewById(R.id.bName);
//        desc = (TextView) findViewById(R.id.desc);
        phone = (TextView) findViewById(R.id.phone);
        date = (TextView) findViewById(R.id.date);

        adapter = ArrayAdapter.createFromResource(this, R.array.ranks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        locationMarks.setAdapter(adapter);
        foodMarks.setAdapter(adapter);
        chefMarks.setAdapter(adapter);

        f =  (FormDetails) getIntent().getSerializableExtra("obj");
        currentkey = getIntent().getStringExtra("key");

        sName.setText(f.getfNameOfSeller() + " " + f.getlNameOfSeller());
        bName.setText(f.getBusinessName());
        phone.setText(f.getPhoneNum());
        date.setText(f.getDate());

        fd = FirebaseDatabase.getInstance();
        dr = fd.getReference();
        fa = FirebaseAuth.getInstance();

    }

    public void approved(View view) {
        f.setStatus("Approved");
        ApproveDenie approveDenie = new ApproveDenie();
        approveDenie.execute("Approved");

    }

    public void denied(View view) {
        f.setStatus("Denied");
        ApproveDenie approveDenie = new ApproveDenie();
        approveDenie.execute("Denied");

    }

    public void goTOThatLocation(View view){
        currentLocation = null;
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(CustomerStatus.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(CustomerStatus.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "mapping: Permssion already granted");
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CODE);
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
                currentLocation = location;

                if(currentLocation.getLatitude() ==  f.getLatitude() &&
                        currentLocation.getLongitude() ==  f.getLongitude() ){
                    Toast.makeText(CustomerStatus.this, "You Are already There", Toast.LENGTH_SHORT).show();

                }
                else{
                    Log.d(TAG, "onClick:1 "+ currentLocation.getLatitude()+","+currentLocation.getLongitude() );
                    Log.d(TAG, "onClick:2 "+ f.getLatitude()+","+ f.getLongitude() );
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+
                                    currentLocation.getLatitude()+","+currentLocation.getLongitude()
                                    +"&daddr="+
                                    f.getLatitude()+","+ f.getLongitude() ) );
//                                    dialog.dismiss();
                    startActivity(intent);

                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(CustomerStatus.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(CustomerStatus.this, "GPS Diabled", Toast.LENGTH_SHORT).show();
            }
        });
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(currentLocation==null){
                    Log.d(TAG, "onLocationChanged: " + location.getLongitude());
                    currentLocation = location;

                    if(currentLocation.getLatitude() ==  f.getLatitude() &&
                            currentLocation.getLongitude() ==  f.getLongitude() ){
                        Toast.makeText(CustomerStatus.this, "You Are already There", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Log.d(TAG, "onClick:1 "+ currentLocation.getLatitude()+","+currentLocation.getLongitude() );
                        Log.d(TAG, "onClick:2 "+ f.getLatitude()+","+ f.getLongitude() );
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr="+
                                        currentLocation.getLatitude()+","+currentLocation.getLongitude()
                                        +"&daddr="+
                                        f.getLatitude()+","+ f.getLongitude() ) );
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
                Toast.makeText(CustomerStatus.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(CustomerStatus.this, "GPS Diabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildAlertMessageNoGps()  {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_CODE);
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
        if(requestCode == LOCATION_CODE){
            goTOThatLocation(goTOThatLocation);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ){
                goTOThatLocation(goTOThatLocation);
            }
        }
    }


    String strLocationMarks, strFoodMarks, strChefMarks, strInspectionComment, strDate;


    ProgressDialog progressDialog;



    class ApproveDenie extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(CustomerStatus.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();


            strLocationMarks = locationMarks.getSelectedItem().toString();
            strFoodMarks = foodMarks.getSelectedItem().toString();
            strChefMarks = chefMarks.getSelectedItem().toString();
            strInspectionComment = inspectionComment.getText().toString();

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            strDate = df.format(c);
        }

        @Override
        protected String doInBackground(final String... strings) {

            dr.child("Users").child(fa.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final List<InspectorClass> list = new ArrayList<>();

                    String inspectorName = dataSnapshot.getValue(Details.class).getfName() + " " + dataSnapshot.getValue(Details.class).getlName();
                    list.add(new InspectorClass(strLocationMarks, strFoodMarks, strChefMarks, strInspectionComment, strDate, fa.getCurrentUser().getUid(), inspectorName ));
                    f.setInspectorList(list);
                    f.setInspectorAssigned(null);

                    dr.child(strings[0]).child(currentkey).setValue(f);
                    dr.child("FormFilled").child(currentkey).setValue(f);

                    final List<String> list1 = new ArrayList<>();
                    dr.child("InspectorWork").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot d : dataSnapshot.getChildren()){
                                list1.add(d.getValue(String.class));
                            }
                            list1.remove(currentkey);
                            dr.child("InspectorWork").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(list1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return strings[0];

        }


        @Override
        protected void onPostExecute(String values) {
            dr.child("Pending").child(currentkey).removeValue();
//            if(values.equals("Approved")){
//                btnApproved.setText("Approved");
//            }
//            else {
//                btnApprovalDenied.setText("Denied");
//            }
//            btnApprovalDenied.setEnabled(false);
//            btnApproved.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    finish();
                }
            }, 3000);

        }
    }


}
