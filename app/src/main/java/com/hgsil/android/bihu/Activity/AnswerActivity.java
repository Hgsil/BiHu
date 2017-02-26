package com.hgsil.android.bihu.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hgsil.android.bihu.Adapter.AnswerAdapter;
import com.hgsil.android.bihu.Information.Answer;
import com.hgsil.android.bihu.Information.News;
import com.hgsil.android.bihu.R;
import com.hgsil.android.bihu.Util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/16 0016.
 */

public class AnswerActivity  extends AppCompatActivity implements View.OnClickListener{
    private List<Answer> mAnswerList = new ArrayList<>();
    RecyclerView mRecyclerView ;
    AnswerAdapter mAnswerAdapter;
    boolean isFirst;
    int page = 0;
    Context mContext;
    News oneNew = new News();
    TextView title;
    TextView content;
    TextView date;
    TextView naive;
    TextView huifu;
    TextView recent;
    TextView authorName;
    TextView exciting;
    de.hdodenhof.circleimageview.CircleImageView avater;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView back;
    ImageView huiImage;
    ImageView excitingImage;
    ImageView naiveImage;
    ImageView favorite;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 1-6单数为取消，双数为选定  7-9为加载
            switch (msg.what) {
                case 1:
                    oneNew.setExciting(oneNew.getExciting()-1);
                    oneNew.setIs_exciting(false);
                    excitingImage.setImageResource(R.mipmap.not_exciting);
                    exciting.setText("("+oneNew.getExciting()+")");
                    Toast.makeText(mContext,"取消喜欢成功",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    oneNew.setExciting(oneNew.getExciting()+1);
                    oneNew.setIs_exciting(true);
                    excitingImage.setImageResource(R.mipmap.is_exciting);
                    exciting.setText("("+oneNew.getExciting()+")");
                    Toast.makeText(mContext,"喜欢成功",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    oneNew.setNaive(oneNew.getNaive()-1);
                    oneNew.setIs_naive(false);
                    naiveImage.setImageResource(R.mipmap.not_naive);
                    naive.setText("("+oneNew.getNaive()+")");
                    Toast.makeText(mContext,"取消讨厌成功",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    oneNew.setNaive(oneNew.getNaive()+1);
                    oneNew.setIs_naive(true);
                    naiveImage.setImageResource(R.mipmap.is_naive);
                    naive.setText("("+oneNew.getNaive()+")");
                    Toast.makeText(mContext,"讨厌成功",Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    favorite.setImageResource(R.mipmap.not_favorite);
                    oneNew.setIs_favorite(false);
                    Toast.makeText(mContext,"取消收藏成功",Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    favorite.setImageResource(R.mipmap.is_favorite);
                    oneNew.setIs_favorite(true);
                    Toast.makeText(mContext,"收藏成功",Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    mAnswerAdapter = new AnswerAdapter(mAnswerList,mContext);
                    mRecyclerView.setAdapter(mAnswerAdapter);
                    mAnswerList.clear();
                    break;
                case 8:
                    if(mAnswerList.size() == 0){
                        Toast.makeText(AnswerActivity.this,"已经没有多的内容了",Toast.LENGTH_SHORT);
                    }
                    mAnswerAdapter.addItem(mAnswerList);
                    mAnswerList.clear();
                    break;
                case 9:
                    mAnswerAdapter.refresh(mAnswerList);
                    mAnswerList.clear();
                    break;
                case 0:
                    Toast.makeText(mContext,"操作失败,请检查网络",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    String token;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManeger.addActivity(this);
        setContentView(R.layout.activity_answer);
        isFirst = true;
        mContext = this;
        mSharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        mEditor = getSharedPreferences("data",MODE_PRIVATE).edit();
        mRecyclerView = (RecyclerView)findViewById(R.id.answer_recycler);
        back = (TextView)findViewById(R.id.back_answer);
        back.setOnClickListener(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.answer_swipeRefresh);
        token = mSharedPreferences.getString("mToken","");
        setOneNew();
        setUi();
        refresh(page,false);
        page++;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(0,true);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState ==RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisibleItem+ 1 ==mAnswerAdapter.getItemCount()){
                    refresh(page,false);
                    page++;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab_answer);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerActivity.this,AddAnswerActivity.class);
                intent.putExtra("qid",oneNew.getId());
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManeger.removeActivity(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.answer_news_isExciting_image:
                if (oneNew.is_exciting()){
                    cancleExcitingOrNaive("exciting",oneNew.getId());
                }else {
                    setExcitingOrNaive("exciting",oneNew.getId());
                }break;
            case R.id.answer_news_isNaive_image:
                if (oneNew.is_naive()){
                    cancleExcitingOrNaive("naive",oneNew.getId());
                }else {
                    setExcitingOrNaive("naive",oneNew.getId());
                }break;
            case R.id.answer_news_favorite:
                if (oneNew.is_favorite()){
                    cancleExcitingOrNaive("favorite",oneNew.getId());
                }else {
                    setExcitingOrNaive("favorite",oneNew.getId());
                }break;
            case R.id.back_answer:
                finish();
                break;
        }
    }
    private void refresh(int page, final boolean isShangLaOrAdd){
        final int time = page;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                String token = sharedPreferences.getString("mToken","");
                try{
                    String url = new String("https://api.caoyue.com.cn/bihu/getAnswerList.php ");
                    String response = HttpUtil.post(url,"token="+token+"&"+"page="+time+""+
                            "&"+"count=20&qid="+oneNew.getId());
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status")==200) {
                        Message message = new Message();
                        if (time == 0 && !isShangLaOrAdd){
                            message.what = 7;}
                        else if (time != 0 && !isShangLaOrAdd){
                            message.what = 8;
                        }
                        else if (isShangLaOrAdd){
                            message.what = 9;
                        }
                        String returnData = jsonObject.getString("data");
                        JSONObject jsonObjectData = new JSONObject(returnData);
                        JSONArray jsonAnswers = jsonObjectData.getJSONArray("answers");
                        for (int i = 0; i < jsonAnswers.length(); i++) {
                            JSONObject jsonAnswer = jsonAnswers.getJSONObject(i);
                            answerListSetOneAnswer(jsonAnswer);

                        }
                        mHandler.sendMessage(message);
                    }
                    else {
                        Looper.prepare();
                        Toast.makeText(AnswerActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
    //取消exciting后将数据传上去
    public void cancleExcitingOrNaive(String excitingOrNaive, final int id){
        if (excitingOrNaive.equals("exciting")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String url = new String("https://api.caoyue.com.cn/bihu/cancelExciting.php");
                        String data = HttpUtil.post(url,"id="+id+"&type=1&token="+token);
                        JSONObject jsonObject = new JSONObject(data);
                        int status = jsonObject.getInt("status");

                        if (status == 200){
                            Message message=new Message();
                            message.what=1;
                            mHandler.sendMessage(message);
                        }
                        else {
                            Message message=new Message();
                            message.what= 0 ;
                            mHandler.sendMessage(message);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        else if (excitingOrNaive.equals("naive")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String url = new String("https://api.caoyue.com.cn/bihu/cancelNaive.php");
                        String data = HttpUtil.post(url,"id="+id+"&type=1&token="+token);
                        JSONObject jsonObject = new JSONObject(data);
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            Message message=new Message();
                            message.what=3;
                            mHandler.sendMessage(message);
                        }
                        else {
                            Message message=new Message();
                            message.what=0;
                            mHandler.sendMessage(message);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else if (excitingOrNaive.equals("favorite")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String url = new String("https://api.caoyue.com.cn/bihu/cancelFavorite.php");
                        String data = HttpUtil.post(url,"qid="+id+"&token="+token);
                        JSONObject jsonObject = new JSONObject(data);
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            Message message=new Message();
                            message.what=5;
                            mHandler.sendMessage(message);
                        }
                        else {
                            Message message=new Message();
                            message.what=0;
                            mHandler.sendMessage(message);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    //点上exciting或者naive后将数据传上去
    public void setExcitingOrNaive(String excitingOrNaive, final int id){
        if (excitingOrNaive.equals("exciting")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String url = new String("https://api.caoyue.com.cn/bihu/exciting.php");
                        String data = HttpUtil.post(url,"id="+id+"&type=1&token="+token);
                        JSONObject jsonObject = new JSONObject(data);
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            Message message=new Message();
                            message.what=2;
                            mHandler.sendMessage(message);
                        }
                        else {
                            Message message=new Message();
                            message.what=0;
                            mHandler.sendMessage(message);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else if (excitingOrNaive.equals("naive")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String url = new String("https://api.caoyue.com.cn/bihu/naive.php");
                        String data = HttpUtil.post(url,"id="+id+"&type=1&token="+token);
                        JSONObject jsonObject = new JSONObject(data);
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            Message message=new Message();
                            message.what=4;
                            mHandler.sendMessage(message);
                        }
                        else {
                            Message message=new Message();
                            message.what=0;
                            mHandler.sendMessage(message);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        } else if (excitingOrNaive.equals("favorite")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String url = new String("https://api.caoyue.com.cn/bihu/favorite.php");
                        String data = HttpUtil.post(url,"qid="+id+"&token="+token);
                        JSONObject jsonObject = new JSONObject(data);
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            Message message=new Message();
                            message.what=6;
                            mHandler.sendMessage(message);
                        }
                        else {
                            Message message=new Message();
                            message.what=0;
                            mHandler.sendMessage(message);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void setUi(){

        title = (TextView)findViewById(R.id.answer_news_title);
        content = (TextView)findViewById(R.id.answer_news_content);
        date = (TextView)findViewById(R.id.answer_news_date);
        naive = (TextView)findViewById(R.id.answer_news_naive);
        huifu = (TextView)findViewById(R.id.answer_news_huifu);
        exciting = (TextView)findViewById(R.id.answer_news_exciting);
        recent = (TextView)findViewById(R.id.answer_news_recent);
        authorName = (TextView)findViewById(R.id.answer_news_username);
        excitingImage = (ImageView)findViewById(R.id.answer_news_isExciting_image);
        naiveImage = (ImageView)findViewById(R.id.answer_news_isNaive_image);
        favorite = (ImageView)findViewById(R.id.answer_news_favorite);
        huiImage = (ImageView)findViewById(R.id.answer_news_huifu_image);
        avater = (de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.answer_news_avater);
        title.setText(oneNew.getTitle());
        content.setText(oneNew.getContent());
        date.setText(oneNew.getDate());
        naive.setText("("+oneNew.getNaive()+")");
        exciting.setText("("+oneNew.getExciting()+")");
        huifu.setText("("+oneNew.getAnswerCount()+")");
        if (!oneNew.getRecent().equals("null"))
        recent.setText(oneNew.getRecent()+ " 更新");
        else
            recent.setText( " 无更新");
        authorName.setText(oneNew.getAuthorName());
        Glide.with(mContext).load(oneNew.getAuthorAvatar()).into(avater);
        if (oneNew.is_exciting()){
            excitingImage.setImageResource(R.mipmap.is_exciting);
        }else {
            excitingImage.setImageResource(R.mipmap.not_exciting);
        }
        if (oneNew.is_naive()){
            naiveImage.setImageResource(R.mipmap.is_naive);
        }else {
            naiveImage.setImageResource(R.mipmap.not_naive);
        }
        if (oneNew.is_favorite()){
            favorite.setImageResource(R.mipmap.is_favorite);
        }else {
            favorite.setImageResource(R.mipmap.not_favorite);
        }
        huiImage.setImageResource(R.mipmap.answer);
    }
    public void setOneNew(){
        oneNew.setId(mSharedPreferences.getInt("qid",0));
        oneNew.setIs_exciting(mSharedPreferences.getBoolean("IsExciting",false));
        oneNew.setIs_naive(mSharedPreferences.getBoolean("IsNaive",false));
        oneNew.setIs_favorite(mSharedPreferences.getBoolean("IsFavorite",false));
        oneNew.setTitle(mSharedPreferences.getString("Title",""));
        oneNew.setContent(mSharedPreferences.getString("Content",""));
        oneNew.setExciting(mSharedPreferences.getInt("Exciting",0));
        oneNew.setNaive(mSharedPreferences.getInt("Naive",0));
        oneNew.setDate(mSharedPreferences.getString("Date",""));
        oneNew.setRecent(mSharedPreferences.getString("Recent",""));
        oneNew.setAuthorName(mSharedPreferences.getString("authorName",""));
        oneNew.setAnswerCount(mSharedPreferences.getInt("AnswerCount",0));
        oneNew.setAuthorAvatar(mSharedPreferences.getString("Avatar",""));
    }
    public void answerListSetOneAnswer(JSONObject jsonObject){
        Answer oneAnswer= new Answer();
        try {
            //编号
            oneAnswer.setId(jsonObject.getInt("id"));
            //内容
            oneAnswer.setContent(jsonObject.getString("content"));
            //发布者头像地址
            oneAnswer.setAuthorAvatar(jsonObject.getString("authorAvatar"));
            //发布者名称
            oneAnswer.setAuthorName(jsonObject.getString("authorName"));
            //包含图片地址
            oneAnswer.setImages(jsonObject.getString("images"));
            //发布时间
            oneAnswer.setDate(jsonObject.getString("date"));
            //喜欢人数
            oneAnswer.setExciting(jsonObject.getInt("exciting"));
            //讨厌人数
            oneAnswer.setNaive(jsonObject.getInt("naive"));
            Log.d("AnswerActivity","content="+oneAnswer.getExciting());
            //该用户是否喜欢
            oneAnswer.setIs_exciting(jsonObject.getBoolean("is_exciting"));
            //该用户是否讨厌
            oneAnswer.setIs_naive(jsonObject.getBoolean("is_naive"));
            //该用户是否收藏
            oneAnswer.setIs_best(jsonObject.getBoolean("best"));
        }catch (Exception e){
            e.printStackTrace();
        }
        mAnswerList.add(oneAnswer);
    }

    @Override
    protected void onStart() {
        if (!isFirst) {
            refresh(0, true);
            mRecyclerView.scrollToPosition(0);
        }else {
            isFirst = false;
        }
        super.onStart();
    }
}
