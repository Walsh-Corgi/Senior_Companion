package com.example.seniorcompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaintActivity extends Activity {

    private static final String TAG = "PaintActivity";

    private PaintView paintView;
    private GridView paint_bottomMenu;
    private ListView lv_popWindow;
    private Button btn_save;
    private Button btn_back;

    private int select_paint_size_index = 0;
    private int select_paint_style_index = 0;
    private int select_paint_color_index = 0;

    // 菜单资源
    private int[] paintItems = {
            R.drawable.paint_more,
            R.drawable.paint_pencil,
            R.drawable.paint_icon_color,
            R.drawable.paint_icon_back,
            R.drawable.paint_icon_forward,
            R.drawable.paint_icon_delete
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        try {
            paint_bottomMenu = findViewById(R.id.paintBottomMenu);
            paint_bottomMenu.setOnItemClickListener(new MenuClickEvent());

            paintView = findViewById(R.id.paint_layout);

            InitPaintMenu();

            btn_save = findViewById(R.id.bt_save);
            btn_back = findViewById(R.id.bt_back);
            btn_save.setOnClickListener(new ClickEvent());
            btn_back.setOnClickListener(new ClickEvent());
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
        }
    }

    // 配置绘图菜单
    public void InitPaintMenu() {
        ArrayList<Map<String, Object>> menus = new ArrayList<>();
        for (int paintItem : paintItems) {
            Map<String, Object> item = new HashMap<>();
            item.put("image", paintItem);
            menus.add(item);
        }
        paint_bottomMenu.setNumColumns(paintItems.length);
        paint_bottomMenu.setSelector(R.drawable.bottom_item);
        SimpleAdapter mAdapter = new SimpleAdapter(this, menus, R.layout.item_button, new String[]{"image"}, new int[]{R.id.item_image});
        paint_bottomMenu.setAdapter(mAdapter);
    }

    // 设置按钮监听器
    class ClickEvent implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                if (v == btn_save) {
                    // 保存绘图
                    Intent intent = getIntent();
                    Bundle b = new Bundle();
                    String path = paintView.saveBitmap();
                    b.putString("paintPath", path);
                    intent.putExtras(b);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (v == btn_back) {
                    // 返回
                    finish();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in ClickEvent", e);
            }
        }
    }

    // 设置菜单项监听器
    class MenuClickEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                switch (position) {
                    case 0:
                        showMoreDialog(view);
                        break;
                    case 1:
                        showPaintSizeDialog(view);
                        break;
                    case 2:
                        showPaintColorDialog(view);
                        break;
                    case 3:
                        paintView.undo();
                        break;
                    case 4:
                        paintView.redo();
                        break;
                    case 5:
                        showClearDialog();
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in MenuClickEvent", e);
            }
        }
    }

    // 弹出画笔颜色选项对话框
    public void showPaintColorDialog(View parent) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.custom_dialog);
            alertDialogBuilder.setTitle("选择画笔颜色：");

            alertDialogBuilder.setSingleChoiceItems(R.array.paintcolor, select_paint_color_index, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    select_paint_color_index = which;
                    paintView.selectPaintColor(which);
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.create().show();
        } catch (Exception e) {
            Log.e(TAG, "Error in showPaintColorDialog", e);
        }
    }

    // 弹出画笔大小选项对话框
    public void showPaintSizeDialog(View parent) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.custom_dialog);
            alertDialogBuilder.setTitle("选择画笔大小：");

            alertDialogBuilder.setSingleChoiceItems(R.array.paintsize, select_paint_size_index, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    select_paint_size_index = which;
                    paintView.selectPaintSize(which);
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.create().show();
        } catch (Exception e) {
            Log.e(TAG, "Error in showPaintSizeDialog", e);
        }
    }

    // 弹出选择画笔或橡皮擦的对话框
    public void showMoreDialog(View parent) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.custom_dialog);
            alertDialogBuilder.setTitle("选择画笔或橡皮擦：");

            alertDialogBuilder.setSingleChoiceItems(R.array.paintstyle, select_paint_style_index, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    select_paint_style_index = which;
                    paintView.selectPaintStyle(which);
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.create().show();
        } catch (Exception e) {
            Log.e(TAG, "Error in showMoreDialog", e);
        }
    }

    // 弹出清空绘画内容确认对话框
    public void showClearDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.custom_dialog);
            builder.setTitle("清空提示");
            builder.setMessage("您确定要清空所有吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    paintView.removeAllPaint();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } catch (Exception e) {
            Log.e(TAG, "Error in showClearDialog", e);
        }
    }
}
