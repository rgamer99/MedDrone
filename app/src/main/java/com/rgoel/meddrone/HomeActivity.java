package com.rgoel.meddrone;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private Button buttonhelpme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);


        buttonhelpme = findViewById(R.id.buttonhelpme);
        buttonhelpme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelpMeActivity();
            }
        });
    }

    public void openHelpMeActivity() {
        Intent intent = new Intent(this, HelpMeActivity.class);
        startActivity(intent);
    }
}