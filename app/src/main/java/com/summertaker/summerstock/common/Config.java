package com.summertaker.summerstock.common;

import android.graphics.Color;
import android.os.Environment;

import java.text.DecimalFormat;

import okhttp3.MediaType;

public class Config {

    public final static String PACKAGE_NAME = "com.summertaker.summerstock";

    //public final static String USER_PREFERENCE_KEY = PACKAGE_NAME;

    //public static String USER_AGENT_DESKTOP = "Mozilla/5.0 (Macintosh; U; Mac OS X 10_6_1; en-US) AppleWebKit/530.5 (KHTML, like Gecko) Chrome/ Safari/530.5";
    //public static String USER_AGENT_MOBILE = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16";

    public static String DATA_PATH = Environment.getExternalStorageDirectory().toString() + java.io.File.separator + "android" + java.io.File.separator + "data" + java.io.File.separator + PACKAGE_NAME;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String KEY_RISING_ITEM = "rising_item";
    public static String KEY_RATE_OF_RETURN = "rate_of_return";
    public static String KEY_TOP_RECOMMENDATION = "top_recommendation";
    public static String KEY_RECOMMENDATION = "recommendation";
    public static String KEY_NEWS = "news";
    public static String KEY_FAVORITE = "favorite";
    public static String KEY_POSSESSION = "possession";

    public static String URL_MY_UPDATE = "http://summertaker.cafe24.com/stock/api/my_update.php";
    public static String URL_NEWS_ITEM = "http://summertaker.cafe24.com/stock/api/news_item.php";
    public static String URL_ITEM_LIST = "http://summertaker.cafe24.com/stock/api/item_list.php";
    public static String URL_ITEM_DETAIL = "http://summertaker.cafe24.com/stock/api/stock_detail.php";
    public static String URL_ITEM_AUTOCOMPLETE = "http://summertaker.cafe24.com/stock/api/item_autocomplete.php";
    //public static String URL_FLUC_LIST = "http://summertaker.cafe24.com/stock/api/fluc_list.php";
    //public static String URL_RECO_LIST = "http://summertaker.cafe24.com/stock/api/reco_list.php";

    //public static int COLOR_PRICE_CURRENT = Color.parseColor("#388E3C");
    public static int COLOR_PRICE_RISE = Color.parseColor("#D32F2F");
    public static int COLOR_PRICE_FALL = Color.parseColor("#1976D2");

    public static DecimalFormat NUMBER_FORMAT = new DecimalFormat("###,###,###");
    public static DecimalFormat FLOAT_FORMAT = new DecimalFormat("###,###.##");

    public static int PRICE_MIN = 50000;  // 최소 가격
    public static int PRICE_MAX = 300000; // 최대 가격
    public static float ADP_MIN = 1000;   // 전일비
    public static float ADR_MIN = 2.0f;   // 등락률
    public static float ROR_MIN = 10.0f;  // 추천일 후 누적 수익률
}
