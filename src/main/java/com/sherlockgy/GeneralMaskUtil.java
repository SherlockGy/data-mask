package com.sherlockgy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class GeneralMaskUtil {

    private static final String MASK = "****";
    private static final Map<String, Set<String>> sensitiveFieldsMap;

    // 在静态初始化块中，从配置文件中加载敏感字段
    static {
        sensitiveFieldsMap = new HashMap<>();
        try (InputStream input = GeneralMaskUtil.class.getClassLoader().getResourceAsStream("data_mask.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            for (String key : properties.stringPropertyNames()) {
                String[] fields = properties.getProperty(key).split(",");
                sensitiveFieldsMap.put(key, new HashSet<>(Arrays.asList(fields)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading sensitive fields", e);
        }
    }

    // 主要方法，接收一个对象，对其所有字段进行检查，如果字段是敏感字段，则进行掩码处理
    public static Object mask(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            Class<?> clazz = obj.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getType() == String.class) {
                    String fieldName = field.getName();
                    String originalValue = (String) field.get(obj);
                    if (originalValue != null) {
                        if (isSensitiveField(fieldName)) {
                            field.set(obj, MASK);
                        } else if (isJson(originalValue)) {
                            String maskedJson = maskJson(originalValue);
                            field.set(obj, maskedJson);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error masking sensitive fields", e);
        }

        return obj;
    }

    // 检查一个字符串是否是一个有效的 JSON 字符串
    private static boolean isJson(String value) {
        try {
            value = value.trim();
            if ((value.startsWith("{") && value.endsWith("}")) || (value.startsWith("[") && value.endsWith("]"))) {
                JSON.parseObject(value);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    // 对 JSON 字符串进行掩码处理
    private static String maskJson(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        for (String key : jsonObject.keySet()) {
            if (isSensitiveField(key)) {
                jsonObject.put(key, MASK);
            }
        }
        return jsonObject.toJSONString();
    }

    // 检查一个字段是否是敏感字段
    private static boolean isSensitiveField(String fieldName) {
        for (Set<String> fields : sensitiveFieldsMap.values()) {
            if (fields.contains(fieldName)) {
                return true;
            }
        }
        return false;
    }

    public static String maskJsonString(String json) {
        if (!isJson(json)) {
            return json;
        }

        Object obj = JSON.parse(json);
        maskJsonObj(obj);
        return JSON.toJSONString(obj);
    }

    private static void maskJsonObj(Object obj) {
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (value instanceof String) {
                    if (isSensitiveField(key)) {
                        jsonObject.put(key, MASK);
                    } else if (isJson(value.toString())) {
                        Object nestedObj = JSON.parse(value.toString());
                        maskJsonObj(nestedObj);
                        jsonObject.put(key, JSON.toJSONString(nestedObj));
                    }
                } else if (isJson(value.toString())) {
                    maskJsonObj(value);
                }
            }
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            for (Object value : jsonArray) {
                if (isJson(value.toString())) {
                    maskJsonObj(value);
                }
            }
        }
    }
}