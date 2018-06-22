package com.summertaker.summerstock.common;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.summertaker.summerstock.data.Site;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    //public static final String mTag = BaseApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private ArrayList<Site> mPagerItems;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mPagerItems = new ArrayList<>();
        //mPagerItems.add(new Site(Config.KEY_POSSESSION, "보유주", Config.URL_ITEM_LIST + "?category=" + Config.KEY_POSSESSION));
        //mPagerItems.add(new Site(Config.KEY_FAVORITE, "주시주", Config.URL_ITEM_LIST + "?category=" + Config.KEY_FAVORITE));
        mPagerItems.add(new Site(Config.KEY_RISING, "주가 상승", "http://finance.naver.com/sise/sise_rise.nhn"));
        mPagerItems.add(new Site(Config.KEY_RETURN, "추천 수익률", "http://recommend.finance.naver.com/Home/GetYieldList"));
        mPagerItems.add(new Site(Config.KEY_TOP, "추천수 상위", "http://recommend.finance.naver.com/Home/GetTopCompanyList"));
        mPagerItems.add(new Site(Config.KEY_CURRENT, "현재 추천", "http://recommend.finance.naver.com/Home/RecommendDetail"));
        //mPagerItems.add(new Site(Config.KEY_NEWS, "뉴스", ""));
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public ArrayList<Site> getPagerItems() {
        return mPagerItems;
    }
}
