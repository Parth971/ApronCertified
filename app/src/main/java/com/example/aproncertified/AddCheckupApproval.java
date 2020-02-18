package com.example.aproncertified;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class AddCheckupApproval extends AppCompatActivity {

    private static final String TAG = "AddCheckupApproval";

    Button saveCheckupApproval;
    Spinner locationMarks, foodMarks, chefMarks;
    EditText inspectionComment;
    ImageButton deleteCheckupForm;
    TextView nameOfInspector, idOfInspector;

    List<InspectorClass> tempList;

    String userId, inspectorId, inspectorName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_checkup_approval);

        saveCheckupApproval = (Button) findViewById(R.id.saveCheckupApproval);
        locationMarks = (Spinner) findViewById(R.id.locationMarks);
        foodMarks = (Spinner) findViewById(R.id.foodMarks);
        chefMarks = (Spinner) findViewById(R.id.chefMarks);
        inspectionComment = (EditText) findViewById(R.id.inspectionComment);
        deleteCheckupForm = (ImageButton) findViewById(R.id.deleteCheckupForm);
        nameOfInspector = (TextView) findViewById(R.id.nameOfInspector);
        idOfInspector = (TextView) findViewById(R.id.idOfInspector);

        userId = getIntent().getStringExtra("userId");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ranks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        locationMarks.setAdapter(adapter);
        foodMarks.setAdapter(adapter);
        chefMarks.setAdapter(adapter);

        FirebaseAuth fa = FirebaseAuth.getInstance();
        final DatabaseReference dr = FirebaseDatabase.getInstance().getReference();

        dr.child("Users").child(fa.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inspectorId = dataSnapshot.getKey();
                inspectorName = dataSnapshot.getValue(Details.class).getfName() + " " + dataSnapshot.getValue(Details.class).getlName();

                nameOfInspector.setText(inspectorName);
                idOfInspector.setText(inspectorId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.toString());
            }
        });

        deleteCheckupForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveCheckupApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strLocationMarks, strFoodMarks, strChefMarks, strInspectionComment, date;

                strLocationMarks = locationMarks.getSelectedItem().toString();
                strFoodMarks = foodMarks.getSelectedItem().toString();
                strChefMarks = chefMarks.getSelectedItem().toString();
                strInspectionComment = inspectionComment.getText().toString();

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                date = df.format(c);


                dr.child("Approved").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FormDetails f =  dataSnapshot.getValue(FormDetails.class);
                        tempList = f.getInspectorList();
                        if(tempList!=null){
                            tempList.add(new InspectorClass(strLocationMarks, strFoodMarks, strChefMarks,
                                    strInspectionComment, date, inspectorId, inspectorName));
                        }
                        else {
                            tempList = new ArrayList<>();
                            tempList.add(new InspectorClass(strLocationMarks, strFoodMarks, strChefMarks,
                                    strInspectionComment, date, inspectorId, inspectorName));
                        }

                        f.setInspectorList(tempList);

                        dr.child("Approved").child(userId).setValue(f);
                        dr.child("FormFilled").child(userId).setValue(f);

                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: "+ databaseError.toString());
                    }
                });

            }
        });
    }
}
