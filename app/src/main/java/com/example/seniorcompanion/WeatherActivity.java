package com.example.seniorcompanion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.gson.Gson;
//
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private Button btnEmergency;
    private Button btnVoiceReport;
    private Button btnBack;
    private TextView tvWeatherInfo;

    private final String API_KEY = "YOUR_API_KEY";
    private final String CITY = "YOUR_CITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        btnEmergency = findViewById(R.id.btn_emergency);
        btnVoiceReport = findViewById(R.id.btn_voice_report);
        btnBack = findViewById(R.id.btn_back);
        tvWeatherInfo = findViewById(R.id.tv_weather_info);

        btnEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherActivity.this, "一键呼救按钮点击", Toast.LENGTH_SHORT).show();
                // 在这里添加您的逻辑，例如拨打紧急电话
            }
        });

        btnVoiceReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherActivity.this, "语音播报按钮点击", Toast.LENGTH_SHORT).show();
                // 在这里添加您的逻辑，例如启动语音播报功能
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(WeatherActivity.this, "返回按钮点击", Toast.LENGTH_SHORT).show();
                // 在这里添加您的逻辑，例如返回上一个界面
            }
        });

//       fetchWeatherData();
    }

    private void fetchWeatherData() {
//        OkHttpClient client = new OkHttpClient();

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&appid=" + API_KEY + "&units=metric";

//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                new Handler(Looper.getMainLooper()).post(() ->
//                        Toast.makeText(WeatherActivity.this, "天气数据获取失败", Toast.LENGTH_SHORT).show()
//                );
//            }

//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseData = response.body().string();
//                    WeatherResponse weatherResponse = new Gson().fromJson(responseData, WeatherResponse.class);
//
//                    new Handler(Looper.getMainLooper()).post(() -> {
//                        String weatherInfo = "城市: " + weatherResponse.name + "\n" +
//                                "温度: " + weatherResponse.main.temp + "°C\n" +
//                                "天气: " + weatherResponse.weather[0].description;
//                        tvWeatherInfo.setText(weatherInfo);
//                    });
//                } else {
//                    new Handler(Looper.getMainLooper()).post(() ->
//                            Toast.makeText(WeatherActivity.this, "天气数据获取失败", Toast.LENGTH_SHORT).show()
//                    );
//                }
//            }
//        });
    }

    class WeatherResponse {
        Main main;
        Weather[] weather;
        String name;

        class Main {
            float temp;
        }

        class Weather {
            String description;
        }
    }
}