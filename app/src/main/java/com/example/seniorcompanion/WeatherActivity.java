package com.example.seniorcompanion;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seniorcompanion.bean.Body;
import com.example.seniorcompanion.bean.Forecast;
import com.example.seniorcompanion.bean.ResponseData;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.CallBackProxy;
import com.xuexiang.xhttp2.callback.SimpleCallBack;
import com.xuexiang.xhttp2.exception.ApiException;

import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {

    private TextToSpeech tts;

    private String cityName;
    private TextView tvContent;

    private TextView temp;
    private TextView type;
    private TextView tempHigh;
    private TextView tempLow;
    private TextView fx;
    private TextView fl;
    private TextView humidity;
    private TextView quality;
    private TextView notice;

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
               startTTS("喝O泡果奶，把清凉抱抱，我要O泡，我要O泡，O泡果奶要要要");
            }
        });


        tvContent = findViewById(R.id.tv_content);

        temp=findViewById(R.id.tempTextView);
        type=findViewById(R.id.typeTextView);
        tempHigh=findViewById(R.id.tempHighTextView);
        tempLow=findViewById(R.id.tempLowTextView);
        fx=findViewById(R.id.fxTextView);
        fl=findViewById(R.id.flTextView);
        humidity=findViewById(R.id.humidityTextView);
        quality=findViewById(R.id.qualityTextView);
        notice=findViewById(R.id.noticeTextView);

        cityName = "北京";
        String url = "http://t.weather.itboy.net/api/weather/city/101010100";

        // Xhttp2 请求网络
        XHttp.get(url)
            .syncRequest(false) //异步请求
            .execute(new CallBackProxy<Body<ResponseData>, ResponseData>(
                    new SimpleCallBack<ResponseData>() {
                        @Override
                        public void onSuccess(ResponseData data) {
                            // 处理 data
                            dealWithData(data);
                        }

                        @Override
                        public void onError(ApiException e) {
                            // 处理异常
                        }
                    }
            ) {
            });//最后一定要有 {} 否则解析失败 作者：为中华之崛起而敲代码 https://www.bilibili.com/read/cv8476993?spm_id_from=333.999.0.0 出处：bilibili

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


    @SuppressLint("SetTextI18n")
    private void dealWithData(ResponseData data) {
        if (data == null) {
            Toast.makeText(WeatherActivity.this, "结果异常！", Toast.LENGTH_SHORT).show();
            return;
        }
        Forecast todayForecast = data.getForecast().get(0);
        String today = buildForecastString(todayForecast, "今天");

        Forecast tomorrowForecast = data.getForecast().get(1);
        String tomorrow = buildForecastString(tomorrowForecast, "明天");

        tvContent.setText(tomorrow);

        temp.setText(data.getWendu());
        type.setText(todayForecast.getType());
        tempHigh.setText(todayForecast.getHigh());
        tempLow.setText(todayForecast.getLow());
        fx.setText(todayForecast.getFx());
        fl.setText(todayForecast.getFl());
        humidity.setText("湿度："+data.getShidu());
        quality.setText("空气质量："+data.getQuality());
        notice.setText(data.getGanmao()+"\n"+todayForecast.getNotice());

    }

    private String buildForecastString(Forecast forecast, String day) {
        return day + "【" + cityName + "】天气" + "\n" +
                "日期：" + forecast.getYmd() + "丨" +
                forecast.getWeek() + ";\n" +
                "天气类型：" + forecast.getType() + ";\n" +
                "最高温度：" + forecast.getHigh() + ";\n" +
                "最低温度：" + forecast.getLow() + ";\n" +
                "刮风情况：" + forecast.getFl() + "丨" +
                forecast.getFx() +
                ";\n" +
                "天气建议：" + forecast.getNotice() + ";\n\n\n";
    }

}
