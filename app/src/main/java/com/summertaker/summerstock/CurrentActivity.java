package com.summertaker.summerstock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.summertaker.summerstock.common.BaseActivity;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.Item;
import com.summertaker.summerstock.util.OkHttpSingleton;
import com.summertaker.summerstock.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CurrentActivity extends BaseActivity {

    private ProgressBar mPbLoading;

    private ArrayList<Item> mItemList;
    private CurrentAdapter mAdapter;
    private ListView mListView;

    private boolean mIsFirstLoading = true;
    private boolean mIsLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rise_activity);

        mContext = CurrentActivity.this;

        setBaseStatusBar();
        initToolbar(null);

        mPbLoading = findViewById(R.id.pbLoading);

        mItemList = new ArrayList<>();
        mAdapter = new CurrentAdapter(mContext, mItemList);

        mListView = findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Item data = (Item) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("code", data.getCode());
                intent.putExtra("name", data.getName());
                intent.putExtra("price", data.getPrice());
                intent.putExtra("per", data.getPer());
                intent.putExtra("roe", data.getRoe());
                startActivity(intent);
            }
        });

        loadData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Intent search = new Intent(this, SearchActivity.class);
                startActivity(search);
                return true;
            case R.id.action_refresh:
                onRefreshClick();
                return true;
            case R.id.action_finish:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        if (!mIsLoading) {
            mIsLoading = true;

            requestData();
        }
    }

    private void requestData() {
        //String url = mUrl;
        //Log.e(mTag, "url: " + url);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("brkCD", "0");
            jsonObject.put("cmpC", "");
            jsonObject.put("curPage", "1");
            jsonObject.put("orderCol", "4");
            jsonObject.put("orderType", "D");
            jsonObject.put("perPage", "100");
            jsonObject.put("pfCD", "0");
            jsonObject.put("stdDt", Util.getToday("yyyy-MM-dd"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String postBody = jsonObject.toString();
        //Log.e(mTag, "postBody: " + postBody);

        RequestBody requestBody = RequestBody.create(Config.JSON, postBody);
        Request request = new Request.Builder().url(Config.URL_CURRENT_LIST).post(requestBody).build();
        OkHttpSingleton.getInstance().getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                //Log.e(">>>>>", "responseString\n" + responseString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseData(responseString);
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

    private void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            //Log.e(mTag, "jsonArray.length() = " + jsonArray.length());

            // 네이버 > 추천종목 > 현재 추천종목
            /*
            NUM	1
            TOTROW	105
            CMP_NM_KOR	대웅제약
            CMP_CD	069620
            BRK_NM_KOR	유안타증권
            BRK_CD	12
            PF_NM_KOR	대형주
            PF_CD	111
            IN_DT	2018/06/21
            TERM_CNT	0
            IN_DT_PRICE	189000
            RECOMM_PRICE	null
            PRE_ADJ_CLOSE_PRC	189000
            REASON_IN	▶ 골관절염치료제(아셀렉스), 유방암치료제(샴페넷) 등 다양한 제품 라인업으로 사업 확장성 기대 ▶ 가장 우려되었던 부분인 나보타공장 cGMP 에 대한 이슈 해결 ▶ 올해 하반기 또는 내년 상반기 나보타의 유럽 EMA 및 미국 FDA 승인 기대
            ACCU_RTN	null
            PRE_DT	2018/06/20
            FILE_NM	01211120180621_069620.pdf
            ANL_DT	2018/06/21
            IN_DIFF_REASON	null
             */
            DateFormat dateInFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            DateFormat dateOutFormat = new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault());

            int no = 1;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                //Log.e(mTag, "object: " + object.toString());

                //int num = Util.getInt(object, "NUM");
                String code = Util.getString(object, "CMP_CD");
                String name = Util.getString(object, "CMP_NM_KOR");
                String pdt = Util.getString(object, "PRE_DT"); // 예측일
                int psp = Util.getInt(object, "PRE_ADJ_CLOSE_PRC"); // 예측가
                int ndp = Util.getInt(object, "TERM_CNT"); // 경과일

                if (psp < Config.PRICE_MIN || psp > Config.PRICE_MAX) {
                    continue;
                }
                if (ndp > 30) { // 30일
                    continue;
                }

                try {
                    Date date = dateInFormat.parse(pdt);
                    pdt = dateOutFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //Log.e(mTag, no + ". " + name + " " + ror);

                Item item = new Item();
                item.setNo(no);
                item.setCode(code);
                item.setName(name);
                item.setPsp(psp);
                item.setPdt(pdt);
                item.setNdp(ndp);

                //data.setFavorite(favorite);
                //data.setPossession(possession);

                mItemList.add(item);
                no++;
            }
        } catch (JSONException e) {
            Log.e(mTag, e.getMessage());
        }

        renderData();
    }

    private void renderData() {
        //Log.d(mTag, "mMemberList.size(): " + mMemberList.size());

        if (mIsFirstLoading) {
            mPbLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mIsFirstLoading = false;
        } else {
            stopRefreshAnimation();
        }

        mAdapter.notifyDataSetChanged();

        mIsLoading = false;
    }

    public void goTop() {
        mListView.setSelection(0);
    }

    protected void onToolbarClick() {
        goTop();
    }

    private void onRefreshClick() {
        startRefreshAnimation();

        mItemList.clear();
        loadData();
    }

    @Override
    protected void onSwipeRight() {
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
