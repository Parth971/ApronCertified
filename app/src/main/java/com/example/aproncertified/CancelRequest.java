package com.example.aproncertified;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CancelRequest extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "CancelRequest";

    Button cancelCerti;
    Spinner spinner;
    EditText FreeText;

    FormDetails formDetails;

    String strReason;

    FormDetails myItemFormdetails;
    String myItemKey;
    List<ReviewDataSnapshot> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_request);

        cancelCerti = (Button) findViewById(R.id.cancelCerti);
        spinner = (Spinner) findViewById(R.id.dropdownReason);
        FreeText = (EditText) findViewById(R.id.FreeText);

        myItemFormdetails = (FormDetails) getIntent().getSerializableExtra("myItemFormdetails");
        myItemKey =  getIntent().getStringExtra("myItemKey");
        list = (List<ReviewDataSnapshot>) getIntent().getSerializableExtra("list");

        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        final DatabaseReference dr = fd.getReference();
        final FirebaseAuth fa = FirebaseAuth.getInstance();

        formDetails = (FormDetails) getIntent().getSerializableExtra("formDetails");

        spinner.setOnItemSelectedListener(this);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, REASONS);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        cancelCerti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tempReason;
                if (!strReason.equals("Others")){
                    tempReason = strReason;
                }
                else {
                    tempReason = FreeText.getText().toString();
                }
                if(!tempReason.equals("")){
                    if(fa.getCurrentUser().getUid().equals("jjPsZ3cIP3O0jEZwDvTOK4vBEbv1") || fa.getCurrentUser().getUid().equals("m0GQzz2q5BOWds4xu1CzX2j6wOB3") ){
                        dr.child("CanceledForms").child(myItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dr.child("CanceledForms").child(myItemKey)
                                        .child(String.valueOf(dataSnapshot.getChildrenCount()+1)).setValue(new CancelApprovalClass(myItemFormdetails, tempReason, list));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        dr.child("Approved").child(myItemKey).removeValue();
                        dr.child("FormFilled").child(myItemKey).removeValue();
                        dr.child("Reviews").child(myItemKey).removeValue();

                        finish();
                    }
                    else {
                        dr.child("Reviews").child(fa.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            List<ReviewDataSnapshot> list = new ArrayList<>();
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for(DataSnapshot d : dataSnapshot.getChildren()){
                                    list.add(new ReviewDataSnapshot(dataSnapshot.getKey(), d.getValue(ReviewClass.class)));
                                }

                                dr.child("CancelApproval").child(fa.getCurrentUser().getUid()).setValue(new CancelApprovalClass(formDetails, tempReason, list));
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: " + databaseError);
                            }
                        });
                    }
                }







//                dr.child("Approved").child(fa.getCurrentUser().getUid()).removeValue();
//                dr.child("FormFilled").child(fa.getCurrentUser().getUid()).removeValue();
//                dr.child("Reviews").child(fa.getCurrentUser().getUid()).removeValue();


            }
        });

    }
    private static final String[] REASONS = new String[] {
            "LOL", "France", "Italy", "Germany", "Spain", "Others"
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),REASONS[position] , Toast.LENGTH_SHORT).show();
        if(REASONS[position].equals("Others")){
            FreeText.setVisibility(View.VISIBLE);
        }
        else {
            FreeText.setVisibility(View.GONE);
        }
        strReason = REASONS[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),"Nothing Selected" , Toast.LENGTH_SHORT).show();
    }
}
