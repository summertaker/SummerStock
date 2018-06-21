package com.summertaker.summerstock.util;

import okhttp3.OkHttpClient;

public class OkHttpSingleton {
    private static OkHttpSingleton singletonInstance;

    private OkHttpClient client;

    private OkHttpSingleton() {
        client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    }

    public static OkHttpSingleton getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new OkHttpSingleton();
        }
        return singletonInstance;
    }

    public OkHttpClient getClient() {
        return client;
    }
}