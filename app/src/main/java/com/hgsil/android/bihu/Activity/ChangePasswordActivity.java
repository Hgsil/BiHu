package com.hgsil.android.bihu.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hgsil.android.bihu.R;
import com.hgsil.android.bihu.Util.HttpUtil;

import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{
    EditText password;
    SharedPreferences mSharedPreferences ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManeger.addActivity(this);
        setContentView(R.layout.activity_forgetpassword);
        TextView confirm = (TextView)findViewById(R.id.confirm_forget);
        password = (EditText)findViewById(R.id.password_forget);
        mSharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        TextView back = (TextView)findViewById(R.id.back_changePassword);
        confirm.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        ActivityManeger.removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm_forget:
                sendRequestWithHttpURLConnection();
                break;
            case R.id.back_changePassword:
                this.finish();
                break;
        }
    }
    private void sendRequestWithHttpURLConnection(){
        final String token = mSharedPreferences.getString("mToken","");
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection =null;

                try{
                    String url = new String("https://api.caoyue.com.cn/bihu/changePassword.php");
                    String data = HttpUtil.post(url,"token="+token
                            +"&password="+password.getText().toString());
                    JSONObject jsonObject = new JSONObject(data);
                    int status = Integer.parseInt(jsonObject.getString("status"));
                    //修改成功 弹出提示
                    if (status == 200) {
                        String newData = jsonObject.getString("data");
                        JSONObject jsonObjectData = new JSONObject(newData);
                        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                        String newToken = jsonObjectData.getString("token");
                        editor.putString("mToken",newToken);
                        editor.apply();
                        Looper.loop();
                        Toast.makeText(ChangePasswordActivity.this,"修改密码成功，",Toast.LENGTH_SHORT).show();
                        Looper.prepare();
                    }
                    //修改失败 弹出提示
                    else {
                        Looper.loop();
                        Toast.makeText(ChangePasswordActivity.this,"修改密码失败，请检查网络",Toast.LENGTH_SHORT).show();
                        Looper.prepare();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
