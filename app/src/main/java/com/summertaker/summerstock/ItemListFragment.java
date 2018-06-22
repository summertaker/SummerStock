package com.summertaker.summerstock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.summertaker.summerstock.common.BaseFragment;
import com.summertaker.summerstock.common.Config;
import com.summertaker.summerstock.data.Item;
import com.summertaker.summerstock.parser.NaverParser;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItemListFragment extends BaseFragment {

    private ItemListFragment.ItemListListener mListener;

    //private int mPosition = -1;
    private String mFragmentId = "";
    private String mUrl = "";

    private boolean mIsFirstLoading = true;
    private ProgressBar mPbLoading;

    private boolean mIsLoading = false;
    private ArrayList<Item> mDataList;
    //private ItemListAdapter mAdapter;
    private ListView mListView;

    // Container Activity must implement this interface
    public interface ItemListListener {
        public void onItemListEvent(String event);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            // This makes sure that the container activity has implemented
            // the callback interface. If not, it throws an exception
            try {
                mListener = (ItemListFragment.ItemListListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
            }
        }
    }

    public ItemListFragment() {
    }

    public static ItemListFragment newInstance(int position, String fragmentId, String url) {
        ItemListFragment fragment = new ItemListFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("fragmentId", fragmentId);
        args.putString("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.item_list_fragment, container, false);

        //mLoLoading = rootView.findViewById(R.id.loLoading);
        //mPbLoading = rootView.findViewById(R.id.pbLoading);
        //mLoLoadMore = rootView.findViewById(R.id.loLoadMore);

        mContext = getContext(); //.getApplicationContext();

        //mPosition = getArguments().getInt("position", -1);
        mFragmentId = getArguments().getString("fragmentId");
        //Log.e(mTag, "mFragmentId: " + mFragmentId);
        mUrl = getArguments().getString("url");

        mPbLoading = rootView.findViewById(R.id.pbLoading);

        mDataList = new ArrayList<>();
        //mAdapter = new ItemListAdapter(mContext, mFragmentId, mDataList);

        mListView = rootView.findViewById(R.id.listView);
        //mListView.setAdapter(mAdapter);

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

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                Item data = (Item) adapterView.getItemAtPosition(position);
                updateFavorite(position, data.getCode()); // 즐겨찾기 설정

                return true;
            }
        });

        if (!mUrl.isEmpty()) {
            loadData();
        }

        return rootView;
    }

    private void loadData() {
        if (!mIsLoading) {
            mIsLoading = true;
            mListener.onItemListEvent("onLoadStarted");

            requestData();
        }
    }

    private void requestData() {
        //String url = mUrl;
        //Log.e(mTag, "url: " + url);

        if (mUrl.contains("GetYieldList")) { // 네이버 > 추천종목 > 추천종목별 수익률
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
            callData(jsonObject);
        } else if (mUrl.contains("GetTopCompanyList")) { // 네이버 > 추천종목 > 종목별추천건수 상위
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
            callData(jsonObject);
        } else if (mUrl.contains("RecommendDetail")) { // 네이버 > 추천종목 > 현재 추천종목
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
            callData(jsonObject);
        } else {
            Request request = new Request.Builder().url(mUrl).get().build();
            OkHttpSingleton.getInstance().getClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseString = response.body().string();
                    //Log.e(">>>>>", "responseString\n" + responseString);
                    getActivity().runOnUiThread(new Runnable() {
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
    }

    private void callData(JSONObject jsonObject) {
        final String postBody = jsonObject.toString();
        //Log.e(mTag, "postBody: " + postBody);

        RequestBody requestBody = RequestBody.create(Config.JSON, postBody);
        Request request = new Request.Builder().url(mUrl).post(requestBody).build();
        OkHttpSingleton.getInstance().getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                //Log.e(">>>>>", "responseString\n" + responseString);
                getActivity().runOnUiThread(new Runnable() {
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
        mDataList.clear();

        if (mUrl.contains("sise_rise.nhn")) {
            NaverParser naverParser = new NaverParser();
            naverParser.parseRise(response, mDataList);
        } else {
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

                DateFormat inDf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                DateFormat outDf = new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault());

                Calendar cal = Calendar.getInstance();
                Date d = cal.getTime();
                long curTime = d.getTime();

                int num = 1;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    //Log.e(mTag, "object: " + object.toString());

                    int no = Util.getInt(object, "NUM");
                    String code = Util.getString(object, "CMP_CD");
                    String name = Util.getString(object, "CMP_NM_KOR");
                    int price = Util.getInt(object, "prc");
                    int psp = Util.getInt(object, "TO_ADJ_PRICE"); // 추천수 상위 > 예측가
                    if (psp == 0) {
                        psp = Util.getInt(object, "PRE_ADJ_CLOSE_PRC"); // 현재 추천 > 예상가
                    }

                    String indt = Util.getString(object, "IN_DT"); // 현재 추천 > 추천일
                    int days = 0;
                    if (indt.isEmpty()) {
                        indt = Util.getString(object, "LAST_IN_DT"); // 추천수 > 최근 추천일
                    }
                    try {
                        Date date = inDf.parse(indt);
                        indt = outDf.format(date);
                        long oldTime = date.getTime();
                        long diffTime = curTime - oldTime;
                        days = (int) ((((diffTime / 1000) / 60) / 60) / 24); // 경과일
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    int adp = Util.getInt(object, "adp");
                    float adr = BigDecimal.valueOf(Util.getDouble(object, "adr")).floatValue();
                    float per = BigDecimal.valueOf(Util.getDouble(object, "per")).floatValue();
                    float roe = BigDecimal.valueOf(Util.getDouble(object, "roe")).floatValue();
                    int ndr = Util.getInt(object, "CNT"); // 경과일
                    float ror = BigDecimal.valueOf(Util.getDouble(object, "ACCU_RTN")).floatValue();
                    int nor = Util.getInt(object, "RECOMAND_CNT");

                    //String favorite = Util.getString(object, "favorite");
                    //String possession = Util.getString(object, "possession");

                    if (ndr == 0) { // 추천 경과일
                        if (days > 0) {
                            ndr = days;
                        }
                    }

                    if (mFragmentId.equals(Config.KEY_RETURN)) { // 추천주 수익률
                        if (ror < Config.ROR_MIN) {
                            continue;
                        }
                    } else if (mFragmentId.equals(Config.KEY_TOP)) { // 추천수 상위
                        if (psp < Config.PRICE_MIN || psp > Config.PRICE_MAX) {
                            continue;
                        }
                    } else if (mFragmentId.equals(Config.KEY_CURRENT)) { // 현재 추천
                        if (psp < Config.PRICE_MIN || psp > Config.PRICE_MAX) {
                            continue;
                        }
                        if (ndr > 30) { // 추천일 오래 된 주식은 제외: 30일
                            break;
                        }
                    }

                    //Log.e(mTag, no + ". " + name + " " + ror);

                    Item item = new Item();
                    item.setNo(num);
                    item.setCode(code);
                    item.setName(name);
                    item.setPrice(price);
                    item.setPsp(psp);
                    item.setPdt(indt);
                    item.setAdp(adp);
                    item.setAdr(adr);
                    item.setPer(per);
                    item.setRoe(roe);
                    item.setNdp(ndr);
                    item.setRor(ror);
                    item.setNor(nor);

                    //data.setFavorite(favorite);
                    //data.setPossession(possession);

                    mDataList.add(item);
                    num++;
                }
            } catch (JSONException e) {
                Log.e(mTag, e.getMessage());
            }
        }

        renderData();
    }

    private void renderData() {
        //Log.d(mTag, "mMemberList.size(): " + mMemberList.size());

        if (mIsFirstLoading) {
            mPbLoading.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
            mIsFirstLoading = false;
        }

        if (mFragmentId.equals(Config.KEY_RISING)) {
            RiseAdapter adapter = new RiseAdapter(mContext, mDataList);
            mListView.setAdapter(adapter);
        } else if (mFragmentId.equals(Config.KEY_RETURN)) {
            ReturnAdapter adapter = new ReturnAdapter(mContext, mDataList);
            mListView.setAdapter(adapter);
        } else if (mFragmentId.equals(Config.KEY_TOP)) {
            TopAdapter adapter = new TopAdapter(mContext, mDataList);
            mListView.setAdapter(adapter);
        } else if (mFragmentId.equals(Config.KEY_CURRENT)) {
            CurrentAdapter adapter = new CurrentAdapter(mContext, mDataList);
            mListView.setAdapter(adapter);
        }

        //mAdapter.notifyDataSetChanged();

        mIsLoading = false;
        mListener.onItemListEvent("onLoadFinished");
    }

    public void goTop() {
        mListView.setSelection(0);
    }

    public void refreshFragment() {
        if (mIsLoading) {
            mListener.onItemListEvent("onLoadFinished");
        } else {
            //goTop();
            mPbLoading.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            mIsFirstLoading = true;

            loadData();
        }
    }

    private void updateFavorite(final int position, String itemCd) {
        //mListener.onItemListEvent("onLoadStarted");

        //String category = Config.KEY_FAVORITE;
        //if (mFragmentId.equals(Config.KEY_POSSESSION)) {
        //    category = Config.KEY_POSSESSION;
        //}
        //String url = Config.URL_MY_UPDATE + "?item_cd=" + itemCd + "&category=" + category;
        //Log.e(mTag, ">>>" + url);

        /*
        StringRequest strReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ///Log.e(mTag, "response:\n" + response);

                //if (mFragmentId.equals(Config.KEY_POSSESSION) || mFragmentId.equals(Config.KEY_FAVORITE)) {
                //    mDataList.remove(position);
                //} else {
                mDataList.get(position).setFavorite(response);
                //}
                mAdapter.notifyDataSetChanged();

                mListener.onItemListEvent("onFavoriteUpdated");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(mVolleyTag, "VOLLEY ERROR: " + error.getMessage());
            }
        });

        BaseApplication.getInstance().addToRequestQueue(strReq, mVolleyTag);
        */
    }
}
