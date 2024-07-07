package com.example.seniorcompanion;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PHONE = 1;
    private Button btnEmergency, btnVoiceReport, btnAddSchedule;
    private EditText etDate;
    private ListView lvSchedule;
    private ArrayList<String> scheduleList;
 //   private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);

        btnEmergency = findViewById(R.id.btn_emergency);
        btnVoiceReport = findViewById(R.id.btn_voice_report);
        etDate = findViewById(R.id.et_date);
        btnAddSchedule = findViewById(R.id.btn_add_schedule);
        lvSchedule = findViewById(R.id.lv_schedule);

        scheduleList = new ArrayList<>();
 //       adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scheduleList);
 //       lvSchedule.setAdapter(adapter);

        btnEmergency.setOnClickListener(view -> makeEmergencyCall());
        btnVoiceReport.setOnClickListener(view -> {
            // 实现语音播报功能
            Toast.makeText(ScheduleActivity.this, "语音播报功能", Toast.LENGTH_SHORT).show();
        });

        etDate.setOnClickListener(view -> showDatePickerDialog());

        btnAddSchedule.setOnClickListener(view -> {
            String date = etDate.getText().toString();
            if (!date.isEmpty()) {
                scheduleList.add(date);
 //               adapter.notifyDataSetChanged();
                etDate.setText("");
            } else {
                Toast.makeText(ScheduleActivity.this, "请选择日期", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, month1, dayOfMonth) -> etDate.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth), year, month, day);
        datePickerDialog.show();
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
