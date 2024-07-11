package com.example.seniorcompanion.Newsdata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seniorcompanion.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class NewsAdapter extends BaseAdapter {
    private Context context;
    private List<Data> dataList;

    public NewsAdapter(Context context, List<Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
//新闻列表的个数
    @Override
    public int getCount() {
        return dataList.size();
    }
    //决定第i处的列表项列表项内容
    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }
//决定第i处的列表项列表项ID
    @Override
    public long getItemId(int i) {
        return i;
    }
//决定第i处的列表项组件
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view==null){
            //如果传入的View为空，则使用LayoutInflater从newsitem.xml布局文件中加载列表项的布局。
            view= LayoutInflater.from(context).inflate(R.layout.newsitem,null);
        }
        TextView tvTittle=(TextView) view.findViewById(R.id.tvTitle);
        TextView tvAuth=(TextView) view.findViewById(R.id.tvAuth);
        TextView tvTime=(TextView) view.findViewById(R.id.tvTime);
        ImageView ivpic=(ImageView) view.findViewById(R.id.ivPic);

        Data data= dataList.get(i);
        tvTittle.setText(data.getTitle());
        tvAuth.setText(data.getAuthor_name());
        tvTime.setText(data.getDate());
        String pic_url=data.getThumbnail_pic_S();

        return view;

    }
    public static void setPic(final ImageView ivPic, final String pic_url)
    {
        //设置图片需要访问网络，因此不能在主线程中设置
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    HttpsURLConnection conn=(HttpsURLConnection) new URL(pic_url).openConnection();
                    conn.connect();
                    InputStream is=conn.getInputStream();
                    Bitmap bitmap= BitmapFactory.decodeStream(is);
                    ivPic.setImageBitmap(bitmap);
                    is.close();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

    }


}
