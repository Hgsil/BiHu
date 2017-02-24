package com.hgsil.android.bihu.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hgsil.android.bihu.Information.User;
import com.hgsil.android.bihu.R;
import com.hgsil.android.bihu.Util.HttpUtil;

import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by Administrator on 2017/2/4 0004.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText username;
    EditText password;
    String token;
    private boolean isExit;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){

                Toast.makeText(LoginActivity.this,"账户或密码错误！",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView gotocreate = (TextView)findViewById(R.id.gotocreate_login);
        TextView login = (TextView)findViewById(R.id.button_login);
        username = (EditText)findViewById(R.id.number_login);
        password = (EditText)findViewById(R.id.password_login);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        gotocreate.setOnClickListener(this);
        login.setOnClickListener(this);
        ActivityManeger.addActivity(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.gotocreate_login){
            Intent intent = new Intent(LoginActivity.this,CreateActivity.class);
            startActivity(intent);
        }
        if (v.getId()==R.id.button_login){
            sendRequestWithHttpURLConnection();
        }
    }

    private void sendRequestWithHttpURLConnection(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection =null;

                try{
                    String url = new String("https://api.caoyue.com.cn/bihu/login.php");
                    String data = HttpUtil.post(url,"username="+username.getText().toString()
                            +"&"+"password="+password.getText().toString());
                    JSONObject jsonObject = new JSONObject(data);
                    int status = Integer.parseInt(jsonObject.getString("status"));
                    //密码正确 进入主界面
                    if (status == 200) {
                        JSONObject jsonObjectData = new JSONObject(jsonObject.getString("data"));
                        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                        token = jsonObjectData.getString("token");
                        String mUsername = jsonObjectData.getString("username");
                        editor.putString("mToken",token);
                        editor.putString("mUsername",mUsername);
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this,NewsActivity.class);
                        startActivity(intent);
                    }
                    //密码错误 弹出Toast
                    else {
                        Message message = new Message();
                        message.what=0;
                        mHandler.sendMessage(message);
                    }
                    Log.d("LoginActivity",status+"");
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
