package com.example.seniorcompanion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.seniorcompanion.Newsdata.Data;
import com.example.seniorcompanion.Newsdata.DatabaseHelper;
import com.example.seniorcompanion.Newsdata.NewsAdapter;
import com.example.seniorcompanion.Newsdata.NewsApiresponse;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsActivity extends AppCompatActivity {
    private ImageButton returnButton;
    private ImageButton helpButton;
    private ListView lvnews;
    private ArrayList<Data> dataList;
    private NewsAdapter adapter;
    private boolean isRequestFinished=false;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
      /*  //返回到主界面
        returnButton=findViewById(R.id.backbtn);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始新的活动
                Intent intent=new Intent(NewsActivity.this, function.class);
                startActivity(intent);
                finish();
            }
        });
        //呼救事件相应
        helpButton =findViewById(R.id.helpbtn);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emergencyNum="120";
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+emergencyNum));
                startActivity(intent);
            }
        });
*/
        //新闻列表的获取
        lvnews=findViewById(R.id.listview);;
        dataList=new ArrayList<>();
        adapter=new NewsAdapter(this,dataList);
        lvnews.setAdapter(adapter);
        //点击列表项跳转界面
       lvnews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Data data =dataList.get(i);
                Intent intent =new Intent(NewsActivity.this, detailnewsActivity.class);
                intent.putExtra("title",data.getTitle());
                intent.putExtra("authorname",data.getAuthor_name());
                intent.putExtra("date",data.getDate());
                intent.putExtra("picturl",data.getThumbnail_pic_S());
                intent.putExtra("contenturl",data.getUniquekey());
                startActivity(intent);
            }
        });
        sendRequest();


    }
    private  void sendRequest()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client =new OkHttpClient();
                      Request request= new Request.Builder()
                        .url("http://v.juhe.cn/toutiao/index?type=&page=&page_size=&is_filter=&key=3e1703d3072d8dbe9213355661c5f714")
                        .build();
                Response response=null;
                response =client.newCall(request).execute();
                String responseData=response.body().string();
                Log.d("测试：", responseData);
                parseAndStoreData(responseData, NewsActivity.this);
                isRequestFinished=true;

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if(isRequestFinished)
                {
                    //更新Adapter,确保在ui线程中更新
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }

            }
        }).start();
    }

    private void parseAndStoreData(String jsonData, Context context){
        Gson gson = new Gson();
        NewsApiresponse news = gson.fromJson(jsonData, NewsApiresponse.class);
       List<Data> list =news.getResult().getData();
        DatabaseHelper dbHelper=new DatabaseHelper(context);
        for(Data data :list)
        {
            dbHelper.insertData(data);
        }

        // 确保在 UI 线程中更新 dataList
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataList.clear();
                dataList.addAll(list);
                adapter.notifyDataSetChanged(); // 更新适配器
            }
        });
    }

}
