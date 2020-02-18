package com.example.aproncertified;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity implements AdminAdapter.OnItemClicked {

    private static final String TAG = "Admin";
    FirebaseAuth fa;
    FirebaseDatabase fd;
    DatabaseReference dr;

    RecyclerView adminRecyclerView;
    TextView selectedInspector;
    Button select;
    Button save;

    List<FormDetails> formDetails;
    List<String> userId;
    List<String> InspectorId;
    List<String> newIdList;



    Boolean ans;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        progressDialog = new ProgressDialog(Admin.this);
        progressDialog.setMessage("Please wait");
        progressDialog.setTitle("Loading");
        progressDialog.show();

        fa = FirebaseAuth.getInstance();
        fd = FirebaseDatabase.getInstance();
        dr = fd.getReference();


        formDetails = new ArrayList<>();
        userId = new ArrayList<>();
        InspectorId = new ArrayList<>();
        newIdList = new ArrayList<>();


    }

    @Override
    public void goToItem(int position) {
        Intent i = new Intent(Admin.this, CustomerStatus.class);
        i.putExtra("obj", formDetails.get(position));
        i.putExtra("key", userId.get(position));

        startActivity(i);
    }

    @Override
    public void addItemToList(int position) {
        newIdList.add(userId.get(position));
        for(String s : newIdList){
            Log.d(TAG, s);
        }
    }

    @Override
    public void removeItemFromList(int position) {
        newIdList.remove(userId.get(position));
    }


    int checkedPosition = -1;
    List<Details> list = new ArrayList<>();
    class A extends AsyncTask<Void, Void,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... vouserId) {
            dr.child("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    formDetails.clear();
                    userId.clear();

                    for(DataSnapshot i : dataSnapshot.getChildren()){
                        String key = i.getKey();
                        FormDetails f = i.getValue(FormDetails.class);

                        userId.add(key);
                        formDetails.add(f);
                    }

                    if(formDetails.isEmpty()){
                        setContentView(R.layout.no_list_available);
                    }else{
                        setContentView(R.layout.activity_admin);

                        adminRecyclerView = (RecyclerView) findViewById(R.id.adminRecyclerView);
                        select = (Button) findViewById(R.id.select);
                        save = (Button) findViewById(R.id.save);
                        selectedInspector = (TextView) findViewById(R.id.selectedInspector);

                        AdminAdapter adminAdapter = new AdminAdapter(Admin.this, formDetails, userId);
                        adminRecyclerView.setLayoutManager(new LinearLayoutManager(Admin.this));
                        adminRecyclerView.setAdapter(adminAdapter);
                        adminAdapter.setOnItemClicked(Admin.this);

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(checkedPosition!=-1 && !newIdList.isEmpty()) {
                                    final DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
                                    dr.child("InspectorWork").child(InspectorId.get(checkedPosition)).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getChildren()!=null){
                                                for(DataSnapshot d : dataSnapshot.getChildren()){
                                                    newIdList.add(d.getValue(String.class));
                                                    Toast.makeText(Admin.this, d.getValue(String.class), Toast.LENGTH_SHORT).show();
                                                }
                                                dr.child("InspectorWork").child(InspectorId.get(checkedPosition)).setValue(newIdList).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        for(String s : newIdList){
                                                            dr.child("Pending").child(s).child("inspectorAssigned").setValue(InspectorId.get(checkedPosition));
                                                            dr.child("FormFilled").child(s).child("inspectorAssigned").setValue(InspectorId.get(checkedPosition));
                                                        }
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                                    }
                                                });
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                                else {
                                    Toast.makeText(Admin.this, "Either inspector or pending_form not selected", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        select.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(Admin.this);
                                View view = LayoutInflater.from(Admin.this).inflate(R.layout.inspectors_list, null);

                                final RecyclerView inspectorsRecyclerView = (RecyclerView) view.findViewById(R.id.inspectorsRecyclerView);
                                inspectorsRecyclerView.setLayoutManager(new LinearLayoutManager(Admin.this));

                                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(checkedPosition!=-1){
                                            Toast.makeText(Admin.this, list.get(checkedPosition).getfName(), Toast.LENGTH_SHORT).show();
                                            selectedInspector.setText("Selected Inspector: " + list.get(checkedPosition).getfName() + " " +
                                                    list.get(checkedPosition).getlName() );
                                        }
                                        else {
                                            Toast.makeText(Admin.this, "Nothing Selected", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                alert.setView(view);
                                final AlertDialog dialog = alert.create();
                                dr = FirebaseDatabase.getInstance().getReference();
                                dr.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        list.clear();
                                        InspectorId.clear();
                                        for(DataSnapshot d : dataSnapshot.getChildren()){
                                            Details details = d.getValue(Details.class);
                                            if(details.getUserType().equals("Inspector")){
                                                list.add(details);
                                                InspectorId.add(d.getKey());
                                            }
                                        }
                                        if(list.isEmpty()){
                                            Toast.makeText(Admin.this, "No inspectors", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        else {
                                            dialog.show();
                                        }

                                        InspectorAdapter adapter = new InspectorAdapter(Admin.this, list);
                                        adapter.setOnItemChecked(new InspectorAdapter.OnItemChecked() {
                                            @Override
                                            public void itemChecked(int position) {
                                                checkedPosition = position;
                                            }
                                        });
                                        inspectorsRecyclerView.setAdapter(adapter);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(Admin.this, "Error: " + databaseError.getDetails(), Toast.LENGTH_SHORT).show();
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
