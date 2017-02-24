package com.hgsil.android.bihu.Util;

import android.accounts.NetworkErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/1/20 0020.
 */

public class HttpUtil {
    public static String post(String url, String content) {
        HttpURLConnection connection = null;
        try {
            URL mUrl = new URL(url);
            connection = (HttpURLConnection) mUrl.openConnection();

            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(10000);
            connection.setDoOutput(true);

            String data = content;
            OutputStream out = connection.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();

            int responseCode = connection.getResponseCode();
            if(responseCode==200){
                InputStream in = connection.getInputStream();
                String response = getStringFromInputStream(in);
                return response ;

            } else {
                throw new NetworkErrorException("response status is " + responseCode);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
            connection.disconnect();
        }
    }

    return null;
    }

    public static String get(String url){
        HttpURLConnection connection = null;
        try {
            URL mUrl = new URL(url);
            connection = (HttpURLConnection)mUrl.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            if(responseCode == 200){
                InputStream is = connection.getInputStream();
                String response = getStringFromInputStream(is);
                return response;
            }
            else throw new NetworkErrorException("response status is "+responseCode);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(connection == null)
                connection.disconnect();
        }
        return null;
    }

    public static String getStringFromInputStream(InputStream inputStream)
                throws IOException{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len=inputStream.read(buffer))!=-1){
            os.write(buffer,0,len);
        }
        inputStream.close();
        String response = os.toString();
        os.close();
        return response;
    }

}
