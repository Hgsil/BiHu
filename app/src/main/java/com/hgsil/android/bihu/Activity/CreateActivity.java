package com.hgsil.android.bihu.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hgsil.android.bihu.R;
import com.hgsil.android.bihu.Util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/1/18 0018.
 */

public class CreateActivity extends AppCompatActivity implements View.OnClickListener{
    EditText username;
    EditText password;
    private boolean isExit;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        username = (EditText)findViewById(R.id.number_create);
        password = (EditText)findViewById(R.id.password_create);
        TextView create = (TextView)findViewById(R.id.button_create);
        TextView gotologin = (TextView)findViewById(R.id.gotologin_create);
        create.setOnClickListener(this);
        gotologin.setOnClickListener(this);


        ActivityManeger.addActivity(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()== R.id.button_create){
            if (password.getText().toString().length()<6){
                Toast.makeText(CreateActivity.this,"密码小于6位！",Toast.LENGTH_SHORT);
            }
            else {
                sendRequestWithHttpURLConnection();
            }

        }
        if (v.getId() == R.id.gotologin_create){
            Intent intent = new Intent(CreateActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }


    private void sendRequestWithHttpURLConnection(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection =null;

                try{
                    String url = new String("https://api.caoyue.com.cn/bihu/register.php");
                    String data = HttpUtil.post(url,"username="+username.getText().toString()
                            +"&"+"password="+password.getText().toString());
                    JSONObject jsonObject = new JSONObject(data);
                    String status = jsonObject.getString("status");
                    String info = jsonObject.getString("info");
                    Log.d("CreateActivity", "status is " + status);
                    Log.d("CreateActivity", "info is " + info);

                    if (info.equals("用户名已被使用")){
                        Looper.prepare();
                        Toast.makeText(CreateActivity.this,"用户名已被使用",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }catch (Exception e){
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
    //双击退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mHandler.sendEmptyMessageDelayed(0, 2000);
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                System.exit(0);
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
