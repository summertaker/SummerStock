package com.summertaker.summerstock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;

public class SearchActivity extends BaseActivity {

    private ArrayList<Item> mItemList;

    ProgressBar mPbLoading;
    LinearLayout mLoMain;
    AutoCompleteTextView mTvAucoComplete;

    private ArrayList<String> mUrls;
    int mUrlLoadCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        mContext = SearchActivity.this;

        setBaseStatusBar();
        initToolbar(null);

        mUrls = new ArrayList<>();
        mUrls.add(Config.URL_KOSPI_LIST);
        mUrls.add(Config.URL_KOSDAQ_LIST);

        mItemList = new ArrayList<>();

        mPbLoading = findViewById(R.id.pbLoading);
        mLoMain = findViewById(R.id.loMain);
        mTvAucoComplete = findViewById(R.id.tvAutoComplete);

        requestData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_finish:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestData() {
        String url = mUrls.get(mUrlLoadCount);
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
        //mItemList.clear();

        DaumParser daumParser = new DaumParser();
        daumParser.parseAllItemPrice(response, mItemList);

        mUrlLoadCount++;
        if (mUrlLoadCount < mUrls.size()) {
            requestData();
        } else {
            renderData();
        }
    }

    private void renderData() {
        mPbLoading.setVisibility(View.GONE);
        mLoMain.setVisibility(View.VISIBLE);
        //mTvAucoComplete.setVisibility(View.VISIBLE);

        String[] searchData = new String[mItemList.size()];
        for (int i = 0; i < mItemList.size(); i++) {
            searchData[i] = mItemList.get(i).getName();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_dropdown, searchData);

        mTvAucoComplete.setThreshold(1); //will start working from first character
        mTvAucoComplete.setAdapter(adapter);
        mTvAucoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = parent.getItemAtPosition(position).toString();
                String code = "";

                for (Item item : mItemList) {
                    if (name.equals(item.getName())) {
                        code = item.getCode();
                        break;
                    }
                }

                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("code", code);
                startActivity(intent);
            }
        });
        mTvAucoComplete.requestFocus();

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr != null) {
            mgr.showSoftInput(mTvAucoComplete, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    protected void onSwipeRight() {
        finish();
    }

    @Override
    protected void onSwipeLeft() {

    }
}
