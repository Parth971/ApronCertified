package com.example.aproncertified;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button btnSignIn, btnRegister;
    RelativeLayout rootLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
////        --------------------------Dialogbox----------------------
//
//        final android.app.AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(MainActivity.this).build();
//        waitingDialog.show();
//        waitingDialog.dismiss();
//
////        ----------------------------------------------------------


        init();

        firebaseAuth = FirebaseAuth.getInstance();

        //Event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });


    }

    private void init(){
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
    }


//    --------------------------------------------------------------
    private void showRegisterDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register");
        dialog.setMessage("Please use email to Register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register,null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtFName = register_layout.findViewById(R.id.edtFName);
        final MaterialEditText edtLName = register_layout.findViewById(R.id.edtLName);
        final MaterialEditText edtPhone = register_layout.findViewById(R.id.edtPhone);



        dialog.setView(register_layout);
        dialog.setPositiveButton("Register", null);
        dialog.setNegativeButton("Cancel", null);

        final AlertDialog mAlertDialog = dialog.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button pos = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int flag = 0;

                        //Check Validaation
                        if(TextUtils.isEmpty(edtEmail.getText().toString()) || !edtEmail.getText().toString().contains("@")){
                            edtEmail.setText("");
                            edtEmail.setHint( "Please enter Proper Email");
                            flag = 1;
                        }
                        if(TextUtils.isEmpty(edtFName.getText().toString())){
                            edtFName.setText("");
                            edtFName.setHint("Please enter Proper First Name");
                            flag = 1;
                        }
                        if(TextUtils.isEmpty(edtLName.getText().toString())){
                            edtLName.setText("");
                            edtLName.setHint("Please enter Proper Last Name");
                            flag = 1;
                        }
                        if(TextUtils.isEmpty(edtPhone.getText().toString())){
                            edtPhone.setText("");
                            edtPhone.setHint("Please enter Proper Phone Number");
                            flag = 1;
                        }
                        if(edtPhone.getText().toString().length() != 10){
                            edtPhone.setText("");
                            edtPhone.setHint("Please enter Phone Number of 10 digits Only");
                            flag = 1;
                        }
                        if(TextUtils.isEmpty(edtPassword.getText().toString())){
                            edtPassword.setText("");
                            edtPassword.setHint("Please enter Password");
                            flag = 1;
                        }
                        if(( edtPassword.getText().toString().length() ) < 6){
                            edtPassword.setText("");
                            edtPassword.setHint("Passsword too short!");
                            flag = 1;
                        }
                        if(flag == 0){
                            final android.app.AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(MainActivity.this).build();
                            waitingDialog.show();

                            mAlertDialog.dismiss();
                            firebaseAuth.createUserWithEmailAndPassword(edtEmail.getText().toString().trim(),edtPassword.getText().toString().trim())
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                Details details;
                                                if(edtEmail.getText().toString().equals("desaiparth971@gmail.com")){
                                                    details = new Details(edtFName.getText().toString(),
                                                            edtLName.getText().toString(),
                                                            edtEmail.getText().toString(),
                                                            edtPassword.getText().toString(),
                                                            edtPhone.getText().toString(),
                                                            null,
                                                            "Admin");
                                                }
                                                else {
                                                    details = new Details(edtFName.getText().toString(),
                                                            edtLName.getText().toString(),
                                                            edtEmail.getText().toString(),
                                                            edtPassword.getText().toString(),
                                                            edtPhone.getText().toString(),
                                                            null,
                                                            "User");
                                                }
                                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(details);
                                                Toast.makeText(MainActivity.this, "Successfull", Toast.LENGTH_LONG).show();


                                                waitingDialog.dismiss();

                                                setResult(101);
                                                finish();

                                            }
                                        }
                                    })
                                    .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            waitingDialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Unsuccessfull: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                        }



                    }
                });
                Button neg = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlertDialog.dismiss();
                    }
                });
            }
        });
        mAlertDialog.show();
    }

    private void showLoginDialog() {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to Sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login,null);

        final MaterialEditText edtEmail = login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.edtPassword);
        TextView forgotPassword = login_layout.findViewById(R.id.forgotPassword);



        dialog.setView(login_layout);
        dialog.setPositiveButton("Sign In", null);
        dialog.setNegativeButton("Cancel", null);

        final AlertDialog mAlertDialog = dialog.create();
//        --------------------------------------------------------

        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
        dialog1.setTitle("Forgot Password");
        dialog1.setMessage("Please use email to Re-authenticate");

        LayoutInflater inflater1 = LayoutInflater.from(MainActivity.this);
        View login_layout1 = inflater1.inflate(R.layout.layout_forgot_password,null);

        final MaterialEditText edtEmail_forgotPassword = login_layout1.findViewById(R.id.edtEmail_forgotPassword);

        dialog1.setView(login_layout1);
        dialog1.setPositiveButton("send mail", null);
        dialog1.setNegativeButton("Cancel", null);

        final AlertDialog mAlertDialog1 = dialog1.create();

//        --------------------------------------------------------
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
                mAlertDialog1.show();
            }
        });

        mAlertDialog1.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button pos = mAlertDialog1.getButton(AlertDialog.BUTTON_POSITIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(edtEmail_forgotPassword.getText().toString())){
                            edtEmail_forgotPassword.setText("");
                            edtEmail_forgotPassword.setHint("Enter Valid Email");
                        }
                        else {
                            firebaseAuth.sendPasswordResetEmail(edtEmail_forgotPassword.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mAlertDialog1.dismiss();
                                                Log.d(TAG, "onComplete: ");
                                                Toast.makeText(MainActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Log.d(TAG, "onNotComplete: ");
                                                Toast.makeText(MainActivity.this, "Email not sent, retry again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
                Button neg = mAlertDialog1.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAlertDialog1.dismiss();
                    }
                });
            }
        });
//        --------------------------------------------
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button pos = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int flag = 0;

                        if(TextUtils.isEmpty(edtEmail.getText().toString())){
                            edtEmail.setText("");
                            edtEmail.setHint("Fill Email Correctly");
                            flag = 1;
                        }
                        if(TextUtils.isEmpty(edtPassword.getText().toString())){
                            edtPassword.setText("");
                            edtPassword.setHint("Password is not valid");
                            flag = 1;
                        };
                        if(flag == 0){
                            final android.app.AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(MainActivity.this).build();
                            waitingDialog.show();

                            mAlertDialog.dismiss();
                            firebaseAuth.signInWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                waitingDialog.dismiss();

                                                setResult(101);
                                                finish();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(MainActivity.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            waitingDialog.dismiss();
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }

                    }
                });
                Button neg = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                neg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlertDialog.dismiss();
                    }
                });
            }
        });

        mAlertDialog.show();

    }

//    ----------------------------------------------------

}
