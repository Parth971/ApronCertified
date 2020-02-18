package com.example.aproncertified;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class InspectorTasks extends AppCompatActivity implements InspectorTaskAdapter.OnItemClicked {

    private static final String TAG = "Admin";
    FirebaseAuth fa;
    FirebaseDatabase fd;
    DatabaseReference dr;

    RecyclerView adminRecyclerView;

    List<FormDetails> formDetails;
    List<String> userId;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        progressDialog = new ProgressDialog(InspectorTasks.this);
        progressDialog.setMessage("Please wait");
        progressDialog.setTitle("Loading");
        progressDialog.show();

        fa = FirebaseAuth.getInstance();
        fd = FirebaseDatabase.getInstance();
        dr = fd.getReference();


        formDetails = new ArrayList<>();
        userId = new ArrayList<>();


    }

    @Override
    public void goToItem(int position) {
        Intent i = new Intent(InspectorTasks.this, CustomerStatus.class);
        i.putExtra("obj", formDetails.get(position));
        i.putExtra("key", userId.get(position));

        startActivity(i);
    }

    InspectorTaskAdapter adminAdapter;
    class A extends AsyncTask<Void, FormDetails ,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(FormDetails... values) {
            formDetails.add(values[0]);
            adminRecyclerView.setAdapter(adminAdapter);
        }

        @Override
        protected Void doInBackground(Void... vouserId) {
            dr.child("InspectorWork").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount()!=0){
                        setContentView(R.layout.activity_inspector_task);
                        adminRecyclerView = (RecyclerView) findViewById(R.id.adminRecyclerView);

                        adminAdapter = new InspectorTaskAdapter(InspectorTasks.this, formDetails);
                        adminRecyclerView.setLayoutManager(new LinearLayoutManager(InspectorTasks.this));
                        adminAdapter.setOnItemClicked(InspectorTasks.this);

                        formDetails.clear();
                        userId.clear();

                        for(DataSnapshot i : dataSnapshot.getChildren()){
                            dr.child("Pending").child(i.getValue(String.class)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getChildrenCount()!=0){
                                        userId.add(dataSnapshot.getKey());
                                        onProgressUpdate(dataSnapshot.getValue(FormDetails.class));
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d(TAG, "onCancelled: " + databaseError.toString());
                                }
                            });
                        }

                    }
                    else {
                        setContentView(R.layout.no_list_available);
                    }



                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(InspectorTasks.this, "Error: " + databaseError.getDetails(), Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new A().execute();
    }
}
