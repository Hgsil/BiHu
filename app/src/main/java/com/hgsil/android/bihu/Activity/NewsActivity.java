package com.hgsil.android.bihu.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hgsil.android.bihu.Adapter.HomePageAdapter;
import com.hgsil.android.bihu.Information.News;
import com.hgsil.android.bihu.R;
import com.hgsil.android.bihu.Util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017/2/7 0007.
 */

public class NewsActivity extends AppCompatActivity {
    private List<News> mNewses = new ArrayList<>();
    int page = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout ;
    private DrawerLayout mDrawerLayout;
    private HomePageAdapter adapter;
    private RecyclerView recyclerView;
    String avaterUrl;
    CircleImageView avatar;
    TextView userName;
    private Context context;
    private boolean isExit;
    boolean isFirst;
    boolean isBackToLogin = false;
    boolean isChangeAvater = false ;

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
                        Toast.makeText(NewsActivity.this,"已经没有多的内容了",Toast.LENGTH_SHORT);
                    }
                    adapter.addItem(mNewses);
                    mNewses.clear();
                    break;
                case 3:
                    adapter.refresh(mNewses);
                    mNewses.clear();
                    break;
                case 9:
                    Glide.with(NewsActivity.this).load(avaterUrl).into(avatar);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ActivityManeger.addActivity(this);
        isFirst= true;
        context = this;
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.news_swipeRefresh);
        recyclerView = (RecyclerView)findViewById(R.id.news_recycler_view);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_newsActivity);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_newsActivity);
        //设置头像
        final NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        View header = navView.inflateHeaderView(R.layout.nav_header);
        avatar = (CircleImageView)header.findViewById(R.id.nav_avatar);
        userName = (TextView)header.findViewById(R.id.nav_username);
        SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        userName.setText(sharedPreferences.getString("mUsername",""));
        avaterUrl = sharedPreferences.getString("userAvatar","");
        Glide.with(NewsActivity.this).load(avaterUrl).into(avatar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_changeAvater);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_back:
                        mDrawerLayout.closeDrawers();
                        return true;
                    case R.id.nav_changePassword:
                        Intent intent = new Intent(NewsActivity.this,ChangePasswordActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_favorite:
                        Intent intent1 = new Intent(NewsActivity.this,FavoriteActivity.class);
                        startActivity(intent1);
                        return true;
                    case R.id.nav_changeAvater:
                        isChangeAvater = true;
                        Intent intent2 = new Intent(NewsActivity.this,ChangeAvaterActivity.class);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_backToLogin:
                        isBackToLogin = true;
                        finish();
                        return true;


                    default:
                        return true;
                }
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
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsActivity.this,AddNewsActivity.class);
                startActivity(intent);
            }
        });

    }

    //网络请求 给news对象集合赋值和上拉加载
    private void refresh(int page, final boolean isShangLaOrAdd){
        final int time = page;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
                String token = sharedPreferences.getString("mToken","");
                try{
                    String url = new String("https://api.caoyue.com.cn/bihu/getQuestionList.php ");
                    String response = HttpUtil.post(url,"token="+token+"&"+"page="+time+""+"&"+"count=20");
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status")==200) {
                        Message message = new Message();
                        if (time == 0 && !isShangLaOrAdd){
                        message.what = 1;}
                        else if (time != 0 && !isShangLaOrAdd){
                            message.what = 2;
                        }
                        else if (isShangLaOrAdd){
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
                        Toast.makeText(NewsActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
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
        if (isBackToLogin)
        {
            Intent intent = new Intent(NewsActivity.this,LoginActivity.class);
            intent.putExtra("isBackToLogin",true);
            startActivity(intent);
        }
        ActivityManeger.removeActivity(this);
        super.onDestroy();
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

    //给每个new对象赋值
    public void newsListSetOneNew(JSONObject jsonObject,List<News> newsList){
        News oneNew = new News();
        try {
            //编号
            oneNew.setId(jsonObject.getInt("id"));
            Log.d("NewsActivity","qid="+oneNew.getId());

            //标题
            oneNew.setTitle(jsonObject.getString("title"));
            //内容
            oneNew.setContent(jsonObject.getString("content"));
            //发布者头像地址
            oneNew.setAuthorAvatar(jsonObject.getString("authorAvatar"));
            Log.d("NewsActivity","avatar="+oneNew.getAuthorAvatar());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {

        //将刚添加的问题加入，然后返回顶部
        if (!isFirst) {
            refresh(0, true);
            recyclerView.scrollToPosition(0);
        }else {
            isFirst = false;
            ActivityManeger.finishAllExceptSelf(this);
        }
        if (isChangeAvater){
            SharedPreferences sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
            avaterUrl = sharedPreferences.getString("userAvatar","");
            Message message = new Message();
            message.what = 9;
            mHandler.sendMessage(message);
            isChangeAvater = false ;
        }

        super.onStart();
    }


}
