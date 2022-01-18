package com.rgoel.meddrone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;


public class HelpMeActivity extends AppCompatActivity {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    TextView tv_lat, tv_lon, tv_altitude, tv_sensor, tv_updates, text_for_user;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch sw_locationupdates, sw_gps;

    boolean updateOn = false;

    LocationRequest locationRequest;

    LocationCallback locationCallBack;

    FusedLocationProviderClient fusedLocationProviderClient;
    private TextToSpeech mTTS;

    private FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_me);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        text_for_user = findViewById(R.id.text_for_user);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationupdates = findViewById(R.id.sw_locationsupdates);

        initializeSdk(this);


        locationRequest = new LocationRequest();
        locationRequest.setInterval(60000000);
        locationRequest.setFastestInterval(60000000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateUIValues(locationResult.getLastLocation());
            }
        };

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(new Locale("hi"));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    }
                } else {
                    Log.e("TTS", "Initialization Failed");
                }
            }
        });

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS Sensors");
                }
                else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Cell Tower + Wifi");
                }
            }
        });

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGPS();
            }
        });

        updateGPS();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        tv_updates.setText("On");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        updateGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void updateGPS() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HelpMeActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        }
        else {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }

    private void updateUIValues(Location location) {
        double user_lat = location.getLatitude();
        double user_long = location.getLongitude();
        tv_updates.setText("On");
        tv_lat.setText(String.valueOf(user_lat));
        tv_lon.setText(String.valueOf(user_long));
        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("Not Available");
        }
        (new Handler()).postDelayed(this::updateUIValuesInnerMethod, 1000);
        double shortest_time_taken = Shortest_Time_Taken(user_lat, user_long);
        int short_tt = (int) Math.round(shortest_time_taken);
        String hitext = "ड्रोन आपके पास " + short_tt + " सेकंड में पहुंचेगा।";
        String entext = "The drone will reach you in " + short_tt + " seconds.";
        String japtext = "ドローンがあなたに届きます " + short_tt + " 秒。";
        text_for_user.setText(hitext + "\n" + entext + "\n" + japtext);
        String hiaudio = hitext + "मैं दोहराता हूँ" + hitext;
        String enaudio = entext + "I repeat" + entext;
        String japaudio = japtext + "繰り返します" + japtext;
        mTTS.setLanguage(new Locale("hi"));
        speak1(hiaudio);
        mTTS.setLanguage(new Locale("en"));
        speak1(enaudio);
        mTTS.setLanguage(new Locale("ja"));
        speak1(japaudio);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                joinMeeting(HelpMeActivity.this, "6875350221", "Iamraghav", fuser.getDisplayName());
                Log.d("Handler", "Running Handler");
            }
        }, 28000);
    }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    private void speak1(String text) {
        mTTS.setPitch(1);
        mTTS.setSpeechRate(1);
        mTTS.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    private void updateUIValuesInnerMethod() {
        tv_updates.setText("Off");
        sw_locationupdates.setChecked(false);
    }

    private double Shortest_Time_Taken(double user_lat, double user_long) {
        double hosp_1 = Distance(user_lat, user_long, 28.5275, 77.2120);
        double hosp_2 = Distance(user_lat, user_long, 28.5659, 77.2111);
        double hosp_3 = Distance(user_lat, user_long, 28.5607, 77.2739);
        ArrayList<Double> hosp_dists = new ArrayList<>();
        hosp_dists.add(hosp_1);
        hosp_dists.add(hosp_2);
        hosp_dists.add(hosp_3);
        Collections.sort(hosp_dists);
        double shortest_distance = hosp_dists.get(0);
        double time_taken = shortest_distance/8.9408;
        return time_taken;
    }

    private double Distance(double user_lat, double user_long,
                            double hosp_lat, double hosp_long){
        double phi_1 = Math.toRadians(user_lat);
        double phi_2 = Math.toRadians(hosp_lat);

        double delta_phi = Math.toRadians(hosp_lat - user_lat);
        double delta_lamba = Math.toRadians(hosp_long - user_long);

        double a = Math.pow(Math.sin(delta_phi / 2.0), 2) + Math.cos(phi_1) * Math.cos(phi_2) * Math.pow(Math.sin(delta_lamba / 2.0),2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return 6371000 * c;
    }

    private void initializeSdk(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();

        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = "Q85gqeIHE3hDFxKxtxruhvF0cq0nMA71bmzZ";
        params.appSecret = "muFHsZmuADMUdOCnzgUZD9BQpYsTN8fohp1O";
        params.domain = "zoom.us";
        params.enableLog = true;

        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            @Override
            public void onZoomSDKInitializeResult(int i, int i1) {

            }

            @Override
            public void onZoomAuthIdentityExpired() {

            }
        };
        sdk.initialize(context, listener, params);
    }

    private void joinMeeting(Context context,String meetingNumber,String meetingPassword,String userName){
        MeetingService meetingService=ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options=new JoinMeetingOptions();
        JoinMeetingParams params=new JoinMeetingParams();
        params.displayName=userName;
        params.meetingNo=meetingNumber;
        params.password=meetingPassword;
        meetingService.joinMeetingWithParams(context, params,options);
    }
}