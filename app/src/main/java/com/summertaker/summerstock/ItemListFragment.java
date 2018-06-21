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
import com.summertaker.summerstock.data.ItemData;
import com.summertaker.summerstock.parser.NaverParser;
import com.summertaker.summerstock.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ItemListFragment extends BaseFragment {

    private ItemListFragment.ItemListListener mListener;

    private int mPosition = -1;
    private String mFragmentId = "";
    private String mUrl = "";

    private boolean mIsFirstLoading = true;
    private ProgressBar mPbLoading;

    private boolean mIsLoading = false;
    private ArrayList<ItemData> mDataList;
    private ItemListAdapter mAdapter;
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

        mPosition = getArguments().getInt("position", -1);
        mFragmentId = getArguments().getString("fragmentId");
        mUrl = getArguments().getString("url");

        mPbLoading = rootView.findViewById(R.id.pbLoading);

        mDataList = new ArrayList<>();
        mAdapter = new ItemListAdapter(mContext, mFragmentId, mDataList);

        mListView = rootView.findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ItemData data = (ItemData) adapterView.getItemAtPosition(position);

                Intent intent = new Intent(mContext, ItemDetailActivity.class);
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
                ItemData data = (ItemData) adapterView.getItemAtPosition(position);
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

        if (mUrl.contains("GetTopCompanyList")) { // 네이버 > 추천종목 > 종목별추천건수 상위
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
            mOkHttpClient.newCall(request).enqueue(new Callback() {
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
        mOkHttpClient.newCall(request).enqueue(new Callback() {
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
        if (mUrl.contains("sise_rise.nhn")) {
            NaverParser naverParser = new NaverParser();
            naverParser.parseRise(response, mDataList);
        } else {
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
                int no = 1;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    //Log.e(mTag, "object: " + object.toString());

                    //int no = Util.getInt(object, "NUM");
                    String code = Util.getString(object, "CMP_CD");
                    String name = Util.getString(object, "CMP_NM_KOR");
                    int price = Util.getInt(object, "prc");
                    int predict = Util.getInt(object, "TO_ADJ_PRICE"); // 추천수 상위 > 예상가
                    if (predict == 0) {
                        predict = Util.getInt(object, "PRE_ADJ_CLOSE_PRC"); // 현재 추천 > 예상가
                    }
                    int adp = Util.getInt(object, "adp");
                    float adr = BigDecimal.valueOf(Util.getDouble(object, "adr")).floatValue();
                    float per = BigDecimal.valueOf(Util.getDouble(object, "per")).floatValue();
                    float roe = BigDecimal.valueOf(Util.getDouble(object, "roe")).floatValue();
                    int recommend = Util.getInt(object, "RECOMAND_CNT");
                    //String inReason = Util.getString(object, "in_reason");
                    //String favorite = Util.getString(object, "favorite");
                    //String possession = Util.getString(object, "possession");

                    if (price > 0) {
                        if (price < Config.PRICE_MIN || price > Config.PRICE_MAX) {
                            continue;
                        }
                    } else if (predict > 0) {
                        if (predict < Config.PRICE_MIN || predict > Config.PRICE_MAX) {
                            continue;
                        }
                    }

                    //Log.e(mTag, mFragmentId + ": " + name);

                    ItemData data = new ItemData();
                    data.setNo(no);
                    data.setCode(code);
                    data.setName(name);
                    data.setPrice(price);
                    data.setPredict(predict);
                    data.setAdp(adp);
                    data.setAdr(adr);
                    data.setPer(per);
                    data.setRoe(roe);
                    data.setRecommend(recommend);

                    //data.setFavorite(favorite);
                    //data.setPossession(possession);

                    mDataList.add(data);
                    no++;
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

        mAdapter.notifyDataSetChanged();

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
            mDataList.clear();

            mPbLoading.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            mIsFirstLoading = true;

            loadData();
        }
    }

    private void updateFavorite(final int position, String itemCd) {
        mListener.onItemListEvent("onLoadStarted");

        String category = Config.KEY_FAVORITE;
        //if (mFragmentId.equals(Config.KEY_POSSESSION)) {
        //    category = Config.KEY_POSSESSION;
        //}
        String url = Config.URL_MY_UPDATE + "?item_cd=" + itemCd + "&category=" + category;
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
