package com.example.seniorcompanion;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class NewsActivity extends AppCompatActivity {

    private Button btnEmergency;
    private Button btnVoiceReport;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_layout);

        btnEmergency = findViewById(R.id.btn_emergency);
        btnVoiceReport = findViewById(R.id.btn_voice_report);
        btnBack = findViewById(R.id.btn_back);

        btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewsActivity.this, "一键呼救按钮点击", Toast.LENGTH_SHORT).show();
                // 在这里添加您的逻辑，例如拨打紧急电话
            }
        });

        btnVoiceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewsActivity.this, "语音播报按钮点击", Toast.LENGTH_SHORT).show();
                // 在这里添加您的逻辑，例如启动语音播报功能
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(NewsActivity.this, "返回按钮点击", Toast.LENGTH_SHORT).show();
                // 在这里添加您的逻辑，例如返回上一个界面
                finish();
            }
        });
    }
}