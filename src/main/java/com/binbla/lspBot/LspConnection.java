package com.binbla.lspBot;

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
public final class LspConnection {
    public static String buildGetUrl(String keyWord, String groupID) {
        String buildUrl = Config.INSTANCE.getAddress();
        buildUrl += "?r18=" + Config.INSTANCE.getGroupMode().get(groupID);
        if (!("".equals(Config.INSTANCE.getApiKey()))) {
            buildUrl += "&apikey=" + Config.INSTANCE.getApiKey();
        }
        if (Config.INSTANCE.getWithProxyOrNot()) {
            buildUrl += "&proxy=" + Config.INSTANCE.getProxyAddress();
        }
        if (Config.INSTANCE.getSize1200()) {
            buildUrl += "&size1200=" + "true";
        }
        if (!("".equals(keyWord))) {
            buildUrl += "&keyword=" + keyWord;
        }
        buildUrl += "&num=" + Config.INSTANCE.getNum();
        return buildUrl;
    }

    public static String getJsonString(String keyWord, String groupID) {
        try {
            String buildURL = buildGetUrl(keyWord, groupID);
            URL url = new URL(buildURL);
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
