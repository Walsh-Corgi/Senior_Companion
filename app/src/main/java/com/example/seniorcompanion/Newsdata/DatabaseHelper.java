package com.example.seniorcompanion.Newsdata;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "NewsDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "news";//表名
    //列名
    private static final String COLUMN_UNIQUE_KEY = "uniqueKey";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_AUTHOR_NAME = "authorName";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_THUMBNAIL_PIC_S = "thumbnailPicS";
    private static final String COLUMN_IS_CONTENT = "content";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_UNIQUE_KEY + " TEXT PRIMARY KEY,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_AUTHOR_NAME + " TEXT,"
                + COLUMN_URL + " TEXT,"
                + COLUMN_THUMBNAIL_PIC_S + " TEXT,"
                + COLUMN_IS_CONTENT+ " TEXT"
                + ");";
        sqLiteDatabase.execSQL(createTableQuery);
        //execSQL()方法可以执行insert、delete、update和CREATE TABLE之类有更改行为的SQL语句；
    }
//数据库升级
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    //插入数据
    public void insertData(Data data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_UNIQUE_KEY, data.getUniquekey());
        values.put(COLUMN_TITLE, data.getTitle());
        values.put(COLUMN_DATE, data.getDate());
        values.put(COLUMN_CATEGORY, data.getCategory());
        values.put(COLUMN_AUTHOR_NAME, data.getAuthor_name());
        values.put(COLUMN_URL, data.getUrl());
        values.put(COLUMN_THUMBNAIL_PIC_S, data.getThumbnail_pic_S());
        values.put(COLUMN_IS_CONTENT, data.getIs_content());

        try {
            db.insertOrThrow(TABLE_NAME, null, values);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            db.close();
        }
    }
    //
    @SuppressLint("Range")
    public List<Data> getDataList() {
        List<Data> dataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor =null;
        try {
            cursor=db.rawQuery(query, null);
            //rawQuery()方法用于执行select语句。

            if (cursor.moveToFirst()) {
                do {
                    Data data = new Data();
                    data.setUniquekey(cursor.getString(cursor.getColumnIndex(COLUMN_UNIQUE_KEY)));
                    data.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                    data.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
                    data.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                    data.setAuthor_name(cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR_NAME)));
                    data.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
                    data.setThumbnail_pic_S(cursor.getString(cursor.getColumnIndex(COLUMN_THUMBNAIL_PIC_S)));
                    data.setIs_content(cursor.getString(cursor.getColumnIndex(COLUMN_IS_CONTENT)));
                    dataList.add(data);
                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return dataList;
    }
}
