package com.summertaker.summerstock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.summertaker.summerstock.common.BaseActivity;
import com.summertaker.summerstock.common.BaseApplication;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.Item;
import com.summertaker.summerstock.data.News;
import com.summertaker.summerstock.parser.DaumNewsParser;
import com.summertaker.summerstock.parser.DaumParser;

import java.util.ArrayList;

public class DetailActivity extends BaseActivity {

    private String mCode;

    //private TextView mTvFavorite;
    //private ImageView mIvFavoriteOff;
    //private ImageView mIvFavoriteOn;
    //private Button mBtnPossessionOn;
    //private Button mBtnPossessionOff;

    private Item mItem;

    private ScrollView mScrollView;
    private ArrayList<News> mNewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        mContext = DetailActivity.this;

        setBaseStatusBar();

        Intent intent = getIntent();
        mCode = intent.getStringExtra("code");

        initToolbar(null);

        mScrollView = findViewById(R.id.scrollView);

        mNewsList = new ArrayList<>();

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

        startRefreshAnimation();
        loadData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                onRefreshClick();
                return true;
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

    private void loadData() {
        String url = Config.URL_ITEM_DETAIL + mCode;
        //Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response:\n" + response);
                parseData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseData("");
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
    }

    private void parseData(String response) {
        DaumParser daumParser = new DaumParser();
        mItem = daumParser.parseItemDetail(response);

        //toggleFavorite(favorite);

        renderData();
    }

    private void renderData() {
        // 액션바 제목
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && mItem.getName() != null) {
            String title = mItem.getName();
            if (mItem.getPrice() > 0) {
                title = (mItem.getName().length() > 5) ? mItem.getName().substring(0, 5) : mItem.getName();
                title += " " + Config.NUMBER_FORMAT.format(mItem.getPrice()) + "원";
            }
            actionBar.setTitle(title);
        }

        // 현재가
        String price = Config.NUMBER_FORMAT.format(mItem.getPrice());
        TextView tvPrice = findViewById(R.id.tvPrice);
        tvPrice.setText(price);
        if (mItem.getAdr() > 0) {
            tvPrice.setTextColor(Config.COLOR_PRICE_RISE);
        } else {
            tvPrice.setTextColor(Config.COLOR_PRICE_FALL);
        }

        // 등락률
        String adr = Config.FLOAT_FORMAT.format(mItem.getAdr()) + "%";
        TextView tvAdr = findViewById(R.id.tvAdr);
        if (mItem.getAdr() > 0) {
            adr = "+" + adr;
            tvAdr.setTextColor(Config.COLOR_PRICE_RISE);
        } else {
            tvAdr.setTextColor(Config.COLOR_PRICE_FALL);
        }
        tvAdr.setText(adr);

        // 전일비
        String adp = Config.NUMBER_FORMAT.format(mItem.getAdp());
        TextView tvAdp = findViewById(R.id.tvAdp);
        if (mItem.getAdr() > 0) {
            adp = "+" + adp;
            tvAdp.setTextColor(Config.COLOR_PRICE_RISE);
        } else {
            adp = "-" + adp;
            tvAdp.setTextColor(Config.COLOR_PRICE_FALL);
        }
        tvAdp.setText(adp);

        // 사이트 링크 - 네이버
        TextView tvNaverFinanceLink = findViewById(R.id.tvNaverFinanceLink);
        tvNaverFinanceLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://m.stock.naver.com/item/main.nhn#/stocks/" + mCode + "/total";
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mItem.getName());
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
                intent.putExtra("title", mItem.getName());
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

        final String year3ChartUrl = "https://chart-finance.daumcdn.net/time3/3year/" + mCode + "-290157.png;"; // 다음
        ImageView ivYear3Chart = findViewById(R.id.ivYear3Chart);
        Glide.with(mContext).load(year3ChartUrl).apply(new RequestOptions().placeholder(R.drawable.placeholder)).into(ivYear3Chart);

        loadNews();
    }

    private void loadNews() {
        String url = Config.URL_NEWS_LIST + mCode;
        //Log.e(mTag, "url: " + url);

        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(mTag, "response:\n" + response);
                parseNews(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseNews("");
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
    }

    private void parseNews(String response) {
        mNewsList.clear();

        DaumNewsParser daumNewsParser = new DaumNewsParser();
        daumNewsParser.parseList(mContext, mItem.getName(), response, mNewsList);

        renderNews();
    }

    private void renderNews() {
        // 네이버 뉴스
        TextView tvNaverSearch = findViewById(R.id.tvNaverNews);
        tvNaverSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://m.search.naver.com/search.naver?where=m_news&query=" + mItem.getName();
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mItem.getName());
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        // 다음 뉴스
        TextView tvDaumSearch = findViewById(R.id.tvDaumNews);
        tvDaumSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://m.search.daum.net/search?w=news&q=" + mItem.getName();
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mItem.getName());
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        // 구글 뉴스
        TextView tvGoogleSearch = findViewById(R.id.tvGoogleNews);
        tvGoogleSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.google.com/search?tbm=nws&q=" + mItem.getName();
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", mItem.getName());
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        // 다음 금융 - 뉴스 목록
        DetailNewsAdapter adapter = new DetailNewsAdapter(mContext, mNewsList);

        ExpandableHeightListView listView = findViewById(R.id.newsListView);
        listView.setExpanded(true);
        listView.setFocusable(false);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News) parent.getItemAtPosition(position);

                String title = mItem.getName();
                String url = news.getUrl();

                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

        stopRefreshAnimation();
    }

    protected void onToolbarClick() {
        mScrollView.scrollTo(0, 0);
    }

    private void onRefreshClick() {
        startRefreshAnimation();
        loadData();
    }

    /*
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
    */

    private void updateMyItem(final String category) {
        //startRefreshAnimation();

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
                stopRefreshAnimation();
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
