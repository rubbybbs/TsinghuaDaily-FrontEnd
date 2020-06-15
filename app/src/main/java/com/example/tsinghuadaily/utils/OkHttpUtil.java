package com.example.tsinghuadaily.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
    public static final MediaType jsonMediaType = MediaType.parse("application/json;charset=utf-8");

    private OkHttpUtil() {
    }

    public static OkHttpClient getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private static enum Singleton {
        INSTANCE;
        private OkHttpClient singleton;

        private Singleton() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(6L, TimeUnit.SECONDS);
            builder.readTimeout(6L, TimeUnit.SECONDS);
            builder.writeTimeout(6L, TimeUnit.SECONDS);
            ConnectionPool connectionPool = new ConnectionPool(50, 60, TimeUnit.SECONDS);
            builder.connectionPool(connectionPool);
            singleton = builder.build();
        }

        public OkHttpClient getInstance() {
            return singleton;
        }
    }

    //Get
    public static String get(String url) {
        try {
            Request request = new Request.Builder().url(url).build();
            Response response = OkHttpUtil.getInstance().newCall(request).execute();
            if (response.isSuccessful()) {
                String content = response.body().string();
                if (StringUtils.isNotBlank(content)) {
                    return content;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * post-form
     */
    public static String postForm(String url, Map<String, String> params) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> m : params.entrySet()) {
                builder.add(m.getKey(), m.getValue());
            }
            RequestBody body = builder.build();
            Request request = new Request.Builder().post(body).url(url).build();
            Response response = OkHttpUtil.getInstance().newCall(request).execute();
            if (response.isSuccessful()) {
                String content = response.body().string();
                if (StringUtils.isNotBlank(content)) {
                    return content;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //post-JSON
    public static String postJson(String url, String jsonBody) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(jsonMediaType, jsonBody))
                    .build();

            Response response = OkHttpUtil.getInstance().newCall(request).execute();
            if (response.isSuccessful()) {
                String content = response.body().string();
                if (StringUtils.isNotBlank(content)) {
                    return content;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
