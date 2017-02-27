package com.hgsil.android.bihu.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hgsil.android.bihu.R;

/**
 * Created by Administrator on 2017/2/27 0027.
 */

public class FindPasswordAcitivity extends AppCompatActivity{
    EditText mUsername;
    EditText mPassword;
    String password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityManeger.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpassword);
        mUsername = (EditText)findViewById(R.id.username_findPassword);
        mPassword = (EditText)findViewById(R.id.password_findPassword);
        TextView back = (TextView)findViewById(R.id.back_findPassword);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView confime = (TextView)findViewById(R.id.confirm_findPassword);
        confime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                password = sharedPreferences.getString(mUsername.getText().toString(),"");
                Log.d("FindPasswordAcitivity",password);
                if (!password.equals(null))
                mPassword.setText(password);
                else {
                    Toast.makeText(FindPasswordAcitivity.this,"对不起，您从未在本机登录过，无密码消息",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        ActivityManeger.addActivity(this);
        super.onDestroy();
    }
}
