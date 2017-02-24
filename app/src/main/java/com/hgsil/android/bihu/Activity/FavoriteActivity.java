package com.hgsil.android.bihu.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hgsil.android.bihu.Adapter.HomePageAdapter;
import com.hgsil.android.bihu.Information.News;
import com.hgsil.android.bihu.R;
import com.hgsil.android.bihu.Util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class FavoriteActivity extends AppCompatActivity {
    private List<News> mNewses = new ArrayList<>();
    static int page = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout ;
    private HomePageAdapter adapter;
    private RecyclerView recyclerView;
    private Context context;
    Handler mHandler = new Handler(){
        //1为第一次设置adapter 2为拉到底部加载 3为上拉刷新
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    adapter = new HomePageAdapter(mNewses,context);
                    recyclerView.setAdapter(adapter);
                    mNewses.clear();
                    break;
                case 2:
                    if(mNewses.size() == 0){
                        Toast.makeText(FavoriteActivity.this,"已经没有多的内容了",Toast.LENGTH_SHORT);
                    }
                    adapter.addItem(mNewses);
                    mNewses.clear();
                    break;
                case 3:
                    adapter.refresh(mNewses);
                    mNewses.clear();
                    break;
            }

        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityManeger.addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        context = this;
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.favorite_swipeRefresh);
        recyclerView = (RecyclerView)findViewById(R.id.favorite_recycler);
        TextView back = (TextView)findViewById(R.id.back_favorite);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(0,true);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        //第一次自动刷新
        refresh(page,false);

        page++;

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState ==RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisibleItem+ 1 ==adapter.getItemCount()){
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
    }
    private void refresh(int page, final boolean isShangLa){
        final int time = page;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                String token = sharedPreferences.getString("mToken","");
                try{
                    String url = new String("https://api.caoyue.com.cn/bihu/getFavoriteList.php");
                    String response = HttpUtil.post(url,"token="+token+"&"+"page="+time+""+"&"+"count=20");
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status")==200) {
                        Message message = new Message();
                        if (time == 0 && !isShangLa){
                            message.what = 1;}
                        else if (time != 0 && !isShangLa){
                            message.what = 2;
                        }
                        else if (isShangLa){
                            message.what = 3;
                        }
                        String returnData = jsonObject.getString("data");
                        JSONObject jsonObjectData = new JSONObject(returnData);
                        JSONArray jsonQuestions = jsonObjectData.getJSONArray("questions");
                        for (int i = 0; i < jsonQuestions.length(); i++) {
                            JSONObject jsonQuestion = jsonQuestions.getJSONObject(i);
                            newsListSetOneNew(jsonQuestion,mNewses);

                        }
                        mHandler.sendMessage(message);
                    }
                    else {
                        Looper.prepare();
                        Toast.makeText(FavoriteActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
    public void newsListSetOneNew(JSONObject jsonObject,List<News> newsList){
        News oneNew = new News();
        try {
            //编号
            oneNew.setId(jsonObject.getInt("id"));
            //标题
            oneNew.setTitle(jsonObject.getString("title"));
            //内容
            oneNew.setContent(jsonObject.getString("content"));
            //发布者头像地址
            oneNew.setAuthorAvatar(jsonObject.getString("authorAvatar"));
            //发布者名称
            oneNew.setAuthorName(jsonObject.getString("authorName"));
            //包含图片地址
            oneNew.setImages(jsonObject.getString("images"));
            //发布时间
            oneNew.setDate(jsonObject.getString("date"));
            //喜欢人数
            oneNew.setExciting(jsonObject.getInt("exciting"));
            //讨厌人数
            oneNew.setNaive(jsonObject.getInt("naive"));
            //最近回复
            oneNew.setRecent(jsonObject.getString("recent"));
            //回复人数
            oneNew.setAnswerCount(jsonObject.getInt("answerCount"));
            //该用户是否喜欢
            oneNew.setIs_exciting(jsonObject.getBoolean("is_exciting"));
            //该用户是否讨厌
            oneNew.setIs_naive(jsonObject.getBoolean("is_naive"));
            //该用户是否收藏
            oneNew.setIs_favorite(jsonObject.getBoolean("is_favorite"));
        }catch (Exception e){
            e.printStackTrace();
        }
        newsList.add(oneNew);

    }

    @Override
    protected void onDestroy() {
        ActivityManeger.removeActivity(this);
        super.onDestroy();
    }
}
