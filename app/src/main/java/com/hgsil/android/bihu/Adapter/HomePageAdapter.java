package com.hgsil.android.bihu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hgsil.android.bihu.Activity.AnswerActivity;


import com.hgsil.android.bihu.Information.News;
import com.hgsil.android.bihu.R;

import com.hgsil.android.bihu.Util.HttpUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/7 0007.
 */

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.ViewHolder> {
    private List<News> mNewses;
    Context mContext;
    News oneNew;
    String token;
    SharedPreferences mSharedPreferences;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView content;
        TextView date;
        TextView naive;
        TextView huifu;
        TextView recent;
        TextView authorName;
        TextView exciting;
        de.hdodenhof.circleimageview.CircleImageView avater;
        ImageView huiImage;
        ImageView excitingImage;
        ImageView naiveImage;
        ImageView favorite;
        public Handler mHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 单数为取消，双数为选定
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
                    case 0:
                        Toast.makeText(mContext,"操作失败,请检查网络",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.news_title);
            content = (TextView)itemView.findViewById(R.id.news_content);
            date = (TextView)itemView.findViewById(R.id.news_date);
            naive = (TextView)itemView.findViewById(R.id.news_naive);
            huifu = (TextView)itemView.findViewById(R.id.news_huifu);
            exciting = (TextView)itemView.findViewById(R.id.news_exciting);
            recent = (TextView)itemView.findViewById(R.id.news_recent);
            authorName = (TextView)itemView.findViewById(R.id.news_username);
            excitingImage = (ImageView)itemView.findViewById(R.id.news_isExciting_image);
            naiveImage = (ImageView)itemView.findViewById(R.id.news_isNaive_image);
            favorite = (ImageView)itemView.findViewById(R.id.news_favorite);
            huiImage = (ImageView)itemView.findViewById(R.id.news_huifu_image);
            avater = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.news_avater);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.news_title:
                case R.id.news_huifu_image:
                case R.id.news_content:
                    Intent intent = new Intent(v.getContext(), AnswerActivity.class);
                    SharedPreferences.Editor editor =
                            mContext.getSharedPreferences("data",mContext.MODE_PRIVATE).edit();
                    editor.putInt("qid",oneNew.getId());
                    editor.putString("Title",oneNew.getTitle());
                    editor.putString("Content",oneNew.getContent());
                    editor.putString("authorName",oneNew.getAuthorName());
                    editor.putString("Recent",oneNew.getRecent());
                    editor.putString("Date",oneNew.getDate());
                    editor.putString("Avatar",oneNew.getAuthorAvatar());
                    editor.putInt("AnswerCount",oneNew.getAnswerCount());
                    editor.putInt("Exciting",oneNew.getExciting());
                    editor.putInt("Naive",oneNew.getNaive());
                    editor.putBoolean("Favorite",oneNew.is_favorite());
                    editor.putBoolean("IsNaive",oneNew.is_naive());
                    editor.putBoolean("IsExciting",oneNew.is_exciting());
                    editor.apply();

                    v.getContext().startActivity(intent);
                    break;
                case R.id.news_isExciting_image:
                    if (oneNew.is_exciting()){
                       cancleExcitingOrNaive("exciting",oneNew.getId());
                    }else {
                        setExcitingOrNaive("exciting",oneNew.getId());
                    }break;
                case R.id.news_isNaive_image:
                    if (oneNew.is_naive()){
                        cancleExcitingOrNaive("naive",oneNew.getId());
                    }else {
                        setExcitingOrNaive("naive",oneNew.getId());
                    }break;
                case R.id.news_favorite:
                    if (oneNew.is_favorite()){
                        cancleExcitingOrNaive("favorite",oneNew.getId());
                    }else {
                        setExcitingOrNaive("favorite",oneNew.getId());
                    }break;

            }
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
                                mHandle.sendMessage(message);
                            }
                            else {
                                Message message=new Message();
                                message.what= 0 ;
                                mHandle.sendMessage(message);
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
                                mHandle.sendMessage(message);
                            }
                            else {
                                Message message=new Message();
                                message.what=0;
                                mHandle.sendMessage(message);
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
                                mHandle.sendMessage(message);
                            }
                            else {
                                Message message=new Message();
                                message.what=0;
                                mHandle.sendMessage(message);
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
                                mHandle.sendMessage(message);
                            }
                            else {
                                Message message=new Message();
                                message.what=0;
                                mHandle.sendMessage(message);
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
                                mHandle.sendMessage(message);
                            }
                            else {
                                Message message=new Message();
                                message.what=0;
                                mHandle.sendMessage(message);
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
                                mHandle.sendMessage(message);
                            }
                            else {
                                Message message=new Message();
                                message.what=0;
                                mHandle.sendMessage(message);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
    }



    public HomePageAdapter(List<News> newses,Context context){
        mNewses =new ArrayList<>();
        mNewses.addAll(newses);
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("data",context.MODE_PRIVATE);
        token = mSharedPreferences.getString("mToken","");
    }

    @Override
    public HomePageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(HomePageAdapter.ViewHolder holder, int position) {
        oneNew = mNewses.get(position);

        holder.title.setText(oneNew.getTitle());
        holder.content.setText(oneNew.getContent());
        holder.date.setText(oneNew.getDate());
        holder.naive.setText("("+oneNew.getNaive()+")");
        holder.exciting.setText("("+oneNew.getExciting()+")");
        holder.huifu.setText("("+oneNew.getAnswerCount()+")");
        holder.authorName.setText(oneNew.getAuthorName());
        if (oneNew.getRecent().equals("null")){
            holder.recent.setText(oneNew.getDate()+" 更新");
        }else if (!oneNew.getRecent().equals("null")){
            holder.recent.setText(oneNew.getRecent()+" 更新");
        }
        Glide.with(mContext).load(oneNew.getAuthorAvatar()).into(holder.avater);
        if (oneNew.is_favorite()){
            holder.favorite.setImageResource(R.mipmap.is_favorite);
        }else if (!oneNew.is_favorite()){
            holder.favorite.setImageResource(R.mipmap.not_favorite);
        }
        holder.huiImage.setImageResource(R.mipmap.answer);
        if (oneNew.is_exciting()){
            holder.excitingImage.setImageResource(R.mipmap.is_exciting);
        }
        else if (!oneNew.is_exciting()){
            holder.excitingImage.setImageResource(R.mipmap.not_exciting);
        }
        if (oneNew.is_naive()){
            holder.naiveImage.setImageResource(R.mipmap.is_naive);
        }
        else if (!oneNew.is_naive()){
            holder.naiveImage.setImageResource(R.mipmap.not_naive);
        }


        holder.content.setOnClickListener(holder);
        holder.excitingImage.setOnClickListener(holder);
        holder.naiveImage.setOnClickListener(holder);
        holder.favorite.setOnClickListener(holder);


    }


    @Override
    public int getItemCount() {
        return mNewses.size();
    }
    public void addItem(List<News> newses){

        mNewses.addAll(newses);
        notifyDataSetChanged();
    }
    public void refresh(List<News> newses){
        mNewses.clear();
        mNewses.addAll(newses);
        notifyDataSetChanged();
    }

}
