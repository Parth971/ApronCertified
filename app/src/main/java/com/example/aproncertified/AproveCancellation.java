package com.example.aproncertified;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AproveCancellation extends AppCompatActivity {

    private static final String TAG = "Admin";
    FirebaseAuth fa;
    FirebaseDatabase fd;
    DatabaseReference dr;

    RecyclerView approveCanecellationRecyclerView;

    List<FormDetails> formDetails;
    List<CancelApprovalClass> cancelApprovalClasses;
    List<String> ids;

    Boolean ans;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(AproveCancellation.this);
        progressDialog.setMessage("Please wait");
        progressDialog.setTitle("Loading");
        progressDialog.show();

        fa = FirebaseAuth.getInstance();
        fd = FirebaseDatabase.getInstance();
        dr = fd.getReference();


        formDetails = new ArrayList<>();
        cancelApprovalClasses = new ArrayList<>();
        ids = new ArrayList<>();


    }


    class A extends AsyncTask<Void, Void,Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dr.child("CancelApproval").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    formDetails.clear();
                    cancelApprovalClasses.clear();
                    ids.clear();

                    for(DataSnapshot i : dataSnapshot.getChildren()){

                        String key = i.getKey();
                        ids.add(key);

                        CancelApprovalClass f = i.getValue(CancelApprovalClass.class);
                        formDetails.add(f.getFormDetails());

                        cancelApprovalClasses.add(f);

                    }

                    if(formDetails.isEmpty()){
                        setContentView(R.layout.no_list_available);
                    }else{
                        setContentView(R.layout.activity_aprove_cancellation);
                        approveCanecellationRecyclerView = (RecyclerView) findViewById(R.id.approveCanecellationRecyclerView);
                        AproveCancellationAdapter adminAdapter = new AproveCancellationAdapter(AproveCancellation.this, formDetails);
                        approveCanecellationRecyclerView.setLayoutManager(new LinearLayoutManager(AproveCancellation.this));
                        approveCanecellationRecyclerView.setAdapter(adminAdapter);
                        adminAdapter.setOnItemClicked(new AproveCancellationAdapter.OnItemClicked() {
                            @Override
                            public void goToItem(int position) {
                                Intent i = new Intent(AproveCancellation.this, CancelOrNot.class);

                                i.putExtra("cancelApprovalClasses", cancelApprovalClasses.get(position));
                                i.putExtra("key", ids.get(position));

                                startActivity(i);
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(AproveCancellation.this, "Error: " + databaseError.getDetails(), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AproveCancellation.A().execute();
    }
}
