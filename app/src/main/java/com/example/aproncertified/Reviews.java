package com.example.aproncertified;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Reviews extends AppCompatActivity implements ReviewsAdapter.ReviewClickListener {

//    implements ReviewsAdapter.ReviewClickListener, PhotosPreviewAdapter.PhotoListener
    private static final String TAG = "Reviews";

    RatingBar ratingBar;
    EditText edtfeedBack;
    Button btnOk;
    RecyclerView reviewView;
    RecyclerView photosToUploadRecycleView;

    String MarkersId;
    String currentUser;
    float oldRating;

    boolean isReReviewer = false;

    List<ReviewClass> reviewClassList;
    List<String> keys;

    DatabaseReference dr;
    FirebaseAuth fa;
    StorageReference sr;

    ReviewsAdapter mReviewsAdapter;
    PhotosPreviewAdapter photosPreviewAdapter;

    String name;
    String date;
    List<String> ReviewerImagesUris;
    List<String> storageImageUris;
    List<Uri> ImageUris;


    public static final int REQUEST_IMAGE = 100;


    ProgressDialog progressDialog;

    String reviewContent;
    float rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        MarkersId = getIntent().getStringExtra("MarkersId");

        ratingBar = findViewById(R.id.ratingBar);
        edtfeedBack = findViewById(R.id.edtfeedBack);
        btnOk = findViewById(R.id.btnOk);
        reviewView = findViewById(R.id.reviewView);
        photosToUploadRecycleView = findViewById(R.id.photosToUpload);


        progressDialog = new ProgressDialog(this);


        reviewClassList = new ArrayList<>();
        keys = new ArrayList<>();
        ReviewerImagesUris = new ArrayList<>();
        storageImageUris = new ArrayList<>();
        ImageUris = new ArrayList<>();

        fa = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        photosToUploadRecycleView.setVisibility(View.GONE);

        photosPreviewAdapter = new PhotosPreviewAdapter(this, ImageUris);
        photosPreviewAdapter.OnclickListener(new PhotosPreviewAdapter.PhotoListener() {
            @Override
            public void onRemovePhoto(int position) {
                ImageUris.remove(position);
                Log.d(TAG, "onRemovePhoto: " + ImageUris.size());
                photosPreviewAdapter.notifyDataSetChanged();
                if (ImageUris.isEmpty()) {
                    photosToUploadRecycleView.setVisibility(View.GONE);
                }

            }
        });
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        photosToUploadRecycleView.setLayoutManager(horizontalLayoutManager);
        photosToUploadRecycleView.setAdapter(photosPreviewAdapter);



//        reReview();

        dr.child("Users").child(fa.getCurrentUser().getUid()).child("fName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                date = df.format(c);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dr.child("Reviews").child(MarkersId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewClassList.clear();
                keys.clear();

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ReviewClass r = d.getValue(ReviewClass.class);

                    reviewClassList.add(r);
                    keys.add(d.getKey());

                    if(d.getKey().equals(fa.getCurrentUser().getUid())){
                        ratingBar.setRating(r.getRating());
                        edtfeedBack.setText(r.getFeedback());
                        storageImageUris = r.getReviewImagesUri();
                        if(storageImageUris!=null){
                            makeFiles();
                        }
//                        if(r.getReviewImagesUri()!=null){
//                            storageImageUris = r.getReviewImagesUri();
//
//                            photosPreviewAdapter = new PhotosPreviewAdapter(Reviews.this, storageImageUris);
//
//                            photosPreviewAdapter.OnclickListener(Reviews.this);
//                            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(Reviews.this, LinearLayoutManager.HORIZONTAL, false);
//                            photosToUploadRecycleView.setLayoutManager(horizontalLayoutManager);
//                            photosToUploadRecycleView.setAdapter(photosPreviewAdapter);
//                            photosToUploadRecycleView.setVisibility(View.VISIBLE);
//                        }

                    }

                }
                mReviewsAdapter = new ReviewsAdapter(reviewClassList, keys, fa.getCurrentUser().getUid(), Reviews.this);
                reviewView.setLayoutManager(new LinearLayoutManager(Reviews.this));
                mReviewsAdapter.OnclickListener(Reviews.this);
                mReviewsAdapter.notifyDataSetChanged();
                reviewView.setAdapter(mReviewsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError);
            }
        });


        ImagePickerActivity.clearCache(this);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MarkersId.equals(fa.getCurrentUser().getUid())){
                    Toast.makeText(Reviews.this, "Cannot Give Review To yourself", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(ratingBar.getRating() != 0.0){

//                    save to firebase storage
                    progressDialog.setMessage("Uploading...");
                    progressDialog.setCancelable(false);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setProgress(0);
                    progressDialog.show();

                    sr = FirebaseStorage.getInstance().getReference();
                    Log.d(TAG, "onClick: " + ImageUris.size());

                    if(ImageUris.size()==0){
                        removeItem(0);
                        dr = FirebaseDatabase.getInstance().getReference();
                        dr.child("Reviews").child(MarkersId).child(fa.getCurrentUser().getUid()).setValue(new ReviewClass(
                                ratingBar.getRating(),
                                edtfeedBack.getText().toString(),
                                name, date,null))

                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        progressDialog.dismiss();

                                        ratingBar.setRating(0);
                                        edtfeedBack.setText("");
                                        storageImageUris.clear();
                                        photosToUploadRecycleView.setVisibility(View.GONE);

                                        dr.child("Reviews").child(MarkersId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                long totalReviews = dataSnapshot.getChildrenCount();
                                                float totalRating = 0;
                                                for(DataSnapshot d : dataSnapshot.getChildren()){
                                                    totalRating = totalRating + d.getValue(ReviewClass.class).getRating();
                                                }
                                                if(totalReviews!=0){
                                                    dr.child("Approved").child(MarkersId).child("ratings").setValue(totalRating/totalReviews);
                                                    dr.child("FormFilled").child(MarkersId).child("ratings").setValue(totalRating/totalReviews);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.d(TAG, "onCancelled: " + databaseError.toString() );
                                            }
                                        });
                                    }
                                });
                    }
                    else {
                        removeItemFromStorage(0);
                    }

                }
                else {
                    Toast.makeText(Reviews.this, "Rating is not given", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
    private void makeFiles(){
        sr = FirebaseStorage.getInstance().getReference();
        clearCache(Reviews.this);
        getFilesDownloaded(0);

        photosToUploadRecycleView.setVisibility(View.VISIBLE);

    }
    private void getFilesDownloaded(final int i){
        File file = getCacheImagePath(i+".jpg");
        final Uri u = Uri.fromFile(file);
        StorageReference referenceFromUrl = FirebaseStorage.getInstance().getReferenceFromUrl(storageImageUris.get(i));
        referenceFromUrl.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: " + u.toString());
                ImageUris.add(u);
                photosPreviewAdapter.notifyDataSetChanged();

                if(i == storageImageUris.size()-1){
                    Log.d(TAG, "onSuccess: all loaded");
                }
                else {
                    getFilesDownloaded(i+1);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
            }
        });
    }
    public static void clearCache(Context context) {
        File path = new File(context.getExternalCacheDir(), "camera");
        if (path.exists() && path.isDirectory()) {
            for (File child : path.listFiles()) {
                child.delete();
                Log.d(TAG, "clearCache: ");
            }
        }
    }
    private File getCacheImagePath(String fileName) {

        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        Log.d(TAG, "getCacheImagePath: "+image);
        return image;
    }

    void removeItemFromStorage(final int i){
        if(storageImageUris==null){
            storageImageUris = new ArrayList<>();
            addItem(0);
            Log.d(TAG, "removeItemFromStorage: ");
            return;
        }
        Log.d(TAG, "removeItemFromStorage: " + storageImageUris.size());
        StorageReference paths = sr.child("reviewPhotos").child(fa.getCurrentUser().getUid()).child(i + ".jpg");
        paths.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "yeah, deleted: ");
                if(i == storageImageUris.size()-1){
                    Log.d(TAG, "all deleted: ");
                    storageImageUris.clear();
                    addItem(0);
                }
                else {
                    removeItemFromStorage(i+1);
                }
            }
        });
    }
    private void addItem(final int i) {
        Log.d(TAG, "addItem: ");
        final StorageReference paths = sr.child("reviewPhotos").child(fa.getCurrentUser().getUid()).child(i + ".jpg");

        paths.putFile(ImageUris.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.setProgress(0);
                paths.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        storageImageUris.add(uri.toString());
                        Log.d(TAG, "onSuccess:" + storageImageUris.size());
                        Log.d(TAG, "onSuccess: " + uri.toString());
                        if(storageImageUris.size() == ImageUris.size()){

                            progressDialog.dismiss();

                            dr = FirebaseDatabase.getInstance().getReference();
                            dr.child("Reviews").child(MarkersId).child(fa.getCurrentUser().getUid()).setValue(new ReviewClass(
                                    ratingBar.getRating(),
                                    edtfeedBack.getText().toString(),
                                    name, date,storageImageUris))

                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            ratingBar.setRating(0);
                                            edtfeedBack.setText("");
                                            photosToUploadRecycleView.setVisibility(View.GONE);

                                            dr.child("Reviews").child(MarkersId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    long totalReviews = dataSnapshot.getChildrenCount();
                                                    float totalRating = 0;
                                                    for(DataSnapshot d : dataSnapshot.getChildren()){
                                                        totalRating = totalRating + d.getValue(ReviewClass.class).getRating();
                                                    }
                                                    if(totalReviews!=0){
                                                        dr.child("Approved").child(MarkersId).child("ratings").setValue(totalRating/totalReviews);
                                                        dr.child("FormFilled").child(MarkersId).child("ratings").setValue(totalRating/totalReviews);
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    Log.d(TAG, "onCancelled: " + databaseError.toString() );
                                                }
                                            });
                                        }
                                    });
                        }else {
                            addItem(i+1);
                        }
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.setProgress((int) (100 * (taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount())));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "  + e.getMessage());
            }
        });
    }

    void removeItem(final int i){
        StorageReference paths = sr.child("reviewPhotos").child(fa.getCurrentUser().getUid()).child(i + ".jpg");
        paths.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "yeah, deleted: ");
                if(i == storageImageUris.size()-1){
                    Log.d(TAG, "all deleted: ");
                }
                else {
                    removeItem(i+1);
                }
            }
        });
    }

    @Override
    public void onRemoveItemClicked(final int position) {
        sr = FirebaseStorage.getInstance().getReference();

        removeItem(0);

        ratingBar.setRating(0);
        edtfeedBack.setText("");
        if(storageImageUris!=null){
            storageImageUris.clear();
        }

        photosToUploadRecycleView.setVisibility(View.GONE);

        dr.child("Reviews").child(MarkersId).child(keys.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                dr.child("Reviews").child(MarkersId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long totalReviews = dataSnapshot.getChildrenCount();
                        float totalRating = 0;
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            totalRating = totalRating + d.getValue(ReviewClass.class).getRating();
                        }
                        if(totalReviews!=0){
                            dr.child("Approved").child(MarkersId).child("ratings").setValue(totalRating/totalReviews);
                            dr.child("FormFilled").child(MarkersId).child("ratings").setValue(totalRating/totalReviews);
                        }
                        else {
                            dr.child("Approved").child(MarkersId).child("ratings").setValue(0);
                            dr.child("FormFilled").child(MarkersId).child("ratings").setValue(0);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
        Intent intent = new Intent(Reviews.this, ImagePickerActivity.class);
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
        Intent intent = new Intent(Reviews.this, ImagePickerActivity.class);
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
                    Uri uri = data.getParcelableExtra("path");

                    photosToUploadRecycleView.setVisibility(View.VISIBLE);
                    ImageUris.add(uri);
                    photosPreviewAdapter.notifyDataSetChanged();

                    Log.d(TAG, "onActivityResult: " + uri.toString());


                }
                else {
                    Log.d(TAG, "onActivityResult: result cancelled");
                }
                break;
            default:
                Log.d(TAG, "onActivityResult: default");

        }

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Reviews.this);
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

    class SubmitReview extends AsyncTask<Void, List<String>, Void> {
        @Override
        protected void onPreExecute() {


        }

        @Override
        protected Void doInBackground(Void... voids) {



            return null;
        }

        @Override
        protected void onProgressUpdate(final List<String>... values) {





        }
    }

}
