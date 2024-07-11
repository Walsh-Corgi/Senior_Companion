package com.example.seniorcompanion;

import static com.example.seniorcompanion.R.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityRecord extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;

    private Button btn_record;
    private int isRecording = 0;
    private int isPlaying = 0;
    //语音保存路径
    private String FilePath = null;
    private Timer mTimer;
    private ImageView iv_microphone;
    private TextView tv_recordTime;
    private ImageView iv_record_wave_left, iv_record_wave_right;
    private AnimationDrawable ad_left, ad_right;
    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // 设置为全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // 检查权限
        if (!checkPermissions()) {
            requestPermissions();
        }

        btn_save = findViewById(R.id.bt_save);
        btn_save.setOnClickListener(new ClickEvent());
        Button btn_back = findViewById(R.id.bt_back);
        btn_back.setOnClickListener(new ClickEvent());
        btn_record = findViewById(R.id.btn_record);
        btn_record.setOnClickListener(new ClickEvent());
        iv_microphone = findViewById(R.id.iv_microphone);
        iv_microphone.setOnClickListener(new ClickEvent());
        iv_record_wave_left = findViewById(R.id.iv_record_wave_left);
        iv_record_wave_right = findViewById(R.id.iv_record_wave_right);

        iv_record_wave_left.setImageResource(drawable.record_wave_left);
        iv_record_wave_right.setImageResource(drawable.record_wave_right);

        ad_left = ((AnimationDrawable) iv_record_wave_left.getDrawable());
        ad_right = ((AnimationDrawable) iv_record_wave_right.getDrawable());
        tv_recordTime = findViewById(R.id.tv_recordTime);
    }

    private boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean recordAudioAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readStorageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted && recordAudioAccepted && readStorageAccepted) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                break;
        }
    }

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String time[] = tv_recordTime.getText().toString().split(":");
                    int hour = Integer.parseInt(time[0]);
                    int minute = Integer.parseInt(time[1]);
                    int second = Integer.parseInt(time[2]);

                    if (second < 59) {
                        second++;
                    } else if (second == 59 && minute < 59) {
                        minute++;
                        second = 0;
                    }
                    if (second == 59 && minute == 59 && hour < 98) {
                        hour++;
                        minute = 0;
                        second = 0;
                    }

                    time[0] = hour + "";
                    time[1] = minute + "";
                    time[2] = second + "";
                    //调整格式显示到屏幕上
                    if (second < 10)
                        time[2] = "0" + second;
                    if (minute < 10)
                        time[1] = "0" + minute;
                    if (hour < 10)
                        time[0] = "0" + hour;

                    //显示在TextView中
                    tv_recordTime.setText(time[0] + ":" + time[1] + ":" + time[2]);

                    break;
            }
        }
    };

    class ClickEvent implements View.OnClickListener {

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                    //点击的是开始录音按钮
                    case R.id.btn_record:
                        handleRecordButton();
                        break;
                    //如果单击的是麦克图标，则可以是进入试听模式，再次点击，停止播放
                    case R.id.iv_microphone:
                        handleMicrophoneButton();
                        break;
                    //点击确定按钮
                    case R.id.bt_save:
                        handleSaveButton();
                        break;
                    case R.id.bt_back:
                        handleBackButton();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleRecordButton() {
            if (isRecording == 0) {
                // 开始录音
                if (FilePath != null) {
                    File oldFile = new File(FilePath);
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }
                }

                String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "record.amr";
                File dir = new File(getExternalFilesDir(null), "notes");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, str);
                FilePath = file.getAbsolutePath();

                // 计时器
                mTimer = new Timer();

                iv_microphone.setClickable(false);
                tv_recordTime.setText("00:00:00");
                isRecording = 1;
                btn_record.setBackgroundResource(drawable.tabbar_record_stop);

                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile(FilePath);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                try {
                    mRecorder.prepare();
                    mRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1);
                    }
                }, 1000, 1000);
                ad_left.start();
                ad_right.start();
            } else {
                // 停止录音
                isRecording = 0;
                btn_record.setBackgroundResource(drawable.tabbar_record_start);
                mRecorder.stop();
                mTimer.cancel();
                mTimer = null;

                mRecorder.release();
                mRecorder = null;

                iv_microphone.setClickable(true);
                ad_left.stop();
                ad_right.stop();
                Toast.makeText(ActivityRecord.this, "单击麦克图标试听，再次点击结束试听", Toast.LENGTH_LONG).show();
            }
        }

        private void handleMicrophoneButton() {
            if (FilePath == null) {
                Toast.makeText(ActivityRecord.this, "没有录音广播可以播放，请先录音", Toast.LENGTH_LONG).show();
            } else {
                if (isPlaying == 0) {
                    isPlaying = 1;
                    mPlayer = new MediaPlayer();
                    tv_recordTime.setText("00:00:00");
                    mTimer = new Timer();
                    mPlayer.setOnCompletionListener(new MediaCompletion());
                    try {
                        mPlayer.setDataSource(FilePath);
                        mPlayer.prepare();
                        mPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(1);
                        }
                    }, 1000, 1000);
                    ad_left.start();
                    ad_right.start();
                } else {
                    isPlaying = 0;
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                    mTimer.cancel();
                    mTimer = null;
                    ad_left.stop();
                    ad_right.stop();
                }
            }
        }

        private void handleSaveButton() {
            Intent intent = getIntent();
            Bundle b = new Bundle();
            b.putString("audio", FilePath);
            intent.putExtras(b);
            setResult(RESULT_OK, intent);
            finish();
        }

        private void handleBackButton() {
            if (FilePath != null) {
                File oldFile = new File(FilePath);
                oldFile.delete();
            }
            finish();
        }
    }

    class MediaCompletion implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mTimer.cancel();
            mTimer = null;
            isPlaying = 0;
            ad_left.stop();
            ad_right.stop();
            Toast.makeText(ActivityRecord.this, "播放完毕", Toast.LENGTH_LONG).show();
            tv_recordTime.setText("00:00:00");
        }
    }
}
