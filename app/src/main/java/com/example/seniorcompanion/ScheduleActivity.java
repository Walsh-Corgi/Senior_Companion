package com.example.seniorcompanion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity {

    private TextView tvMonth;
    private TextView tvSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_layout);

        tvMonth = findViewById(R.id.tvMonth);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        CalendarView calendarView = findViewById(R.id.calendarView);

        // 初始化日历
        initCalendar();

        // 监听日期选择事件
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = (month + 1) + "月" + dayOfMonth + "日";
                tvSelectedDate.setText(selectedDate);
                // 这里可以更新日程列表
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;

        tvMonth.setText(month + "月");
    }
}