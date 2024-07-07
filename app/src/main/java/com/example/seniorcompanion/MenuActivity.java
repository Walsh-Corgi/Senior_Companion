package com.example.seniorcompanion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MenuActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PHONE = 1;
    private Button btnEmergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        btnEmergency = findViewById(R.id.btn_emergency);
        btnEmergency.setOnClickListener(view -> makeEmergencyCall());

        Button schedulebutton = (Button) findViewById(R.id.btn_schedule);
        schedulebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,ScheduleActivity.class);
                startActivity(intent);
            }
        });

        Button newsbutton = (Button) findViewById(R.id.btn_news);
        newsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,NewsActivity.class);
                startActivity(intent);
            }
        });

        Button weatherbutton = (Button) findViewById(R.id.btn_weather);
        weatherbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,WeatherActivity.class);
                startActivity(intent);
            }
        });

        Button healthbutton = (Button) findViewById(R.id.btn_health);
        healthbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,HealthActiviry.class);
                startActivity(intent);
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
