package com.occs.ldsoft.occs;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yeliu on 15/7/30.
 * web API访问方法，get和post，用就可以
 */
public class WebFetcher {

    public static final String TAG = "WebFetcher";

    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        Log.i(TAG,urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }finally {
            connection.disconnect();
        }
    }

    byte[] postUrlBytes(String urlSpec) throws IOException {
        Log.i(TAG,urlSpec);
        StringBuffer chaine = new StringBuffer("");
        try {
            URL url = new URL(urlSpec);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        return String.valueOf(chaine).getBytes();
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public String posttUrl(String urlSpec) throws IOException {
        return new String(postUrlBytes(urlSpec));
    }

    public String fetchItems(String url){
        try {
            return(getUrl(url));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to fetch items from get", e);
        }
        return null;
    }

    public String fetchItemsPost(String url){
        try {
            return(posttUrl(url));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to fetch items from post", e);
        }
        return null;
    }

}
