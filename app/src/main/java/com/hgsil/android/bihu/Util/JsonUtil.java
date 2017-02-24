package com.hgsil.android.bihu.Util;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/2/9 0009.
 */

public class JsonUtil {
    public static String get(String data,String whatYouWant){
        try {
            JSONObject jsonObject = new JSONObject(data);
            String s = jsonObject.getString("\""+whatYouWant+"\"");
            return s;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
