package com.summertaker.summerstock.common;

import android.app.Application;

import com.summertaker.summerstock.data.SiteData;

import java.util.ArrayList;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    public static final String TAG = BaseApplication.class.getSimpleName();

    private ArrayList<SiteData> mPagers;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mPagers = new ArrayList<>();
        //mPagers.add(new SiteData(Config.KEY_POSSESSION, "보유주", Config.URL_ITEM_LIST + "?category=" + Config.KEY_POSSESSION));
        //mPagers.add(new SiteData(Config.KEY_FAVORITE, "주시주", Config.URL_ITEM_LIST + "?category=" + Config.KEY_FAVORITE));
        mPagers.add(new SiteData(Config.KEY_RISING_STOCK, "상승 종목", "http://finance.naver.com/sise/sise_rise.nhn"));
        mPagers.add(new SiteData(Config.KEY_RATE_OF_RETURN, "수익률", "http://recommend.finance.naver.com/Home/GetYieldList"));
        mPagers.add(new SiteData(Config.KEY_TOP_RECOMMENDATION, "추천수", "http://recommend.finance.naver.com/Home/GetTopCompanyList"));
        mPagers.add(new SiteData(Config.KEY_RECOMMENDATION, "현재 추천", "http://recommend.finance.naver.com/Home/RecommendDetail"));
        //mPagers.add(new SiteData(Config.KEY_NEWS, "뉴스", ""));
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public ArrayList<SiteData> getPagers() {
        return mPagers;
    }
}
