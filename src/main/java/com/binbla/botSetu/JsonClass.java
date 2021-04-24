package com.binbla.botSetu;

import com.google.gson.Gson;

import java.util.List;

/**
 * >ClassName Json.java
 * >Description 从lolicon获取到的json,使用谷歌的gson把其转成对象
 * >Author binbla
 * >Version 1.0.0
 * >CreateTime 2021-04-20  10:25
 */
public class JsonClass {
    int code;
    String msg;
    int quota;
    int quota_min_ttl;
    int count;
    List<Data> data;

    public static JsonClass getAPI(String json) {
        Gson gson = new Gson();
        JsonClass object = gson.fromJson(json, JsonClass.class);
        return object;
    }
}

class Data {
    int pid;//作品 PID
    int p;//作品所在 P
    int uid;//作者 UID
    String title;//作品标题
    String author;//作者名（入库时，并过滤掉 @ 及其后内容）
    String url;//图片链接（可能存在有些作品因修改或删除而导致 404 的情况）
    Boolean r18;//是否 R18（在色图库中的分类，并非作者标识的 R18）
    int width;//原图宽度 px
    int height;//原图高度 px
    String[] tags;//作品标签，包含标签的中文翻译（有的话）
}
