package com.hgsil.android.bihu.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
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

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class AddAnswerActivity extends AppCompatActivity {
    EditText content;
    String token;
    int qid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityManeger.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addanswer);

        content= (EditText)findViewById(R.id.content_edit_addAnswer);
        TextView add = (TextView)findViewById(R.id.add_addAnswer);
        TextView back = (TextView)findViewById(R.id.back_addAnswer);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        token = sharedPreferences.getString("mToken","");
        qid = sharedPreferences.getInt("qid",0);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!content.getText().toString().equals(null))
                    sendRequestWithHttpURLConnection();
                else {
                    Toast.makeText(AddAnswerActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void sendRequestWithHttpURLConnection(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    String url = new String("https://api.caoyue.com.cn/bihu/answer.php");
                    String data = HttpUtil.post(url,"qid="+qid+"&content="+content.getText().toString()
                            +"&token="+token);
                    JSONObject jsonObject = new JSONObject(data);
                    int status = Integer.parseInt(jsonObject.getString("status"));
                    //成功就返回
                    if (status == 200) {
                        finish();

                    }
                    //失败就弹出提示
                    else {
                        Looper.prepare();
                        Toast.makeText(AddAnswerActivity.this,"添加失败，请检查网络",Toast.LENGTH_SHORT).show();
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
        ActivityManeger.removeActivity(this);
        super.onDestroy();
    }
}
