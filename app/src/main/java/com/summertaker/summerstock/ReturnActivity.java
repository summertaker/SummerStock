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
import java.math.BigDecimal;
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

public class ReturnActivity extends BaseActivity {

    private ProgressBar mPbLoading;

    private ArrayList<Item> mItemList;
    private ReturnAdapter mAdapter;
    private ListView mListView;

    private boolean mIsFirstLoading = true;
    private boolean mIsLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_activity);

        mContext = ReturnActivity.this;

        setBaseStatusBar();
        initToolbar(null);

        mPbLoading = findViewById(R.id.pbLoading);

        mItemList = new ArrayList<>();
        mAdapter = new ReturnAdapter(mContext, mItemList);

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

        /*
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Item data = (Item) adapterView.getItemAtPosition(position);
                updateFavorite(position, data.getCode()); // 즐겨찾기 설정

                return true;
            }
        });
        */

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
            jsonObject.put("orderCol", "6");
            jsonObject.put("orderType", "D");
            jsonObject.put("perPage", "100");
            jsonObject.put("pfcd", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String postBody = jsonObject.toString();
        //Log.e(mTag, "postBody: " + postBody);

        RequestBody requestBody = RequestBody.create(Config.JSON, postBody);
        Request request = new Request.Builder().url(Config.URL_RETURN_LIST).post(requestBody).build();
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

            // 네이버 > 추천종목 > 추천종목별 수익률
            /*
            NUM	1
            TOTROW	102
            CMP_NM_KOR	DB하이텍
            CMP_CD	000990
            IN_DT	2018/05/16
            CNT	35
            BRK_NM_KOR	유안타증권
            BRK_CD	12
            PF_NM_KOR	대형주
            PF_CD	111
            ACCU_RTN	26.88524590164
            W_RTN	2.9255
            MN_RTN	28.1456
            MN3_RTN	null
            JAN1_RTN	null
            REASON_IN	▶ 삼성전자 TV 신제품 효과로 제품 Mix 개선 본격화 ▶ 2분기 중후반부터 가파른 가동률/실적 상승 전망
            ANL_DT	2018/05/15
            IN_DIFF_REASON	장 종료후 추천으로 추천일자 순연
            */

            DateFormat dateInFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            DateFormat dateOutFormat = new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault());

            int no = 1;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                //Log.e(mTag, "object: " + object.toString());

                //int num = Util.getInt(object, "NUM");
                String name = Util.getString(object, "CMP_NM_KOR"); // 종목 이름
                String code = Util.getString(object, "CMP_CD"); // 종목 코드
                String pdt = Util.getString(object, "IN_DT"); // 예측일
                int ndp = Util.getInt(object, "CNT"); // 경과일
                float ror = BigDecimal.valueOf(Util.getDouble(object, "ACCU_RTN")).floatValue(); // 추천일 후 수익률
                String reason = Util.getString(object, "REASON_IN"); // 추천 사유

                if (ror < Config.ROR_MIN) {
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
                item.setPdt(pdt);
                item.setNdp(ndp);
                item.setRor(ror);
                item.setReason(reason);

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
