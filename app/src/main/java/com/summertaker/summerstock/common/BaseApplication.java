package com.summertaker.summerstock.common;

import android.app.Application;

import com.summertaker.summerstock.data.Site;

import java.util.ArrayList;

public class BaseApplication extends Application {

    private static BaseApplication mInstance;

    public static final String TAG = BaseApplication.class.getSimpleName();

    private ArrayList<Site> mPagerItems;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        mPagerItems = new ArrayList<>();
        //mPagerItems.add(new Site(Config.KEY_POSSESSION, "보유주", Config.URL_ITEM_LIST + "?category=" + Config.KEY_POSSESSION));
        //mPagerItems.add(new Site(Config.KEY_FAVORITE, "주시주", Config.URL_ITEM_LIST + "?category=" + Config.KEY_FAVORITE));
        mPagerItems.add(new Site(Config.KEY_RISING_ITEM, "상승 종목", "http://finance.naver.com/sise/sise_rise.nhn"));
        mPagerItems.add(new Site(Config.KEY_RATE_OF_RETURN, "수익률", "http://recommend.finance.naver.com/Home/GetYieldList"));
        mPagerItems.add(new Site(Config.KEY_TOP_RECOMMENDATION, "추천수", "http://recommend.finance.naver.com/Home/GetTopCompanyList"));
        mPagerItems.add(new Site(Config.KEY_RECOMMENDATION, "현재 추천", "http://recommend.finance.naver.com/Home/RecommendDetail"));
        //mPagerItems.add(new Site(Config.KEY_NEWS, "뉴스", ""));
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public ArrayList<Site> getPagerItems() {
        return mPagerItems;
    }
}
