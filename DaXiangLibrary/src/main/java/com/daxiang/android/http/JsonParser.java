package com.daxiang.android.http;

import android.text.TextUtils;

import com.daxiang.android.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Json解析；
 * Created by xiangfei.wei on 2017/8/22.
 */

public class JsonParser {

    public static <T> List<T> parseJsonArray(String json,
                                             Type type) {
        if (TextUtils.isEmpty(json)) return null;
        Gson gson = new Gson();
        ArrayList<T> list = gson.fromJson(json, type);// new TypeToken<List<MarketEventInfo>>(){}.getType()
        return list;
    }

    /**
     * @param json  json字符串；
     * @param clazz T.class，T为单个json元素对应的Java bean；
     * @param <T>
     * @return
     */
    public static <T> List<T> parseJsonArray(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json))
            return null;
        try {
            List<T> lst = new ArrayList<T>();
            JsonArray array = new com.google.gson.JsonParser().parse(json).getAsJsonArray();
            for (final JsonElement elem : array) {
                lst.add(new Gson().fromJson(elem, clazz));
            }
            return lst;
        } catch (Exception e) {
            LogUtils.e("JsonUtil", e.toString());
            return null;
        }
    }

    public static <T> T parseJsonObject(String json, Class<T> clazz) {
        if (TextUtils.isEmpty(json))
            return null;
        try {
            return new Gson().fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*JsonElement jsonElement = new Gson().fromJson(sb.toString(), JsonElement.class);
           java.lang.reflect.Type mType = new TypeToken<CachePaths>() { }.getType();
           CachePaths cachePaths = new Gson().fromJson(jsonElement, mType);*/
    public static <T> T parseJsonObject(File file, Class<T> clazz) {
        Reader reader = null;
        try {
            reader = new FileReader(file);
            return new Gson().fromJson(reader, clazz);
        } catch (FileNotFoundException notFound) {
            notFound.printStackTrace();
        } catch (JsonSyntaxException jse) {
            jse.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
