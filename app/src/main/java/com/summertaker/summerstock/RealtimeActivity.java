package com.summertaker.summerstock;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.summertaker.summerstock.common.BaseActivity;
import com.summertaker.summerstock.common.BaseApplication;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.Item;
import com.summertaker.summerstock.parser.DaumParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RealtimeActivity extends BaseActivity {

    private ProgressBar mPbLoading;

    private ArrayList<Item> mItemList;
    private RealtimeAdapter mAdapter;
    private ListView mListView;

    private boolean mIsFirstLoading = true;
    private boolean mIsLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realtime_activity);

        mContext = RealtimeActivity.this;

        setBaseStatusBar();
        initToolbar(null);

        mPbLoading = findViewById(R.id.pbLoading);

        mItemList = new ArrayList<>();
        mAdapter = new RealtimeAdapter(mContext, mItemList);

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

        //loadData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.realtime, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            //case R.id.action_search:
            //    Intent search = new Intent(this, SearchActivity.class);
            //    startActivity(search);
            //    return true;
            //case R.id.action_refresh:
            //    onRefreshClick();
            //    return true;
            case R.id.action_finish:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        if (!mIsLoading) {
            mIsLoading = true;

            DateFormat df = new SimpleDateFormat("a K시 mm분 ss초", Locale.getDefault());
            String title = df.format(Calendar.getInstance().getTime());
            mToolbar.setTitle(title);

            requestData();
        }
    }

    private void requestData() {
        String url = Config.URL_KOSPI_LIST;
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
        mItemList.clear();

        DaumParser daumParser = new DaumParser();
        daumParser.parseAllItemPrice(response, mItemList);

        renderData();
    }

    private void renderData() {
        //Log.d(mTag, "mMemberList.size(): " + mMemberList.size());

        if (mIsFirstLoading) {
            mPbLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mIsFirstLoading = false;
        }

        mAdapter.notifyDataSetChanged();
        mIsLoading = false;

        loadData();
    }

    public void goTop() {
        mListView.setSelection(0);
    }

    protected void onToolbarClick() {
        goTop();
    }

    @Override
    protected void onSwipeRight() {
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
