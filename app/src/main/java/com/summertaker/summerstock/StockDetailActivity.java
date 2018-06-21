package com.summertaker.summerstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.summertaker.summerstock.common.BaseActivity;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.NewsData;
import com.summertaker.summerstock.parser.DaumNewsParser;
import com.summertaker.summerstock.util.OkHttpSingleton;
import com.summertaker.summerstock.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class StockDetailActivity extends BaseActivity {

    private String mCode;
    private String mName;
    private int mPrice;
    private float mPer;
    private float mRoe;

    private TextView mTvFavorite;
    //private ImageView mIvFavoriteOff;
    //private ImageView mIvFavoriteOn;
    private Button mBtnPossessionOn;
    private Button mBtnPossessionOff;

    private boolean mIsFirstLoading = true;

    private ScrollView mScrollView;
    private ArrayList<NewsData> mNewsDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail_activity);

        mContext = StockDetailActivity.this;

        setBaseStatusBar();

        Intent intent = getIntent();
        mCode = intent.getStringExtra("code");
        mName = intent.getStringExtra("name");
        mPrice = intent.getIntExtra("price", 0);
        mPer = intent.getFloatExtra("price", 0);
        mRoe = intent.getFloatExtra("price", 0);

        initToolbar(null);

        // 액션바 제목
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            String title = mName;
            if (mPrice > 0) {
                title = (mName.length() > 7) ? mName.substring(0, 7) : mName;
                title += " " + Config.NUMBER_FORMAT.format(mPrice) + "원";
            }
            actionBar.setTitle(title);
        }

        mScrollView = findViewById(R.id.scrollView);

        /*
        // 관심
        mTvFavorite = findViewById(R.id.tvFavorite);
        mTvFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyItem(Config.KEY_FAVORITE);
            }
        });
        */

        /*
        // 관심: Off
        mIvFavoriteOff = findViewById(R.id.ivFavoriteOff);
        mIvFavoriteOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyItem(Config.CATEGORY_KEY_FAVORITE);
            }
        });

        // 관심: On
        mIvFavoriteOn = findViewById(R.id.ivFavoriteOn);
        mIvFavoriteOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyItem(Config.CATEGORY_KEY_FAVORITE);
            }
        });
        */

        //loadItem();
        renderData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stock_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            /*
            case R.id.action_refresh:
                onRefreshClick();
                return true;
            */
            /*
            case R.id.action_open_in_new:
                String url = "http://m.stock.naver.com/item/main.nhn#/stocks/" + mCode + "/total"; // 네이버
                //String url = "http://m.finance.daum.net/m/item/main.daum?code=" + itemCode; // 다음
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                //startActivity(intent);
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mItemNm);
                intent.putExtra("url", url);
                startActivity(intent);
                return true;
            */
            case R.id.action_finish:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadItem() {
        String url = Config.URL_ITEM_DETAIL + "?item_cd=" + mCode;
        //Log.e(mTag, "url: " + url);

        /*
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response:\n" + response);
                parseItem(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseItem("");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //headers.put("User-agent", mSiteData.getUserAgent());
                return headers;
            }
        };

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
        */
    }

    private void parseItem(String response) {
        int prc = 0;
        int adp = 0;
        float adr = 0.0f;
        int recoCnt = 0;
        int adjPrc = 0;
        float wkRtn = 0.0f;
        float mnRtn = 0.0f;
        float mn3Rtn = 0.0f;

        int vol = 0;
        float per = 0.0f;
        float roe = 0.0f;

        String inReason = "";
        String favorite = "";
        String possession = "";

        try {
            JSONObject object = new JSONObject(response);
            mName = Util.getString(object, "item_nm");
            prc = Util.getInt(object, "prc");
            adp = Util.getInt(object, "adp");
            adr = BigDecimal.valueOf(Util.getDouble(object, "adr")).floatValue();

            recoCnt = Util.getInt(object, "reco_cnt");
            adjPrc = Util.getInt(object, "adj_prc");
            wkRtn = BigDecimal.valueOf(Util.getDouble(object, "wk_rtn")).floatValue();
            mnRtn = BigDecimal.valueOf(Util.getDouble(object, "mn_rtn")).floatValue();
            mn3Rtn = BigDecimal.valueOf(Util.getDouble(object, "mn3_rtn")).floatValue();

            vol = Util.getInt(object, "vol");
            per = BigDecimal.valueOf(Util.getDouble(object, "per")).floatValue();
            roe = BigDecimal.valueOf(Util.getDouble(object, "roe")).floatValue();

            inReason = Util.getString(object, "in_reason");

            favorite = Util.getString(object, "favorite");
            possession = Util.getString(object, "possession");

            inReason = inReason.replace("null", "");
            inReason = inReason.replace("▶", "-");
            inReason = inReason.replace("|", "\n");
            //Log.e(mTag, "inReason: " + inReason);

        } catch (JSONException e) {
            Log.e(mTag, e.getMessage());
        }

        toggleFavorite(favorite);

        /*
        // 현재가
        String prcText = Config.NUMBER_FORMAT.format(prc) + "원";
        //Log.e(mTag, mItemNm + ": " + prcText);
        TextView tvPrc = findViewById(R.id.tvPrice);
        tvPrc.setText(prcText);

        // 보유 종목: Off
        mBtnPossessionOff = findViewById(R.id.tvPossessionOff);
        if (possession.isEmpty()) {
            mBtnPossessionOff.setVisibility(View.VISIBLE);
        }
        mBtnPossessionOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyItem(Config.KEY_POSSESSION);
            }
        });
        */

        /*
        // 보유 종목: On
        mBtnPossessionOn = findViewById(R.id.tvPossession);
        if (!possession.isEmpty()) {
            mBtnPossessionOn.setVisibility(View.VISIBLE);
        }
        mBtnPossessionOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyItem(Config.KEY_POSSESSION);
            }
        });

        // 등락률
        String adrText = Config.FLOAT_FORMAT.format(adr) + "%";
        TextView tvAdr = findViewById(R.id.tvAdr);
        if (adr > 0.0) {
            adrText = "+" + adrText;
            tvAdr.setTextColor(Config.COLOR_PRICE_RISE);
        } else {
            tvAdr.setTextColor(Config.COLOR_PRICE_FALL);
        }
        //adrText = "(" + getString(R.string.advanced_decline_rate) + " " + adrText + ")";
        tvAdr.setText(adrText);
        */

        /*
        LinearLayout loReturn = findViewById(R.id.loReturn);
        if (adjPrc == 0) {
            loReturn.setVisibility(View.GONE);
        } else {
            loReturn.setVisibility(View.VISIBLE);

            // 목표가
            String adjPrcText = Config.NUMBER_FORMAT.format(adjPrc) + "원";
            TextView tvAdjPrc = findViewById(R.id.tvAdjPrc);
            if (adjPrc > prc) {
                tvAdjPrc.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                tvAdjPrc.setTextColor(Config.COLOR_PRICE_FALL);
            }
            tvAdjPrc.setText(adjPrcText);

            // 가격 차이
            int diff = adjPrc - prc;
            String diffPrcText = Config.NUMBER_FORMAT.format(diff) + "원";
            TextView tvDiffPrc = findViewById(R.id.tvDiffPrc);
            if (diff > 0) {
                diffPrcText = "+" + diffPrcText;
                tvDiffPrc.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                tvDiffPrc.setTextColor(Config.COLOR_PRICE_FALL);
            }
            diffPrcText = "(" + diffPrcText + ")";
            tvDiffPrc.setText(diffPrcText);

            // 수익률: 1주일
            String wkRtnText = Config.FLOAT_FORMAT.format(wkRtn) + "%";
            TextView tvWkRtn = findViewById(R.id.tvWkRtn);
            tvWkRtn.setText(wkRtnText);
            if (wkRtn > 0.0) {
                tvWkRtn.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                tvWkRtn.setTextColor(Config.COLOR_PRICE_FALL);
            }

            // 수익률: 1개월
            String mnRtnText = Config.FLOAT_FORMAT.format(mnRtn) + "%";
            TextView tvMnRtn = findViewById(R.id.tvMnRtn);
            tvMnRtn.setText(mnRtnText);
            if (mnRtn > 0.0) {
                tvMnRtn.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                tvMnRtn.setTextColor(Config.COLOR_PRICE_FALL);
            }

            // 수익률: 3개월
            String mn3RtnText = Config.FLOAT_FORMAT.format(mn3Rtn) + "%";
            TextView tvMn3Rtn = findViewById(R.id.tvMn3Rtn);
            tvMn3Rtn.setText(mn3RtnText);
            if (mn3Rtn > 0.0) {
                tvMn3Rtn.setTextColor(Config.COLOR_PRICE_RISE);
            } else {
                tvMn3Rtn.setTextColor(Config.COLOR_PRICE_FALL);
            }

            // 추천수
            LinearLayout loRecoCnt = findViewById(R.id.loRecoCnt);
            if (recoCnt == 0) {
                loRecoCnt.setVisibility(View.GONE);
            } else {
                String recoCntText = "+" + recoCnt;
                TextView tvRecoCnt = findViewById(R.id.tvRecoCnt);
                tvRecoCnt.setText(recoCntText);
            }
        }
        */

        /*
        //Log.e(mTag, "거래량: " + vol);
        LinearLayout loVol = findViewById(R.id.loVol);
        if (vol == 0) {
            loVol.setVisibility(View.GONE);
        } else {
            loVol.setVisibility(View.VISIBLE);

            // 거래량
            String volText = Config.NUMBER_FORMAT.format(vol);
            TextView tvVol = findViewById(R.id.tvVol);
            tvVol.setText(volText);

            // PER
            String perText = Config.NUMBER_FORMAT.format(per);
            TextView tvPer = findViewById(R.id.tvPer);
            tvPer.setText(perText);

            // ROE
            String roeText = Config.NUMBER_FORMAT.format(roe);
            TextView tvRoe = findViewById(R.id.tvRoe);
            tvRoe.setText(roeText);
        }
        */

        /*
        // 추천 이유
        TextView tvInReason = findViewById(R.id.tvInReason);
        if (inReason.isEmpty()) {
            tvInReason.setVisibility(View.GONE);
        } else {
            tvInReason.setText(inReason);
        }

        if (mIsFirstLoading) {
            loadChart();
        } else {
            stopAnimateRefresh();
        }
        */
    }

    private void renderData() {
        // 사이트 링크 - 네이버
        TextView tvNaverFinanceLink = findViewById(R.id.tvNaverFinanceLink);
        tvNaverFinanceLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://m.stock.naver.com/item/main.nhn#/stocks/" + mCode + "/total";
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mName);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        // 사이트 링크 - 다음
        TextView tvDaumFinanceLink = findViewById(R.id.tvDaumFinanceLink);
        tvDaumFinanceLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://m.finance.daum.net/m/item/main.daum?code=" + mCode;
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mName);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        loadChart();
    }

    private void loadChart() {

        final String d1Url = "https://ssl.pstatic.net/imgfinance/chart/mobile/candle/day/" + mCode + "_end.png";
        ImageView ivD1Chart = findViewById(R.id.ivD1Chart);
        Glide.with(mContext).load(d1Url).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivD1Chart);

        final String w1Url = "https://ssl.pstatic.net/imgfinance/chart/mobile/candle/week/" + mCode + "_end.png";
        ImageView ivW1Chart = findViewById(R.id.ivW1Chart);
        Glide.with(mContext).load(w1Url).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivW1Chart);

        //final String m1Url = "https://ssl.pstatic.net/imgfinance/chart/mobile/candle/month/" + mCode + "_end.png";
        //ImageView ivM1Chart = findViewById(R.id.ivM1Chart);
        //Glide.with(mContext).load(m1Url).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivM1Chart);

        /*
        //final String dayChartUrl = "https://ssl.pstatic.net/imgfinance/chart/item/area/day/" + mCode + ".png";
        final String dayChartUrl = "https://chart-finance.daumcdn.net/time3/real/" + mCode + "-290157.png";
        ImageView ivDayChart = findViewById(R.id.ivDayChart);
        Glide.with(mContext).load(dayChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivDayChart);
        /*
        ivDayChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(dayChartUrl);
            }
        });
        */

        //final String weekChartUrl = "https://ssl.pstatic.net/imgfinance/chart/item/area/week/" + mCode + ".png";
        //ImageView ivWeekChart = findViewById(R.id.ivWeekChart);
        //Glide.with(mContext).load(weekChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivWeekChart);
        /*
        ivWeekChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(weekChartUrl);
            }
        });
        */

        //final String monthChartUrl = "https://chart-finance.daumcdn.net/candle3/" + mCode + "-290157.png";
        //ImageView ivMonthChart = findViewById(R.id.ivMonthChart);
        //Glide.with(mContext).load(monthChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivMonthChart);
        /*
        ivMonthChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(monthChartUrl);
            }
        });
        */

        //final String month3ChartUrl = "https://chart-finance.daumcdn.net/time3/3month/" + mCode + "-290157.png"; // 다음
        //ImageView ivMonth3Chart = findViewById(R.id.ivMonth3Chart);
        //Glide.with(mContext).load(month3ChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivMonth3Chart);
        /*
        ivMonth3Chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(month3ChartUrl);
            }
        });
        */

        /*
        final String yearChartUrl = "https://ssl.pstatic.net/imgfinance/chart/item/area/year/"+mCode+".png";
        ImageView ivYearChart = findViewById(R.id.ivYearChart);
        Glide.with(mContext).load(yearChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivYearChart);
        ivYearChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(yearChartUrl);
            }
        });
        */

        final String year3ChartUrl = "https://chart-finance.daumcdn.net/time3/3year/" + mCode + "-290157.png;"; // 다음
        //final String year3ChartUrl = "https://ssl.pstatic.net/imgfinance/chart/item/area/year3/"+mCode+".png"; // 네이버
        ImageView ivYear3Chart = findViewById(R.id.ivYear3Chart);
        Glide.with(mContext).load(year3ChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivYear3Chart);
        /*
        ivYear3Chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(year3ChartUrl);
            }
        });
        */

        /*
        final String year5ChartUrl = "https://ssl.pstatic.net/imgfinance/chart/item/area/year5/" + mCode + ".png";
        ImageView ivYear5Chart = findViewById(R.id.ivYear5Chart);
        Glide.with(mContext).load(year5ChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivYear5Chart);
        ivYear5Chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(year5ChartUrl);
            }
        });
        */

        /*
        final String year10ChartUrl = "https://ssl.pstatic.net/imgfinance/chart/item/area/year10/" + mCode + ".png";
        ImageView ivYear10Chart = findViewById(R.id.ivYear10Chart);
        Glide.with(mContext).load(year10ChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivYear10Chart);
        ivYear10Chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onImageClick(year10ChartUrl);
            }
        });
        */

        loadNews();
    }

    private void loadNews() {
        String url = "http://finance.daum.net/item/news.daum?code=" + mCode;
        //Log.e(mTag, "url: " + url);

        Request request = new Request.Builder().url(url).get().build();
        OkHttpSingleton.getInstance().getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                //Log.e(">>>>>", "responseString\n" + responseString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseNews(responseString);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.e(mTag, "Error: " + e.getMessage());
            }
        });
    }

    private void parseNews(String response) {
        mNewsDataList = new ArrayList<>();

        DaumNewsParser daumNewsParser = new DaumNewsParser();
        daumNewsParser.parseList(mContext, mName, response, mNewsDataList);

        renderNews();
    }

    private void renderNews() {
        // 네이버 뉴스
        TextView tvNaverSearch = findViewById(R.id.tvNaverNews);
        tvNaverSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://m.search.naver.com/search.naver?where=m_news&query=" + mName;
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mName);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        // 다음 뉴스
        TextView tvDaumSearch = findViewById(R.id.tvDaumNews);
        tvDaumSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://m.search.daum.net/search?w=news&q=" + mName;
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mName);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        // 구글 뉴스
        TextView tvGoogleSearch = findViewById(R.id.tvGoogleNews);
        tvGoogleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com/search?tbm=nws&q=" + mName;
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mName);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        // 다음 금융 - 뉴스 목록
        StockDetailNewsAdapter adapter = new StockDetailNewsAdapter(mContext, mNewsDataList);

        ExpandableHeightListView listView = findViewById(R.id.newsListView);
        listView.setExpanded(true);
        listView.setFocusable(false);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsData newsData = (NewsData) parent.getItemAtPosition(position);

                String title = mName; //newsData.getItemNm();
                String url = newsData.getUrl();

                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        mIsFirstLoading = false;
    }

    protected void onToolbarClick() {
        mScrollView.scrollTo(0, 0);
    }

    private void onRefreshClick() {
        //startAnimateRefresh();
        //loadItem();
    }

    private void toggleFavorite(String item_cd) {
        //Log.e(mTag, "favorite: " + item_cd);

        if (item_cd.isEmpty()) {
            mTvFavorite.setText(getString(R.string.favorite_off));
            mTvFavorite.setTextColor(getResources().getColor(R.color.favorite_off));
            //mIvFavoriteOff.setVisibility(View.VISIBLE);
            //mIvFavoriteOn.setVisibility(View.GONE);
        } else {
            mTvFavorite.setText(getString(R.string.favorite_on));
            mTvFavorite.setTextColor(getResources().getColor(R.color.favorite_on));
            //mIvFavoriteOff.setVisibility(View.GONE);
            //mIvFavoriteOn.setVisibility(View.VISIBLE);
        }
    }

    private void togglePossession(String item_cd) {
        //Log.e(mTag, "posession: " + item_cd);

        if (item_cd.isEmpty()) {
            mBtnPossessionOff.setVisibility(View.VISIBLE);
            mBtnPossessionOn.setVisibility(View.GONE);
        } else {
            mBtnPossessionOff.setVisibility(View.GONE);
            mBtnPossessionOn.setVisibility(View.VISIBLE);
        }
    }

    private void updateMyItem(final String category) {
        startAnimateRefresh();

        /*
        String url = Config.URL_MY_UPDATE + "?item_cd=" + mCode + "&category=" + category;
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response:\n" + response);
                if (category.equals(Config.KEY_FAVORITE)) {
                    toggleFavorite(response);
                } else if (category.equals(Config.KEY_POSSESSION)) {
                    togglePossession(response);
                }
                stopAnimateRefresh();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Util.alert(mContext, getString(R.string.network_error), error.getMessage(), null);
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
        */
    }

    @Override
    protected void onSwipeRight() {
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
