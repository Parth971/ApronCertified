package com.example.aproncertified;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FormActivity extends AppCompatActivity implements GetLoc.onSomeEventListener, TimingFrg.TimingListener {

    private static final String TAG = "FormActivity";

    private static final String STAGE1 = "Pending";

    private Toolbar toolbar;

    String id;

    ImageView imgProfile, imgPlus;
    public static final int REQUEST_IMAGE = 100;

    EditText fNameOfSeller, lNameOfSeller, businessName, phoneNum;
    Button btnSubmit, btnGetLocationFromMap, selectTiming;
    ImageView  smallCheckTiming;
    TextView txtLatitude, txtLongitude;

    RadioGroup radioGroup;
    RadioButton r;

    FirebaseAuth fa;
    DatabaseReference dr;
    StorageReference sr;

    RelativeLayout l;

    double latitude= 0.0, longitude=0.0;

    String USER_STATUS;
    String profilePicUri;
    StorageReference ref;

    FormDetails currentUserDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        fNameOfSeller = (EditText) findViewById(R.id.fNameOfSeller);
        lNameOfSeller = (EditText) findViewById(R.id.lNameOfSeller);
        businessName = (EditText) findViewById(R.id.businessName);
        phoneNum = (EditText) findViewById(R.id.phoneNum);

        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnGetLocationFromMap =(Button) findViewById(R.id.btnGetLocationFromMap);
        selectTiming = (Button) findViewById(R.id.selectTiming);

        imgProfile = (ImageView) findViewById(R.id.img_profile);
        imgPlus = (ImageView) findViewById(R.id.img_plus);

        ImagePickerActivity.clearCache(this);

        smallCheckTiming = (ImageView) findViewById(R.id.smallCheckTiming);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        r = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());

        fa = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();


        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Form");
        }
//        toolbar.inflateMenu(R.menu.form_activity_menubar);


        USER_STATUS = getIntent().getStringExtra("USER_STATUS");
        currentUserDetails = (FormDetails) getIntent().getSerializableExtra("currentUserDetails");

        if(!USER_STATUS.equals("NOT FILLED FORM YET")) {
            fNameOfSeller.setText(currentUserDetails.getfNameOfSeller());
            lNameOfSeller.setText(currentUserDetails.getlNameOfSeller());
            businessName.setText(currentUserDetails.getBusinessName());
            phoneNum.setText(currentUserDetails.getPhoneNum());
            profilePicUri = currentUserDetails.getProfilePicUri();

            if(currentUserDetails.getProfilePicUri()!=null){
                loadProfile(currentUserDetails.getProfilePicUri());
            }

            Log.d(TAG, "onCreate: " +  r.getText());
            if(!currentUserDetails.getBusinessType().equals(r.getText())){
                radioGroup.clearCheck();
                radioGroup.check(R.id.radioButton2);
            }

            if(currentUserDetails.getTimingList()!=null){
                TimingList = currentUserDetails.getTimingList();
                smallCheckTiming.setVisibility(View.VISIBLE);
            }

            latitude = currentUserDetails.getLatitude();
            longitude = currentUserDetails.getLongitude();

            txtLatitude.setText(String.valueOf(latitude));
            txtLongitude.setText(String.valueOf(longitude));

            btnSubmit.setText("Resubmit");

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Form - " + USER_STATUS);
            }
        }



        switch (USER_STATUS){
            case "Approved":
                btnSubmit.setEnabled(false);
//                btnGetLocationFromMap.setVisibility(View.GONE);
////                selectMarkerImage.setVisibility(View.GONE);
//                selectTiming.setVisibility(View.GONE);
//
//                nameOfSeller.setEnabled(false);
//                businessName.setEnabled(false);
//
//                phoneNum.setEnabled(false);
//
//                if (getSupportActionBar() != null) {
//                    getSupportActionBar().setTitle("Form - Approved Already");
//                }
//
                break;
            case "Denied":
            case "Pending":


                break;
            default:

        }

        l = (RelativeLayout) findViewById(R.id.cont);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        onClickListeners();

    }


    public void onClickListeners(){
        selectTiming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timingFrg = new TimingFrg(FormActivity.this, TimingList);

                timingTransaction = getSupportFragmentManager().beginTransaction();

                timingTransaction.replace(R.id.menuContainer, timingFrg).commit();

                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                isSelectTimingOpen = true;
            }
        });

        btnGetLocationFromMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new GetLoc(FormActivity.this, latitude, longitude);

                transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.container, fragment).commit();

                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                isOpen =true;
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "formSubmitted: "+ latitude +", "+ longitude);
                if(latitude==0.0 || longitude==0.0){
                    Snackbar snackbar = Snackbar
                            .make(l, "Location not found", Snackbar.LENGTH_LONG);

                    snackbar.show();
                    return;
                }


                if(fNameOfSeller.getText().toString().isEmpty()
                        || lNameOfSeller.getText().toString().isEmpty()
                        || businessName.getText().toString().isEmpty()
                        || phoneNum.getText().toString().isEmpty() ){
                    Snackbar snackbar = Snackbar
                            .make(l, "Check that all field are filled", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
                else{
                    if(phoneNum.getText().toString().length()!=10){
                        Snackbar snackbar = Snackbar
                                .make(l, "Phone Num not valid, should be 10 digits", Snackbar.LENGTH_LONG);

                        snackbar.show();
                    }else{
                        SubmitFormTask submitFormTask = new SubmitFormTask();
                        submitFormTask.execute();
                    }


                }
            }
        });

    }



    TimingFrg timingFrg;
    FragmentTransaction timingTransaction;
    boolean isSelectTimingOpen = false;

    List<Timings> TimingList;

    @Override
    public void getTiming(List<Timings> strings) {

        timingTransaction = getSupportFragmentManager().beginTransaction();
        timingTransaction.remove(timingFrg);
        timingTransaction.commit();

        TimingList = strings;

        smallCheckTiming.setVisibility(View.VISIBLE);
        isSelectTimingOpen =false;

        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

    }

    GetLoc fragment;
    FragmentTransaction transaction;
    boolean isOpen = false;

    @Override
    public void someEvent(LatLng latLng) {
        Log.d(TAG, "someEvent: " + latLng.latitude);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.remove(fragment);
        transaction.commit();

        latitude = latLng.latitude;
        longitude = latLng.longitude;

        txtLatitude.setText(String.valueOf(latitude));
        txtLongitude.setText(String.valueOf(longitude));

        isOpen =false;
        btnGetLocationFromMap.setText("Location is Set");

        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

    }

    FormDetails formDetails;

    class SubmitFormTask extends AsyncTask<Void, ArrayList<String>, Void>{

        @Override
        protected void onPreExecute() {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            final String formattedDate = df.format(c);

            r = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
            formDetails = new FormDetails(
                    fNameOfSeller.getText().toString(),
                    lNameOfSeller.getText().toString(),
                    businessName.getText().toString(),
                    phoneNum.getText().toString(),
                    STAGE1,
                    latitude,
                    longitude,
                    formattedDate,
                    "Public",
                    TimingList,
                    0,
                    profilePicUri,
                    new ArrayList<InspectorClass>(),
                    (String) r.getText(),
                    null);

            sr = FirebaseStorage.getInstance().getReference();
            ref = sr.child("images/"+ fa.getCurrentUser().getUid());

        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(filePath != null)
            {
                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        formDetails.setProfilePicUri(uri.toString());
                                        publishProgress();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                    }
                                });



                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(FormActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                        .getTotalByteCount());
                            }
                        });
                return null;
            }
            else {
                publishProgress();
                return null;
            }

        }

        @Override
        protected void onProgressUpdate(ArrayList<String>... values) {
            dr.child("Pending").child(Objects.requireNonNull(fa.getCurrentUser()).getUid()).setValue(formDetails);
            dr.child("FormFilled").child(Objects.requireNonNull(fa.getCurrentUser()).getUid()).setValue(formDetails);

            if(USER_STATUS.equals("Denied")){
                dr.child("Denied").child(fa.getCurrentUser().getUid()).removeValue();
            }
            sendEmail();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent i = new Intent(FormActivity.this, ThankYou.class);
            i.putExtra("Name", fNameOfSeller.getText().toString());
            startActivity(i);
            finish();
        }


    }

    private void sendEmail() {
        //Getting content for email
        String email = "desaiparth974@gmail.com";
        String subject = "Pending: " + fa.getCurrentUser().getUid();
        String message = "Name of seller: " + fNameOfSeller.getText().toString() + lNameOfSeller.getText().toString()
                + "\nBusiness name: " + businessName.getText().toString();

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }

//    protected void startIntentService() {
//        Intent intent = new Intent(this, FetchAddressIntentService.class);
//        intent.putExtra(Constants.RECEIVER, resultReceiver);
//        intent.putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation);
//        startService(intent);
//    }
//    public void startSer(){
//        if (lastLocation == null) {
//            return;
//        }
//        if (!Geocoder.isPresent()) {
//            Toast.makeText(this,
//                    R.string.no_geocoder_available,
//                    Toast.LENGTH_LONG).show();
//            return;
//        }
//        // Start service and update UI to reflect new location
//        resultReceiver = new AddressResultReceiver(new Handler());
//        startIntentService();
//    }
//    class AddressResultReceiver extends ResultReceiver {
//        public AddressResultReceiver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//
//            if (resultData == null) {
//                return;
//            }
//
//            // Display the address string
//            // or an error message sent from the intent service.
//            String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
//            if (addressOutput == null) {
//                addressOutput = "";
//            }else if(addressOutput.equals(getString(R.string.service_not_available))){
//                addressOutput="";
//            }else if(addressOutput.equals(getString(R.string.invalid_lat_long_used))){
//                addressOutput="";
//            }
//            else if(addressOutput.equals(getString(R.string.no_address_found))){
//                addressOutput="";
//            }
//            Log.d(TAG, "ad: "+ addressOutput);
//            if(addressOutput.equals("")){
//                Snackbar snackbar = Snackbar
//                        .make(l, "Cannot find current Location", Snackbar.LENGTH_LONG);
//                snackbar.show();
//            }
//            else {
//                autocompleteFragment.setText(addressOutput);
//                autocompleteFragmentText = addressOutput;
//            }
//
//
//            // Show a toast message if an address was found.
//            if (resultCode == Constants.SUCCESS_RESULT) {
//                Log.d(TAG, "onReceiveResult: "+ getString(R.string.address_found));
//            }
//
//        }
//    }


    @Override
    public void onBackPressed() {

        if(isOpen){
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            isOpen =false;
        }
        else if(isSelectTimingOpen){
            timingTransaction = getSupportFragmentManager().beginTransaction();
            timingTransaction.remove(timingFrg);
            timingTransaction.commit();
            toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            isSelectTimingOpen = false;
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.form_activity_menubar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.cancel_form){
            dr.child(USER_STATUS).child(fa.getCurrentUser().getUid()).removeValue();
            dr.child("FormFilled").child(fa.getCurrentUser().getUid()).removeValue();
//                        Intent i = new Intent(FormActivity.this, CancelRequest.class);
//                        i.putExtra("formDetails",currentUserDetails );
//                        startActivity(i);
//                        finish();
////                        dr.child("Approved").child(Objects.requireNonNull(fa.getCurrentUser()).getUid()).removeValue();
////                        dr.child("FormFilled").child(fa.getCurrentUser().getUid()).removeValue();
////                        dr.child("Reviews").child(fa.getCurrentUser().getUid()).removeValue();
            finish();
            return true;
        }
        return false;
    }


    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);

        Glide.with(this).load(url)
                .into(imgProfile);
        imgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }


    public void onProfileImageClick(View v) {
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
        Intent intent = new Intent(FormActivity.this, ImagePickerActivity.class);
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
        Intent intent = new Intent(FormActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    Uri filePath;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    filePath = data.getParcelableExtra("path");

                    try {
                        Log.d(TAG, "onActivityResult: ");
                        // You can update this bitmap to your server
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);

                        // loading profile image from local cache
                        loadProfile(filePath.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                Log.d(TAG, "onActivityResult: default");

        }

    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FormActivity.this);
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
