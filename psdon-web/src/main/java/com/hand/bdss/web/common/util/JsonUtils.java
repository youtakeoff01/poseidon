package com.hand.bdss.web.common.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtils {

    /**
     * 将json字符串转化为object对象
     *
     * @return
     */
    public static <T> T toObject(String json, Class<T> cls) {
        T object = JSON.parseObject(json, cls);
        return object;
    }

    /**
     * 将object 转化为json 字符串
     */
    public static String toJson(Object object) {
        String json = JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect,SerializerFeature.WriteMapNullValue);
        return json;
    }

    /**
     * 将json字符串转化为List 对象
     */
    public static <T> List<T> toArray(String json, Class<T> cls) {
        List<T> lists = JSON.parseArray(json, cls);
        return lists;
    }
}
