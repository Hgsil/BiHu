package com.hgsil.android.bihu.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hgsil.android.bihu.R;
import com.hgsil.android.bihu.Util.HttpUtil;
import com.qiniu.util.Auth;

import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by Administrator on 2017/2/23 0023.
 */

public class AddNewsActivity extends AppCompatActivity {

    EditText title;
    EditText content;
    String token;
    ImageView photo;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addnews);
        ActivityManeger.addActivity(this);
        title = (EditText) findViewById(R.id.title_edit_addnews);
        content = (EditText) findViewById(R.id.content_edit_addnews);
        TextView add = (TextView) findViewById(R.id.add_addnews);
        TextView back = (TextView) findViewById(R.id.back_addNews);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        token = sharedPreferences.getString("mToken", "");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!title.getText().toString().equals(null) && !content.getText().toString().equals(null))
                    sendRequestWithHttpURLConnection();
                else {
                    Toast.makeText(AddNewsActivity.this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendRequestWithHttpURLConnection() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String url = new String("https://api.caoyue.com.cn/bihu/question.php");
                    String data = HttpUtil.post(url, "title=" + title.getText().toString()
                            + "&" + "content=" + content.getText().toString() + "&token=" + token);
                    JSONObject jsonObject = new JSONObject(data);
                    int status = Integer.parseInt(jsonObject.getString("status"));
                    Log.d("AddNewsActivity", status + "");
                    //成功就返回
                    if (status == 200) {
                        finish();

                    }
                    //失败就弹出提示
                    else {
                        Looper.prepare();
                        Toast.makeText(AddNewsActivity.this, "添加失败，请检查网络", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManeger.removeActivity(this);
    }




}
