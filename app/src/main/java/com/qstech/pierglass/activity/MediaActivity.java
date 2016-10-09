package com.qstech.pierglass.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.qstech.pierglass.R;

import com.qstech.pierglass.utils.BitmapUtils;
import com.qstech.pierglass.utils.PathUtils;
import com.qstech.pierglass.utils.TimeUtils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;

public class MediaActivity extends AppCompatActivity {

    //时间选择器的三级数组
    private ArrayList<String> options1Items = new ArrayList<>();

    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();

    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    private ImageView imgButton;

    private TextView timeTv;

    private Button confirmBtn;

    OptionsPickerView optionTime;

    private Uri imageUri;

    private int imageTime;

    private int position;

    private Activity mContext;

    View vMasker;

    private File saveFile;

    //版本比较：是否是4.4及以上版本
    final boolean mIsKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_media);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        timeTv = (TextView) findViewById(R.id.activity_add_media_tv);
        //点击弹出选项选择器
        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionTime.show();
            }
        });

        //点击选择图片
        imgButton = (ImageView) findViewById(R.id.activity_add_media_image);
        imgButton.setImageBitmap(BitmapUtils.getSmallBitmap(R.drawable.img_add_backgroud));
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
                //imgButton.setImageDrawable(null);
                saveFile = new File(Environment.getExternalStorageDirectory(), "Qstech//Pierglass");
                if (!saveFile.exists()) {
                    saveFile.mkdirs();
                }

                Crop.pickImage(mContext);
                //selectedImg();
            }
        });

        //确认提交
        confirmBtn = (Button) findViewById(R.id.activity_add_media_confirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (imageUri == null) {
                    Toast.makeText(mContext, "Images have no choice", Toast.LENGTH_SHORT).show();
                } else {
                    if (position != -1) {
                        intent.putExtra("imageUri", imageUri.toString());
                        intent.putExtra("imageTime", imageTime);
                        intent.putExtra("position", position);
                    } else {

                        intent.putExtra("imageUri", imageUri.toString());
                        intent.putExtra("imageTime", imageTime);
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        vMasker = findViewById(R.id.vMasker);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        if (position != -1) {
            imageUri = Uri.parse(intent.getStringExtra("imageUri"));
            imageTime = intent.getIntExtra("imageTime", 0);
            imgButton.setImageBitmap(BitmapUtils.getSmallBitmap(imageUri));
            timeTv.setText("Play Time: " + TimeUtils.toString(imageTime));
        }

        optionTime = new OptionsPickerView(mContext);
        optionTimeInit();

    }

    //时间选择器初始化
    public void optionTimeInit() {
        //选项1 时
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                options1Items.add("0" + i);
            } else {
                options1Items.add("" + i);
            }
        }

        //选项2 分
        ArrayList<String> options2Items_item = new ArrayList<>();
        for (int j = 0; j < 60; j++) {
            if (j < 10) {
                options2Items_item.add("0" + j);
            } else {
                options2Items_item.add("" + j);
            }
        }
        for (int i = 0; i < options1Items.size(); i++) {
            options2Items.add(options2Items_item);
        }

        //选项3 秒
        ArrayList<ArrayList<String>> options3Items_items = new ArrayList<>();
        ArrayList<String> options3Items_items_items = new ArrayList<>();
        for (int j = 0; j < 60; j++) {
            if (j < 10) {
                options3Items_items_items.add("0" + j);
            } else {
                options3Items_items_items.add("" + j);
            }
        }
        for (int i = 0; i < 60; i++) {
            options3Items_items.add(options3Items_items_items);
        }
        for (int i = 0; i < 24; i++) {
            options3Items.add(options3Items_items);
        }

        //三级联动效果
        optionTime.setPicker(options1Items, options2Items, options3Items, true);
        //设置选择的三级单位
        optionTime.setLabels(":", ":", "");
        optionTime.setTitle("select time");
        //设置是否可循环
        optionTime.setCyclic(false, false, false);
        //设置默认选中的三级项目
        if (position >= 0) {
            optionTime.setSelectOptions(TimeUtils.getHour(imageTime), TimeUtils.getMinutes(imageTime), TimeUtils.getSeconds(imageTime));
        } else {
            optionTime.setSelectOptions(0, 0, 0);
        }
        //监听确定选择按钮
        optionTime.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3) {
                //返回的分别是三个级别的选中位置
                String tx = "Play Time: " + options1Items.get(options1) + ":"
                        + options2Items.get(options1).get(options2) + ":"
                        + options3Items.get(options1).get(options2).get(options3);
                timeTv.setText(tx);
                imageTime = options1 * 3600 + options2 * 60 + options3;
                vMasker.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (optionTime.isShowing()) {
                optionTime.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Crop.REQUEST_PICK:
                if (resultCode == RESULT_OK) {
                    beginCrop(data.getData());
                }
                break;
            case Crop.REQUEST_CROP:
                handleCrop(resultCode, data);
                break;
            default:
                break;
        }

    }

    /**
     * 功能：根据传回的地址判断是否需要裁剪
     *
     * @param source imageUri
     */
    private void beginCrop(Uri source) {
        String fileName = System.currentTimeMillis() + ".jpg";
        //判断编译环境，对地址进行格式化，例：content://media/external/ 转为：file:///storage/emulated
        Uri temp;
        if (mIsKitKat) {
            String mPicturePath = "file://" + PathUtils.getPath(mContext, source);
            temp = Uri.parse(mPicturePath);
        } else {
            temp = source;
        }

        Log.d("---------------------", "---------------------");
        Log.d("source=", source.toString());
        Log.d("temp=", temp.toString());
        Log.d("---------------------", "---------------------");

        if (!BitmapUtils.isQualifiedBitmap(temp)) {
            Uri destination = Uri.fromFile(new File(saveFile, fileName));
            Crop.of(source, destination).withAspect(8, 27).start(mContext);
        } else {
            //由于未进行裁剪，裁剪返回的是标准地址，这里无法使用content:// 格式，需使用 file:// 格式
            imageUri = temp;
            imgButton.setImageBitmap(BitmapUtils.getSmallBitmap(imageUri));
        }

    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            imgButton.setImageDrawable(null);
            //imgButton.setImageURI(Crop.getOutput(result));//内存消耗太大
            imageUri = Crop.getOutput(result);
            imgButton.setImageBitmap(BitmapUtils.getSmallBitmap(imageUri));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(MediaActivity.this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
