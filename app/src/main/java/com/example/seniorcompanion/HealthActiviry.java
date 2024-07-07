package com.example.seniorcompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class HealthActiviry extends AppCompatActivity {

    private Button btnEmergency;
    private Button btnVoiceReport;
    private Button btnBack;
    private Button btnStartTest;
    private ImageView btnGenerateReport;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_layout);

        btnEmergency = findViewById(R.id.btn_emergency);
        btnVoiceReport = findViewById(R.id.btn_voice_report);
        btnBack = findViewById(R.id.btn_back);
        btnStartTest = findViewById(R.id.btn_start_test);
        btnGenerateReport = findViewById(R.id.btn_generate_report);
        viewPager = findViewById(R.id.viewPager);

        btnEmergency.setOnClickListener(v -> {
            Toast.makeText(HealthActiviry.this, "一键呼救按钮点击", Toast.LENGTH_SHORT).show();
            // 在这里添加您的逻辑，例如拨打紧急电话
        });

        btnVoiceReport.setOnClickListener(v -> {
            Toast.makeText(HealthActiviry.this, "语音播报按钮点击", Toast.LENGTH_SHORT).show();
            // 在这里添加您的逻辑，例如启动语音播报功能
        });

        btnBack.setOnClickListener(v -> {
            Toast.makeText(HealthActiviry.this, "返回按钮点击", Toast.LENGTH_SHORT).show();
            // 在这里添加您的逻辑，例如返回上一个界面
        });

        btnStartTest.setOnClickListener(v -> {
            int currentItem = viewPager.getCurrentItem();
            switch (currentItem) {
                case 0:
                    Toast.makeText(HealthActiviry.this, "开始心率检测", Toast.LENGTH_SHORT).show();
                    // 在这里添加您的逻辑，例如启动心率检测功能
                    break;
                case 1:
                    Toast.makeText(HealthActiviry.this, "开始血压检测", Toast.LENGTH_SHORT).show();
                    // 在这里添加您的逻辑，例如启动血压检测功能
                    break;
                case 2:
                    Toast.makeText(HealthActiviry.this, "开始血糖检测", Toast.LENGTH_SHORT).show();
                    // 在这里添加您的逻辑，例如启动血糖检测功能
                    break;
            }
        });

        btnGenerateReport.setOnClickListener(v -> {
            Toast.makeText(HealthActiviry.this, "生成报告按钮点击", Toast.LENGTH_SHORT).show();
            // 在这里添加您的逻辑，例如生成分析图表和建议
        });

//        setupViewPager();
    }

//    private void setupViewPager() {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new HeartRateFragment(), "心率");
//        adapter.addFragment(new BloodPressureFragment(), "血压");
//        adapter.addFragment(new BloodSugarFragment(), "血糖");
//        viewPager.setAdapter(adapter);
//    }
}