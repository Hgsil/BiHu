package com.hgsil.android.bihu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hgsil.android.bihu.Activity.AnswerActivity;
import com.hgsil.android.bihu.Information.Answer;
import com.hgsil.android.bihu.Information.News;
import com.hgsil.android.bihu.R;
import com.hgsil.android.bihu.Util.HttpUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.Viewholder>{
    Context mContext;
    Answer oneAnswer;
    List<Answer> mAnswers ;
    int qid;
    String token;
    SharedPreferences mSharedPreferences;

    class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView content;
        TextView date;
        TextView naive;
        TextView authorName;
        TextView exciting;
        de.hdodenhof.circleimageview.CircleImageView avater;
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
                        oneAnswer.setExciting(oneAnswer.getExciting()-1);
                        oneAnswer.setIs_exciting(false);
                        excitingImage.setImageResource(R.mipmap.not_exciting);
                        exciting.setText("("+oneAnswer.getExciting()+")");
                        Toast.makeText(mContext,"取消喜欢成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        oneAnswer.setExciting(oneAnswer.getExciting()+1);
                        oneAnswer.setIs_exciting(true);
                        excitingImage.setImageResource(R.mipmap.is_exciting);
                        exciting.setText("("+oneAnswer.getExciting()+")");
                        Toast.makeText(mContext,"喜欢成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        oneAnswer.setNaive(oneAnswer.getNaive()-1);
                        oneAnswer.setIs_naive(false);
                        naiveImage.setImageResource(R.mipmap.not_naive);
                        naive.setText("("+oneAnswer.getNaive()+")");
                        Toast.makeText(mContext,"取消讨厌成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        oneAnswer.setNaive(oneAnswer.getNaive()+1);
                        oneAnswer.setIs_naive(true);
                        naiveImage.setImageResource(R.mipmap.is_naive);
                        naive.setText("("+oneAnswer.getNaive()+")");
                        Toast.makeText(mContext,"讨厌成功",Toast.LENGTH_SHORT).show();
                        break;

                    case 6:
                        favorite.setImageResource(R.mipmap.is_caina);
                        oneAnswer.setIs_best(true);
                        Toast.makeText(mContext,"采纳成功",Toast.LENGTH_SHORT).show();
                        break;
                    case 0:
                        Toast.makeText(mContext,"操作失败,请检查网络",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };



        public Viewholder(View itemView) {
            super(itemView);

            content = (TextView)itemView.findViewById(R.id.answer_content);
            date = (TextView)itemView.findViewById(R.id.answer_date);
            naive = (TextView)itemView.findViewById(R.id.answer_naive);
            exciting = (TextView)itemView.findViewById(R.id.answer_exciting);
            authorName = (TextView)itemView.findViewById(R.id.answer_username);
            excitingImage = (ImageView)itemView.findViewById(R.id.answer_isExciting_image);
            naiveImage = (ImageView)itemView.findViewById(R.id.answer_isNaive_image);
            favorite = (ImageView)itemView.findViewById(R.id.answer_favorite);
            avater = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.answer_avater);
        }
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.answer_isExciting_image:
                    if (oneAnswer.is_exciting()){
                        cancleExcitingOrNaive("exciting",oneAnswer.getId());
                    }else {
                        setExcitingOrNaive("exciting",oneAnswer.getId());
                    }break;
                case R.id.answer_isNaive_image:
                    if (oneAnswer.is_naive()){
                        cancleExcitingOrNaive("naive",oneAnswer.getId());
                    }else {
                        setExcitingOrNaive("naive",oneAnswer.getId());
                    }break;
                case R.id.answer_best:
                    if (!oneAnswer.is_best()){
                        setExcitingOrNaive("best",oneAnswer.getId());
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
                            String data = HttpUtil.post(url,"id="+id+"&type=2&token="+token);
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
                            String data = HttpUtil.post(url,"id="+id+"&type=2&token="+token);
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

        }
        //点上exciting或者naive后将数据传上去
        public void setExcitingOrNaive(String excitingOrNaive, final int id){
            if (excitingOrNaive.equals("exciting")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String url = new String("https://api.caoyue.com.cn/bihu/exciting.php");
                            String data = HttpUtil.post(url,"id="+id+"&type=2&token="+token);
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
                            String data = HttpUtil.post(url,"id="+id+"&type=2&token="+token);
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

            } else if (excitingOrNaive.equals("best")){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String url = new String("https://api.caoyue.com.cn/bihu/accept.php");
                            String data = HttpUtil.post(url,"qid="+qid+"&aid="+id+"&token="+token);
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

    public AnswerAdapter(List<Answer> answerList,Context context){
        mAnswers = new ArrayList<>();
        mAnswers.addAll(answerList);
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences("data",mContext.MODE_PRIVATE);
        token = mSharedPreferences.getString("mToken","");
        qid = mSharedPreferences.getInt("qid",0);
    }

    @Override
    public AnswerAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_item,parent,false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(AnswerAdapter.Viewholder holder, int position) {
        oneAnswer = mAnswers.get(position);

        holder.content.setText(oneAnswer.getContent());
        holder.date.setText(oneAnswer.getDate());
        holder.naive.setText("("+oneAnswer.getNaive()+")");
        holder.exciting.setText("("+oneAnswer.getExciting()+")");

        holder.authorName.setText(oneAnswer.getAuthorName());
        Glide.with(mContext).load(oneAnswer.getAuthorAvatar()).into(holder.avater);
        if (oneAnswer.is_best()){
            holder.favorite.setImageResource(R.mipmap.is_caina);
        }else if (!oneAnswer.is_best()){
            holder.favorite.setImageResource(R.mipmap.no_caina);
        }

        if (oneAnswer.is_exciting()){
            holder.excitingImage.setImageResource(R.mipmap.is_exciting);
        }
        else if (!oneAnswer.is_exciting()){
            holder.excitingImage.setImageResource(R.mipmap.not_exciting);
        }
        if (oneAnswer.is_naive()){
            holder.naiveImage.setImageResource(R.mipmap.is_naive);
        }
        else if (!oneAnswer.is_naive()){
            holder.naiveImage.setImageResource(R.mipmap.not_naive);
        }


        holder.content.setOnClickListener(holder);
        holder.excitingImage.setOnClickListener(holder);
        holder.naiveImage.setOnClickListener(holder);
        holder.favorite.setOnClickListener(holder);

    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }
    public void addItem(List<Answer> answers){

        mAnswers.addAll(answers);
        notifyDataSetChanged();
    }
    public void refresh(List<Answer> answers){
        mAnswers.clear();
        mAnswers.addAll(answers);
        notifyDataSetChanged();

    }
}
