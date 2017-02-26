package com.hgsil.android.bihu.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hgsil.android.bihu.R;
import com.hgsil.android.bihu.Util.HttpUtil;
import com.qiniu.android.common.Zone;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class ChangeAvaterActivity extends AppCompatActivity implements View.OnClickListener{
    private String ACCESS_KEY = "3nDfpwAgBX-uZeS1psc4tJtUdLdVSGuVF5B1XDYW";
    private String SECRET_KEY = "FGBJYFzVtuN2ENz9C9mXUqHdJykUV-r_1V8avxLn";
    //要上传的空间
    String bucketname = "bihuavatar";
    //上传到七牛后保存的文件名
    String key ;
    String userName;
    //上传文件的路径
    String path;
    //密钥配置
    private Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    private String mToken;
    String TAG = "ChangeAvaterActivity";
    de.hdodenhof.circleimageview.CircleImageView mAvater;
    TextView back;
    TextView openPhoto;
    TextView changeAvater;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(ChangeAvaterActivity.this,"没有选中头像",Toast.LENGTH_SHORT);
                    break;
                case 1:
                    Toast.makeText(ChangeAvaterActivity.this,"修改成功",Toast.LENGTH_SHORT);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityManeger.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeavater);
        back = (TextView)findViewById(R.id.back_changeAvater);
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        mToken = sharedPreferences.getString("mToken","");
        userName = sharedPreferences.getString("mUsername","");
        key = userName;
        openPhoto = (TextView)findViewById(R.id.changeAvater_fromphoto);
        changeAvater = (TextView)findViewById(R.id.changeAvater);
        mAvater = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.changeAvater_cirecleview);
        mAvater.setImageResource(R.mipmap.head);
        back.setOnClickListener(this);
        openPhoto.setOnClickListener(this);
        changeAvater.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        ActivityManeger.removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_changeAvater:
                finish();
                break;
            case R.id.changeAvater_fromphoto:
                openAlbum();
                break;
            case R.id.changeAvater:
                if (!path.equals(null)){

                    SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                    editor.putString("userAvatar","http://olytlzxao.bkt.clouddn.com"+userName);
                    finish();
                }
                else
                    Toast.makeText(ChangeAvaterActivity.this,"头像为空",Toast.LENGTH_SHORT);
                break;

        }
    }
    private void openAlbum(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);


    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null){

            Message message=new Message();
            message.what=0;
            mHandler.sendMessage(message);

        }
        else {

            path = data.getDataString();
            Bitmap bm = null;
            //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
            ContentResolver resolver = getContentResolver();
            //此处的用于判断接收的Activity是不是你想要的那个
            if (requestCode == 1) {
                try {
                    Uri originalUri = data.getData();        //获得图片的uri

                    bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        //显得到bitmap图片


                    String[] proj = {MediaStore.Images.Media.DATA};

                    //好像是Android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    path = cursor.getString(column_index);
                }catch (IOException e) {
                    Log.e(TAG,e.toString());
                }
            }
            uploadmAvatar();
            uploadAvaterToBiHu();
            Glide.with(ChangeAvaterActivity.this).load(path).into(mAvater);
        }

    }
    public void uploadmAvatar() {

        try {
            InputStream is = new FileInputStream(path);
            byte[] bytes = getBytes(is);
            UploadManager uploadManager = new UploadManager(new Configuration.Builder().zone(Zone.zone2).build());

            //第二个参数就是指定在bucket里面存储文件的名字，唯一性
            uploadManager
                    .put(bytes,
                            userName,
                            getUpToken(),
                            new UpCompletionHandler() {
                                @Override
                                public void complete(String key,
                                                     ResponseInfo info, JSONObject response) {
                                    Log.e(TAG, key);
                                    Log.e(TAG, info.toString());
                                    Log.e(TAG, "上传是否成功" + info.isOK());

                                    if (response != null) {
                                        Log.e(TAG, response.toString());

                                    }
                                }
                            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUpToken(){

            return auth.uploadToken(bucketname);
        }

    public byte[] getBytes(InputStream is)  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[2048];
        int len;
        try {
            while ((len = is.read(b, 0, 2048)) != -1) {
                baos.write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
    public void uploadAvaterToBiHu(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    String url = new String("https://api.caoyue.com.cn/bihu/modifyAvatar.php");
                    String response = HttpUtil.post(url,"token="+mToken+"&avatar=http://olytlzxao.bkt.clouddn.com/"+userName);
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("ChangeAvatarActivity","info="+jsonObject.getString("info"));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
