package com.example.seniorcompanion;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MenuActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PHONE = 1;
    private EditText phoneNumberEditText;
    private Button callButton;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private MediaRecorder mediaRecorder;

    // 按钮有关互动
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        ImageButton btnEmergency = findViewById(R.id.helpbtn);
        btnEmergency.setOnClickListener(view -> makeEmergencyCall());

        ImageButton schedulebutton = findViewById(R.id.daliybtn);
        schedulebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,ScheduleActivity.class);
                startActivity(intent);
            }
        });

        ImageButton newsbutton = findViewById(R.id.newbtn);
        newsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,NewsActivity.class);
                startActivity(intent);
            }
        });

        ImageButton weatherbutton = findViewById(R.id.weatherbtn);
        weatherbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,WeatherActivity.class);
                startActivity(intent);
            }
        });

        ImageButton healthbutton =  findViewById(R.id.healthbtn);
        healthbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,HealthActiviry.class);
                startActivity(intent);
            }
        });

        ImageButton btnVoiceAnnouncements = findViewById(R.id.voice1);
        btnVoiceAnnouncements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }




    private void makeEmergencyCall() {
        String emergencyNumber = "112"; // 紧急号码，例如 112 或 911
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        } else {
            startCall(emergencyNumber);
        }
    }

    private void startCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        try {
            startActivity(callIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "拨打电话权限被拒绝", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeEmergencyCall();
            } else {
                Toast.makeText(this, "拨打电话权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
