package com.example.aproncertified;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

import java.util.List;

public class CheckUpApproval extends AppCompatActivity {

    private static final String TAG = "CheckUpApproval";
    
    String myItemKey;
    
    ImageButton addCheckupApproval;
    RecyclerView CheckupRecyclerView;
    TextView passed, percent;

    FirebaseAuth fa;
    DatabaseReference dr;

    List<InspectorClass> inspectorClassList;

    boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_up_approval);

        myItemKey =  getIntent().getStringExtra("myItemKey");

        fa = FirebaseAuth.getInstance();
        dr = FirebaseDatabase.getInstance().getReference();

        addCheckupApproval = (ImageButton) findViewById(R.id.addCheckupApproval);
        CheckupRecyclerView =(RecyclerView) findViewById(R.id.CheckupRecyclerView);
        passed =(TextView) findViewById(R.id.passed);
        percent =(TextView) findViewById(R.id.percent);

        first = false;
        setRecyclerView();

        addCheckupApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CheckUpApproval.this, AddCheckupApproval.class);
                i.putExtra("userId", myItemKey);
                startActivity(i);
            }
        });

    }
    public void setRecyclerView(){
        dr.child("Approved").child(myItemKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FormDetails f = dataSnapshot.getValue(FormDetails.class);
                inspectorClassList = f.getInspectorList();
                int temp = 0;
                for(InspectorClass i : inspectorClassList){
                    int num =  strParser(i.getStrChefMarks())
                            + strParser(i.getStrFoodMarks())
                            + strParser(i.getStrLocationMarks());
                    temp = temp + (num/3);
                }
                percent.setText((temp*10)/inspectorClassList.size()+"%");
                if((temp*10)/inspectorClassList.size()>=80){
                    passed.setText("Passed");
                }
                else {
                    passed.setText("Failed");
                }

                CheckupApprovalAdapter checkupApprovalAdapter = new CheckupApprovalAdapter(getApplicationContext(),inspectorClassList ) ;
                CheckupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                CheckupRecyclerView.setAdapter(checkupApprovalAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public int strParser(String s){
        String temp;
        temp = s.replace("/10", "");
        return Integer.valueOf(temp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!first){
            setRecyclerView();
        }
    }
}
