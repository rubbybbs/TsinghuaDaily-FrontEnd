package com.example.tsinghuadaily.utils;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtils {
    private static OkHttpUtils okHttpUtils = null;

    private OkHttpUtils() {

    }

    public static OkHttpUtils getInstance() {
        if (okHttpUtils == null)
        {
            synchronized (OkHttpUtils.class) {
                if (okHttpUtils == null)
                    okHttpUtils = new OkHttpUtils();
            }
        }
        return okHttpUtils;
    }

    private static OkHttpClient okHttpClient = null;

    private static synchronized OkHttpClient getOkHttpClient() {
        if (okHttpClient == null)
        {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.i("lj", "log:" + message);
                }
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient = new OkHttpClient.Builder()
                                            .addInterceptor(interceptor)
                                            .addInterceptor(new Interceptor() {
                                                @NotNull
                                                @Override
                                                public Response intercept(@NotNull Chain chain) throws IOException {
                                                    Request request = chain.request()
                                                            .newBuilder()
                                                            .addHeader("source", "android")
                                                            .build();
                                                    return chain.proceed(request);
                                                }
                                            })
                                            .build();
        }
        return okHttpClient;
    }

    /**
     * doGet
     */
    public void doGet(String url, Callback callback) {
        //创建okhttp
        OkHttpClient okHttpClient = getOkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * doPost
     */
    public void doPost(String url, Map<String, String> params, Callback callback) {
        OkHttpClient okHttpClient = getOkHttpClient();
        //请求体
        FormBody.Builder formBody = new FormBody.Builder();
        for (String key : params.keySet()) {
            //遍历map集合
            formBody.add(key, params.get(key));
        }
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }




}
