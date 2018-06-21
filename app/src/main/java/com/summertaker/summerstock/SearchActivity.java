package com.summertaker.summerstock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.summertaker.summerstock.common.BaseActivity;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.Item;
import com.summertaker.summerstock.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity {

    private ArrayList<Item> mItemList;

    ProgressBar mPbLoading;
    LinearLayout mLoMain;
    AutoCompleteTextView mTvAucoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        mContext = SearchActivity.this;

        setBaseStatusBar();
        initToolbar(null);

        mItemList = new ArrayList<>();

        mPbLoading = findViewById(R.id.pbLoading);
        mLoMain = findViewById(R.id.loMain);
        mTvAucoComplete = findViewById(R.id.tvAutoComplete);

        requestData();
    }

    private void requestData() {
        String url = Config.URL_ITEM_AUTOCOMPLETE;
        //Log.e(mTag, "url: " + url);

        /*
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

    private void parseData(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            //Log.e(mTag, "jsonArray.length() = " + jsonArray.length());

            //DecimalFormat formatter = new DecimalFormat("###,###,###");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                //Log.e(mTag, "object: " + object.toString());

                String itemCd = Util.getString(object, "item_cd");
                String itemNm = Util.getString(object, "item_nm");
                //int prc = Integer.valueOf(Util.getString(object, "prc"));
                //float adr = BigDecimal.valueOf(Util.getDouble(object, "adr")).floatValue();

                Item data = new Item();
                data.setCode(itemCd);
                data.setName(itemNm);
                //data.setPrc(prc);
                //data.setAdr(adr);

                mItemList.add(data);
            }
        } catch (JSONException e) {
            Log.e(mTag, e.getMessage());
        }

        render();
    }

    private void render() {
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
                String itemNm = parent.getItemAtPosition(position).toString();
                String itemCd = "";

                for (Item item : mItemList) {
                    if (itemNm.equals(item.getName())) {
                        itemCd = item.getCode();
                        break;
                    }
                }

                Intent intent = new Intent(mContext, ItemDetailActivity.class);
                intent.putExtra("itemCd", itemCd);
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
