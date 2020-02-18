package com.example.aproncertified;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CancelOrNot extends AppCompatActivity {

    TextView sName, bName, location, phone, date, reason;
    String currentkey;

    Button btnNotSeen, btnCancelationApproved, btnCancellationDenied;

    FirebaseDatabase fd;
    DatabaseReference dr;

    CancelApprovalClass cancelApprovalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_or_not);


        btnCancelationApproved = (Button) findViewById(R.id.btnCancelationApproved);
        btnCancellationDenied = (Button) findViewById(R.id.btnCancellationDenied);

        sName = (TextView) findViewById(R.id.sName);
        bName = (TextView) findViewById(R.id.bName);
//        desc = (TextView) findViewById(R.id.desc);
        location = (TextView) findViewById(R.id.location);
        phone = (TextView) findViewById(R.id.phone);
        date = (TextView) findViewById(R.id.date);
        reason  = (TextView) findViewById(R.id.reason);


        cancelApprovalClass =  (CancelApprovalClass) getIntent().getSerializableExtra("cancelApprovalClasses");
        currentkey = getIntent().getStringExtra("key");

        sName.setText(cancelApprovalClass.getFormDetails().getfNameOfSeller() + " " + cancelApprovalClass.getFormDetails().getlNameOfSeller());
        bName.setText(cancelApprovalClass.getFormDetails().getBusinessName());
        phone.setText(cancelApprovalClass.getFormDetails().getPhoneNum());
        date.setText(cancelApprovalClass.getFormDetails().getDate());
        reason.setText(cancelApprovalClass.getReason());

        fd = FirebaseDatabase.getInstance();
        dr = fd.getReference();

    }

    public void approved(View view) {
//        f.setStatus("Approved");
//        CustomerStatus.ApproveDenie approveDenie = new CustomerStatus.ApproveDenie();
//        approveDenie.execute("Approved");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        dr.child("CanceledForms").child(currentkey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dr.child("CanceledForms").child(currentkey)
                        .child(String.valueOf(dataSnapshot.getChildrenCount()+1)).setValue(cancelApprovalClass);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dr.child("CancelApproval").child(currentkey).removeValue();
        dr.child("Approved").child(currentkey).removeValue();
        dr.child("FormFilled").child(currentkey).removeValue();
        dr.child("Reviews").child(currentkey).removeValue();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                finish();
            }
        }, 3000);

    }

    public void denied(View view) {
//        f.setStatus("Denied");
//        CustomerStatus.ApproveDenie approveDenie = new CustomerStatus.ApproveDenie();
//        approveDenie.execute("Denied");

    }

}
