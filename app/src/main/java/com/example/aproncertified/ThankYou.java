package com.example.aproncertified;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYou extends AppCompatActivity {

    String name;
    TextView NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        NAME = (TextView) findViewById(R.id.name);

        name = getIntent().getStringExtra("Name");

        NAME.setText(name);

    }
}
