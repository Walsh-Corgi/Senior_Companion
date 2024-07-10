package com.example.seniorcompanion;

import android.app.ActionBar;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {


    private TextToSpeech tts;
//    private TextView tvWeatherInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        Button btn_voice = (Button) findViewById(R.id.voice_broadcast_button);
        initTTS();
        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String text = tvWeatherInfo.getText().toString();
//                startTTS(text);
               startTTS("当前天气：晴 n温度：25°C n明天：晴，28°C n后天：多云，26°C n大后天：小雨，22°C");
            }
        });


    }

    private void initTTS() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.SUCCESS) {
                    ActionBar ToastUtil = null;
                    Toast.makeText(WeatherActivity.this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startTTS(String str) {
        // 设置音量(值越大声音越尖(女生)，值越小则变成男声，1.0是常规)
        tts.setPitch(1.0f);
        // 设置语速
        tts.setSpeechRate(1.0f);
        // 设置需要播报的语言
        tts.setLanguage(Locale.CHINA);
        // 设置需要播报的语句(若设备不支持则不会读出来)
        /**
         * TextToSpeech.QUEUE_FLUSH：中断当时的播报，播报新的语音
         * TextToSpeech.QUEUE_ADD：添加到当前任务之后
         */
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
    }

    protected void onStop() {
        super.onStop();
        tts.stop();//停止播报
        tts.shutdown();//释放资源
    }
}
