package com.example.seniorcompanion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private TextView weatherData;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        weatherData = findViewById(R.id.fl_weather_report);

        // Initialize TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            } else {
                Toast.makeText(this, "TextToSpeech Initialization failed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Voice Report Button
        findViewById(R.id.btn_voice_report).setOnClickListener(v -> {
            if (tts != null) {
                String text = weatherData.getText().toString();
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        // Emergency Button
        findViewById(R.id.btn_emergency).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:112")); // or your local emergency number
            startActivity(intent);
        });

        // Load weather data (this is just an example, you need to implement the actual loading)
        loadWeatherData();
    }

    private void loadWeatherData() {
        // Here you should load actual weather data, for now, we use a placeholder
        weatherData.setText("Weather data goes here");
    }

    public void onReturnClick(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
