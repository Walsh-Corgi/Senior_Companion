package com.example.seniorcompanion;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class HealthActiviry extends AppCompatActivity {

    List<String> dataList = new ArrayList<>();
    List<String> dataList1 = new ArrayList<>();

    private List<BarEntry> entries = new ArrayList<>();
    private BarDataSet dataSet;
    private BarData barData;
    private EditText editText;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_layout);

        barChart = (BarChart) findViewById(R.id.chart);
        showBarChart(barChart, getBarChartData());

        PieChart mPieChartweight = (PieChart) findViewById(R.id.chart_weight);
        showPieChart(mPieChartweight, getPieChartData());

        Button btn_add = (Button)findViewById(R.id.add_button);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });
    }

    //派状图添加图表数据
    private List<PieEntry> getPieChartData() {
        //给刚才定义的dataList添加数据的方式，注意一下传递的参数是字符串哦
        dataList.add("xxx");
        dataList.add("yyy");
        dataList.add("zzz");

        List<PieEntry> mPie = new ArrayList<>();
        for (String data : dataList) {
            //下面这个PieEntry第一个参数主要要传递float类型的参数，表示百分比的
            PieEntry pieEntry = new PieEntry(15F, data);
            mPie.add(pieEntry);
        }
        return mPie;
    }

    //柱状
    private List<BarEntry> getBarChartData() {
        // 给 dataList 添加数据
//        dataList1.add("xxx");
//        dataList1.add("yyy");
//        dataList1.add("zzz");

        List<BarEntry> entries = new ArrayList<>();
        // 模拟数据，实际使用时根据需求设置具体的数值
        entries.add(new BarEntry(0, 15)); // 第一个参数是x轴的位置，第二个参数是y轴的值
        entries.add(new BarEntry(1, 25));
        entries.add(new BarEntry(2, 10));
        return entries;
    }

    // 显示柱状图
    private void showBarChart(BarChart barChart, List<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Label");

        // 设置柱状图的颜色
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // 设置柱状图数据
        BarData barData = new BarData(dataSet);

        // 设置描述，我设置了不显示，你也可以试试让它显示
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

        // 设置柱状图的一些属性
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);

        // 设置X轴和Y轴
        barChart.getXAxis().setEnabled(false); // 禁用X轴
        barChart.getAxisLeft().setEnabled(true); // 启用Y轴
        barChart.getAxisRight().setEnabled(false); // 禁用右侧Y轴

        // 设置动画效果
        barChart.animateY(1000);

        // 显示图例
        Legend legend = barChart.getLegend();
        legend.setEnabled(true);

        // 将数据设置给柱状图
        barChart.setData(barData);

        // 更新柱状图视图
        barChart.invalidate();
    }

    //显示图表
    private void showPieChart(PieChart pieChart, List<PieEntry> pieList) {

        PieDataSet dataSet = new PieDataSet(pieList, "");

        // 设置颜色list，让不同的块显示不同颜色
        ArrayList<Integer> colors = new ArrayList<Integer>();
        int[] MATERIAL_COLORS = {
                Color.rgb(200, 172, 255)
        };
        for (int c : MATERIAL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        dataSet.setColors(colors);
        PieData pieData = new PieData(dataSet);

        // 设置描述，我设置了不显示，因为不好看，你也可以试试让它显示，真的不好看
        Description description = new Description();
        description.setEnabled(false);
        pieChart.setDescription(description);
        //设置半透明圆环的半径, 0为透明
        pieChart.setTransparentCircleRadius(0f);

        //设置初始旋转角度
        pieChart.setRotationAngle(-15);

        //数据连接线距图形片内部边界的距离，为百分数
        dataSet.setValueLinePart1OffsetPercentage(80f);

        //设置连接线的颜色
        dataSet.setValueLineColor(Color.LTGRAY);
        // 连接线在饼状图外面
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // 设置饼块之间的间隔
        dataSet.setSliceSpace(1f);
        dataSet.setHighlightEnabled(true);
        // 显示图例
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);

        // 和四周相隔一段距离,显示数据
        pieChart.setExtraOffsets(26, 5, 26, 5);

        // 设置pieChart图表是否可以手动旋转
        pieChart.setRotationEnabled(true);
        // 设置piecahrt图表点击Item高亮是否可用
        pieChart.setHighlightPerTapEnabled(true);
        // 设置pieChart图表展示动画效果，动画运行1.4秒结束
        //  pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        //设置pieChart是否只显示饼图上百分比不显示文字
        pieChart.setDrawEntryLabels(true);
        //是否绘制PieChart内部中心文本
        pieChart.setDrawCenterText(false);
        // 绘制内容value，设置字体颜色大小
        pieData.setDrawValues(true);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.DKGRAY);

        pieChart.setData(pieData);
        // 更新 piechart 视图
        pieChart.postInvalidate();
    }


    private void addData() {
        String entryString = editText.getText().toString().trim();
        if (!entryString.isEmpty()) {
            float value = Float.parseFloat(entryString);
            entries.add(new BarEntry(entries.size(), value));
            editText.getText().clear();

            // 重新设置数据源
            dataSet = new BarDataSet(entries, "Label");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barData = new BarData(dataSet);

            // 设置柱状图的数据
            barChart.setData(barData);
            barChart.invalidate();
        }
    }

}

