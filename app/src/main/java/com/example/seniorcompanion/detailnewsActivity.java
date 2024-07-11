package com.example.seniorcompanion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seniorcompanion.DetailNewsData.detailNewsResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class detailnewsActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private boolean isRequestFinished=false;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news1);
        //返回到主界面
        returnButton=findViewById(R.id.backbtn);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始新的活动
                Intent intent=new Intent(detailnewsActivity.this, NewsActivity.class);
                startActivity(intent);
                finish();
            }
        });
       //接受传来的数据
        String title = getIntent().getStringExtra("title");
        String authorname=getIntent().getStringExtra("authorname");
        String date=getIntent().getStringExtra("date");
        String picturl=getIntent().getStringExtra("picturl");
        String  contenturl=getIntent().getStringExtra("contenturl");//得到列表的详细的uniquekey
        int num=getIntent().getIntExtra("num",1);
        sendRequest(contenturl);
         textView=findViewById(R.id.textView);

    }
    //获得新闻详情的json
    private void sendRequest(String contenturl)
    {
        OkHttpClient client=new OkHttpClient();
        //构建请求参数
        HttpUrl.Builder urlbuilder=HttpUrl.parse("http://v.juhe.cn/toutiao/content").newBuilder();
        urlbuilder.addQueryParameter("uniquekey",contenturl);
        urlbuilder.addQueryParameter("key","3e1703d3072d8dbe9213355661c5f714");
        HttpUrl url=urlbuilder.build();
        //构建请求
        Request request =new Request.Builder().url(url)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .build();
        //异步发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //请求失败
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //请求成功
                if(response.isSuccessful())
                {
                    String responseData=response.body().string();
                    Log.d("测试", "responseData");
                    paserAndsolveData(responseData,detailnewsActivity.this);
                }
            }
        });


    }
    //处理得到的json数据
    private void paserAndsolveData(String jsonData, Context context)
    {
        Gson gson = new Gson();
        detailNewsResponse detailnews=gson.fromJson(jsonData,detailNewsResponse.class);
        String textcontent=detailnews.getResult().getContent();
        // 使用正则表达式移除HTML标签
        textcontent = textcontent.replaceAll("<[^>]*>", "");

        //将textcontent得到的内容设置到TextView中
        String finalTextcontent = textcontent;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(finalTextcontent);
            }
        });

    }
}