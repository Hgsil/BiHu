package com.hgsil.android.bihu.Information;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public class Answer {
    private int id ;
    private String title;
    //内容
    private String content ;
    //发布者头像地址
    private String authorAvatar;
    //发布者名称
    private String authorName;
    //包含图片地址
    private String images ;
    //发布时间
    private String date;
    //喜欢人数
    private int exciting;
    //讨厌人数
    private int naive ;


    //该用户是否喜欢
    private boolean is_exciting;
    //该用户是否讨厌
    private boolean is_naive;
    //该用户是否收藏
    private boolean is_best;

    public Answer(){

    }



    public int getId() {
        return id;
    }

    public boolean is_best() {
        return is_best;
    }

    public void setIs_best(boolean is_best) {
        this.is_best = is_best;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String data) {
        this.date = data;
    }

    public int getExciting() {
        return exciting;
    }

    public void setExciting(int exciting) {
        this.exciting = exciting;
    }

    public int getNaive() {
        return naive;
    }

    public void setNaive(int naive) {
        this.naive = naive;
    }

    public boolean is_exciting() {
        return is_exciting;
    }

    public void setIs_exciting(boolean is_exciting) {
        this.is_exciting = is_exciting;
    }

    public boolean is_naive() {
        return is_naive;
    }

    public void setIs_naive(boolean is_naive) {
        this.is_naive = is_naive;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
