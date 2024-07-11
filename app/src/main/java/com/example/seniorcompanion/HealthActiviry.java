package com.example.seniorcompanion;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HealthActiviry extends AppCompatActivity {

    private Map<String, Float[]> dataMap = new HashMap<>(); // To store bloodSugar, bloodPressure, heartRate for each date
    private List<Entry> bloodSugarEntries = new ArrayList<>();
    private List<Entry> bloodPressureEntries = new ArrayList<>();
    private List<Entry> heartRateEntries = new ArrayList<>();
    private LineDataSet bloodSugarDataSet;
    private LineDataSet bloodPressureDataSet;
    private LineDataSet heartRateDataSet;
    private LineData lineData;
    private EditText editTextBloodSugar;
    private EditText editTextBloodPressure;
    private EditText editTextHeartRate;
    private TextView tvBloodSugarValue;
    private TextView tvBloodPressureValue;
    private TextView tvHeartRateValue;
    private TextView tvDate;
    private LineChart lineChart;
    private Calendar calendar;
    private SimpleDateFormat sdf;
    private static final int MAX_DAYS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_layout);

        editTextBloodSugar = findViewById(R.id.edit_text_blood_sugar);
        editTextBloodPressure = findViewById(R.id.edit_text_blood_pressure);
        editTextHeartRate = findViewById(R.id.edit_text_heart_rate);
        tvBloodSugarValue = findViewById(R.id.tv_blood_sugar_value);
        tvBloodPressureValue = findViewById(R.id.tv_blood_pressure_value);
        tvHeartRateValue = findViewById(R.id.tv_heart_rate_value);
        tvDate = findViewById(R.id.tv_date);
        lineChart = findViewById(R.id.chart);

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        tvDate.setText(sdf.format(calendar.getTime()));

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        showLineChart(lineChart);

        Button btn_add = findViewById(R.id.add_button);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                addData();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        tvDate.setText(sdf.format(calendar.getTime()));
                        loadDataForSelectedDate();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadDataForSelectedDate() {
        String selectedDate = tvDate.getText().toString();
        if (dataMap.containsKey(selectedDate)) {
            Float[] values = dataMap.get(selectedDate);
            tvBloodSugarValue.setText(values[0] != null ? String.format("%.1f mmol/L", values[0]) : "");
            tvBloodPressureValue.setText(values[1] != null ? String.format("%.1f mmHg", values[1]) : "");
            tvHeartRateValue.setText(values[2] != null ? String.format("%.1f bpm", values[2]) : "");
        } else {
            tvBloodSugarValue.setText("");
            tvBloodPressureValue.setText("");
            tvHeartRateValue.setText("");
        }
    }

    // Display line chart
    private void showLineChart(LineChart lineChart) {
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDrawBorders(false);

        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        lineChart.getAxisRight().setEnabled(false);

        lineChart.animateX(1000);

        updateLineChart();
    }

    // Update line chart
    private void updateLineChart() {
        bloodSugarDataSet = new LineDataSet(bloodSugarEntries, "Blood Sugar");
        bloodPressureDataSet = new LineDataSet(bloodPressureEntries, "Blood Pressure");
        heartRateDataSet = new LineDataSet(heartRateEntries, "Heart Rate");

        bloodSugarDataSet.setColor(Color.RED);
        bloodPressureDataSet.setColor(Color.GREEN);
        heartRateDataSet.setColor(Color.BLUE);

        bloodSugarDataSet.setLineWidth(2.5f);
        bloodPressureDataSet.setLineWidth(2.5f);
        heartRateDataSet.setLineWidth(2.5f);

        bloodSugarDataSet.setCircleRadius(4f);
        bloodPressureDataSet.setCircleRadius(4f);
        heartRateDataSet.setCircleRadius(4f);

        lineData = new LineData(bloodSugarDataSet, bloodPressureDataSet, heartRateDataSet);
        lineChart.setData(lineData);
        updateXAxisLabels();
        lineChart.invalidate();
    }

    private void updateXAxisLabels() {
        XAxis xAxis = lineChart.getXAxis();
        List<String> dates = new ArrayList<>(dataMap.keySet());
        Collections.sort(dates, new Comparator<String>() {
            @Override
            public int compare(String date1, String date2) {
                try {
                    return sdf.parse(date1).compareTo(sdf.parse(date2));
                } catch (Exception e) {
                    return 0;
                }
            }
        });
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addData() {
        String bloodSugarString = editTextBloodSugar.getText().toString().trim();
        String bloodPressureString = editTextBloodPressure.getText().toString().trim();
        String heartRateString = editTextHeartRate.getText().toString().trim();
        String selectedDate = tvDate.getText().toString();

        if (!bloodSugarString.isEmpty() || !bloodPressureString.isEmpty() || !heartRateString.isEmpty()) {
            if (dataMap.size() < MAX_DAYS || dataMap.containsKey(selectedDate)) {
                Float[] values = dataMap.getOrDefault(selectedDate, new Float[3]);

                if (!bloodSugarString.isEmpty()) {
                    try {
                        float bloodSugarValue = Float.parseFloat(bloodSugarString);
                        values[0] = bloodSugarValue;
                        tvBloodSugarValue.setText(String.format("%.1f mmol/L", bloodSugarValue));
                        editTextBloodSugar.getText().clear();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter a valid blood sugar value", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!bloodPressureString.isEmpty()) {
                    try {
                        float bloodPressureValue = Float.parseFloat(bloodPressureString);
                        values[1] = bloodPressureValue;
                        tvBloodPressureValue.setText(String.format("%.1f mmHg", bloodPressureValue));
                        editTextBloodPressure.getText().clear();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter a valid blood pressure value", Toast.LENGTH_SHORT).show();
                    }
                }
                if (!heartRateString.isEmpty()) {
                    try {
                        float heartRateValue = Float.parseFloat(heartRateString);
                        values[2] = heartRateValue;
                        tvHeartRateValue.setText(String.format("%.1f bpm", heartRateValue));
                        editTextHeartRate.getText().clear();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter a valid heart rate value", Toast.LENGTH_SHORT).show();
                    }
                }
                dataMap.put(selectedDate, values);
                updateEntries();
                updateLineChart();
            } else {
                Toast.makeText(this, "You can only record data for up to 7 days.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Input cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEntries() {
        bloodSugarEntries.clear();
        bloodPressureEntries.clear();
        heartRateEntries.clear();

        int dayIndex = 0;
        List<String> dates = new ArrayList<>(dataMap.keySet());
        Collections.sort(dates, new Comparator<String>() {
            @Override
            public int compare(String date1, String date2) {
                try {
                    return sdf.parse(date1).compareTo(sdf.parse(date2));
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        for (String date : dates) {
            Float[] values = dataMap.get(date);
            if (values[0] != null) {
                bloodSugarEntries.add(new Entry(dayIndex, values[0]));
            }
            if (values[1] != null) {
                bloodPressureEntries.add(new Entry(dayIndex, values[1]));
            }
            if (values[2] != null) {
                heartRateEntries.add(new Entry(dayIndex, values[2]));
            }
            dayIndex++;
        }
    }
}
