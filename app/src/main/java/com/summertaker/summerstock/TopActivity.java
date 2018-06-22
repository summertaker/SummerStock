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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TopActivity extends BaseActivity {

    private ProgressBar mPbLoading;

    private ArrayList<Item> mItemList;
    private TopAdapter mAdapter;
    private ListView mListView;

    private boolean mIsFirstLoading = true;
    private boolean mIsLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_activity);

        mContext = TopActivity.this;

        setBaseStatusBar();
        initToolbar(null);

        mPbLoading = findViewById(R.id.pbLoading);

        mItemList = new ArrayList<>();
        mAdapter = new TopAdapter(mContext, mItemList);

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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("brkcd", "0");
            jsonObject.put("cmpcd", "");
            jsonObject.put("curPage", "1");
            jsonObject.put("enddt", Util.getToday("yyyyMMdd"));
            jsonObject.put("orderCol", "1");
            jsonObject.put("orderType", "D");
            jsonObject.put("perPage", "100");
            jsonObject.put("pfcd", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String postBody = jsonObject.toString();
        //Log.e(mTag, "postBody: " + postBody);

        RequestBody requestBody = RequestBody.create(Config.JSON, postBody);
        Request request = new Request.Builder().url(Config.URL_TOP_LIST).post(requestBody).build();
        OkHttpSingleton.getInstance().getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                //Log.e(">>>>>", "responseString\n" + responseString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        parseData(res);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.e(mTag, "ERROR: " + e.getMessage());
            }
        });
    }

    private void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            //Log.e(mTag, "jsonArray.length() = " + jsonArray.length());

            // 네이버 > 추천종목 > 종목별추천건수 상위
            /*
            {"NUM":1,
            "TOTROW":92,
            "RECOMAND_CNT":2,
            "CMP_NM_KOR":"녹십자랩셀",
            "CMP_CD":"144510",
            "FIRST_IN_DT":"2018/06/18",
            "LAST_IN_DT":"2018/06/18",
            "TO_ADJ_PRICE":47550,
            "AVG_DT":2,
            "W_RTN":-12.10730000000000,
            "MN_RTN":-10.28310000000000,
            "MN3_RTN":-23.18260000000000,
            "JAN1_RTN":-4.90000000000000
            }
            */

            DateFormat dateInFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            DateFormat dateOutFormat = new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault());

            Calendar cal = Calendar.getInstance();
            Date d = cal.getTime();
            long curTime = d.getTime();

            int no = 1;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                //Log.e(mTag, "object: " + object.toString());

                //int num = Util.getInt(object, "NUM");
                int nor = Util.getInt(object, "RECOMAND_CNT"); // 추천수
                String name = Util.getString(object, "CMP_NM_KOR");
                String code = Util.getString(object, "CMP_CD");
                String pdt = Util.getString(object, "LAST_IN_DT"); // 최근 추천일
                int psp = Util.getInt(object, "TO_ADJ_PRICE"); // 예측가

                int ndp = 0;
                try {
                    Date date = dateInFormat.parse(pdt);
                    pdt = dateOutFormat.format(date);
                    long oldTime = date.getTime();
                    long diffTime = curTime - oldTime;
                    ndp = (int) ((((diffTime / 1000) / 60) / 60) / 24); // 경과일
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (psp < Config.PRICE_MIN || psp > Config.PRICE_MAX) {
                    continue;
                }

                //Log.e(mTag, no + ". " + name + " " + ror);

                Item item = new Item();
                item.setNo(no);
                item.setName(name);
                item.setCode(code);
                item.setNor(nor);
                item.setPdt(pdt);
                item.setPsp(psp);
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
