package com.example.aproncertified;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Setting extends AppCompatActivity {

    private static final String TAG = "Setting";

    Button edit, form, addAdmin, removeAdmin, addInspector, removeInspector;
    TextView txtName, adminUid, pendingCertificationForm, pendingCancellation, pendingMonthlyInspection, totalActiveCertification,
            inspectorUid, newCertificationTask, monthlyInspectionTask, head, hours, latLong, bType, bPhone, CertiDate;
    RatingBar bRating;
    EditText email, phone;
//    ImageButton profileImageButton;
//    ImageView profileImageView;
    ImageView imgProfile, imgPlus;
    public static final int REQUEST_IMAGE = 100;


    LinearLayout adminPanel, inspectorPanel, approvedLayout, timeLayout;

    FirebaseAuth fa;
    DatabaseReference dr;

    boolean isEditing = false;
    private static final int GALLARY_REQUEST_CODE = 2;
    private static final int CAMERA_CODE = 3;

    FormDetails currentUserDetails;
    String USER_STATUS;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        edit = (Button) findViewById(R.id.edit);
        form = (Button) findViewById(R.id.form);
        addAdmin = (Button) findViewById(R.id.addAdmin);
        removeAdmin = (Button) findViewById(R.id.removeAdmin);
        addInspector = (Button) findViewById(R.id.addInspector);
        removeInspector = (Button) findViewById(R.id.removeInspector);

        adminUid = (TextView) findViewById(R.id.adminUid);
        pendingCertificationForm = (TextView) findViewById(R.id.pendingCertificationForm);
        pendingCancellation = (TextView) findViewById(R.id.pendingCancellation);
        pendingMonthlyInspection = (TextView) findViewById(R.id.pendingMonthlyInspection);
        totalActiveCertification = (TextView) findViewById(R.id.totalActiveCertification);

        inspectorUid = (TextView) findViewById(R.id.inspectorUid);
        newCertificationTask = (TextView) findViewById(R.id.newCertificationTask);
        monthlyInspectionTask = (TextView) findViewById(R.id.monthlyInspectionTask);

        txtName = (TextView) findViewById(R.id.txtName);
//        profileImageView = (ImageView) findViewById(R.id.profileImageView);
//        profileImageButton = (ImageButton) findViewById(R.id.profileImageButton);
        imgProfile = (ImageView) findViewById(R.id.img_profile);
        imgPlus = (ImageView) findViewById(R.id.img_plus);


        adminPanel = (LinearLayout) findViewById(R.id.adminPanel);
        timeLayout = (LinearLayout) findViewById(R.id.timeLayout);
        inspectorPanel = (LinearLayout) findViewById(R.id.inspectorPanel);
        approvedLayout = (LinearLayout) findViewById(R.id.approvedLayout);

        head = (TextView) findViewById(R.id.head);
        hours = (TextView) findViewById(R.id.hours);
        latLong = (TextView) findViewById(R.id.latLong);
        bType = (TextView) findViewById(R.id.bType);
        bPhone = (TextView) findViewById(R.id.bPhone);
        CertiDate = (TextView) findViewById(R.id.CertiDate);
        bRating = (RatingBar) findViewById(R.id.bRating);


        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        ImagePickerActivity.clearCache(this);

        pendingCertificationForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Setting.this, Admin.class));
            }
        });
        pendingCancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Setting.this, AproveCancellation.class);
                startActivity(i);
            }
        });
        newCertificationTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Setting.this, InspectorTasks.class));
            }
        });
        addInspector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Setting.this);
                final View view = LayoutInflater.from(Setting.this).inflate(R.layout.add_remove_admin, null);
                final RecyclerView addRemoveAdminRecyclerView = (RecyclerView) view.findViewById(R.id.addRemoveAdminRecyclerView);

                alert.setView(view);
                final Dialog dialog = alert.create();

                final List<Details> list = new ArrayList<>();
                final List<String> keys = new ArrayList<>();
                dr = FirebaseDatabase.getInstance().getReference();
                dr.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        keys.clear();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            Details details = d.getValue(Details.class);
                            if(details.getUserType().equals("User")){
                                list.add(details);
                                keys.add(d.getKey());
                            }
                        }
                        if(!list.isEmpty()){
                            dialog.show();
                        }else {
                            Toast.makeText(Setting.this, "No Users found", Toast.LENGTH_SHORT).show();
                        }

                        final AddRemoveAdminAdapter adapter = new AddRemoveAdminAdapter(Setting.this, list, "Add");
                        adapter.setOnAddClicked(new AddRemoveAdminAdapter.OnBtnClicked() {
                            @Override
                            public void action(final int position) {

                                dr.child("Users").child(keys.get(position)).child("userType").setValue("Inspector").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        list.remove(position);
                                        if(list.isEmpty()){
                                            dialog.dismiss();
                                        }
                                        addRemoveAdminRecyclerView.setAdapter(adapter);
                                    }
                                });
                            }
                        });
                        addRemoveAdminRecyclerView.setLayoutManager(new LinearLayoutManager(Setting.this));
                        addRemoveAdminRecyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        removeInspector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Setting.this);
                final View view = LayoutInflater.from(Setting.this).inflate(R.layout.add_remove_admin, null);
                final RecyclerView addRemoveAdminRecyclerView = (RecyclerView) view.findViewById(R.id.addRemoveAdminRecyclerView);

                alert.setView(view);
                final Dialog dialog = alert.create();

                final List<Details> list = new ArrayList<>();
                final List<String> keys = new ArrayList<>();
                dr = FirebaseDatabase.getInstance().getReference();
                dr.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        keys.clear();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            Details details = d.getValue(Details.class);
                            if(details.getUserType().equals("Inspector")){
                                list.add(details);
                                keys.add(d.getKey());
                            }
                        }
                        if(!list.isEmpty()){
                            dialog.show();
                        }else {
                            Toast.makeText(Setting.this, "No Inspectors found", Toast.LENGTH_SHORT).show();
                        }

                        final AddRemoveAdminAdapter adapter = new AddRemoveAdminAdapter(Setting.this, list, "Remove");
                        adapter.setOnAddClicked(new AddRemoveAdminAdapter.OnBtnClicked() {
                            @Override
                            public void action(final int position) {

                                dr.child("Users").child(keys.get(position)).child("userType").setValue("User").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        list.remove(position);
                                        if(list.isEmpty()){
                                            dialog.dismiss();
                                        }
                                        addRemoveAdminRecyclerView.setAdapter(adapter);

                                    }
                                });
                            }
                        });
                        addRemoveAdminRecyclerView.setLayoutManager(new LinearLayoutManager(Setting.this));
                        addRemoveAdminRecyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        addAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Setting.this);
                final View view = LayoutInflater.from(Setting.this).inflate(R.layout.add_remove_admin, null);
                final RecyclerView addRemoveAdminRecyclerView = (RecyclerView) view.findViewById(R.id.addRemoveAdminRecyclerView);

                alert.setView(view);
                final Dialog dialog = alert.create();

                final List<Details> list = new ArrayList<>();
                final List<String> keys = new ArrayList<>();
                dr = FirebaseDatabase.getInstance().getReference();
                dr.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        keys.clear();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            Details details = d.getValue(Details.class);
                            if(!details.getUserType().equals("Admin")){
                                list.add(details);
                                keys.add(d.getKey());
                            }
                        }
                        if(!list.isEmpty()){
                           dialog.show();
                        }else {
                            Toast.makeText(Setting.this, "No Users or Inspectors found", Toast.LENGTH_SHORT).show();
                        }

                        final AddRemoveAdminAdapter adapter = new AddRemoveAdminAdapter(Setting.this, list, "Add");
                        adapter.setOnAddClicked(new AddRemoveAdminAdapter.OnBtnClicked() {
                            @Override
                            public void action(final int position) {
                                dr.child("Users").child(keys.get(position)).child("userType").setValue("Admin").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        list.remove(position);
                                        if(list.isEmpty()){
                                            dialog.dismiss();
                                        }
                                        addRemoveAdminRecyclerView.setAdapter(adapter);
                                    }
                                });
                            }
                        });
                        addRemoveAdminRecyclerView.setLayoutManager(new LinearLayoutManager(Setting.this));
                        addRemoveAdminRecyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        removeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(Setting.this);
                final View view = LayoutInflater.from(Setting.this).inflate(R.layout.add_remove_admin, null);
                final RecyclerView addRemoveAdminRecyclerView = (RecyclerView) view.findViewById(R.id.addRemoveAdminRecyclerView);

                final List<Details> list = new ArrayList<>();
                final List<String> keys = new ArrayList<>();
                dr = FirebaseDatabase.getInstance().getReference();
                dr.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        keys.clear();
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            Details details = d.getValue(Details.class);
                            if(details.getUserType().equals("Admin")){
                                list.add(details);
                                keys.add(d.getKey());
                            }
                        }

                        final AddRemoveAdminAdapter adapter = new AddRemoveAdminAdapter(Setting.this, list, "Remove");
                        adapter.setOnAddClicked(new AddRemoveAdminAdapter.OnBtnClicked() {
                            @Override
                            public void action(final int position) {
                                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(keys.get(position))){
                                    if(list.size()==1){
                                        Toast.makeText(Setting.this, "You cannot leave", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    dr.child("Users").child(keys.get(position)).child("userType").setValue("User");
                                    Toast.makeText(Setting.this, "Now you are not Admin", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                }

                                dr.child("Users").child(keys.get(position)).child("userType").setValue("User").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        list.remove(position);
                                        addRemoveAdminRecyclerView.setAdapter(adapter);

                                    }
                                });
                            }
                        });
                        addRemoveAdminRecyclerView.setLayoutManager(new LinearLayoutManager(Setting.this));
                        addRemoveAdminRecyclerView.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                alert.setView(view);
                alert.show();
            }
        });





//        profileImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
////                    ActivityCompat.requestPermissions(Setting.this,
////                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
////                            CAMERA_CODE);
////                    return;
////                }
////                getProfilePic();
//
//
//            }
//        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEditing){
                    if(uri!=null){
                        StorageReference sr = FirebaseStorage.getInstance().getReference();
                        final StorageReference paths = sr.child("UserPhotos")
                                .child(fa.getCurrentUser().getUid()).child(fa.getCurrentUser().getUid() + ".jpg");
                        paths.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                paths.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        dr.child("Users").child(fa.getCurrentUser().getUid()).child("ProfilePicUrl").setValue(uri.toString());

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getMessage() );
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.getMessage());
                            }
                        });
                    }


                    isEditing = false;
//                    profileImageButton.setVisibility(View.GONE);
//                    profileImageView.setVisibility(View.VISIBLE);

                    edit.setText("Edit");

                    email.setEnabled(false);
                    phone.setEnabled(false);

                    dr.child("Users").child(fa.getCurrentUser().getUid()).child("email").setValue(email.getText().toString());
                    dr.child("Users").child(fa.getCurrentUser().getUid()).child("phone").setValue(phone.getText().toString());






                }
                else {
                    isEditing = true;
//                    profileImageButton.setVisibility(View.VISIBLE);
//                    profileImageView.setVisibility(View.GONE);

                    edit.setText("Save");

                    email.setEnabled(true);
                    phone.setEnabled(true);
                }

            }
        });
        form.setEnabled(false);
        form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Setting.this, FormActivity.class);
                i.putExtra("USER_STATUS", USER_STATUS);
                i.putExtra("currentUserDetails", currentUserDetails);
                startActivity(i);

            }
        });

    }
    Uri uri;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    getProfilePic();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.d(TAG, "onRequestPermissionsResult: default");
        }
    }

//    public void getProfilePic(){
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLARY_REQUEST_CODE);
//    }

    class CheckForStatus extends AsyncTask<Void,FormDetails, Void> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute: ");
            fa = FirebaseAuth.getInstance();
            dr = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        protected Void doInBackground(final Void... voids) {

            dr.child("FormFilled").child(fa.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    publishProgress(dataSnapshot.getValue(FormDetails.class));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " +databaseError);
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(FormDetails... values) {
            Log.d(TAG, "onProgressUpdate: " + values[0]);
            if(values[0]!=null){
                USER_STATUS = values[0].getStatus();
                currentUserDetails = values[0];
            }
            else{
                USER_STATUS = "NOT FILLED FORM YET";
            }
            form.setEnabled(true);

        }
    }

    class PendingCetificate extends AsyncTask<Void, Long, Void>{
        @Override
        protected void onPreExecute() {
            dr = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dr.child("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    publishProgress(dataSnapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Long... longs) {
            pendingCertificationForm.setText(longs[0].toString());
        }
    }
    class PendingCancellation extends AsyncTask<Void, Long, Void>{
        @Override
        protected void onPreExecute() {
            dr = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dr.child("CancelApproval").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    publishProgress(dataSnapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Long... longs) {
            pendingCancellation.setText(longs[0].toString());
        }
    }
    class TotalActiveCertificate extends AsyncTask<Void, Long, Void>{
        @Override
        protected void onPreExecute() {
            dr = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dr.child("Approved").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    publishProgress(dataSnapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Long... longs) {
            totalActiveCertification.setText(longs[0].toString());
        }
    }
    class GetApprovedUserDetails extends AsyncTask<Void, FormDetails, Void>{
        @Override
        protected void onPreExecute() {
            dr = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dr.child("Approved").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        if(d.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            publishProgress(d.getValue(FormDetails.class));
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
        protected void onProgressUpdate(FormDetails... formDetails) {
            if(formDetails[0]!=null){
                approvedLayout.setVisibility(View.VISIBLE);

                head.setText(formDetails[0].getBusinessName() + " - Approved");
                if(formDetails[0].getTimingList()!=null){
                    hours.setText("Open Today Untill " + getDate(formDetails[0].getTimingList()));
                }
                else {
                    timeLayout.setVisibility(View.GONE);
                }
                latLong.setText(formDetails[0].getLatitude() + ", " + formDetails[0].getLongitude());
                bType.setText(formDetails[0].getBusinessType());
                bPhone.setText(formDetails[0].getPhoneNum());
                bRating.setRating(formDetails[0].getRatings());
                CertiDate.setText(formDetails[0].getInspectorList().get(0).getDate());
            }
        }
    }
    class NewCertificationTask extends AsyncTask<Void, Long, Void>{
        @Override
        protected void onPreExecute() {
            dr = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dr.child("InspectorWork").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    publishProgress(dataSnapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Long... longs) {
            newCertificationTask.setText(longs[0].toString());
        }
    }


    public String getDate(List<Timings> list){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
        String currentDateandTime = sdf.format(new Date());
        for(Timings t : list){
            if(t.getDay().equals(currentDateandTime)){
                return getTime(t.getToHr(), t.getToMin());
            }
        }

        return "";
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
    boolean firsttime = true;
    @Override
    protected void onResume() {
        super.onResume();
        CheckForStatus task = new CheckForStatus();
        task.execute();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fa = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        dr.child("Users").child(fa.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Details d = dataSnapshot.getValue(Details.class);
                if(firsttime){
                    email.setText(d.getEmail());
                    phone.setText(d.getPhone());
                    txtName.setText(d.getfName() + " " + d.getlName());
                    if(d.getProfilePicUrl()!=null){
                        loadProfile(d.getProfilePicUrl());
                    }
                    firsttime = false;
                }

                progressDialog.dismiss();
                if(d.getUserType()!=null){
                    switch (d.getUserType()){
                        case "Admin":
                            adminPanel.setVisibility(View.VISIBLE);

                            adminUid.setText(getId(fa.getCurrentUser().getUid()));

                            PendingCetificate task1 = new PendingCetificate();
                            task1.execute();

                            PendingCancellation task2 = new PendingCancellation();
                            task2.execute();

                            TotalActiveCertificate task3 = new TotalActiveCertificate();
                            task3.execute();

                            break;
                        case "Inspector":
                            inspectorPanel.setVisibility(View.VISIBLE);
                            inspectorUid.setText(getId(fa.getCurrentUser().getUid()));

                            NewCertificationTask task4 = new NewCertificationTask();
                            task4.execute();

                            break;
                        default:

                            GetApprovedUserDetails getApprovedUserDetails = new GetApprovedUserDetails();
                            getApprovedUserDetails.execute();

                            form.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onCreate: default called" );
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " +databaseError.toString());
            }
        });
    }

    public String getId(String string){
        String newString = "";
        for(char c : string.toCharArray()){
            if( ( (int) c >=48 ) && ( (int) c <= 57 ) ){
                newString += c;
            }

        }

        return newString;
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this).load(url)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }


    public void onProfileImageClick(View v) {
        if(isEditing){
            Dexter.withActivity(this)
                    .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                showImagePickerOptions();
                            }

                            if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingsDialog();
                            }
                        }
                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }
        else {
            Toast.makeText(this, "not Editing", Toast.LENGTH_SHORT).show();
        }
        
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(Setting.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(Setting.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    uri = data.getParcelableExtra("path");
                    try {
                        Log.d(TAG, "onActivityResult: ");
                        // You can update this bitmap to your server
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                        // loading profile image from local cache
                        loadProfile(uri.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
//            case GALLARY_REQUEST_CODE:
////
////                if (resultCode == Activity.RESULT_OK) {
////                    if (data != null && data.getData() != null) {
////
////                        uri = data.getData();
////
////                        try {
////                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
////                            profileImageButton.setImageBitmap(bitmap);
////                            profileImageView.setImageBitmap(bitmap);
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////
////                    }
////                }else if (resultCode == Activity.RESULT_CANCELED) {
////                    Log.d(TAG, "onActivityResult: result cancelled");
////                }
////                break;
            default:
                Log.d(TAG, "onActivityResult: default");

        }

    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
