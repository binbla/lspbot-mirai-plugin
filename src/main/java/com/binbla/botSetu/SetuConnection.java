package com.binbla.botSetu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * >ClassName LSP.java
 * >Description 这个没啥好说的，构造url,连接，抓取返回值（api返回的json）
 * >Author binbla
 * >Version 1.0.0
 * >CreateTime 2021-04-19  22:36
 */
public final class SetuConnection {
    public static String getJsonString(String buildUrl) {
        try {
            URL url = new URL(buildUrl);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception();
            }
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String content = bufferedReader.readLine();
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
            return content;
        } catch (Exception e) {
            return "";
        }
    }
}
