package com.rgoel.meddrone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;

public class AnimationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Thread thread = new Thread() {

            public void run() {
                try {
                    Thread.sleep(5000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                finally {
                    Intent intent = new Intent(AnimationActivity.this, MainKotlinActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        thread.start();
    }
}