package com.example.seniorcompanion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private GridView bottomMenu;
    private LineEditText et_Notes;
    private Button bt_back;
    private Button bt_save;
    private SQLiteDatabase db;
    private DatabaseOperation dop;
    Intent intent;
    String editModel = null;
    int item_Id;
    InputMethodManager imm;
    private TextView tv_title;

    // 底部按钮
    private int[] bottomItems = {
            R.drawable.tabbar_handwrite,
            R.drawable.tabbar_paint,
            R.drawable.tabbar_microphone,
            R.drawable.tabbar_photo,
            R.drawable.tabbar_camera,
            R.drawable.tabbar_appendix
    };

    // 记录editText中的图片，用于单击时判断单击的是那一个图片
    private List<Map<String, String>> imgList = new ArrayList<>();

    // 配置底部菜单
    private void initBottomMenu() {
        ArrayList<Map<String, Object>> menus = new ArrayList<>();
        for (int bottomItem : bottomItems) {
            Map<String, Object> item = new HashMap<>();
            item.put("image", bottomItem);
            menus.add(item);
        }
        bottomMenu.setNumColumns(bottomItems.length);
        bottomMenu.setSelector(R.drawable.bottom_item);
        SimpleAdapter mAdapter = new SimpleAdapter(AddActivity.this, menus, R.layout.item_button, new String[]{"image"}, new int[]{R.id.item_image});
        bottomMenu.setAdapter(mAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
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

        et_Notes = findViewById(R.id.et_note);
        et_Notes.setOnClickListener(new TextClickEvent());

        bottomMenu = findViewById(R.id.bottomMenu);
        // 配置菜单
        initBottomMenu();
        bottomMenu.setOnItemClickListener(new MenuClickEvent());

        bt_back = findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new ClickEvent());

        bt_save = findViewById(R.id.bt_save);
        bt_save.setOnClickListener(new ClickEvent());

        // 默认关闭软键盘
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_Notes.getWindowToken(), 0);

        dop = new DatabaseOperation(this, db);
        intent = getIntent();
        editModel = intent.getStringExtra("editModel");
        item_Id = intent.getIntExtra("noteId", 0);
        tv_title = findViewById(R.id.tv_title);
        loadData();
    }

    private boolean checkPermissions() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean readStorageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                if (!writeStorageAccepted || !cameraAccepted || !readStorageAccepted) {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    // 为EditText设置监听器
    class TextClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Spanned s = et_Notes.getText();
            ImageSpan[] imageSpans = s.getSpans(0, s.length(), ImageSpan.class);

            int selectionStart = et_Notes.getSelectionStart();
            for (ImageSpan span : imageSpans) {
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);
                // 找到图片
                if (selectionStart >= start && selectionStart < end) {
                    String path = null;
                    for (Map<String, String> map : imgList) {
                        // 找到了
                        if (map.get("location").equals(start + "-" + end)) {
                            path = map.get("path");
                            break;
                        }
                    }
                    if (path != null) {
                        // 判断当前图片是否是录音，如果为录音，则跳转到试听录音的Activity，如果不是，则跳转到查看图片的界面
                        if (path.endsWith("amr")) {
                            Intent intent = new Intent(AddActivity.this, ShowRecord.class);
                            intent.putExtra("audioPath", path);
                            startActivity(intent);
                        }
                    }
                } else {
                    // 如果单击的是空白处或文字，则获得焦点，即打开软键盘
                    imm.showSoftInput(et_Notes, 0);
                }
            }
        }
    }

    // 加载数据
    private void loadData() {
        if ("newAdd".equals(editModel)) {
            et_Notes.setText("");
        } else if ("update".equals(editModel)) {
            tv_title.setText("编辑记事");

            dop.create_db();
            Cursor cursor = dop.query_db(item_Id);
            cursor.moveToFirst();
            @SuppressLint("Range") String context = cursor.getString(cursor.getColumnIndex("context"));

            Pattern p = Pattern.compile("/([^\\.]*)\\.\\w{3}");
            Matcher m = p.matcher(context);
            int startIndex = 0;
            while (m.find()) {
                if (m.start() > 0) {
                    et_Notes.append(context.substring(startIndex, m.start()));
                }
                SpannableString ss = new SpannableString(m.group());
                String path = m.group();
                String type = path.substring(path.length() - 3);
                Bitmap bm;
                Bitmap rbm;

                if ("amr".equals(type)) {
                    bm = BitmapFactory.decodeResource(getResources(), R.drawable.record_icon);
                    rbm = resize(bm, 200);
                } else {
                    bm = BitmapFactory.decodeFile(m.group());
                    rbm = resize(bm, 480);
                }

                rbm = getBitmapHuaSeBianKuang(rbm);
                ImageSpan span = new ImageSpan(this, rbm);
                ss.setSpan(span, 0, m.end() - m.start(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                et_Notes.append(ss);
                startIndex = m.end();
                Map<String, String> map = new HashMap<>();
                map.put("location", m.start() + "-" + m.end());
                map.put("path", path);
                imgList.add(map);
            }
            et_Notes.append(context.substring(startIndex));
            dop.close_db();
        }
    }

    // 等比例缩放图片
    private Bitmap resize(Bitmap bitmap, int S) {
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        double partion = imgWidth * 1.0 / imgHeight;
        double sqrtLength = Math.sqrt(partion * partion + 1);
        double newImgW = S * (partion / sqrtLength);
        double newImgH = S * (1 / sqrtLength);
        float scaleW = (float) (newImgW / imgWidth);
        float scaleH = (float) (newImgH / imgHeight);

        Matrix mx = new Matrix();
        mx.postScale(scaleW, scaleH);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, imgWidth, imgHeight, mx, true);
        return bitmap;
    }

    // 给图片加边框，并返回边框后的图片
    public Bitmap getBitmapHuaSeBianKuang(Bitmap bitmap) {
        float frameSize = 0.2f;
        Matrix matrix = new Matrix();
        Bitmap bitmapbg = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapbg);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        float scale_x = (bitmap.getWidth() - 2 * frameSize - 2) * 1f / bitmap.getWidth();
        float scale_y = (bitmap.getHeight() - 2 * frameSize - 2) * 1f / bitmap.getHeight();
        matrix.reset();
        matrix.postScale(scale_x, scale_y);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(new Rect(0, 0, bitmapbg.getWidth(), bitmapbg.getHeight()), paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect(new Rect((int) (frameSize), (int) (frameSize), bitmapbg.getWidth() - (int) (frameSize), bitmapbg.getHeight() - (int) (frameSize)), paint);

        canvas.drawBitmap(bitmap, frameSize + 1, frameSize + 1, paint);
        return bitmapbg;
    }

    // 设置按钮监听器
    class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_back:
                    AddActivity.this.finish();
                    break;
                case R.id.bt_save:
                    String context = et_Notes.getText().toString();
                    if (context.isEmpty()) {
                        Toast.makeText(AddActivity.this, "记事为空!", Toast.LENGTH_LONG).show();
                    } else {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date curDate = new Date(System.currentTimeMillis());
                        String time = formatter.format(curDate);
                        String title = getTitle(context);
                        dop.create_db();
                        if ("newAdd".equals(editModel)) {
                            dop.insert_db(title, context, time);
                        } else if ("update".equals(editModel)) {
                            dop.update_db(title, context, time, item_Id);
                        }
                        dop.close_db();
                        Intent intent = new Intent(AddActivity.this, MainActivity.class);
                        startActivity(intent);
                        AddActivity.this.finish();
                    }
                    break;
            }
        }
    }

    // 截取EditText中的前一部分作为标题，用于显示在主页列表中
    private String getTitle(String context) {
        Pattern p = Pattern.compile("/([^\\.]*)\\.\\w{3}");
        Matcher m = p.matcher(context);
        StringBuffer strBuff = new StringBuffer();
        String title = "";
        int startIndex = 0;
        while (m.find()) {
            if (m.start() > 0) {
                strBuff.append(context.substring(startIndex, m.start()));
            }
            String path = m.group();
            String type = path.substring(path.length() - 3);
            if ("amr".equals(type)) {
                strBuff.append("[录音]");
            } else {
                strBuff.append("[图片]");
            }
            startIndex = m.end();
            if (strBuff.length() > 15) {
                title = strBuff.toString().replaceAll("\r|\n|\t", " ");
                return title;
            }
        }
        strBuff.append(context.substring(startIndex));
        title = strBuff.toString().replaceAll("\r|\n|\t", " ");
        return title;
    }

    // 设置菜单项监听器
    class MenuClickEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent;
            switch (position) {

                case 1:
                    intent = new Intent(AddActivity.this, PaintActivity.class);
                    startActivityForResult(intent, 3);
                    break;
                case 2:
                    intent = new Intent(AddActivity.this, ActivityRecord.class);
                    startActivityForResult(intent, 4);
                    break;
                case 3:
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, 1);
                    break;
                case 4:
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 2);
                    break;
                case 5:
                    break;
            }
        }
    }

    // 将图片等比例缩放到合适的大小并添加在EditText中
    void InsertBitmap(Bitmap bitmap, int S, String imgPath) {
        bitmap = resize(bitmap, S);
        bitmap = getBitmapHuaSeBianKuang(bitmap);
        final ImageSpan imageSpan = new ImageSpan(this, bitmap);
        SpannableString spannableString = new SpannableString(imgPath);
        spannableString.setSpan(imageSpan, 0, spannableString.length(), SpannableString.SPAN_MARK_MARK);
        Editable editable = et_Notes.getEditableText();
        int selectionIndex = et_Notes.getSelectionStart();
        spannableString.getSpans(0, spannableString.length(), ImageSpan.class);
        editable.insert(selectionIndex, spannableString);
        et_Notes.append("\n");
        Map<String, String> map = new HashMap<>();
        map.put("location", selectionIndex + "-" + (selectionIndex + spannableString.length()));
        map.put("path", imgPath);
        imgList.add(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = getContentResolver();
            Bitmap bitmap = null;
            Bundle extras;
            if (requestCode == 1) {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                try {
                    bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                InsertBitmap(bitmap, 480, path);
            } else if (requestCode == 2) {
                try {
                    if (uri != null) {
                        bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                    } else {
                        extras = data.getExtras();
                        bitmap = extras.getParcelable("data");
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date curDate = new Date(System.currentTimeMillis());
                    String str = formatter.format(curDate) + "paint.png";
                    File dir = new File("/sdcard/notes/");
                    File file = new File("/sdcard/notes/", str);
                    if (!dir.exists()) {
                        dir.mkdir();
                    } else {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                    String path = "/sdcard/notes/" + str;
                    InsertBitmap(bitmap, 480, path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 3) {
                extras = data.getExtras();
                String path = extras.getString("paintPath");
                bitmap = BitmapFactory.decodeFile(path);
                InsertBitmap(bitmap, 480, path);
            } else if (requestCode == 4) {
                extras = data.getExtras();
                String audioPath = extras.getString("audio");
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.record_microphone_icon);
                InsertBitmap(bitmap, 80, audioPath);
            } else if (requestCode == 5) {
                extras = data.getExtras();
                String path = extras.getString("handwritePath");
                bitmap = BitmapFactory.decodeFile(path);
                InsertBitmap(bitmap, 680, path);
            }
        }
    }
}
