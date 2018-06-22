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
import com.summertaker.summerstock.parser.NaverParser;
import com.summertaker.summerstock.util.OkHttpSingleton;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class RiseActivity extends BaseActivity {

    private ProgressBar mPbLoading;
    private ArrayList<Item> mItemList;
    private RiseAdapter mAdapter;
    private ListView mListView;

    private boolean mIsFirstLoading = true;
    private boolean mIsLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rise_activity);

        mContext = RiseActivity.this;

        setBaseStatusBar();
        initToolbar(null);

        mPbLoading = findViewById(R.id.pbLoading);

        mItemList = new ArrayList<>();
        mAdapter = new RiseAdapter(mContext, mItemList);

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
        Request request = new Request.Builder().url(Config.URL_RISE_LIST).get().build();
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
        NaverParser naverParser = new NaverParser();
        naverParser.parseRise(response, mItemList);

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
